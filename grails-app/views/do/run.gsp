<!DOCTYPE html>
<html>
  <head>
    <meta name="layout" content="main"/>
  </head>
  <body>
    
    <mzextract:settingsForm settings="${settings}" project="${project}" run="${run}"/>
    
    <h2><g:link action="project" params="[project: project.name.encodeAsSHA1()]">${project.name}</g:link> <small>${run.name.replace('_',' ')}</small></h2>

    <g:if test="${flash.message}">
      <p><font color="red">${flash.message}</font></p>
    </g:if>
    
    <div style="height:50px;" id="runDetails">
      <mzextract:runDetails projectSha1="${project.name.encodeAsSHA1()}" runSha1="${run.name.encodeAsSHA1()}" />    
    </div>
    
    <script type="text/javascript">
        function AjaxReLoad(){
            var xmlHttp;
            try{ xmlHttp=new XMLHttpRequest(); } // Firefox, Opera 8.0+, Safari
            catch (e){
                try{ xmlHttp=new ActiveXObject("Msxml2.XMLHTTP"); } // Internet Explorer
                catch (e){
                    try{ xmlHttp=new ActiveXObject("Microsoft.XMLHTTP"); }
                    catch (e){ return false;
                    }
                }
            }

            xmlHttp.onreadystatechange=function(){
                if(xmlHttp.readyState==4){
                    document.getElementById('runDetails').innerHTML=xmlHttp.responseText;
                    setTimeout('AjaxReLoad()',2500);
                }
            }

            xmlHttp.open("GET","${g.createLink(controller: 'do', action: 'runDetails', params:[projectSha1:project.name.encodeAsSHA1() , runSha1:run.name.encodeAsSHA1()], base: resource(dir:''))}",true);
            xmlHttp.send(null);
        }

        window.onload=function(){ setTimeout('AjaxReLoad()',100); }
    </script>  
</body>
</html>