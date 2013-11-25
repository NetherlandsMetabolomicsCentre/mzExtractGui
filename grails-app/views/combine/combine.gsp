<!DOCTYPE html>
<html>
    <head>
        <meta name="layout" content="main"/>
    </head>
    <body>
        <h2>Combine
            <g:link controller="data" action="folder" params="[dataFolderKey:dataFolder.key]">${dataFolder.name}</g:link>
            <small> / ${extractFolder.name}</small>
        </h2>

        <common:combineButtons dataFolderKey="${dataFolder.key}" extractFolderKey="${extractFolder.key}" alignFolderKey="${alignFolder.key}" combineFolderKey="${combineFolder.key}" />

        <h3>${combineFolder.name}</h3>
        <table class="page-table">
          <tr class="page-tr">
            <td class="page-left">
              <data:dataFolder dataFolder="${dataFolder}" extractFolder="${extractFolder}" alignFolder="${alignFolder}" combineFolder="${combineFolder}" />
            </td>
            <td class="page-right">
                <data:combineReportData combineFolder="${combineFolder}" />
            </td>
          </tr>
        </table>
    </body>
</html>
