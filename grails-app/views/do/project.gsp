<!DOCTYPE html>
<html>
  <head>
    <meta name="layout" content="main"/>
  </head>
  <body>
    <h2>${projectFolder.name}</h2>
  <g:link action="run" params="[project: projectFolder.name.encodeAsSHA1()]">create run</g:link> <i class="icon-indent-left"></i>
  <hr />
  <h3>files</h3>
  <g:if test="${projectMzxmlFiles}">	
    <ul>	
      <g:each in="${projectMzxmlFiles}" var="${file}">
        <li>
          <i class="icon-signal"></i> ${file.name} 
          <small>(${new Date(file.lastModified()).format('yyyy-MM-dd')})</small>
        </li>
      </g:each>
    </ul>
  </g:if>
  <g:else>
    no files found!
  </g:else>
  
  <g:if test="${projectMzFile.exists()}">
    <h3>mzFile</h3>
    <ul>
      <li><i class="icon-download"></i> <g:link action="download" id="${(projectMzFile.canonicalPath).encodeAsBase64().toString()}">${projectMzFile.name}</g:link> <small>(${new Date(projectMzFile.lastModified()).format('yyyy-MM-dd')})</small></li>
    </ul>
  </g:if>  

  <mzextract:projectRuns project="${projectFolder}"/>
</body>
</html>