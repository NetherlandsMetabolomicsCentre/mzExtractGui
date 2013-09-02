import nl.nmc.mzextract.Setting
import nl.nmc.mzextract.Option

import org.codehaus.groovy.grails.commons.ConfigurationHolder

class BootStrap {


    // include configuration of mzExtract
    def config = ConfigurationHolder.config.mzextract

    def init = { servletContext ->

        // Inject settings for extraxt, align and combine
        // MS TYPE
        def mstype = new Setting()
        mstype.category = 'extract'
        mstype.name = 'mstype'
        mstype.label = 'MS Type'
        mstype.type = 'select'
        mstype.value = '1'
        mstype.help = 'MS type'

        mstype.addToOptions(new Option(label:"FT", value:"1"))
        mstype.addToOptions(new Option(label:"ORBITRAP", value:"2"))
        mstype.addToOptions(new Option(label:"TOF", value:"3"))
        mstype.addToOptions(new Option(label:"GC/UNIT (MASS)", value:"4"))
        mstype.addToOptions(new Option(label:"QUADRUPOLE", value:"5"))

        mstype.save()

        // CALIBRATION MASS
        def calibrationmass = new Setting()
        calibrationmass.category = 'extract'
        calibrationmass.name = 'calibrationmass'
        calibrationmass.label = 'Calibration mass'
        calibrationmass.type = 'number'
        calibrationmass.value = '1000'
        calibrationmass.help = 'Calibration mass'
        calibrationmass.save()

        // NOISE THRESHOLD FACTOR
        def noisethresholdfactor = new Setting()
        noisethresholdfactor.category = 'extract'
        noisethresholdfactor.name = 'noisethresholdfactor'
        noisethresholdfactor.label = 'Noise threshold factor'
        noisethresholdfactor.type = 'number'
        noisethresholdfactor.value = '10'
        noisethresholdfactor.help = 'Noise threshold factor'
        noisethresholdfactor.save()

        // PPM RESOLUTION
        def ppmresolution = new Setting()
        ppmresolution.category = 'extract'
        ppmresolution.name = 'ppmresolution'
        ppmresolution.label = 'PPM resolution'
        ppmresolution.type = 'number'
        ppmresolution.value = '4000'
        ppmresolution.help = 'PPM resolution'
        ppmresolution.save()

        // CENTROID THRESHOLD
        def centroidthreshold = new Setting()
        centroidthreshold.category = 'extract'
        centroidthreshold.name = 'centroidthreshold'
        centroidthreshold.label = 'Centroid threshold'
        centroidthreshold.type = 'number'
        centroidthreshold.value = '1000'
        centroidthreshold.help = 'Centroid threshold'
        centroidthreshold.save()

        // SPLIT RATION
        def splitratio = new Setting()
        splitratio.category = 'extract'
        splitratio.name = 'splitratio'
        splitratio.label = 'Spilt ratio'
        splitratio.type = 'number'
        splitratio.value = '0.001'
        splitratio.help = 'Spilt ratio'
        splitratio.save()

        // MODE
        def mode = new Setting()
        mode.category = 'extract'
        mode.name = 'mode'
        mode.label = 'Mode'
        mode.type = 'select'
        mode.value = 'positive'
        mode.help = 'Mode'

        mode.addToOptions(new Option(label:"positive", value:"positive"))
        mode.addToOptions(new Option(label:"negative", value:"negative"))

        mode.save()

        // SGFILT
        def sgfilt = new Setting()
        sgfilt.category = 'extract'
        sgfilt.name = 'sgfilt'
        sgfilt.label = 'SG Filter'
        sgfilt.type = 'select'
        sgfilt.value = '1'
        sgfilt.help = 'SG Filter'

        sgfilt.addToOptions(new Option(label:"no", value:"0"))
        sgfilt.addToOptions(new Option(label:"yes", value:"1"))

        sgfilt.save()

        // USE MZFILE
        def usemz = new Setting()
        usemz.category = 'combine'
        usemz.name = 'usemz'
        usemz.label = 'Use the mz file'
        usemz.type = 'select'
        usemz.value = '0'
        usemz.help = 'Use the mz file to ...'

        usemz.addToOptions(new Option(label:"no, ignore the mzfile", value:"0"))
        usemz.addToOptions(new Option(label:"yes, use it when available", value:"1"))

        usemz.save()

        // RT WINDOW
        def rtwindow = new Setting()
        rtwindow.category = 'combine'
        rtwindow.name = 'rtwindow'
        rtwindow.label = 'Retention time window'
        rtwindow.type = 'number'
        rtwindow.value = '10'
        rtwindow.help = 'Retention time window (in seconds)'
        rtwindow.save()

        // REVERT SEARCH TO SINGLE FEATURES
        def rs2sf = new Setting()
        rs2sf.category = 'combine'
        rs2sf.name = 'rs2sf'
        rs2sf.label = 'Revert search to single features'
        rs2sf.type = 'select'
        rs2sf.value = '1'
        rs2sf.help = 'Revert search to single features'

        rs2sf.addToOptions(new Option(label:"no", value:"0"))
        rs2sf.addToOptions(new Option(label:"yes", value:"1"))

        rs2sf.save()

        // MATCH SINGLE FEATURES ONLY
        def msfonly = new Setting()
        msfonly.category = 'combine'
        msfonly.name = 'msfonly'
        msfonly.label = 'Match single features only'
        msfonly.type = 'select'
        msfonly.value = '1'
        msfonly.help = 'Match single features only'

        msfonly.addToOptions(new Option(label:"no", value:"0"))
        msfonly.addToOptions(new Option(label:"yes", value:"1"))

        msfonly.save()

    }
    def destroy = {
    }
}
