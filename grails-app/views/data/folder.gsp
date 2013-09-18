<!DOCTYPE html>
<html>
    <head>
        <meta name="layout" content="main"/>
    </head>
    <body>
        <h2>${dataFolder.name}</h2>        
        <ul class="nav nav-tabs">
            <li class="active"><a href="#files" data-toggle="tab">files</a></li>
            <li><a href="#extractions" data-toggle="tab">extractions</a></li>
        </ul>

        <div class="tab-content">
            <div class="tab-pane active" id="files">
              <data:dataFolder dataFolder="${dataFolder}" />
            </div>
            <div class="tab-pane" id="extractions">
              <common:extractButton dataFolder="${dataFolder}" />
              <data:extractionFolders dataFolder="${dataFolder}" />
            </div>          
        </div>      
    </body>
</html>
