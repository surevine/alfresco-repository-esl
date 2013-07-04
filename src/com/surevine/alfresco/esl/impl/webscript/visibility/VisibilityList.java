/**
 * 
 */
package com.surevine.alfresco.esl.impl.webscript.visibility;

import java.io.IOException;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import org.alfresco.model.ContentModel;
import org.alfresco.module.org_alfresco_module_dod5015.caveat.RMCaveatConfigService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.security.PersonService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;

/**
 * @author richardm
 */
public class VisibilityList extends AbstractWebScript {

	private static final Log LOG = LogFactory.getLog(VisibilityList.class);
	
	private PersonService personService;
	private NodeService nodeService;
	private RMCaveatConfigService caveatConfigService;
	
	public void setPersonService(final PersonService personService) {
		this.personService = personService;
	}
	
	public void setNodeService(final NodeService nodeService) {
		this.nodeService = nodeService;
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
		
		Collections.sort(whoCanSee);
		
		final JSONObject result = new JSONObject();
		final JSONArray authoritiesAllowedAccess = new JSONArray();
		
		for (final String userName : whoCanSee) {
			
			// Omit superUser from list
			if(!userName.equalsIgnoreCase("superUser")) {
				
				// Only return results about authorities who are people
				// This will also filter out any people listed in caveatconfig.json who don't actually exist in the system
				final NodeRef person = personService.getPerson(userName);
				
				if (person != null) {
					try {
						
						final JSONObject authorityObj = new JSONObject();
						
						final Serializable organisation = nodeService.getProperty(person, ContentModel.PROP_ORGANIZATION);
						
						authorityObj.put("sid", userName);
						authorityObj.put("fullName", String.format("%s %s",
								nodeService.getProperty(person, ContentModel.PROP_FIRSTNAME),
								nodeService.getProperty(person, ContentModel.PROP_LASTNAME)));
						authorityObj.put("org", organisation == null ? "" : String.valueOf(organisation).toUpperCase());
						
						authoritiesAllowedAccess.put(authorityObj);
						
					} catch (final JSONException e) {
						LOG.error("Need to handle this properly.", e);
					}
				}
				
			}

		}

		try {
			result.put("authoritiesAllowedAccess", authoritiesAllowedAccess);
		} catch (final JSONException e) {
			LOG.error("Need to handle this properly.", e);
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
		response.getWriter().write(result.toString());
		response.getWriter().flush();
	}
}
