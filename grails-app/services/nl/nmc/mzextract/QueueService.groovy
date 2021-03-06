package nl.nmc.mzextract

class QueueService {

    def grailsApplication
    def sharedService

    /*
      * folder with queued extractions
      */
    def extractionsQueueFolder(){
        def queuePath = "${  grailsApplication.config.mzextract.path.project}/.queue/extractions"
        // make sure it exists
        new File(queuePath).mkdirs()
        return queuePath
    }

    /*
      * folder with queued alignments
      */
    def alignmentsQueueFolder(){
        def queuePath = "${  grailsApplication.config.mzextract.path.project}/.queue/alignments"
        // make sure it exists
        new File(queuePath).mkdirs()
        return queuePath
    }

    /*
      * folder with queued combines
      */
    def combinesQueueFolder(){
        def queuePath = "${  grailsApplication.config.mzextract.path.project}/.queue/combines"
        // make sure it exists
        new File(queuePath).mkdirs()
        return queuePath
    }

    /*
      * add the extraction to the queue for execution
      * @param dataFolderKey String
      * @param extractFolderKey String
      */
    def queueExtraction(String dataFolderKey, String extractFolderKey) {

        def queueFile = new File(extractionsQueueFolder(), "${dataFolderKey}_${extractFolderKey}.job")
        queueFile << "Start Extraction log ${sharedService.dateFolderName()}"

        return true
    }

    /*
      * add the alignment to the queue for execution
      * @param dataFolderKey String
      * @param extractFolderKey String
      * @param alignFolderKey String
      */
    def queueAlignment(String dataFolderKey, String extractFolderKey, String alignFolderKey) {

        def queueFile = new File(alignmentsQueueFolder(),  "${dataFolderKey}_${extractFolderKey}_${alignFolderKey}.job")
        queueFile << "Start Alignment log ${sharedService.dateFolderName()}"

        return true
    }

    /*
      * add the combine to the queue for execution
      * @param dataFolderKey String
      * @param extractFolderKey String
      * @param alignFolderKey String
      * @param combineFolderKey String
      */
    def queueCombine(String dataFolderKey, String extractFolderKey, String alignFolderKey, String combineFolderKey) {

        def queueFile = new File(combinesQueueFolder(), "${dataFolderKey ?: 'null'}_${extractFolderKey ?: 'null'}_${alignFolderKey ?: 'null'}_${combineFolderKey ?: 'null'}.job")
        queueFile << "Start Combine log ${sharedService.dateFolderName()}"

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
