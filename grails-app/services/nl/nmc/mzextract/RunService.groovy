package nl.nmc.mzextract

class RunService {
    
    boolean transactional = false    
    
    def grailsApplication 
    def projectService
    
    // init a new run
    def initRun(File project){

        //prepare a run directory
        def run = new File(project.canonicalPath + '/runs/' + new Date().format('yyyy-MM-dd_hh-mm-ss'))
        run.mkdirs()

        def runSha1 = run.name.encodeAsSHA1()
        def projectSha1 = project.name.encodeAsSHA1()                

        //queue run
        new Queue(project: projectSha1, run: runSha1, status:1 as int).save()
        
        return run
    }
    
    def writeSettings(File project, File run, parameters){
        
        // all expected settings sorted
    	def settings = settings()
        
        // read existing config from file
        def xmlSettings = null
        def configFile = configFileFromRunFolder(run)
        if (configFile.exists()){
            xmlSettings = new XmlSlurper().parseText(configFile.text)
        }            
            
        // add all config settings from file which are not in the parameters, or use the default
        settings.each { name, setting ->
            settings[name]['value'] = parameters[name] ?: (xmlSettings?."${name}"?.text() ?: (setting['default'] ?: ''))
        }
                
        //prepare the settings file
        def configXML = ""
        configXML += "<config>\n"
        configXML += "\t<created>" + new Date().time + "</created>\n"
        configXML += "\t<outputpath>" + run.canonicalPath + "</outputpath>\n"
        configXML += settings.findAll{ name, setting -> setting.value != '' }.collect { name, setting -> "\t<" + name + ">" + setting.value + "</" + name + '>'}.join("\n")
        
        if (!parameters['usemz'] || parameters['usemz'] != '0'){
            def mzFile = projectService.mzFileFromProjectFolder(project)
            if (mzFile.exists()){
                configXML += "\n\t<mzfile>" + mzFile.canonicalPath + "</mzfile>"
            }
        }
        configXML += "\n</config>"

        // save XML to config file
        if (configFile.exists()){ configFile.delete() }                
        configFileFromRunFolder(run) << configXML
        
        return settings
        
    }

    // returns a List of run setting properties
    def settings() {

        def settings = [:]
            settings['mstype'] = ['label':'MS Type', 'type':'number', 'default':'5', 'help':'MS type']
            settings['calibrationmass'] = ['label':'Calibration mass', 'type':'number', 'default':'1000', 'help':'Calibration mass']
            settings['noisethresholdfactor'] = ['label':'Noise threshold factor', 'type':'number', 'default':'10', 'help':'Noise threshold factor']
            settings['ppmresolution'] = ['label':'PPM resolution', 'type':'number', 'default':'4000', 'help':'PPM resolution']
            settings['centroidthreshold'] = ['label':'Centroid threshold', 'type':'number', 'default':'1000', 'help':'Centroid threshold']
            settings['splitratio'] = ['label':'Split ratio', 'type':'number', 'default':'0.001', 'help':'Split ratio']
            settings['mode'] = ['label':'Mode', 'type':'select', 'default':'positive', 'options': [['value':'positive', 'label':'positive'],['value':'negative', 'label':'negative']], 'help':'Mode (positive/negative)']
            settings['sgfilt'] = ['label':'SG filter', 'type':'number', 'default':'1', 'help':'SG filter']
            settings['usemz'] = ['label':'Use the mz file', 'type':'select', 'options':[['value':0, 'label':'no, ignore the mzfile'],['value':1, 'label':'yes, use it when available']], 'help':'An mzFile can be added to the project to ... when...']
            
        return settings
    }
    
    def status(String projectSha1, String runSha1){
        return Queue.findByProjectAndRun(projectSha1, runSha1)?.status ?: 0
    } 
    
    // returns a File object of the config file of the run
    def configFileFromRunFolder(File runFolder){
        return new File(runFolder.canonicalPath + '/config.xml')
    }    
}
