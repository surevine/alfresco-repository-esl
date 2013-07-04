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
	
	public Object decide(Authentication authentication, Object object, ConfigAttributeDefinition config,
			Object returnedObject) throws AccessDeniedException {
		Iterator iter = this.getProviders().iterator();

		Object result = returnedObject;

		while (iter.hasNext()) {
			AfterInvocationProvider provider = (AfterInvocationProvider) iter.next();
			try {
				result = provider.decide(authentication, object, config, result);
			}
			catch (InvalidNodeRefException e) {
				if (provider instanceof RMAfterInvocationProvider) {
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("Skipping an InvalidNodeRefException: "+e);
					}
				}
			}
		}

		return result;

	}
}