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

            def logging = " -- start ${new Date()}\n"

            // store the filename to retrieve the dataFolderKey and extractFolderKey
            def filename = nextExtractionJob.name
            def name = filename.tokenize('.')[0]
            def dataFolderKey = name.tokenize('_')[0]
            def extractFolderKey = name.tokenize('_')[1]

            // delete the file from the queue
            nextExtractionJob.delete()

            // fetch dataFolder and extractFolder
            def dataFolder = dataService.dataFolder(dataFolderKey)
            def extractFolder = extractService.extractFolder(dataFolderKey, extractFolderKey)

            // include path of extraction in logging
            logging += " -- extraction folder: ${extractFolder.path}\n"

            // update status
            extractService.writeStatus(dataFolderKey, extractFolderKey, ['status':'running', 'updated': new Date(), 'logging':logging])

            //fetch config to use
            def configFile = extractService.settingsFile(dataFolderKey, extractFolderKey)
            def configFileHash = configFile.text.encodeAsBase64().toString()

            // include config of extraction in logging
            logging += "\n -- settings used \n"
            extractService.readSettings(dataFolderKey, extractFolderKey).each { label, value ->
                logging += " --- ${label}\t ${value}\n"
            }
            logging += "\n\n"


            // retrieve a list of the selected mzxml files to process
            def selectedMzxmlFiles = []
            new File(extractFolder.path + '/mzxml.txt').eachLine { line ->
                selectedMzxmlFiles << line
            }

            // run parallel
            GParsPool.withPool(10) { pool -> // defines the max number of Threads to use
                selectedMzxmlFiles.eachParallel { mzxmlFileKey ->
                //selectedMzxmlFiles.each { mzxmlFileKey ->

                    def fileToProcess = dataFolder.files['mzxml'].find { it.key == mzxmlFileKey } ?: null

                    //stop when config changed
                    if (configFileHash == extractService.settingsFile(dataFolderKey, extractFolderKey).text.encodeAsBase64().toString()){

                        // log file to logging
                        logging += "\n -- At ${new Date()} starting file ${fileToProcess.path} \n"

                        if (fileToProcess != null){
                            log.info(" - extracting file ${fileToProcess.name}")
                            logging += extractService.extract(fileToProcess.path, configFile.canonicalPath)
                        }
                    } else {
                        log.info("Skipping file ${fileToProcess.name}, config has changed!")
                    }
                }

                pool.shutdown() //remove the pool from memory
            }

            // update status
            extractService.writeStatus(dataFolderKey, extractFolderKey, ['status':'done', 'updated': new Date(), 'logging':logging])
        }
    }
}
