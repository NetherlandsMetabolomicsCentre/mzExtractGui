package nl.nmc.mzextract

class CombineController {

    def combineService

    def index() { }


    def test(){

        def testConfig = '/Users/miv/Desktop/temp/mzextract/data/project/small/combine.xml'

        combineService.combine(testConfig)

        render ("done")
    }
}
