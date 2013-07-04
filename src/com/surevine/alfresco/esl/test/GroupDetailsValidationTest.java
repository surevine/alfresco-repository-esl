package com.surevine.alfresco.esl.test;

import com.surevine.alfresco.esl.exception.EnhancedSecurityException;
import com.surevine.alfresco.esl.impl.EnhancedSecurityConstraint;
import com.surevine.alfresco.esl.impl.GroupDetails;
import com.surevine.alfresco.esl.impl.PermissionAuthority;
import java.util.Iterator;
import junit.framework.TestCase;

public class GroupDetailsValidationTest extends TestCase {

	public void testExpectedInput()
	{
		GroupDetails details = perform("systemName=ORG1\npermissionAuthorities=john   surname - brg1,Simon Othersurname - AOrg2\nhumanName=Organisation One\ntype=Organisation\ndescription=Organisation One Description", true, "Expected normal input");
		assertEquals(details.getSystemName(), "ORG1");
		assertEquals(details.getHumanName(), "Organisation One");
		assertEquals(details.getDescription(), "Organisation One Description");
		assertEquals(details.getType(), "Organisation");
		assertEquals(details.isDeprecated(), false);
		
		Iterator<PermissionAuthority> permissionAuthorities = details.getPermissionAuthorities();
		assertEquals(true, permissionAuthorities.hasNext());
		PermissionAuthority simonAorg2 = permissionAuthorities.next();
		assertEquals("Simon Othersurname", simonAorg2.getName());
		assertEquals("AOrg2", simonAorg2.getDepartment());
		assertEquals(true, permissionAuthorities.hasNext());
		assertEquals(true, permissionAuthorities.hasNext());
		PermissionAuthority johnBrg1 = permissionAuthorities.next();
		assertEquals(false, permissionAuthorities.hasNext());
		assertEquals("john surname", johnBrg1.getName());
		assertEquals("brg1", johnBrg1.getDepartment());
	}
	
	public void testEmptyPermissionAuthorityName()
	{
		perform("systemName=ORG1\npermissionAuthorities=   - brg1,Simon Othersurname - AOrg2\nhumanName=Organisation One\ntype=Organisation\ndescription=Organisation One Description", false, "Empty permission authority name");
	}
	
	public void testEmptyPermissionAuthoritySpecificationTooLong()
	{
		perform("systemName=ORG1\npermissionAuthorities=john   surname - brg1,Simon Othersurname -                                                                                                                                                                                                                                                                                                                                                                                                                 AOrg2\nhumanName=Organisation One\ntype=Organisation\ndescription=Organisation One Description", false, "Permission Authority specification too long");
	}
	
	public void testEmptyPermissionAuthorityDepartment()
	{
		perform("systemName=ORG1\npermissionAuthorities=  user  ,Simon Othersurname - AOrg2\nhumanName=Organisation One\ntype=Organisation\ndescription=Organisation One Description", false, "No department, no - after user name in permission authority specification");
		perform("systemName=ORG1\npermissionAuthorities=  user - ,Simon Othersurname - AOrg2\nhumanName=Organisation One\ntype=Organisation\ndescription=Organisation One Description", false, "A hyphen in the pa specification, but no department specified");

	}
	
	public void testNoPermissionAuthorities()
	{
		perform("systemName=ORG1\nhumanName=Organisation One\ntype=Organisation\ndescription=Organisation One Description", false, "No permissionAuthorities specified");
	}
	
	public void testEmptyPermissionAuthorities()
	{
		perform("systemName=ORG1\nhumanName=Organisation One\npermissionAuthorities=\ntype=Organisation\ndescription=Organisation One Description", false, "No permissionAuthorities specified");
	}
	
	public void testExpectedInputDeprecated()
	{
		GroupDetails details = perform("systemName=ORG1\npermissionAuthorities=john - org1,simon - org2\nhumanName=Organisation One\ndeprecated=tRuE\ntype=Organisation\ndescription=Organisation One Description", true, "Expected normal input with depecated keyword");
		assertEquals(details.getSystemName(), "ORG1");
		assertEquals(details.getHumanName(), "Organisation One");
		assertEquals(details.getDescription(), "Organisation One Description");
		assertEquals(details.getType(), "Organisation");
		assertEquals(details.isDeprecated(), true);
	}
	
	public void testExpectedInputKeysInDifferentCases()
	{
		GroupDetails details = perform("sYsTeMnAmE=ORG1\npermissionAuthorities=john - org1,simon - org2\nhumanname=Organisation One\ntype=Organisation\ndescription=Organisation One Description", true, "Expected normal input confirming that keys are case insensitive");
		assertEquals(details.getSystemName(), "ORG1");
		assertEquals(details.getHumanName(), "Organisation One");
		assertEquals(details.getDescription(), "Organisation One Description");
		assertEquals(details.getType(), "Organisation");
	}
	
	public void testExpectedInputWithWhitespace()
	{
		GroupDetails details = perform("   	systemName =	ORG1	 \npermissionAuthorities=john - org1,simon - org2\n humanName	=	Organisation	One	\n\n	type       =    	Organisation \r\n description = Organisation  One  Description     	", true, "Expected normal input with added whitespace");
		assertEquals(details.getSystemName(), "ORG1");
		assertEquals(details.getHumanName(), "Organisation	One");
		assertEquals(details.getDescription(), "Organisation  One  Description");
		assertEquals(details.getType(), "Organisation");
	}
	
	public void testSystemNameCaseInsensitive()
	{
		GroupDetails details = perform("systemName=oRg1\npermissionAuthorities=john - org1,simon - org2\nhumanName=Organisation One\ntype=Organisation\ndescription=Organisation One Description", true, "Expected normal input but with system name in mixed case - should be converted to upper case");
		assertEquals(details.getSystemName(), "ORG1");
	}
	
	public void testSystemNameWithUnderscore()
	{
		perform("systemName=parent_org1\npermissionAuthorities=john - org1,simon - org2\nhumanName=Organisation One\ntype=Organisation\ndescription=Organisation One Description", true, "Expected normal input but with system name containing an underscore");
	}
	
	public void testArgumentsInDifferentOrder()
	{
		perform("description=Organisation One Description\npermissionAuthorities=john - org1,simon - org2\nhumanName=Organisation One\ntype=Organisation\nsystemName=ORG1", true, "Expected normal input with argument order switched");
	}
	
	public void testSameKeyDefinedTwice()
	{
		perform("description=Organisation One Description\npermissionAuthorities=john - org1,simon - org2\nhumanName=Organisation One\ntype=Organisation,systemName=ORG1\ndescription=This Should Fail", false, "Description key defined twice - error expected");
	}
	
	public void testCommasInDefinition()
	{
		perform("description=Organisa,tion One Description\npermissionAuthorities=john - org1,simon - org2\nhumanName=Organisation One\ntype=Organisation,systemName=ORG1\ndescription=This Should Fail", false, "Errant , in the description");
	}
	
	public void testEqualsInDefinition()
	{
		perform("description=Organisa=tion One Description\npermissionAuthorities=john - org1,simon - org2\nhumanName=Organisation One\ntype=Organisation,systemName=ORG1\ndescription=This Should Fail", false, "Errant = in the description");
	}
	
	public void testAllowablePunctuation()
	{
		perform("description=Organisa[]<>.?;'\\}!@%^&*(O)-+tion One Description\npermissionAuthorities=john - org1,simon - org2\nhumanName=Organisation One\ntype=Organisation\nsystemName=ORG1", true, "Allowable punctuation charecters");
	}
	
	public void testPunctuationInSystemName()
	{
		perform("description=Organisation One Description\npermissionAuthorities=john - org1,simon - org2\nhumanName=Organisation One\ntype=Organisation,systemName=OR-G1", false, "Adding a - to the system name to confirm A-Z 0-9 only");
	}
	
	public void testMissingKeysButAllAreMandatory()
	{
		perform("humanName=Organisation One\ntype=Organisation\npermissionAuthorities=john - org1,simon - org2\ndescription=Organisation One Description", false, "SystemName key is missing");
		perform("systemName=ORG1\ntype=Organisation\npermissionAuthorities=john - org1,simon - org2\ndescription=Organisation One Description", false, "HumanName key is missing");
		perform("systemName=ORG1\nhumanName=Organisation One\npermissionAuthorities=john - org1,simon - org2\ndescription=Organisation One Description", false, "Type key is missing");
		perform("systemName=ORG1\nhumanName=Organisation One\npermissionAuthorities=john - org1,simon - org2\ntype=Organisation", false, "Description key is missing");
	}
	
	public void testEmptyString()
	{
		perform("", false, "Empty String");
	}
	
	public void testWhitespaceOnly()
	{
		perform(" ", false, "Whitespace only");
	}
	
	public void testNull()
	{
		perform(null, false, "Input String is null");
	}
	
	public void testUnknownKey()
	{
		perform("systemName=ORG1\nhumanName=Organisation One\npermissionAuthorities=john - org1,simon - org2\ntype=Organisation\ndescription=Organisation One Description,foo=bar", false, "Unknown key (foo)");
	}
	
	public void testKeyWithoutEquals()
	{
		perform("systemName=ORG1\nhumanName=\npermissionAuthorities=john - org1,simon - org2\ntype=Organisation\ndescription=Organisation One Description,foo=bar", false, "humanName key with no value");
	}
	
	private GroupDetails perform(String specification, boolean successExpected, String description)
	{
		EnhancedSecurityConstraint constraint = new EnhancedSecurityConstraint();
		GroupDetails groupDetails = null;
		try
		{
			groupDetails = new GroupDetails(specification, constraint);
			groupDetails.validate();
		}
		catch (EnhancedSecurityException e)
		{
			if (successExpected)
			{
				throw e;
			}
			//else exception expected = do nothing
			return null;
		}
		
		if (!successExpected) //We shouldn't have got here, so fail!
		{
			fail("String specification ["+specification+"] for ["+description+"] was created succesfully, but failure was expected");
		}
		return groupDetails;
	}
	
}
