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
    def settingsFile(String dataFolderKey, String extractFolderKey, String alignFolderKey, String combineFolderKey){

        // load the combine folder
        def combineFolder = combineFolder(dataFolderKey, extractFolderKey, alignFolderKey, combineFolderKey)

        return new File(combineFolder.path + '/combine.xml')
    }

    /*
      * returns the combine status file
      */
    def statusFile(String dataFolderKey, String extractFolderKey, String alignFolderKey, String combineFolderKey){

        // load the combine folder
        def combineFolder = combineFolder(dataFolderKey, extractFolderKey, alignFolderKey, combineFolderKey)

        return new File(combineFolder.path + '/status.xml')
    } 
    
    /*
      * read status from the status.xml file in the combine folder
      * @param dataFolderKey String
      * @param extractFolderKey String
      * @param alignFolderKey String
      * @param combineFolderKey String
      */
    def readStatus(String dataFolderKey, String extractFolderKey, String alignFolderKey, String combineFolderKey){

      // init empty status HashMap
      def status = [:]

      // load settings file
      def statusFile = statusFile(dataFolderKey, extractFolderKey, alignFolderKey, combineFolderKey)

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
      * @param alignFolderKey String
      * @param combineFolderKey String
      * @param parameters HashMap
      */
    def writeStatus(String dataFolderKey, String extractFolderKey, String alignFolderKey, String combineFolderKey, HashMap parameters){

        def status = [:]
       
        // load existing status
        def existingStatus = readStatus(dataFolderKey, extractFolderKey, alignFolderKey, combineFolderKey)

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
        def settingsFile = settingsFile(dataFolderKey, extractFolderKey, alignFolderKey, combineFolderKey)
        if (settingsFile.exists()){
          statusXML += "\n\t" + settingsFile.text ?: ""
        }

        // close the xml
        statusXML += "\n</status>"

        // write the file
        def statusFile = statusFile(dataFolderKey, extractFolderKey, alignFolderKey, combineFolderKey)
        statusFile.delete()
        statusFile << statusXML

        return readStatus(dataFolderKey, extractFolderKey, alignFolderKey, combineFolderKey)
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
      * @param extractFolderKey String
      * @param alignFolderKey String
      * @param combineFolderKey String
      */
    def readSettings(String dataFolderKey, String extractFolderKey, String alignFolderKey = null, String combineFolderKey){

      def defaultSettings = defaultSettings()

      // init empty settings HashMap
      def settings = [:]

      // load settings file
      def settingsFile = settingsFile(dataFolderKey, extractFolderKey, alignFolderKey, combineFolderKey)

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
      * @param extractFolderKey String
      * @param alignFolderKey String
      * @param combineFolderKey String
      * @param parameters HashMap
      */
    def writeSettings(String dataFolderKey, String extractFolderKey, String alignFolderKey = null, String combineFolderKey, HashMap parameters){

        //try {

          // load the extraction, alignment and combine folder
          def extractFolder = extractService.extractFolder(dataFolderKey, extractFolderKey)
          def alignFolder = alignFolderKey ? alignService.alignFolder(dataFolderKey, extractFolderKey, alignFolderKey) : null
          def combineFolder = combineFolder(dataFolderKey, extractFolderKey, alignFolderKey, combineFolderKey)

          // load existing settings
          def existingSettings = readSettings(dataFolderKey, extractFolderKey, alignFolderKey, combineFolderKey)

          //prepare the xml
          def configXML = ""
          configXML += "<config>"
          configXML += "\n\t<created>" + new Date().time + "</created>"
          configXML += "\n\t<exportfile>${combineFolder.path}/${config.combine.outputfile}</exportfile>"
        
          // add settings from parameters or use the default value
          existingSettings.each { label, value ->
            configXML += "\n\t<" + label + ">" + (parameters[label] ?: value) + "</" + label + ">"
          }

          // add the files to combine
          new File(combineFolder.path + '/mat.txt').eachLine { matFileKey ->

            if (extractFolder?.files['mat']){
              def matlabFile = extractFolder.files['mat'].find { it.key == matFileKey }
              if (matlabFile){
                configXML += "\n\t<filename>" + matlabFile.path + "</filename>"
              }
            }

            if (alignFolder != null && alignFolder?.files['mat']){
              def matlabFile = alignFolder.files['mat'].find { it.key == matFileKey }
              if (matlabFile){
                configXML += "\n\t<filename>" + matlabFile.path + "</filename>"
              }
            }
          }

          // close the xml
          configXML += "\n</config>"

          // write the file
          def settingsFile = settingsFile(dataFolderKey, extractFolderKey, alignFolderKey, combineFolderKey)
          settingsFile.delete()
          settingsFile << configXML


        //} catch (e) {
        //  log.error(e.message)
        //}

        return readSettings(dataFolderKey, extractFolderKey, alignFolderKey, combineFolderKey)

    }

    /*
      * add the combine to the queue for execution
      * @param dataFolderKey String
      * @param extractFolderKey String
      * @param alignFolderKey String
      * @param combineFolderKey String
      */
    def queue(String dataFolderKey, String extractFolderKey, String alignFolderKey, String combineFolderKey){
        
        // reset any previous status info
        statusFile(dataFolderKey, extractFolderKey, alignFolderKey, combineFolderKey)?.delete()        
        
        queueService.queueCombine(dataFolderKey, extractFolderKey, alignFolderKey, combineFolderKey)
    }


    /*
      * retrieve combines root folder
      * @param dataFolderKey String
      * @param extractFolderKey String
      * @param alignFolderKey String
      */
    def combinesFolder(String dataFolderKey, String extractFolderKey, String alignFolderKey = null){

        def combinesFolder

        def extractFolder = extractService.extractFolder(dataFolderKey, extractFolderKey)        
        
        if (alignFolderKey){
          combinesFolder = dataService.getFolder(new File(alignService.alignFolder(dataFolderKey, extractFolderKey, alignFolderKey).path + '/.combines/'))
        } else {
          combinesFolder = dataService.getFolder(new File(extractFolder.path + '/.combines/'))
        }

        return combinesFolder
    }

    /*
      * retrieve combine folders
      * @param dataFolderKey String
      */
    def combineFolders(String dataFolderKey, String extractFolderKey, String alignFolderKey = null){
        return combinesFolder(dataFolderKey, extractFolderKey, alignFolderKey)?.folders ?: []
    }

    /*
      * retrieve an combine folder
      * @param dataFolderKey String
      * @param extractFolderKey String
      * @param alignFolderKey String
      * @param combineFolderKey String
      */
    def combineFolder(String dataFolderKey, String extractFolderKey, String alignFolderKey = null, String combineFolderKey){
        //println alignFolderKey
        return dataService.getFolder(new File(combineFolders(dataFolderKey, extractFolderKey, alignFolderKey).find { it.key == combineFolderKey }.path))
    }

    /*
      * initiate a new alignment from a list of mat files
      * @param dataFolderKey String
      * @param extractFolderKey String
      * @param alignFolderKey String
      * @param matFiles ArrayList
      */
    def initCombine(String dataFolderKey, String extractFolderKey, String alignFolderKey, ArrayList matFiles){

        def newCombineFolder = new File(combinesFolder(dataFolderKey, extractFolderKey, alignFolderKey).path + '/' + new Date().format('yyyy-MM-dd_HH-mm-ss'))

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

        // set status to new
        writeStatus(dataFolderKey, extractFolderKey, alignFolderKey, combineFolder.key, ['status':'new', 'created': new Date()])        
        
        // return combineFolder key
        return combineFolder.key
    }

}
