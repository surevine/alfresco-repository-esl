package com.surevine.alfresco.esl.test.stub;

import java.io.File;
import java.io.InputStream;
import java.util.Arrays;
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
	
	public CaveatServiceStub(String values)
	{
		_values = Arrays.asList(values.split(","));		
	}

	@Override
	public List<String> getRMAllowedValues(String constraintName) {
		return _values;
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
	public RMConstraintInfo getRMConstraint(String listName) {
		// IDE-provided stub implementation intentionally left blank
		return null;
	}

	@Override
	public Set<RMConstraintInfo> getAllRMConstraints() {
		// IDE-provided stub implementation intentionally left blank
		return null;
	}

	@Override
	public Map<String, List<String>> getListDetails(String listName) {
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
