package com.cloudburo.utility

import static org.junit.Assert.*

import org.junit.Test
import org.junit.Before

import com.cloudburo.utility.AwsS3

import groovy.util.logging.Slf4j

@Slf4j
class AwsS3TestCase {

	@Before
	public void setUp() {
		Utilities.executeOnShell("rm -rf build/tst", new File("."))
		Utilities.executeOnShell("mkdir build/tst",new File("."))
	}
	
	@Test
	public void testSync() {
		def bool = AwsS3.syncNecessary("s3://tst.ms.curationplatform.us-west-2/test", "build/tst")
		assert bool == true
		def ret = AwsS3.s3sync("s3://tst.ms.curationplatform.us-west-2/test", "build/tst")
		assert ret == 0
		bool = AwsS3.syncNecessary("s3://tst.ms.curationplatform.us-west-2/test", "build/tst")
		assert bool == false
	}
	
	@Test
	public void testGetEncrypted() {
		String output = AwsS3.getObject("tst.ms.curationplatform.us-west-2", "testenc/HelloWorld.txt")
		log.debug("Encryption Output: "+output)
		assert output.trim().equals("Hello World")
	}
	
	@Test
	public void testGetWithExistenceCheck() {
		String  output = AwsS3.getObjectWithExistenceCheck("tst.ms.curationplatform.us-west-2", "gugus/gugus")
		assert output.equals("")
		output = AwsS3.getObjectWithExistenceCheck("tst.ms.curationplatform.us-west-2", "testenc/HelloWorld.txt")
		log.debug("Encryption Output: "+output)
		assert output.trim().equals("Hello World")
	}
	
	
	
	@Test
	public void testPutGetDelete() {
		AwsS3.putObject("tst.ms.curationplatform.us-west-2", "test/putHelloWorld.txt", new File("test/helloWorldTest.txt"))
		String output = AwsS3.getObject("tst.ms.curationplatform.us-west-2", "test/putHelloWorld.txt")
		assert output.equals("Hello World Küsnacht")
		AwsS3.deleteObject("tst.ms.curationplatform.us-west-2", "test/putHelloWorld.txt")
	}
	
	@Test
	public void testPutGetDeleteEncrypted() {
		AwsS3.putEncryptedObject("tst.ms.curationplatform.us-west-2", "testenc/putEncHelloWorld.txt", new File("test/helloWorldTest.txt"))
		String output = AwsS3.getObject("tst.ms.curationplatform.us-west-2", "testenc/putEncHelloWorld.txt")
		assert output.equals("Hello World Küsnacht")
		AwsS3.deleteObject("tst.ms.curationplatform.us-west-2", "testenc/putEncHelloWorld.txt")
	}
	
}
