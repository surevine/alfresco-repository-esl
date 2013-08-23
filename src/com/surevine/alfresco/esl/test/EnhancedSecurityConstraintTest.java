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
package com.surevine.alfresco.esl.test;

import java.util.ArrayList;
import java.util.List;

import com.surevine.alfresco.esl.exception.EnhancedSecurityException;
import com.surevine.alfresco.esl.impl.EnhancedSecurityConstraint;
import com.surevine.alfresco.esl.impl.EnhancedSecurityConstraintLocator;
import com.surevine.alfresco.esl.impl.GroupDetails;
import com.surevine.alfresco.esl.test.stub.StubEnhancedSecurityConstraintLocator;

import junit.framework.TestCase;

public class EnhancedSecurityConstraintTest extends TestCase {

    /**
     * For tests of the detail of a specification, see GroupDetailsValidationTest
     */
    public void testNormalSpecification() {
        String a = "systemName=ORG2\nhumanName=Organisation One\ntype=Organisation\ndescription=Organisation One Description\npermissionAuthorities=Hello World - test, Another Test - Org";
        String b = "systemName=ORG3\nhumanName=Organisation Two\ntype=Organisation\ndescription=Organisation Two Description\npermissionAuthorities=Hello World - test, Another Test - Org";
        String c = "systemName=ORG4\nhumanName=Organisation Three\ntype=Organisation\ndescription=Organisation Three Description\npermissionAuthorities=Hello World - test, Another Test - Org";
        List<String> spec = new ArrayList<String>(3);
        spec.add(a);
        spec.add(b);
        spec.add(c);
        new EnhancedSecurityConstraint().setGroupDetailsSpecification(spec);
    }

    /**
     * For tests of the detail of a specification, see GroupDetailsValidationTest
     */
    public void testEmptySpecification() {
        try {
            List<String> spec = new ArrayList<String>(3);
            new EnhancedSecurityConstraint().setGroupDetailsSpecification(spec);
        } catch (EnhancedSecurityException e) {
            return; // test success
        }
        fail("Empty test specification should cause an exception, but it did not");
    }

    public void testNullSpecification() {
        try {
            new EnhancedSecurityConstraint().setGroupDetailsSpecification(null);
        } catch (EnhancedSecurityException e) {
            return; // test success
        }
        fail("Null test specification should cause an exception, but it did not");
    }

    public void testSystemNameDefinedTwice() {
        try {
            String a = "systemName=ORG2\nhumanName=Organisation One\ntype=Organisation\ndescription=Organisation One Description\npermissionAuthorities=Hello World - test, Another Test - Org";
            String b = "systemName=ORG3\nhumanName=Organisation Two\ntype=Organisation\ndescription=Organisation Two Description\npermissionAuthorities=Hello World - test, Another Test - Org";
            String c = "systemName=ORG2\nhumanName=Organisation Three\ntype=Organisation\ndescription=Organisation Three Description\npermissionAuthorities=Hello World - test, Another Test - Org";
            List<String> spec = new ArrayList<String>(3);
            spec.add(a);
            spec.add(b);
            spec.add(c);
            new EnhancedSecurityConstraint().setGroupDetailsSpecification(spec);
        } catch (EnhancedSecurityException e) {
            return; // test success
        }
        fail("Defining the same system name twice in test specification should cause an exception, but it did not");
    }

    public void testHumanNameDefinedTwice() {
        try {
            String a = "systemName=ORG2\nhumanName=Organisation One\ntype=Organisation\ndescription=Organisation One Description\npermissionAuthorities=Hello World - test, Another Test - Org";
            String b = "systemName=ORG3\nhumanName=Organisation Two\ntype=Organisation\ndescription=Organisation Two Description\npermissionAuthorities=Hello World - test, Another Test - Org";
            String c = "systemName=ORG4\nhumanName=Organisation One\ntype=Organisation\ndescription=Organisation Three Description\npermissionAuthorities=Hello World - test, Another Test - Org";
            List<String> spec = new ArrayList<String>(3);
            spec.add(a);
            spec.add(b);
            spec.add(c);
            new EnhancedSecurityConstraint().setGroupDetailsSpecification(spec);
        } catch (EnhancedSecurityException e) {
            return; // test success
        }
        fail("Defining the same human name twice in test specification should cause an exception, but it did not");
    }

    public void testSunnyDayPropertySet() {
        getValidEnhancedSecurityConstraint().validate();
    }

    public void testBadFilterMatchLogicCombination() {
        try {
            EnhancedSecurityConstraint constraint = getValidEnhancedSecurityConstraint();
            constraint.setFilterDisplay(false);
            constraint.setMatchLogic("AND");
            constraint.validate();
        } catch (EnhancedSecurityException e) {
            return;
        }
        fail("Expected to fail validation with MatchLogic AND and filterDisplay false but passed");
    }

    public void testNullPriority() {
        try {
            EnhancedSecurityConstraint constraint = getValidEnhancedSecurityConstraint();
            constraint.setDisplayPriority(null);
            constraint.validate();
        } catch (EnhancedSecurityException e) {
            return;
        }
        fail("Expected to fail validation with a null display priority but passed");
    }

    public void testNullDescription() {
        try {
            EnhancedSecurityConstraint constraint = getValidEnhancedSecurityConstraintNullDescription();
            constraint.setDescription(null);
            constraint.validate();
        } catch (EnhancedSecurityException e) {
            return;
        }
        fail("Expected to fail validation with a null description but passed");
    }

    public void testEmptyDescription() {
        try {
            EnhancedSecurityConstraint constraint = getValidEnhancedSecurityConstraint();
            constraint.setDescription("");
            constraint.validate();
        } catch (EnhancedSecurityException e) {
            return;
        }
        fail("Expected to fail validation with an empty description but passed");
    }

    public void testWhitespaceOnlyDescription() {
        try {
            EnhancedSecurityConstraint constraint = getValidEnhancedSecurityConstraint();
            constraint.setDescription("       ");
            constraint.validate();
        } catch (EnhancedSecurityException e) {
            return;
        }
        fail("Expected to fail validation with an empty description but passed");
    }

    public void testGetDetailsForGroup() {
        String a = "systemName=ORG2\nhumanName=Organisation One\ntype=Organisation\ndescription=Organisation One Description\npermissionAuthorities=Hello World - test, Another Test - Org";
        String b = "systemName=ORG3\nhumanName=Organisation Two\ntype=Organisation\ndescription=Organisation Two Description\npermissionAuthorities=Hello World - test, Another Test - Org";
        String c = "systemName=ORG4\nhumanName=Organisation Three\ntype=Organisation\ndescription=Organisation Three Description\npermissionAuthorities=Hello World - test, Another Test - Org";
        List<String> spec = new ArrayList<String>(3);
        spec.add(a);
        spec.add(b);
        spec.add(c);
        EnhancedSecurityConstraint esc = new EnhancedSecurityConstraint();
        esc.setGroupDetailsSpecification(spec);
        GroupDetails gd = esc.getDetailsForGroup("ORG3");
        assertEquals("Organisation Two Description", gd.getDescription());
        assertEquals("Organisation Two", gd.getHumanName());
        assertEquals("ORG3", gd.getSystemName());
    }

    private EnhancedSecurityConstraint getValidEnhancedSecurityConstraint() {
        EnhancedSecurityConstraint constraint = new EnhancedSecurityConstraint() {
            public EnhancedSecurityConstraintLocator getLocator() {
                return new StubEnhancedSecurityConstraintLocator();
            }
        };
        constraint.setDescription("This is a description");
        constraint.setDisplayPriority("low");
        constraint.setFilterDisplay(true);
        constraint.setCaseSensitive(true);
        constraint.setMatchLogic("OR");

        String a = "systemName=ORG2\nhumanName=Organisation One\ntype=Organisation\ndescription=Organisation One Description\npermissionAuthorities=Hello World - test, Another Test - Org";
        String b = "systemName=ORG3\nhumanName=Organisation Two\ntype=Organisation\ndescription=Organisation Two Description\npermissionAuthorities=Hello World - test, Another Test - Org";
        String c = "systemName=ORG4\nhumanName=Organisation Three\ntype=Organisation\ndescription=Organisation Three Description\npermissionAuthorities=Hello World - test, Another Test - Org";
        List<String> spec = new ArrayList<String>(3);
        spec.add(a);
        spec.add(b);
        spec.add(c);

        constraint.setGroupDetailsSpecification(spec);
        return constraint;
    }

    private EnhancedSecurityConstraint getValidEnhancedSecurityConstraintNullDescription() {
        EnhancedSecurityConstraint constraint = new EnhancedSecurityConstraint() {
            public EnhancedSecurityConstraintLocator getLocator() {
                return new StubEnhancedSecurityConstraintLocator();
            }
        };
        constraint.setDescription(null);
        constraint.setDisplayPriority("low");
        constraint.setFilterDisplay(true);
        constraint.setCaseSensitive(true);
        constraint.setMatchLogic("OR");

        String a = "systemName=ORG2\nhumanName=Organisation One\ntype=Organisation\ndescription=Organisation One Description\npermissionAuthorities=Hello World - test, Another Test - Org";
        String b = "systemName=ORG3\nhumanName=Organisation Two\ntype=Organisation\ndescription=Organisation Two Description\npermissionAuthorities=Hello World - test, Another Test - Org";
        String c = "systemName=ORG4\nhumanName=Organisation Three\ntype=Organisation\ndescription=Organisation Three Description\npermissionAuthorities=Hello World - test, Another Test - Org";
        List<String> spec = new ArrayList<String>(3);
        spec.add(a);
        spec.add(b);
        spec.add(c);

        constraint.setGroupDetailsSpecification(spec);
        return constraint;
    }
}
