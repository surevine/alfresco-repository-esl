
/**
 * This webscript retrieves metadata on available enhanced security markings.
 * 
 * The data is stored in a JSON file located in Company Home (TODO: Is there a better location?).
 * This script by and large reprints the data in the input JSON file, with the following changes.
 * 
 * 1)  If the "filterDisplay" property for a constraint is set, then this script will only return those
 *     marking values to which the user has access.  This support "Closed Marking" type logic.
 * 2)  Regardless of (1), a "hasAccess" field is added indicating whether the user has access to the given marking
 *
 * An example input JSON file is given at the end of this script
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