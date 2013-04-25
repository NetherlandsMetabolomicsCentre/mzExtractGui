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

    	// retrieve all runs that have status 2 (waiting to run)
        def queuedRuns = Queue.findAllByStatus(2 as int)

        if (queuedRuns){
            // get the oldest one
            def queue = queuedRuns.sort { a,b -> a.dateCreated <=> b.dateCreated }[0]
            
            // mark it as running
            queue.status = 3 as int
            queue.save(flush: true)                    

            try {
                // and run it
                def projectFolder = projectService.projectFolderFromSHA1EncodedProjectName(queue.project)
                def run = projectService.runFolderFromSHA1EncodedProjectNameAndRunName(queue.project, queue.run)
                def inputFiles = projectService.mzxmlFilesFromProjectFolder(projectFolder)  
                def configFile = runService.configFileFromRunFolder(run)
                
                def configFileText = configFile.text.encodeAsBase64().toString() 
            
                GParsPool.withPool(1) {
                    inputFiles.eachParallel { file ->
                                            
                        //stop when config changed
                        if (configFileText == runService.configFileFromRunFolder(run).text){
                        
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
                                    println " = = = ERROR = = = "
                                    println "command: ${commandExtract}"
                                    println "stdout: ${procExtract.in.text}"							
                                    println "stderr: ${procExtract.err.text}"
                                    println " = = = = = = = = = "
                                }
                            } else {
                                // for osx we simulate a processing time of x seconds
                                def delay = 20 as int
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
                        println " = = = ERROR = = = "
                        println "command: ${commandCombine}"
                        println "stdout: ${procCombine.in.text}"							
                        println "stderr: ${procCombine.err.text}"
                        println " = = = = = = = = = "
                    }
                }

                // mark it as done
                queue.status = 4 as int
                queue.save(flush: true)			
            } catch (e) {

                println e
                println e.dump()

                // mark it as failed
                queue.status = -1 as int
                queue.save(flush: true)							
            }
        }
    }
}
