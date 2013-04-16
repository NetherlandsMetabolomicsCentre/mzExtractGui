package nl.nmc.mzextract

class RunService {

    // returns a List of run setting properties
    def settings() {

        def settings = []
            settings << ['label':'MS yype', 'name':'mstype', 'type':'number', 'default':'5', 'help':'MS type']
            settings << ['label':'Calibration mass', 'name':'calibrationmass', 'type':'number', 'default':'1000', 'help':'Calibration mass']
            settings << ['label':'Noise threshold factor', 'name':'noisethresholdfactor', 'type':'number', 'default':'10', 'help':'Noise threshold factor']
            settings << ['label':'PPM resolution', 'name':'ppmresolution', 'type':'number', 'default':'4000', 'help':'PPM resolution']
            settings << ['label':'Centroid threshold', 'name':'centroidthreshold', 'type':'number', 'default':'1000', 'help':'Centroid threshold']
            settings << ['label':'Split ratio', 'name':'splitratio', 'type':'number', 'default':'0.001', 'help':'Split ratio']
            settings << ['label':'Mode', 'name':'mode', 'type':'select', 'default':'positive', 'options': ['positive', 'negative'], 'help':'Mode (positive/negative)']
            settings << ['label':'SG filter', 'name':'sgfilt', 'type':'number', 'default':'1', 'help':'SG filter']
            settings << ['label':'Use the mz file', 'name':'usemz', 'type':'select', 'options':['yes, use it when available','no, ignore the mzfile'], 'help':'An mzFile can be added to the project to ... when...']
            
        return settings
    }
}
