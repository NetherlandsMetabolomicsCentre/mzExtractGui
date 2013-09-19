<!DOCTYPE html>
<html>
    <head>
        <meta name="layout" content="main"/>
    </head>
    <body>
        <h2>Combine ${dataFolder.name}</h2>
        <h3>${extractFolder.name}</h3>
        <h4>${alignFolder?.name ?: ":)"}</h4>
        <data:matSelect dataFolder="${dataFolder}" controller="combine" action="select" extractFolder="${extractFolder}" alignFolder="${alignFolder}" />
    </body>
</html>
