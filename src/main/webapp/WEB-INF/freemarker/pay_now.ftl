<#assign title = "Test"/>
<#escape x as x?xml>
<#include "head.ftl" />

<p>Your order has been received; please continue to <a href="bitcoin:?r=${order_id?url}">${order_id}</a> to pay.</p>

<#include "foot.ftl" />
</#escape>