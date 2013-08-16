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
package com.surevine.alfresco.esl.impl;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;
import org.alfresco.module.org_alfresco_module_dod5015.caveat.RMCaveatConfigService;
import com.surevine.alfresco.esl.exception.EnhancedSecurityException;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

/**
 * Returns details on the EnhancedSecurityConstraints used in the system and their values/groups in JSON format
 * 
 * EXAMPLE OUTPUT:
 * 
 * <pre>
 * {
 *   "constraints":[{
 *    "constraintName": "es_validOpenMarkings",
 *    "constraintDescription": "Open Groups",
 *    "displayPriority": "High",
 *    "markings": [ { "name": "OG01", "longName": "OPENGROUP1", "type": "typeA", "description":"This is the first test open group", "permissionAuthorities": ["user-org1", "user-org2"], "hasAccess": true},
 *                  { "name": "OG20", "longName": "OPENGROUP2", "type": "typeA", "description":"This is the second test open group", "permissionAuthorities": ["user-org1", "user-org2"], "hasAccess": false}
 *                ]
 *   },
 *   {
 *    "constraintName": "es_validClosedMarkings",     
 *    "constraintDescription": "Closed Groups",
 *    "displayPriority": "Low",
 *    "markings": [{ "name": "CLOSEDGROUP1", "longName": "CLOSEDGROUP1", "type": "Closed", "description":"This is the first test closed group", "permissionAuthorities": ["user-org1", "user-org2"], "hasAccess": true},
 *                 { "name": "CG02", "longName": "CLOSEDGROUP2", "type": "Closed", "description":"This is the second test closed group", "permissionAuthorities": ["user-org1", "user-org2"], "hasAccess": true},
 *                 { "name": "APPLESANDPEARS", "longName": "APPLES AND PEARS", "type": "Closed", "description": "No-one should have this group.  If you can see it, something's gone wrong", "permissionAuthorities": ["user-org1", "user-org2"], "hasAccess": true}
 *                ]
 *  }]
 * }
 * </pre>
 * @author simonw
 *
 */
public class GroupDetailsWebscript extends AbstractWebScript 
{
	
	private static final Log LOG = LogFactory.getLog(GroupDetailsWebscript.class);
	
	private DictionaryService _dictionaryService;
	
	private RMCaveatConfigService _caveatConfigService;
	
	/**
	 * Usually spring-injected
	 */
	public void setDictionaryService(DictionaryService dictionaryService)
	{
		_dictionaryService=dictionaryService;
	}

	/**
	 * Usually spring-injected
	 */
	public void setCaveatConfigService(RMCaveatConfigService caveatConfigService)
	{
		_caveatConfigService=caveatConfigService;
	}
	
	/**
	 * Execute the webscript.  There's no parameters to this call (although the results do vary depending upon logged in user via _caveatConfig.getAllowedValues())
	 * so the actual logic of this method is factored-out into a separate method for easier unit testing and reuse
	 * 
	 */
    public void execute(WebScriptRequest req, WebScriptResponse resp) throws IOException
    {
    	resp.setContentType("application/json");
    	resp.getWriter().write(getJSONResponseString());   
    }
    
    /**
     * The actual logic.  See comments on the class for example output
     * @return
     */
    public String getJSONResponseString()
    {
      	try
    	{
      		//Get all the enhanced security constraints
    		Iterator<EnhancedSecurityConstraint> eslConstraints = getAllEnhancedSecurityConstraints().iterator();
    		JSONObject root = new JSONObject();
    		JSONArray constraints= new JSONArray();
    		root.put("constraints", constraints);
    		
    		//Iterate through the constraints, populating the JSON to return.  All very straightforward, although the code is a little dense
    		while (eslConstraints.hasNext())
    		{
    			EnhancedSecurityConstraint eslConstraint = eslConstraints.next();
    			Iterator<GroupDetails> eslConstraintDetails = eslConstraint.getGroupDetails().iterator();
    			Collection<String> allowedValuesForUser = _caveatConfigService.getRMAllowedValues(eslConstraint.getShortName());
    			JSONObject constraint = new JSONObject();
    			constraints.put(constraint);
    			constraint.put("constraintName", eslConstraint.getShortName().replace(':', '_'));
    			constraint.put("constraintDescription", eslConstraint.getDescription());
    			constraint.put("displayPriority", eslConstraint.getDisplayPriority());
    			JSONArray markings = new JSONArray();
    			constraint.put("markings", markings);
    			while (eslConstraintDetails.hasNext())
    			{
    				GroupDetails groupDetails = eslConstraintDetails.next();
    				boolean hasAccess = allowedValuesForUser.contains(groupDetails.getSystemName());
    				if ((!eslConstraint.getFilterDisplay() || hasAccess) && !groupDetails.isDeprecated())
    				{
    					JSONObject marking = new JSONObject();
    					markings.put(marking);
    					marking.put("description", groupDetails.getDescription());
    					marking.put("hasAccess", hasAccess);
    					marking.put("type", groupDetails.getType());
    					marking.put("longName", groupDetails.getHumanName());
    					marking.put("name", groupDetails.getSystemName());
    					JSONArray permissionAuthorities = new JSONArray();
    					marking.put("permissionAuthorities", permissionAuthorities);
    					Iterator<PermissionAuthority> authorities = groupDetails.getPermissionAuthorities();
    					while (authorities.hasNext())
    					{
    						PermissionAuthority authority = authorities.next();
    						permissionAuthorities.put(authority.getName()+" - "+authority.getDepartment());
    					}
    				}
    			}
    		}
    		String rVal = root.toString();
    		if (LOG.isDebugEnabled())
    		{
    			LOG.debug("Returning: "+rVal);
    		}
    		return rVal;
    	}
    	catch (JSONException e)
    	{
    		throw new EnhancedSecurityException("The security configuration was corrupt and could not be serialised to JSON", e);
    	}
    }
    
    /**
     * Go through the system and get all of the enhanced security constraints
     */
    protected Collection<EnhancedSecurityConstraint> getAllEnhancedSecurityConstraints()
    {
    	EnhancedSecurityConstraintLocator locator = new EnhancedSecurityConstraintLocator();
    	locator.setDictionaryService(_dictionaryService);
    	return locator.getAllEnhancedSecurityConstraints(false);
    }
}
