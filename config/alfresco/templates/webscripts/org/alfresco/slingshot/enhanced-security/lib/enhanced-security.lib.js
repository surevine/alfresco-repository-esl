
/**
 * Given a set of closed markings, seperate out any atomal markings embedded inside
 * @param closedMarkings Array of closed markings
 * @return An object with two properties; "atomal" is the atomal value derived from the closed markings. "closedMarkings"
 * is the input set of closed markings with any atomal component removed
 */
function seperateAtomalFromClosedMarkings(closedArr)
{
    if (closedArr==null)
    {
        return { atomal: "", closedMarkings:[]};
    }
    var atomal="";
    var newClosed=[];
    for (var i=0; i < closedArr.length; i++)
    {
        if (closedArr[i]=="@@esc.atomal@@1" && atomal!="@@esc.atomal@@2")
        {
            atomal="@@esc.atomal@@1";
        }
        else if (closedArr[i]=="@@esc.atomal@@2")
        {
            atomal="@@esc.atomal@@2";
        }
        else {
            newClosed.push(closedArr[i]);
        }
    }
    return { atomal: atomal, closedMarkings: newClosed};
}

/**
 * Given an atomal string, return some groups to add to the closed markings before creating an item
 * @param atomalString One of null, "", "NO ATOMAL", "ATOMAL1" or "ATOMAL2"
 * @param needsLeadingComma If true, attach a leading comma to any non-empty return value
 * @return Comma seperated list of closed groups derived from the input atomal value
 */
function atomalToGroups(atomalString, needsLeadingComma)
{
    var lead="";
    if (needsLeadingComma)
    {
        lead=",";
    }
    if (atomalString==null || atomalString=="NO @@esc.atomal@@")
    {
        return "";
    }
    else if (atomalString=="@@esc.atomal@@1")
    {
        
        return lead+"@@esc.atomal@@1";
    }
    else if (atomalString=="@@esc.atomal@@2")
    {
        return lead+"@@esc.atomal@@2";
    }
    else
    {
        return "";
    }
}