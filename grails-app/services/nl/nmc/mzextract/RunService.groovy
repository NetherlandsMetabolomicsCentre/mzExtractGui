package nl.nmc.mzextract

import groovy.json.*
import grails.converters.*

class RunService {

    boolean transactional = false

    def grailsApplication
    def projectService
    def sharedService    

    // init a new run
    def initRun(File project){

        //prepare a run directory
        def run = new File(project.canonicalPath + '/runs/' + sharedService.dateFolderName())
        run.mkdirs()

        def runSha1 = run.name.encodeAsSHA1()
        def projectSha1 = project.name.encodeAsSHA1()

        // write settings
        writeSettings(project, run, [:])

        //queue run
        def queue = new Queue(project: projectSha1, run: runSha1, status:10 as int)
        queue.save()

        return run
    }

    def writeSettings(File project, File run, parameters){

        // get Sha1 hashes
        def projectSha1 = project.name.encodeAsSHA1()
        def runSha1 = run.name.encodeAsSHA1()

        // all expected settings sorted
    	def settings = readSettings(project, run)

        //prepare the settings file
        def configXML = ""
        configXML += "<config>\n"
        configXML += "\t<created>" + new Date().time + "</created>\n"
        configXML += "\t<outputpath>" + run.canonicalPath + "</outputpath>\n"
        configXML += settings.findAll{ name, setting -> setting.value != '' }.collect { name, setting -> "\t<" + name + ">" + (parameters[name] ?: setting.value) + "</" + name + '>'}.join("\n")

        if (!parameters['usemz'] || parameters['usemz'] != '0'){
            def mzFile = projectService.mzFileFromProjectFolder(project)
            if (mzFile.exists()){
                configXML += "\n\t<mzfile>" + mzFile.canonicalPath + "</mzfile>"
            }
        }
        configXML += "\n</config>"

        // new settings means deleting all the previous output
        run.eachFile { it.delete() }

        // set status to new again
        def queue = Queue.findByProjectAndRun(projectSha1, runSha1)
        if (queue){
            queue.status = 10 as int
            queue.save()
        } else {
            new Queue(project: projectSha1, run: runSha1, status:10 as int).save()
        }

        // save new XML to config file
        configFileFromRunFolder(run, 'extract') << configXML

        // read new settings
        settings = readSettings(project, run)

        return settings

    }

    // returns a List of extract settings
    def readSettings(File project, File run) {

        // init return hashmap
        def settings = [:]

        // read in default settings
        Setting.list()?.each { defaultSetting ->

            def setting = [:]

            defaultSetting.properties.each { propertyKey, propertyValue ->
                if (propertyKey == 'options' && propertyValue) { // settings are stored in JSON, have to convert it to an ArrayList first
                    setting[propertyKey] =  new JsonSlurper().parseText(propertyValue)
                } else {
                    setting[propertyKey] = propertyValue
                }
            }

            settings[defaultSetting.name] = setting
        }

        // read existing config files
        def xmlSettings = null
        ['extract', 'align', 'combine'].each { category ->
            def configFile = configFileFromRunFolder(run, category)
            if (configFile.exists()){
                // read old settings
                xmlSettings = new XmlSlurper().parseText(configFile.text)
            }

            // add all config settings from file which are not in the parameters, or use the value
            settings.each { name, setting ->
                settings[name]['value'] = xmlSettings?."${name}"?.text() ?: (setting['value'] ?: '')
            }
        }

        return settings
    }

    def status(String projectSha1, String runSha1){

        def status

        def queue = Queue.findByProjectAndRun(projectSha1, runSha1)
        if (queue?.status){
            status = queue?.status
        } else {
            // no entry!
            if (hasData(projectSha1, runSha1)){
                // when it has data set it to finished
                status = 40
            } else {
                // no data we assume it is new
                status = 0
            }
            new Queue(project: projectSha1, run: runSha1, status:status as int).save()
        }

        return status
    }

    def hasData(String projectSha1, String runSha1){

        def run = projectService.runFolderFromSHA1EncodedProjectNameAndRunName(projectSha1, runSha1)

        try {
            if (new FileNameFinder().getFileNames(run.canonicalPath, '**/*.mat **/*.txt')){
                return true
            }
        } catch (e) {
            // could not determine if the run has data
        }

        return false
    }

    // returns a File object of the config file of the run
    def configFileFromRunFolder(File runFolder, String category){
        return new File(runFolder.canonicalPath + '/' + category + '.xml')
    }
}
