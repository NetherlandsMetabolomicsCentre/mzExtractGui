package nl.nmc.mzextract

class AlignmentJob {
    static triggers = {
      simple repeatInterval: 3000l // execute job once in 3 seconds
    }

    def queueService
    def dataService
    def extractService
    def alignService

    def execute() {

        def nextAlignmentJob = null
        def queuedAlignments = queueService.queuedAlignments()

        if (queuedAlignments.size()){
            // get first from the queue (TODO: sort by date to play fair)
            nextAlignmentJob = queuedAlignments[0]
        }

        if (nextAlignmentJob != null){

            // store the filename to retrieve the dataFolderKey and extractFolderKey
            def filename = nextAlignmentJob.name
            def name = filename.tokenize('.')[0]
            def dataFolderKey = name.tokenize('_')[0]
            def extractFolderKey = name.tokenize('_')[1]
            def alignFolderKey = name.tokenize('_')[2]

            log.info("\nStarting new alignment (${name})...")

            // delete the file from the queue
            nextAlignmentJob.delete()

            // fetch dataFolder, extractFolder and alignFolder
            def dataFolder = dataService.dataFolder(dataFolderKey)
            def extractFolder = extractService.extractFolder(dataFolderKey, extractFolderKey)
            def alignFolder = alignService.alignFolder(dataFolderKey, extractFolderKey, alignFolderKey)

            //fetch config to use
            def configFile = alignService.settingsFile(dataFolderKey, extractFolderKey, alignFolderKey)

            // retrieve a list of the selected mat files to process
            def selectedMatFiles = []
            new File(alignFolder.path + '/mat.txt').eachLine { line ->
                selectedMatFiles << line
            }

            // run parallel
            //GParsPool.withPool(10) { // defines the max number of Threads to use
                //selectedMatFiles.eachParallel { matFileKey ->
                selectedMatFiles.each { matFileKey ->
                    def fileToProcess = extractFolder.files['mat'].find { it.key == matFileKey } ?: null

                    if (fileToProcess != null){
                        log.info(" --- aligning file ${fileToProcess.name}")
                        alignService.align(fileToProcess.path, configFile.canonicalPath)
                    }
                }
            //}

        }
    }
}
