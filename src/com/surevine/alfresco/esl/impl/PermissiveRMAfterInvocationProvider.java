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
