package nl.nmc.mzextract

class Queue {

    	String project
	String run
        Integer status
	Date dateCreated
	Date lastUpdated
	
	static mapping = {
            autoTimestamp true
	}

    static constraints = {
    }
}
