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
      render(data.dataFolderFiles(dataFolderKeys:params))
    }

    /*
     * renders extract buttons
     */
    def extractButtons(){
        render common.extractButtonData(dataFolderKey:params.dataFolderKey, extractFolderKey:params.extractFolderKey)
    }
    
    /*
     * renders combine buttons
     */
    def combineButtons(){
        render common.combineButtonData(dataFolderKey:params.dataFolderKey, extractFolderKey:params.extractFolderKey, alignFolderKey:params.alignFolderKey, combineFolderKey:params.combineFolderKey)
    }    
}
