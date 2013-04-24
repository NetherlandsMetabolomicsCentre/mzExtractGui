<!DOCTYPE html>
<html>
  <head>
    <meta name="layout" content="main"/>
  </head>
  <body>
    <h2>Run</h2>
    <p>project: <g:link action="project" params="[project: projectFolder.name.encodeAsSHA1()]">${projectFolder.name}</g:link><br /></p>
    <ul><mzextract:projectRun project="${projectFolder}" run="${run}" /></ul>
  
    <h3>settings</h3>    
    <mzextract:settingsForm settings="${settings}" project="${projectFolder}"/>    
  
  <h3>inputFiles</h3>
  <ul> 
    <g:each in="${inputFiles.sort()}" var="${inputFile}">
      <li><i class="icon-signal"></i> ${inputFile.name} (${new Date(inputFile.lastModified())})</li>
    </g:each>
  </ul>

  <h3>outputFiles</h3>
  <ul>
    <g:each in="${outputFiles.sort()}" var="${outputFile}">
      <li><i class="icon-download"></i> <g:link action="download" id="${(outputFile.canonicalPath).encodeAsBase64().toString()}">${outputFile.name}</g:link> (${new Date(outputFile.lastModified())})</li>
    </g:each>		
  </ul>
</body>
</html>