<!DOCTYPE html>
<html>
  <head>
    <meta name="layout" content="main"/>
  </head>
  <body>
    
    <mzextract:settingsForm settings="${settings}" project="${project}" run="${run}"/>
    
    <h2><g:link action="project" params="[project: project.name.encodeAsSHA1()]">${project.name}</g:link> <small>${run.name.replace('_',' ')}</small></h2>

    <div style="height:25px;">
      <p><font color="red">${flash.message}</font></p>
    </div>    
    
    <div style="height:50px;" id="runDetails">
      <mzextract:runDetails projectSha1="${project.name.encodeAsSHA1()}" runSha1="${run.name.encodeAsSHA1()}" />    
    </div>
    
    <g:set var="runDetailsUrl" value="${g.createLink(controller: 'do', action: 'runDetails', params:[projectSha1:project.name.encodeAsSHA1() , runSha1:run.name.encodeAsSHA1()], base: resource(dir:''))}" /> 
    <script>
     $(document).ready(function() {
         $("#runDetails").load("${runDetailsUrl}");
       var refreshId = setInterval(function() {
          $("#runDetails").load('${runDetailsUrl}');
       }, 2500);
       $.ajaxSetup({ cache: false });
    });
    </script>    
    <div id="runDetails">
    </div>      
</body>
</html>