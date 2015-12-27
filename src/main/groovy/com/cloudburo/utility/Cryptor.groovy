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
	String sslkey
	String ssliv
	AWSKMSClient awsKmsClient = new AWSKMSClient(getAWSCredentials())
	
	Cryptor () {
		sslkey = getSSLKey()
		ssliv = getSSLIv()
		Regions reg= Regions.fromName(getAWSRegion())
		awsKmsClient.setRegion(Region.getRegion(reg))
	}
	
	
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
	
	String  decrypt(String fileName)  {
		StringBuffer out = new StringBuffer()
		Utilities.SENSIBLEOUTPUT = true
		def result = Utilities.executeOnShell("openssl des3 -d -K ${sslkey} -iv ${ssliv} -in ${fileName}",new File(credentialsPath),out) 
		Utilities.SENSIBLEOUTPUT = false
		if (result != 0)
		  log.warn("Decrypt of '${fileName}' failed")
		return out.toString()
	}

	String getSSLKey() {
		def sslkey = System.getenv('SSL_KEY')
		if (!sslkey) {	sslkey = System.properties['SSL_KEY'] }
		if (!sslkey) log.warn("No SSL_KEY found in environment")
		return sslkey
	}
	
	String getSSLIv() {
		def ssliv = System.getenv('SSL_IV')
		if (!ssliv) { ssliv = System.properties['SSL_IV'] }
		if (!ssliv) log.warn("No SSL_IV found in environment")
		return ssliv
	}
	
	AWSCredentials getAWSCredentials() {
		return new BasicAWSCredentials(getAWSAccessKeyId(),getAWSSecretAccessKey())
	}
	
	ByteBuffer awsEncrypt(String data) {
		EncryptRequest req = new EncryptRequest()
		req.setKeyId(getAWSEncryptionKey() )
		req.setPlaintext(str_to_bb(data, Charset.forName("UTF-8") ))

		EncryptResult res = awsKmsClient.encrypt(req)
		return res.getCiphertextBlob()
	}
	
	String awsDecrypt(ByteBuffer data) {
		DecryptRequest req = new DecryptRequest()
		req.setCiphertextBlob(data)
		DecryptResult res = awsKmsClient.decrypt(req)	
		bb_to_str(res.plaintext,Charset.forName("UTF-8"))
	}
	
	String getAWSRegion () {
		def env = System.getenv('AWS_DEFAULT_REGION')
		if (!env) {	env = System.properties['AWS_DEFAULT_REGION'] }
		if (!env) log.warn("No AWS_DEFAULT_REGION found in environment")
		return env
	}
	
	String getAWSRegionUnderscore () {
		String env = System.getenv('AWS_DEFAULT_REGION')
		if (!env) {	env = System.properties['AWS_DEFAULT_REGION'] }
		if (!env) log.warn("No AWS_DEFAULT_REGION found in environment")
		env = env.replace("-","_")
		return env
	}
	
	String getAWSAccessKeyId () {
		def sslkey = System.getenv('AWS_ACCESS_KEY_ID')
		if (!sslkey) {	sslkey = System.properties['AWS_ACCESS_KEY_ID'] }
		if (!sslkey) log.error("No AWS_ACCESS_KEY_ID found in environment")
		return sslkey
	}
	
	String getAWSSecretAccessKey () {
		def sslkey = System.getenv('AWS_SECRET_ACCESS_KEY')
		if (!sslkey) {	sslkey = System.properties['AWS_SECRET_ACCESS_KEY'] }
		if (!sslkey) log.error("No AWS_SECRET_ACCESS_KEY found in environment")
		return sslkey
	}
	
	String getAWSEncryptionKey() {
		String env = getEnv()
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
	
	String getEnv() {
		def env = System.getenv('ENV')
		if (!env) {	env = System.properties['ENV'] }
		if (!env) log.error("No ENV found in environment")
		return env
	}

}
