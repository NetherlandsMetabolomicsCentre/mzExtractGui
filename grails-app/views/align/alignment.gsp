<!DOCTYPE html>
<html>
    <head>
        <meta name="layout" content="main"/>
    </head>
    <body>
        <h2>Align
            <g:link controller="data" action="folder" params="[dataFolderKey:dataFolder.key]">${dataFolder.name}</g:link>
            <small>(${extractFolder.name})</small>
        </h2>
        <h3>${alignFolder.name}</h3>
        <common:runAlignButton dataFolder="${dataFolder}"  extractFolder="${extractFolder}" alignFolder="${alignFolder}" />
        <common:settingsAlignButton dataFolder="${dataFolder}"  extractFolder="${extractFolder}" alignFolder="${alignFolder}" />
        <common:combineButton dataFolder="${dataFolder}"  extractFolder="${extractFolder}" />
        <data:dataFolder dataFolder="${alignFolder}" />
    </body>
</html>
