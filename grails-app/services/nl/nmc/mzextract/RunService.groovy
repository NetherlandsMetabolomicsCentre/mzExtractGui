package nl.nmc.mzextract

class RunService {
    
    boolean transactional = false    
    
    def grailsApplication 
    def projectService
    
    // init a new run
    def initRun(File project){

        //prepare a run directory
        def run = new File(project.canonicalPath + '/runs/' + new Date().format('yyyy-MM-dd_HH-mm-ss'))
        run.mkdirs()

        def runSha1 = run.name.encodeAsSHA1()
        def projectSha1 = project.name.encodeAsSHA1()   
        
        // write settings
        writeSettings(project, run, [:])

        //queue run
        new Queue(project: projectSha1, run: runSha1, status:10 as int).save()
        
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
        configFileFromRunFolder(run) << configXML
        
        // read new settings
        settings = readSettings(project, run)
        
        return settings
        
    }

    // returns a List of run setting properties
    def readSettings(File project, File run) {
        
        def settings = [:]
            settings['mstype'] = ['label':'MS Type', 'type':'select', 'value':'5', 'options':[['value':1, 'label':'FT'],['value':2, 'label':'ORBITRAP'],['value':3, 'label':'TOF'],['value':4, 'label':'GC/UNIT (MASS)'],['value':5, 'label':'QUADRUPOLE']], 'help':'MS type']
            settings['calibrationmass'] = ['label':'Calibration mass', 'type':'number', 'value':'1000', 'help':'Calibration mass']
            settings['noisethresholdfactor'] = ['label':'Noise threshold factor', 'type':'number', 'value':'10', 'help':'Noise threshold factor']
            settings['ppmresolution'] = ['label':'PPM resolution', 'type':'number', 'value':'4000', 'help':'PPM resolution']
            settings['centroidthreshold'] = ['label':'Centroid threshold', 'type':'number', 'value':'1000', 'help':'Centroid threshold']
            settings['splitratio'] = ['label':'Split ratio', 'type':'number', 'value':'0.001', 'help':'Split ratio']
            settings['mode'] = ['label':'Mode', 'type':'select', 'value':'positive', 'options': [['value':'positive', 'label':'positive'],['value':'negative', 'label':'negative']], 'help':'Mode (positive/negative)']
            settings['sgfilt'] = ['label':'SG filter', 'type':'select', 'value':'1', 'options':[['value':0, 'label':'no'],['value':1, 'label':'yes']], 'help':'SG filter']
            settings['usemz'] = ['label':'Use the mz file', 'value':0, 'type':'select', 'options':[['value':0, 'label':'no, ignore the mzfile'],['value':1, 'label':'yes, use it when available']], 'help':'An mzFile can be added to the project to ... when...']        
            settings['rtwindow'] = ['label':'Retention time window', 'type':'number', 'value':'10', 'help':'Retention time window (in seconds)']
            settings['rs2sf'] = ['label':'Revert search to single features', 'type':'select', 'value':'1', 'options':[['value':0, 'label':'no'],['value':1, 'label':'yes']], 'help':'Revert search to single features']
            settings['msfonly'] = ['label':'Match single features only', 'type':'select', 'value':'1', 'options':[['value':0, 'label':'no'],['value':1, 'label':'yes']], 'help':'Match single features only']
        
        // read existing config from file
        def xmlSettings = null
        def configFile = configFileFromRunFolder(run)
        if (configFile.exists()){
            // read old settings
            xmlSettings = new XmlSlurper().parseText(configFile.text)            
        }            
            
        // add all config settings from file which are not in the parameters, or use the value
        settings.each { name, setting ->
            settings[name]['value'] = xmlSettings?."${name}"?.text() ?: (setting['value'] ?: '')
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
    def configFileFromRunFolder(File runFolder){
        return new File(runFolder.canonicalPath + '/config.xml')
    }    
}
