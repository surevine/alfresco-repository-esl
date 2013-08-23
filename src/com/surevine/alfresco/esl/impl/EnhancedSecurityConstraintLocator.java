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
package com.surevine.alfresco.esl.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.alfresco.service.cmr.dictionary.Constraint;
import org.alfresco.service.cmr.dictionary.ConstraintDefinition;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.namespace.QName;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class EnhancedSecurityConstraintLocator {

    private static final Log LOG = LogFactory.getLog(EnhancedSecurityConstraintLocator.class);

    private DictionaryService _dictionaryService;

    /**
     * Usually spring-injected
     */
    public void setDictionaryService(DictionaryService dictionaryService) {
        _dictionaryService = dictionaryService;
    }

    /**
     * Go through the system and get all of the enhanced security constraints
     */
    public Collection<EnhancedSecurityConstraint> getAllEnhancedSecurityConstraints(boolean returnDeprecated) {
        LOG.debug("Getting all ESL constraints");
        Collection<EnhancedSecurityConstraint> constraints = new ArrayList<EnhancedSecurityConstraint>(10);

        // For each model...
        Iterator<QName> models = _dictionaryService.getAllModels().iterator();
        while (models.hasNext()) {
            QName model = models.next();
            LOG.debug("  Looking for ESL constraints in: " + model.toString());

            // ... for each constraint...
            Iterator<ConstraintDefinition> constraintsInModel = _dictionaryService.getConstraints(model, true).iterator();
            while (constraintsInModel.hasNext()) {
                // ... if it's an EnhancedSecurityConstraint...
                Constraint constraint = constraintsInModel.next().getConstraint();
                if (constraint instanceof EnhancedSecurityConstraint) {
                    // ...and it's not deprecated...
                    LOG.debug("    Found ESL constraint: " + constraint.getShortName());
                    if (returnDeprecated == true || !((EnhancedSecurityConstraint) constraint).getDeprecated()) {
                        // ...add it to our return value...
                        constraints.add((EnhancedSecurityConstraint) constraint); // Cast is safe, see instanceof just above
                    }
                }
            }
        }
        return constraints;
    }

}
