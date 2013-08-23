package com.surevine.alfresco.esl.test.stub;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.alfresco.service.cmr.dictionary.InvalidAspectException;
import org.alfresco.service.cmr.dictionary.InvalidTypeException;
import org.alfresco.service.cmr.repository.AssociationExistsException;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.InvalidChildAssociationRefException;
import org.alfresco.service.cmr.repository.InvalidNodeRefException;
import org.alfresco.service.cmr.repository.InvalidStoreRefException;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.Path;
import org.alfresco.service.cmr.repository.StoreExistsException;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.repository.NodeRef.Status;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.namespace.QNamePattern;

/**
 * Very simple stub that stubs getProperty simply by returning the name of the property back
 * @author simonw
 *
 */
public class StubNodeServiceGetPropertyStub implements NodeService {

	@Override
	public Serializable getProperty(NodeRef nodeRef, QName qname) throws InvalidNodeRefException {
		return qname.getLocalName();
	}
	
	//Everything after this line is IDE-generated nulls
	
	@Override
	public void addAspect(NodeRef arg0, QName arg1,
			Map<QName, Serializable> arg2) throws InvalidNodeRefException,
			InvalidAspectException {
		// TODO Auto-generated method stub

	}

	@Override
	public ChildAssociationRef addChild(NodeRef arg0, NodeRef arg1, QName arg2,
			QName arg3) throws InvalidNodeRefException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ChildAssociationRef> addChild(Collection<NodeRef> arg0,
			NodeRef arg1, QName arg2, QName arg3)
			throws InvalidNodeRefException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addProperties(NodeRef arg0, Map<QName, Serializable> arg1)
			throws InvalidNodeRefException {
		// TODO Auto-generated method stub

	}

	@Override
	public AssociationRef createAssociation(NodeRef arg0, NodeRef arg1,
			QName arg2) throws InvalidNodeRefException,
			AssociationExistsException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ChildAssociationRef createNode(NodeRef arg0, QName arg1, QName arg2,
			QName arg3) throws InvalidNodeRefException, InvalidTypeException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ChildAssociationRef createNode(NodeRef arg0, QName arg1, QName arg2,
			QName arg3, Map<QName, Serializable> arg4)
			throws InvalidNodeRefException, InvalidTypeException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public StoreRef createStore(String arg0, String arg1)
			throws StoreExistsException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteNode(NodeRef arg0) throws InvalidNodeRefException {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteStore(StoreRef arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean exists(StoreRef arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean exists(NodeRef arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<NodeRef> findNodes(FindNodeParameters arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<NodeRef> getAllRootNodes(StoreRef arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<QName> getAspects(NodeRef arg0) throws InvalidNodeRefException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AssociationRef getAssoc(Long arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ChildAssociationRef> getChildAssocs(NodeRef arg0)
			throws InvalidNodeRefException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ChildAssociationRef> getChildAssocs(NodeRef arg0,
			Set<QName> arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ChildAssociationRef> getChildAssocs(NodeRef arg0,
			QNamePattern arg1, QNamePattern arg2)
			throws InvalidNodeRefException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ChildAssociationRef> getChildAssocs(NodeRef arg0,
			QNamePattern arg1, QNamePattern arg2, boolean arg3)
			throws InvalidNodeRefException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ChildAssociationRef> getChildAssocs(NodeRef arg0, QName arg1,
			QName arg2, int arg3, boolean arg4) throws InvalidNodeRefException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ChildAssociationRef> getChildAssocsByPropertyValue(
			NodeRef arg0, QName arg1, Serializable arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<ChildAssociationRef> getChildAssocsWithoutParentAssocsOfType(
			NodeRef arg0, QName arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NodeRef getChildByName(NodeRef arg0, QName arg1, String arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ChildAssociationRef> getChildrenByName(NodeRef arg0,
			QName arg1, Collection<String> arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long getNodeAclId(NodeRef arg0) throws InvalidNodeRefException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NodeRef getNodeRef(Long arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Status getNodeStatus(NodeRef arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ChildAssociationRef> getParentAssocs(NodeRef arg0)
			throws InvalidNodeRefException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ChildAssociationRef> getParentAssocs(NodeRef arg0,
			QNamePattern arg1, QNamePattern arg2)
			throws InvalidNodeRefException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Path getPath(NodeRef arg0) throws InvalidNodeRefException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Path> getPaths(NodeRef arg0, boolean arg1)
			throws InvalidNodeRefException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ChildAssociationRef getPrimaryParent(NodeRef arg0)
			throws InvalidNodeRefException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<QName, Serializable> getProperties(NodeRef arg0)
			throws InvalidNodeRefException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NodeRef getRootNode(StoreRef arg0) throws InvalidStoreRefException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<AssociationRef> getSourceAssocs(NodeRef arg0, QNamePattern arg1)
			throws InvalidNodeRefException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NodeRef getStoreArchiveNode(StoreRef arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<StoreRef> getStores() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<AssociationRef> getTargetAssocs(NodeRef arg0, QNamePattern arg1)
			throws InvalidNodeRefException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public QName getType(NodeRef arg0) throws InvalidNodeRefException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasAspect(NodeRef arg0, QName arg1)
			throws InvalidNodeRefException, InvalidAspectException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public ChildAssociationRef moveNode(NodeRef arg0, NodeRef arg1, QName arg2,
			QName arg3) throws InvalidNodeRefException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void removeAspect(NodeRef arg0, QName arg1)
			throws InvalidNodeRefException, InvalidAspectException {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeAssociation(NodeRef arg0, NodeRef arg1, QName arg2)
			throws InvalidNodeRefException {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeChild(NodeRef arg0, NodeRef arg1)
			throws InvalidNodeRefException {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean removeChildAssociation(ChildAssociationRef arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void removeProperty(NodeRef arg0, QName arg1)
			throws InvalidNodeRefException {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean removeSeconaryChildAssociation(ChildAssociationRef arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public NodeRef restoreNode(NodeRef arg0, NodeRef arg1, QName arg2,
			QName arg3) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setChildAssociationIndex(ChildAssociationRef arg0, int arg1)
			throws InvalidChildAssociationRefException {
		// TODO Auto-generated method stub

	}

	@Override
	public void setProperties(NodeRef arg0, Map<QName, Serializable> arg1)
			throws InvalidNodeRefException {
		// TODO Auto-generated method stub

	}

	@Override
	public void setProperty(NodeRef arg0, QName arg1, Serializable arg2)
			throws InvalidNodeRefException {
		// TODO Auto-generated method stub

	}

	@Override
	public void setType(NodeRef arg0, QName arg1)
			throws InvalidNodeRefException {
		// TODO Auto-generated method stub

	}

}
