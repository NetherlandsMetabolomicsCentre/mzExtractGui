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
        <!--
          <common:runCombineButton dataFolder="${dataFolder}"  extractFolder="${extractFolder}" alignFolder="${alignFolder}" combineFolder="${combineFolder}" />
          <common:settingsCombineButton dataFolder="${dataFolder}"  extractFolder="${extractFolder}" alignFolder="${alignFolder}" combineFolder="${combineFolder}" />
        -->
        <data:dataFolder dataFolder="${dataFolder}" extractFolder="${extractFolder}" alignFolder="${alignFolder}" combineFolder="${combineFolder}"/>
    </body>
</html>
