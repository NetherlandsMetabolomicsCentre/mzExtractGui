package nl.nmc.mzextract

//import groovyx.gpars.GParsPool
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
        if (config.os == 'lin') { command += "${config.matlab.home} " }

        // add arguments
        command += "${arguments.join(' ')}"

        // start the execution
        def proc = command.execute()
        proc.waitFor()

        // log response and any errors
        log.info  "executing: ${command}"
        if (proc.exitValue() != 0){ log.error "stderr: ${proc.err.text}" }
        log.info "stdout: ${proc.in.text}"
    }

//    def execCollection(HashMap execCollection){
//
//        GParsPool.withPool() {
//            inputFiles.eachParallel { file ->
//            }
//        }
//    }
//
}
