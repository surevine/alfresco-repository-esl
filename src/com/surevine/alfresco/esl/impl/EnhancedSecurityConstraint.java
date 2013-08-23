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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.naming.OperationNotSupportedException;
import org.alfresco.module.org_alfresco_module_dod5015.caveat.RMListOfValuesConstraint;
import com.surevine.alfresco.esl.exception.EnhancedSecurityException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Subclass of RMLOVConstraint with identical RLS behavior, but which incorporates a number of display-related metadata designed to inform and operate the Enhanced Security user interface
 * 
 * @author simonw
 * 
 */
public class EnhancedSecurityConstraint extends RMListOfValuesConstraint {

    private static final Log LOG = LogFactory.getLog(EnhancedSecurityConstraint.class);

    /**
     * Metadata referring to individual groups (ie. single values) rather than the constraint as a whole
     */
    private Collection<GroupDetails> _groupDetails;

    /**
     * Enum restricting display priority values to LOW or HIGH
     */
    private enum DisplayPriority {
        LOW("Low"), HIGH("High");
        private String _pri;

        DisplayPriority(String pri) {
            _pri = pri;
        }

        public String toString() {
            return _pri;
        }
    };

    /**
     * How much priority (in practice, how far up or down the interface) should be given to display this constraint in a UI. Note that the internal representation of this field is via the DisplayPriority enum which ensures correctness, but the property is exposed as a String via the public interface of this class to enable easier integration with Spring and to create a simpler interface.
     */
    private DisplayPriority _displayPriority;

    /**
     * If true, then the backend should hide from users values for this constraint which aren't actually assigned to the users. A value of 'false' is only valid when MatchLogic=true
     */
    private boolean _filterDisplay;

    /**
     * Description of the constraint
     */
    private String _description;

    /**
     * Validation is expensive, we only want to perform it once per constraint to make sure the config is set up right and this boolean is the flag to make that work
     */
    private boolean _validated = false;

    /**
     * Set of system names that have been declared for values of this constraint - we use this internally to prevent system names from being reused as they should be unique, but that's all
     */
    private Collection<String> _declaredSystemNames = new HashSet<String>();

    /**
     * Is the constraint deprecated? If so, it means that it still takes effect at the back end, but front ends may choose not to render the constraint, mark it as deprecated etc
     */
    private boolean _deprecated = false;

    /**
     * As above, but for human names. Note that human names and system names are unique individually, not as a compound
     */
    private Collection<String> _declaredHumanNames = new HashSet<String>();

    /**
     * Instance of a class that knows how to search the model for enhanced security constraints
     */
    private static EnhancedSecurityConstraintLocator _locator;

    public static synchronized void setEnhancedSecurityLocator(EnhancedSecurityConstraintLocator locator) {
        _locator = locator;
    }

    /**
     * Record that the given system name and human name have been used in this constraint, and throw an exception if this would break uniqueness
     * 
     * @param systemName
     *            System-facing name (should be [A-Z0-9]+) to register
     * @param humanName
     *            Human-facing name to register
     * @throws EnhancedSecurityException
     *             If either the system name or the human name has already been used in this constraint
     */
    public void registerValue(String systemName, String humanName) {
        if (_declaredHumanNames.contains(humanName)) {
            throw new EnhancedSecurityException("A value for the constraint [" + this.getShortName() + "] with human name: " + humanName + " has already been declared and must be unique");
        }
        if (_declaredSystemNames.contains(systemName)) {
            throw new EnhancedSecurityException("A value for the constraint [" + this.getShortName() + "] with system name: " + systemName + " has already been declared and must be unique");
        }
        _declaredSystemNames.add(systemName);
        _declaredHumanNames.add(humanName);
    }

    /**
     * @param description
     *            A human-readable description of what the constraint is for - maybe a sentance or two in length. Must be set to a non-null, non-all-whitespace value
     */
    public void setDescription(String description) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Setting description to:  " + description + "  for " + this.getShortName());
        }
        if (description != null) {
            _description = description;
        } else // Description is null!
        {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Something tried to set the description of " + this.getShortName() + " to null.  Writing stack");
                StackTraceElement[] stack = Thread.currentThread().getStackTrace();
                for (int i = 0; i < stack.length; i++) {
                    LOG.debug("    " + stack[i].getClassName() + "." + stack[i].getMethodName() + "(" + stack[i].getLineNumber() + ")");
                }
            }
        }
    }

    public String getDescription() {
        return _description;
    }

    /**
     * @param displayPriority
     *            Hint to the UI as to how much important to attach to this constraint. Must be set to either "low" or "high". Case insensitive.
     */
    public void setDisplayPriority(String displayPriority) {
        if (displayPriority == null) {
            throw new EnhancedSecurityException("Display Priority cannot be null on: " + this.getShortName());
        }
        if (displayPriority.equalsIgnoreCase(DisplayPriority.LOW.toString())) {
            _displayPriority = DisplayPriority.LOW;
        } else if (displayPriority.equalsIgnoreCase(DisplayPriority.HIGH.toString())) {
            _displayPriority = DisplayPriority.HIGH;
        } else {
            throw new EnhancedSecurityException("DisplayPriority must be either 'low' or 'high' (case insensitive).  The value specified for [" + this.getShortName() + "] was: " + displayPriority);
        }
    }

    public String getDisplayPriority() {
        return _displayPriority.toString();
    }

    /**
     * @param filterDisplay
     *            If true, then indicate to clients of this class that they should hide values in this constraint that users don't have from those users. Note that this class doesn't, and indeed can't, enforce that, it's up to clients of the class
     */
    public void setFilterDisplay(boolean filterDisplay) {
        _filterDisplay = filterDisplay;
    }

    public boolean getFilterDisplay() {
        return _filterDisplay;
    }

    /**
     * Although we're a subclass of RMLOVConstraint, we have a richer way of setting the allowed values, setGroupDetailsSpecification, so although we need to support the method through the subclass relationship, we throw an exception if anyone actually calls it
     * 
     * @throws EnhancedSecurityException
     *             all the time
     */
    @SuppressWarnings("rawtypes")
    @Override
    public void setAllowedValues(List allowedValues) {
        throw new EnhancedSecurityException("Use setGroupDetailsSpecification instead of setAllowedValues", new OperationNotSupportedException("Property not supported in EnhancedSecurityConstraint.  Please use the groupDetailsSpecification property instead"));
    }

    /**
     * Our richer version of setAllowedValues. Defines the allowable values in this constraint, sets their display-oriented metadata and validates
     * 
     * @param groupDetailsSpecification
     *            A list of String values taken from the model XML file. See GroupDetails for the exact syntax. Must be non-null and non-empty
     */
    public void setGroupDetailsSpecification(List<String> groupDetailsSpecification) {
        if (LOG.isInfoEnabled()) {
            logGroupDetailsSpecification(groupDetailsSpecification);
        }

        if (groupDetailsSpecification == null || groupDetailsSpecification.size() == 0) {
            throw new EnhancedSecurityException("A null or empty group details specification was passed to the constraint: " + this.getShortName());
        }
        _groupDetails = new ArrayList<GroupDetails>(groupDetailsSpecification.size());
        List<String> allowedValuesList = new ArrayList<String>(groupDetailsSpecification.size());
        Iterator<String> i = groupDetailsSpecification.iterator();
        while (i.hasNext()) {
            GroupDetails groupDetails = new GroupDetails(i.next(), this);
            groupDetails.validate();
            _groupDetails.add(groupDetails);
            if (LOG.isDebugEnabled()) {
                LOG.debug("Adding to allowed values: " + groupDetails.getSystemName());
            }
            allowedValuesList.add(groupDetails.getSystemName());
        }
        super.setAllowedValues(allowedValuesList);
    }

    /**
     * Called by setGroupDetailsSpecification, this method logs the config found in the custom model at INFO level
     */
    protected void logGroupDetailsSpecification(List<String> groupDetailsSpecification) {
        LOG.info("Group Details Specififcation for " + this.getShortName() + ":");
        if (groupDetailsSpecification != null) {
            Iterator<String> i = groupDetailsSpecification.iterator();
            while (i.hasNext()) {
                LOG.info("  [" + i.next() + "]");
            }
        }
    }

    /**
     * Regenerate a specification for the current state of the constraint. This will usually return the configuration that was used to create the constraint, minus some normalisation of case and whitespace, but will reflect any changes that have occurred since then
     * 
     * @return
     */
    public List<String> getGroupDetailsSpecification() {
        List<String> out = new ArrayList<String>(_groupDetails.size());
        Iterator<GroupDetails> details = _groupDetails.iterator();

        while (details.hasNext()) {
            out.add(details.next().toString());
        }
        return out; // Could potentially improve performance by caching return value and only recalculating if a mutator has been called
    }

    /**
     * Retrieve details on the values (aka groups) managed by this constraint. A richer version of getAllowedValues(), although getAllowedValues() is still allowed and supported
     * 
     * @return
     */
    public Collection<GroupDetails> getGroupDetails() {
        return new ArrayList<GroupDetails>(_groupDetails);
    }

    public GroupDetails getDetailsForGroup(final String groupName) {
        Iterator<GroupDetails> i = _groupDetails.iterator();
        while (i.hasNext()) {
            GroupDetails details = i.next();
            if (details.getSystemName().equals(groupName)) {
                return details;
            }
        }
        throw new EnhancedSecurityException("The specified group " + groupName + "'s details were requested, but could not be found registered to the constraint " + this.getShortName());
    }

    @Override
    public Map<String, Object> getParameters() {
        Map<String, Object> params = new HashMap<String, Object>(2);

        params.put("caseSensitive", isCaseSensitive());
        params.put("allowedValues", getAllowedValues());
        params.put("description", getDescription());
        params.put("displayPriority", getDisplayPriority());
        params.put("filterDisplay", getFilterDisplay());
        params.put("groupDetailsSpecification", getGroupDetailsSpecification());
        params.put("matchLogic", getMatchLogic());
        params.put("deprecated", getDeprecated());
        return params;
    }

    /**
     * Best hook I (simonw) could find for adding some extra validation. The first time we use the constraint to evaluate something, run the validate() method then pass behaviour to superclass
     */
    @Override
    protected void evaluateSingleValue(Object value) {
        if (LOG.isDebugEnabled()) {
            LOG.debug(this + " evaluating " + value);
        }
        try {
            if (!_validated) {
                validate();
            }
            super.evaluateSingleValue(value);
        } catch (Exception e) {
            if (LOG.isInfoEnabled()) {
                LOG.info("Encountered an exception while evaluating " + value + " with " + this + ":  " + e, e);
                e.printStackTrace();
            }
            throw new EnhancedSecurityException("Encountered an exception while evaluating " + value + " with " + this + ":  " + e, e);
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug(this + " evaluated " + value);
        }
    }

    /**
     * Does some validation of the constraint as a whole (individual values within the constraint are validated at construction time but we can't do that for the constraint as a whole as the framework uses setter-injection) then pokes a flag to indicate that validation is successful, to prevent us from continuously validating something that never really changes
     * 
     * @throws EnhancedSecurityException
     *             If validation fails for whatever reason
     */
    public void validate() {

        if (_groupDetails == null || _groupDetails.size() == 0) {
            throw new EnhancedSecurityException("The constraint [" + this.getShortName() + "] does not have any values assigned to it");
        }

        if (_displayPriority == null) {
            throw new EnhancedSecurityException("The constraint [" + this.getShortName() + "] does not have a displayPriority associated with it");
        }

        if (_description == null || _description.trim().length() == 0) {
            throw new EnhancedSecurityException("The constraint [" + this.getShortName() + "] must have a non-empty description but it's description was: [" + _description + "]");
        }

        if (_filterDisplay == false && getMatchLogicEnum() == MatchLogic.AND) {
            throw new EnhancedSecurityException("The constraint [" + this.getShortName() + "] is set to not filter the display, but has an AND MatchLogic, which is not valid");
        }

        confirmTypesUnique();

        _validated = true;
    }

    /**
     * Confirm that the types declared in this constraint are not in any other constraint
     */
    protected void confirmTypesUnique() {
        Set<String> uniqueSet = new HashSet<String>();

        // First, check all the _other_ constraints, building up a unique set of types as we go
        Iterator<EnhancedSecurityConstraint> constraints = getLocator().getAllEnhancedSecurityConstraints(true).iterator();
        while (constraints.hasNext()) {
            EnhancedSecurityConstraint constraint = constraints.next();
            if (constraint != this) // Leave this constraint until the end as we cannot guarantee it will be picked up by the locator yet
            {
                Iterator<String> types = constraint.getTypes().iterator();
                while (types.hasNext()) {
                    String type = types.next();
                    if (!uniqueSet.add(type)) {
                        throw new EnhancedSecurityException("The type [" + type + "] cannot be used in multiple constraints");
                    }
                }
            }
        }

        // Lastly, check this constraint
        Iterator<String> types = getTypes().iterator();
        while (types.hasNext()) {
            String type = types.next();
            if (!uniqueSet.add(type)) {
                throw new EnhancedSecurityException("The type [" + type + "] cannot be used in multiple constraints");
            }
        }
    }

    /**
     * Get a collection representing all the group types managed by this constraint
     * 
     * @return Collection of String type names from the group details
     */
    protected Collection<String> getTypes() {
        Set<String> types = new HashSet<String>();
        Iterator<GroupDetails> i = _groupDetails.iterator();

        while (i.hasNext()) {
            types.add(i.next().getType());
        }
        return types;
    }

    /**
     * Included for completeness - mark the constraint as not yet validated. If programmatic changes are made to the constraint that might affect it's validity, this method should then be called. At the time of writing, nothing needs to do this
     */
    public void resetValidationState() {
        _validated = false;
    }

    public void setDeprecated(boolean deprecated) {
        _deprecated = deprecated;
    }

    public boolean getDeprecated() {
        return _deprecated;
    }

    /**
     * Accessor for the locator mostly used for unit testing
     */
    public EnhancedSecurityConstraintLocator getLocator() {
        return _locator;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(80);
        sb.append("EnhancedSecurityConstraint").append("[allowedValues=").append(getAllowedValues()).append(", caseSensitive=").append(isCaseSensitive()).append(", matchLogic=").append(getMatchLogic()).append("]");
        return sb.toString();
    }
}
