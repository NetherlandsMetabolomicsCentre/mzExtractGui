package nl.nmc.mzextract

import javax.servlet.http.*

class DataController {

    def grailsApplication

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
            def pathToFile = "${grailsApplication.config.mzextract.path.project}" + new String(params.id.decodeBase64())
            def download = new File(pathToFile)
            if (download.isFile()){
                response.setContentType("application/octet-stream")
                response.setHeader("Content-disposition", "attachment;filename=${download.name}")
                response.outputStream << download.bytes
            } else {
                log.error "Unable to download the file: ${pathToFile}"
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "File not found");
            }
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid request");
        }
    }
}
