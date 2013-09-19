<!DOCTYPE html>
<html>
    <head>
        <meta name="layout" content="main"/>
    </head>
    <body>
        <h2>${dataFolder.name}</h2>
        <table class="page-table">
          <tr class="page-tr">
            <td class="page-left"><data:dataFolder dataFolder="${dataFolder}" /></td>
            <td class="page-right">
              <data:extractFolders dataFolder="${dataFolder}" />
            </td>
          </tr>              
        </table>              
    </body>
</html>
