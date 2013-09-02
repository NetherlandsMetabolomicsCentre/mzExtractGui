package nl.nmc.mzextract

import org.codehaus.groovy.grails.commons.ConfigurationHolder

class AlignService {

    def executionService

    // include configuration of mzExtract
    def config = ConfigurationHolder.config.mzextract

    /*
      * run extraction on a collection of matlab filea using the supplied arguments
      *
      * @param configFile String
      */
    def align(String configFile) {
        executionService.execCommand("${config.path.commandline}/${config.path.command.align}", [configFile])
    }

}
