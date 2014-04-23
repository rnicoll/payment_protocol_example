<#assign title = "Test"/>
<#escape x as x?xml>
<#include "head.ftl" />

<form action="" method="post">

<p>Network:
<select name="network">
<option value="main">Main</option>
<option value="test">Test</option>
</select></p>


<p>Memo: <input type="text" name="memo" /></p>

<p><input type="submit" /></p>

</form>

<#include "foot.ftl" />
</#escape>