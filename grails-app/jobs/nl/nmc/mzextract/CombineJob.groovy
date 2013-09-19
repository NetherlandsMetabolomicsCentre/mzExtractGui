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

            // store the filename to retrieve the dataFolderKey, extractFolderKey and alignFolderKey
            def filename = nextCombineJob.name
            def name = filename.tokenize('.')[0]
            def dataFolderKey = name.tokenize('_')[0]
            def extractFolderKey = name.tokenize('_')[1]
            def alignFolderKey = name.tokenize('_')[2] != 'null' ? name.tokenize('_')[2] : null
            def combineFolderKey = name.tokenize('_')[3]

            log.info("\nStarting new combine (${name})...")

            // delete the file from the queue
            nextCombineJob.delete()

            // fetch dataFolder, extractFolder and alignFolder
            def dataFolder = dataService.dataFolder(dataFolderKey)
            def extractFolder = extractService.extractFolder(dataFolderKey, extractFolderKey)
            def alignFolder = alignFolderKey ? alignService.alignFolder(dataFolderKey, extractFolderKey, alignFolderKey) : null
            def combineFolder = combineService.combineFolder(dataFolderKey, extractFolderKey, alignFolderKey, combineFolderKey)

            //fetch config to use
            def configFile = combineService.settingsFile(dataFolderKey, extractFolderKey, alignFolderKey, combineFolderKey)

            log.info(" --- combine file ${combineFolderKey}")
            combineService.combine(configFile.canonicalPath)
        }
    }
}
