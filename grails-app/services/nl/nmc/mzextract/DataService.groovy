package nl.nmc.mzextract

import org.codehaus.groovy.grails.commons.ConfigurationHolder

class DataService {

    // include configuration of mzExtract
    def config = ConfigurationHolder.config.mzextract

    /*
      * Data access methods
      */
    def getFile(File file){

        def fileHash = [:]

        fileHash['key'] = file.canonicalPath.encodeAsSHA1()
        fileHash['name'] = file.name
        fileHash['path'] = file.canonicalPath
        fileHash['relpath'] = file.canonicalPath - "${config.path.project}"
        fileHash['relpathencoded'] = fileHash['relpath'].encodeAsBase64().toString()
        fileHash['updated'] = new Date(file.lastModified()).format('yyyy-MM-dd')
        return fileHash
    }

    def getFolder(File folder){

        // init + load basic file properties
        def folderHash = getFile(folder)

        // index files
        folderHash['files'] = getFilesByExtension(folder)

        // index folders
        folderHash['folders'] = []
        if (folder.exists()){
            folder.eachFile { file ->
                if (file.isDirectory()){
                    folderHash['folders'] << getFile(file)
                }
            }
        }

        return folderHash
    }

    def getFilesByExtension(File folder){

        def filesByExtension = [:]

        if (folder.exists()){
            folder.eachFile { file ->
                if (!file.isDirectory()){
                    def extension = file.name.tokenize('.')[-1].toLowerCase()

                    if (!filesByExtension[extension]) { filesByExtension[extension] = [] }

                    filesByExtension[extension] << getFile(file)
                }
            }
        }

        return filesByExtension
    }


    /*
      * Data
      */
    def dataFolders() {

        def folders = [:]
        def folderIdx = 0

        new File("${config.path.project}")?.eachFile{ entry ->
            if (entry.isDirectory() && entry.name[0] != '.'){

                // add to HashMap with folders
                folders[folderIdx] = getFolder(entry)

                // increase folderIdx
                folderIdx++
            }
        }

        return folders

    }

    def dataFolder(String dataFolderKey){

        def folder

        new File("${config.path.project}")?.eachFile{ entry ->
            if (entry.isDirectory() && entry.canonicalPath.encodeAsSHA1() == dataFolderKey){
                folder = getFolder(entry)
            }
        }

        return folder
    }
}
