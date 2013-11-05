package nl.nmc.mzextract

import grails.converters.JSON

class CommonTagLib {

    static namespace = "common"

    def combineService
    def dataService
    def extractService
    def alignService

    def extractStatus = { attrs, body ->
        def extractStatus = extractService.readStatus(attrs.dataFolderKey, attrs.extractFolderKey)['status']
        if (extractStatus){
            out << '<div style="float: right;">status: '
            out << extractStatus
            if ((extractStatus == 'running') || (extractStatus == 'stopping')){
                out << ' <img src="' + resource(dir: 'images', file: 'spinner.gif') + '" />'
            }
            out << '</div>'
        }
    }    
    
    def extractButtonData = { attrs, body ->
        def dataFolder = dataService.dataFolder(attrs.dataFolderKey)
        def extractFolder = extractService.extractFolder(attrs.dataFolderKey, attrs.extractFolderKey)

        out << '<div style="padding-bottom: 5px; border-bottom:thin solid #cdcdcd;">'
        out << runExtractButton(dataFolder: dataFolder, extractFolder: extractFolder)
        out << stopExtractButton(dataFolder: dataFolder, extractFolder: extractFolder)                
        out << settingsExtractButton(dataFolder: dataFolder, extractFolder: extractFolder)
        out << deleteExtractButton(dataFolder: dataFolder, extractFolder: extractFolder)
        out << extractStatus(dataFolderKey: dataFolder.key, extractFolderKey: extractFolder.key)        
        out << '</div>'
    }

    def extractButtons = { attrs, body ->

        def remoteUrl = g.createLink(controller: 'remote', action: 'extractButtons', params:[dataFolderKey:attrs.dataFolderKey, extractFolderKey:attrs.extractFolderKey], base: resource(dir:''))

        def divId = "extractButtons_${UUID.randomUUID()}"
        out << '<div id="' + divId + '"></div>'
        out << '<script>'
        out << '$(document).ready(function() {$("#' + divId + '").load("' + remoteUrl + '"); var refreshId = setInterval(function() { $("#' + divId + '").load("' + remoteUrl + '"); }, 2000); $.ajaxSetup({ cache: false }); });'
        out << '</script>'
    }

    def combineStatus = { attrs, body ->        
        def combineStatus = combineService.readStatus(attrs.dataFolderKey, attrs.extractFolderKey, attrs.alignFolderKey, attrs.combineFolderKey)['status']
        if (combineStatus){
            out << '<div style="float: right;">status: '
            out << combineStatus
            if (combineStatus == 'running'){
                out << ' <img src="' + resource(dir: 'images', file: 'spinner.gif') + '" />'
            }            
            out << '</div>'
        }
    }        
        
    def combineButtonData = { attrs, body ->
        def dataFolder = dataService.dataFolder(attrs.dataFolderKey)
        def extractFolder = extractService.extractFolder(attrs.dataFolderKey, attrs.extractFolderKey)
        def alignFolder = alignService.alignFolder(attrs.dataFolderKey, attrs.extractFolderKey, attrs.alignFolderKey)
        def combineFolder = combineService.combineFolder(attrs.dataFolderKey, attrs.extractFolderKey, attrs.alignFolderKey, attrs.combineFolderKey)          

        out << '<div style="padding-bottom: 5px; border-bottom:thin solid #cdcdcd;">'
        out << runCombineButton(dataFolder: dataFolder, extractFolder: extractFolder, alignFolder: alignFolder, combineFolder: combineFolder)
        out << settingsCombineButton(dataFolder: dataFolder, extractFolder: extractFolder, alignFolder: alignFolder, combineFolder: combineFolder)
        out << deleteCombineButton(dataFolder: dataFolder, extractFolder: extractFolder, alignFolder: alignFolder, combineFolder: combineFolder)
        out << combineStatus(dataFolderKey: attrs.dataFolderKey, extractFolderKey: attrs.extractFolderKey, alignFolderKey: attrs.alignFolderKey, combineFolderKey: attrs.combineFolderKey)        
        out << '</div>'
    }

    def combineButtons = { attrs, body ->

        def remoteUrl = g.createLink(controller: 'remote', action: 'combineButtons', params:[dataFolderKey:attrs.dataFolderKey, extractFolderKey:attrs.extractFolderKey, alignFolderKey:attrs.alignFolderKey ?: null, combineFolderKey:attrs.combineFolderKey], base: resource(dir:''))
        def divId = "combineButtons_${UUID.randomUUID()}"
        out << '<div id="' + divId + '"></div>'
        out << '<script>'
        out << '$(document).ready(function() {$("#' + divId + '").load("' + remoteUrl + '"); var refreshId = setInterval(function() { $("#' + divId + '").load("' + remoteUrl + '"); }, 2000); $.ajaxSetup({ cache: false }); });'
        out << '</script>'
    }        

    def extractButton = { attrs, body ->

        def dataFolder = attrs.dataFolder

        out << g.link(controller:'extract', action:'select', params:[dataFolderKey: dataFolder.key], alt:"Extract", class:"btn btn-mini btn-primary") { 'new' }
    }

    def alignButton = { attrs, body ->

        def dataFolder = attrs.dataFolder
        def extractFolder = attrs.extractFolder

        //out << g.link(controller:'align', action:'select', params:[submit_align: 'true', dataFolderKey: dataFolder.key, extractFolderKey: extractFolder.key], alt:"Align", class:"btn btn-mini btn-warning") { ' - align - ' }
    }

    def combineButton = { attrs, body ->

        def dataFolder = attrs.dataFolder
        def extractFolder = attrs.extractFolder

        out << g.link(controller:'combine', action:'select', params:[submit_combine: 'true', dataFolderKey: dataFolder.key, extractFolderKey: extractFolder.key], alt:"Combine", class:"btn btn-mini btn-warning") { 'new combine' }
    }

    def viewExtractButton = { attrs, body ->

        def dataFolder = attrs.dataFolder
        def extractFolder = attrs.extractFolder

        out << g.link(class:"btn btn-mini", controller: 'extract', action:"extraction", params:[dataFolderKey: dataFolder.key, extractFolderKey: extractFolder.key]){ 'view' }
    }

    def viewCombineButton = { attrs, body ->

        def dataFolder = attrs.dataFolder
        def extractFolder = attrs.extractFolder
        def alignFolder = attrs.alignFolder
        def combineFolder = attrs.combineFolder        

        out << g.link(class:"btn btn-mini", controller: 'combine', action:"combine", params:[dataFolderKey: dataFolder.key, extractFolderKey: extractFolder.key, alignFolderKey: alignFolder?.key, combineFolderKey: combineFolder.key]){ 'view' }
    }
    
    def settingsExtractButton = { attrs, body ->

        def dataFolder = attrs.dataFolder
        def extractFolder = attrs.extractFolder
        def buttonText = '<i class="icon-pencil icon-white"></i> settings'

        def currentStatus = extractService.readStatus(dataFolder.key, extractFolder.key)['status']
        if (currentStatus != 'new' ){
            out << '<button class="btn btn-mini  btn-warning disabled">' + buttonText + '</button>'
        } else {
            out << g.link(controller:'extract', action:'settings', params:[dataFolderKey: dataFolder.key, extractFolderKey: extractFolder.key], alt:"Settings") { '<button class="btn btn-mini btn-warning">' + buttonText + '</button>'}
        }
    }

    def settingsAlignButton = { attrs, body ->

        def dataFolder = attrs.dataFolder
        def extractFolder = attrs.extractFolder
        def alignFolder = attrs.alignFolder

        //out << g.link(controller:'align', action:'settings', params:[dataFolderKey: dataFolder.key, extractFolderKey: extractFolder.key, alignFolderKey: alignFolder.key], alt:"Settings", class:"btn btn-mini btn-warning") { '<i class="icon-pencil icon-white"></i> settings' }
    }

    def settingsCombineButton = { attrs, body ->

        def dataFolder = attrs.dataFolder
        def extractFolder = attrs.extractFolder
        def alignFolder = attrs.alignFolder
        def combineFolder = attrs.combineFolder

        def buttonText = '<i class="icon-pencil icon-white"></i> settings'

        def currentStatus = combineService.readStatus(dataFolder.key, extractFolder.key, alignFolder?.key ?: null, combineFolder.key)['status']
        if (currentStatus != 'new' ){
            out << '<button class="btn btn-mini  btn-warning disabled">' + buttonText + '</button>'
        } else {
            out << g.link(controller:'combine', action:'settings', params:[dataFolderKey: dataFolder.key, extractFolderKey: extractFolder.key, alignFolderKey: alignFolder?.key, combineFolderKey: combineFolder.key], alt:"Settings", class:"btn btn-mini btn-warning") { buttonText }
        }                
    }

    def runExtractButton = { attrs, body ->

        def dataFolder = attrs.dataFolder
        def extractFolder = attrs.extractFolder
        def currentStatus = extractService.readStatus(dataFolder.key, extractFolder.key)['status']
        def buttonText = '<i class="icon-play icon-white"></i> run'

        if (currentStatus != 'new'){
            out << '<button class="btn btn-mini  btn-success disabled">' + buttonText + '</button>'
        } else {
            out << g.link(controller:'extract', action:'extraction', params:[submit_extract: 'true', dataFolderKey: dataFolder.key, extractFolderKey: extractFolder.key], alt:"Run", class:"btn btn-mini btn-success") { buttonText }
        }
    }

    def runAlignButton = { attrs, body ->

        def dataFolder = attrs.dataFolder
        def extractFolder = attrs.extractFolder
        def alignFolder = attrs.alignFolder

        out << g.link(controller:'align', action:'alignment', params:[submit_align: 'true', dataFolderKey: dataFolder.key, extractFolderKey: extractFolder.key, alignFolderKey: alignFolder?.key], alt:"Run", class:"btn btn-mini btn-success") { '<i class="icon-play icon-white"></i> run' }
    }

    def runCombineButton = { attrs, body ->

        def dataFolder = attrs.dataFolder
        def extractFolder = attrs.extractFolder
        def alignFolder = attrs.alignFolder
        def combineFolder = attrs.combineFolder

        def currentStatus = combineService.readStatus(dataFolder.key, extractFolder.key, alignFolder?.key ?: null, combineFolder.key)['status']
        def buttonText = '<i class="icon-play icon-white"></i> run'

        if (currentStatus != 'new'){
            out << '<button class="btn btn-mini  btn-success disabled">' + buttonText + '</button>'
        } else {
            out << g.link(controller:'combine', action:'combine', params:[submit_combine: 'true', dataFolderKey: dataFolder.key, extractFolderKey: extractFolder.key, alignFolderKey: alignFolder?.key, combineFolderKey: combineFolder.key], alt:"Run", class:"btn btn-mini btn-success") { buttonText }
        }                
    }

    def deleteExtractButton = { attrs, body ->

        def dataFolder = attrs.dataFolder
        def extractFolder = attrs.extractFolder
        def currentStatus = extractService.readStatus(dataFolder.key, extractFolder.key)['status']
        def buttonText = '<i class="icon-trash icon-white"></i> delete'

        if (!currentStatus || (currentStatus == 'new' || currentStatus == 'done')){
            out << g.link(controller:'extract', action:'delete', params:[submit_delete: 'true', dataFolderKey: dataFolder.key, extractFolderKey: extractFolder.key], alt:"Delete", class:"btn btn-mini btn-danger", onclick:"return confirm('Are you sure you want to delete this extraction?')") { buttonText }            
        } else {
            out << '<button class="btn btn-mini  btn-danger disabled">' + buttonText + '</button>'            
        }
    }

    def stopExtractButton = { attrs, body ->

        def dataFolder = attrs.dataFolder
        def extractFolder = attrs.extractFolder
        def currentStatus = extractService.readStatus(dataFolder.key, extractFolder.key)['status']
        def buttonText = '<i class="icon-stop icon-white"></i> stop'

        if (currentStatus == 'running'){
            out << g.link(controller:'extract', action:'stop', params:[submit_stop: 'true', dataFolderKey: dataFolder.key, extractFolderKey: extractFolder.key], alt:"Stop", class:"btn btn-mini btn-danger", onclick:"return confirm('Are you sure you want to stop this extraction of the remaining files?')") { buttonText }            
        } else {
            out << '<button class="btn btn-mini disabled">' + buttonText + '</button>'            
        }
    }
    
    def deleteAlignButton = { attrs, body ->

        def dataFolder = attrs.dataFolder
        def extractFolder = attrs.extractFolder
        def alignFolder = attrs.alignFolder

        out << g.link(controller:'align', action:'delete', params:[submit_delete: 'true', dataFolderKey: dataFolder.key, extractFolderKey: extractFolder.key, alignFolderKey: alignFolder.key], alt:"Delete", class:"btn btn-mini btn-danger", onclick:"return confirm('Are you sure you want to delete this alignment?')") { '<i class="icon-trash icon-white"></i> delete' }
    }

    def deleteCombineButton = { attrs, body ->

        def dataFolder = attrs.dataFolder
        def extractFolder = attrs.extractFolder
        def alignFolder = attrs.alignFolder ?: null
        def combineFolder = attrs.combineFolder
        def buttonText = '<i class="icon-trash icon-white"></i> delete'        

        def currentStatus = combineService.readStatus(dataFolder.key, extractFolder.key, alignFolder?.key ?: null, combineFolder.key)['status']
        
        if (!currentStatus || (currentStatus == 'new' || currentStatus == 'done')){
            out << g.link(controller:'combine', action:'delete', params:[submit_delete: 'true', dataFolderKey: dataFolder.key, extractFolderKey: extractFolder.key, alignFolderKey: alignFolder?.key ?: null, combineFolderKey: combineFolder.key], alt:"Delete", class:"btn btn-mini btn-danger", onclick:"return confirm('Are you sure you want to delete this combine?')") { buttonText }
        } else {
            out << '<button class="btn btn-mini  btn-danger disabled">' + buttonText + '</button>'            
        }                
    }

}
