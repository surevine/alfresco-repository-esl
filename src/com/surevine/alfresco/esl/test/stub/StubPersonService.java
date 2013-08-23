package com.surevine.alfresco.esl.test.stub;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.security.PersonService;
import org.alfresco.service.namespace.QName;

/**
 * Really simple stub that just exists to confirm that all prospective people are valid
 * 
 * @author simonw
 * 
 */
public class StubPersonService implements PersonService {

    @Override
    public NodeRef getPerson(String userName) {
        return new NodeRef("workspace://SpacesStore/112435675");
    }

    // Everything after this line is auto-generated nulls

    @Override
    public NodeRef getPerson(String userName, boolean autoCreateHomeFolderAndMissingPersonIfAllowed) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean personExists(String userName) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean createMissingPeople() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void setCreateMissingPeople(boolean createMissing) {
        // TODO Auto-generated method stub

    }

    @Override
    public Set<QName> getMutableProperties() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setPersonProperties(String userName, Map<QName, Serializable> properties) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setPersonProperties(String userName, Map<QName, Serializable> properties, boolean autoCreateHomeFolder) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean isMutable() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public NodeRef createPerson(Map<QName, Serializable> properties) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public NodeRef createPerson(Map<QName, Serializable> properties, Set<String> zones) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void deletePerson(String userName) {
        // TODO Auto-generated method stub

    }

    @Override
    public void deletePerson(NodeRef personRef) {
        // TODO Auto-generated method stub

    }

    @Override
    public Set<NodeRef> getAllPeople() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Set<NodeRef> getPeopleFilteredByProperty(QName propertyKey, Serializable propertyValue) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public NodeRef getPeopleContainer() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean getUserNamesAreCaseSensitive() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public String getUserIdentifier(String caseSensitiveUserName) {
        // TODO Auto-generated method stub
        return null;
    }

}
