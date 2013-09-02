package nl.nmc.mzextract

class Queue {

        String project
        String run
        Integer extract
        Integer align
        Integer combine
        Integer status
        Date dateCreated
        Date lastUpdated

        static mapping = {
            autoTimestamp true
        }

        static constraints = {
            extract nullable: true, empty: true
            align nullable: true, empty: true
            combine nullable: true, empty: true
        }
}
