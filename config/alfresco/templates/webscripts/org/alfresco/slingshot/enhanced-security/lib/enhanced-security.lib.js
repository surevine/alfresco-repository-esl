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
