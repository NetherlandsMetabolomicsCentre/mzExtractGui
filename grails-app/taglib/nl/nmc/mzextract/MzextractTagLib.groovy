package nl.nmc.mzextract

class MzextractTagLib {
    
    static namespace = "mzextract"
    
    def projectService
    def runService    
    def mzxmlService    
    
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
            out << '  <div class="modal-body" style="min-height: 520px;">'

            out << '<table>'
                settings.each { name, setting ->
                    out << '    <tr>'
                    out << '        <td style="padding-right:15px;"><div style="margin: 5px" class="hint hint--top hint--rounded" data-hint="' + setting.help + '"><i class="icon-info-sign"></i></div> ' + setting.label + '</td>'
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
            if (runService.hasData(projectSha1, runSha1)){            
                out << g.submitButton(name:"do", value:"Save changes", class:"btn btn-primary", onclick:"return confirm('Are you sure you want to save the settings? Existing data will be deleted as the output files do not reflect the results from the new settings!')")
            } else {
                out << g.submitButton(name:"do", value:"Save changes", class:"btn btn-primary")
            }
            out << '</div>'
            out << '</div>'
        }              
          
    }
    
    def projectMzxmls = { attrs, body ->
                
        def project = attrs.project
        def mzxmlFiles = projectService.mzxmlFilesFromProjectFolder(project)
        
        out << '<table>'
        mzxmlFiles.each { mzxml ->
            
            def mzxmlMeta = mzxmlService.parseHeader(mzxml)
            def mzxmlSha1 = mzxml?.name?.encodeAsSHA1()
            
            out << '    <tr>'
            out << '        <td><i class="icon-signal"></i></td>'
            out << '        <td>' + mzxml.name + '</td>'
            out << '        <td><a href="#mzxml'+mzxmlSha1+'" data-toggle="modal"><i class="icon-info-sign"></i></a>'
            out << '            <div id="mzxml'+mzxmlSha1+'" class="modal hide fade" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">'
            out << '                <div class="modal-header">'
            out << '                    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>'
            out << '                    <h3 id="myModalLabel">mzXML header information</h3>'
            out << '                </div>'
            out << '                <div class="modal-body">'
            mzxmlMeta.each { k, v ->
                out << "<b>${k}</b>: ${v}<br />"
            }
            out << '                </div>'
            out << '                <div class="modal-footer"></div>'
            out << '            </div>'
              out << '        </td>'
            out << '    </tr>'
        }
        out << '</table>'
    }        

    def projectRuns = { attrs, body ->
                
        def project = attrs.project
        def projectSha1 = project.name.encodeAsSHA1()       
        def projectRunFolders = projectService.runFoldersFromProjectFolder(project)
                
        projectRunFolders.each { run -> 
            out << projectRun(project: project, run: run)
        }
    }
    
    def projectRun = { attrs, body ->
        
        def project = attrs.project
        def projectSha1 = project.name.encodeAsSHA1()
        def run = attrs.run
        def runSha1 = run.name.encodeAsSHA1()

        out << '<li>'                                       
        out << '  <div class="hint hint--bottom hint--rounded" data-hint="view">'
        out <<      g.link(action:'run', params:[project: projectSha1, run: runSha1], style:'text-decoration: none;') { 
                        '<i class="icon-tasks"></i> ' + 
                        run.name.replace('_',' ') +
                        '&nbsp;' +
                        runStatus(projectSha1: projectSha1, runSha1: runSha1, buttonSize: 'btn-mini', buttonWidth: '100px')
                    }       
        out << '  </div>'          
        out << '</li>'
        
    }
    
    def runDetails = { attrs, body ->
        
        def project = projectService.projectFolderFromSHA1EncodedProjectName(attrs.projectSha1)
        def run     = projectService.runFolderFromSHA1EncodedProjectNameAndRunName(attrs.projectSha1, attrs.runSha1)
        
        out << '<div style="height:50px;">'        
        out <<      projectRunButtons(project: project, run: run)       
        out << '</div>'
        
        out << '<table width="100%">'
        out << '    <tr>'
        out << '        <td width="1" valign="top">'
        out << '            <h3>input files</h3>'
        out << '            <ul>'
        
        projectService.mzxmlFilesFromProjectFolder(project)?.sort().each { inputFile ->
            out << '    <li>'
            out << '        <div class="hint hint--bottom hint--rounded" data-hint="' + new Date(inputFile.lastModified()) + '">'
            out << '            <i class="icon-signal"></i>'
            out << '        </div>&nbsp;'            
            out <<          g.link(action:"download", id:(inputFile.canonicalPath).encodeAsBase64().toString()) { inputFile.name }
            out << '    </li>'
        }
        out << '            </ul>'
        out << '        </td>'
        out << '        <td width="1" valign="top">'
        out << '            <h3>output files</h3>'
        out << '            <ul>'
        
        projectService.runFolderFilesFromRunFolder(run)?.sort().each { outputFile ->
            out << '    <li>'
            out << '        <div class="hint hint--bottom hint--rounded" data-hint="' + new Date(outputFile.lastModified()) + '">'
            out << '            <i class="icon-download"></i>'
            out << '        </div>&nbsp;'
            out <<          g.link(action:"download", id:(outputFile.canonicalPath).encodeAsBase64().toString()) { outputFile.name }            
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
        out << '<div class="hint hint--top hint--rounded" data-hint="settings">'
        if (status < 20 || status >= 40 ){
            out <<      '<a href="#settings" role="button" class="btn btn-info btn-large" data-toggle="modal"><i class="icon-cog"></i></a>'
        } else {
            out <<      '<a href="" role="button" disabled="disabled" class="btn btn-large"><i class="icon-cog"></i></a>'
        }
        out << '</div>'
        out << '&nbsp;'
        out << '<div class="hint hint--top hint--rounded" data-hint="start">'
        if (status < 20 || status >= 40 ){
            if (runService.hasData(projectSha1, runSha1)){
                out << g.link(action:'queue', params:[project: projectSha1, run: runSha1], class:"btn btn-success btn-large", onclick:"return confirm('Are you sure you want to start this run? Existing data will be deleted first!')") { '<i class="icon-play"></i>' }         
            } else {
                out << g.link(action:'queue', params:[project: projectSha1, run: runSha1], class:"btn btn-success btn-large") { '<i class="icon-play"></i>' }                         
            }
        } else {
            out <<      '<a href="" role="button" disabled="disabled" class="btn btn-large"><i class="icon-play"></i></a>'
        }
        out << '</div>'        
        out << '&nbsp;'        
        out << '<div class="hint hint--top hint--rounded" data-hint="stop">'
        if (status >= 20 && status < 40 ){        
            out <<      g.link(action:'stoprun', params:[project: projectSha1, run: runSha1], class:"btn btn-large btn-warning", onclick:"return confirm('Are you sure you want to stop this run?')") { '<i class="icon-stop"></i>' }
        } else {
            out <<      '<a href="" role="button" disabled="disabled" class="btn btn-large"><i class="icon-stop"></i></a>'            
        }
        out << '</div>' 
        out << '&nbsp;'                
        out << '<div class="hint hint--top hint--rounded" data-hint="delete">'
        if (status < 20 || status >= 40 ){        
            out <<      g.link(action:'delrun', params:[project: projectSha1, run: runSha1], class:"btn btn-large btn-danger", onclick:"return confirm('Are you sure you want to delete this run?')") { '<i class="icon-remove-sign"></i>' }
        } else {
            out <<      '<a href="" role="button" disabled="disabled" class="btn btn-large"><i class="icon-remove-sign"></i></a>'
        }
        out << '</div>'         
        out << '&nbsp;'        
        out << '<div class="hint hint--top hint--rounded" data-hint="status">'
        out <<      runStatus(projectSha1:projectSha1, runSha1:runSha1)
        out << '</div>'         
    }
    
    def runStatus = { attrs, body ->        
        
        def status = runService.status(attrs.projectSha1, attrs.runSha1) ?: ''
        
        def buttonSize = attrs.buttonSize ?: 'btn-large'
        def buttonWidth = attrs.buttonWidth ?: '200px'
        
        out << '<button disabled class="btn btn-link '+buttonSize+'" type="button" style="width:'+buttonWidth+';'
        
        switch(status){
            case '-1'   :    out << '"> failed <i class="icon-warning-sign"></i>'; break;            
            case '11'   :    out << '"> stopped <i class="icon-stop"></i>'; break;            
            case '20'   :    out << '"> waiting <i class="icon-time"></i>'; break;            
            case '30'   :    out << '"> running <img id="spinner" style="width:15px;" src="' + resource(dir: 'images', file: 'spin.gif') + '" alt="Spinner"/>'; break;                            
            case '40'   :    out << '"> finished <i class="icon-ok"></i>'; break;                
            default     :    out << '"> new <i class="icon-plus"></i>';
        } 
        
        out << '    </button>'
    }    
}
