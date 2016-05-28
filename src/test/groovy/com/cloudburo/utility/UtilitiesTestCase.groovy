package com.cloudburo.utility;

import static org.junit.Assert.*

import com.cloudburo.utility.Cryptor

import org.junit.Test
import org.junit.Before

class UtilitiesTestCase {
	
	Cryptor cryp
	Cloudflare gen
	Mailgun gun
	
	@Before
	public void setUp() {
		cryp = new Cryptor();
		cryp.credentialsPath = "test"
		gen = new Cloudflare()
		gun = new Mailgun()
	}

	//@Test
	public void testSimple() {
			StringBuffer inp = new StringBuffer()
		    def ret = Utilities.executeOnShell("ls", new File("test/dummy"), inp)
			println "'${inp}'"
			assert "dummy.txt" == inp.toString()
			assert ret == 0
			
	}
	
	//@Test
	public void testSimple1() {
			StringBuffer inp = new StringBuffer()
			Utilities.SENSIBLEOUTPUT = true;
			def ret = Utilities.executeOnShell("ls", new File("test/dummy"), inp)
			println "'${inp}'"
			assert "dummy.txt" == inp.toString()
			assert ret == 0
			
	}
	
	//@Test
	public void testSimple2() {
		StringBuffer inp = new StringBuffer()
		Utilities.SENSIBLEOUTPUT = true;
		def ret = Utilities.executeOnShell("ls -lart", new File("test"), inp)
		println "'${inp}'"
		assert ret == 0
			
	}
	
	//@Test
	public void testCloudflare1() {
		def user = Utilities.getEnvVar("CLF_ACCESS_KEY")
		def key = Utilities.getEnvVar("CLF_SECRET_ACCESS_KEY")
		// Zone
		def result = gen.getZone(user, key,"curation.space")
		assert(result != null)
		assert(result.name == "curation.space")
		result = gen.getZone(user, key,"curation.spacegugus")
		assert(result == null)
		// DNS Entry
		result = gen.getDNSEntry(user, key, "curation.space", "CNAME", "dognews.curation.space")
		assert(result != null)
		assert(result.name == "dognews.curation.space")
		//System.out.println ( result)
		result = gen.getDNSEntry(user, key, "curation.space", "CNAME", "dognewsgugus.curation.space")
		assert(result == null)
		result = gen.createUpdateDNSEntry(user,key, "curation.space", "CNAME", "dognewsgugus.curation.space","acloudburo.github.io")
		assert(result != null)
		assert(result.name == "dognewsgugus.curation.space")
		result = gen.createUpdateDNSEntry(user,key, "curation.space", "CNAME", "dognewsgugus.curation.space","acloudburo1.github.io")
		assert(result != null)
		assert(result.content == "acloudburo1.github.io")
		result = gen.deleteDNSEntry(user, key, "curation.space", "dognewsgugus.curation.space")
		assert(result != null)
		assert(result.id)	
	}
	
	//@Test 
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
	
	@Test
	public void testMailgun() {
		def key  = Utilities.getEnvVar("MG_KEY") 
		def domain  = Utilities.getEnvVar("MG_DOMAIN")
		gun.sendEmail(key, domain, "Cloudburo Publishing Bot <support@cloudbro.net>", "felix@cloudburo.net", "Your Site is live", "Hi there, \nwe created your site which is ready to be served with fresh content from Evernote.")
	}
	
	
	
}
