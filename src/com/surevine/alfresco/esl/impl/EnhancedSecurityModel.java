/*
 * Copyright (C) 2008-2010 Surevine Limited.
 *
 * This file is contributed by Surevine to be distributed with Alfresco.
 *
 * This file should be considered a 'Contribution' as defined in Alfrescos 
 * standard contribution agreement, see Paragraph 1 bullet 1 of
 * <http://www.alfresco.org/resource/AlfrescoContributionAgreementv2.pdf>.
 *
 * Surevine's contributions to Alfresco are free software: you can redistribute 
 * and/or modify it under the terms of the GNU Lesser General Public License as 
 * published by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Surevine's contributions to Alfresco are distributed in the hope that it will 
 * be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package com.surevine.alfresco.esl.impl;

import org.alfresco.service.namespace.QName;

/*
 * You've got to mandrolically keep this Java class in step with the XML model.
 * 
 * Note the first part of each field name indicates the field's type within the model.
 * 
 * @author alfresco@surevine.com
 * @author simon.white@surevine.com
 *
 *
 */
public final class EnhancedSecurityModel {
	
	/**
	 * Cannot be instantiated - holder for statics only.
	 */
	private EnhancedSecurityModel(){ } ;
	
	/**
	 * Namespace for extensions used throughout this model
	 */
	public static final String NAMESPACE_SV="http://www.alfresco.org/model/enhancedSecurity/0.3";
	
	/**
	 * Shortened form of the above namespace
	 */
	public static final String SHORT_NAMESPACE_SV="es";
	
	/**
	 * QName of the Aspect used to tag a security marked content node, and to imply the security marking property
	 */
	public static final QName ASPECT_SECURITY_MARKING = QName.createQName(NAMESPACE_SV, "enhancedSecurityLabel");
	
	/**
	 * QName of the security marking Property holding the open markings part of the security marking of a content node
	 */
	public static final QName PROP_OPEN_GROUPS = QName.createQName(NAMESPACE_SV, "openMarkings");
	
	/**
	 * QName of the security marking Property holding the organisations part of the security marking of a content node
	 */
	public static final QName PROP_ORGANISATIONS = QName.createQName(NAMESPACE_SV, "organisations");
	
	/**
	 * QName of the security marking Property holding the closed markings part of the actual security marking of a content node
	 */
	public static final QName PROP_CLOSED_GROUPS = QName.createQName(NAMESPACE_SV, "closedMarkings");
	
	/**
	 * QName of the security marking Property holding the closed markings part of the actual security marking of a content node
	 */
	public static final QName PROP_NOD = QName.createQName(NAMESPACE_SV, "nod");
	
	/**
	 * QName of the custom model itself (defined in enhancedSecurityCustomModel.xml)
	 */
	public static final QName MODEL = QName.createQName(NAMESPACE_SV, "escCustom");
	
}
