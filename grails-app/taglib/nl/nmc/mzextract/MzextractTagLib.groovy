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
                        case 'number':  out << g.field(type:"number", name:name, value:setting.value)
                                        break;
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
    
    def projectRunButtons = { attrs, body ->
        
        def project = attrs.project
        def projectSha1 = project.name.encodeAsSHA1()
        def run = attrs.run
        def runSha1 = run.name.encodeAsSHA1()
 
        out << '<div class="hint hint--right hint--rounded" data-hint="settings">'
        out <<      '<a href="#settings" role="button" class="btn btn-large" data-toggle="modal"><i class="icon-cog"></i></a>'
        out << '</div>'
        out << '&nbsp;'
        out << '<div class="hint hint--right hint--rounded" data-hint="start">'
        out <<      g.link(action:'queue', params:[project: projectSha1, run: runSha1], class:"btn btn-large", onclick:"return confirm('Are you sure you want to start this run? Existing data will be deleted first!')") { '<i class="icon-play"></i>' }         
        out << '</div>'        
        out << '&nbsp;'        
        out << '<div class="hint hint--right hint--rounded" data-hint="stop">'
        out <<      g.link(action:'stoprun', params:[project: projectSha1, run: runSha1], class:"btn btn-large", onclick:"return confirm('Are you sure you want to stop this run?')") { '<i class="icon-stop"></i>' }
        out << '</div>'        
        out << '&nbsp;'        
        out << '<div class="hint hint--right hint--rounded" data-hint="delete">'
        out <<      g.link(action:'delrun', params:[project: projectSha1, run: runSha1], class:"btn btn-large btn-danger", onclick:"return confirm('Are you sure you want to delete this run?')") { '<i class="icon-remove-sign"></i>' }
        out << '</div>' 
    }    
}
