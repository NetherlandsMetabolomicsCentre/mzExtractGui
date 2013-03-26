<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main"/>
	</head>
	<body>
		<h2>${project.name}</h2>
		<g:link action="settings" id="${project.canonicalPath.encodeAsBase64().toString()}">run</g:link> <i class="icon-indent-left"></i>
		<hr />
		<h3>files</h3>
		<g:if test="${files}">	
			<ul>	
				<g:each in="${files}" var="${file}">
					<li><i class="icon-signal"></i> ${file.name} <small>(${new Date(file.lastModified()).format('dd-MM-yyyy')})</small></li>
				</g:each>
			</ul>
		</g:if>
		<g:else>
			no files found!
		</g:else>

		<g:if test="${runs}">
			<h3>runs</h3>
			<ul>
				<g:each in="${runs}" var="${run}">
					<li><i class="icon-tasks"></i> <g:link action="run" id="${run.canonicalPath.encodeAsBase64().toString()}">${run.name}</g:link> <small>(${new Date(run.lastModified()).format('dd-MM-yyyy')})</small></li>
				</g:each>		
			</ul>
		</g:if>
	</body>
</html>