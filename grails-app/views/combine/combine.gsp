<!DOCTYPE html>
<html>
    <head>
        <meta name="layout" content="main"/>
    </head>
    <body>
        <h2>Combine
            <g:link controller="data" action="folder" params="[dataFolderKey:dataFolder.key]">${dataFolder.name}</g:link>
            <small>(${extractionFolder.name})</small>
        </h2>
        <h3>${combineFolder.name}</h3>
        <common:runCombineButton dataFolder="${dataFolder}"  extractionFolder="${extractionFolder}" alignmentFolder="${alignmentFolder}" combineFolder="${combineFolder}" />
        <common:settingsCombineButton dataFolder="${dataFolder}"  extractionFolder="${extractionFolder}" alignmentFolder="${alignmentFolder}" combineFolder="${combineFolder}" />
        <data:dataFolder dataFolder="${combineFolder}" />
    </body>
</html>
