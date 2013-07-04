/**
 * 
 */
package com.surevine.alfresco.esl.impl.webscript.visibility;

import java.io.IOException;
import java.util.List;

import org.alfresco.module.org_alfresco_module_dod5015.caveat.RMCaveatConfigService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.security.PersonService;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;

/**
 * @author richardm
 */
public class VisibilityCount extends AbstractWebScript {

	private PersonService personService;	
	private RMCaveatConfigService caveatConfigService;
	
	public void setPersonService(final PersonService personService) {
		this.personService = personService;
	}	
	
	public void setCaveatConfigService(final RMCaveatConfigService caveatConfigService) {
		this.caveatConfigService = caveatConfigService;
	}
	
	private VisibilityUtil _visibilityUtil;
	public void setVisibilityUtil(VisibilityUtil util) {
		_visibilityUtil=util;
	}
	
	/**
	 * 
	 */
	@Override
	public void execute(final WebScriptRequest request, final WebScriptResponse response)
			throws IOException {
		final List<String> whoCanSee = _visibilityUtil.whoCanSeeMarking(caveatConfigService, _visibilityUtil.getMarkingFromRequest(request));
		
		int count = 0;
		
		for (final String userName : whoCanSee) {
			
			// Omit superUser from count
			if(!userName.equalsIgnoreCase("superUser")) {
				
				// Only return results about authorities who are people
				// This will also filter out any people listed in caveatconfig.json who don't actually exist in the system
				final NodeRef person = personService.getPerson(userName);
				
				if (person != null) {
					count++;
				}
				
			}
			
		}

		Cache cache = new Cache();
		cache.setNeverCache(false);
		cache.setIsPublic(false);
		cache.setMaxAge(900L);
		cache.setMustRevalidate(false);
		cache.setETag("100");

		response.setCache(cache);

		response.setStatus(200);
		response.setContentType("application/json");
		response.getWriter().write(String.format("{\"result\": %d}", count));
		response.getWriter().flush();
	}
}
