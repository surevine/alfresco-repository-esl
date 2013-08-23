package com.surevine.alfresco.esl.test.stub;

import java.util.Set;

import org.alfresco.repo.security.authentication.AuthenticationException;
import org.alfresco.service.cmr.security.AuthenticationService;

/**
 * Simple stub allowing us to set the current user name
 * @author simonw
 *
 */
public class StubAuthenticationService implements AuthenticationService {
	
	private String _currentUser;
	
	public StubAuthenticationService(String userName) {
		_currentUser=userName;
	}
	
	@Override
	public String getCurrentUserName() throws AuthenticationException {
		return _currentUser;
	}

	//Everything after here is auto-generated nulls
	
	@Override
	public boolean getAuthenticationEnabled(String userName)
			throws AuthenticationException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void authenticate(String userName, char[] password)
			throws AuthenticationException {
		// TODO Auto-generated method stub

	}

	@Override
	public void authenticateAsGuest() throws AuthenticationException {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean guestUserAuthenticationAllowed() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean authenticationExists(String userName) {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public void invalidateUserSession(String userName)
			throws AuthenticationException {
		// TODO Auto-generated method stub

	}

	@Override
	public void invalidateTicket(String ticket) throws AuthenticationException {
		// TODO Auto-generated method stub

	}

	@Override
	public void validate(String ticket) throws AuthenticationException {
		// TODO Auto-generated method stub

	}

	@Override
	public String getCurrentTicket() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getNewTicket() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void clearCurrentSecurityContext() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isCurrentUserTheSystemUser() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Set<String> getDomains() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<String> getDomainsThatAllowUserCreation() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<String> getDomainsThatAllowUserDeletion() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<String> getDomiansThatAllowUserPasswordChanges() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<String> getDefaultAdministratorUserNames() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<String> getDefaultGuestUserNames() {
		// TODO Auto-generated method stub
		return null;
	}

}
