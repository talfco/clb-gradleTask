package com.cloudburo.utility;

import static org.junit.Assert.*;


import org.junit.Test;
import org.junit.Before;

class CryptorTestCase {

	//@Test
	public void testDecrypt() {
			Cryptor crypt = new Cryptor()
			crypt.credentialsPath = "src/test/resources/"
			String out = crypt.decrypt("test.user")
			assert "talfco" == out
	}
}
