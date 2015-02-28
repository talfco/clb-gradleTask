package com.cloudburo.utility

import org.apache.log4j.Logger

// Handles the encryption of passwords stored in the cloud
class Cryptor   {

	String credentialsPath="src/main/resources/"
	String sslkey
	String ssliv
	
	static final Logger logger = Logger.getLogger(Cryptor.class)
	
	Cryptor () {
		sslkey = getSSLKey()
		ssliv = getSSLIv()
	}
	
	String  decrypt(String fileName)  {
		StringBuffer out = new StringBuffer()
		def result = Utilities.executeOnShell("openssl des3 -d -K ${sslkey} -iv ${ssliv} -in ${fileName}",new File(credentialsPath),out) 
		if (result != 0)
		  logger.warn("Decrypt of '${fileName}' failed")
		return out.toString()
	}

	String getSSLKey() {
		def sslkey = System.getenv('SSL_KEY')
		if (!sslkey) {	sslkey = System.properties['SSL_KEY'] }
		return sslkey
	}
	
	String getSSLIv() {
		def ssliv = System.getenv('SSL_IV')
		if (!ssliv) { ssliv = System.properties['SSL_IV'] }
		return ssliv
	}

}
