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

import org.alfresco.repo.security.permissions.impl.acegi.AffirmativeBasedAccessDecisionManger;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.security.AccessStatus;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


import net.sf.acegisecurity.ConfigAttributeDefinition;
import net.sf.acegisecurity.vote.AccessDecisionVoter;
import net.sf.acegisecurity.Authentication;

import java.util.Iterator;

//Recommend this class be removed before production - just a template to play with voting

/**
 * @deprecated This class should be disposed of before we reach production
 */
@Deprecated
public class LoggingAffirmitiveAccessDescisionManager extends
		AffirmativeBasedAccessDecisionManger {
	
    private static final Log logger = LogFactory.getLog(LoggingAffirmitiveAccessDescisionManager.class);
	
    
    public void decide(Authentication authentication, Object object, ConfigAttributeDefinition config) {
    	pre(object, config);
    		
    	super.decide(authentication, object, config);
    }
    
	 public AccessStatus pre(Object object, ConfigAttributeDefinition attr) {
		 
	        Iterator iter = this.getDecisionVoters().iterator();
	        int deny=0;

	        while (iter.hasNext())
	        {
	            AccessDecisionVoter voter = (AccessDecisionVoter) iter.next();
	            logger.debug("Using voter: "+voter);
	            int result = voter.vote(AuthenticationUtil.getFullAuthentication(), object, attr);
	            logger.debug("Result was: "+result);
	            
	            
	            switch (result)
	            {
	            case AccessDecisionVoter.ACCESS_GRANTED:
	            	logger.debug("Granting Access");
	            	return AccessStatus.ALLOWED;

	            case AccessDecisionVoter.ACCESS_DENIED:
	                logger.debug("Denying Access");
	                deny++;
	                break;

	            default:
	            	logger.debug("Abstaining");
	                break;
	            }
	        }
	        
	        logger.debug("All voters have voted");

	        
	        if (deny >0) 
	        {
	        	logger.debug(deny+" voters have denied.  Denied");
	            return AccessStatus.DENIED;
	        }

	        logger.debug("All voters abstained.  Breaking the tie");
	        // To get this far, every AccessDecisionVoter abstained
	        if (this.isAllowIfAllAbstainDecisions())
	        {
	        	logger.debug("Allowing");
	            return AccessStatus.ALLOWED;
	        }
	        else
	        {
	        	logger.debug("Denying");
	            return AccessStatus.DENIED;
	        }

	    }

}
