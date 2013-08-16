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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.sf.acegisecurity.Authentication;
import net.sf.acegisecurity.ConfigAttribute;
import net.sf.acegisecurity.ConfigAttributeDefinition;
import net.sf.acegisecurity.vote.AccessDecisionVoter;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.security.permissions.impl.acegi.ACLEntryVoter;
import org.alfresco.repo.site.SiteModel;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.security.AuthorityService;
import org.alfresco.service.cmr.security.AuthorityType;
import org.alfresco.service.cmr.security.PermissionService;
import org.alfresco.service.cmr.site.SiteVisibility;
import org.alfresco.service.namespace.QName;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.surevine.alfresco.esl.NodeServiceMethodInterceptionService;

import org.alfresco.model.ContentModel;
import org.alfresco.module.org_alfresco_module_dod5015.caveat.RMCaveatConfigComponent;

public class ESCEntryVoter implements AccessDecisionVoter {

	// Permission types defined in the Spring config.
	// Note that we're re-using the ACL definitions here
	private static final String ACL_NODE = "ACL_NODE";
	private static final String ACL_PARENT = "ACL_PARENT";
	private static final String ACL_ALLOW = "ACL_ALLOW";
	private static final String ACL_METHOD = "ACL_METHOD";
	private static final String ACL_DENY = "ACL_DENY";

	private static Log logger = LogFactory.getLog(ESCEntryVoter.class);

	public void setCaveatComponent(RMCaveatConfigComponent caveatComponent) {
		_caveatComponent = caveatComponent;
	}

	private RMCaveatConfigComponent _caveatComponent;

	public void setNodeServiceMethodInterceptionService(
			NodeServiceMethodInterceptionService nsmis) {
		_nodeMethodService = nsmis;
	}

	private NodeServiceMethodInterceptionService _nodeMethodService;
	
	private DictionaryService _dictionaryService;

	public void setDictionaryService(DictionaryService ds) {
		_dictionaryService = ds;
	}

	private NodeService _nodeService;

	public void setNodeService(NodeService ns) {
		_nodeService = ns;
	}

	private PermissionService _permissionService;

	public void setPermissionService(PermissionService ps) {
		_permissionService = ps;
	}

	private AuthorityService _authorityService;

	public void setAuthorityService(AuthorityService as) {
		_authorityService = as;
	}

	public int vote(Authentication authentication, Object object,
			ConfigAttributeDefinition config) {
		// Debug the method we've just intercepted
		if (logger.isDebugEnabled()) {
			MethodInvocation mi = (MethodInvocation) object;
			logger.debug("Method: " + mi.getMethod().toString());
		}

		// Let the system user straight through
		if (AuthenticationUtil.isRunAsUserTheSystemUser()) {
			if (logger.isDebugEnabled()) {
				logger.debug("Access granted for the system user");
			}
			return AccessDecisionVoter.ACCESS_GRANTED;
		}

		// ESC business logic is node-level, so if we're not doing a node-level
		// method then grant access
		if (!_nodeMethodService.hasNodeRefDefinition(config)) {
			if (logger.isTraceEnabled()) {
				logger.trace("Context object " + object
						+ " does not have a NodeRef parameter for this method");
			}
			return AccessDecisionVoter.ACCESS_GRANTED;
		}

		// Perform the ESC and, if it passes, return GRANTED
		if (userHasAccess(config, (MethodInvocation) object)) {
			return ACLEntryVoter.ACCESS_GRANTED;
		}

		return ACLEntryVoter.ACCESS_DENIED;
	}

	/**
	 * Can the user actually see all the nodes mentioned in the parameters of
	 * the intercepted method?
	 * 
	 * As this method will be called very frequently, log statements are
	 * optimised for performance rather than readability, and some other minor
	 * optimisations have been performed.
	 * 
	 * @param config
	 *            Describes the security configuration defined in the Alfresco
	 *            Spring XML. We're piggybacking of the ACL definitions
	 * @param mi
	 *            Information on the method the user is trying to invoke.
	 * @return True IFF the user can access every NodeRef in the method
	 *         arguments that is defined in the Spring XML as protected by ACL
	 */
	protected boolean userHasAccess(ConfigAttributeDefinition config,
			MethodInvocation mi) {

		Collection<NodeRef> nodeRefs = _nodeMethodService
				.getNodeRefsUsedInMethodCall(config, mi);

		Iterator<NodeRef> nodeRefIter = nodeRefs.iterator();

		// If we can't see any of the NodeRefs in the method call, deny access
		while (nodeRefIter.hasNext()) {
			NodeRef nr = nodeRefIter.next();
			if (!_caveatComponent.hasAccess(nr) || !userHasSiteAccess(nr)) {
				return false;
			}
		}

		// We can see all of the NodeRefs in the method call, so grant access
		return true;

	}

	public boolean supports(Class clazz) {
		return (MethodInvocation.class.isAssignableFrom(clazz));
	}

	public boolean supports(ConfigAttribute attribute) {
		if ((attribute.getAttribute() != null)
				&& (attribute.getAttribute().startsWith(ACL_NODE)
						|| attribute.getAttribute().startsWith(ACL_PARENT)
						|| attribute.getAttribute().equals(ACL_ALLOW)
						|| attribute.getAttribute().startsWith(ACL_METHOD) || attribute
						.getAttribute().equals(ACL_DENY))) {
			return true;
		} else {
			return false;
		}
	}

	protected NodeRef getSiteNodeRef(NodeRef nodeRef) {
		NodeRef siteNodeRef = null;
		QName nodeRefType = _nodeService.getType(nodeRef);
		if (_dictionaryService.isSubClass(nodeRefType, SiteModel.TYPE_SITE) == true) {
			siteNodeRef = nodeRef;
		} else {
			ChildAssociationRef primaryParent = _nodeService
					.getPrimaryParent(nodeRef);
			if (primaryParent != null && primaryParent.getParentRef() != null) {
				siteNodeRef = getSiteNodeRef(primaryParent.getParentRef());
			}
		}
		return siteNodeRef;
	}

	public boolean userHasSiteAccess(NodeRef target) {
		String userName = AuthenticationUtil.getRunAsUser();
		NodeRef siteNodeRef=getSiteNodeRef(target);
		if (siteNodeRef==null) {
			return true;
		}
		if (isSitePublic(siteNodeRef)) {
			return true;
		}
		return (!getPermissionGroups(siteNodeRef, userName).isEmpty());
	}

	private List<String> getPermissionGroups(NodeRef siteNodeRef, String authorityName) {
		if (siteNodeRef == null) {
			throw new RuntimeException("The site does not exist");
		}

		String siteShortName = getSiteShortName(siteNodeRef);

		List<String> fullResult = new ArrayList<String>(5);
		QName siteType = _nodeService.getType(siteNodeRef);
		Set<String> roles = _permissionService.getSettablePermissions(siteType);

		// First use the authority's cached recursive group memberships to
		// answer the question quickly
		Set<String> authorities = _authorityService
				.getAuthoritiesForUser(authorityName);
		for (String role : roles) {
			String roleGroup = getSiteRoleGroup(siteShortName, role, true);
			if (authorities.contains(roleGroup)) {
				fullResult.add(roleGroup);
			}
		}

		// Unfortunately, due to direct membership taking precedence, we can't
		// answer the question quickly if more than one role has been inherited
		if (fullResult.size() <= 1) {
			return fullResult;
		}

		// Check direct group memberships
		List<String> result = new ArrayList<String>(5);
		Set<String> authorityGroups = _authorityService
				.getContainingAuthorities(AuthorityType.GROUP, authorityName,
						true);
		for (String role : roles) {
			String roleGroup = getSiteRoleGroup(siteShortName, role, true);
			if (authorityGroups.contains(roleGroup)) {
				result.add(roleGroup);
			}
		}

		// If there are user permissions then they take priority
		return result.size() > 0 ? result : fullResult;
	}

	protected String getSiteShortName(NodeRef siteNodeRef) {
		// Get the properties
		Serializable property = _nodeService.getProperty(siteNodeRef,
				ContentModel.PROP_NAME);
		String shortName = property.toString();
		return shortName;
	}

	/**
	 * Helper method to get the name of the site group
	 * 
	 * @param shortName
	 *            site short name
	 * @return String site group name
	 */
	public String getSiteGroup(String shortName, boolean withGroupPrefix) {
		StringBuffer sb = new StringBuffer(64);
		if (withGroupPrefix == true) {
			sb.append(PermissionService.GROUP_PREFIX);
		}
		sb.append("site_");
		sb.append(shortName);
		return sb.toString();
	}

	public String getSiteRoleGroup(String shortName, String permission,
			boolean withGroupPrefix) {
		return getSiteGroup(shortName, withGroupPrefix) + '_' + permission;
	}
	
	protected boolean isSitePublic(NodeRef siteNodeRef) {
		String visibilityValue=_nodeService.getProperty(siteNodeRef, SiteModel.PROP_SITE_VISIBILITY).toString();
		return SiteVisibility.valueOf(visibilityValue).equals(SiteVisibility.PUBLIC);
	}
}
