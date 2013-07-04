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
package com.surevine.alfresco.esl.exception;

/**
 * Exception to be used for unrecoverable errors within the Enhanced Security Module
 *
 * Copyright Surevine Ltd 2010.  All rights reserved
 * 
 * @author alfresco@surevine.com
 * @author simon.white@surevine.com
 *
 */
public class EnhancedSecurityException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;

	public EnhancedSecurityException(String message) {
		super(message);
	}
	
	public EnhancedSecurityException(String message, Throwable cause) {
		super(message, cause);
	}

}
