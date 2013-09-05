package nl.nmc.mzextract

class CombineController {

    def combineService
    def dataService
    def extractService
    def alignService


    // starting point, enable the user to select one or more .mat files to combine them
    def select() {

        def dataFolder
        def extractionFolder
        def alignmentFolder
        def alignmentFolders = []

        // check if a datafolder is selected
        if (params.dataFolderKey && params.extractionFolderKey){

            // fetch datafolder
            dataFolder = dataService.dataFolder(params.dataFolderKey)

            // fetch extractionfolder
            extractionFolder = extractService.extractionFolder(params.dataFolderKey, params.extractionFolderKey)

            // we will select files from a folder of which has been aligned
            if (params.alignmentFolderKey){
                // fetch alignmentfolder
                alignmentFolder = alignService.alignmentFolder(params.dataFolderKey, params.extractionFolderKey, params.alignmentFolderKey)
            }

            // fetch alignment folders
            alignmentFolders = alignService.alignmentFolders(params.dataFolderKey, params.extractionFolderKey)

            // see if the user selected one or more mat files
            if (params.matfiles?.size() >= 1){

                // extract all unique keys
                def uniqueFiles = []

                // if one file is selected it returns a string, so we have to check what we are dealing with
                if (params.matfiles instanceof String){
                    uniqueFiles.add(params.matfiles)
                } else {
                    uniqueFiles = params.matfiles.findAll{ it }.unique()
                }

                // generate a combine folder and return the key to pass with the redirect
                def combineFolderKey = combineService.initCombine(params.dataFolderKey, params.extractionFolderKey, params.alignmentFolderKey, uniqueFiles)

                // files selected, proceed to setting the settings
                redirect(action:'settings', params:[dataFolderKey: params.dataFolderKey, extractionFolderKey: params.extractionFolderKey, alignmentFolderKey: params.alignmentFolderKey, combineFolderKey: combineFolderKey])
            }
        }

        [ dataFolder: dataFolder, extractionFolder: extractionFolder,  alignmentFolders: alignmentFolders ]
    }

    def settings(){

        // (over-)write settings and return the updated values
        def existingSettings = combineService.writeSettings(params.dataFolderKey, params.extractionFolderKey, params.alignmentFolderKey, params.combineFolderKey, params as HashMap)

        // check if the 'Next' button was clicked, if so go to the next page to schedule the run
        if (params['submit_next']){

            // redirect to combine page to view the status
            redirect(action:'combine', params:[dataFolderKey: params.dataFolderKey, extractionFolderKey: params.extractionFolderKey, alignmentFolderKey: params.alignmentFolderKey, combineFolderKey: params.combineFolderKey])
        }

        [ defaultSettings: combineService.defaultSettings(), existingSettings: existingSettings ]

    }

    def combine(){

        def dataFolder = dataService.dataFolder(params.dataFolderKey)
        def extractionFolder = extractService.extractionFolder(params.dataFolderKey, params.extractionFolderKey)
        def alignmentFolder = params.alignmentFolderKey ? alignService.alignmentFolder(params.dataFolderKey, params.extractionFolderKey, params.alignmentFolderKey) : []
        def combineFolder = combineService.combineFolder(params.dataFolderKey, params.extractionFolderKey, params.alignmentFolderKey, params.combineFolderKey)

        // check if the 'combine' button was clicked, if so queue it
        if (params['submit_combine']){
            combineService.queue(params.dataFolderKey, params.extractionFolderKey, params.alignmentFolderKey, params.combineFolderKey)
        }

        [ dataFolder: dataFolder, extractionFolder: extractionFolder, alignmentFolder: alignmentFolder, combineFolder: combineFolder ]
    }

    def delete(){

        def dataFolder = dataService.dataFolder(params.dataFolderKey)
        def extractionFolder = extractService.extractionFolder(params.dataFolderKey, params.extractionFolderKey)

        // delete it
        if (params.submit_delete){
            new File(extractionFolder.path).deleteDir()

            // redirect to data folder page
            redirect(controller:'data', action:'folder', params:[dataFolderKey: params.dataFolderKey])
        }
    }

}

