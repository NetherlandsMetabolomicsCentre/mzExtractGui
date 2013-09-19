package nl.nmc.mzextract

class AlignController {

    def dataService
    def extractService
    def alignService


    // starting point, enable the user to select one or more .mat files to align them
    def select() {

        def dataFolder
        def extractFolder
        def alignFolders = []

        // check if a datafolder is selected
        if (params.dataFolderKey && params.extractFolderKey){

            // fetch datafolder
            dataFolder = dataService.dataFolder(params.dataFolderKey)

            // fetch extractFolder
            extractFolder = extractService.extractFolder(params.dataFolderKey, params.extractFolderKey)

            // fetch alignment folders
            alignFolders = alignService.alignFolders(params.dataFolderKey, params.extractFolderKey)

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

                // generate a alignment folder and return the key to pass with the redirect
                def alignFolderKey = alignService.initAlignment(params.dataFolderKey, params.extractFolderKey, uniqueFiles)

                // files selected, proceed to setting the settings
                redirect(action:'settings', params:[dataFolderKey: params.dataFolderKey, extractFolderKey: params.extractFolderKey, alignFolderKey: alignFolderKey])
            }
        }

        [ dataFolder: dataFolder, extractFolder: extractFolder,  alignFolders: alignFolders ]
    }

    def settings(){

        // (over-)write settings and return the updated values
        def existingSettings = alignService.writeSettings(params.dataFolderKey, params.extractFolderKey, params.alignFolderKey, params as HashMap)

        // check if the 'Next' button was clicked, if so go to the next page to schedule the run
        if (params['submit_next']){

            // redirect to alignment page to view the status
            redirect(action:'alignment', params:[dataFolderKey: params.dataFolderKey, extractFolderKey: params.extractFolderKey, alignFolderKey: params.alignFolderKey])
        }

        [ defaultSettings: alignService.defaultSettings(), existingSettings: existingSettings ]

    }

    def alignment(){

        def dataFolder = dataService.dataFolder(params.dataFolderKey)
        def extractFolder = extractService.extractFolder(params.dataFolderKey, params.extractFolderKey)
        def alignFolder = alignService.alignFolder(params.dataFolderKey, params.extractFolderKey, params.alignFolderKey)

        // check if the 'Align' button was clicked, if so queue it
        if (params['submit_align']){
            alignService.queue(params.dataFolderKey, params.extractFolderKey, params.alignFolderKey)
        }

        [ dataFolder: dataFolder, extractFolder: extractFolder, alignFolder: alignFolder ]
    }

    def delete(){

        def dataFolder = dataService.dataFolder(params.dataFolderKey)
        def extractFolder = extractService.extractFolder(params.dataFolderKey, params.extractFolderKey)

        // delete it
        if (params.submit_delete){
            new File(extractFolder.path).deleteDir()

            // redirect to data folder page
            redirect(controller:'data', action:'folder', params:[dataFolderKey: params.dataFolderKey])
        }
    }

}
