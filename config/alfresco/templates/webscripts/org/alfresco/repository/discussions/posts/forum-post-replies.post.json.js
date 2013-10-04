<import resource = 'classpath:alfresco/templates/webscripts/org/alfresco/repository/requestutils.lib.js' >
<import resource = 'classpath:alfresco/templates/webscripts/org/alfresco/repository/nodenameutils.lib.js' >
<import resource = 'classpath:alfresco/templates/webscripts/org/alfresco/repository/discussions/topicpost.lib.js' >
<import resource = 'classpath:/alfresco/templates/webscripts/org/alfresco/slingshot/enhanced-security/lib/enhanced-security.lib.js' >

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

const ASPECT_SYNDICATION = 'cm:syndication';
const PROP_PUBLISHED = 'cm:published';

/**
 * Creates a post inside the passed forum node.
 */
function createPostReplyImpl(topicNode, parentPostNode)
{
   // fetch the data required to create a topic
   var title = '';
   if (json.has('title'))
   {
      title = json.get('title');
   }
   var content = '';
   if (json.has('content'))
   {
      content = json.get('content');
   }

   //Get the enhanced security label properties (If they exist)
   var nod = null,
         pm = null,
         freeformcaveats = null,
         closedmarkings = null,
         openmarkings = null,
         organisations = null,
         eyes = null,
         atomal = null;


   nod = json.get('eslNationalOwner');
   pm = json.get('eslProtectiveMarking');
   freeformcaveats = json.get('eslCaveats');
   closedmarkings = json.get('eslClosedGroupsHidden');
   openmarkings = json.get('eslOpenGroupsHidden');
   organisations = json.get('eslOrganisationsHidden');
   eyes = json.get('eslNationalCaveats');
   atomal = json.get('eslAtomal');

   //Note that this is less logic than is in the client-side javascript
   //We're not trying to replicate the formatting here, just protect against XSS
   //and the formatting rules happen to make it easy
   freeformcaveats = freeformcaveats.replaceAll(/[^a-zA-Z ]+/g, '');

   if (closedmarkings != null)
   {
       closedmarkings = closedmarkings + atomalToGroups(atomal, closedmarkings.length() > 0);
   }
   else
   {
       closedmarkings = atomalToGroups(atomal, false);
   }

   // create the post node using a unique name
   var name = getUniqueChildName(topicNode, 'post');
   var postNode = topicNode.createNode(name, 'fm:post');
   postNode.mimetype = 'text/html';
   postNode.properties.title = title;
   postNode.content = content;
   postNode.save();

   // add the cm:syndication aspect
   var props = new Array();
   props[PROP_PUBLISHED] = new Date();
   postNode.addAspect(ASPECT_SYNDICATION, props);

   // link it to the parent post
   postNode.addAspect('cm:referencing');
   postNode.createAssociation(parentPostNode, 'cm:references');
   postNode.save(); // probably not necessary here

   //Set ESC properties
   //Iff we've got a PM (mandatory property of an ESL) then populate the whole ESL
   // both for the content node and the topic node
   if (pm != null && pm != '') {
	   postNode.properties['es:nod'] = nod;
	   postNode.properties['es:pm'] = pm;
	   postNode.properties['es:freeFormCaveats'] = freeformcaveats;
	   postNode.properties['es:nationalityCaveats'] = eyes;
	   if (closedmarkings != null && closedmarkings != '') {
		   postNode.properties['es:closedMarkings'] = closedmarkings.split(',');
	   }
	   else {
		   postNode.properties['es:closedMarkings'] = null;
	   }
	   if (openmarkings != null && openmarkings != '') {
		   postNode.properties['es:openMarkings'] = openmarkings.split(',');
	   }
	   else {
		   postNode.properties['es:openMarkings'] = null;
	   }
	   if (organisations != null && organisations != '') {
		   postNode.properties['es:organisations'] = organisations.split(',');
	   }
	   else {
		   postNode.properties['es:organisations'] = null;
	   }
	   postNode.save();
   }

   return getReplyPostData(postNode);
}

/**
 * Creates a reply to a post.
 * @param node The parent post node
 */
function createPostReply(node)
{
   // we have to differentiate here whether this is a top-level post or a reply
   if (node.type == '{http://www.alfresco.org/model/forum/1.0}topic')
   {
      // find the primary post node
      var topic = node;
      var post = findPostNode(node);
      return createPostReplyImpl(topic, post);
   }
   else if (node.type == '{http://www.alfresco.org/model/forum/1.0}post')
   {
      // the forum is the parent of the node
      var topic = node.parent;
      var post = node;
      return createPostReplyImpl(topic, post);
   }
   else
   {
      status.setCode(STATUS_BAD_REQUEST, 'Incompatible node type. Required either fm:topic or fm:post. Received: ' + node.type);
      return null;
   }
}

function main()
{
   // get requested node
   var node = getRequestNode();
   if (status.getCode() != status.STATUS_OK)
   {
      return;
   }

   model.postData = createPostReply(node);

   // add an activity item
   if (json.has('site') && json.has('page'))
   {
      // fetch the topic (and with it the root post
      var topicData = getTopicPostData(model.postData.post.parent);
      var data =
      {
         title: topicData.post.properties.title,
         page: json.get('page') + '?topicId=' + topicData.topic.name
      };
      activities.postActivity('org.alfresco.discussions.reply-created', json.get('site'), 'discussions', jsonUtils.toJSONString(data));
   }
}

main();
