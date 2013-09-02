package nl.nmc.mzextract

class AlignmentJob {
    static triggers = {
      simple repeatInterval: 5000l // execute job once in 5 seconds
    }

    def queueService

    def execute() {
        println queueService.queuedAlignments()
    }
}
