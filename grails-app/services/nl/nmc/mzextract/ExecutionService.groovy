package nl.nmc.mzextract

class ExecutionService {

    def grailsApplication

    def execCommand(String executable, ArrayList arguments) {

        // log response
        def response = ""

        // init command to execute
        def command = ""

        // add executable
        command += "\"${executable}\" "

        // calling MATLAB executables on Linux requires an additional argument, the path of MATLAB home
        if (grailsApplication.mzextract.os == 'lin') { command += "\"${grailsApplication.config.mzextract.matlab.home}\" " }

        // add arguments
        arguments.each { argument -> command += "\"${argument}\" " }

        try {

            // start the execution
            def proc = command.execute()
            proc.waitFor()

            if (proc.exitValue() != 0){ response += "\n --- stderr: ${proc.err.text}" }
            response += "\n  --- stdout: ${proc.in.text}"
        } catch (e) {
            log.error "execution failed of: ${command}"
            log.error "stderr: ${proc.err.text}"
            log.error " due to: ${e}"
            response += "\n  --- stderr: ${proc.err.text}"
        }

        return response
    }
}
