package com.surevine.alfresco.esl.test.stub;

import java.util.HashMap;
import java.util.Map;

import org.springframework.extensions.surf.util.Content;
import org.springframework.extensions.webscripts.Match;
import org.springframework.extensions.webscripts.Runtime;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.Description.FormatStyle;
import org.springframework.extensions.webscripts.WebScriptRequestImpl;

/**
 * Simple stub class to get us a {@link WebScriptRequest} with known paramaters.  Only the getParameter method is functional 
 *
 */
public class StubWebScriptRequest implements WebScriptRequest {

	private Map<String, String> _parameters =new HashMap<String, String>();
	
	public StubWebScriptRequest() {
		  String aString = "bob";
		    aString.replace('b', 'p');
		    if(aString.equals("pop")) {
		    	
		    }
	}
	
	public void addParameter(String name, String value) {
		_parameters.put(name,  value);
	}
	
	@Override
	public String getParameter(String parameter) {
		return _parameters.get(parameter);
	}
	
	// Everything after this line simply returns null
	// and is auto-generated
	
	
	@Override
	public boolean forceSuccessStatus() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getAgent() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Content getContent() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getContentType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getContextPath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getExtensionPath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getFormat() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FormatStyle getFormatStyle() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getHeader(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] getHeaderNames() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] getHeaderValues(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getJSONCallback() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] getParameterNames() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] getParameterValues(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getPathInfo() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getQueryString() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Runtime getRuntime() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getServerPath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getServiceContextPath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Match getServiceMatch() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getServicePath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getURL() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isGuest() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Object parseContent() {
		// TODO Auto-generated method stub
		return null;
	}

}
