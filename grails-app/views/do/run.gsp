<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main"/>
	</head>
	<body>
		<h2>
			Run
			<small>
				status: ${status} (0:waiting, 1:running, 2:done, -1:failed)
			</small>
		</h2>
		<g:link action="project" id="${(project.canonicalPath).encodeAsBase64().toString()}">back to project: ${project.name}</g:link>		
		<h3>inputFiles</h3>
		<ul>
			<g:each in="${inputFiles.sort()}" var="${inputFile}">
				<li><i class="icon-signal"></i> ${inputFile.name} (${new Date(inputFile.lastModified())})</li>
			</g:each>
		</ul>

		<h3>outputFiles</h3>
		<ul>
			<g:each in="${outputFiles.sort()}" var="${outputFile}">
				<li><i class="icon-download"></i> <g:link action="download" id="${(outputFile.canonicalPath).replaceAll('\\','/').encodeAsBase64().toString()}">${outputFile.name}</g:link> (${new Date(outputFile.lastModified())})</li>
			</g:each>		
		</ul>
	</body>
</html>