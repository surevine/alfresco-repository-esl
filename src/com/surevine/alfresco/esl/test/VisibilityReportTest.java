package com.surevine.alfresco.esl.test;

import java.io.IOException;

import junit.framework.TestCase;

import com.surevine.alfresco.esl.impl.webscript.visibility.SharedVisibilityReport;
import com.surevine.alfresco.esl.impl.webscript.visibility.VisibilityUtil;
import com.surevine.alfresco.esl.test.stub.CaveatServiceStub;
import com.surevine.alfresco.esl.test.stub.StubAuthenticationService;
import com.surevine.alfresco.esl.test.stub.StubNodeServiceGetPropertyStub;
import com.surevine.alfresco.esl.test.stub.StubPersonService;
import com.surevine.alfresco.esl.test.stub.StubWebScriptRequest;
import com.surevine.alfresco.esl.test.stub.StubWebScriptResponse;

public class VisibilityReportTest extends TestCase {

	protected SharedVisibilityReport _fixture = new SharedVisibilityReport();
	protected CaveatServiceStub _caveatService = new CaveatServiceStub(("CLOSED01,CLOSED02,OPEN01,OPEN02,ORG01,ORG02"));
	
	
	public VisibilityReportTest() {
		VisibilityUtil visibilityUtil = new VisibilityUtil();
		_fixture.setVisibilityUtil(visibilityUtil);
		_fixture.setCaveatConfigService(_caveatService);
		_fixture.setPersonService(new StubPersonService());
		_fixture.setNodeService(new StubNodeServiceGetPropertyStub());
		_fixture.setAuthenticationService(new StubAuthenticationService("user3"));
		_fixture.setGroupName("es:validClosedMarkings");
		
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
		
		String marking="es_validClosedMarkings,ATOMAL1,ATOMAL2,CLOSED01,CLOSED02";		
		StubWebScriptRequest request = new StubWebScriptRequest();
		request.addParameter("marking", marking);

		StubWebScriptResponse response = new StubWebScriptResponse();
		_fixture.execute(request, response);
	}
	
}
