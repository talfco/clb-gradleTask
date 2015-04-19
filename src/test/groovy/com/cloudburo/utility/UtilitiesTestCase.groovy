package com.cloudburo.utility;

import static org.junit.Assert.*

import com.cloudburo.utility.Cryptor

import org.junit.Test
import org.junit.Before

class UtilitiesTestCase {
	
	Cryptor cryp
	Cloudflare gen
	
	@Before
	public void setUp() {
		cryp = new Cryptor();
		cryp.credentialsPath = "test"
		gen = new Cloudflare()
	}

	//@Test
	public void testSimple() {
			StringBuffer inp = new StringBuffer()
		    def ret = Utilities.executeOnShell("ls", new File("src/test/resources"), inp)
			println "'${inp}'"
			assert "test.user" == inp.toString()
			assert ret == 0
			
	}
	
	//@Test
	public void testSimple1() {
			StringBuffer inp = new StringBuffer()
			Utilities.SENSIBLEOUTPUT = true;
			def ret = Utilities.executeOnShell("ls", new File("src/test/resources"), inp)
			println "'${inp}'"
			assert "test.user" == inp.toString()
			assert ret == 0
			
	}
	
	//@Test
	public void testSimple2() {
		StringBuffer inp = new StringBuffer()
		Utilities.SENSIBLEOUTPUT = true;
		def ret = Utilities.executeOnShell("ls -lart", new File("src/test/resources"), inp)
		println "'${inp}'"
		assert ret == 0
			
	}
	
	//@Test
	public void testCloudflare() {
		def key = cryp.decrypt('cloudflare.key')
		def user = cryp.decrypt('cloudflare.user')
		// Add an entry
		def result = gen.configureCloudFlareDomainName(key, user,"curation.space","dognews1","test.test.test")
		assert(result)
		// Existing entry
		result = gen.configureCloudFlareDomainName(key, user,"curation.space","dognews1","test.test.test")
		assert(result)
		// Modify an entry
		result = gen.configureCloudFlareDomainName(key, user,"curation.space","dognews1","test1.test1.test1")
		assert(result)
		// Delete Entry
		result = gen.deleteCloudFlareDomainName(key, user,"curation.space","dognews1")
		assert(result)
		// Failure
		result = gen.configureCloudFlareDomainName(key, user,"curation.space1","dognews1","test1.test1.test1")
		assert(!result)
	}
	
	@Test 
	public void testTimeHandling() {
		int hour = Utilities.getCurrentHour("Europe/Zurich")
		System.out.println("Actual hour ${hour}")
		def inS = Utilities.isCurrentHourInRange("Europe/Zurich", 8,18)
		System.out.println("Actual hour ${inS}") 
		Utilities.updateProcessEntryInHour("build", "newsq", "blogtest", 5)
		int i = Utilities.getProcessEntryInHour("build", "newsq", "blogtest")
		
		System.out.println("Got ${i}")
		//Utilities.decreaseProcessEntryInHour("build", "newsq", "blogtest")
	}
	
	
	
}
