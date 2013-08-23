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
package com.surevine.alfresco.esl.impl.webscript.visibility;

import java.io.IOException;

import org.alfresco.model.ContentModel;
import org.alfresco.module.org_alfresco_module_dod5015.caveat.RMCaveatConfigService;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.security.authentication.AuthenticationUtil.RunAsWork;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.security.AuthenticationService;
import org.alfresco.service.cmr.security.PersonService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;

import com.surevine.alfresco.presence.Presence;
import com.surevine.alfresco.presence.PresenceService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeSet;

public class SharedVisibilityReport extends AbstractWebScript {

    private static final Log _logger = LogFactory.getLog(SharedVisibilityReport.class);

    private String _allGroupsUser = "admin";

    public void setAllGroupsUser(String userName) {
        _allGroupsUser = userName;
    }

    private RMCaveatConfigService _caveatConfigService;

    public void setCaveatConfigService(final RMCaveatConfigService caveatConfigService) {
        _caveatConfigService = caveatConfigService;
    }

    private PersonService _personService;

    public void setPersonService(final PersonService personService) {
        _personService = personService;
    }

    private PresenceService _presenceService;

    public void setPresenceService(final PresenceService presenceService) {
        _presenceService = presenceService;
    }

    private NodeService _nodeService;

    public void setNodeService(final NodeService nodeService) {
        _nodeService = nodeService;
    }

    private AuthenticationService _authenticationService;

    public void setAuthenticationService(final AuthenticationService authenticationService) {
        _authenticationService = authenticationService;
    }

    private VisibilityUtil _visibilityUtil;

    public void setVisibilityUtil(VisibilityUtil util) {
        _visibilityUtil = util;
    }

    private String _constraintName;

    public void setGroupName(final String constraintName) {
        _constraintName = constraintName;
    }

    private String _displayName;

    public void setDisplayName(final String displayName) {
        _displayName = displayName;
    }

    private Collection<String> _usersWithNoGroups = null;
    private Date _allUserNamesNextUpdate = new Date(0l);
    private static final long MILLIS_IN_HOUR = 1000l * 60l * 60l * 60l;

    protected Collection<String> getUsersWithNoGroups() {
        if (_logger.isDebugEnabled()) {
            _logger.debug("Cache expires on " + _allUserNamesNextUpdate);
        }
        if (new Date().after(_allUserNamesNextUpdate)) {
            if (_logger.isDebugEnabled()) {
                _logger.debug("Refreshing users with no groups cache");
            }
            Collection<String> newAllNames = new ArrayList<String>(2000);
            Iterator<NodeRef> peopleNodes = _personService.getAllPeople().iterator();
            while (peopleNodes.hasNext()) {
                NodeRef nr = peopleNodes.next();
                String userName = _nodeService.getProperty(nr, ContentModel.PROP_USERNAME).toString();
                if (_logger.isDebugEnabled()) {
                    _logger.debug("  Adding " + userName + " as a user with no groups");
                }
                newAllNames.add(userName);
            }

            Collection<String> usersWithAGroup = AuthenticationUtil.runAs(new GetUsersWithAGroupWork(), _allGroupsUser);
            newAllNames.removeAll(usersWithAGroup);

            _usersWithNoGroups = newAllNames;
            _allUserNamesNextUpdate = new Date(new Date().getTime() + MILLIS_IN_HOUR);
            if (_logger.isDebugEnabled()) {
                _logger.debug("Users with no groups cache updated");
            }
        }
        return _usersWithNoGroups;
    }

    private class GetUsersWithAGroupWork implements RunAsWork<Collection<String>> {

        @Override
        public Collection<String> doWork() throws Exception {
            Iterator<String> groupNames = _visibilityUtil.getGroupsForCurrentUser(_caveatConfigService, _constraintName, _displayName).iterator();

            Collection<String> usersInAnyGroup = new HashSet<String>(1000);
            while (groupNames.hasNext()) {
                String groupName = groupNames.next();
                Collection<Presence> usersWithAccessToThisGroup = new ArrayList<Presence>();
                usersWithAccessToThisGroup.addAll(_visibilityUtil.whoHasAccessToGroup(_caveatConfigService, _constraintName, groupName));
                Iterator<Presence> i = usersWithAccessToThisGroup.iterator();
                while (i.hasNext()) {
                    usersInAnyGroup.add(i.next().getUserName());
                }
            }
            return usersInAnyGroup;

        }
    }

    @Override
    public void execute(WebScriptRequest request, WebScriptResponse response) throws IOException {
        final String currentUserName = _authenticationService.getCurrentUserName();

        if (_logger.isDebugEnabled()) {
            _logger.debug("Gettng visibility report for " + currentUserName);
        }
        Set<Presence> usersInAnyGroup = new TreeSet<Presence>(new OnlineFirstComparator());
        Map<String, Collection<Presence>> report = new HashMap<String, Collection<Presence>>();
        Iterator<String> groupNames = _visibilityUtil.getGroupsForCurrentUser(_caveatConfigService, _constraintName, _displayName).iterator();
        if (_logger.isDebugEnabled()) {
            _logger.debug("Groups retrieved for " + currentUserName);
        }
        boolean userHasGroups = false;
        while (groupNames.hasNext()) {
            userHasGroups = true;
            String groupName = groupNames.next();
            if (_logger.isDebugEnabled()) {
                _logger.debug("  Adding " + groupName + " to shared visibility report");
            }
            Collection<Presence> usersWithAccessToThisGroup = new TreeSet<Presence>(new OnlineFirstComparator());
            usersWithAccessToThisGroup.addAll(_visibilityUtil.whoHasAccessToGroup(_caveatConfigService, _constraintName, groupName));
            usersInAnyGroup.addAll(usersWithAccessToThisGroup);
            report.put(groupName, stripUsersFromPresence(currentUserName, usersWithAccessToThisGroup));
        }
        if (userHasGroups) {
            report.put("Any of my Groups", stripUsersFromPresence(currentUserName, usersInAnyGroup));
        } else {
            if (_logger.isDebugEnabled()) {
                _logger.debug("  User does not have any groups");
            }
            Iterator<String> usersWithNoGroups = getUsersWithNoGroups().iterator();
            Set<Presence> usersInNoGroupsWithPresence = new TreeSet<Presence>(new OnlineFirstComparator());
            if (_logger.isDebugEnabled()) {
                _logger.debug("  Adding " + getUsersWithNoGroups() + " users with no groups to report");
            }
            while (usersWithNoGroups.hasNext()) {
                String userName = usersWithNoGroups.next();
                if (_logger.isDebugEnabled()) {
                    _logger.debug("    Adding " + userName + " as a user with no groups");
                }
                usersInNoGroupsWithPresence.add(_presenceService.getUserPresence(userName, false));
            }
            report.put("Users with no security groups", stripUsersFromPresence(currentUserName, usersInNoGroupsWithPresence));
        }

        JSONObject json = new JSONObject(report);

        Cache cache = new Cache();
        cache.setNeverCache(false);
        cache.setIsPublic(false);
        cache.setMaxAge(900L);
        cache.setMustRevalidate(false);
        cache.setETag("100");

        response.setCache(cache);

        response.setStatus(200);
        response.setContentType("application/json");
        response.getWriter().write(json.toString());
        response.getWriter().flush();
    }

    private Collection<Presence> stripUsersFromPresence(final String currentUserName, final Collection<Presence> presenceCollection) {

        // Strip current user
        for (final Presence presence : presenceCollection) {
            if (presence.getUserName().equals(currentUserName)) {
                presenceCollection.remove(presence);
                break; // Surely we'll only hit one instance?
            }
        }
        // Strip superUser
        for (final Presence presence : presenceCollection) {
            if (presence.getUserName().equals("superUser")) {
                presenceCollection.remove(presence);
                break; // Surely we'll only hit one instance?
            }
        }

        return presenceCollection;
    }

    private class OnlineFirstComparator implements Comparator<Presence> {

        @Override
        public int compare(Presence p1, Presence p2) {
            int presenceVal = 0;
            if (p1.getAvailability() != null && p2.getAvailability() != null) {
                presenceVal = p1.getAvailability().compareTo(p2.getAvailability());
                if (_logger.isDebugEnabled()) {
                    _logger.debug("Comparing " + p1 + "[" + p1.getAvailability() + "] with " + p2 + "[" + p2.getAvailability() + "]  -  Result: " + presenceVal);
                }
            }
            // If presences are equal, use full names
            if (presenceVal == 0) {
                presenceVal = p1.getFullUserName(_personService, _nodeService).compareToIgnoreCase(p2.getFullUserName(_personService, _nodeService));
            }
            return presenceVal;
        }
    }
}
