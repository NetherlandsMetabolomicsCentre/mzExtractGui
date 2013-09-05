package nl.nmc.mzextract

class CommonTagLib {

    static namespace = "common"

    def extractButton = { attrs, body ->

        def dataFolder = attrs.dataFolder

        out << g.link(controller:'extract', action:'select', params:[dataFolderKey: dataFolder.key], alt:"Extract") { '<i class="icon-play"></i> new extraction' }
    }

    def settingsButton = { attrs, body ->

        def dataFolder = attrs.dataFolder
        def extractionFolder = attrs.extractionFolder

        out << g.link(controller:'extract', action:'settings', params:[dataFolderKey: dataFolder.key, extractionFolderKey: extractionFolder.key], alt:"Settings") { '<i class="icon-stop"></i> settings' }
    }

    def runExtractButton = { attrs, body ->

        def dataFolder = attrs.dataFolder
        def extractionFolder = attrs.extractionFolder

        out << g.link(controller:'extract', action:'extraction', params:[submit_extract: 'true', dataFolderKey: dataFolder.key, extractionFolderKey: extractionFolder.key], alt:"Run") { '<i class="icon-play"></i> run' }
    }

    def runAlignButton = { attrs, body ->

        def dataFolder = attrs.dataFolder
        def extractionFolder = attrs.extractionFolder
        def alignmentFolder = attrs.alignmentFolder

        out << g.link(controller:'align', action:'alignment', params:[submit_align: 'true', dataFolderKey: dataFolder.key, extractionFolderKey: extractionFolder.key, alignmentFolderKey: alignmentFolder.key], alt:"Run") { '<i class="icon-play"></i> run' }
    }

    def runCombineButton = { attrs, body ->

        def dataFolder = attrs.dataFolder
        def extractionFolder = attrs.extractionFolder
        def alignmentFolder = attrs.alignmentFolder
        def combineFolder = attrs.combineFolder

        out << g.link(controller:'combine', action:'combine', params:[submit_combine: 'true', dataFolderKey: dataFolder.key, extractionFolderKey: extractionFolder.key, alignmentFolderKey: alignmentFolder.key, combineFolderKey: combineFolder.key], alt:"Run") { '<i class="icon-play"></i> run' }
    }

    def alignButton = { attrs, body ->

        def dataFolder = attrs.dataFolder
        def extractionFolder = attrs.extractionFolder

        out << g.link(controller:'align', action:'select', params:[submit_align: 'true', dataFolderKey: dataFolder.key, extractionFolderKey: extractionFolder.key], alt:"Align") { ' - align - ' }
    }

    def combineButton = { attrs, body ->

        def dataFolder = attrs.dataFolder
        def extractionFolder = attrs.extractionFolder

        out << g.link(controller:'combine', action:'select', params:[submit_align: 'true', dataFolderKey: dataFolder.key, extractionFolderKey: extractionFolder.key], alt:"Combine") { ' - combine - ' }
    }

    def deleteButton = { attrs, body ->

        def dataFolder = attrs.dataFolder
        def extractionFolder = attrs.extractionFolder

        out << g.link(controller:'extract', action:'delete', params:[submit_delete: 'true', dataFolderKey: dataFolder.key, extractionFolderKey: extractionFolder.key], alt:"Delete") { '<i class="icon-stop"></i> delete' }
    }

}
