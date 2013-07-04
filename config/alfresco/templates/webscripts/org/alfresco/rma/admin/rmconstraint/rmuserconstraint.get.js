/**
 * List the names of the rm constraints
 * 
 * Doesn't include legacy thematic (aka open) groups
 */ 
function main()
{
    // Decide on whether or not there was a url extension
    if (url.extension == null || url.extension == "")
    {
        var closedMarkings = ESLCaveatConfig.getAllowedValuesForCurrentUser("es_validClosedMarkings");
        var allMarkings = ESLCaveatConfig.getAllowedValuesForCurrentUser("es_validOrganisations");
        allMarkings.addAll(closedMarkings);
        model.values = allMarkings;
    }
    else
    {
        model.values = ESLCaveatConfig.getAllowedValuesForCurrentUser(url.extension);
    }
}

main();