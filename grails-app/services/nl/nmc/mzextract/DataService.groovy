package nl.nmc.mzextract

class DataService {

    def grailsApplication

    /*
      * Data access methods
      */
    def getFile(File file){

        def fileHash = [:]

        fileHash['key'] = file.canonicalPath.encodeAsSHA1()
        fileHash['name'] = file.name
        fileHash['path'] = file.canonicalPath
        fileHash['relpath'] = fileHash['path'] - "${ grailsApplication.config.mzextract.path.project}"
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

        new File("${grailsApplication.config.mzextract.path.project}")?.eachFile{ entry ->
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

        new File("${ grailsApplication.config.mzextract.path.project}")?.eachFile{ entry ->
            if (entry.isDirectory() && entry.canonicalPath.encodeAsSHA1() == dataFolderKey){
                folder = getFolder(entry)
            }
        }

        return folder
    }

    def uniqueFilesFromParams(files){

                // extract all unique keys
                def uniqueFiles = []

                // if one file is selected it returns a string, so we have to check what we are dealing with
                if (files) {
                    if (files instanceof String){
                        uniqueFiles.add(files)
                    } else {
                        uniqueFiles = files.findAll{ it }.unique()
                    }
                }

                return uniqueFiles
    }
}
