package nl.nmc.mzextract

class ExtractController {

    def dataService
    def extractService

    // starting point, enable the user to select one or more mzXML files to extract features from
    def select() {

        def dataFolder
        def extractionFolders = []

        // check if a datafolder is selected
        if (params.dataFolderKey){

            // fetch datafolder
            dataFolder = dataService.dataFolder(params.dataFolderKey)

            // fetch extraction folders
            extractionFolders = extractService.extractionFolders(dataFolder.key)

            // see if the user selected one or more mzXML files
            if (params.mzxmlfiles?.size() >= 1){

                // extract all unique keys
                def uniqueFiles = []

                // if one file is selected it returns a string, so we have to check what we are dealing with
                if (params.mzxmlfiles instanceof String){
                    uniqueFiles.add(params.mzxmlfiles)
                } else {
                    uniqueFiles = params.mzxmlfiles.findAll{ it }.unique()
                }

                // generate a extraction folder and return the key to pass with the redirect
                def extractionFolderKey = extractService.initExtraction(dataFolder.key, uniqueFiles)

                // files selected, proceed to setting the settings
                redirect(action:'settings', params:[dataFolderKey: dataFolder.key, extractionFolderKey: extractionFolderKey])
            }
        }

        [ dataFolder: dataFolder, extractionFolders: extractionFolders ]
    }

    def settings(){

        // (over-)write settings and return the updated values
        def existingSettings = extractService.writeSettings(params.dataFolderKey, params.extractionFolderKey, params as HashMap)

        // check if the 'Next' button was clicked, if so go to the next page to schedule the run
        if (params['submit_next']){

            // redirect to extraction page to view the status
            redirect(action:'extraction', params:[dataFolderKey: params.dataFolderKey, extractionFolderKey: params.extractionFolderKey])
        }

        [ defaultSettings: extractService.defaultSettings(), existingSettings: existingSettings ]

    }

    def extraction(){

        def dataFolder = dataService.dataFolder(params.dataFolderKey)
        def extractionFolder = extractService.extractionFolder(params.dataFolderKey, params.extractionFolderKey)

        // check if the 'Run' button was clicked, if so queue it
        if (params['submit_extract']){
            extractService.queue(params.dataFolderKey, params.extractionFolderKey)
        }

        [ dataFolder: dataFolder, extractionFolder: extractionFolder ]
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
