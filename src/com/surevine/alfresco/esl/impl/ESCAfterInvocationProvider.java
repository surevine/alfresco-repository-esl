/*
 * Copyright (C) 2008-2010 Surevine Limited.
 *
 * This file is contributed by Surevine to be distributed with Alfresco.
 *
 * This file should be considered a 'Contribution' as defined in Alfrescos 
 * standard contribution agreement, see Paragraph 1 bullet 1 of
 * <http://www.alfresco.org/resource/AlfrescoContributionAgreementv2.pdf>.
 *
 * Surevine's contributions to Alfresco are free software: you can redistribute 
 * and/or modify it under the terms of the GNU Lesser General Public License as 
 * published by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Surevine's contributions to Alfresco are distributed in the hope that it will 
 * be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package com.surevine.alfresco.esl.impl;

import net.sf.acegisecurity.AccessDeniedException;
import net.sf.acegisecurity.Authentication;
import net.sf.acegisecurity.ConfigAttribute;
import net.sf.acegisecurity.ConfigAttributeDefinition;
import net.sf.acegisecurity.afterinvocation.AfterInvocationProvider;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.repo.security.permissions.impl.acegi.FilteringResultSet;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.module.org_alfresco_module_dod5015.caveat.RMCaveatConfigComponent;

import java.util.BitSet;
import java.util.Date;
import org.alfresco.service.cmr.repository.NodeService;


/**
 * After invocation provider to remove items the user can't see from results lists in searches,
 * a la Records Management, by using FilteringResultSets
 * 
 * Copyright Surevine Ltd 2010.  All rights reserved
 * 
 * @author alfresco@surevine.com
 * @author simon.white@surevine.com
 *
 *
 */
public class ESCAfterInvocationProvider implements AfterInvocationProvider {
	
	
	private static final Log LOGGER = LogFactory.getLog(ESCAfterInvocationProvider.class);
    
	/**
	 * String in the Spring config identifying which methods are to be filtered 
	 */
	protected static final String ATTR_IDENT = "AFTER_ESC";
	
	private Date _nextTimeToReportMissingNodes = new Date(0l);
	
	private long _missingNodeReportingFrequencyMillis=60000l;
	
	private RMCaveatConfigComponent _caveatComponent;
	
	public void setCaveatComponent(RMCaveatConfigComponent caveatComponent) {
		_caveatComponent=caveatComponent;
	}
	
	private NodeService _nodeService;
	
	public void setNodeService(NodeService nodeService) {
		_nodeService=nodeService;
	}
	
	public void setFrequencyOfReportingOnMissingNodesInMillis(long frequency)
	{
		_missingNodeReportingFrequencyMillis=frequency;
	}
    	
    /**
     * Perform the filtering on ResultSets, and ignore everything else
     */
	public Object decide(Authentication authentication, Object object, ConfigAttributeDefinition config, Object returnedObject)
			throws AccessDeniedException {
		
		//Pass everything except ResultSets along, filter resultsets
		if (returnedObject!=null && ResultSet.class.isAssignableFrom(returnedObject.getClass())) {
			return decideOnResultSet((ResultSet)returnedObject); //safe cast as per above if() statement
		}
		//else 
		
		return returnedObject;
	}
	
	/**
	 * Called from decide(...) this method filters the contents of the input ResultSet, 
	 * returning an output ResultSet containing only those results that RMCaveatConfig
	 * allows the current user to see
	 * @param unfilteredResultSet A result set to filter
	 * @return ResultSet containing only those elemenets in <code>unfilteredResultSet</code> for which the Caveat
	 * Service allows access to the current user
	 */
	protected ResultSet decideOnResultSet(ResultSet unfilteredResultSet) {
		
		boolean recordAnyMissingNodes = new Date().after(_nextTimeToReportMissingNodes);
		boolean foundMissingNodes=false;
		
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Entering decideOnResultSet");
		}
		
		//Shortcut some obvious exit conditions
		if (unfilteredResultSet==null) {
			return null;
		}
		if (unfilteredResultSet.length()==0) {
			return unfilteredResultSet;
		}
		
        BitSet inclusionMask = new BitSet(unfilteredResultSet.length());
        FilteringResultSet frs = new FilteringResultSet(unfilteredResultSet, inclusionMask);
        
        int length = unfilteredResultSet.length();

        for (int i=0; i < length; i++) {
        	
        	NodeRef nodeRef = unfilteredResultSet.getNodeRef(i);
        	
        	if (_nodeService.exists(nodeRef)) { //If the node exists, check whether we can see it
        	
	        	if (_caveatComponent.hasAccess(nodeRef)) {
	        		if (LOGGER.isDebugEnabled()) {
	        			LOGGER.debug("Access Granted to "+nodeRef);
	        		}
	        		inclusionMask.set(i);	
	        	}
	        	else if (LOGGER.isDebugEnabled()) {
	    			LOGGER.debug("Access forbidden to "+nodeRef);
	    		}
        	}
        	else {
        		foundMissingNodes=true;
        		if (recordAnyMissingNodes)
        		{
        			LOGGER.warn("The node ["+nodeRef+"] was returned from a search but does not exist.");
        		}
        	}
        }
        
        if (foundMissingNodes)
        {
        	_nextTimeToReportMissingNodes = new Date(new Date().getTime()+_missingNodeReportingFrequencyMillis);
        	LOGGER.info("To preserve performance, the system will not report on further missing nodes until "+_nextTimeToReportMissingNodes);
        }
        
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Leaving decideOnResultSet");
		}
        
        return frs;
	}

	public boolean supports(ConfigAttribute attribute) {
		
        if ((attribute.getAttribute() != null) && (attribute.getAttribute().startsWith(ATTR_IDENT)))
        {
            return true;
        }
        else
        {
            return false;
        }
	}

    public boolean supports(Class clazz) {
        return (MethodInvocation.class.isAssignableFrom(clazz));
    }
    
}
