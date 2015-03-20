package com.cloudburo.utility

import org.apache.log4j.Logger;

import groovy.json.JsonBuilder;
import groovyx.net.http.HTTPBuilder
import static groovyx.net.http.ContentType.URLENC

class Cloudflare {
	
	static final Logger logger = Logger.getLogger(Cloudflare.class)

	public Cloudflare() {
		// TODO Auto-generated constructor stub
	}
	
	public static String CLOUDFLARE_URL="https://www.cloudflare.com/"
	public static String CLOUDFLARE_APIPATH="api_json.html"
	
	public boolean configureCloudFlareDomainName(String key, String user,String domainName,String subDomain, String cnameTarget) {
		def http = new HTTPBuilder( CLOUDFLARE_URL )
		boolean hasMore = true
		def offset = 0
		def blogDomain = "${subDomain}.${domainName}"
		def error = false
		def found = false
		
		while (hasMore) {
			def postBody = [a: 'rec_load_all', tkn: "${key}", email : "${user}", z: "${domainName}", o: "${offset}"]
			http.post( path: CLOUDFLARE_APIPATH, body: postBody, requestContentType: URLENC ) { resp, json ->
			  if (resp.statusLine.statusCode == 200) {
				  if (json.result == "success") {
					 hasMore = json.response.recs.has_more
					 offset += json.response.recs.count
					 logger.info("Cloudflare API result records received: ${offset} (hasMore: ${hasMore})")
					 def domainNameFound = false
					 // Search for a subdomain entry
					 json.response.recs.objs.findAll{ it.name =="${blogDomain}" }.each {
						 domainNameFound = true
						 logger.info("DNS configuration found for ${blogDomain} of type ${it.type}")
						 if (it.type.equals("CNAME")) {
							 if (it.content == cnameTarget) {
								 logger.info("DNS CNAME configuration already correct, nothing to do (${it.content})")
								 found = true
								 hasMore = false
								 return
							 } else {
								   found = false  // We do a creation afterwards
								  hasMore = false
								  logger.info("DNS CNAME configuration exists, content change necessary ${it.content} -> ${cnameTarget}, delete and re-create")
								  postBody = [a: 'rec_delete', tkn: "${key}", email : "${user}", z: "${domainName}", id: "${it.rec_id}"]
								  http.post( path: CLOUDFLARE_APIPATH, body: postBody, requestContentType: URLENC ) { resp1, json1 ->
									  if (resp1.statusLine.statusCode == 200) {
										  if (json1.result == "success") {
											  logger.info("DNS CNAME deleted ${blogDomain}")
										  } else {
											logger.error("configureCloudFlareDomainName (rec_delete): Cloudflare JSON Response NOK: '${json1.result}' - ${json1.msg}");
											error = true
										  }
									  } else {
										 logger.error("configureCloudFlareDomainName (rec_delete): Cloudflare HTTP Response NOK: '${resp1.statusLine.statusCode}'")
										 error = true
									  }
								  }
							 }
						 } else {
							 logger.error("configureCloudFlareDomainName (rec_load_all): DNS CNAME configuration of type '${it.type}' exists for '${blogDomain}', will not do anything")
							 error = true
							 hasMore = false
							 return
						 }
					 }
				  } else {
					logger.error("configureCloudFlareDomainName (rec_load_all): Cloudflare JSON Response NOK: '${json.result}' - ${json.msg}");
					error = true
					hasMore = false
					return
				  }
				  
			  } else {
			   logger.error("configureCloudFlareDomainName (rec_load_all): Cloudflare HTTP Response NOK: '${resp.statusLine.statusCode}'")
			   error = true
			   hasMore = false
			   return
			  }
			}
		}
		if (!found && !error) {
			logger.info("No DNS CNAME configuration found for ${blogDomain} going to create one")
			def postBody = [a: 'rec_new', tkn: "${key}", email : "${user}", z: "${domainName}", type: "CNAME", content: "${cnameTarget}", name: "${blogDomain}", ttl: '1', service_mode: '1']
			http.post( path: CLOUDFLARE_APIPATH, body: postBody, requestContentType: URLENC ) { resp, json ->
				if (resp.statusLine.statusCode == 200) {
					if (json.result == "success") {
						logger.info("DNS CNAME added for ${blogDomain} -> ${cnameTarget}")
						return true
					} else {
					  logger.error("configureCloudFlareDomainName (rec_new): Cloudflare JSON Response NOK: '${json.result}' - ${json.msg}");
					  return false
					}
				} else {
				   logger.error("configureCloudFlareDomainName (rec_new): Cloudflare HTTP Response NOK: '${resp.statusLine.statusCode}'")
				   return false
				}
			}
		} else return found
	}
	
	public boolean deleteCloudFlareDomainName(String key, String user,String domainName,String subDomain) {
		def http = new HTTPBuilder( CLOUDFLARE_URL )
		boolean hasMore = true
		def offset = 0
		def blogDomain = "${subDomain}.${domainName}"
		def error = false
		def found = false
		
		while (hasMore) {
			def postBody = [a: 'rec_load_all', tkn: "${key}", email : "${user}", z: "${domainName}", o: "${offset}"]
			http.post( path: CLOUDFLARE_APIPATH, body: postBody, requestContentType: URLENC ) { resp, json ->
			  if (resp.statusLine.statusCode == 200) {
				  if (json.result == "success") {
					 hasMore = json.response.recs.has_more
					 offset += json.response.recs.count
					 logger.info("Cloudflare API result records received: ${offset} (hasMore: ${hasMore})")
					 def domainNameFound = false
					 // Search for a subdomain entry
					 json.response.recs.objs.findAll{ it.name =="${blogDomain}" }.each {
						 logger.info("DNS configuration found for ${blogDomain} of type ${it.type}")
						 hasMore = false
						 postBody = [a: 'rec_delete', tkn: "${key}", email : "${user}", z: "${domainName}", id: "${it.rec_id}"]
						 http.post( path: CLOUDFLARE_APIPATH, body: postBody, requestContentType: URLENC ) { resp1, json1 ->
							 if (resp1.statusLine.statusCode == 200) {
								 if (json1.result == "success") {
									 logger.info("DNS CNAME deleted ${blogDomain}")
								 } else {
								   logger.error("configureCloudFlareDomainName (rec_delete): Cloudflare JSON Response NOK: '${json1.result}' - ${json1.msg}");
								   error = true
								 }
							 } else {
								logger.error("configureCloudFlareDomainName (rec_delete): Cloudflare HTTP Response NOK: '${resp1.statusLine.statusCode}'")
								error = true
							 }
						 }
						 
					 }
				  } else {
					logger.error("configureCloudFlareDomainName (rec_load_all): Cloudflare JSON Response NOK: '${json.result}' - ${json.msg}");
					error = true
					hasMore = false
					return
				  }
				  
			  } else {
			   logger.error("configureCloudFlareDomainName (rec_load_all): Cloudflare HTTP Response NOK: '${resp.statusLine.statusCode}'")
			   error = true
			   hasMore = false
			   return
			  }
			}
			return !error
		}
	}
	
	
}
