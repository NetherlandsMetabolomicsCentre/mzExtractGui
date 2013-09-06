<!DOCTYPE html>
<html>
    <head>
        <meta name="layout" content="main"/>
    </head>
    <body>
        <h2>Combine ${dataFolder.name}</h2>
        <h3>${extractionFolder.name}</h3>
        <h4>${alignmentFolder?.name ?: ":)"}</h4>
        <data:matSelect dataFolder="${dataFolder}" controller="combine" action="select" extractionFolder="${extractionFolder}" alignmentFolder="${alignmentFolder}" />
    </body>
</html>
