/**
 * This webscript carries out a person search for the visibility drilldown.
 * 
 * Note that a lot of this logic is copied from org/alfresco/slingshot/search/search.lib.js
 */

const DEFAULT_MAX_RESULTS = 100;

function main()
{
   var query = (args.query !== undefined) ? args.query : null;
   var maxResults = (args.maxResults !== undefined) ? parseInt(args.maxResults, 10) : DEFAULT_MAX_RESULTS;
   
   var luceneQuery = "";
   
   if (query !== null && (query.length !== 0))
   {
	   var terms = query.split(/\s/);
	   
	   var t;
	   
	   // Go through each of the terms and construct the lucene query
	   for(var i in terms) {
         t = terms[i];
         
         // Remove quotes
         t = t.replace(/\"/g, "");
         
         if (t.length !== 0)
         {
	          luceneQuery += "AND (@cm\\:userName:\"" + t + "*\"" + // Username
	                         " @cm\\:firstName:\"" + t + "*\"" +  // First name
	                         " @cm\\:lastName:\"" + t + "*\") "; // Last name
         }
	   }
	   
	   if(luceneQuery.length > 0) {
		   luceneQuery = "(TYPE:\"{http://www.alfresco.org/model/content/1.0}person\"" + luceneQuery + ")";
		   
		   model.nodes = search.luceneSearch(luceneQuery, "@cm:userName", true, DEFAULT_MAX_RESULTS);
	   } else {
		   model.nodes = [];
	   }
   }
}

main();