package nl.nmc.mzextract

import org.codehaus.groovy.grails.commons.ConfigurationHolder

class ExtractService {

    def dataService
    def queueService
    def executionService
    def sharedService

    // include configuration of mzExtract
    def config = ConfigurationHolder.config.mzextract

    /*
      * returns the default extraction settings
      */
    def defaultSettings() {
      return Setting.findAllByCategory('extract')
    }

    /*
      * returns the extraction status file
      */
    def statusFile(String dataFolderKey, String extractFolderKey){

        // load the extraction folder
        def extractFolder = extractFolder(dataFolderKey, extractFolderKey)

        return new File(extractFolder.path, 'status.xml')
    }

    /*
      * read status from the status.xml file in the extraction folder
      * @param dataFolderKey String
      * @param extractFolderKey String
      */
    def readStatus(String dataFolderKey, String extractFolderKey){

      // init empty status HashMap
      def status = [:]

      // load settings file
      def statusFile = statusFile(dataFolderKey, extractFolderKey)

      // read old status
      def xmlStatus = [:]
      if (statusFile.size() > 0){
        xmlStatus = new XmlSlurper().parseText(statusFile?.text)
        xmlStatus.children().each {
            status[it.name()] = it.text()
        }
      }

      return status
    }

    /*
      * (over)write status to the status.xml file in the extraction folder
      * @param dataFolderKey String
      * @param extractFolderKey String
      * @param parameters HashMap
      */
    def writeStatus(String dataFolderKey, String extractFolderKey, HashMap parameters){

        def status = [:]

        // load existing status
        def existingStatus = readStatus(dataFolderKey, extractFolderKey)

        // merge existing status with new status
        existingStatus.each { label, value ->
            if (label != 'config'){
                status["${label}"] = value as String
            }
        }
        parameters.each { label, value ->
          status["${label}"] = value as String
        }

        //prepare the xml
        def statusXML = ""
        statusXML += "<status>"

        // add status from
        status.each { label, value ->
          statusXML += "\n\t<" + label + ">" + value + "</" + label + ">"
        }

        // include the settings
        def settingsFile = settingsFile(dataFolderKey, extractFolderKey)
        if (settingsFile.exists()){
          statusXML += "\n\t" + settingsFile.text ?: ""
        }

        // close the xml
        statusXML += "\n</status>"

        // write the file
        def statusFile = statusFile(dataFolderKey, extractFolderKey)
        statusFile.delete()
        statusFile << statusXML

        return readStatus(dataFolderKey, extractFolderKey)

    }

    /*
      * returns the extraction config file
      */
    def settingsFile(String dataFolderKey, String extractFolderKey){

        // load the extraction folder
        def extractFolder = extractFolder(dataFolderKey, extractFolderKey)

        return new File(extractFolder.path, 'extract.xml')
    }

    /*
      * run extraction on a (mzXML) file using the supplied arguments
      *
      * @param mzXMLFile String with full path to file to extract
      * @param configFile String
      */
    def extract(String mzXMLFile, String configFile) {
        executionService.execCommand("${config.path.commandline}/${config.path.command.extract}", [mzXMLFile, configFile])
    }

    /*
      * read settings from the extraction.xml file in the extraction folder
      * @param dataFolderKey String
      * @param extractFolderKey String
      */
    def readSettings(String dataFolderKey, String extractFolderKey){

      def defaultSettings = defaultSettings()

      // load the extraction folder
      def extractFolder = extractFolder(dataFolderKey, extractFolderKey)

      // init empty settings HashMap
      def settings = [:]

      // load settings file
      def settingsFile = settingsFile(dataFolderKey, extractFolder.key)

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
      * @param extractFolderKey String
      * @param parameters HashMap
      */
    def writeSettings(String dataFolderKey, String extractFolderKey, HashMap parameters){

        try {

          // load the extraction folder
          def extractFolder = extractFolder(dataFolderKey, extractFolderKey)

          // load existing settings
          def existingSettings = readSettings(dataFolderKey, extractFolderKey)

          //prepare the xml
          def configXML = ""
          configXML += "<config>"
          configXML += "\n\t<created>" + new Date().time + "</created>"
          configXML += "\n\t<outputpath>" + extractFolder.path + "</outputpath>"

          // add settings from parameters or use the default value
          existingSettings.each { label, value ->
            configXML += "\n\t<" + label + ">" + (parameters[label] ?: value) + "</" + label + ">"
          }

          // close the xml
          configXML += "\n</config>"

          // write the file
          def settingsFile = settingsFile(dataFolderKey, extractFolderKey)
          settingsFile.delete()
          settingsFile << configXML


        } catch (e) {
          log.error(e.message)
        }

        return readSettings(dataFolderKey, extractFolderKey)

    }

    /*
      * add the extraction to the queue for execution
      * @param dataFolderKey String
      * @param extractFolderKey String
      */
    def queue(String dataFolderKey, String extractFolderKey){

        // reset any previous status info
        statusFile(dataFolderKey, extractFolderKey)?.delete()

        queueService.queueExtraction(dataFolderKey, extractFolderKey)
    }


    /*
      * retrieve extractions root folder from data folder key
      * @param dataFolderKey String
      */
    def extractionsFolder(String dataFolderKey){
        def dataFolder = dataService.dataFolder(dataFolderKey)
        return dataService.getFolder(new File(dataFolder.path, '.extractions'))
    }

    /*
      * retrieve extraction folders from data folder key
      * @param dataFolderKey String
      */
    def extractFolders(String dataFolderKey){
        return extractionsFolder(dataFolderKey)?.folders ?: []
    }

    /*
      * retrieve an extraction folder from a data folder key and extraction folder key
      * @param dataFolderKey String
      * @param extractFolderKey String
      */
    def extractFolder(String dataFolderKey, String extractFolderKey){
        return dataService.getFolder(new File(extractFolders(dataFolderKey).find { it.key == extractFolderKey }.path))
    }

    /*
      * initiate a new extraction from a list of mzxml fils for a data folder
      * @param dataFolderKey String
      * @param mzxmlFiles ArrayList
      */
    def initExtraction(String dataFolderKey, ArrayList mzxmlFiles){

        def newextractFolder = new File(extractionsFolder(dataFolderKey).path, sharedService.dateFolderName())

        // make sure it is created
        newextractFolder.mkdirs()

        // read extraction folder
        def extractFolder = dataService.getFile(newextractFolder)

        // create list of mzxml files included in this extraction folder
        def mzxmls = ""
        mzxmlFiles.each { mzxmlFile ->
            mzxmls += "${mzxmlFile}\n"
        }

        new File(extractFolder.path, 'mzxml.txt') << mzxmls

        // set status to new
        writeStatus(dataFolderKey, extractFolder.key, ['status':'new', 'created': new Date()])

        // return extractFolder key
        return extractFolder.key
    }
}
