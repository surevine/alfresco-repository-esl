package com.surevine.alfresco.esl.test.stub;

import java.util.Collection;

import com.surevine.alfresco.esl.impl.EnhancedSecurityConstraint;
import com.surevine.alfresco.esl.impl.GroupDetailsWebscript;

/**
 * Version of group details webscript that overrides the section of the code that deals with the data dictionary,
 * which allows us to test without just re-testing the dictionary and with less dependencies
 * @author simonw
 *
 */
public class StubbedModelGroupDetailsWebscript extends GroupDetailsWebscript {
	
    protected Collection<EnhancedSecurityConstraint> getAllEnhancedSecurityConstraints()
    {
    	return new StubEnhancedSecurityConstraintLocator().getAllEnhancedSecurityConstraints(true);
	}

}
