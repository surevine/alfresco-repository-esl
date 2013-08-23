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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.StoreRef;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.surevine.alfresco.esl.exception.EnhancedSecurityException;

import net.sf.acegisecurity.ConfigAttribute;
import net.sf.acegisecurity.ConfigAttributeDefinition;
import com.surevine.alfresco.esl.NodeServiceMethodInterceptionService;

/**
 * Concrete implementation of NodeServiceMethodInterceptionService providing methods to link NodeRefs to the MethodInvocations that contain them.
 * 
 * Copyright Surevine Ltd 2010. All rights reserved
 * 
 * @author alfresco@surevine.com
 * @author simon.white@surevine.com
 * 
 */
public class NodeServiceMethodInterceptionServiceImpl implements NodeServiceMethodInterceptionService {

    private static final Log LOGGER = LogFactory.getLog(NodeServiceMethodInterceptionServiceImpl.class);
    private static final String ACL_NODE = "ACL_NODE";

    private NodeService _nodeService;

    /**
     * Injected
     * 
     * @param nodeService
     */
    public void setNodeService(NodeService nodeService) {
        _nodeService = nodeService;
    }

    /**
     * Does the method we're intercepting here actually have a parameter of type NodeRef?
     * 
     * @param config
     * @return
     */
    @SuppressWarnings("unchecked")
    public boolean hasNodeRefDefinition(ConfigAttributeDefinition config) {
        Iterator<ConfigAttribute> atts = (Iterator<ConfigAttribute>) (config.getConfigAttributes());

        while (atts.hasNext()) {
            ConfigAttribute attr = atts.next(); // These attributes are the text written into public-security-services-context.xml in Alfresco
            String attrVal = attr.getAttribute();

            if (attrVal.startsWith(ACL_NODE)) { // At least one NodeRef parameter, so we need to pass this method through an ESC
                return true;
            }
        }
        return false; // No node definitions
    }

    /**
     * Get all the noderefs used in the given method call that we should protect with an ESC by parsing the Spring config. Any NodeRef we protect with an ACL we'll also protect with an ESC.
     * 
     * @param config
     * @param mi
     * @return
     */
    @SuppressWarnings("unchecked")
    public Collection<NodeRef> getNodeRefsUsedInMethodCall(ConfigAttributeDefinition config, MethodInvocation mi) {

        // Creating an ArrayList of size 3 on the basis it's better to allocate 24 bytes extra memory most of the time
        // than performing array copies some of the time. Most of the time, there will only be one value in the
        // return Collection
        Collection<NodeRef> refs = new ArrayList<NodeRef>(3);
        Iterator<ConfigAttribute> atts = (Iterator<ConfigAttribute>) (config.getConfigAttributes());

        while (atts.hasNext()) {
            ConfigAttribute attr = atts.next();
            String attrVal = attr.getAttribute();
            if (attrVal.startsWith(ACL_NODE)) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Found Node config for " + mi);
                }
                Object o = mi.getArguments()[getParamIdx(attrVal)];
                NodeRef nr = convertToNodeRef(o);
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Found nodeRef: " + nr);
                }
                refs.add(nr);
            }
        }
        return refs;
    }

    /**
     * For a given Spring ConfigAttribute, parse the text in the XML file and work out which parameter in the protected method it is that the security definition is trying to protect. Note that as a private method, a minimal of checking is performed around the input parameters.
     * 
     * @param attrVal
     *            Value of a Config Attribute
     * @return index into the method parameters at which the config indicates a NodeRef can be found. We should run an ESC against that NodeRef
     */
    public int getParamIdx(String attrVal) {
        String startOfNumber = null;
        String numberStr = null;
        int paramIdx = -1; // We can guarantee this won't still be -1 by the time it's used if the config isn't broken
        try {
            startOfNumber = attrVal.substring(9, attrVal.length()); // 9=after "ACL_NODE."
            numberStr = startOfNumber.substring(0, startOfNumber.indexOf("."));
            paramIdx = Integer.parseInt(numberStr);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Looking for a NodeRef at index " + paramIdx);
            }
        } catch (NumberFormatException e) {
            throw new EnhancedSecurityException("Could not convert " + numberStr + " from " + attrVal + " into an int", e);
        } catch (ArrayIndexOutOfBoundsException ex) {
            throw new EnhancedSecurityException("Could not parse " + attrVal + " as a valid Attribute", ex);
        }
        return paramIdx;
    }

    /**
     * Simple method to take a NodeRef or StoreRef and return a NodeRef
     * 
     * @param o
     * @return
     */
    public NodeRef convertToNodeRef(Object o) {
        if (o == null) {
            throw new EnhancedSecurityException("Expecting NodeRef or StoreRef, found null", new NullPointerException());
        }
        Class clazz = o.getClass();
        if (NodeRef.class.isAssignableFrom(clazz)) {
            return (NodeRef) o;
        }
        if (StoreRef.class.isAssignableFrom(clazz)) {
            StoreRef store = (StoreRef) o;
            return getStoreRootNode(store);
        }
        throw new EnhancedSecurityException("Expecting NodeRef or StoreRef, found " + o.getClass());
    }

    protected NodeRef getStoreRootNode(StoreRef store) {
        return _nodeService.getRootNode(store);
    }

}
