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
<import resource="classpath:alfresco/templates/webscripts/org/alfresco/repository/requestutils.lib.js">
<import resource="classpath:alfresco/templates/webscripts/org/alfresco/repository/nodenameutils.lib.js">
<import resource="classpath:alfresco/templates/webscripts/org/alfresco/repository/comments/comments.lib.js">

/**
 * Creates a post inside the passed forum node.
 */
function addComment(node)
{
   // fetch the data required to create a comment
   var title = "";
   if (json.has("title"))
   {
      title = json.get("title");
   }
   var content = json.get("content");

   // fetch the parent to add the node to
   var commentsFolder = getOrCreateCommentsFolder(node);

   // get a unique name
   var name = getUniqueChildName(commentsFolder, "comment");
   
   //Get the enhanced security label properties (If they exist)
   var   nod = null,
         pm=null,
         freeformcaveats=null,
         closedmarkings = null,
         organisations = null,
         openmarkings = null //If the parent item has legacy thematic groups we still want to copy them onto the child item
         ;
      
   nod=json.get("eslNod");
   pm=json.get("eslPM");
   freeformcaveats=json.get("eslFreeFormCaveats");
   closedmarkings=json.get("eslClosedMarkings");
   openmarkings=json.get("eslOpenMarkings");
   organisations=json.get("eslOrganisations");
   
   //Note that this is less logic than is in the client-side javascript
   //We're not trying to replicate the formatting here, just protect against XSS
   //and the formatting rules happen to make it easy
   freeformcaveats=freeformcaveats.replaceAll(/[^a-zA-Z ]+/g,'');
   
   
   // create the comment
   var commentNode = commentsFolder.createNode(name, "fm:post");
   commentNode.mimetype = "text/html";
   commentNode.properties.title = title;
   commentNode.content = content;


   //Set ESC properties
   //Iff we've got a PM (mandatory property of an ESL) then populate the whole ESL
   //if we can't see a comment, we also need to restrict access to the two containers above the comment
   if (pm != null && pm != "") {
   	commentNode.properties["es:nod"] = nod;
   	commentNode.parent.properties["es:nod"] = nod;
   	commentNode.parent.parent.properties["es:nod"] = nod;
   	
   	commentNode.properties["es:pm"] = pm;
   	commentNode.parent.properties["es:pm"] = pm;
   	commentNode.parent.parent.properties["es:pm"] = pm;
   	
	commentNode.properties["es:freeformcaveats"] = freeformcaveats;
	commentNode.parent.properties["es:freeformcaveats"] = freeformcaveats;
	commentNode.parent.parent.properties["es:freeformcaveats"] = freeformcaveats;
	
	if (closedmarkings != null && closedmarkings != "") {	
       		commentNode.properties["es:closedMarkings"] = closedmarkings.split(",");
       		commentNode.parent.properties["es:closedMarkings"] = closedmarkings.split(",");
       		commentNode.parent.parent.properties["es:closedMarkings"] = closedmarkings.split(",");
       	}
       	else 
       	{
       		commentNode.properties["es:closedMarkings"] = null;
       	}
	if (openmarkings != null && openmarkings != "") {
	       	commentNode.properties["es:openMarkings"] = openmarkings.split(","); 
	       	commentNode.parent.properties["es:openMarkings"] = openmarkings.split(","); 
	       	commentNode.parent.parent.properties["es:openMarkings"] = openmarkings.split(","); 
	}
	else 
	{
		commentNode.properties["es:openMarkings"] = null;
	}
	if (organisations != null && organisations != "") {
       	commentNode.properties["es:organisations"] = organisations.split(","); 
       	commentNode.parent.properties["es:organisations"] = organisations.split(","); 
       	commentNode.parent.parent.properties["es:organisations"] = organisations.split(","); 
    }
    else 
    {
	    commentNode.properties["es:organisations"] = null;
    }
   }

   commentNode.save();
   commentNode.parent.save();
   commentNode.parent.parent.save();
    
   return commentNode;
}

function main()
{
   // get requested node
   var node = getRequestNode();
   if (status.getCode() != status.STATUS_OK)
   {
      return;
   }

   var comment = addComment(node);
   model.item = getCommentData(comment);
   model.node = node;
   
   // post an activity item, but only if we've got a site
   if (json.has("site") && json.has("itemTitle") && json.has("page"))
   {
      var siteId = json.get("site");
      if ((siteId != null) && (siteId != ""))
      {
         var params = jsonUtils.toObject(json.get("pageParams")), strParams = "";
         for (param in params)
         {
            strParams += param + "=" + encodeURIComponent(params[param]) + "&";
         }
         var data =
         {
            title: json.get("itemTitle"),
            page: json.get("page") + (strParams != "" ? "?" + strParams.substring(0, strParams.length - 1) : "")
         }
         activities.postActivity("org.alfresco.comments.comment-created", siteId, "comments", jsonUtils.toJSONString(data));
      }
   }
}

main();
