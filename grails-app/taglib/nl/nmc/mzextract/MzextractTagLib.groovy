package nl.nmc.mzextract

class MzextractTagLib {
    
    static namespace = "mzextract"
    
    def settingsForm = { attrs, body ->
        
        def settings = attrs.settings
        def project = attrs.project
        
        
        out << g.form(name:"runProject", action:"settings") {
            
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
            out << g.field(type:"hidden", name:"project", value:project.name.encodeAsSHA1())
            out << g.submitButton(name:"do", value:"next...")
        }
    }

}
