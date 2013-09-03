package nl.nmc.mzextract

import org.codehaus.groovy.grails.commons.ConfigurationHolder

class ExtractService {

    def dataService
    def queueService
    def executionService

    // include configuration of mzExtract
    def config = ConfigurationHolder.config.mzextract

    /*
      * returns the default extraction settings
      */
    def defaultSettings() {
      return Setting.findAllByCategory('extract')
    }

    /*
      * returns the extraction config file
      */
    def settingsFile(String dataFolderKey, String extractionFolderKey){

        // load the extraction folder
        def extractionFolder = extractionFolder(dataFolderKey, extractionFolderKey)

        return new File(extractionFolder.path + '/extract.xml')
    }

    /*
      * run extraction on a (mzXML) file using the supplied arguments
      *
      * @param mzXMLFile String with full path to file to extract
      * @param configFile String
      */
    def extract(String mzXMLFile, String configFile) {
        executionService.execCommand("${config.path.commandline}/${config.path.command.extract}", [mzXMLFile, configFile])

        /***** for testing purposes!!! ******/
        def xmlConfig = new XmlSlurper().parseText(new File(configFile).text)

        def matFile = new File("${xmlConfig.outputpath}/${mzXMLFile.tokenize('/')[-1]}.mat")
        matFile.delete()
        matFile << "Dummy matlab file for testing"

    }

    /*
      * read settings from the extraction.xml file in the extraction folder
      * @param dataFolderKey String
      * @param extractionFolderKey String
      */
    def readSettings(String dataFolderKey, String extractionFolderKey){

      def defaultSettings = defaultSettings()

      // load the extraction folder
      def extractionFolder = extractionFolder(dataFolderKey, extractionFolderKey)

      // init empty settings HashMap
      def settings = [:]

      // load settings file
      def settingsFile = settingsFile(dataFolderKey, extractionFolder.key)

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
      * (over)write settings to the extraction.xml file in the extraction folder
      * @param dataFolderKey String
      * @param extractionFolderKey String
      * @param parameters HashMap
      */
    def writeSettings(String dataFolderKey, String extractionFolderKey, HashMap parameters){

        try {

          // load the extraction folder
          def extractionFolder = extractionFolder(dataFolderKey, extractionFolderKey)

          // load existing settings
          def existingSettings = readSettings(dataFolderKey, extractionFolderKey)

          //prepare the xml
          def configXML = ""
          configXML += "<config>"
          configXML += "\n\t<created>" + new Date().time + "</created>"
          configXML += "\n\t<outputpath>" + extractionFolder.path + "</outputpath>"

          // add settings from parameters or use the default value
          existingSettings.each { label, value ->
            configXML += "\n\t<" + label + ">" + (parameters[label] ?: value) + "</" + label + ">"
          }

          // close the xml
          configXML += "\n</config>"

          // write the file
          def settingsFile = settingsFile(dataFolderKey, extractionFolderKey)
          settingsFile.delete()
          settingsFile << configXML


        } catch (e) {
          log.error(e.message)
        }

        return readSettings(dataFolderKey, extractionFolderKey)

    }

    /*
      * add the extraction to the queue for execution
      * @param dataFolderKey String
      * @param extractionFolderKey String
      */
    def queue(String dataFolderKey, String extractionFolderKey){
        queueService.queueExtraction(dataFolderKey, extractionFolderKey)
    }


    /*
      * retrieve extractions root folder from data folder key
      * @param dataFolderKey String
      */
    def extractionsFolder(String dataFolderKey){
        def dataFolder = dataService.dataFolder(dataFolderKey)
        return dataService.getFolder(new File(dataFolder.path + '/extractions/'))
    }

    /*
      * retrieve extraction folders from data folder key
      * @param dataFolderKey String
      */
    def extractionFolders(String dataFolderKey){
        return extractionsFolder(dataFolderKey)?.folders ?: []
    }

    /*
      * retrieve an extraction folder from a data folder key and extraction folder key
      * @param dataFolderKey String
      * @param extractionFolderKey String
      */
    def extractionFolder(String dataFolderKey, String extractionFolderKey){
        return dataService.getFolder(new File(extractionFolders(dataFolderKey).find { it.key == extractionFolderKey }.path))
    }

    /*
      * initiate a new extraction from a list of mzxml fils for a data folder
      * @param dataFolderKey String
      * @param mzxmlFiles ArrayList
      */
    def initExtraction(String dataFolderKey, ArrayList mzxmlFiles){

        def newExtractionFolder = new File(extractionsFolder(dataFolderKey).path + '/' + new Date().format('yyyy-MM-dd_HH-mm-ss'))

        // make sure it is created
        newExtractionFolder.mkdirs()

        // read extraction folder
        def extractionFolder = dataService.getFile(newExtractionFolder)

        // create list of mzxml files included in this extraction folder
        def mzxmls = ""
        mzxmlFiles.each { mzxmlFile ->
            mzxmls += "${mzxmlFile}\n"
        }

        new File(extractionFolder.path + '/mzxml.txt') << mzxmls

        // return extractionFolder key
        return extractionFolder.key
    }
}
