package com.surevine.alfresco.esl;

import org.alfresco.module.org_alfresco_module_dod5015.caveat.ScriptRMCaveatConfigService;
import java.util.List;

public class ESLScriptRMCaveatConfigService extends ScriptRMCaveatConfigService {
	
    public List<String> getAllowedValuesForCurrentUser(String constraintName) {	  
    	
    	constraintName = constraintName.replace('_', ':');
    	
    	List<String> allowedList = getRmCaveatConfigService().getRMAllowedValues(constraintName);
    	
    	return allowedList;
    }

}
