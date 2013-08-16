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
