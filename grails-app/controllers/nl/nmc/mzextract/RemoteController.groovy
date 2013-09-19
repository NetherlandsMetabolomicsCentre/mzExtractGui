package nl.nmc.mzextract

class RemoteController {

    def dataService
    def extractService
    def alignService    
    def combineService    
    
    /*
     * renders a list of files with the data folder
     */
    def filesList() {
      render(data.dataFolderFiles(dataFolderKeys:params) + "<br />${new Date()}")      
    }
    
    /*
     * renders extract buttons
     */
    def extractButtons(){
        render common.extractButtonData(dataFolderKey:params.dataFolderKey, extractFolderKey:params.extractFolderKey)
    }
}
