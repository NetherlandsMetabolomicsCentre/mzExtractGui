<!DOCTYPE html>
<html>
    <head>
        <meta name="layout" content="main"/>
    </head>
    <body>
        <h2>Extract
            <g:link controller="data" action="folder" params="[dataFolderKey:dataFolder.key]">${dataFolder.name}</g:link>
            <small>(${extractionFolder.name})</small>
        </h2>
        <common:runExtractButton dataFolder="${dataFolder}"  extractionFolder="${extractionFolder}" />
        <common:settingsExtractButton dataFolder="${dataFolder}"  extractionFolder="${extractionFolder}" />

        <ul class="nav nav-tabs">
            <li class="active"><a href="#files" data-toggle="tab">files</a></li>
            <!--<li><a href="#alignments" data-toggle="tab">alignments</a></li>-->
            <li><a href="#combines" data-toggle="tab">combines</a></li>
        </ul>

        <div class="tab-content">
            <div class="tab-pane active" id="files">
              <data:dataFolder dataFolder="${extractionFolder}" />
            </div>
            <!--<div class="tab-pane" id="alignments">
              <common:alignButton dataFolder="${dataFolder}"  extractionFolder="${extractionFolder}" />
              <data:alignmentFolders dataFolder="${dataFolder}" extractionFolder="${extractionFolder}"/>
            </div>-->
            <div class="tab-pane" id="combines">
              <common:combineButton dataFolder="${dataFolder}" extractionFolder="${extractionFolder}" />            
              <data:combineFolders dataFolder="${dataFolder}" extractionFolder="${extractionFolder}"/>              
            </div>
        </div>                  
    </body>
</html>
