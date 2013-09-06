<!DOCTYPE html>
<html>
    <head>
        <meta name="layout" content="main"/>
    </head>
    <body>
        <h2>Extract
            <g:link controller="data" action="folder" params="[dataFolderKey:dataFolder.key]">${dataFolder.name}</g:link>
            <small>(${extractionFolder.name})</small>
        </h2>
        <common:runExtractButton dataFolder="${dataFolder}"  extractionFolder="${extractionFolder}" />
        <common:settingsExtractButton dataFolder="${dataFolder}"  extractionFolder="${extractionFolder}" />
        <common:alignButton dataFolder="${dataFolder}"  extractionFolder="${extractionFolder}" />
        <common:combineButton dataFolder="${dataFolder}" extractionFolder="${extractionFolder}" />
        <data:dataFolder dataFolder="${extractionFolder}" />
        <data:alignmentFolders dataFolder="${dataFolder}" extractionFolder="${extractionFolder}"/>
        <data:combineFolders dataFolder="${dataFolder}" extractionFolder="${extractionFolder}"/>
    </body>
</html>
