package com.surevine.alfresco.esl.test.stub;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.surevine.alfresco.esl.impl.EnhancedSecurityConstraint;
import com.surevine.alfresco.esl.impl.EnhancedSecurityConstraintLocator;

public class StubEnhancedSecurityConstraintLocator extends
		EnhancedSecurityConstraintLocator {

	@Override
    public Collection<EnhancedSecurityConstraint> getAllEnhancedSecurityConstraints(boolean b)
    {
    	EnhancedSecurityConstraint closed = new EnhancedSecurityConstraint();
    	closed.setShortName("es:closed");
    	closed.setDescription("Closed Groups");
    	closed.setDisplayPriority("high");
    	closed.setMatchLogic("AND");
    	closed.setFilterDisplay(true);
    	String c1 = "systemName=CLOSED1\n permissionAuthorities=user1-org1,user2-org2\n humanName=Closed 1\n type=Group\n description=Description One";
    	String c2 = "systemName=CLOSED2\n permissionAuthorities=user1-org1\n humanName=Closed 2\n type=Group\n description=Description Two";
    	String c3 = "systemName=CLOSED3\n permissionAuthorities=user1-org1\n humanName=Closed 3\n type=Group\n description=Description Three";
    	String c4 = "systemName=CLOSED4\n permissionAuthorities=user1-org1\n humanName=Closed 4\n type=Restriction\n description=Description Four";
    	String c5 = "systemName=CLOSED5\n permissionAuthorities=user1-org1\n humanName=Closed 5\n type=Restriction\n description=Description Five";
    	List<String> closedGroups = new ArrayList<String>(5);
    	closedGroups.add(c1);
    	closedGroups.add(c2);
    	closedGroups.add(c3);
    	closedGroups.add(c4);
    	closedGroups.add(c5);
    	closed.setGroupDetailsSpecification(closedGroups);
    	
    	EnhancedSecurityConstraint open = new EnhancedSecurityConstraint();
    	open.setShortName("es:open");
    	open.setDescription("Open Groups");
    	open.setDisplayPriority("low");
    	open.setMatchLogic("OR");
    	open.setFilterDisplay(false);
    	String o1 = "systemName=OPEN1\n permissionAuthorities=user1-org1\n humanName=Open 1\n type=Open\n description=Description One";
    	String o2 = "systemName=OPEN2\n permissionAuthorities=user1-org1\n humanName=Open 2\n type=Open\n description=Description Two";
    	String o3 = "systemName=OPEN3\n permissionAuthorities=user1-org1\n humanName=Open 3\n type=Open\n description=Description Three";
    	String o4 = "systemName=OPEN4\n permissionAuthorities=user1-org1\n humanName=Open 4\n type=Open\n description=Description Four";
    	String o5 = "systemName=OPEN5\n permissionAuthorities=user1-org1\n humanName=Open 5\n type=Open\n description=Description Five";
    	List<String> openGroups = new ArrayList<String>(5);
    	openGroups.add(o1);
    	openGroups.add(o2);
    	openGroups.add(o3);
    	openGroups.add(o4);
    	openGroups.add(o5);
    	open.setGroupDetailsSpecification(openGroups);
    	
    	EnhancedSecurityConstraint orgs = new EnhancedSecurityConstraint();
    	orgs.setShortName("es:org");
    	orgs.setDescription("Organisations");
    	orgs.setDisplayPriority("low");
    	orgs.setMatchLogic("OR");
    	orgs.setFilterDisplay(false);
    	String org1 = "systemName=ORG1\n permissionAuthorities=user1-org1\n humanName=Org 1\n type=Org\n description=Description One";
    	String org2 = "systemName=ORG2\n permissionAuthorities=user1-org1\n humanName=Org 2\n type=Org\n description=Description Two";
    	String org3 = "systemName=ORG3\n permissionAuthorities=user1-org1\n humanName=Org 3\n type=Org\n description=Description Three";
    	String org4 = "systemName=ORG4\n permissionAuthorities=user1-org1\n humanName=Org 4\n type=Org\n description=Description Four";
    	String org5 = "systemName=ORG5\n permissionAuthorities=user1-org1\n humanName=Org 5\n type=Org\n description=Description Five";
    	List<String> organisations = new ArrayList<String>(5);
    	organisations.add(org1);
    	organisations.add(org2);
    	organisations.add(org3);
    	organisations.add(org4);
    	organisations.add(org5);
    	orgs.setGroupDetailsSpecification(organisations);

    	List<EnhancedSecurityConstraint> rV = new ArrayList<EnhancedSecurityConstraint>(3);
    	rV.add(open);
    	rV.add(closed);
    	rV.add(orgs);
    	
    	return rV;
	}
}
