package com.surevine.alfresco.esl.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.alfresco.service.cmr.dictionary.Constraint;
import org.alfresco.service.cmr.dictionary.ConstraintDefinition;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.namespace.QName;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class EnhancedSecurityConstraintLocator 
{
	
	private static final Log LOG = LogFactory.getLog(EnhancedSecurityConstraintLocator.class);

	private DictionaryService _dictionaryService;	
	
	/**
	 * Usually spring-injected
	 */
	public void setDictionaryService(DictionaryService dictionaryService)
	{
		_dictionaryService=dictionaryService;
	}

	
    /**
     * Go through the system and get all of the enhanced security constraints
     */
    public Collection<EnhancedSecurityConstraint> getAllEnhancedSecurityConstraints(boolean returnDeprecated)
    {
    	LOG.debug("Getting all ESL constraints");
    	Collection<EnhancedSecurityConstraint> constraints = new ArrayList<EnhancedSecurityConstraint>(10);
    	
    	//For each model...
    	Iterator<QName> models = _dictionaryService.getAllModels().iterator();
    	while (models.hasNext())
    	{
    		QName model = models.next();
    		LOG.debug("  Looking for ESL constraints in: "+model.toString());
    		
    		//... for each constraint...
    		Iterator<ConstraintDefinition> constraintsInModel = _dictionaryService.getConstraints(model, true).iterator();
    		while (constraintsInModel.hasNext())
    		{
    			//... if it's an EnhancedSecurityConstraint...
    			Constraint constraint = constraintsInModel.next().getConstraint();
    			if (constraint instanceof EnhancedSecurityConstraint)
    			{
    				//...and it's not deprecated...
    				LOG.debug("    Found ESL constraint: "+constraint.getShortName());
    				if (returnDeprecated==true || !((EnhancedSecurityConstraint)constraint).getDeprecated())
    				{
    					//...add it to our return value...
    					constraints.add((EnhancedSecurityConstraint)constraint); //Cast is safe, see instanceof just above
    				}
    			}
    		}
    	}
    	return constraints;
    }

}
