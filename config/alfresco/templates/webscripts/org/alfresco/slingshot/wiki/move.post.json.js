<import resource="classpath:/alfresco/templates/webscripts/org/alfresco/slingshot/wiki/lib/wiki.lib.js">
/**
 * Renames a wiki page. Updates the name of the current page
 * and creates a link with the previous name that points to the page;
 * this is done so that all references to the old page will still work.
 *
 * @method POST
 * @param uri {string} /slingshot/wiki/page/{siteid}/{pageTitle}
 */
model.result = main();

function main()
{
   if (json.isNull("name"))
   {
      return jsonError("No new name property specified");
   }
   // Remove any whitespace and replace with "_"
   var newName = new String(json.get("name"));
   newName = newName.replace(/\s+/g, "_");
   
   var params = getTemplateArgs(["siteId", "pageTitle"]);
   
   // Get the site
    var site = siteService.getSite(params.siteId);
    if (site === null)
    {
      return jsonError("Could not find site: " + params.siteId);
    }

   var wiki = getWikiContainer(site);
   if (wiki === null)
   {
      return jsonError("Could not locate wiki");
   }

   var page = wiki.childByNamePath(params.pageTitle);
   if (!page)
   {
      return jsonError("Could not find specified page.");
   }
   
   var existing = wiki.childByNamePath(newName);
   {
      if (existing)
      {
         status.setCode(status.STATUS_CONFLICT, "Duplicate name.");
         return;
      }
   }
   
   // Finally, now we can do what we are supposed to do
   var currentName = new String(page.name);
   
   page.name = newName;
   page.properties["cm:title"] = new String(newName).replace(/_/g, " ");
   page.save();
   
   var placeholder = createWikiPage(currentName, wiki,
   {
      content: msg.get("page-moved") + " [[" + newName + "|" + msg.get("page-moved-here") + "]]."
   });
   
   //Set the ESL attributes of the placeholder == those of the "new" page
   
   //First, get the ESL attributes of the renamed page
   var nod=page.properties["es:nod"];
   var pm=page.properties["es:pm"];
   var freeformcaveats=page.properties["es:freeFormCaveats"];
   var eyes=page.properties["es:nationalityCaveats"];
   var closedMarkingsArr=page.properties["es:closedMarkings"];
   var openMarkingsArr=page.properties["es:openMarkings"];
   var organisationsArr=page.properties["es:organisations"];
   
   //Next, set the attributes of the placeholder page
   placeholder.properties["es:nod"]=nod;
   placeholder.properties["es:pm"]=pm;
   placeholder.properties["es:freeFormCaveats"]=freeformcaveats;
   placeholder.properties["es:nationalityCaveats"]=eyes;
   placeholder.properties["es:closedMarkings"]=closedMarkingsArr;
   placeholder.properties["es:openMarkings"]=openMarkingsArr;
   placeholder.properties["es:organisations"]=organisationsArr;

   
   placeholder.save();
      
   var data =
   {
      title: newName.replace(/_/g, " "),
      page: json.get("page") + "?title=" + newName,
      custom0: currentName.replace(/_/g, " ")
   }
   
   activities.postActivity("org.alfresco.wiki.page-renamed", params.siteId, "wiki", jsonUtils.toJSONString(data));
   
   return (
   {
      name: newName // Return the new name to the client
   });
}