package nl.nmc.mzextract

class DoController {
    
    def ProjectService

    def home(){}
    def help(){}

    def index() {         
    	[projects: ProjectService.projectFolders().collect { it.name }]
    }

    def project(){

        def projectFolder = ProjectService.projectFolderFromSHA1EncodedProjectName(params.project)
        
    	[   
            projectFolder: projectFolder,
            projectMzxmlFiles: ProjectService.mzxmlFilesFromProjectFolder(projectFolder),
            projectRunFolders: ProjectService.runFoldersFromProjectFolder(projectFolder)
        ]
    }

    def settings(){

    	def projectFolder = ProjectService.projectFolderFromSHA1EncodedProjectName(params.project)
    	def settings = [:]
        
        if (projectFolder.isDirectory()){
            
            // read settings from params or use the default value
            settings['mstype']                  = params.mstype ?: 5
            settings['calibrationmass'] 	= params.calibrationmass ?: 1000
            settings['noisethresholdfactor'] 	= params.noisethresholdfactor ?: 10
            settings['ppmresolution'] 		= params.ppmresolution ?: 4000
            settings['centroidthreshold'] 	= params.centroidthreshold ?: 1000
            //settings['splitfeatures'] 		= params.splitfeatures ?: 1
            settings['splitratio'] 		= params.splitratio ?: 0.001
            settings['mode']                    = params.mode ?: 'positive'
            settings['sgfilt']                  = params.sgfilt ?: 1
            
            if (params.do){
                redirect(action: "schedule", params: params)
            }
        }

    	[projectFolder: projectFolder, settings: settings]
    }

    def schedule(){

    	def projectFolder = ProjectService.projectFolderFromSHA1EncodedProjectName(params.project)
    	def settings = params

        if (projectFolder.isDirectory()){

            //prepare a run directory
            def runFolder = new File(projectFolder.canonicalPath + '/runs/' + new Date().format('yyy-MM-dd_hh-mm-ss'))
            runFolder.mkdirs()				
            
            //prepare the settings file
            ProjectService.configFileFromRunFolder(runFolder) << "<config>\n\t<outputpath>" + runFolder.canonicalPath + "</outputpath>\n\t" + settings.collect { setting -> "\t<" + setting.key + ">" + setting.value + "</" + setting.key + '>'}.join("\n") + "\n</config>"

            def encodedRunFolder = runFolder.name.encodeAsSHA1()
            def encodedProjectFolder = projectFolder.name.encodeAsSHA1()
            
            //queue run
            new Queue(project: encodedProjectFolder, run: encodedRunFolder, status:0 as int).save()

            // redirect to run page
            redirect(action: "run", params: [project: encodedProjectFolder, run: encodedRunFolder])                
        }         	
    }

    def run(){
        
        def projectFolder = ProjectService.projectFolderFromSHA1EncodedProjectName(params.project)
        def run = ProjectService.runFolderFromSHA1EncodedProjectNameAndRunName(params.project, params.run)
        def outputFiles = ProjectService.runFolderFilesFromRunFolder(run)
        def inputFiles = ProjectService.mzxmlFilesFromProjectFolder(projectFolder)

        
        [projectFolder: projectFolder, run: run, outputFiles: outputFiles, inputFiles: inputFiles]
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
