package nl.nmc.mzextract

class MzxmlService {

    boolean transactional = false

    def parseHeader(File mzxml) {

        def meta = [:]

        def header = ""

        try {
            try {
                def isHeader = true
                mzxml.eachLine { line, idx ->

                    // kill the loop after the first 50 lines
                    if (idx >= 50){
                        throw new ArrayIndexOutOfBoundsException()
                    }

                    // header ends a first scan, stop writing to the header
                    if (line.indexOf('<scan') >= 1){
                        isHeader = false
                    }

                    // only concat lines that are part of the header
                    if (isHeader){
                        header += line
                    }
                }
            } catch(e) {
                //expected error
            }

            //make it valid XML again.
            header += "</msRun></mzXML>"

            // parse the XML and retrieve some basic metadata
            def xml = new XmlSlurper().parseText(header)

            meta['parentFile']          = xml?.msRun?.parentFile?.@fileName?.text() ?: ''
            meta['msManufacturer']      = xml?.msRun?.msInstrument?.msManufacturer?.@value?.text() ?: ''
            meta['msModel']             = xml?.msRun?.msInstrument?.msModel?.@value?.text() ?: ''
            meta['msIonisation']        = xml?.msRun?.msInstrument?.msIonisation?.@value?.text() ?: ''
            meta['msMassAnalyzer']      = xml?.msRun?.msInstrument?.msMassAnalyzer?.@value?.text() ?: ''
            meta['msDetector']          = xml?.msRun?.msInstrument?.msDetector?.@value?.text() ?: ''
            meta['msSoftware']          = (xml?.msRun?.msInstrument?.software?.@name?.text() ?: '') + ' ' + (xml?.msRun?.msInstrument?.software?.@version?.text() ?: '')
            meta['processingSoftware']  = (xml?.msRun?.dataProcessing?.software?.@name?.text() ?: '') + ' ' + (xml?.msRun?.dataProcessing?.software?.@version?.text() ?: '')
            meta['processingOperation'] = xml?.msRun?.dataProcessing?.processingOperation?.@name?.text() ?: ''

        } catch (e) {
            /**
              * this parsing is build with the current setup/format of the mzXML files available.
              * this may fail due to file size or wrong format. Then this should be ignored as it is nice to have info.
              **/
        }

        return meta
    }
}
