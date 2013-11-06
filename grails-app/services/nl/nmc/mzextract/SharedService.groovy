package nl.nmc.mzextract

class SharedService {

    def dateFolderName() {
        return new Date().format('yyyy-MM-dd_HH-mm-ss')
    }
}
