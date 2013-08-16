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

import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.TreeSet;

import com.surevine.alfresco.esl.exception.EnhancedSecurityException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Models a single enhanced security group.  Or, using more framework-oriented language, models a single value managed by an EnhancedSecurityConstraint.
 * 
 * Group details are instantiated via a String specification.  The format of the specification consists of a series of line-seperated key value pairs.  The keys are:
 * <ul>
 *  <li>systemName</li>
 *  <li>humanName</li>
 *  <li>description</li>
 *  <li>type</li>
 *  <li>permissionAuthorities</li>
 * </ul>
 * 
 * Permission authorities are a comma-seperated list of user - department pairs.  Eg, <pre>usera-deptone,userb-dept2</pre>.  Whitespace and order are irrelevant here.
 * 
 * Keys may be defined in any case, in any order.  The only optional key is "deprecated", which defaults to "false".  A key may only be defined once for a given constraint.  Whitespace is allowed, and will be stripped (whitespaces _within_ values
 * will not be stripped).  Anything is allowed in a value, except for =.  System name has a bit more logic to it - after trimming, it may only consist of letters and numbers 
 * and will be converted to upper case.  "Deprecated" may take only the values "true" or "false", in any case
 * 
 * EXMAPLE VALID SPECIFICATION:
 * 
 * <pre>
 * 	systemName = ORG1
 * 	humanName = Organisation One
 * 	description = Members of Organisation One
 * 	type = Organisation
 * </pre>
 * 
 * This is exactly the same (note that the value of system name, but not the other fields, can change case) as:
 * 
 * <pre>
 * 	HUMANNAME=Organisation One, 
 * 			DESCRIPTION = Members of Organisation One, 
 * 	SYSTEMNAME=org1, 
 *      TYPE = Organisation
 * </pre>
 * 
 * ...and so on.
 */
public class GroupDetails {

	private static final Log LOG = LogFactory.getLog(GroupDetails.class);

	/**
	 * Regular expression defining the validation for system names.
	 */
	public static final String GROUP_NAME_REGEX = "[_A-Z0-9]+";
	
	//Note no setters provided - must use the constructor - this helps ensure continuous validity
	
	/**
	 * The name of the constraint that will be used by the system, in properties etc.  e.g. "DIRECTORSANDPAS" Upper case, letters and numbers only.  Must be unique within a given constraint
	 */
	private String _systemName=null;
	
	/**
	 * Human-readable version of a system name e.g. "Directors and their Personal Assistants".  Must be unique within a given constraint
	 */
	private String _humanName=null;
	
	/**
	 * Brief human-facing description of a constraint e.g. "Information intended for Directors only, but which may also be seen by their PAs, secretaries and other private office staff"
	 */
	private String _description=null;
	
	/**
	 * Collection of permission authorities related to this group
	 */
	private Collection<PermissionAuthority> _permissionAuthorities = new TreeSet<PermissionAuthority>(); //TreeSet ensures elements are returned in order
	
	/**
	 * Type of the value within the constraints.  This is ignored at the back-end but a user interface may wish to group certain values together within boxes, columns etc and this is done with types
	 */
	private String _type=null;
	
	/**
	 * The only field to have a default value (false) and hence the only optional key in the specification string.  If true, then the value exists at the backend and behaves normally,
	 * but user interfaces should handle the deprecation somehow, such as by not displaying the field, or by marking it as deprecated
	 */
	private boolean _deprecated=false;
	
	/**
	 * Link back to the constraint which is managing this value
	 */
	private EnhancedSecurityConstraint _constraint;
	
	/**
	 * Maximum length of the permission authorities specification (after trimming).  Defaults to 400 but can be overridden with setMaxPermissionAuthoritiesSpecificationLength
	 */
	private int _maxPermissionAuthoritiesSpecificationLength=400;
		
	public void setMaxPermissionAuthoritiesSpecificationLength(int newValue)
	{
		if (newValue<1)
		{
			throw new EnhancedSecurityException("The value "+newValue+" for the maximum length of the permission authorities specification must be greater than zero");
		}
		_maxPermissionAuthoritiesSpecificationLength=newValue;
	}
	
	/**
	 * Constructor.  Create a GroupDetails within the given constraint according to the given specification
	 * @param specification See the comments at the top of this class, and the unit tests, for more details
	 * @param parentConstraint
	 */
	public GroupDetails (String specification, EnhancedSecurityConstraint parentConstraint)
	{
		//Check params look OK
		if (specification==null || parentConstraint == null)
		{
			throw new EnhancedSecurityException("Both the specification and the parent constraint must be non-null to create a GroupDetails");
		}
		
		//Parse out key=value,key=value pairs and use setKVPair to set relevant properties
		try
		{
			_constraint = parentConstraint;
			Scanner keyValuePair = new Scanner(specification.trim());
			keyValuePair.useDelimiter("\n+");
			while (keyValuePair.hasNext())
			{
				String keyOrValueStr = keyValuePair.next().trim();
				if (keyOrValueStr=="")
				{
					continue;
				}
				if (LOG.isDebugEnabled())
				{
					LOG.debug("Found Key/Value Pair: "+keyOrValueStr);
				}
				Scanner keyOrValue = new Scanner(keyOrValueStr);
				keyOrValue.useDelimiter("=");
				String key = keyOrValue.next();
				LOG.debug("  Found Key: "+key);
				String value = keyOrValue.next();
				LOG.debug("  Found Value: "+value);
				if (keyOrValue.hasNext())
				{
					throw new EnhancedSecurityException("Was expecting a single '=' in ["+keyValuePair+"]");
				}
				setKVPair(key, value);
			}
		}
		catch (NoSuchElementException e)
		{
			throw new EnhancedSecurityException("The group details specification was incorrectly formatted.  It should be key=value [lineBreak] key=value with no '=' charecters in the keys or values", e);
		}
		
	}
	
	/**
	 * Given a key and a value, set the relevant field in this object
	 * @param key
	 * @param value
	 */
	private void setKVPair(String key, String value)
	{
		
		//Check params are non-null
		if (value==null || key==null)
		{
			throw new EnhancedSecurityException("Either the key or value was null: ["+key+","+","+value+"]");
		}
		
		//Set relevant param, normalising whitespace and, in the case of system name, also normaluse case and check for invalid values
		key=key.toLowerCase().trim();
		value=value.trim();
		
		if (key.equals("systemname"))
		{
			if (_systemName!=null)
			{
				throw new EnhancedSecurityException("System Name was defined twice for "+this);
			}
			_systemName=value.toUpperCase();
			if (!_systemName.matches(GROUP_NAME_REGEX))
			{
				throw new EnhancedSecurityException("The system name of a value must only contain upper case characters, underscores and/or numbers");
			}
		}
		else if (key.equals("humanname"))
		{
			if (_humanName!=null)
			{
				throw new EnhancedSecurityException("Human Name was defined twice for "+this);
			}
			_humanName=value;
		}
		else if (key.equals("description"))
		{
			if (_description!=null)
			{
				throw new EnhancedSecurityException("Description was defined twice for "+this);
			}
			_description=value;
		}
		else if (key.equals("type"))
		{
			if (_type!=null)
			{
				throw new EnhancedSecurityException("Type was defined twice for "+this);
			}
			_type=value;
		}
		else if (key.equals("deprecated"))
		{
			if (value.equalsIgnoreCase("TRUE"))
			{
				_deprecated=true;
			}
			else if (value.equalsIgnoreCase("FALSE"))
			{
				_deprecated=false;
			}
			else
			{
				throw new EnhancedSecurityException("The deprecated property for ["+this+"] was set to "+value+" but it should either be 'true' or 'false' (case insensitive)");
			}
		}
		else if (key.equals("permissionauthorities"))
		{
			setPermissionAuthorities(value);
		}
		else
		{
			throw new EnhancedSecurityException("Unknown key: "+key);
		}
	}
	
	/**
	 * Set the permission authorities for this group according to the given specification
	 * @param permissionAuthoritiesSpecification comma seperated list of name & department pairs where the name and department are hyphen seperated.
	 * eg. <code>Firstname Surname - department1,name surname-department2,name surname-department3</code>  Whitespace is normalised and both a name and department
	 * must be specified for each permission authority
	 */
	private void setPermissionAuthorities(String permissionAuthoritiesSpecification)
	{
		if (permissionAuthoritiesSpecification.trim().length() > _maxPermissionAuthoritiesSpecificationLength)
		{
			throw new EnhancedSecurityException("The specified permission authorities specification was "+permissionAuthoritiesSpecification.length()+" charecters long, and the maximum allowable length is "+_maxPermissionAuthoritiesSpecificationLength);
		}
		
		String[] authorities = permissionAuthoritiesSpecification.split(",");
		if (authorities.length==0)
		{
			throw new EnhancedSecurityException("No permission authorities were defined within the permission authorities element for "+this);
		}
		for (int i=0; i<authorities.length; i++)
		{
			String[] parts = authorities[i].split("-");
			if (parts.length!=2)
			{
				throw new EnhancedSecurityException("The permission authority with specification ["+authorities[i]+"] was expected to have a single -, but "+(parts.length - 1)+" were found");
			}
			PermissionAuthority authority = new PermissionAuthority(parts[0],parts[1]);
			if (LOG.isInfoEnabled())
			{
				LOG.info("Adding "+authority+" to "+this);
			}
			_permissionAuthorities.add(authority);
		}
	}
	
	/**
	 * Retrieve an iterator of permission authorities, ordered first by their department, then by their name
	 * @return
	 */
	public Iterator<PermissionAuthority> getPermissionAuthorities()
	{
		return _permissionAuthorities.iterator();
	}
	
	public String getSystemName()
	{
		return _systemName;
	}
	
	public String getHumanName()
	{
		return _humanName;
	}
	
	public String getDescription()
	{
		return _description;
	}
	
	public String getType()
	{
		return _type;
	}
	
	public EnhancedSecurityConstraint getConstraint()
	{
		return _constraint;
	}
	
	/**
	 * Check everything is non-null, non-empty and that system name only contains upper case letters and numbers
	 * @throws EnhancedSecurityException
	 */
	public void validate() throws EnhancedSecurityException
	{
		checkStringValue("systemName", _systemName);
		checkStringValue("humanName", _humanName);
		checkStringValue("description", _description);
		checkStringValue("type", _type);
		if (_constraint==null)
		{
			throw new EnhancedSecurityException("No parent constraint for "+this+" was provided, which is not allowed");
		}
		
		if (!_systemName.matches(GROUP_NAME_REGEX))
		{
			throw new EnhancedSecurityException("A system name may only contain upper case letters and numbers");
		}
		
		if (_permissionAuthorities==null || _permissionAuthorities.size()==0)
		{
			throw new EnhancedSecurityException("No permission authorities were defined for "+this);
		}
		
		_constraint.registerValue(_systemName, _humanName);
		
	}

	private void checkStringValue(String key, String value) throws EnhancedSecurityException
	{
		if (value==null || value.trim().length()<1)
		{
			throw new EnhancedSecurityException("The key: ["+key+"] has en empty value: ["+value+"] which is not allowed");
		}
	}
	
	/**
	 * Returns a specification of the group as per the input of the constructor
	 */
	public String toString()
	{
		StringBuffer out = new StringBuffer(120);
		
		out.append("systemName="+_systemName+"\nhumanName="+_humanName+"\ntype="+_type+"\ndescription="+_description+"\npermissionAuthorities="+getPermissionAuthoritiesString());
		if (_deprecated)
		{
			out.append("\ndeprecated=true");
		}
		return out.toString();
	}
	
	private String getPermissionAuthoritiesString()
	{
		StringBuffer out = new StringBuffer(40);
		Iterator<PermissionAuthority> pas = getPermissionAuthorities();
		while (pas.hasNext())
		{
			PermissionAuthority pa = pas.next();
			out.append(pa.getName()+" - "+pa.getDepartment());
			if (pas.hasNext())
			{
				out.append(",");
			}
		}
		return out.toString();
	}
	
	public int hashCode()
	{
		return toString().hashCode();
	}
	
	public boolean isDeprecated()
	{
		return _deprecated;
	}
	
}
