/**
 * Delete the rm constraint list
 */ 
function main()
{
   // Get the shortname
   var shortName = url.extension;
   
   // Get the constraint
   var constraint = ESLCaveatConfig.getConstraint(shortName);
   
   if (constraint != null)
   {
      ESLCaveatConfig.deleteConstraintList(shortName);
      
      // Pass the constraint name to the template
      model.constraintName = shortName;
   }
   else
   {
      // Return 404
      status.setCode(404, "Constraint List " + shortName + " does not exist");
      return;
   }
}

main();