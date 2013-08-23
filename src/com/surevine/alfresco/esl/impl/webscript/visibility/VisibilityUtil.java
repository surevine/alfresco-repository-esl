/*
 * Copyright (C) 2008-2010 Surevine Limited.
 * 
 * Although intended for deployment and use alongside Alfresco this module should
 * be considered 'Not a Contribution' as defined in Alfresco'sstandard contribution agreement, see
 * http://www.alfresco.org/resource/AlfrescoContributionAgreementv2.pdf
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
*/
package com.surevine.alfresco.esl.impl.webscript.visibility;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.alfresco.module.org_alfresco_module_dod5015.caveat.RMCaveatConfigService;
import org.alfresco.module.org_alfresco_module_dod5015.caveat.RMConstraintInfo;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.security.PersonService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.webscripts.WebScriptRequest;

import com.surevine.alfresco.esl.exception.EnhancedSecurityException;
import com.surevine.alfresco.esl.impl.EnhancedSecurityConstraint;
import com.surevine.alfresco.esl.impl.EnhancedSecurityConstraintLocator;
import com.surevine.alfresco.esl.impl.GroupDetails;
import com.surevine.alfresco.presence.Presence;
import com.surevine.alfresco.presence.PresenceService;

/**
 * @author richardm
 */
public class VisibilityUtil {
	
	private static final Log _logger = LogFactory.getLog(VisibilityUtil.class);

	private PersonService _personService;
	public void setPersonService(PersonService personService) {
		_personService=personService;
	}
	
	private NodeService _nodeService;
	public void setNodeService(NodeService nodeService) {
		_nodeService=nodeService;
	}
	
	private PresenceService _presenceService;
	public void setPresenceService(PresenceService presenceService) {
		_presenceService=presenceService;
	}
		
	private EnhancedSecurityConstraintLocator _locator;
	public void setLocator(EnhancedSecurityConstraintLocator locator) {
		_locator=locator;
	}
	
	public Map<String, String[]> getMarkingFromRequest(final WebScriptRequest request) {
		final Map<String, String[]> markings = new HashMap<String, String[]>();
		final String marking = request.getParameter("marking");
		final String[] eachMarking = marking.split(";");
		
		for (int i=0; i<eachMarking.length; i++) {
			final String[] markingComponents = eachMarking[i].split(",");
			final String[] values = new String[markingComponents.length-1];
			
			System.arraycopy(markingComponents, 1, values, 0, values.length);
			
			markings.put(markingComponents[0].replaceAll("_", ":"), values);
		}
		return markings;
	}
	
	public List<String> whoCanSeeMarking(final RMCaveatConfigService caveatConfig, final Map<String, String[]> markings) {
		// For each marking, build an associative array based on who could see the given marking if the operating constraint
		// was the only constraint in the marking, and put each of these into an array
		final List<Map<String, Boolean>> individualMarkingResults = new ArrayList<Map<String, Boolean>>();
		
		for (final Entry<String, String[]> constraintSpecEntry : markings.entrySet()) {
			individualMarkingResults.add(whoCanSeeMarkingForConstraint(caveatConfig, constraintSpecEntry.getKey(), constraintSpecEntry.getValue()));
		}
		
		// We now merge these individual return values into an aggregate associative array.  If a single constraint
		// denies access for a user (username->false) then access is denied in the aggregate.
		final List<String> totalMarkingResults = new ArrayList<String>();
		
		// associative array mapping users to the number of times they are mentioned in a constraint
		final Map<String, Integer> authorityCount = new HashMap<String, Integer>();
		
		//For each constraint...
		for (final Map<String, Boolean> individualMarking : individualMarkingResults) {
			//...iterate through all the authorities and count how many constraints they have passed
			for (final Entry<String, Boolean> authorityEntry : individualMarking.entrySet()) {
			    //If the current user passes the current constraint
			    if (authorityEntry.getValue()) {
    				//Count the number of times we have seen this user (i.e. the number of constraints they have passed)
    				if (!authorityCount.containsKey(authorityEntry.getKey())) {
    					authorityCount.put(authorityEntry.getKey(), 1);
    				} else {
    					authorityCount.put(authorityEntry.getKey(), authorityCount.get(authorityEntry.getKey()) +1);
    				}
			    }
			}
		}
		
		//Generate and return a list of users who passed all the constraints
		for (final String authorityName : authorityCount.keySet()) {
			if (authorityCount.get(authorityName) == markings.keySet().size()) {
			    totalMarkingResults.add(authorityName);
			}
		}
		
		return totalMarkingResults;
	}
	
	private Map<String, Boolean> whoCanSeeMarkingForConstraint(final RMCaveatConfigService caveatConfig, final String constraintSpecName, final String[] constraintSpecValues) {
		final Map<String, Boolean> retVal = new HashMap<String, Boolean>();
		
		final RMConstraintInfo constraint = caveatConfig.getRMConstraint(constraintSpecName);
		final Map<String, List<String>> authorities = caveatConfig.getListDetails(constraint.getName());
		final boolean andLogic = constraintSpecName.equals("es:validClosedMarkings");
		
		//For each authority...
		for (final String authorityName : authorities.keySet()) {
			boolean hasAccess = processLogic(caveatConfig, constraintSpecName, constraintSpecValues, authorityName,andLogic);
			
			retVal.put(authorityName, hasAccess);
		}
		return retVal;
	}
	
	private boolean processLogic(final RMCaveatConfigService caveatConfig, final String constraintSpecName, final String[] constraintSpecValues, final String authorityName,
			final boolean andLogic) {
	    final List<String> authorityValues = caveatConfig.getListDetails(constraintSpecName).get(authorityName);
	    
	    // Special case: If no groups specified, then allow access
	    if (constraintSpecValues.length == 0) {
	    	return true;
	    }

	    // For each marking specified...
		for (int specified = 0; specified < constraintSpecValues.length; specified++) {
			boolean accessGranted = false;
			
			//...compare with each marking the user has access to...
			for (int allowed = 0; allowed<authorityValues.size(); allowed++) {
				if (constraintSpecValues[specified].equals(authorityValues.get(allowed))) {
					accessGranted = true;
					break;
				}
			}
			
			//...if we haven't matched yet, then the user doesn't have the specified marking, so return false
			if (accessGranted != andLogic) {
				return !andLogic;
			}
		}
		
		//The user has all the specified markings
		return andLogic;
	}
	
	public Collection<String> getGroupsForCurrentUser(final RMCaveatConfigService configService, final String constraintName) {
		return getGroupsForCurrentUser(configService, constraintName, null);
	}
	
	/**
	 * Get a collection of groups the current user has access to, for the given constraint and, optionally, display type
	 * @param configService Provided by the caller from spring
	 * @param constraintName ShortName of the constraint to return groups for.  Must refer to an EnhancedSecurityConstraint, not a regular RMListOfValuesConstraint
	 * @param type If not null, only return groups which are of this type.  This is case sensitive
	 * @return Collection of Strings representing names of security groups
	 */
	public Collection<String> getGroupsForCurrentUser(final RMCaveatConfigService configService, final String constraintName, final String type) {
		
		//Get all the groups of the given constraint that the current user has
		Collection<String> allowedValues = configService.getRMAllowedValues(constraintName);
		Collection<String> rV=allowedValues;
		
		if (_logger.isDebugEnabled()) {
			_logger.debug("Current User's groups:");
			Iterator<String> logElements = allowedValues.iterator();
			while (logElements.hasNext()) {
				_logger.debug("    "+logElements.next());
			}
		}
		
		
		//If we haven't chosen to restrict by a display type, then we can just use that, otherwise...
		if (type!=null) {
			rV = new ArrayList<String>(allowedValues.size());
			Iterator<EnhancedSecurityConstraint> constraints = _locator.getAllEnhancedSecurityConstraints(true).iterator();
			
			boolean foundConstraint=false;
			//Find all the EnhancedSecurityConstraints and iterate through them until we get to the specified constraint
			while (constraints.hasNext()) {
				EnhancedSecurityConstraint constraint = constraints.next();
				if (_logger.isDebugEnabled()) {
					_logger.debug("Examining constraint "+constraint.getShortName());
				}
				if (constraint.getShortName().equals(constraintName)) {
					foundConstraint=true;
					
					//For each value we would have otherwise returned, remove it from the return value unless it's display type matches the provided type
					Iterator<String> potentialGroupNames = allowedValues.iterator();
					while (potentialGroupNames.hasNext()) {
						String potentialGroupName = potentialGroupNames.next();
						_logger.debug("    Examining "+potentialGroupName);
						GroupDetails potentialGroupDetails=null;
						try {
							potentialGroupDetails=constraint.getDetailsForGroup(potentialGroupName);
						}
						catch (Exception e) { //if we can't retrieve details for the group then it probably doesn't exist anymore, so ignore it
							if (_logger.isInfoEnabled()) {
								_logger.info("Could not retrieve group details for "+potentialGroupName, e);
							}
						}
						if (potentialGroupDetails!=null &&  potentialGroupDetails.getType().equals(type)) {
							_logger.debug("    Using "+potentialGroupName);
							rV.add(potentialGroupName);
						}
					}
					break;
				}
			}
			if (!foundConstraint) {
				throw new EnhancedSecurityException("The constraint "+constraintName+" was not registered as an EnhancedSecurityConstraint");
			}
		}
		return rV;
	}
	
	public Collection<Presence> whoHasAccessToGroup(final RMCaveatConfigService configService, final String constraintName, final String groupName) {
		//Quite a big debugging section here as this was causing some issues while the code was being written.  The real implementation of
		//this method is below
		if (_logger.isDebugEnabled()) {
			_logger.debug("Who has access to "+constraintName+"."+groupName+"?");
			Iterator<String> keys = configService.getListDetails(constraintName).keySet().iterator();
			_logger.debug("    Keys in entry set:");
			while (keys.hasNext()) {
				String key = keys.next();
				Object o = configService.getListDetails(constraintName).get(key);
				StringBuilder sb = new StringBuilder();
				if (o instanceof Collection) {
					Iterator out = ((Collection)o).iterator();
					while (out.hasNext()) {
						sb.append(out.next());
						if (out.hasNext()) {
							sb.append(",");
						}
					}
				}
				else {
					sb.append(o);
				}
				_logger.trace("        "+key+"|"+sb.toString());
			}
		}
		//Actual logic starts here
		
		//Alfresco maps users -> groups
		Map<String, List<String>> authorities = configService.getListDetails(constraintName);
		Collection<Presence> rV = new ArrayList<Presence>(authorities.size()/2); // The div 2 is just a gut heuristic with no particular mathematical reasoning behind it
		Iterator<Entry<String, List<String>>> authorityEntries = authorities.entrySet().iterator();
		int groupsAdded=0;
		while (authorityEntries.hasNext()) {
			Entry<String, List<String>> authorityEntry = authorityEntries.next();
			String authorityName = authorityEntry.getKey();
			if (authorityName.equalsIgnoreCase("manager")) {
				continue;
			}
			Iterator<String> groupsForAuthority = authorityEntry.getValue().iterator();
			while (groupsForAuthority.hasNext()) {
				if (groupsForAuthority.next().equals(groupName)) {
					if (_logger.isDebugEnabled()) {
						_logger.debug("Examining "+authorityName);
					}
					try {
						Presence presence = _presenceService.getUserPresence(authorityName, false);
						String fullUserName = presence.getFullUserName(_personService, _nodeService);
						if (_logger.isDebugEnabled()) {
							_logger.debug("Full username for "+presence.getUserName()+" is "+fullUserName);
						}
						groupsAdded++;
						rV.add(presence);
					}
					catch(Exception e) {
							_logger.warn("Could not fully calculate users in group "+groupName, e);
					}
					break;
				}
			}
		}
		if (_logger.isDebugEnabled()) {
			_logger.debug("Retuning a total of "+groupsAdded+" groups");
		}
		return rV;
	}
}
