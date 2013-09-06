<!DOCTYPE html>
<html>
    <head>
        <meta name="layout" content="main"/>
    </head>
    <body>
        <h2>Align ${dataFolder.name}</h2>
        <h3>${extractionFolder.name}</h3>
        <data:matSelect dataFolder="${dataFolder}" controller="align" action="select" extractionFolder="${extractionFolder}" />
    </body>
</html>
