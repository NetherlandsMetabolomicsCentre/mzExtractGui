package nl.nmc.mzextract

class DoController {
    
    def ProjectService
    def RunService

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
            projectRunFolders: ProjectService.runFoldersFromProjectFolder(projectFolder),
            projectMzFile: ProjectService.mzFileFromProjectFolder(projectFolder)
        ]
    }

    def settings(){

    	def projectFolder = ProjectService.projectFolderFromSHA1EncodedProjectName(params.project)
    	def settings = RunService.settings().sort { a,b -> a.name <=> b.name }
        
        if (projectFolder?.isDirectory()){
            if (params.do){
                redirect(action: "schedule", params: params)
            }
        }

    	[projectFolder: projectFolder, settings: settings]
    }

    def schedule(){

    	def projectFolder = ProjectService.projectFolderFromSHA1EncodedProjectName(params.project)
    	def settings = RunService.settings().sort { a,b -> a.name <=> b.name }

        if (projectFolder?.isDirectory()){

            //prepare a run directory
            def runFolder = new File(projectFolder.canonicalPath + '/runs/' + new Date().format('yyyy-MM-dd_hh-mm-ss'))
            runFolder.mkdirs()		
                        
            //prepare the settings file
            def configXML = ""
                configXML += "<config>\n"
                configXML += "\t<outputpath>" + runFolder.canonicalPath + "</outputpath>\n"
                configXML += settings.collect { setting -> "\t<" + setting.name + ">" + (params[setting.name] ?: setting.default) + "</" + setting.name + '>'}.join("\n")
                if (params['usemz'].split(',')[0] == 'yes'){
                    def mzFile = ProjectService.mzFileFromProjectFolder(projectFolder)
                    if (mzFile.exists()){
                        configXML += "\n\t<mzfile>" + mzFile.canonicalPath + "</mzfile>"
                    }
                }
                configXML += "\n</config>"
                
            ProjectService.configFileFromRunFolder(runFolder) << configXML

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
    
    def delrun(){
        
        def run = ProjectService.runFolderFromSHA1EncodedProjectNameAndRunName(params.project, params.run)

        // delete the run directory. This can cause problems when you delete a run which is still running!!!
        run.deleteDir()
        
        // redirect to project page
        redirect(action: "project", params: [project: params.project])                        
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
