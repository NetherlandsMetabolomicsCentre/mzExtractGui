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
            projectMzFile: ProjectService.mzFileFromProjectFolder(projectFolder)
        ]
    }

    def run(){
        
        def project = ProjectService.projectFolderFromSHA1EncodedProjectName(params.project)
        def run = ProjectService.runFolderFromSHA1EncodedProjectNameAndRunName(params.project, params.run) ?: null
        
        // create the required directories before we continue
        if (run == null){
            //prepare a run directory
            run = new File(project.canonicalPath + '/runs/' + new Date().format('yyyy-MM-dd_hh-mm-ss'))
            run.mkdirs()

            def runSha1 = run.name.encodeAsSHA1()
            def projectSha1 = project.name.encodeAsSHA1()                

            //queue run
            new Queue(project: projectSha1, run: runSha1, status:1 as int).save()
        }        
        
    	def settings = RunService.settings().sort { a,b -> a.name <=> b.name }        
        def outputFiles = ProjectService.runFolderFilesFromRunFolder(run)
        def inputFiles = ProjectService.mzxmlFilesFromProjectFolder(project)
        
        if (project?.isDirectory()){

            if (params.do){
                //prepare the settings file
                def configXML = ""
                configXML += "<config>\n"
                configXML += "\t<outputpath>" + run.canonicalPath + "</outputpath>\n"
                configXML += settings.collect { setting -> "\t<" + setting.name + ">" + (params[setting.name] ?: setting.default) + "</" + setting.name + '>'}.join("\n")
                if (params['usemz'].split(',')[0] == 'yes'){
                    def mzFile = ProjectService.mzFileFromProjectFolder(project)
                    if (mzFile.exists()){
                        configXML += "\n\t<mzfile>" + mzFile.canonicalPath + "</mzfile>"
                    }
                }
                configXML += "\n</config>"

                // save XML to config file
                def configFile = ProjectService.configFileFromRunFolder(run)
                if (configFile.exists()){
                    configFile.delete()
                }
                configFile << configXML
            }
        }
                
        [projectFolder: project, run: run, settings: settings, outputFiles: outputFiles, inputFiles: inputFiles]
    } 
    
    def delrun(){
        
        def run = ProjectService.runFolderFromSHA1EncodedProjectNameAndRunName(params.project, params.run)

        // delete the run directory. This can cause problems when you delete a run which is still running!!!
        if (run.deleteDir()){
            Queue.findByProjectAndRun(params.project, params.run)?.delete()
        }
        
        // redirect to project page
        redirect(action: "project", params: [project: params.project])                        
    }
    
    def queue(){
        
        def run = ProjectService.runFolderFromSHA1EncodedProjectNameAndRunName(params.project, params.run)

        // delete all non xml files from the output directory.
        ProjectService.runFolderFilesFromRunFolder(run).each { outFile ->
            if (outFile.name.tokenize('.')[-1].toLowerCase() != 'xml'){
                outFile.delete()
            }        
        }
        
        def queue = Queue.findByProjectAndRun(params.project, params.run)
        if (queue){
            queue.status = 2 as int
            queue.save(flush: true)
        } else {
            new Queue(project: params.project, run: params.run, status:2 as int).save()
        }
        
        // redirect to project page
        redirect(action: "project", params: [project: params.project])                        
    }    

    def download(){

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
