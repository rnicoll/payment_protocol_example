<#assign title = "Test"/>
<#escape x as x?xml>
<#include "head.ftl" />

<form action="" method="post" role="form">

  <div class="form-group">
    <label for="network">Network:</label>
    <select class="form-control" id="network" name="network" required="required">
      <option value="DOGECOIN_MAIN" selected="selected">Main</option>
      <option value="DOGECOIN_TEST">Test</option>
    </select>
  </div>
  <div class="form-group">
    <label for="amount">Amount (DOGE):</label>
    <input type="number" class="form-control" id="amount" name="amount" min="0" max="1000000" step="0.00000001" value="100" required="required" />
  </div>
  <div class="form-group">
    <label for="address">Address:</label>
    <input type="text" class="form-control" id="address" name="address" required="required" />
  </div>
  <div class="form-group">
    <label for="memo">Memo:</label>
    <input type="text" class="form-control" id="memo" name="memo" />
  </div>
  <button type="submit" class="btn btn-default">Submit</button>

</form>

<#include "foot.ftl" />
</#escape>