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
        <h3>${alignmentFolder.name}</h3>
        <common:runAlignButton dataFolder="${dataFolder}"  extractionFolder="${extractionFolder}" alignmentFolder="${alignmentFolder}" />
        <common:settingsButton dataFolder="${dataFolder}"  extractionFolder="${extractionFolder}" />
        <common:combineButton dataFolder="${dataFolder}"  extractionFolder="${extractionFolder}" />
        <data:dataFolder dataFolder="${alignmentFolder}" />
    </body>
</html>