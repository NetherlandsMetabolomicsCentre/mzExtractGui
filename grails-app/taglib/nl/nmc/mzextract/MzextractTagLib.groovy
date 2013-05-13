package nl.nmc.mzextract

class MzextractTagLib {
    
    static namespace = "mzextract"
    
    def projectService
    def runService    
    
    def settingsForm = { attrs, body ->
        
        def settings = attrs.settings
        def project = attrs.project
        def projectSha1 = project?.name?.encodeAsSHA1()
        def run = attrs.run
        def runSha1 = run?.name?.encodeAsSHA1()   
        
        //out << '<a href="#settings" role="button" class="btn" data-toggle="modal">settings <i class="icon-cog"></i></a>'

        out << g.form(name:"runProject", action:"run", params:[project:projectSha1, run:runSha1]) {

            out << '<div id="settings" class="modal hide fade" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">'
            out << '  <div class="modal-header">'
            out << '    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>'
            out << '    <h3 id="myModalLabel">Settings</h3>'
            out << '  </div>'
            out << '  <div class="modal-body">'

            out << '<table>'
                settings.each { name, setting ->
                    out << '    <tr>'
                    out << '        <td style="padding-right:15px;"><div style="margin: 10px" class="hint hint--right hint--rounded" data-hint="' + setting.help + '"><i class="icon-info-sign"></i></div> ' + setting.label + '</td>'
                    out << '        <td>'
                    switch(setting.type){
//                        case 'number':  out << g.field(type:"number", name:name, value:setting.value)
//                                        break;
                        case 'select':  out << g.select(name:name, optionKey:"value", optionValue:"label", from:setting.options, value:setting.value)
                                        break;  
                        default      :  out << g.field(type:"text", name:name, value:setting.value)
                                        break;
                    }
                    out << '        </td>'
                    out << '    </tr>'
                }
            out << '</table>'         
            out << '</div>'
            out << '<div class="modal-footer">'
            out << g.submitButton(name:"do", value:"Save changes", class:"btn btn-primary")
            out << '</div>'
            out << '</div>'
        }              
          
    }

    def projectRuns = { attrs, body ->
                
        def project = attrs.project
        def projectSha1 = project.name.encodeAsSHA1()       
        def projectRunFolders = projectService.runFoldersFromProjectFolder(project)
        
        out << '<h3>runs</h3>'
        out << '<ul>'        
        projectRunFolders.each { run -> 
            out << projectRun(project: project, run: run)
        }
        out << '</ul>'         
    }
    
    def projectRun = { attrs, body ->
        
        def project = attrs.project
        def projectSha1 = project.name.encodeAsSHA1()
        def run = attrs.run
        def runSha1 = run.name.encodeAsSHA1()

        out << '<li>'   
        out << '  <div class="hint  hint--right hint--rounded" data-hint="view">'
        out <<      g.link(action:'run', params:[project: projectSha1, run: runSha1]) { '<i class="icon-tasks"></i>' } 
        out << '  </div>'          
        out << '&nbsp;'        
        out <<    run.name.replace('_',' ')
        out << '&nbsp;'        
        out << '  <div class="hint  hint--right hint--rounded" data-hint="status: 10=New, 11=Stopped, 20=Waiting, 30=Running, 40=Finished, -1=Failed">'
        out <<      runService.status(projectSha1,runSha1)
        out << '  </div>'        
        out << '</li>'
        
    }
    
    def runDetails = { attrs, body ->
        
        def projectSha1 = attrs.projectSha1
        def project = projectService.projectFolderFromSHA1EncodedProjectName(projectSha1)
        def runSha1 = attrs.runSha1
        def run = projectService.runFolderFromSHA1EncodedProjectNameAndRunName(projectSha1, runSha1)
        
        out << '<div style="height:50px;">'        
        out <<      projectRunButtons(project: project, run: run)       
        out << '</div>'

        //def outputFiles = projectService.runFolderOutputFilesFromRunFolder(run)
        def outputFiles = projectService.runFolderFilesFromRunFolder(run)
        def inputFiles = projectService.mzxmlFilesFromProjectFolder(project)
        
        out << '<table width="100%">'
        out << '    <tr>'
        out << '        <td width="1" valign="top">'
        out << '            <h3>input files</h3>'
        out << '            <ul>'
        
        inputFiles.sort().each { inputFile ->
            out << '    <li>'
            out << '        <div class="hint  hint--right hint--rounded" data-hint="' + new Date(inputFile.lastModified()) + '">'
            out << '            <i class="icon-signal"></i> '
            out <<              g.link(action:"download", id:(inputFile.canonicalPath).encodeAsBase64().toString()) { inputFile.name }
            out << '        </div>'
            out << '    </li>'
        }
        out << '            </ul>'
        out << '        </td>'
        out << '        <td width="1" valign="top">'
        out << '            <h3>output files</h3>'
        out << '            <ul>'
        
        outputFiles.sort().each { outputFile ->
            out << '    <li>'
            out << '        <div class="hint  hint--right hint--rounded" data-hint="' + new Date(outputFile.lastModified()) + '">'
            out << '            <i class="icon-download"></i> '
            out <<              g.link(action:"download", id:(outputFile.canonicalPath).encodeAsBase64().toString()) { outputFile.name }
            out << '        </div>'
            out << '    </li>'
        }
        
        out << '            </ul>'
        out << '        </td>'
        out << '    </tr>'
        out << '</table>' 
    }
    
    def projectRunButtons = { attrs, body ->
        
        def project = attrs.project
        def projectSha1 = project.name.encodeAsSHA1()
        def run = attrs.run
        def runSha1 = run.name.encodeAsSHA1()
        
        def status = runService.status(projectSha1, runSha1) as int
         
        out << '&nbsp;' 
        out << '<div class="hint hint--right hint--rounded" data-hint="settings">'
        if (status < 20 || status >= 40 ){
            out <<      '<a href="#settings" role="button" class="btn btn-info btn-large" data-toggle="modal"><i class="icon-cog"></i></a>'
        } else {
            out <<      '<a href="" role="button" disabled="disabled" class="btn btn-large" data-toggle="modal"><i class="icon-cog"></i></a>'
        }
        out << '</div>'
        out << '&nbsp;'
        out << '<div class="hint hint--right hint--rounded" data-hint="start">'
        if (status < 20 || status >= 40 ){        
            out <<      g.link(action:'queue', params:[project: projectSha1, run: runSha1], class:"btn btn-success btn-large", onclick:"return confirm('Are you sure you want to start this run? Existing data will be deleted first!')") { '<i class="icon-play"></i>' }         
        } else {
            out <<      '<a href="" role="button" disabled="disabled" class="btn btn-large" data-toggle="modal"><i class="icon-play"></i></a>'
        }
        out << '</div>'        
        out << '&nbsp;'        
        out << '<div class="hint hint--right hint--rounded" data-hint="stop">'
        if (status >= 20 && status < 40 ){        
            out <<      g.link(action:'stoprun', params:[project: projectSha1, run: runSha1], class:"btn btn-large btn-warning", onclick:"return confirm('Are you sure you want to stop this run?')") { '<i class="icon-stop"></i>' }
        } else {
            out <<      '<a href="" role="button" disabled="disabled" class="btn btn-large" data-toggle="modal"><i class="icon-stop"></i></a>'            
        }
        out << '</div>' 
        out << '&nbsp;'                
        out << '<div class="hint hint--right hint--rounded" data-hint="status">'
        out <<      runStatus(projectSha1:projectSha1, runSha1:runSha1)
        out << '</div>'         
        out << '&nbsp;'        
        out << '<div class="hint hint--right hint--rounded" data-hint="delete">'
        if (status < 20 || status >= 40 ){        
            out <<      g.link(action:'delrun', params:[project: projectSha1, run: runSha1], class:"btn btn-large btn-danger", onclick:"return confirm('Are you sure you want to delete this run?')") { '<i class="icon-remove-sign"></i>' }
        } else {
            out <<      '<a href="" role="button" disabled="disabled" class="btn btn-large" data-toggle="modal"><i class="icon-remove-sign"></i></a>'
        }
        out << '</div>' 
    }
    
    def runStatus = { attrs, body ->        
        
        def status = runService.status(attrs.projectSha1, attrs.runSha1) ?: ''
        
        out << '<button disabled class="btn btn-large" type="button" style="width:200px;'
        
        switch(status){
            case '-1'   :    out << '"> failed <i class="icon-warning-sign"></i>'; break;            
            case '11'   :    out << '"> stopped <i class="icon-stop"></i>'; break;            
            case '20'   :    out << '"> waiting <i class="icon-time"></i>'; break;            
            case '30'   :    out << '"> running <img id="spinner" width="18px" src="' + resource(dir: 'images', file: 'spin.gif') + '" alt="Spinner"/>'; break;                            
            case '40'   :    out << '"> finished <i class="icon-ok"></i>'; break;                
            default     :    out << '"> new <i class="icon-plus"></i>';
        } 
        
        out << '    </button>'
    }    
}
