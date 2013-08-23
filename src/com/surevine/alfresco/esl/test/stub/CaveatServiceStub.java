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
package com.surevine.alfresco.esl.test.stub;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.alfresco.module.org_alfresco_module_dod5015.caveat.RMCaveatConfigService;
import org.alfresco.module.org_alfresco_module_dod5015.caveat.RMConstraintInfo;
import org.alfresco.service.cmr.repository.NodeRef;

/**
 * Stub implementation of RMCaveatConfigService to simulate the getRMAllowedValues method call
 * @author simonw
 *
 */
public class CaveatServiceStub implements RMCaveatConfigService {

	private List<String> _values;
	private Map<String, Map<String, List<String>>> _constraints = new HashMap<String, Map<String,List<String>>>();
	
	public CaveatServiceStub(String values)
	{
		_values = Arrays.asList(values.split(","));		
	}

	@Override
	public List<String> getRMAllowedValues(String constraintName) {
		return _values;
	}
	
	@Override
	public RMConstraintInfo getRMConstraint(String listName) {
		RMConstraintInfo rV = new RMConstraintInfo();
		rV.setName(listName);
		rV.setTitle("Test of "+listName);
		rV.setCaseSensitive(true);
		if (_constraints.get(listName)!=null) {
			rV.setAllowedValues(_constraints.get(listName).keySet().toArray(new String[1]));
		}
		return rV;
	}
	
	public void addConstraint(String name) {
		_constraints.put(name, new HashMap<String, List<String>>());
	}
	
	public void addUserToValue(String constraint, String value, String user) {
		List<String> existingValues = _constraints.get(constraint).get(user);
		if (existingValues==null) {
			existingValues= new ArrayList<String>();
		}
		existingValues.add(value);
		_constraints.get(constraint).put(user, existingValues);
	}
	
	@Override
	public Map<String, List<String>> getListDetails(String listName) {
		return _constraints.get(listName);
	}

	
	//The rest of this class is all blank implementations of the interface
	
	@Override
	public boolean hasAccess(NodeRef nodeRef) {
		// IDE-provided stub implementation intentionally left blank
		return false;
	}

	@Override
	public void init() {
		// IDE-provided stub implementation intentionally left blank

	}

	@Override
	public Set<RMConstraintInfo> getAllRMConstraints() {
		// IDE-provided stub implementation intentionally left blank
		return null;
	}

	@Override
	public NodeRef updateOrCreateCaveatConfig(File jsonFile) {
		// IDE-provided stub implementation intentionally left blank
		return null;
	}

	@Override
	public NodeRef updateOrCreateCaveatConfig(String jsonString) {
		// IDE-provided stub implementation intentionally left blank
		return null;
	}

	@Override
	public NodeRef updateOrCreateCaveatConfig(InputStream is) {
		// IDE-provided stub implementation intentionally left blank
		return null;
	}

	@Override
	public RMConstraintInfo addRMConstraint(String listName, String listTitle,
			String[] allowedValues) {
		// IDE-provided stub implementation intentionally left blank
		return null;
	}

	@Override
	public RMConstraintInfo updateRMConstraintAllowedValues(String listName,
			String[] allowedValues) {
		// IDE-provided stub implementation intentionally left blank
		return null;
	}

	@Override
	public RMConstraintInfo updateRMConstraintTitle(String listName,
			String newTitle) {
		// IDE-provided stub implementation intentionally left blank
		return null;
	}

	@Override
	public void deleteRMConstraint(String listName) {
		// IDE-provided stub implementation intentionally left blank

	}

	@Override
	public void addRMConstraintListValue(String listName, String authorityName,
			String value) {
		// IDE-provided stub implementation intentionally left blank

	}

	@Override
	public void updateRMConstraintListAuthority(String listName,
			String authorityName, List<String> values) {
		// IDE-provided stub implementation intentionally left blank

	}

	@Override
	public void removeRMConstraintListAuthority(String listName,
			String authorityName) {
		// IDE-provided stub implementation intentionally left blank

	}

	@Override
	public void updateRMConstraintListValue(String listName, String value,
			List<String> authorities) {
		// IDE-provided stub implementation intentionally left blank

	}

	@Override
	public void removeRMConstraintListValue(String listName, String valueName) {
		// IDE-provided stub implementation intentionally left blank

	}

}
