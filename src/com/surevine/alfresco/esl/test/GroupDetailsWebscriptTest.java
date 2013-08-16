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

import com.surevine.alfresco.esl.impl.GroupDetailsWebscript;
import com.surevine.alfresco.esl.test.stub.CaveatServiceStub;
import com.surevine.alfresco.esl.test.stub.StubbedModelGroupDetailsWebscript;

import junit.framework.TestCase;


/**
 * This test feels a little like cheating but when I think about it it seems to work out - the model stuff is not within scope of a unit, 
 * all the lower level stuff is tested elsewhere, which just leaves the json formatting, which doesn't vary with input.  So here we are
 * @author simonw
 *
 */
public class GroupDetailsWebscriptTest extends TestCase {
	
	private GroupDetailsWebscript ws; 
	
	public void setUp()
	{
		ws= new StubbedModelGroupDetailsWebscript();
		ws.setCaveatConfigService(new CaveatServiceStub("CLOSED1,CLOSED2,OPEN1,OPEN2,ORG1,ORG2"));
	}
	
	public void testJSONRendition()
	{
		assertEquals(ws.getJSONResponseString(), "{\"constraints\":[{\"constraintDescription\":\"Open Groups\",\"constraintName\":\"es_open\",\"displayPriority\":\"Low\",\"markings\":[{\"hasAccess\":true,\"description\":\"Description One\",\"name\":\"OPEN1\",\"longName\":\"Open 1\",\"permissionAuthorities\":[\"user1 - org1\"],\"type\":\"Open\"},{\"hasAccess\":true,\"description\":\"Description Two\",\"name\":\"OPEN2\",\"longName\":\"Open 2\",\"permissionAuthorities\":[\"user1 - org1\"],\"type\":\"Open\"},{\"hasAccess\":false,\"description\":\"Description Three\",\"name\":\"OPEN3\",\"longName\":\"Open 3\",\"permissionAuthorities\":[\"user1 - org1\"],\"type\":\"Open\"},{\"hasAccess\":false,\"description\":\"Description Four\",\"name\":\"OPEN4\",\"longName\":\"Open 4\",\"permissionAuthorities\":[\"user1 - org1\"],\"type\":\"Open\"},{\"hasAccess\":false,\"description\":\"Description Five\",\"name\":\"OPEN5\",\"longName\":\"Open 5\",\"permissionAuthorities\":[\"user1 - org1\"],\"type\":\"Open\"}]},{\"constraintDescription\":\"Closed Groups\",\"constraintName\":\"es_closed\",\"displayPriority\":\"High\",\"markings\":[{\"hasAccess\":true,\"description\":\"Description One\",\"name\":\"CLOSED1\",\"longName\":\"Closed 1\",\"permissionAuthorities\":[\"user1 - org1\",\"user2 - org2\"],\"type\":\"Group\"},{\"hasAccess\":true,\"description\":\"Description Two\",\"name\":\"CLOSED2\",\"longName\":\"Closed 2\",\"permissionAuthorities\":[\"user1 - org1\"],\"type\":\"Group\"}]},{\"constraintDescription\":\"Organisations\",\"constraintName\":\"es_org\",\"displayPriority\":\"Low\",\"markings\":[{\"hasAccess\":true,\"description\":\"Description One\",\"name\":\"ORG1\",\"longName\":\"Org 1\",\"permissionAuthorities\":[\"user1 - org1\"],\"type\":\"Org\"},{\"hasAccess\":true,\"description\":\"Description Two\",\"name\":\"ORG2\",\"longName\":\"Org 2\",\"permissionAuthorities\":[\"user1 - org1\"],\"type\":\"Org\"},{\"hasAccess\":false,\"description\":\"Description Three\",\"name\":\"ORG3\",\"longName\":\"Org 3\",\"permissionAuthorities\":[\"user1 - org1\"],\"type\":\"Org\"},{\"hasAccess\":false,\"description\":\"Description Four\",\"name\":\"ORG4\",\"longName\":\"Org 4\",\"permissionAuthorities\":[\"user1 - org1\"],\"type\":\"Org\"},{\"hasAccess\":false,\"description\":\"Description Five\",\"name\":\"ORG5\",\"longName\":\"Org 5\",\"permissionAuthorities\":[\"user1 - org1\"],\"type\":\"Org\"}]}]}");
	}

}
