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

import java.util.Iterator;

import org.alfresco.module.org_alfresco_module_dod5015.capability.RMAfterInvocationProvider;
import org.alfresco.service.cmr.repository.InvalidNodeRefException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.sf.acegisecurity.AccessDeniedException;
import net.sf.acegisecurity.Authentication;
import net.sf.acegisecurity.ConfigAttributeDefinition;
import net.sf.acegisecurity.afterinvocation.AfterInvocationProvider;
import net.sf.acegisecurity.afterinvocation.AfterInvocationProviderManager;

public class ESLAfterInvocationProviderManager extends AfterInvocationProviderManager {

    private static final Log LOGGER = LogFactory.getLog(ESLAfterInvocationProviderManager.class);

    public Object decide(Authentication authentication, Object object, ConfigAttributeDefinition config, Object returnedObject) throws AccessDeniedException {
        Iterator iter = this.getProviders().iterator();

        Object result = returnedObject;

        while (iter.hasNext()) {
            AfterInvocationProvider provider = (AfterInvocationProvider) iter.next();
            try {
                result = provider.decide(authentication, object, config, result);
            } catch (InvalidNodeRefException e) {
                if (provider instanceof RMAfterInvocationProvider) {
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("Skipping an InvalidNodeRefException: " + e);
                    }
                }
            }
        }

        return result;

    }
}
