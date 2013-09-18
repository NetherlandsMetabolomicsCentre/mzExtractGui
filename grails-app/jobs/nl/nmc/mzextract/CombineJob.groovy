package nl.nmc.mzextract

class CombineJob {
    static triggers = {
      simple repeatInterval: 5000l // execute job once in 5 seconds
    }

    def queueService
    def dataService
    def extractService
    def alignService
    def combineService

    def execute() {

        def nextCombineJob = null
        def queuedCombines = queueService.queuedCombines()

        if (queuedCombines.size()){
            // get first from the queue (TODO: sort by date to play fair)
            nextCombineJob = queuedCombines[0]
        }

        if (nextCombineJob != null){

            // store the filename to retrieve the dataFolderKey, extractionFolderKey and alignmentFolderKey
            def filename = nextCombineJob.name
            def name = filename.tokenize('.')[0]
            def dataFolderKey = name.tokenize('_')[0]
            def extractionFolderKey = name.tokenize('_')[1]
            def alignmentFolderKey = name.tokenize('_')[2] != 'null' ? name.tokenize('_')[2] : null
            def combineFolderKey = name.tokenize('_')[3]

            log.info("\nStarting new combine (${name})...")

            // delete the file from the queue
            nextCombineJob.delete()

            // fetch dataFolder, extractionFolder and alignmentFolder
            def dataFolder = dataService.dataFolder(dataFolderKey)
            def extractionFolder = extractService.extractionFolder(dataFolderKey, extractionFolderKey)
            def alignmentFolder = alignmentFolderKey ? alignService.alignmentFolder(dataFolderKey, extractionFolderKey, alignmentFolderKey) : null
            def combineFolder = combineService.combineFolder(dataFolderKey, extractionFolderKey, alignmentFolderKey, combineFolderKey)

            //fetch config to use
            def configFile = combineService.settingsFile(dataFolderKey, extractionFolderKey, alignmentFolderKey, combineFolderKey)

            log.info(" --- combine file ${combineFolderKey}")
            combineService.combine(configFile.canonicalPath)
        }
    }
}
