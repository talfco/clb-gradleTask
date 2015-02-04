package com.ubs.cloudburo.utility

class Cryptor {
	
	def sslkey
	def ssliv
	
	Cryptor () {
		sslkey = getSSLKey()
		assert sslkey
		ssliv = getSSLIv()
		assert ssliv
	}
	
	String  decrypt(String fileName)  {
		def outputAsString
		new ByteArrayOutputStream().withStream { os ->
			def result = exec {
				executable = "openssl"
				args =["des3",
					  "-d",
					  "-K",
					  "${sslkey}",
					  "-iv",
					  "${ssliv}",
					  "-in",
					  "src/main/resources/${fileName}"
					 ]
				standardOutput = os
			}
			outputAsString = os.toString()
		}
		return outputAsString
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
