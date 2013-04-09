package nl.nmc.mzextract

class ProjectService {
    
    def grailsApplication 
    
    // returns a File object of the directory where the projects are
    def projectsFolder(){
        return new File(grailsApplication.config.mzextract.path.project)
    }

    // returns a list of File objecs of the projects
    def projectFolders() {
        
        def projectFolders = []
        
        projectsFolder()?.eachFile{ entry ->
            if (entry.isDirectory()){
                projectFolders << entry
            }
    	}
        
        return projectFolders
    }
    
    // translates an encoded project name to a File object of the project
    def projectFolderFromSHA1EncodedProjectName(String sha1EncodedProjectName){
        return projectFolders().find { it.name.encodeAsSHA1() == sha1EncodedProjectName }
    }
            
    // translates an encoded project name and encoded run name to a File object of the run
    def runFolderFromSHA1EncodedProjectNameAndRunName(String sha1EncodedProjectName, String sha1EncodedRunName){
        return runFoldersFromProjectFolder(projectFolderFromSHA1EncodedProjectName(sha1EncodedProjectName)).find { it.name.encodeAsSHA1() == sha1EncodedRunName }
    }    
    
    // returns a mzFile object
    def mzFileFromProjectFolder(File projectFolder){    
        return new File(projectFolder.canonicalPath + '/mzs.txt')
    }
    
    // returns a list of File objects, each representing a run done on the project data
    def runFoldersFromProjectFolder(File projectFolder){
        
        def runFolders = []
        
        def runsFolder = new File(projectFolder.canonicalPath + '/runs')
        if (!runsFolder.exists()){
            runsFolder.mkdirs()
        }
        
        runsFolder.eachFile{ entry ->
            if (entry.isDirectory()){
                runFolders << entry
            }
    	}
        
        return runFolders  
    }
    
    // returns a list of File objects, each representing a file from the run folder
    def runFolderFilesFromRunFolder(File runFolder){
        
        def runFolderFiles = []
           
        runFolder.eachFile{ entry ->
            if (!entry.isDirectory()){
                runFolderFiles << entry
            }
    	}
        
        return runFolderFiles  
    } 
    
    // returns a File object of the config file of the run
    def configFileFromRunFolder(File runFolder){
        return new File(runFolder.canonicalPath + '/config.xml')
    }
    
    // returns a list of File objects, each representing a mzXML file from the project data
    def mzxmlFilesFromProjectFolder(File projectFolder){
        
        def mzxmlFiles = []
        
        if (projectFolder.isDirectory()){
            projectFolder.eachFile { file ->
                if (!file.isDirectory()){                        
                    if (file.name.tokenize('.')[-1].toLowerCase() == 'mzxml'){
                        mzxmlFiles << file
                    }
                }
            }
        }                
        return mzxmlFiles                
    }
}
