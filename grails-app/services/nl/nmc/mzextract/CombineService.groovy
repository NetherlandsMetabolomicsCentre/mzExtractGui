package nl.nmc.mzextract

import org.codehaus.groovy.grails.commons.ConfigurationHolder

class CombineService {

    def executionService

    // include configuration of mzExtract
    def config = ConfigurationHolder.config.mzextract

    /*
      * run combine on a collection of matlab filea using the supplied arguments
      *
      * @param configFile String
      */
    def combine(String configFile) {
        executionService.execCommand("${config.path.commandline}/${config.path.command.combine}", [configFile])
    }

}
