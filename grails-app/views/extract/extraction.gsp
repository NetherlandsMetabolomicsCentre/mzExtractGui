<!DOCTYPE html>
<html>
    <head>
        <meta name="layout" content="main"/>
    </head>
    <body>
        <h2>Extract
            <g:link controller="data" action="folder" params="[dataFolderKey:dataFolder.key]">${dataFolder.name}</g:link>
            <small> / ${extractFolder.name}</small>
        </h2>

        <common:extractButtons dataFolderKey="${dataFolder.key}" extractFolderKey="${extractFolder.key}" />

        <table class="page-table">
          <tr class="page-tr">
            <td class="page-left">
              <data:dataFolder dataFolder="${dataFolder}" extractFolder="${extractFolder}" />
            </td>
            <td class="page-right">
              <data:combineFolders dataFolder="${dataFolder}" extractFolder="${extractFolder}"/>
            </td>
          </tr>
        </table>
    </body>
</html>
