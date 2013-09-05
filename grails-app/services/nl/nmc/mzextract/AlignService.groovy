package nl.nmc.mzextract

import org.codehaus.groovy.grails.commons.ConfigurationHolder

class AlignService {

    def dataService
    def extractService
    def queueService
    def executionService

    // include configuration of mzExtract
    def config = ConfigurationHolder.config.mzextract

    /*
      * returns the default alignment settings
      */
    def defaultSettings() {
      return Setting.findAllByCategory('align')
    }

    /*
      * returns the alignment config file
      */
    def settingsFile(String dataFolderKey, String extractionFolderKey, String alignmentFolderKey){

        // load the alignment folder
        def alignmentFolder = alignmentFolder(dataFolderKey, extractionFolderKey, alignmentFolderKey)

        return new File(alignmentFolder.path + '/align.xml')
    }

    /*
      * run alignment on a collection of matlab files using the supplied arguments
      *
      * @param configFile String
      */
    def align(String configFile) {
        executionService.execCommand("${config.path.commandline}/${config.path.command.align}", [configFile])
    }

    /*
      * read settings from the align.xml file in the alignment folder
      * @param dataFolderKey String
      * @param extractionFolderKey String
      * @param alignmentFolderKey String
      */
    def readSettings(String dataFolderKey, String extractionFolderKey, String alignmentFolderKey){

      def defaultSettings = defaultSettings()

      // init empty settings HashMap
      def settings = [:]

      // load settings file
      def settingsFile = settingsFile(dataFolderKey, extractionFolderKey, alignmentFolderKey)

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
      * @param extractionFolderKey String
      * @param alignmentFolderKey String
      * @param parameters HashMap
      */
    def writeSettings(String dataFolderKey, String extractionFolderKey, String alignmentFolderKey, HashMap parameters){

        try {

          // load the alignment folder
          def alignmentFolder = alignmentFolder(dataFolderKey, extractionFolderKey, alignmentFolderKey)

          // load existing settings
          def existingSettings = readSettings(dataFolderKey, extractionFolderKey, alignmentFolderKey)

          //prepare the xml
          def configXML = ""
          configXML += "<config>"
          configXML += "\n\t<created>" + new Date().time + "</created>"
          configXML += "\n\t<outputpath>" + alignmentFolder.path + "</outputpath>"

          // add settings from parameters or use the default value
          existingSettings.each { label, value ->
            configXML += "\n\t<" + label + ">" + (parameters[label] ?: value) + "</" + label + ">"
          }

          // close the xml
          configXML += "\n</config>"

          // write the file
          def settingsFile = settingsFile(dataFolderKey, extractionFolderKey, alignmentFolderKey)
          settingsFile.delete()
          settingsFile << configXML


        } catch (e) {
          log.error(e.message)
        }

        return readSettings(dataFolderKey, extractionFolderKey, alignmentFolderKey)

    }

    /*
      * add the alignment to the queue for execution
      * @param dataFolderKey String
      * @param extractionFolderKey String
      * @param alignmentFolderKey String
      */
    def queue(String dataFolderKey, String extractionFolderKey, String alignmentFolderKey){
        queueService.queueAlignment(dataFolderKey, extractionFolderKey, alignmentFolderKey)
    }


    /*
      * retrieve alignments root folder
      * @param dataFolderKey String
      * @param extractionFolderKey String
      */
    def alignmentsFolder(String dataFolderKey, String extractionFolderKey){
        def extractionFolder = extractService.extractionFolder(dataFolderKey, extractionFolderKey)
        return dataService.getFolder(new File(extractionFolder.path + '/.alignments/'))
    }

    /*
      * retrieve alignment folders
      * @param dataFolderKey String
      */
    def alignmentFolders(String dataFolderKey, String extractionFolderKey){
        return alignmentsFolder(dataFolderKey, extractionFolderKey)?.folders ?: []
    }

    /*
      * retrieve an alignment folder
      * @param dataFolderKey String
      * @param extractionFolderKey String
      * @param alignmentFolderKey String
      */
    def alignmentFolder(String dataFolderKey, String extractionFolderKey, String alignmentFolderKey){
        return dataService.getFolder(new File(alignmentFolders(dataFolderKey, extractionFolderKey).find { it.key == alignmentFolderKey }?.path))
    }

    /*
      * initiate a new alignment from a list of mat files
      * @param dataFolderKey String
      * @param extractionFolderKey String
      * @param matFiles ArrayList
      */
    def initAlignment(String dataFolderKey, String extractionFolderKey, ArrayList matFiles){

        def newAlignmentFolder = new File(alignmentsFolder(dataFolderKey, extractionFolderKey).path + '/' + new Date().format('yyyy-MM-dd_HH-mm-ss'))

        // make sure it is created
        newAlignmentFolder.mkdirs()

        // read alignment folder
        def alignmentFolder = dataService.getFile(newAlignmentFolder)

        // create list of mat files included in this extraction folder
        def mats = ""
        matFiles.each { matFile ->
            mats += "${matFile}\n"
        }

        new File(alignmentFolder.path + '/mat.txt') << mats

        // return alignmentFolder key
        return alignmentFolder.key
    }

}
