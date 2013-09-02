package nl.nmc.mzextract

class AlignController {

    def alignService

    def index() { }


    def test(){

        def testConfig = '/Users/miv/Desktop/temp/mzextract/data/project/small/align.xml'

        alignService.align(testConfig)

        render ("done")
    }
}
