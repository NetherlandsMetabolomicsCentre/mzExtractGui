<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main"/>
	</head>
	<body>
		<h2>${project.name}	<small>project </small></h2>
		<g:link action="settings" id="${project.canonicalPath.bytes.encodeBase64().toString()}">run</g:link> <i class="icon-indent-left"></i>
		<hr />
		<h3>files</h3>
		<g:if test="${files}">	
			<ul>	
				<g:each in="${files}" var="${file}">
					<li><i class="icon-signal"></i> ${file.name} ${new Date(file.lastModified())}</li>
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
					<li><i class="icon-tasks"></i> <g:link action="run" id="${run.canonicalPath.bytes.encodeBase64().toString()}">${run.name}</g:link> ${new Date(run.lastModified())}</li>
				</g:each>		
			</ul>
		</g:if>
	</body>
</html>