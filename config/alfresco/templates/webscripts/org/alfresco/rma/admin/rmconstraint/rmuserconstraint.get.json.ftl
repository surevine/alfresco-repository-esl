<#escape x as jsonUtils.encodeJSONString(x)>
{
    "allowedValues": [<#list values as value>"${value}"<#if value_has_next>,</#if></#list>]
}
</#escape>