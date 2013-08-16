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

import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;

import net.sf.acegisecurity.AccessDeniedException;
import net.sf.acegisecurity.Authentication;
import net.sf.acegisecurity.ConfigAttribute;
import net.sf.acegisecurity.ConfigAttributeDefinition;
import net.sf.acegisecurity.afterinvocation.AfterInvocationProvider;

/**
 * Permissive AfterInvocationProvider implementation that participates in decisions for all ConfigAttributes,
 * provides "after invocation" processing for all object types, and allows access to all return values
 * 
 * @author paulguare
 *
 */
public class PermissiveRMAfterInvocationProvider implements AfterInvocationProvider, InitializingBean {
    
    private static Log logger = LogFactory.getLog(PermissiveRMAfterInvocationProvider.class);
    
    @SuppressWarnings("unused")
    private static final String AFTER_RM = "AFTER_RM";

    @Override
    public Object decide(Authentication authentication, Object object, ConfigAttributeDefinition config, Object returnedObject) throws AccessDeniedException {
        if (logger.isDebugEnabled())
        {
            logger.debug("Method: " + ((MethodInvocation) object).getMethod().toString());
            logger.debug("Access allowed for " + object.getClass().getName());
        }
        return returnedObject;
    }

    @Override
    public boolean supports(ConfigAttribute config) {
        return true;
    }

    @Override
    public boolean supports(@SuppressWarnings("rawtypes") Class clazz) {
        return true;
    }
    
    @Override
    public void afterPropertiesSet() throws Exception {
                
    }

}
