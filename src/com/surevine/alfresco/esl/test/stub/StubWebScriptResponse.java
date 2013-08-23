package com.surevine.alfresco.esl.test.stub;

import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.Writer;

import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.Runtime;
import org.springframework.extensions.webscripts.WebScriptResponse;

/**
 * A webscript response we can use for testing purposes.  Currently, only the getWriter() mehtod is functional.  
 * This writes to a local String so the data written can be retrieved with a call to getData(); 
 * @author simonw
 *
 */
public class StubWebScriptResponse implements WebScriptResponse {

	private StringWriter _dataBuilder = new StringWriter();
	
	public String getData() {
		return _dataBuilder.toString();
	}
	
	@Override
	public Writer getWriter() throws IOException {
		return _dataBuilder;
	}
	
	
	//Everything after this line is auto-generated and non-functional
	
	@Override
	public void addHeader(String arg0, String arg1) {
		// TODO Auto-generated method stub
	}

	@Override
	public String encodeResourceUrl(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String encodeScriptUrl(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getEncodeResourceUrlFunction(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getEncodeScriptUrlFunction(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OutputStream getOutputStream() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Runtime getRuntime() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub

	}

	@Override
	public void setCache(Cache arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setContentEncoding(String arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setContentType(String arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setHeader(String arg0, String arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setStatus(int arg0) {
		// TODO Auto-generated method stub

	}

}
