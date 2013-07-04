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
package com.surevine.alfresco.esl;

import java.util.Collection;

import org.alfresco.service.cmr.repository.NodeRef;
import org.aopalliance.intercept.MethodInvocation;

import net.sf.acegisecurity.ConfigAttributeDefinition;

/**
 * Describes a service implementing a collection of methods to links NodeRefs to MethodInvocations operating
 * on those NodeRefs
 *
 * Copyright Surevine Ltd 2010.  All rights reserved
 * 
 * @author alfresco@surevine.com
 * @author simon.white@surevine.com
 *
 */
public interface NodeServiceMethodInterceptionService {
	
	/**
	 * Does the given config definition indicate that protection should be applied to Nodes?
	 * @param config
	 * @return
	 */
	public boolean hasNodeRefDefinition (ConfigAttributeDefinition config);
	
	/**
	 * For a given method call, return the collection of NodeRefs used as parameters to that method call that
	 * are also marked as protected in the security configuration
	 * @param config Configuration indicating which, if any, NodeRefs in the method call parameters should be protected
	 * @param mi MethodInvocation object describing an intercepted method call 
	 * @return Collection of NodeRefs that are both in the MethodInvocationParameters and are configured in the Configuration
	 */
	public Collection<NodeRef> getNodeRefsUsedInMethodCall(ConfigAttributeDefinition config, MethodInvocation mi);
	
	/**
	 * For a given Node-level security definition within a ConfigAttributeDefinition, return the index into the
	 * protected method's parameters for which that node refers
	 * @param attrVal
	 * @return
	 */
	public int getParamIdx(String attrVal);
	
	/**
	 * Take a NodeRef or StoreRef and return a NodeRef representing the input parameter
	 * @param o
	 * @return
	 */
	public NodeRef convertToNodeRef(Object o);
}