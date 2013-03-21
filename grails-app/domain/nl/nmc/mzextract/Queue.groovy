package nl.nmc.mzextract

class Queue {

	String run
	Integer status // 0 waiting, 1 running, 2 done, -1 failed
	Date dateCreated
	Date lastUpdated
	
	static mapping = {
		autoTimestamp true
	}

    static constraints = {
    }

    def runPath() {
    	return new String(run.decodeBase64())
    }

}
