package nl.nmc.mzextract

import org.springframework.context.ApplicationContext
import org.codehaus.groovy.grails.commons.ApplicationHolder
import org.codehaus.groovy.grails.commons.ConfigurationHolder

class QueueJob {
    static triggers = {
      simple repeatInterval: 1000l // execute job once in 5 seconds
    }

    def execute() {

    	def config = ConfigurationHolder.config.mzextract
    	def applicationLoc = ApplicationHolder.getApplication().getMainContext().getResource("/").getFile().getAbsolutePath().replace('web-app','')

    	// retrieve all runs that have status 0 (waiting)
        def queuedRuns = Queue.findAllByStatus(0 as int)

        if (queuedRuns){
	        // get the oldest one
	        def queue = queuedRuns.sort { a,b -> a.dateCreated <=> b.dateCreated }[0]

	        // mark it as running
			queue.status = 1 as int
			queue.save(flush: true)

			try {
				// and run it
				def inputDir = new File(queue.runPath() + '/in')
				def outputDir = new File(queue.runPath() + '/out')				
				inputDir.eachFile { file ->
					if (file.name.tokenize('.')[-1].toLowerCase() == 'settings'){
						//def commandExtract = "${config.path.commandline}/${config.path.command.extract} ${file.canonicalPath}"
						def commandExtract = "${config.path.commandline}/${config.path.command.extract} ${config.matlab.home} ${file.canonicalPath}"
                                                def procExtract = commandExtract.execute()
						procExtract.waitFor()

						if (procExtract.exitValue() != 0){
							println " = = = ERROR = = = "
							println "command: ${commandExtract}"
							println "stdout: ${procExtract.in.text}"							
							println "stderr: ${procExtract.err.text}"
							println " = = = = = = = = = "
						}

						//mv .mat file to outputDir
						def ant = new AntBuilder()
						def tempFileName = file.name.replace('.settings','')
							tempFileName = tempFileName.replace(".${tempFileName.tokenize('.')[-1]}",'')
							tempFileName = tempFileName + '.mat'
						def tempFile = new File(applicationLoc + tempFileName)
						def outputFile = new File(outputDir.canonicalPath + '/' + tempFile.name)
						ant.copy(file:tempFile, tofile:outputFile, overwrite:true)
						ant.delete(file:tempFile)
					}
				}

                def matFiles = []
                outputDir.eachFile { file ->
					if (file.name.tokenize('.')[-1].toLowerCase() == 'mat'){
						matFiles << file.canonicalPath
					}
				}

                // prepare the combine file						
                def combineXML = "<config>\n\t<exportfile>"+ outputDir.canonicalPath + "/results.txt</exportfile>\n" + matFiles.collect { matFile -> "\t<filename>" + matFile + "</filename>"}.join("\n") + "\n</config>"				
                def combineXMLFile = new File(outputDir.canonicalPath + '/combine.xml') << combineXML

                // execute combine
                                //def commandCombine = "${config.path.commandline}/${config.path.command.combine} ${combineXMLFile.canonicalPath}"
                                def commandCombine = "${config.path.commandline}/${config.path.command.combine} ${config.matlab.home} ${combineXMLFile.canonicalPath}"
				def procCombine = commandCombine.execute()
				procCombine.waitFor()

				if (procCombine.exitValue() != 0){
					println " = = = ERROR = = = "
					println "command: ${commandCombine}"
					println "stdout: ${procCombine.in.text}"							
					println "stderr: ${procCombine.err.text}"
					println " = = = = = = = = = "
				}

		        // mark it as done
				queue.status = 2 as int
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