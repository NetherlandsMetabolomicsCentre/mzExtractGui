<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main"/>
	</head>
	<body>
		<h2>Projects</h2>
		<ul>
			<g:each in="${projects.keySet().sort()}" var="project">
				<li><i class="icon-th-list"></i> <g:link action="project" id="${(projects[project].canonicalPath).encodeAsBase64().toString()}">${project}</g:link></li>
			</g:each>
		</ul>
	</body>
</html>