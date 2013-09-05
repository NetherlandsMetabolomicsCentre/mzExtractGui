package nl.nmc.mzextract

import org.codehaus.groovy.grails.commons.ConfigurationHolder

class CombineService {

    def dataService
    def extractService
    def alignService
    def queueService
    def executionService

    // include configuration of mzExtract
    def config = ConfigurationHolder.config.mzextract

    /*
      * returns the default combine settings
      */
    def defaultSettings() {
      return Setting.findAllByCategory('combine')
    }

    /*
      * returns the combine config file
      */
    def settingsFile(String dataFolderKey, String extractionFolderKey, String alignmentFolderKey, String combineFolderKey){

        // load the combine folder
        def combineFolder = combineFolder(dataFolderKey, extractionFolderKey, alignmentFolderKey, combineFolderKey)

        return new File(combineFolder.path + '/combine.xml')
    }

    /*
      * run combine on a collection of matlab files using the supplied arguments
      *
      * @param configFile String
      */
    def combine(String configFile) {
        executionService.execCommand("${config.path.commandline}/${config.path.command.combine}", [configFile])
    }

    /*
      * read settings from the combine.xml file in the combine folder
      * @param dataFolderKey String
      * @param extractionFolderKey String
      * @param alignmentFolderKey String
      * @param combineFolderKey String
      */
    def readSettings(String dataFolderKey, String extractionFolderKey, String alignmentFolderKey = null, String combineFolderKey){

      def defaultSettings = defaultSettings()

      // init empty settings HashMap
      def settings = [:]

      // load settings file
      def settingsFile = settingsFile(dataFolderKey, extractionFolderKey, alignmentFolderKey, combineFolderKey)

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
      * (over)write settings to the combine.xml file in the combine folder
      * @param dataFolderKey String
      * @param extractionFolderKey String
      * @param alignmentFolderKey String
      * @param combineFolderKey String
      * @param parameters HashMap
      */
    def writeSettings(String dataFolderKey, String extractionFolderKey, String alignmentFolderKey = null, String combineFolderKey, HashMap parameters){

        try {

          // load the combine folder
          def combineFolder = combineFolder(dataFolderKey, extractionFolderKey, alignmentFolderKey, combineFolderKey)

          // load existing settings
          def existingSettings = readSettings(dataFolderKey, extractionFolderKey, alignmentFolderKey, combineFolderKey)

          //prepare the xml
          def configXML = ""
          configXML += "<config>"
          configXML += "\n\t<created>" + new Date().time + "</created>"
          configXML += "\n\t<outputpath>" + combineFolder.path + "</outputpath>"

          // add settings from parameters or use the default value
          existingSettings.each { label, value ->
            configXML += "\n\t<" + label + ">" + (parameters[label] ?: value) + "</" + label + ">"
          }

          // close the xml
          configXML += "\n</config>"

          // write the file
          def settingsFile = settingsFile(dataFolderKey, extractionFolderKey, alignmentFolderKey, combineFolderKey)
          settingsFile.delete()
          settingsFile << configXML


        } catch (e) {
          log.error(e.message)
        }

        return readSettings(dataFolderKey, extractionFolderKey, alignmentFolderKey, combineFolderKey)

    }

    /*
      * add the combine to the queue for execution
      * @param dataFolderKey String
      * @param extractionFolderKey String
      * @param alignmentFolderKey String
      * @param combineFolderKey String
      */
    def queue(String dataFolderKey, String extractionFolderKey, String alignmentFolderKey, String combineFolderKey){
        queueService.queueCombine(dataFolderKey, extractionFolderKey, alignmentFolderKey, combineFolderKey)
    }


    /*
      * retrieve combines root folder
      * @param dataFolderKey String
      * @param extractionFolderKey String
      * @param alignmentFolderKey String
      */
    def combinesFolder(String dataFolderKey, String extractionFolderKey, String alignmentFolderKey = null){

        def combinesFolder

        def extractionFolder = extractService.extractionFolder(dataFolderKey, extractionFolderKey)

        if (alignmentFolderKey){
          combinesFolder = dataService.getFolder(new File(alignService.alignmentFolder(dataFolderKey, extractionFolderKey, alignmentFolderKey).path + '/.combines/'))
        } else {
          combinesFolder = dataService.getFolder(new File(extractionFolder.path + '/.combines/'))
        }

        return combinesFolder
    }

    /*
      * retrieve combine folders
      * @param dataFolderKey String
      */
    def combineFolders(String dataFolderKey, String extractionFolderKey, String alignmentFolderKey = null){
        return combinesFolder(dataFolderKey, extractionFolderKey, alignmentFolderKey)?.folders ?: []
    }

    /*
      * retrieve an combine folder
      * @param dataFolderKey String
      * @param extractionFolderKey String
      * @param alignmentFolderKey String
      * @param combineFolderKey String
      */
    def combineFolder(String dataFolderKey, String extractionFolderKey, String alignmentFolderKey = null, String combineFolderKey){
        return dataService.getFolder(new File(combineFolders(dataFolderKey, extractionFolderKey, alignmentFolderKey).find { it.key == combineFolderKey }.path))
    }

    /*
      * initiate a new alignment from a list of mat files
      * @param dataFolderKey String
      * @param extractionFolderKey String
      * @param alignmentFolderKey String
      * @param matFiles ArrayList
      */
    def initCombine(String dataFolderKey, String extractionFolderKey, String alignmentFolderKey, ArrayList matFiles){

        def newCombineFolder = new File(combinesFolder(dataFolderKey, extractionFolderKey, alignmentFolderKey).path + '/' + new Date().format('yyyy-MM-dd_HH-mm-ss'))

        // make sure it is created
        newCombineFolder.mkdirs()

        // read combine folder
        def combineFolder = dataService.getFile(newCombineFolder)

        // create list of mat files included in this extraction folder
        def mats = ""
        matFiles.each { matFile ->
            mats += "${matFile}\n"
        }

        new File(combineFolder.path + '/mat.txt') << mats

        // return combineFolder key
        return combineFolder.key
    }

}
