package nl.nmc.mzextract

import groovyx.gpars.GParsPool

class ExtractionJob {
    static triggers = {
      simple repeatInterval: 5000l // execute job once in 5 seconds
    }

    def queueService
    def dataService
    def extractService

    def execute() {

        def nextExtractionJob = null
        def queuedExtractions = queueService.queuedExtractions()

        if (queuedExtractions.size()){
            // get first from the queue (TODO: sort by date to play fair)
            nextExtractionJob = queuedExtractions[0]
        }

        if (nextExtractionJob != null){

            // store the filename to retrieve the dataFolderKey and extractionFolderKey
            def filename = nextExtractionJob.name
            def name = filename.tokenize('.')[0]
            def dataFolderKey = name.tokenize('_')[0]
            def extractionFolderKey = name.tokenize('_')[1]

            log.info("\nStarting new extraction (${name})...")

            // delete the file from the queue
            nextExtractionJob.delete()

            // fetch dataFolder and extractionFolder
            def dataFolder = dataService.dataFolder(dataFolderKey)
            def extractionFolder = extractService.extractionFolder(dataFolderKey, extractionFolderKey)

            //fetch config to use
            def configFile = extractService.settingsFile(dataFolderKey, extractionFolderKey)

            // retrieve a list of the selected mzxml files to process
            def selectedMzxmlFiles = []
            new File(extractionFolder.path + '/mzxml.txt').eachLine { line ->
                selectedMzxmlFiles << line
            }

            // run parallel
            GParsPool.withPool(10) { // defines the max number of Threads to use
                selectedMzxmlFiles.eachParallel { mzxmlFileKey ->
                    def fileToProcess = dataFolder.files['mzxml'].find { it.key == mzxmlFileKey } ?: null

                    if (fileToProcess != null){
                        log.info(" --- extracting file ${fileToProcess.name}")
                        extractService.extract(fileToProcess.path, configFile.canonicalPath)
                    }
                }
            }

        }
    }
}
