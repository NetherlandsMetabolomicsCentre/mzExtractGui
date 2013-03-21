package nl.nmc.mzextract

class DoController {

    def home(){}
    def help(){}

    def index() { 

    	// projects
		def projects = [:] //[name:path]

    	new File(grailsApplication.config.mzextract.path.project).eachFile{ entry ->
    		if (entry.isDirectory()){
    			projects[entry.name] = entry
    		}
    	}

    	[projects: projects]
    }

    def project(){

    	def project
    	def files = []
        def runs = []

    	if (params.id){

            //read the project
    		project = new File(new String(params.id.decodeBase64()))
    		if (project.isDirectory()){
				project.eachFile { file ->
                    if (!file.isDirectory()){                        
    					if (file.name.tokenize('.')[-1].toLowerCase() == 'mzxml'){
    						files << file
    					}
                    }
    			}
    		}

            //read the project runs
            def projectRuns = new File(project.canonicalPath + '/runs')
            if (projectRuns.isDirectory()){
                projectRuns.eachFile { runDirectory ->
                    if (runDirectory.isDirectory()){
                        runs << runDirectory
                    }
                }
            }
    	}

    	[project: project, files: files, runs: runs]
    }

    def settings(){

    	def project
    	def settings = [:]

    	if (params.id){
    		project = new File(new String(params.id.decodeBase64()))
			if (project.isDirectory()){

		    	// read settings from params or use the default value
		    	settings['mstype'] 					= params.mstype ?: 5
		    	settings['calibrationmass'] 		= params.calibrationmass ?: 1000
		    	settings['noisethresholdfactor'] 	= params.noisethresholdfactor ?: 10
		    	settings['ppmresolution'] 			= params.ppmresolution ?: 4000
		    	settings['centroidthreshold'] 		= params.centroidthreshold ?: 1000
		    	settings['splitfeatures'] 			= params.splitfeatures ?: 1
		    	settings['splitratio'] 				= params.splitratio ?: 0.001

		    	if (params.do == 'run'){
		    		redirect(action: "schedule", id: params.id, params: settings)
		    	}
			}
    	}

    	[project: project, settings: settings]
    }

    def schedule(){

    	def project
    	def settings = params

    	if (params.id){
    		project = new File(new String(params.id.decodeBase64()))
			if (project.isDirectory()){

				//prepare a run directory
				def runDir 		= new File(project.canonicalPath + '/runs/' + UUID.randomUUID().toString())
				runDir.mkdirs()				
				
				//prepare an input directory				
				def inputDir 	= new File(runDir.canonicalPath + '/in')
				inputDir.mkdirs()

				//prepare an output directory
				def outputDir 	= new File(runDir.canonicalPath + '/out')
				outputDir.mkdirs()

				//prepare the settings file
				def settingsXML = "<config>\n\t<filename>@@FILENAME</filename>\n" + settings.collect { setting -> "\t<" + setting.key + ">" + setting.value + "</" + setting.key + '>'}.join("\n") + "\n</config>"

				//copy mzXML files
				def ant = new AntBuilder()

    			project.eachFile { file ->
					if (file.name.tokenize('.')[-1].toLowerCase() == 'mzxml'){					
    					def inputFile = new File(inputDir.canonicalPath + '/' + file.name)
    					ant.copy(file : file, tofile : inputFile, overwrite : true)
    					new File(inputFile.canonicalPath + '.settings') << settingsXML.replace('@@FILENAME', inputFile.canonicalPath)
    				}
    			}                               

                // set encodedRunDir
                def encodedRunDir = runDir.canonicalPath.bytes.encodeBase64().toString()

                //queue run
                new Queue(run:encodedRunDir, status:0 as int).save()

                // redirect to run page
                redirect(action: "run", id: encodedRunDir)                
    			
			}
		}           	
    }

    def run(){

        def run
        def project
        def status = null
        def outputFiles = []
        def inputFiles = []

        if (params.id){
            status = Queue.findByRun(params.id)?.status
            run = new File(new String(params.id.decodeBase64()))
            project = new File(run.canonicalPath.split('/runs')[0])
            if (run.isDirectory()){
                def outputDir = new File(run.canonicalPath + '/out')
                if (outputDir.isDirectory()){                
                    outputDir.eachFile { file ->
                        if (!file.isDirectory()){                        
                            outputFiles << file
                        }
                    }
                }
                def inputDir = new File(run.canonicalPath + '/in')
                if (inputDir.isDirectory()){                
                    inputDir.eachFile { file ->
                        if (!file.isDirectory()){                        
                            inputFiles << file
                        }
                    }
                }
            }
        }        

        [project: project, run: run, status: status, outputFiles: outputFiles, inputFiles: inputFiles]
    } 

    def download(){

        def run
        def project
        def status = null
        def outputFiles = []
        def inputFiles = []

        if (params.id){
            def download = new File(new String(params.id.decodeBase64()))
            if (download.isFile()){
                response.setContentType("application/octet-stream") // or or image/JPEG or text/xml or whatever type the file is
                response.setHeader("Content-disposition", "attachment;filename=${download.name}")
                response.outputStream << download.bytes
            } else {
              render "was unable to download the file" // appropriate error handling            
            }
        } else {
            render "file not found"
        }
    }
}
