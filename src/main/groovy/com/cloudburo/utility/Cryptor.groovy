package com.cloudburo.utility

import groovy.util.logging.Slf4j

// Handles the encryption of passwords stored in the cloud
@Slf4j
class Cryptor   {

	String credentialsPath="src/main/resources/"
	String sslkey
	String ssliv
	
	Cryptor () {
		sslkey = getSSLKey()
		ssliv = getSSLIv()
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
		return sslkey
	}
	
	String getSSLIv() {
		def ssliv = System.getenv('SSL_IV')
		if (!ssliv) { ssliv = System.properties['SSL_IV'] }
		return ssliv
	}

}
