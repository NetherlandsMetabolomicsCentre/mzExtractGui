<!DOCTYPE html>
<html>
  <head>
    <meta name="layout" content="main"/>
  </head>
  <body>
    <h2>${projectFolder.name}	<small>project </small></h2>
    <h3>settings</h3>
  <g:form name="runProject" action="settings">
    <table>
      <g:each in="${settings.keySet()}" var="setting">
        <tr><td nowrap align="right" valign="top">${setting} : </td><td><g:field type="number" name="${setting}" value="${settings[setting]}"/></td></tr>
      </g:each>
      <tr><td colspan="2"><hr /></td></tr>		
      <tr><td colspan="2"><g:submitButton name="do" style="width:400px;" value="next..." /></td></tr>
    </table>
    <g:field type="hidden" name="project" value="${projectFolder.name.encodeAsSHA1()}" />
  </g:form>
</body>
</html>