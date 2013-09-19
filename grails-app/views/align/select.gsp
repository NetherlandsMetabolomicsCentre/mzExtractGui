<!DOCTYPE html>
<html>
    <head>
        <meta name="layout" content="main"/>
    </head>
    <body>
        <h2>Align ${dataFolder.name}</h2>
        <h3>${extractFolder.name}</h3>
        <data:matSelect dataFolder="${dataFolder}" controller="align" action="select" extractFolder="${extractFolder}" />
    </body>
</html>
