<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main"/>
	</head>
	<body>
		<h2>${project.name}	<small>project </small></h2>
		<h3>settings</h3>
		<g:form name="runProject" action="settings" id="${project.canonicalPath.encodeAsBase64().toString()}">
			<table>
				<g:each in="${settings.keySet()}" var="setting">
					<tr><td nowrap align="right" valign="top">${setting} : </td><td><g:field type="number" name="${setting}" value="${settings[setting]}"/></td></tr>
				</g:each>
				<tr><td colspan="2"><hr /></td></tr>		
				<tr><td colspan="2"><g:submitButton name="do_1" style="width:400px;" value="extract" /></td></tr>
				<tr><td colspan="2"><g:submitButton name="do_2" style="width:400px;" value="extract and align" /></td></tr>
				<tr><td colspan="2"><g:submitButton name="do_3" style="width:400px;" value="extract, align, and combine" /></td></tr>								
			</table>
		</g:form>
	</body>
</html>