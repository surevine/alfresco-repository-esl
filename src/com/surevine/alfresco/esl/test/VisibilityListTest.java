package com.surevine.alfresco.esl.test;

import java.io.IOException;

import junit.framework.TestCase;

import com.surevine.alfresco.esl.impl.webscript.visibility.VisibilityList;
import com.surevine.alfresco.esl.impl.webscript.visibility.VisibilityUtil;
import com.surevine.alfresco.esl.test.stub.CaveatServiceStub;
import com.surevine.alfresco.esl.test.stub.StubNodeServiceGetPropertyStub;
import com.surevine.alfresco.esl.test.stub.StubPersonService;
import com.surevine.alfresco.esl.test.stub.StubWebScriptRequest;
import com.surevine.alfresco.esl.test.stub.StubWebScriptResponse;

public class VisibilityListTest extends TestCase {

	protected VisibilityList _fixture = new VisibilityList();
	protected CaveatServiceStub _caveatService = new CaveatServiceStub(("CLOSED1,CLOSED2,OPEN1,OPEN2,ORG1,ORG2"));
	
	
	public VisibilityListTest() {
		VisibilityUtil visibilityUtil = new VisibilityUtil();
		_fixture.setVisibilityUtil(visibilityUtil);
		_fixture.setCaveatConfigService(_caveatService);
		_fixture.setPersonService(new StubPersonService());
		_fixture.setNodeService(new StubNodeServiceGetPropertyStub());
		
		_caveatService.addConstraint("es:validOpenMarkings");
		_caveatService.addUserToValue("es:validOpenMarkings", "OPEN01", "user1");
		_caveatService.addUserToValue("es:validOpenMarkings", "OPEN02", "user2");
		
		_caveatService.addConstraint("es:validClosedMarkings");
		_caveatService.addUserToValue("es:validClosedMarkings", "ATOMAL1", "user1");
		_caveatService.addUserToValue("es:validClosedMarkings", "ATOMAL2", "user1");
		_caveatService.addUserToValue("es:validClosedMarkings", "CLOSED01", "user1");
		_caveatService.addUserToValue("es:validClosedMarkings", "CLOSED02", "user1");
		_caveatService.addUserToValue("es:validClosedMarkings", "ATOMAL1", "user2");
		_caveatService.addUserToValue("es:validClosedMarkings", "ATOMAL2", "user2");
		_caveatService.addUserToValue("es:validClosedMarkings", "CLOSED01", "user2");
		_caveatService.addUserToValue("es:validClosedMarkings", "CLOSED02", "user2");
		
		_caveatService.addConstraint("es:validOrganisations");
		_caveatService.addUserToValue("es:validOrganisations", "ORG01", "user1");
		_caveatService.addUserToValue("es:validOrganisations", "ORG01", "user2");

	}
	
	
	public void testExecution() throws IOException {
		
		String marking="es_validOpenMarkings,OPEN01,OPEN02,OPEN03;es_validClosedMarkings,ATOMAL1,ATOMAL2,CLOSED01,CLOSED02;es_validOrganisations,ORG01,ORG02,ORG03";		
		StubWebScriptRequest request = new StubWebScriptRequest();
		request.addParameter("marking", marking);

		StubWebScriptResponse response = new StubWebScriptResponse();
		_fixture.execute(request, response);
		assertEquals("{\"authoritiesAllowedAccess\":[{\"sid\":\"user1\",\"fullName\":\"firstName lastName\",\"org\":\"ORGANIZATION\"},{\"sid\":\"user2\",\"fullName\":\"firstName lastName\",\"org\":\"ORGANIZATION\"}]}", response.getData());
	}
	
}
