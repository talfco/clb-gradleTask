package com.cloudburo.utility

import groovy.util.logging.Slf4j
import java.nio.ByteBuffer
import java.nio.charset.Charset
import com.amazonaws.services.kms.AWSKMSClient
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.auth.AWSCredentials
import com.amazonaws.regions.Regions
import com.amazonaws.regions.Region
import com.amazonaws.services.kms.model.DecryptRequest
import com.amazonaws.services.kms.model.DecryptResult
import com.amazonaws.services.kms.model.EncryptRequest
import com.amazonaws.services.kms.model.EncryptResult


// Handles the encryption of passwords stored in the cloud
@Slf4j
class Cryptor   {
	
	String credentialsPath="src/main/resources/"
	String sslkey = getSSLKey()
	String ssliv = getSSLIv()
	static AWSKMSClient awsKmsClient = null
	
	Cryptor () {}
	
	static ByteBuffer str_to_bb(String msg, Charset charset){
		return ByteBuffer.wrap(msg.getBytes(charset))
	}
	
	static String bb_to_str(ByteBuffer buffer, Charset charset){
		byte[] bytes;
		if(buffer.hasArray()) {
			bytes = buffer.array()
		} else {
			bytes = new byte[buffer.remaining()]
			buffer.get(bytes)
		}
		return new String(bytes, charset);
	}
	
	public static AWSKMSClient getAWSKMSClient() {
		if (awsKmsClient == null) {
		  awsKmsClient = new AWSKMSClient(getAWSCredentials())
		  awsKmsClient.setRegion(Region.getRegion(Regions.fromName(getAWSRegion())))
		}
		return awsKmsClient
	} 
	
	public String encrypt(String inFileName, String outFileName) {
		StringBuffer out = new StringBuffer()
		Utilities.SENSIBLEOUTPUT = true
		def result = Utilities.executeOnShell("openssl des3 -e -K ${sslkey} -iv ${ssliv} -in ${inFileName} -out ${outFileName} -p",new File(credentialsPath),out)
		Utilities.SENSIBLEOUTPUT = false
		if (result != 0)
		  log.warn("Decrypt of '${infileName}' failed")
		return out.toString()
	}
	
	public String  decrypt(String fileName)  {
		StringBuffer out = new StringBuffer()
		Utilities.SENSIBLEOUTPUT = true
		def result = Utilities.executeOnShell("openssl des3 -d -K ${sslkey} -iv ${ssliv} -in ${fileName}",new File(credentialsPath),out) 
		Utilities.SENSIBLEOUTPUT = false
		if (result != 0)
		  log.warn("Decrypt of '${fileName}' failed")
		return out.toString()
	}

	static String getSSLKey() {
		return Utilities.getEnvVar('SSL_KEY')
	}
	
	static String getSSLIv() {
		return Utilities.getEnvVar('SSL_IV')
	}
	
	static public AWSCredentials getAWSCredentials() {
		return new BasicAWSCredentials(getAWSAccessKeyId(),getAWSSecretAccessKey())
	}
	
	static public ByteBuffer awsEncrypt(String data) {
		EncryptRequest req = new EncryptRequest()
		req.setKeyId(getAWSEncryptionKey() )
		req.setPlaintext(str_to_bb(data, Charset.forName("UTF-8") ))

		EncryptResult res = getAWSKMSClient().encrypt(req)
		return res.getCiphertextBlob()
	}
	
	static public String awsDecrypt(ByteBuffer data) {
		DecryptRequest req = new DecryptRequest()
		req.setCiphertextBlob(data)
		DecryptResult res = getAWSKMSClient().decrypt(req)	
		bb_to_str(res.plaintext,Charset.forName("UTF-8"))
	}
	
	static public String getAWSRegion () {
		return Utilities.getEnvVar('AWS_DEFAULT_REGION')
	}
	
	static String getAWSRegionUnderscore () {
		String env = System.getenv('AWS_DEFAULT_REGION')
		if (!env) {	env = System.properties['AWS_DEFAULT_REGION'] }
		if (!env) log.warn("No AWS_DEFAULT_REGION found in environment")
		env = env.replace("-","_")
		return env
	}
	
	static public String getAWSAccessKeyId () {
		return Utilities.getEnvVar('AWS_ACCESS_KEY_ID')
	}
	
	static public String getAWSSecretAccessKey () {
		return Utilities.getEnvVar('AWS_SECRET_ACCESS_KEY')
	}
	
	static public String getAWSEncryptionKey() {
		String env = Utilities.getEnv()
		String envId
		if (env == 'prod') envId = 'AWS_KMS_KEY'+"_"+getAWSRegionUnderscore()
		else envId = 'AWS_KMS_KEY'+"_TEST_"+getAWSRegionUnderscore()
		String key = System.getenv(envId)
		if (!key) key = System.properties[envId]
		if (!key)
		  log.error("No encryptionKey "+envId+" found in environment")
		else
		  log.debug("Using encryptionKey environmnent variable: "+envId)
		return key
	}

}
