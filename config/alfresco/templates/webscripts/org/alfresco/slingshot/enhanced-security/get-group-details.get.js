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
//Find the groupDetails.json file at the expected location in the Repo, and parse it
var jsonFile = companyhome.childByNamePath("groupDetails.json");
var content = jsonFile.properties.content.content; 

//Parse the content as JSON
var jsonObj = eval('('+content+')');

model.output = jsonUtils.toJSONString(parseContent(jsonObj));

// Parse the root of the input JSON
function parseContent(json) 
{
    var constraintId, constraint, markingId, marking, filter, allowedMarkings, newConstraint, hasAccess;
    
    var output = {
            constraints: []
    };
    
    // Go through each of the constraints
    for (constraintId in json.constraints)
    {
        constraint = json.constraints[constraintId];
        
        // Create the new constraint object
        newConstraint = {
                constraintName : constraint.constraintName,
                constraintDescription : constraint.constraintDescription,
                markings: []
            };
        
        if (constraint.displayPriority) {
            newConstraint.displayPriority = constraint.displayPriority;
        }
        
        // Whether we should filter out markings to which the user doesn't have access (e.g. for closed group markings)
        filter = needsFiltering(constraint);
        
        // Get the list of markings the user is allowed to see
        allowedMarkings = convertToArray(''+ESLCaveatConfig.getAllowedValuesForCurrentUser(constraint.constraintName));
        
        
        // Go through the list of markings for the constraint
        for (markingId in constraint.markings) {
            marking = constraint.markings[markingId];

            hasAccess = valueIsInArray(marking.name, allowedMarkings);
            
            if (!filter || hasAccess) {
                // Create the new marking object
                newMarking = {
                        name : marking.name,
                        longName : marking.longName,
                        type : marking.type,
                        description : marking.description,
                        hasAccess : hasAccess
                };
                
                // And shove it into the markings array
                newConstraint.markings.push(newMarking);
            }
        }
        
        output.constraints.push(newConstraint);
    }
    
    return output;
}

function valueIsInArray(value, arr)
{
	for (var i =0; i < arr.length; i++)
	{
		if (arr[i]==value)
		{
			return true;
		}
	}
	return false;
}

function convertToArray(markingsStr)
{
  markingsCSL = markingsStr.replace("[","").replace("]","").replace(/ /g,"");
  return markingsCSL.split(",");
}

function needsFiltering(constraint)
{
  var filterStr=constraint.filterDisplay;
  if (filterStr==null)
  {
	  return false;
  }
  if (filterStr.toUpperCase().equals("FALSE"))
  {
	  return false;
  }
  return true;
}

/*

EXAMPLE INPUT JSON FILE:

{
     "constraints":[{
      "constraintName": "es_validOpenMarkings",
      "constraintDescription": "Open Groups",
      "displayPriority": "High",
      "filterDisplay":"false",
      "markings": [ { "name": "OG01", "longName": "OPENGROUP1", "type": "typeA", "description":"This is the first test open group"},
                    { "name": "OG20", "longName": "OPENGROUP2", "type": "typeA", "description":"This is the second test open group"}
                  ]
     },
     {
      "constraintName": "es_validClosedMarkings",     
      "constraintDescription": "Closed Groups",
      "displayPriority": "Low",
      "filterDisplay":"true",
      "markings": [{ "name": "CLOSEDGROUP1", "longName": "CLOSEDGROUP1", "type": "Closed", "description":"This is the first test closed group"},
                   { "name": "CG02", "longName": "CLOSEDGROUP2", "type": "Closed", "description":"This is the second test closed group"},
                   { "name": "APPLESANDPEARS", "longName": "APPLES AND PEARS", "type": "Closed", "description": "No-one should have this group.  If you can see it, something's gone wrong"}
                  ]
   }]
}

*/
