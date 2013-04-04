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
          <small>(${new Date(file.lastModified()).format('dd-MM-yyyy')})</small>
        </li>
      </g:each>
    </ul>
  </g:if>
  <g:else>
    no files found!
  </g:else>

  <g:if test="${projectRunFolders}">
    <h3>runs</h3>
    <ul>
      <g:each in="${projectRunFolders}" var="${runFolder}">
        <li>
          <i class="icon-tasks"></i> 
          <g:link action="run" params="[project: projectFolder.name.encodeAsSHA1(), run: runFolder.name.encodeAsSHA1()]">${runFolder.name}</g:link> 
        </li>
      </g:each>		
    </ul>
  </g:if>
</body>
</html>