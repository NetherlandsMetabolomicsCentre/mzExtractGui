package nl.nmc.mzextract

class Option {

    String label
    String value

    static belongsTo = [setting: Setting]

    static constraints = {
    }
}
