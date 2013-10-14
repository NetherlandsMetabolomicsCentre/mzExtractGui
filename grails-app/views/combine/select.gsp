<!DOCTYPE html>
<html>
    <head>
        <meta name="layout" content="main"/>
    </head>
    <body>
        <h2>Combine ${dataFolder.name}</h2>
        <h3>${extractFolder.name}</h3>
        <g:if test="${alignFolder}"><h4>${alignFolder}</h4></g:if>
        <data:matSelect dataFolder="${dataFolder}" controller="combine" action="select" extractFolder="${extractFolder}" alignFolder="${alignFolder}" />
    </body>
</html>
