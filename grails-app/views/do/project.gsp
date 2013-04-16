<!DOCTYPE html>
<html>
  <head>
    <meta name="layout" content="main"/>
  </head>
  <body>
    <h2>${projectFolder.name}</h2>
  <g:link action="settings" params="[project: projectFolder.name.encodeAsSHA1()]">run</g:link> <i class="icon-indent-left"></i>
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

  <g:if test="${projectRunFolders}">
    <h3>runs</h3>
    <ul>
      <g:each in="${projectRunFolders}" var="${runFolder}">
        <li>
          <g:link action="delrun" params="[project: projectFolder.name.encodeAsSHA1(), run: runFolder.name.encodeAsSHA1()]" onclick="return confirm('Are you sure you want to delete this run?')"><i class="icon-remove-sign"></i></g:link>
          <i class="icon-tasks"></i> 
          <g:link action="run" params="[project: projectFolder.name.encodeAsSHA1(), run: runFolder.name.encodeAsSHA1()]">run</g:link> 
          ${runFolder.name.replace('_',' ')}
        </li>
      </g:each>		
    </ul>
  </g:if>
</body>
</html>