package nl.nmc.mzextract

class CombineJob {
    static triggers = {
      simple repeatInterval: 3000l // execute job once in 3 seconds
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
            
            def logging = " -- start ${new Date()}\n"            

            // store the filename to retrieve the dataFolderKey, extractFolderKey and alignFolderKey
            def filename = nextCombineJob.name
            def name = filename.tokenize('.')[0]
            
            def dataFolderKey = name.tokenize('_')[0]
            def extractFolderKey = name.tokenize('_')[1]
            def alignFolderKey = name.tokenize('_')[2] != 'null' ? name.tokenize('_')[2] : null
            def combineFolderKey = name.tokenize('_')[3]

            // delete the file from the queue
            nextCombineJob.delete()

            // fetch dataFolder, extractFolder and alignFolder
            def dataFolder = dataService.dataFolder(dataFolderKey)
            def extractFolder = extractService.extractFolder(dataFolderKey, extractFolderKey)
            def alignFolder = alignService.alignFolder(dataFolderKey, extractFolderKey, alignFolderKey)
            def combineFolder = combineService.combineFolder(dataFolderKey, extractFolderKey, alignFolderKey, combineFolderKey)

            // include path of combine in logging
            logging += " -- combine folder: ${combineFolder.path}\n"            
            
            //fetch config to use
            def configFile = combineService.settingsFile(dataFolderKey, extractFolderKey, alignFolderKey, combineFolderKey)

            log.info(" --- combine file ${combineFolderKey}")
            
            // update status
            combineService.writeStatus(dataFolderKey, extractFolderKey, alignFolderKey, combineFolderKey, ['status':'running', 'updated': new Date(), 'logging':logging])            
            
            combineService.combine(configFile.canonicalPath)
            
            // update status
            combineService.writeStatus(dataFolderKey, extractFolderKey, alignFolderKey, combineFolderKey, ['status':'done', 'updated': new Date(), 'logging':logging])            
        }
    }
}
