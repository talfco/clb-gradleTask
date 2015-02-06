package com.cloudburo.utility;

import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.Before;

class UtilitiesTestCase {

	@Test
	public void test() {
			StringBuffer inp = new StringBuffer()
		    def ret = Utilities.executeOnShell("ls", new File("src/test/resources"), inp)
			println "'${inp}'"
			assert "test.user" == inp.toString()
			assert ret == 0
			
	}
	
}
