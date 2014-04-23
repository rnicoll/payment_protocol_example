<#assign title = "Test"/>
<#escape x as x?xml>
<#include "head.ftl" />

<form action="" method="post">

<p>Network:
<select name="network">
<option value="main">Main</option>
<option value="test">Test</option>
</select></p>

<p>Amount: <input type="number" name="amount" min="0" max="1000000" step="0.00000001" value="100" /></p>

<p>Address: <input type="text" name="address" /></p>

<p>Memo: <input type="text" name="memo" /></p>

<p><input type="submit" /></p>

</form>

<#include "foot.ftl" />
</#escape>