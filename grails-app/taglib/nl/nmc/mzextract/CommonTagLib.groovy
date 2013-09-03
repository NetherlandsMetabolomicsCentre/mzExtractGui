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

    def runButton = { attrs, body ->

        def dataFolder = attrs.dataFolder
        def extractionFolder = attrs.extractionFolder

        out << g.link(controller:'extract', action:'extraction', params:[submit_run: 'true', dataFolderKey: dataFolder.key, extractionFolderKey: extractionFolder.key], alt:"Settings") { '<i class="icon-play"></i> run' }
    }

    def alignButton = { attrs, body ->

        def dataFolder = attrs.dataFolder
        def extractionFolder = attrs.extractionFolder

        out << g.link(controller:'align', action:'select', params:[submit_align: 'true', dataFolderKey: dataFolder.key, extractionFolderKey: extractionFolder.key], alt:"Settings") { ' - align - ' }
    }

    def deleteButton = { attrs, body ->

        def dataFolder = attrs.dataFolder
        def extractionFolder = attrs.extractionFolder

        out << g.link(controller:'extract', action:'delete', params:[submit_delete: 'true', dataFolderKey: dataFolder.key, extractionFolderKey: extractionFolder.key], alt:"Settings") { '<i class="icon-stop"></i> delete' }
    }

}
