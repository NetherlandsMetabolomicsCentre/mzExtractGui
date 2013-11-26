package nl.nmc.mzextract

import org.codehaus.groovy.grails.commons.ConfigurationHolder

class DataTagLib {

    static namespace = "data"

    def dataService
    def extractService
    def alignService
    def combineService

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

    def combineReportData = { attrs, body ->

        def combineFolder = attrs.combineFolder
        def combineFolderDataFile = new File(combineFolder.path)


            def jsonData

            // build some html
            def html = ''

            //rsdqcr graph
            def rsdqcrFile = new File(combineFolder.path, "${config.combine.outputfile}_summary_rsdqcrtar.txt")
            if (rsdqcrFile.exists()){

                def rsdqcrEntries = []
                def rsdqcrEntriesHeader = []
                rsdqcrFile.eachLine { rsdqcrLine ->

                    //if (rsdqcrEntries.size() <= 50){

                        def rsdqcrParts = rsdqcrLine.split("\t")
                        if (rsdqcrEntriesHeader == []){
                            rsdqcrEntriesHeader = rsdqcrParts
                        } else {
                            def entryHash = [:]
                            rsdqcrEntriesHeader.eachWithIndex { headerLabel, headerIdx ->
                                entryHash[headerLabel] = rsdqcrParts[headerIdx]
                            }
                            rsdqcrEntries << entryHash
                        }
                    //}
                }

                jsonData = '['
                rsdqcrEntries.each { rsdqcrEntry ->
                    jsonData += '{ '
                    rsdqcrEntry.each { l, v ->
                        jsonData += '"' + l + '":"'+ v +'", '
                    }
                    jsonData += '"EOL":"TRUE"'
                    jsonData += '}, '
                }
                jsonData += ']'

                html += """ <div id="rsdqcrSVG">
                                        <script type="text/javascript">
                                        var svg = dimple.newSvg("#rsdqcrSVG", 700, ${20 * rsdqcrEntries.size() + 200});

                                        var data = ${jsonData};
                                        var chart = new dimple.chart(svg, data);

                                        chart.setBounds(100, 50, 540, ${20 * rsdqcrEntries.size()})

                                        var x1Axis = chart.addMeasureAxis("x", "RSDQC (QC)");
                                        var x2Axis = chart.addMeasureAxis("x", "RSDRT (QC)");
                                        var yAxis = chart.addCategoryAxis("y", "COMPOUND");
                                        yAxis.addOrderRule("COMPOUND");
                                        var bars1 = chart.addSeries(null, dimple.plot.bar, [x1Axis,yAxis]);
                                        var bars2 = chart.addSeries(null, dimple.plot.bar, [x2Axis,yAxis]);
                                        chart.draw();
                                        </script>
                                    </div>
                """

                // reformat the data to a per compound list of RSDQC (QC), RSDRT (QC) and MEANRT(QC)
                jsonData = '['
                rsdqcrEntries.each { rsdqcrEntry ->
                    jsonData += """
                        {"COMPOUND":"${rsdqcrEntry['COMPOUND']}","Property":"RSDRT (QC)","Value":"${(rsdqcrEntry['RSDRT (QC)'] as Float)}"},
                        {"COMPOUND":"${rsdqcrEntry['COMPOUND']}","Property":"RSDQC (QC)","Value":"${(rsdqcrEntry['RSDQC (QC)'] as Float)}"},
                        {"COMPOUND":"${rsdqcrEntry['COMPOUND']}","Property":"MEANRT(QC)","Value":"${(rsdqcrEntry['MEANRT(QC)'] as Float)}"},
                    """
                }
                jsonData += ']'

                html += """ <div id="combiSVG">
                                        <script type="text/javascript">
                                        var svg = dimple.newSvg("#combiSVG", 700, ${20 * rsdqcrEntries.size() + 200});

                                        var data = ${jsonData};
                                        var chart = new dimple.chart(svg, data);

                                        chart.setBounds(120, 50, 520, ${20 * rsdqcrEntries.size()})
                                        var xAxis = chart.addMeasureAxis("x", "Value");
                                        //var xAxis = chart.addLogAxis("x", "Value");

                                        var yAxis = chart.addCategoryAxis("y", ["COMPOUND", "Property"]);
                                        yAxis.addOrderRule("COMPOUND");
                                        var bars = chart.addSeries("Property", dimple.plot.bar);
                                        bars.barGap = 0.5;
                                        var myLegend = chart.addLegend(10, 5, 540, 10, "right", bars);
                                        chart.draw();

                                        xAxis.titleShape.remove()

                                        chart.legends = [];

                                        // Get a unique list of Owner values to use when filtering
                                        var filterValues = dimple.getUniqueValues(data, "Property");
                                        // Get all the rectangles from our now orphaned legend
                                        myLegend.shapes.selectAll("rect")
                                          // Add a click event to each rectangle
                                          .on("click", function (e) {
                                            // This indicates whether the item is already visible or not
                                            var hide = false;
                                            var newFilters = [];
                                            // If the filters contain the clicked shape hide it
                                            filterValues.forEach(function (f) {
                                              if (f === e.aggField.slice(-1)[0]) {
                                                hide = true;
                                              } else {
                                                newFilters.push(f);
                                              }
                                            });
                                            // Hide the shape or show it
                                            if (hide) {
                                              d3.select(this).style("opacity", 0.2);
                                            } else {
                                              newFilters.push(e.aggField.slice(-1)[0]);
                                              d3.select(this).style("opacity", 0.8);
                                            }
                                            // Update the filters
                                            filterValues = newFilters;
                                            // Filter the data
                                            chart.data = dimple.filterData(data, "Property", filterValues);
                                            // Passing a duration parameter makes the chart animate. Without
                                            // it there is no transition
                                            chart.draw(800);
                                          });
                                        </script>
                                    </div>
                """

                // dump data to HTML
                html += "<div style='width:1000px;'><pre><small>${rsdqcrFile.text}</small></pre></div>"


            }



            out << html

    }

    // display a single datafolder
    def dataFolder = { attrs, body ->

        def dataFolder = attrs.dataFolder ?: null
        def extractFolder = attrs.extractFolder ?: null
        def alignFolder = attrs.alignFolder ?: null
        def combineFolder = attrs.combineFolder ?: null

        out << '<div id="filesList"></div>'

        def remoteUrl = g.createLink(controller: 'remote', action: 'filesList', params:[dataFolderKey:dataFolder?.key, extractFolderKey:extractFolder?.key, alignFolderKey:alignFolder?.key, combineFolderKey:combineFolder?.key], base: resource(dir:''))

        out << '<script>'
        out << ' $(document).ready(function() {'
        out << '     $("#filesList").load("' + remoteUrl + '");'
        out << '     var refreshId = setInterval(function() {'
        out << '     $("#filesList").load("' + remoteUrl + '");'
        out << '   }, 1000);'
        out << '   $.ajaxSetup({ cache: false });'
        out << '});'
        out << '</script>'
    }

    // display the files in a datafolder
    def dataFolderFiles = { attrs, body ->

        def folder = null

        def dataFolderKeys = attrs.dataFolderKeys

        if (dataFolderKeys['combineFolderKey']){
            folder = combineService.combineFolder(dataFolderKeys['dataFolderKey'], dataFolderKeys['extractFolderKey'], dataFolderKeys['alignFolderKey'] ?: null, dataFolderKeys['combineFolderKey'])
        } else if(dataFolderKeys['alignFolderKey']) {
           folder = alignService.alignFolder(dataFolderKeys['dataFolderKey'], dataFolderKeys['extractFolderKey'], dataFolderKeys['alignFolderKey'])
        } else if(dataFolderKeys['extractFolderKey']) {
            folder = extractService.extractFolder(dataFolderKeys['dataFolderKey'], dataFolderKeys['extractFolderKey'])
        } else if(dataFolderKeys['dataFolderKey']) {
            folder = dataService.dataFolder(dataFolderKeys['dataFolderKey'])
        }

        folder?.files?.keySet().each { ext ->

            if (ext in config.exepted.extensions){
                out << "<h3>${ext}</h3>"
                out << "<ul>"
                folder.files[ext].each { f ->
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
        def extractFolder = attrs.extractFolder
        def alignFolder = attrs.alignFolder

        out << '''<script language="JavaScript">
                            <!-- Begin
                                function checkAll(field){ for (i = 0; i < field.length; i++) field[i].checked = true ; }
                                function uncheckAll(field){ for (i = 0; i < field.length; i++) field[i].checked = false ; }
                            //  End -->
                        </script>'''

        out << g.form(name:"selectMat", action:action, controller:controller, params:[dataFolderKey: dataFolder.key, extractFolderKey: extractFolder.key, alignFolderKey: alignFolder?.key ?: null], method:"POST") {
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
            extractFolder.files['mat'].each { f ->
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

    def extractFolders = { attrs, body ->

        def dataFolder = attrs.dataFolder
        def extractFolders = extractService.extractFolders(dataFolder.key)

        out << '<div style="float:right;">'
        out << common.extractButton(dataFolder:dataFolder)
        out << '</div>'
        out << '<h3>extractions</h3>'
        out << '<ul>'
        extractFolders.each { extractFolder ->
                out << '<li>'
                out << '     <table style="padding:13px;"><tr>'
                out << '        <td><i class="icon-th-list"></i></td>'
                out << '        <td>' + common.viewExtractButton(dataFolder:dataFolder, extractFolder:extractFolder) + '</td>'
                out << '        <td>' + extractFolder.name + '</td>'
                out << '        </td>'

                out << '     </tr></table>'
                out << '</li>'
        }
        out << '</ul>'

    }

    def alignFolders = { attrs, body ->

        def dataFolder = attrs.dataFolder
        def extractFolder = attrs.extractFolder
        def alignFolders = alignService.alignFolders(dataFolder.key, extractFolder.key)

        out << '<h3>alignments</h3>'
        out << '<ul>'
        alignFolders.each { alignFolder ->
                out << '<li>'
                out << '    <i class="icon-th-list"></i> '
                out <<      g.link(controller: 'align', action:"alignment", params:[dataFolderKey: dataFolder.key, extractFolderKey: extractFolder.key, alignFolderKey: alignFolder.key]){ alignFolder.name }
                out <<      common.deleteAlignButton(dataFolder:dataFolder, extractFolder: extractFolder, alignFolder: alignFolder)
                out << '</li>'
        }
        out << '</ul>'

    }

    def combineFolders = { attrs, body ->

        def dataFolder = attrs.dataFolder
        def extractFolder = attrs.extractFolder
        def alignFolder = attrs.alignFolder ?: null //optional
        def combineFolders = combineService.combineFolders(dataFolder.key, extractFolder.key, alignFolder?.key ?: '')

        out << '<div style="float:right;">'
        out << common.combineButton(dataFolder:dataFolder, extractFolder:extractFolder, alignFolder:alignFolder)
        out << '</div>'
        out << '<h3>combines</h3>'
        out << '<ul>'
        combineFolders.each { combineFolder ->
                out << '<li>'
                out << '     <table style="padding:13px;"><tr>'
                out << '        <td><i class="icon-th-list"></i></td>'
                out << '        <td>' + common.viewCombineButton(dataFolder:dataFolder, extractFolder:extractFolder, alignFolder:alignFolder, combineFolder:combineFolder) + '</td>'
                out << '        <td>' + combineFolder.name + '</td>'
                out << '        </td>'

                out << '     </tr></table>'
                out << '</li>'
        }
        out << '</ul>'

    }
}
