package nl.nmc.mzextract

class DoController {
    
    def projectService
    def runService

    def home(){}
    def help(){}

    def index() {         
    	[projects: projectService.projectFolders().collect { it.name }]
    }

    def project(){

        def projectFolder = projectService.projectFolderFromSHA1EncodedProjectName(params.project)
        
    	[   
            projectFolder: projectFolder,
            projectMzxmlFiles: projectService.mzxmlFilesFromProjectFolder(projectFolder),
            projectMzFile: projectService.mzFileFromProjectFolder(projectFolder)
        ]
    }

    def run(){
                
        def project = projectService.projectFolderFromSHA1EncodedProjectName(params.project)
        def run
        
        if (!params.run){
            run = runService.initRun(project)
            flash.message = 'Change settings using the <i class="icon-cog"></i> button'
        } else {
            run = projectService.runFolderFromSHA1EncodedProjectNameAndRunName(params.project, params.run)           
        }
        
        def settings = runService.settings()
        if (params.do){
            // parse the parameters and save them to the XML file
            settings = runService.writeSettings(project, run, params)
        }
                
        [project: project, run: run, settings: settings]
    } 
    
    def delrun(){
        
        def run = projectService.runFolderFromSHA1EncodedProjectNameAndRunName(params.project, params.run)

        // delete the run directory. This can cause problems when you delete a run which is still running!!!
        if (run.deleteDir()){
            Queue.findByProjectAndRun(params.project, params.run)?.delete()
        }
        
        // redirect to project page
        redirect(action: "project", params: [project: params.project])                        
    }
    
    def stoprun(){
        def project = projectService.projectFolderFromSHA1EncodedProjectName(params.project)        
        def run = projectService.runFolderFromSHA1EncodedProjectNameAndRunName(params.project, params.run)
        
        // just re-write the existing config to change the file.date which will stop the Job from processing new files
        runService.writeSettings(project, run, [:])
        
        def queue = Queue.findByProjectAndRun(params.project, params.run)
        if (queue){
            queue.status = 11 as int
            queue.save(flush: true)
        }        
        
        // redirect to run page
        redirect(action: "run", params: [project: params.project, run: params.run])        
    }
    
    def queue(){
        
        def run = projectService.runFolderFromSHA1EncodedProjectNameAndRunName(params.project, params.run)

        // delete all non xml files from the output directory.
        projectService.runFolderFilesFromRunFolder(run).each { outFile ->
            if (outFile.name != 'config.xml'){
                outFile.delete()
            }        
        }
        
        def queue = Queue.findByProjectAndRun(params.project, params.run)
        if (queue){
            queue.status = 20 as int
            queue.save(flush: true)
        } else {
            new Queue(project: params.project, run: params.run, status:20 as int).save()
        }
        
        // redirect to run page
        redirect(action: "run", params: [project: params.project, run: params.run])                        
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
    
    def runDetails(){ 
        render mzextract.runDetails(projectSha1: params.projectSha1, runSha1: params.runSha1)
    }
}
