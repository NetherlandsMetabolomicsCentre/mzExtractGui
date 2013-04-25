<!DOCTYPE html>
<html>
  <head>
    <meta name="layout" content="main"/>
  </head>
  <body>
    
    <div style="float: right;"><mzextract:settingsForm settings="${settings}" project="${project}" run="${run}"/></div>
    
    <h2><g:link action="project" params="[project: project.name.encodeAsSHA1()]">${project.name}</g:link> <small>${run.name.replace('_',' ')}</small></h2>    
        
    <mzextract:projectRunButtons project="${project}" run="${run}" />
    
    <g:if test="${flash.message}">
      <p><font color="red">${flash.message}</font></p>
    </g:if>
    
    <table>
      <tr>
        <td valign="top">

          <h3>input files</h3>
          <ul> 
            <g:each in="${inputFiles.sort()}" var="${inputFile}">
              <li><i class="icon-signal"></i> ${inputFile.name} (${new Date(inputFile.lastModified())})</li>
            </g:each>
          </ul>
        </td>
        <td valign="top">
          <h3>output files</h3>
          <ul>
            <g:each in="${outputFiles.sort()}" var="${outputFile}">
              <li><i class="icon-download"></i> <g:link action="download" id="${(outputFile.canonicalPath).encodeAsBase64().toString()}">${outputFile.name}</g:link> (${new Date(outputFile.lastModified())})</li>
            </g:each>		
          </ul>          
        </td>
      </tr>
    </table>    
</body>
</html>