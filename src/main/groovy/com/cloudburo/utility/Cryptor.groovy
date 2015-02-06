package com.cloudburo.utility

// Handles the encryption of passwords stored in the cloud
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
		def result = Utilities.executeOnShell("openssl des3 -d -K ${sslkey} -iv ${ssliv} -in ${fileName}",new File(credentialsPath),out) 
		assert result == 0
		return out.toString()
	}

	String getSSLKey() {
		def sslkey = System.getenv('SSL_KEY')
		if (!sslkey) {	sslkey = System.properties['SSL_KEY'] }
		assert(sslkey)
		return sslkey
	}
	
	String getSSLIv() {
		def ssliv = System.getenv('SSL_IV')
		if (!ssliv) { ssliv = System.properties['SSL_IV'] }
		assert (ssliv)
		return ssliv
	}

}
