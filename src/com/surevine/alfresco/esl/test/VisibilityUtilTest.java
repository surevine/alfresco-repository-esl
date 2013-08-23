package com.surevine.alfresco.esl.test;

import java.util.Map;

import com.surevine.alfresco.esl.impl.webscript.visibility.VisibilityUtil;
import com.surevine.alfresco.esl.test.stub.StubWebScriptRequest;

import junit.framework.TestCase;

public class VisibilityUtilTest extends TestCase {

    public VisibilityUtil _fixture = new VisibilityUtil();

    public void testGetMarkingFromRequest() {

        // Get a webscript request with known values
        String inputString = "es_validOpenMarkings,OPEN01,OPEN02,OPEN03;es_validClosedMarkings,ATOMAL1,ATOMAL2,CLOSED01,CLOSED02;es_validOrganisations,ORG01,ORG02,ORG03";
        StubWebScriptRequest request = new StubWebScriptRequest();
        request.addParameter("marking", inputString);

        // Convert to object representation
        Map<String, String[]> results = _fixture.getMarkingFromRequest(request);

        // Confirm correct number of results
        assertEquals(3, results.size());

        // Validate open markings
        String[] openMarkings = results.get("es:validOpenMarkings");
        assertNotNull(openMarkings);
        assertEquals(3, openMarkings.length);
        assertEquals("OPEN01", openMarkings[0]);
        assertEquals("OPEN02", openMarkings[1]);
        assertEquals("OPEN03", openMarkings[2]);

        // Validate closed markings
        String[] closedMarkings = results.get("es:validClosedMarkings");
        assertNotNull(closedMarkings);
        assertEquals(4, closedMarkings.length);
        assertEquals("ATOMAL1", closedMarkings[0]);
        assertEquals("ATOMAL2", closedMarkings[1]);
        assertEquals("CLOSED01", closedMarkings[2]);
        assertEquals("CLOSED02", closedMarkings[3]);

        // Validate organisations
        String[] organisations = results.get("es:validOrganisations");
        assertNotNull(organisations);
        assertEquals(3, organisations.length);
        assertEquals("ORG01", organisations[0]);
        assertEquals("ORG02", organisations[1]);
        assertEquals("ORG03", organisations[2]);

    }

}
