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
        <common:runButton dataFolder="${dataFolder}"  extractionFolder="${extractionFolder}" />
        <common:settingsButton dataFolder="${dataFolder}"  extractionFolder="${extractionFolder}" />
        <data:dataFolder dataFolder="${extractionFolder}" />
    </body>
</html>
