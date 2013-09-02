package nl.nmc.mzextract

class Setting {

    String category
    String name
    String label
    String type
    String value
    String help

    static hasMany = [options: Option]

    static constraints = {
        options nullable: true, empty: true
        help nullable: true, empty: true
    }

    static mapping = {
        //options type: 'text'
        help type: 'text'
    }
}
