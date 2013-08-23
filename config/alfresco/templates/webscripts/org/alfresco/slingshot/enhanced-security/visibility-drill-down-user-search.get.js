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
const DEFAULT_MAX_RESULTS = 100;

function main()
{
   var query = (args.query !== undefined) ? args.query : null;
   var maxResults = (args.maxResults !== undefined) ? parseInt(args.maxResults, 10) : DEFAULT_MAX_RESULTS;

   var luceneQuery = '';

   if (query !== null && (query.length !== 0))
   {
	   var terms = query.split(/\s/);

	   var t;

	   // Go through each of the terms and construct the lucene query
	   for (var i in terms) {
         t = terms[i];

         // Remove quotes
         t = t.replace(/\"/g, '');

         if (t.length !== 0)
         {
	          luceneQuery += 'AND (@cm\\:userName:\"' + t + '*\"' + // Username
	                         ' @cm\\:firstName:\"' + t + '*\"' +  // First name
	                         ' @cm\\:lastName:\"' + t + '*\") '; // Last name
         }
	   }

	   if (luceneQuery.length > 0) {
		   luceneQuery = '(TYPE:\"{http://www.alfresco.org/model/content/1.0}person\"' + luceneQuery + ')';

		   model.nodes = search.luceneSearch(luceneQuery, '@cm:userName', true, DEFAULT_MAX_RESULTS);
	   } else {
		   model.nodes = [];
	   }
   }
}

main();
