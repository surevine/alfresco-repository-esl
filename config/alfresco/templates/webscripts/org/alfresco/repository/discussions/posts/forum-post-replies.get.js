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
<import resource = 'classpath:alfresco/templates/webscripts/org/alfresco/repository/requestutils.lib.js' >
< import resource = 'classpath:alfresco/templates/webscripts/org/alfresco/repository/discussions/topicpost.lib.js' >

/**
 * Returns all reply nodes to a post
 */
function getRepliesForPost(post)
{
   var children = post.sourceAssocs['cm:references'];
   if (children === null)
   {
      return new Array();
   }
   else
   {
      //Workaround - return only those children we can access
      //to avoid accessDenied issues
      var filteredChildren = new Array();
      for (i = 0; i < children.length; i++)
      {
        try
        {
          //Attempt to access a mandatory property that everything inside children should have
          var creator = children[i].properties['cm:creator'];
          filteredChildren.push(children[i]);
        }
        catch (error)
        {
          //do nothing - filter results
        }
      }
      return filteredChildren;
   }
}

/**
 * Returns a data object containing the passed post,
 * the number of replies to the post, as well as
 * the replies themselves if levels > 1
 */
function getReplyDataRecursive(post, levels)
{
   // encapsulates the data: node, childCount, children
   var data = getReplyPostData(post);
   var children = getRepliesForPost(post);
   data.childCount = children.length;
   if (levels > 1)
   {
      data.children = new Array();
      var x = 0;
      for (x = 0; x < children.length; x++)
      {
         data.children.push(getReplyDataRecursive(children[x], levels - 1));
      }
   }
   return data;
}

/**
 * Returns a data object containing all replies of a post.
 * @return data object with "children" property that contains an array of reply data objects
 */
function getRepliesImpl(post, levels)
{
   var data = getReplyDataRecursive(post, levels + 1);
   if (data.children != undefined)
   {
      return data.children;
   }
   else
   {
      return new Array();
   }
}

function getReplies(node, levels)
{
   // we have to differentiate here whether this is a top-level post or a reply
   if (node.type == '{http://www.alfresco.org/model/forum/1.0}topic')
   {
      // find the primary post node.
      var data = getTopicPostData(node);
      return getRepliesImpl(data.post, levels);
   }
   else if (node.type == '{http://www.alfresco.org/model/forum/1.0}post')
   {
      // the node is already a post
      return getRepliesImpl(node, levels);
   }
   else
   {
      status.setCode(STATUS_BAD_REQUEST, 'Incompatible node type. Required either fm:topic or fm:post. Received: ' + node.type);
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

   // process additional parameters
   var levels = args['levels'] != undefined ? parseInt(args['levels']) : 1;

   model.data = getReplies(node, levels);

   // fetch the contentLength param
   var contentLength = args['contentLength'] != undefined ? parseInt(args['contentFormat']) : -1;
   model.contentLength = isNaN(contentLength) ? -1 : contentLength;
}

main();
