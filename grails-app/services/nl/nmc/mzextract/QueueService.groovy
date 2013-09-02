package nl.nmc.mzextract

import org.codehaus.groovy.grails.commons.ConfigurationHolder

class QueueService {

    // include configuration of mzExtract
    def config = ConfigurationHolder.config.mzextract


    /*
      * folder with queued extractions
      */
    def extractionsQueueFolder(){
        def queuePath = "${config.path.project}/.queue/extractions"
        // make sure it exists
        new File(queuePath).mkdirs()
        return queuePath
    }

    /*
      * folder with queued alignments
      */
    def alignmentsQueueFolder(){
        def queuePath = "${config.path.project}/.queue/alignments"
        // make sure it exists
        new File(queuePath).mkdirs()
        return queuePath
    }

    /*
      * folder with queued combines
      */
    def combinesQueueFolder(){
        def queuePath = "${config.path.project}/.queue/combines"
        // make sure it exists
        new File(queuePath).mkdirs()
        return queuePath
    }

    /*
      * add the extraction to the queue for execution
      * @param dataFolderKey String
      * @param extractionFolderKey String
      */
    def queueExtraction(String dataFolderKey, String extractionFolderKey) {

        def queueFile = new File(extractionsQueueFolder() + '/' + dataFolderKey + '_' + extractionFolderKey + '.job')
        queueFile << "Start Extraction log ${new Date().format('yyyy-MM-dd_HH-mm-ss')}"

        return true
    }

    /*
      * add the alignment to the queue for execution
      * @param dataFolderKey String
      * @param alignmentFolderKey String
      */
    def queueAlignment(String dataFolderKey, String alignmentFolderKey) {

        def queueFile = new File(alignmentsQueueFolder() + '/' + dataFolderKey + '_' + alignmentFolderKey + '.job')
        queueFile << "Start Alignment log ${new Date().format('yyyy-MM-dd_HH-mm-ss')}"

        return true
    }

    /*
      * add the combine to the queue for execution
      * @param dataFolderKey String
      * @param combineFolderKey String
      */
    def queueCombine(String dataFolderKey, String combineFolderKey) {

        def queueFile = new File(alignmentsQueueFolder() + '/' + dataFolderKey + '_' + combineFolderKey + '.job')
        queueFile << "Start Combine log ${new Date().format('yyyy-MM-dd_HH-mm-ss')}"

        return true
    }

    def queuedExtractions(){
        def extractionJobs = []
        new File(extractionsQueueFolder())?.eachFile { extractionJobFile ->
            extractionJobs << extractionJobFile
        }
        return extractionJobs
    }

    def queuedAlignments(){
        def alignmentJobs = []
        new File(alignmentsQueueFolder())?.eachFile { alignmentJobFile ->
            alignmentJobs << alignmentJobFile
        }
        return alignmentJobs
    }

    def queuedCombines(){
        def combineJobs = []
        new File(combinesQueueFolder())?.eachFile { combineJobFile ->
            combineJobs << combineJobFile
        }
        return combineJobs
    }
}
