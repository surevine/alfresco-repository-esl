package com.surevine.alfresco.esl.impl;

import com.surevine.alfresco.esl.exception.EnhancedSecurityException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Class modeling a "Permission Authority" for a single security group.  Basically a wrapper around two Strings - a name and a department
 * @author simonw
 *
 */
public class PermissionAuthority implements Comparable<PermissionAuthority>
{

	private String _name;
	private String _department;
	
	private static final Log LOG = LogFactory.getLog(PermissionAuthority.class);

	/**
	 * Empty constructor - setName and setDepartment must then be called
	 */
	public PermissionAuthority()
	{
		
	}
	
	public PermissionAuthority(String name, String department)
	{
		setName(name);
		setDepartment(department);
		validate();
	}
	
	public String getName()
	{
		return _name;
	}
	
	public String getDepartment()
	{
		return _department;
	}
	
	public void setName(String name)
	{
		_name=name.trim().replaceAll("\\s+", " ");
	}
	
	public void setDepartment(String department)
	{
		_department=department.trim().replaceAll("\\s+", " ");
	}
	
	/**
	 * Compare/order first by department, then by name, case insensitively
	 */
	public int compareTo(PermissionAuthority toCompare)
	{

		int deptVal = _department.toLowerCase().compareTo(toCompare.getDepartment().toLowerCase());
		if (deptVal!=0)
		{
			return deptVal;
		}
		return _name.toLowerCase().compareTo(toCompare.getName().toLowerCase());
	}
	
	public boolean equals(Object o)
	{
		if (!(o instanceof PermissionAuthority))
		{
			return false;
		}
		return compareTo((PermissionAuthority)o)==0;
	}
	
	public String toString()
	{
		return "PermissionAuthority["+_name+" - "+_department+"]";
	}
	
	public int hashCode()
	{
		return (_department.toLowerCase()+_name.toLowerCase()).hashCode();
	}
	
	/**
	 * Passes validation if the name and department are both set to non-empty values
	 * @throws EnhancedSecurityException If validation fails
	 */
	public void validate()
	{
		if (_name==null)
		{
			throw new EnhancedSecurityException("A Permission Authority was specified with a null name and department: "+_department);
		}
		
		if (_name.trim().length()==0)
		{
			throw new EnhancedSecurityException("The name ["+_name+"] for a Permission Authority appears to be empty.  The department specified was: "+_department);
		}
		
		if (_department==null)
		{
			throw new EnhancedSecurityException("A Permission Authority was specified with a null department and name: "+_name);
		}
		
		if (_department.trim().length()==0)
		{
			throw new EnhancedSecurityException("The department ["+_department+"] for a Permission Authority appears to be empty.  The name specified was: "+_name);
		}
		if (LOG.isDebugEnabled())
		{
			LOG.debug(this+" passed validation");
		}
	}
	
}
