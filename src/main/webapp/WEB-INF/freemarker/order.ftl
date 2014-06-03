<#assign title = "Test"/>
<#escape x as x?xml>
<#include "head.ftl" />

<p>Your order has been received; please continue to
<a href="bitcoin:?r=http://${request.getLocalName()?url}:${request.getLocalPort()?c}${request.getContextPath()}/order/${order_id?url}/request">${order_id}</a> to pay.</p>

<#include "foot.ftl" />
</#escape>