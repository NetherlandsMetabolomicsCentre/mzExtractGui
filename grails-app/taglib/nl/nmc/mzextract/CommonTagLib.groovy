package nl.nmc.mzextract

class CommonTagLib {

    static namespace = "common"

    def extractButton = { attrs, body ->

        def dataFolder = attrs.dataFolder

        out << g.link(controller:'extract', action:'select', params:[dataFolderKey: dataFolder.key], alt:"Extract", class:"btn btn-warning") { 'new extraction' }
    }

    def alignButton = { attrs, body ->

        def dataFolder = attrs.dataFolder
        def extractionFolder = attrs.extractionFolder

        //out << g.link(controller:'align', action:'select', params:[submit_align: 'true', dataFolderKey: dataFolder.key, extractionFolderKey: extractionFolder.key], alt:"Align", class:"btn btn-warning") { ' - align - ' }
    }

    def combineButton = { attrs, body ->

        def dataFolder = attrs.dataFolder
        def extractionFolder = attrs.extractionFolder

        out << g.link(controller:'combine', action:'select', params:[submit_combine: 'true', dataFolderKey: dataFolder.key, extractionFolderKey: extractionFolder.key], alt:"Combine", class:"btn btn-warning") { 'new combine' }
    }

    def settingsExtractButton = { attrs, body ->

        def dataFolder = attrs.dataFolder
        def extractionFolder = attrs.extractionFolder

        out << g.link(controller:'extract', action:'settings', params:[dataFolderKey: dataFolder.key, extractionFolderKey: extractionFolder.key], alt:"Settings", class:"btn btn-warning") { 'settings' }
    }

    def settingsAlignButton = { attrs, body ->

        def dataFolder = attrs.dataFolder
        def extractionFolder = attrs.extractionFolder
        def alignmentFolder = attrs.alignmentFolder

        //out << g.link(controller:'align', action:'settings', params:[dataFolderKey: dataFolder.key, extractionFolderKey: extractionFolder.key, alignmentFolderKey: alignmentFolder.key], alt:"Settings", class:"btn btn-warning") { 'settings' }
    }

    def settingsCombineButton = { attrs, body ->

        def dataFolder = attrs.dataFolder
        def extractionFolder = attrs.extractionFolder
        def alignmentFolder = attrs.alignmentFolder
        def combineFolder = attrs.combineFolder

        out << g.link(controller:'combine', action:'settings', params:[dataFolderKey: dataFolder.key, extractionFolderKey: extractionFolder.key, alignmentFolderKey: alignmentFolder.key, combineFolderKey: combineFolder.key], alt:"Settings", class:"btn btn-warning") { 'settings' }
    }

    def runExtractButton = { attrs, body ->

        def dataFolder = attrs.dataFolder
        def extractionFolder = attrs.extractionFolder

        out << g.link(controller:'extract', action:'extraction', params:[submit_extract: 'true', dataFolderKey: dataFolder.key, extractionFolderKey: extractionFolder.key], alt:"Run", class:"btn btn-warning") { 'run' }
    }

    def runAlignButton = { attrs, body ->

        def dataFolder = attrs.dataFolder
        def extractionFolder = attrs.extractionFolder
        def alignmentFolder = attrs.alignmentFolder

        out << g.link(controller:'align', action:'alignment', params:[submit_align: 'true', dataFolderKey: dataFolder.key, extractionFolderKey: extractionFolder.key, alignmentFolderKey: alignmentFolder.key], alt:"Run", class:"btn btn-warning") { 'run' }
    }

    def runCombineButton = { attrs, body ->

        def dataFolder = attrs.dataFolder
        def extractionFolder = attrs.extractionFolder
        def alignmentFolder = attrs.alignmentFolder
        def combineFolder = attrs.combineFolder

        out << g.link(controller:'combine', action:'combine', params:[submit_combine: 'true', dataFolderKey: dataFolder.key, extractionFolderKey: extractionFolder.key, alignmentFolderKey: alignmentFolder.key, combineFolderKey: combineFolder.key], alt:"Run", class:"btn btn-warning") { 'run' }
    }

    def deleteExtractionButton = { attrs, body ->

        def dataFolder = attrs.dataFolder
        def extractionFolder = attrs.extractionFolder

        out << g.link(controller:'extract', action:'delete', params:[submit_delete: 'true', dataFolderKey: dataFolder.key, extractionFolderKey: extractionFolder.key], alt:"Delete", class:"btn btn-warning") { 'delete' }
    }

    def deleteAlignButton = { attrs, body ->

        def dataFolder = attrs.dataFolder
        def extractionFolder = attrs.extractionFolder
        def alignmentFolder = attrs.alignmentFolder

        out << g.link(controller:'align', action:'delete', params:[submit_delete: 'true', dataFolderKey: dataFolder.key, extractionFolderKey: extractionFolder.key, alignmentFolderKey: alignmentFolder.key], alt:"Delete", class:"btn btn-warning") { 'delete' }
    }

    def deleteCombineButton = { attrs, body ->

        def dataFolder = attrs.dataFolder
        def extractionFolder = attrs.extractionFolder
        def alignmentFolder = attrs.alignmentFolder ?: null
        def combineFolder = attrs.combineFolder

        out << g.link(controller:'combine', action:'delete', params:[submit_delete: 'true', dataFolderKey: dataFolder.key, extractionFolderKey: extractionFolder.key, alignmentFolderKey: alignmentFolder?.key ?: null, combineFolderKey: combineFolder.key], alt:"Delete", class:"btn btn-warning") { 'delete' }
    }

}
