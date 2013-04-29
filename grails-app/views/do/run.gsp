<!DOCTYPE html>
<html>
  <head>
    <meta name="layout" content="main"/>
  </head>
  <body>
    
    <mzextract:settingsForm settings="${settings}" project="${project}" run="${run}"/>
    
    <h2><g:link action="project" params="[project: project.name.encodeAsSHA1()]">${project.name}</g:link> <small>${run.name.replace('_',' ')}</small></h2>
        
    <mzextract:projectRunButtons project="${project}" run="${run}" />
    
    <g:if test="${flash.message}">
      <p><font color="red">${flash.message}</font></p>
    </g:if>
    
    <table width="100%">
      <tr>
        <td width="1" valign="top">

          <h3>input files</h3>
          <ul> 
            <g:each in="${inputFiles.sort()}" var="${inputFile}">
              <li>
                <div class="hint  hint--right hint--rounded" data-hint="${new Date(inputFile.lastModified())}">
                  <i class="icon-signal"></i> 
                  <g:link action="download" id="${(inputFile.canonicalPath).encodeAsBase64().toString()}">${inputFile.name}</g:link>
                </div>
              </li>
            </g:each>
          </ul>
        </td>
        <td width="1" valign="top">
          <h3>output files</h3>
          <ul>
            <g:each in="${outputFiles.sort()}" var="${outputFile}">
              <li>
                <div class="hint  hint--right hint--rounded" data-hint="${new Date(outputFile.lastModified())}">
                  <i class="icon-download"></i> 
                  <g:link action="download" id="${(outputFile.canonicalPath).encodeAsBase64().toString()}">${outputFile.name}</g:link>
                </div>
              </li>
            </g:each>		
          </ul>          
        </td>
      </tr>
    </table> 
</body>
</html>