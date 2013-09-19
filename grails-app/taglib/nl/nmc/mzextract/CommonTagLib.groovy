package nl.nmc.mzextract

class CommonTagLib {

    static namespace = "common"
    
    def combineService
    def dataService
    def extractService
    def alignService
    
    def extractButtonData = { attrs, body ->
        def dataFolder = dataService.dataFolder(attrs.dataFolderKey)
        def extractFolder = extractService.extractFolder(attrs.dataFolderKey, attrs.extractFolderKey)
        def disabled = attrs.disabled ?: [:]

        if (!disabled['new'])       { out << extractButton(dataFolder: dataFolder) }
        if (!disabled['view'])      { out << viewExtractButton(dataFolder: dataFolder, extractFolder: extractFolder) }
        if (!disabled['settings'])  { out << settingsExtractButton(dataFolder: dataFolder, extractFolder: extractFolder) }
        if (!disabled['run'])       { out << runExtractButton(dataFolder: dataFolder, extractFolder: extractFolder) }         
        if (!disabled['delete'])    { out << deleteExtractButton(dataFolder: dataFolder, extractFolder: extractFolder) }
    }
    
    def extractButtons = { attrs, body ->
        
        def divId = "extractButtons_${UUID.randomUUID()}"
        
        out << '<div id="' + divId + '"></div>'
        
        def remoteUrl = g.createLink(controller: 'remote', action: 'extractButtons', params:[dataFolderKey:attrs.dataFolderKey, extractFolderKey:attrs.extractFolderKey], base: resource(dir:''))
        
        out << '<script>'
        out << ' $(document).ready(function() {'
        out << '     $("#' + divId + '").load("' + remoteUrl + '");'
        out << '     var refreshId = setInterval(function() {'
        out << '     $("#' + divId + '").load("' + remoteUrl + '");'
        out << '   }, 5000);'
        out << '   $.ajaxSetup({ cache: false });'
        out << '});'
        out << '</script>'        
    }

    def extractButton = { attrs, body ->

        def dataFolder = attrs.dataFolder

        out << g.link(controller:'extract', action:'select', params:[dataFolderKey: dataFolder.key], alt:"Extract", class:"btn btn-primary") { 'new' }
    }

    def alignButton = { attrs, body ->

        def dataFolder = attrs.dataFolder
        def extractFolder = attrs.extractFolder

        //out << g.link(controller:'align', action:'select', params:[submit_align: 'true', dataFolderKey: dataFolder.key, extractFolderKey: extractFolder.key], alt:"Align", class:"btn btn-warning") { ' - align - ' }
    }

    def combineButton = { attrs, body ->

        def dataFolder = attrs.dataFolder
        def extractFolder = attrs.extractFolder

        out << g.link(controller:'combine', action:'select', params:[submit_combine: 'true', dataFolderKey: dataFolder.key, extractFolderKey: extractFolder.key], alt:"Combine", class:"btn btn-warning") { 'new combine' }
    }
    
    def viewExtractButton = { attrs, body ->
        
        def dataFolder = attrs.dataFolder
        def extractFolder = attrs.extractFolder        
        
        out << g.link(class:"btn", controller: 'extract', action:"extraction", params:[dataFolderKey: dataFolder.key, extractFolderKey: extractFolder.key]){ 'view' }    
    }
        
    def settingsExtractButton = { attrs, body ->

        def dataFolder = attrs.dataFolder
        def extractFolder = attrs.extractFolder
        def buttonText = '<i class="icon-pencil icon-white"></i> settings'
        
        if (extractService.readStatus(dataFolder.key, extractFolder.key)['status'] != 'new'){
            out << '<button class="btn btn-warning disabled" >' + buttonText + '</button>'
        } else {
            out << g.link(controller:'extract', action:'settings', params:[dataFolderKey: dataFolder.key, extractFolderKey: extractFolder.key], alt:"Settings") { '<button class="btn btn-warning">' + buttonText + '</button>'}            
        }       
    }

    def settingsAlignButton = { attrs, body ->

        def dataFolder = attrs.dataFolder
        def extractFolder = attrs.extractFolder
        def alignFolder = attrs.alignFolder

        //out << g.link(controller:'align', action:'settings', params:[dataFolderKey: dataFolder.key, extractFolderKey: extractFolder.key, alignFolderKey: alignFolder.key], alt:"Settings", class:"btn btn-warning") { '<i class="icon-pencil icon-white"></i> settings' }
    }

    def settingsCombineButton = { attrs, body ->

        def dataFolder = attrs.dataFolder
        def extractFolder = attrs.extractFolder
        def alignFolder = attrs.alignFolder
        def combineFolder = attrs.combineFolder

        out << g.link(controller:'combine', action:'settings', params:[dataFolderKey: dataFolder.key, extractFolderKey: extractFolder.key, alignFolderKey: alignFolder.key, combineFolderKey: combineFolder.key], alt:"Settings", class:"btn btn-warning") { '<i class="icon-pencil icon-white"></i> settings' }
    }

    def runExtractButton = { attrs, body ->

        def dataFolder = attrs.dataFolder
        def extractFolder = attrs.extractFolder

        out << g.link(controller:'extract', action:'extraction', params:[submit_extract: 'true', dataFolderKey: dataFolder.key, extractFolderKey: extractFolder.key], alt:"Run", class:"btn btn-success") { '<i class="icon-play icon-white"></i> run' }
    }

    def runAlignButton = { attrs, body ->

        def dataFolder = attrs.dataFolder
        def extractFolder = attrs.extractFolder
        def alignFolder = attrs.alignFolder

        out << g.link(controller:'align', action:'alignment', params:[submit_align: 'true', dataFolderKey: dataFolder.key, extractFolderKey: extractFolder.key, alignFolderKey: alignFolder.key], alt:"Run", class:"btn btn-success") { '<i class="icon-play icon-white"></i> run' }
    }

    def runCombineButton = { attrs, body ->

        def dataFolder = attrs.dataFolder
        def extractFolder = attrs.extractFolder
        def alignFolder = attrs.alignFolder
        def combineFolder = attrs.combineFolder

        out << g.link(controller:'combine', action:'combine', params:[submit_combine: 'true', dataFolderKey: dataFolder.key, extractFolderKey: extractFolder.key, alignFolderKey: alignFolder.key, combineFolderKey: combineFolder.key], alt:"Run", class:"btn btn-success") { '<i class="icon-play icon-white"></i> run' }
    }

    def deleteExtractButton = { attrs, body ->

        def dataFolder = attrs.dataFolder
        def extractFolder = attrs.extractFolder

        out << g.link(controller:'extract', action:'delete', params:[submit_delete: 'true', dataFolderKey: dataFolder.key, extractFolderKey: extractFolder.key], alt:"Delete", class:"btn btn-danger") { '<i class="icon-stop icon-white"></i> delete' }
    }

    def deleteAlignButton = { attrs, body ->

        def dataFolder = attrs.dataFolder
        def extractFolder = attrs.extractFolder
        def alignFolder = attrs.alignFolder

        out << g.link(controller:'align', action:'delete', params:[submit_delete: 'true', dataFolderKey: dataFolder.key, extractFolderKey: extractFolder.key, alignFolderKey: alignFolder.key], alt:"Delete", class:"btn btn-danger") { '<i class="icon-stop icon-white"></i> delete' }
    }

    def deleteCombineButton = { attrs, body ->

        def dataFolder = attrs.dataFolder
        def extractFolder = attrs.extractFolder
        def alignFolder = attrs.alignFolder ?: null
        def combineFolder = attrs.combineFolder

        out << g.link(controller:'combine', action:'delete', params:[submit_delete: 'true', dataFolderKey: dataFolder.key, extractFolderKey: extractFolder.key, alignFolderKey: alignFolder?.key ?: null, combineFolderKey: combineFolder.key], alt:"Delete", class:"btn btn-danger") { '<i class="icon-stop icon-white"></i> delete' }
    }

}
