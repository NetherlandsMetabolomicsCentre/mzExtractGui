package nl.nmc.mzextract

class AlignmentJob {
    static triggers = {
      simple repeatInterval: 5000l // execute job once in 5 seconds
    }

    def queueService
    def dataService
    def extractService

    def execute() {

        def nextAlignmentJob = null
        def queuedAlignments = queueService.queuedAlignments()

        if (queuedAlignments.size()){
            // get first from the queue (TODO: sort by date to play fair)
            nextAlignmentJob = queuedAlignments[0]
        }

        if (nextAlignmentJob != null){

            // store the filename to retrieve the dataFolderKey and extractionFolderKey
            def filename = nextAlignmentJob.name
            def name = filename.tokenize('.')[0]
            def dataFolderKey = name.tokenize('_')[0]
            def extractionFolderKey = name.tokenize('_')[1]
            def alignmentFolderKey = name.tokenize('_')[2]

            log.info("\nStarting new alignment (${name})...")

            // delete the file from the queue
            nextAlignmentJob.delete()

            // fetch dataFolder, extractionFolder and alignmentFolder
            def dataFolder = dataService.dataFolder(dataFolderKey)
            def extractionFolder = extractService.extractionFolder(dataFolderKey, extractionFolderKey)
            def alignmentFolder = alignService.alignmentFolder(dataFolderKey, extractionFolderKey, alignmentFolderKey)

            //fetch config to use
            def configFile = alignService.settingsFile(dataFolderKey, extractionFolderKey, alignmentFolderKey)

            // retrieve a list of the selected mat files to process
            def selectedMatFiles = []
            new File(extractionFolder.path + '/mat.txt').eachLine { line ->
                selectedMatFiles << line
            }

            // run parallel
            GParsPool.withPool(10) { // defines the max number of Threads to use
                selectedMatFiles.eachParallel { matFileKey ->
                    def fileToProcess = extractionFolder.files['mat'].find { it.key == matFileKey } ?: null

                    if (fileToProcess != null){
                        log.info(" --- aligning file ${fileToProcess.name}")
                        alignService.align(fileToProcess.path, configFile.canonicalPath)
                    }
                }
            }

        }
    }
}
