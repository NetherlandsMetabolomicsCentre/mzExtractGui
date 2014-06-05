package nl.nmc.mzextract

class CombineController {

    def combineService
    def dataService
    def extractService
    def alignService


    // starting point, enable the user to select one or more .mat files to combine them
    def select() {

        def dataFolder
        def extractFolder
        def alignFolder
        def alignFolders = []

        // check if a datafolder is selected
        if (params.dataFolderKey && params.extractFolderKey){

            // fetch datafolder
            dataFolder = dataService.dataFolder(params.dataFolderKey)

            // fetch extractFolder
            extractFolder = extractService.extractFolder(params.dataFolderKey, params.extractFolderKey)

            // we will select files from a folder of which has been aligned
            if (params.alignFolderKey){
                // fetch alignFolder
                alignFolder = alignService.alignFolder(params.dataFolderKey, params.extractFolderKey, params.alignFolderKey)
            }

            // fetch alignment folders
            alignFolders = alignService.alignFolders(params.dataFolderKey, params.extractFolderKey)

            // see if the user selected one or more mat files
            if (params.matfiles?.size() >= 1){

                // extract all unique keys
                def uniqueFiles = dataService.uniqueFilesFromParams(params.matfiles)

                // generate a combine folder and return the key to pass with the redirect
                def combineFolderKey = combineService.initCombine(params.dataFolderKey, params.extractFolderKey, params.alignFolderKey, uniqueFiles)

                // files selected, proceed to setting the settings
                redirect(action:'settings', params:[dataFolderKey: params.dataFolderKey, extractFolderKey: params.extractFolderKey, alignFolderKey: params.alignFolderKey, combineFolderKey: combineFolderKey])
            }
        }

        [ dataFolder: dataFolder, extractFolder: extractFolder,  alignFolders: alignFolders ]
    }

    def settings(){

        // (over-)write settings and return the updated values
        def existingSettings = combineService.writeSettings(params.dataFolderKey, params.extractFolderKey, params.alignFolderKey, params.combineFolderKey, params as HashMap)

        // check if the 'Next' button was clicked, if so go to the next page to schedule the run
        if (params['submit_next']){

            // redirect to combine page to view the status
            redirect(action:'combine', params:[dataFolderKey: params.dataFolderKey, extractFolderKey: params.extractFolderKey, alignFolderKey: params.alignFolderKey, combineFolderKey: params.combineFolderKey])
        }

        [ defaultSettings: combineService.defaultSettings(), existingSettings: existingSettings ]

    }

    def combine(){

        def dataFolder = dataService.dataFolder(params.dataFolderKey)
        def extractFolder = extractService.extractFolder(params.dataFolderKey, params.extractFolderKey)
        def alignFolder = params.alignFolderKey ? alignService.alignFolder(params.dataFolderKey, params.extractFolderKey, params.alignFolderKey) : []
        def combineFolder = combineService.combineFolder(params.dataFolderKey, params.extractFolderKey, params.alignFolderKey, params.combineFolderKey)

        // check if the 'combine' button was clicked, if so queue it
        if (params['submit_combine']){
            combineService.queue(params.dataFolderKey, params.extractFolderKey, params.alignFolderKey, params.combineFolderKey)
        }

        [ dataFolder: dataFolder, extractFolder: extractFolder, alignFolder: alignFolder, combineFolder: combineFolder ]
    }

    def delete(){

        def dataFolder = dataService.dataFolder(params.dataFolderKey)
        def extractFolder = extractService.extractFolder(params.dataFolderKey, params.extractFolderKey)
        def alignFolder = params.alignFolderKey ? alignService.alignFolder(params.dataFolderKey, params.extractFolderKey, params.alignFolderKey) : []
        def combineFolder = combineService.combineFolder(params.dataFolderKey, params.extractFolderKey, params.alignFolderKey, params.combineFolderKey)

        // delete it
        if (params.submit_delete){
            new File(combineFolder.path).deleteDir()

            // redirect to alignment page
            if (alignFolder?.key){
                redirect(controller:'align', action:'alignment', params:[dataFolderKey: params.dataFolderKey, extractFolderKey: extractFolder.key, alignFolderKey: alignFolder.key])
            } else {
                redirect(controller:'extract', action:'extraction', params:[dataFolderKey: params.dataFolderKey, extractFolderKey: extractFolder.key])
            }
        }
    }
}

