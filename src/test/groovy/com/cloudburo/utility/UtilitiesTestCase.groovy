package com.cloudburo.utility;

import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.Before;

class UtilitiesTestCase {

	@Test
	public void testSimple() {
			StringBuffer inp = new StringBuffer()
		    def ret = Utilities.executeOnShell("ls", new File("src/test/resources"), inp)
			println "'${inp}'"
			assert "test.user" == inp.toString()
			assert ret == 0
			
	}
	
	@Test
	public void testSimple1() {
			StringBuffer inp = new StringBuffer()
			Utilities.SENSIBLEOUTPUT = true;
			def ret = Utilities.executeOnShell("ls", new File("src/test/resources"), inp)
			println "'${inp}'"
			assert "test.user" == inp.toString()
			assert ret == 0
			
	}
	
	@Test
	public void testSimple2() {
			StringBuffer inp = new StringBuffer()
			Utilities.SENSIBLEOUTPUT = true;
			def ret = Utilities.executeOnShell("ls -lart", new File("src/test/resources"), inp)
			println "'${inp}'"
			assert "test.user" == inp.toString()
			assert ret == 0
			
	}
	
}
