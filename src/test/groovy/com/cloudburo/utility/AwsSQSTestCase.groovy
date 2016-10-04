package com.cloudburo.utility

import static org.junit.Assert.*

import org.junit.Test
import org.junit.Before

import com.cloudburo.utility.AwsSQS

import groovy.util.logging.Slf4j

@Slf4j 
class AwsSQSTestCase {

	@Before
	public void setUp() {
	}
	
	@Test
	public void testGetMessage() {
		AwsSQS.sendMessage("tst_ms_publishingbot_us-west-2", "Hello World", Cryptor.getAWSAccountId())
		String output = AwsSQS.receiveMessage("tst_ms_publishingbot_us-west-2", Cryptor.getAWSAccountId())	
		assert output.trim().equals("Hello World")
	}
	
}
