package nl.nmc.mzextract

import org.codehaus.groovy.grails.commons.ConfigurationHolder

class DataController {

    // include configuration of mzExtract
    def config = ConfigurationHolder.config.mzextract

    def dataService

    /*
      * List root of data repository
      */
    def index() {
        [ dataFolders: dataService.dataFolders() ]
    }

    /*
      * A single dataFolder
      */
    def folder() {
        [ dataFolder: dataService.dataFolder(params.dataFolderKey) ]
    }
    
    /*
      * Download a file
      */
    def download(){

        if (params.id){
            def pathToFile = "${config.path.project}" + new String(params.id.decodeBase64())
            def download = new File(pathToFile)
            if (download.isFile()){
                response.setContentType("application/octet-stream")
                response.setHeader("Content-disposition", "attachment;filename=${download.name}")
                response.outputStream << download.bytes
            } else {
                log.error "Unable to download the file: ${pathToFile}"
            }
        } else {
            response.sendError(404, "File not found");
        }
    }
}
