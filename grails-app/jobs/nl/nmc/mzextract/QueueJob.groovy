package nl.nmc.mzextract

import groovyx.gpars.GParsPool

import org.codehaus.groovy.grails.commons.ConfigurationHolder

class QueueJob {
    
    def projectService
    def runService
    
    static triggers = {
        simple repeatInterval: 5000l
    }

    def execute() {
        
    	def config = ConfigurationHolder.config.mzextract

    	// retrieve all runs that have status 20 (waiting to run)
        def queuedRuns = Queue.findAllByStatus(20 as int)

        if (queuedRuns){
            // get the oldest one
            def queue = queuedRuns.sort { a,b -> a.dateCreated <=> b.dateCreated }[0]
            def queueID = queue.id
            
            // mark it as running
            queue.status = 30 as int
            queue.save(flush: true)                    

            try {
                // and run it
                def projectFolder = projectService.projectFolderFromSHA1EncodedProjectName(queue.project)
                def run = projectService.runFolderFromSHA1EncodedProjectNameAndRunName(queue.project, queue.run)
                def inputFiles = projectService.mzxmlFilesFromProjectFolder(projectFolder)  
                def configFile = runService.configFileFromRunFolder(run)
                
                def configFileText = configFile.text.encodeAsBase64().toString() 
                
                queue = null // destroy object :(
            
                GParsPool.withPool(10) {
                    inputFiles.eachParallel { file ->
                                            
                        //stop when config changed
                        if (configFileText == runService.configFileFromRunFolder(run).text.encodeAsBase64().toString()){
                        
                            def commandExtract = ""
                                commandExtract += "${config.path.commandline}/${config.path.command.extract} " // add executable
                                if (config.os == 'lin') { commandExtract += "\"${config.matlab.home}\" "} // add path to MatLab for linux
                                commandExtract += "\"${file.canonicalPath}\" " // add mzXML file
                                commandExtract += "\"${configFile.canonicalPath}\" " // add config file
                            println commandExtract
                            if (config.os != 'osx'){
                                def procExtract = commandExtract.execute()
                                procExtract.waitFor()

                                if (procExtract.exitValue() != 0){
                                    //log this to a file and store it in the run directory
                                    def errorLogEntry = ""
                                    errorLogEntry += "command: ${commandExtract}"
                                    errorLogEntry += "stdout: ${procExtract.in.text}"							
                                    errorLogEntry += "stderr: ${procExtract.err.text}"                                    
                                    
                                    new File(file.canonicalPath + '.error.log') << errorLogEntry
                                }
                            } else {
                                // for osx we simulate a processing time of x seconds
                                def delay = 10 as int
                                println "Sleeping ${delay} seconds..."
                                sleep(delay*1000)
                            }
                        } else {
                            println "###########################"
                            println "STOPPING, CONFIG CHANGED!!!"
                            println "###########################"
                        }
                    }
                }

                def matFiles = []
                run.eachFile { file ->
                    if (file.name.tokenize('.')[-1].toLowerCase() == 'mat'){
                        matFiles << file.canonicalPath
                    }
                }

                // prepare the combine file						
                def combineXML = "<config>\n\t<exportfile>"+ run.canonicalPath + "/results.txt</exportfile>\n" + matFiles.collect { matFile -> "\t<filename>" + matFile + "</filename>"}.join("\n") + "\n</config>"				
                def combineXMLFile = new File(run.canonicalPath + '/combine.xml') << combineXML

                // execute combine
                def commandCombine = ""
                    commandCombine += "${config.path.commandline}/${config.path.command.combine} "
                    if (config.os == 'lin') { commandCombine += "\"${config.matlab.home}\" " }
                    commandCombine += "\"${combineXMLFile.canonicalPath}\" "
                println commandCombine
                if (config.os != 'osx'){
                    def procCombine = commandCombine.execute()
                    procCombine.waitFor()

                    if (procCombine.exitValue() != 0){
                        //log this to a file and store it in the run directory
                        def errorLogCombineEntry = ""
                        errorLogCombineEntry += "command: ${commandCombine}"
                        errorLogCombineEntry += "stdout: ${procCombine.in.text}"							
                        errorLogCombineEntry += "stderr: ${procCombine.err.text}"                                    

                        new File(run.canonicalPath + 'combine.error.log') << errorLogEntry                        
                    }
                }

                // mark it as done
                queue = Queue.get(queueID)
                if (queue){
                    queue.status = 40 as int
                    queue.save(flush: true)
                    queue = null
                }
            } catch (e) {

                //println e
                //println e.dump()

                // mark it as failed
                queue = Queue.get(queueID) 
                if (queue){
                    queue.status = -1 as int
                    queue.save(flush: true)							
                    queue = null
                }
            }
        }
    }
}
