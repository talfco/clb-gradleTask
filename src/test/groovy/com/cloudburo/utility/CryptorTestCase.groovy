package com.cloudburo.utility;

import static org.junit.Assert.*;

import java.nio.ByteBuffer
import org.junit.Test;
import org.junit.Before;
import groovy.util.logging.Slf4j

@Slf4j
class CryptorTestCase {
	
	@Before
	public void setUp() {
		Utilities.executeOnShell("cp test/helloWorldTest.txt build/.", new File("."))
	}

	@Test
	public void testOpenSSLEncryptDecrypt() {
		Cryptor crypt = new Cryptor()
		crypt.credentialsPath = "build"
		crypt.encrypt("helloWorldTest.txt", "helloWorldTestEnc.txt")
		
		String out = crypt.decrypt("helloWorldTestEnc.txt")
		assert "Hello World Küsnacht" == out
	}

	@Test
	public void testAwsKMSEncryptDecrypt() {
		String data = "Hello World"
		Cryptor crypt = new Cryptor()
		ByteBuffer buf =  crypt.awsEncrypt(data)
		String data1 = crypt.awsDecrypt(buf)
		assert data == data1

	}
	
}
