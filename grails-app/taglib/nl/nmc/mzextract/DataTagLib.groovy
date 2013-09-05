package nl.nmc.mzextract

import org.codehaus.groovy.grails.commons.ConfigurationHolder

class DataTagLib {

    static namespace = "data"

    def dataService
    def extractService

    // include configuration of mzExtract
    def config = ConfigurationHolder.config.mzextract


    // list all top-level data folders
    def dataFolders = { attrs, body ->

        def dataFolders = attrs.dataFolders

        out << '<ul>'
        dataFolders.each { k, f ->
                out << '<li>'
                out << '    <i class="icon-th-list"></i> '
                out <<      g.link(controller: 'data', action:"folder", params:[dataFolderKey: f.key]){ f.name }
                out << '</li>'
        }
        out << '</ul>'

    }


    // display a single datafolder
    def dataFolder = { attrs, body ->

        def dataFolder = attrs.dataFolder

        dataFolder.files.keySet().each { ext ->

            if (ext in config.exepted.extensions){
                out << "<h3>${ext}</h3>"
                out << "<ul>"
                dataFolder.files[ext].each { f ->
                    out << '<li>' + dataFile(dataFile: f) + '</li>'
                }
                out << '</ul>'
            }
        }
    }


    // display a single data file
    def dataFile = { attrs, body ->

        def dataFile = attrs.dataFile

        out <<  g.link(controller: 'data', action:"download", id:dataFile.relpathencoded) { '<i class="icon-download"></i> ' }
        out <<  dataFile.name
    }

    // form to select mzxml files
    def mzxmlSelect = { attrs, body ->

        def dataFolder = attrs.dataFolder

        out << '''<script language="JavaScript">
                            <!-- Begin
                                function checkAll(field){ for (i = 0; i < field.length; i++) field[i].checked = true ; }
                                function uncheckAll(field){ for (i = 0; i < field.length; i++) field[i].checked = false ; }
                            //  End -->
                        </script>'''

        out << g.form(name:"selectMzxml", action:"select", controller: "extract", params:[dataFolderKey: dataFolder.key], method:"POST") {
            out << '<h4>select mzXML files for extraction</h4>'
            out << g.checkBox(name:'mzxmlfiles', style:'display:none', checked: false, value:"")
            out << '<br />'
            out << '<div style="margin: 10px;">'

            // list all available mzxml files
            out << '<table class="table table-striped">'
            out << '    <tr>'
            out << '        <td colspan="2">'
            out << '            <input type=submit name="next" value="next" class="btn">'
            out << '            <input type=button name="CheckAll" value="select all" class="btn btn-success" onClick="checkAll(document.selectMzxml.mzxmlfiles)"> '
            out << '            <input type=button name="UnCheckAll" value="deselect all" class="btn btn-inverse" onClick="uncheckAll(document.selectMzxml.mzxmlfiles)">'
            out << '        </td>'
            out << '    </tr>'
            dataFolder.files['mzxml'].each { f ->
                out << '<tr>'
                out <<      '<td>' + g.checkBox(name:'mzxmlfiles', checked: false, value:f.key) + '</td>'
                out <<      '<td>' + f.name + '</td>'
                out << '</tr>'
            }
            out << '    <tr>'
            out << '        <td colspan="2">'
            out << '            <input type=submit name="next" value="next" class="btn">'
            out << '            <input type=button name="CheckAll" value="select all" class="btn btn-success" onClick="checkAll(document.selectMzxml.mzxmlfiles)"> '
            out << '            <input type=button name="UnCheckAll" value="deselect all" class="btn btn-inverse" onClick="uncheckAll(document.selectMzxml.mzxmlfiles)">'
            out << '        </td>'
            out << '    </tr>'
            out << '</table>'

            out << '</div>'
        }

    }

    // form to select mat files
    def matSelect = { attrs, body ->

        def controller = attrs.controller
        def action = attrs.action
        def dataFolder = attrs.dataFolder
        def extractionFolder = attrs.extractionFolder
        def alignmentFolder = attrs.alignmentFolder

        out << '''<script language="JavaScript">
                            <!-- Begin
                                function checkAll(field){ for (i = 0; i < field.length; i++) field[i].checked = true ; }
                                function uncheckAll(field){ for (i = 0; i < field.length; i++) field[i].checked = false ; }
                            //  End -->
                        </script>'''

        out << g.form(name:"selectMat", action:action, controller:controller, params:[dataFolderKey: dataFolder.key, extractionFolderKey: extractionFolder.key, alignmentFolderKey: alignmentFolder?.key ?: null], method:"POST") {
            out << '<h4>select matlab files for alignment</h4>'
            out << g.checkBox(name:'matfiles', style:'display:none', checked: false, value:"")
            out << '<br />'
            out << '<div style="margin: 10px;">'

            // list all available mat files
            out << '<table class="table table-striped">'
            out << '    <tr>'
            out << '        <td colspan="2">'
            out << '            <input type=submit name="next" value="next" class="btn">'
            out << '            <input type=button name="CheckAll" value="select all" class="btn btn-success" onClick="checkAll(document.selectMat.matfiles)"> '
            out << '            <input type=button name="UnCheckAll" value="deselect all" class="btn btn-inverse" onClick="uncheckAll(document.selectMat.matfiles)">'
            out << '        </td>'
            out << '    </tr>'
            extractionFolder.files['mat'].each { f ->
                out << '<tr>'
                out <<      '<td>' + g.checkBox(name:'matfiles', checked: false, value:f.key) + '</td>'
                out <<      '<td>' + f.name + '</td>'
                out << '</tr>'
            }
            out << '    <tr>'
            out << '        <td colspan="2">'
            out << '            <input type=submit name="next" value="next" class="btn">'
            out << '            <input type=button name="CheckAll" value="select all" class="btn btn-success" onClick="checkAll(document.selectMat.matfiles)"> '
            out << '            <input type=button name="UnCheckAll" value="deselect all" class="btn btn-inverse" onClick="uncheckAll(document.selectMat.matfiles)">'
            out << '        </td>'
            out << '    </tr>'
            out << '</table>'

            out << '</div>'
        }

    }

    def extractionFolders = { attrs, body ->

        def dataFolder = attrs.dataFolder
        def extractionFolders = extractService.extractionFolders(dataFolder.key)

        out << '<ul>'
        extractionFolders.each { executionFolder ->
                out << '<li>'
                out << '    <i class="icon-th-list"></i> '
                out <<      g.link(controller: 'extract', action:"extraction", params:[dataFolderKey: dataFolder.key, extractionFolderKey: executionFolder.key]){ executionFolder.name }
                out <<      common.deleteButton(dataFolder:dataFolder, extractionFolder: executionFolder)
                out << '</li>'
        }
        out << '</ul>'

    }
}
