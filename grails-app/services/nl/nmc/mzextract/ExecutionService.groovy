package nl.nmc.mzextract

import org.codehaus.groovy.grails.commons.ConfigurationHolder

class ExecutionService {

    // include configuration of mzExtract
    def config = ConfigurationHolder.config.mzextract

    def execCommand(String executable, ArrayList arguments) {

        // init command to execute
        def command = ""

        // add executable
        command += "${executable} "

        // calling MATLAB executables on Linux requires an additional argument, the path of MATLAB home
        if (config.os == 'lin') { command += "\"${config.matlab.home}\" " }

        // add arguments
        arguments.each { argument -> command += "\"${argument}\" " }

        // start the execution
        def proc = command.execute()
        proc.waitFor()

        // log response and any errors
        log.info "################################################################"
        log.info "/> ${command}"
        if (proc.exitValue() != 0){ log.error "stderr: ${proc.err.text}" }
        log.info "stdout: ${proc.in.text}"
        log.info "################################################################"
        log.info " "
    }
}
