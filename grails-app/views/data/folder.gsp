<!DOCTYPE html>
<html>
    <head>
        <meta name="layout" content="main"/>
    </head>
    <body>
        <h2>${dataFolder.name}</h2>
        <common:extractButton dataFolder="${dataFolder}" />
        <data:dataFolder dataFolder="${dataFolder}" />
        <data:extractionFolders dataFolder="${dataFolder}" />
    </body>
</html>
