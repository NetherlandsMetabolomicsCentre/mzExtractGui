<!DOCTYPE html>
<html>
    <head>
        <meta name="layout" content="main"/>
    </head>
    <body>
        <h2>
            <g:link controller="data" action="folder" params="[dataFolderKey:dataFolder.key]">${dataFolder.name}</g:link>
            <small>(${extractionFolder.name})</small>
        </h2>
        <h3>${combineFolder.name}</h3>
        <common:runCombineButton dataFolder="${dataFolder}"  extractionFolder="${extractionFolder}" alignmentFolder="${alignmentFolder}" combineFolder="${combineFolder}" />
        <common:settingsButton dataFolder="${dataFolder}"  extractionFolder="${extractionFolder}" />
        <data:dataFolder dataFolder="${combineFolder}" />
    </body>
</html>
