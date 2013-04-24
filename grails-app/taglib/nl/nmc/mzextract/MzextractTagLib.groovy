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
        
        out << g.form(name:"runProject", action:"run") {
            
            out << '<table>'
        
            settings.each { setting ->
                out << '    <tr style="padding: 10px">'
                out << '        <td>' + setting.label + '</td>'
                out << '        <td>'
                switch(setting.type){
                    case 'number':  out << g.field(type:"number", name:setting.name, value:params[setting.name] ?: setting.default)
                                    break;
                    case 'select':  out << g.select(name:setting.name, from:setting.options, value:params[setting.name] ?: setting.default)
                                    break;  
                    default      :  out << g.field(type:"text", name:setting.name, value:params[setting.name] ?: setting.default)
                                    break;
                }
                out << '        </td>'
                out << '        <td><div style="margin: 10px" class="hint  hint--right hint--rounded" data-hint="' + setting.help + '"> help <i class="icon-info-sign"></i></div></td>'
                out << '    </tr>'
            }
            out << '</table>'
            out << g.field(type:"hidden", name:"project", value: projectSha1)
            out << g.field(type:"hidden", name:"run", value: runSha1)            
            out << g.submitButton(name:"do", value:"save")
        }
    }

    def projectRuns = { attrs, body ->
        
        println 'Hello there!'
        
        def project = attrs.project
        println project
        def projectSha1 = project.name.encodeAsSHA1()
        println projectSha1
        
        def projectRunFolders = projectService.runFoldersFromProjectFolder(project)
        println projectRunFolders
        
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
        out << '  <div class="hint  hint--right hint--rounded" data-hint="status: 0=Waiting, 1=Queued, 3=Running, 4=Done, -1=Failed">'
        out <<      runService.status(projectSha1,runSha1)
        out << '  </div>'   
        out << '&nbsp;'
        out << '  <div class="hint  hint--right hint--rounded" data-hint="view">'
        out <<      g.link(action:'run', params:[project: projectSha1, run: runSha1]) { '<i class="icon-tasks"></i>' } 
        out << '  </div>'          
        out << '&nbsp;'        
        out << '  <div class="hint  hint--right hint--rounded" data-hint="run">'
        out <<      g.link(action:'queue', params:[project: projectSha1, run: runSha1], onclick:"return confirm('Are you sure you want to start this run? Existing data will be deleted first!')") { '<i class="icon-fast-forward"></i>' }         
        out << '  </div>'
        out << '&nbsp;'
        out << '  <div class="hint  hint--right hint--rounded" data-hint="delete">'
        out <<      g.link(action:'delrun', params:[project: projectSha1, run: runSha1], onclick:"return confirm('Are you sure you want to delete this run?')") { '<i class="icon-remove-sign"></i>' }
        out << '  </div>'                  
        out << '&nbsp;'
        out <<    run.name.replace('_',' ')
        out << '</li>'
        
    }
}
