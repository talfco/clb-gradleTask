package com.cloudburo.utility;

import static org.junit.Assert.*;

import java.nio.ByteBuffer
import org.junit.Test;
import org.junit.Before;
import groovy.util.logging.Slf4j

@Slf4j
class CryptorTestCase {

	@Test
	public void testOpenSSLDecrypt() {
		Cryptor crypt = new Cryptor()
		crypt.credentialsPath = "src/test/resources/"
		String out = crypt.decrypt("test.user")
		assert "talfco" == out
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
