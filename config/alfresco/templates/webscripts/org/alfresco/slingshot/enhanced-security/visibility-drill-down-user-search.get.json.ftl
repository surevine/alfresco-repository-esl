<#escape x as jsonUtils.encodeJSONString(x)>
{
	"people":
	[
		<#list nodes as item>
		{
			"userName": "${item.properties["cm:userName"]!''}",
			"firstName": "${item.properties["cm:firstName"]!''}",
			"lastName": "${item.properties["cm:lastName"]!''}"
		}<#if item_has_next>,</#if>
		</#list>
	]
}
</#escape>