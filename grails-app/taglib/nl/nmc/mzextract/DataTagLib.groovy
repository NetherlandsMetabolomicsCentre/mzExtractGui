package nl.nmc.mzextract

class DataTagLib {

    static namespace = "data"

    def dataService
    def extractService
    def alignService
    def combineService
    def grailsApplication

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
            def rsdqcrFile = new File(combineFolder.path, "${ grailsApplication.config.mzextract.combine.outputfile}_summary_rsdqcrtar.txt")
            if (rsdqcrFile.exists()){

                def rsdqcrEntries = []
                def rsdqcrEntriesHeader = []
                rsdqcrFile.eachLine { rsdqcrLine ->

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
                }

                // reformat the data to a per compound list of AREA, RT (min) and MEANRT(QC)
                jsonData = '['
                rsdqcrEntries.each { rsdqcrEntry ->

                    def rsdRT = (rsdqcrEntry['RSDRT (QC)'] as Float) * 1000
                    def rsdRTGroup = "00-05"
                    if (rsdRT > 5 && rsdRT <= 10) { rsdRTGroup = "05-10" }
                    if (rsdRT > 10 && rsdRT <= 15) { rsdRTGroup = "10-15" }
                    if (rsdRT > 15 && rsdRT <= 20) { rsdRTGroup = "15-20" }
                    if (rsdRT > 20 && rsdRT <= 25) { rsdRTGroup = "20-25" }
                    if (rsdRT > 25) { rsdRTGroup = "25 >" }

                    def rsdQC = (rsdqcrEntry['RSDQC (QC)'] as Float) * 100
                    def rsdQCGroup = "00-10"
                    if (rsdQC > 10 && rsdQC <= 20) { rsdQCGroup = "10-20" }
                    if (rsdQC > 20 && rsdQC <= 30) { rsdQCGroup = "20-30" }
                    if (rsdQC > 30 && rsdQC <= 40) { rsdQCGroup = "30-40" }
                    if (rsdQC > 40 && rsdQC <= 50) { rsdQCGroup = "40-50" }
                    if (rsdQC > 50) { rsdQCGroup = "50 >" }

                    def meanRT = (rsdqcrEntry['MEANRT(QC)'] as Float) * 10
                    def meanRTGroup = "00-20"
                    if (meanRT > 20 && meanRT <= 40) { meanRTGroup = "20-40" }
                    if (meanRT > 40 && meanRT <= 60) { meanRTGroup = "40-60" }
                    if (meanRT > 60 && meanRT <= 80) { meanRTGroup = "60-80" }
                    if (meanRT > 80 && meanRT <= 99) { meanRTGroup = "80-99" }
                    if (meanRT > 99) { meanRTGroup = "99 >" }

                    jsonData += """
                        {
                            "COMPOUND":"${rsdqcrEntry['COMPOUND']}",
                            "RT (min)":"${rsdRT}",
                            "AREA":"${rsdQC}",
                            "MEANRT":"${meanRT}",
                            "rsdRTGroup":"${rsdRTGroup}",
                            "rsdQCGroup":"${rsdQCGroup}",
                            "meanRTGroup":"${meanRTGroup}"
                        },
                    """
                }
                jsonData += ']'

                [
                    'RT (min)':'rsdRTGroup',
                    'AREA':'rsdQCGroup',
                    'MEANRT':'meanRTGroup'
                ].each { chartData, chartType ->

                    html += """ <div id="${chartType}_SVG">
                                            <script type="text/javascript">
                                            var ${chartType}_svg = dimple.newSvg("#${chartType}_SVG", document.documentElement.clientWidth-330, 250);

                                            var ${chartType}_data = ${jsonData};
                                            var ${chartType}_chart = new dimple.chart(${chartType}_svg, ${chartType}_data);

                                            ${chartType}_chart.setBounds(60, 60, document.documentElement.clientWidth-400, 110)
                                            var ${chartType}_yAxis = ${chartType}_chart.addMeasureAxis("y", "${chartData}");
                                            ${chartType}_yAxis.tickFormat = ",g";

                                            var ${chartType}_xAxis = ${chartType}_chart.addCategoryAxis("x", ["COMPOUND", "${chartType}"]);
                                            ${chartType}_xAxis.addOrderRule("COMPOUND");
                                            var ${chartType}_bars = ${chartType}_chart.addSeries("${chartType}", dimple.plot.bar);
                                            ${chartType}_bars.addOrderRule("${chartType}", true);

                                            var ${chartType}_myLegend = ${chartType}_chart.addLegend(1, 1, 440, 1, "right", ${chartType}_bars);
                                            ${chartType}_chart.draw();
                                            ${chartType}_chart.legends = [];

                                            // Get a unique list of Owner values to use when filtering
                                            var ${chartType}_filterValues = dimple.getUniqueValues(${chartType}_data, "${chartType}");
                                            // Get all the rectangles from our now orphaned legend
                                            ${chartType}_myLegend.shapes.selectAll("rect")
                                              // Add a click event to each rectangle
                                              .on("click", function (e) {
                                                // This indicates whether the item is already visible or not
                                                var ${chartType}_hide = false;
                                                var ${chartType}_newFilters = [];
                                                // If the filters contain the clicked shape hide it
                                                ${chartType}_filterValues.forEach(function (f) {
                                                  if (f === e.aggField.slice(-1)[0]) {
                                                    ${chartType}_hide = true;
                                                  } else {
                                                    ${chartType}_newFilters.push(f);
                                                  }
                                                });
                                                // Hide the shape or show it
                                                if (${chartType}_hide) {
                                                  d3.select(this).style("opacity", 0.2);
                                                } else {
                                                  ${chartType}_newFilters.push(e.aggField.slice(-1)[0]);
                                                  d3.select(this).style("opacity", 0.8);
                                                }
                                                // Update the filters
                                                ${chartType}_filterValues = ${chartType}_newFilters;
                                                // Filter the data
                                                ${chartType}_chart.data = dimple.filterData(${chartType}_data, "${chartType}", ${chartType}_filterValues);
                                                // Passing a duration parameter makes the ${chartType}_chart animate. Without
                                                // it there is no transition
                                                ${chartType}_chart.draw();
                                              });
                                            </script>
                                        </div>
                    """

                }

                // html += """ <div id="areaSVG">
                //                         <script type="text/javascript">
                //                         var svg = dimple.newSvg("#areaSVG", document.documentElement.clientWidth-380, 270);

                //                         var data = ${jsonData};
                //                         var chart = new dimple.chart(svg, data);

                //                         chart.setBounds(120, 20, document.documentElement.clientWidth-500, 130)
                //                         var yAxis = chart.addMeasureAxis("y", "AREA");
                //                         yAxis.tickFormat = ",g";

                //                         var xAxis = chart.addCategoryAxis("x", ["COMPOUND", "rsdQCGroup"]);
                //                         xAxis.addOrderRule("COMPOUND");
                //                         var bars = chart.addSeries("rsdQCGroup", dimple.plot.bar);
                //                         bars.addOrderRule("rsdQCGroup", true);
                //                         //bars.barGap = 0.1;
                //                         var myLegend = chart.addLegend(10, 5, 540, 10, "Right", bars);
                //                         chart.draw();
                //                         chart.legends = [];

                //                         // Get a unique list of Owner values to use when filtering
                //                         var filterValues = dimple.getUniqueValues(data, "rsdQCGroup");
                //                         // Get all the rectangles from our now orphaned legend
                //                         myLegend.shapes.selectAll("rect")
                //                           // Add a click event to each rectangle
                //                           .on("click", function (e) {
                //                             // This indicates whether the item is already visible or not
                //                             var hide = false;
                //                             var newFilters = [];
                //                             // If the filters contain the clicked shape hide it
                //                             filterValues.forEach(function (f) {
                //                               if (f === e.aggField.slice(-1)[0]) {
                //                                 hide = true;
                //                               } else {
                //                                 newFilters.push(f);
                //                               }
                //                             });
                //                             // Hide the shape or show it
                //                             if (hide) {
                //                               d3.select(this).style("opacity", 0.2);
                //                             } else {
                //                               newFilters.push(e.aggField.slice(-1)[0]);
                //                               d3.select(this).style("opacity", 0.8);
                //                             }
                //                             // Update the filters
                //                             filterValues = newFilters;
                //                             // Filter the data
                //                             chart.data = dimple.filterData(data, "rsdQCGroup", filterValues);
                //                             // Passing a duration parameter makes the chart animate. Without
                //                             // it there is no transition
                //                             chart.draw(800);
                //                           });
                //                         </script>
                //                     </div>
                // """

                // html += """ <div id="rtSVG">
                //                         <script type="text/javascript">
                //                         var rtsvg = dimple.newSvg("#rtSVG", document.documentElement.clientWidth-380, 270);

                //                         var rtdata = ${jsonData};
                //                         var rtchart = new dimple.chart(rtsvg, rtdata);

                //                         rtchart.setBounds(120, 50, document.documentElement.clientWidth-500, 130)
                //                         var rtyAxis = rtchart.addMeasureAxis("y", "RT (min)");
                //                         rtyAxis.tickFormat = ",g";

                //                         var rtxAxis = rtchart.addCategoryAxis("x", ["COMPOUND", "rsdRTGroup"]);
                //                         rtxAxis.addOrderRule("COMPOUND");
                //                         var rtbars = rtchart.addSeries("rsdRTGroup", dimple.plot.bar);
                //                         rtbars.addOrderRule("rsdRTGroup", true);
                //                         //rtbars.barGap = 0.1;
                //                         var rtmyLegend = rtchart.addLegend(10, 5, 540, 10, "Right", rtbars);
                //                         rtchart.draw();
                //                         rtchart.legends = [];

                //                         // Get a unique list of Owner values to use when filtering
                //                         var rtfilterValues = dimple.getUniqueValues(rtdata, "rsdRTGroup");
                //                         // Get all the rectangles from our now orphaned legend
                //                         rtmyLegend.shapes.selectAll("rect")
                //                           // Add a click event to each rectangle
                //                           .on("click", function (e) {
                //                             // This indicates whether the item is already visible or not
                //                             var rthide = false;
                //                             var rtnewFilters = [];
                //                             // If the filters contain the clicked shape hide it
                //                             rtfilterValues.forEach(function (f) {
                //                               if (f === e.aggField.slice(-1)[0]) {
                //                                 rthide = true;
                //                               } else {
                //                                 rtnewFilters.push(f);
                //                               }
                //                             });
                //                             // Hide the shape or show it
                //                             if (rthide) {
                //                               d3.select(this).style("opacity", 0.2);
                //                             } else {
                //                               rtnewFilters.push(e.aggField.slice(-1)[0]);
                //                               d3.select(this).style("opacity", 0.8);
                //                             }
                //                             // Update the filters
                //                             rtfilterValues = rtnewFilters;
                //                             // Filter the data
                //                             rtchart.data = dimple.filterData(rtdata, "rsdRTGroup", rtfilterValues);
                //                             // Passing a duration parameter makes the rtchart animate. Without
                //                             // it there is no transition
                //                             rtchart.draw(800);
                //                           });
                //                         </script>
                //                     </div>
                // """

                // dump data to HTML
                //html += "<div style='width:1000px;'><pre><small>${rsdqcrFile.text}</small></pre></div>"


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

            if (ext in   grailsApplication.config.mzextract.exepted.extensions){
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
