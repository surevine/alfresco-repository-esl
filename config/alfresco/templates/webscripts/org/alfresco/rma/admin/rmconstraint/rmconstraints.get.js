/**
 * List the names of the rm constraints
 */ 
function main()
{
   var wel = true;
   var withEmptyLists = args["withEmptyLists"];
   // Pass the information to the template
   if (withEmptyLists != null && withEmptyLists === 'false')
   {		
	  model.constraints = ESLCaveatConfig.constraintsWithoutEmptyList;
   }
   else
   {
	  model.constraints = ESLCaveatConfig.allConstraints;
   }
}

main();