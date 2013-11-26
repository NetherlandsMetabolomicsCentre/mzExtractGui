package nl.nmc.mzextract

class AlignService {

    def dataService
    def extractService
    def queueService
    def executionService
    def sharedService
    def grailsApplication

    /*
      * returns the default alignment settings
      */
    def defaultSettings() {
      return Setting.findAllByCategory('align')
    }

    /*
      * returns the alignment config file
      */
    def settingsFile(String dataFolderKey, String extractFolderKey, String alignFolderKey){

        // load the alignment folder
        def alignFolder = alignFolder(dataFolderKey, extractFolderKey, alignFolderKey)

        return new File(alignFolder.path, 'align.xml')
    }

    /*
      * run alignment on a collection of matlab files using the supplied arguments
      *
      * @param configFile String
      */
    def align(String configFile) {
        executionService.execCommand("${grailsApplication.config.mzextract.path.commandline}/${grailsApplication.config.mzextract.path.command.align}", [configFile])
    }

    /*
      * read settings from the align.xml file in the alignment folder
      * @param dataFolderKey String
      * @param extractFolderKey String
      * @param alignFolderKey String
      */
    def readSettings(String dataFolderKey, String extractFolderKey, String alignFolderKey){

      def defaultSettings = defaultSettings()

      // init empty settings HashMap
      def settings = [:]

      // load settings file
      def settingsFile = settingsFile(dataFolderKey, extractFolderKey, alignFolderKey)

      // read old settings
      def xmlSettings = [:]
      if (settingsFile.size() > 0){
        xmlSettings = new XmlSlurper().parseText(settingsFile?.text)
      }

      // merge settings
      defaultSettings.each { s ->
        settings["${s.name}"] = xmlSettings?."${s.name}"?.text() ?: s.value
      }

      return settings
    }

    /*
      * (over)write settings to the align.xml file in the alignment folder
      * @param dataFolderKey String
      * @param extractFolderKey String
      * @param alignFolderKey String
      * @param parameters HashMap
      */
    def writeSettings(String dataFolderKey, String extractFolderKey, String alignFolderKey, HashMap parameters){

        try {

          // load the alignment folder
          def alignFolder = alignFolder(dataFolderKey, extractFolderKey, alignFolderKey)

          // load existing settings
          def existingSettings = readSettings(dataFolderKey, extractFolderKey, alignFolderKey)

          //prepare the xml
          def configXML = ""
          configXML += "<config>"
          configXML += "\n\t<created>" + new Date().time + "</created>"
          configXML += "\n\t<outputpath>" + alignFolder.path + "</outputpath>"

          // add settings from parameters or use the default value
          existingSettings.each { label, value ->
            configXML += "\n\t<" + label + ">" + (parameters[label] ?: value) + "</" + label + ">"
          }

          // close the xml
          configXML += "\n</config>"

          // write the file
          def settingsFile = settingsFile(dataFolderKey, extractFolderKey, alignFolderKey)
          settingsFile.delete()
          settingsFile << configXML


        } catch (e) {
          log.error(e.message)
        }

        return readSettings(dataFolderKey, extractFolderKey, alignFolderKey)

    }

    /*
      * add the alignment to the queue for execution
      * @param dataFolderKey String
      * @param extractFolderKey String
      * @param alignFolderKey String
      */
    def queue(String dataFolderKey, String extractFolderKey, String alignFolderKey){
        queueService.queueAlignment(dataFolderKey, extractFolderKey, alignFolderKey)
    }


    /*
      * retrieve alignments root folder
      * @param dataFolderKey String
      * @param extractFolderKey String
      */
    def alignmentsFolder(String dataFolderKey, String extractFolderKey){
        def extractFolder = extractService.extractFolder(dataFolderKey, extractFolderKey)
        return dataService.getFolder(new File(extractFolder.path, '.alignments/'))
    }

    /*
      * retrieve alignment folders
      * @param dataFolderKey String
      */
    def alignFolders(String dataFolderKey, String extractFolderKey){
        return alignmentsFolder(dataFolderKey, extractFolderKey)?.folders ?: []
    }

    /*
      * retrieve an alignment folder
      * @param dataFolderKey String
      * @param extractFolderKey String
      * @param alignFolderKey String
      */
    def alignFolder(String dataFolderKey, String extractFolderKey, String alignFolderKey){
        def alignmentFolderPath = alignFolders(dataFolderKey, extractFolderKey).find { it.key == alignFolderKey }?.path ?: null
        if (alignmentFolderPath){
            return dataService.getFolder(new File(alignmentFolderPath))
        } else {
            return null
        }
    }

    /*
      * initiate a new alignment from a list of mat files
      * @param dataFolderKey String
      * @param extractFolderKey String
      * @param matFiles ArrayList
      */
    def initAlignment(String dataFolderKey, String extractFolderKey, ArrayList matFiles){

        def newalignFolder = new File(alignmentsFolder(dataFolderKey, extractFolderKey).path + '/' + sharedService.dateFolderName())

        // make sure it is created
        newalignFolder.mkdirs()

        // read alignment folder
        def alignFolder = dataService.getFile(newalignFolder)

        // create list of mat files included in this extraction folder
        def mats = ""
        matFiles.each { matFile ->
            mats += "${matFile}\n"
        }

        new File(alignFolder.path, 'mat.txt') << mats

        // return alignFolder key
        return alignFolder.key
    }

}
