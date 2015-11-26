package com.cloudburo.utility

import groovy.util.logging.Slf4j
import groovy.json.JsonBuilder;
import groovyx.net.http.HTTPBuilder
import static groovyx.net.http.ContentType.URLENC

@Slf4j
class Cloudflare {
	
	public Cloudflare() {}
	
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
					 log.info("Cloudflare API result records received: ${offset} (hasMore: ${hasMore})")
					 def domainNameFound = false
					 // Search for a subdomain entry
					 json.response.recs.objs.findAll{ it.name =="${blogDomain}" }.each {
						 domainNameFound = true
						 log.info("DNS configuration found for ${blogDomain} of type ${it.type}")
						 if (it.type.equals("CNAME")) {
							 if (it.content == cnameTarget) {
								 log.info("DNS CNAME configuration already correct, nothing to do (${it.content})")
								 found = true
								 hasMore = false
								 return
							 } else {
								   found = false  // We do a creation afterwards
								  hasMore = false
								  log.info("DNS CNAME configuration exists, content change necessary ${it.content} -> ${cnameTarget}, delete and re-create")
								  postBody = [a: 'rec_delete', tkn: "${key}", email : "${user}", z: "${domainName}", id: "${it.rec_id}"]
								  http.post( path: CLOUDFLARE_APIPATH, body: postBody, requestContentType: URLENC ) { resp1, json1 ->
									  if (resp1.statusLine.statusCode == 200) {
										  if (json1.result == "success") {
											  log.info("DNS CNAME deleted ${blogDomain}")
										  } else {
											log.error("configureCloudFlareDomainName (rec_delete): Cloudflare JSON Response NOK: '${json1.result}' - ${json1.msg}");
											error = true
										  }
									  } else {
										 log.error("configureCloudFlareDomainName (rec_delete): Cloudflare HTTP Response NOK: '${resp1.statusLine.statusCode}'")
										 error = true
									  }
								  }
							 }
						 } else {
							 log.error("configureCloudFlareDomainName (rec_load_all): DNS CNAME configuration of type '${it.type}' exists for '${blogDomain}', will not do anything")
							 error = true
							 hasMore = false
							 return
						 }
					 }
				  } else {
					log.error("configureCloudFlareDomainName (rec_load_all): Cloudflare JSON Response NOK: '${json.result}' - ${json.msg}");
					error = true
					hasMore = false
					return
				  }
				  
			  } else {
			   log.error("configureCloudFlareDomainName (rec_load_all): Cloudflare HTTP Response NOK: '${resp.statusLine.statusCode}'")
			   error = true
			   hasMore = false
			   return
			  }
			}
		}
		if (!found && !error) {
			log.info("No DNS CNAME configuration found for ${blogDomain} going to create one")
			def postBody = [a: 'rec_new', tkn: "${key}", email : "${user}", z: "${domainName}", type: "CNAME", content: "${cnameTarget}", name: "${blogDomain}", ttl: '1', service_mode: '1']
			http.post( path: CLOUDFLARE_APIPATH, body: postBody, requestContentType: URLENC ) { resp, json ->
				if (resp.statusLine.statusCode == 200) {
					if (json.result == "success") {
						log.info("DNS CNAME added for ${blogDomain} -> ${cnameTarget}")
						return true
					} else {
					  log.error("configureCloudFlareDomainName (rec_new): Cloudflare JSON Response NOK: '${json.result}' - ${json.msg}");
					  return false
					}
				} else {
				   log.error("configureCloudFlareDomainName (rec_new): Cloudflare HTTP Response NOK: '${resp.statusLine.statusCode}'")
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
					 log.info("Cloudflare API result records received: ${offset} (hasMore: ${hasMore})")
					 def domainNameFound = false
					 // Search for a subdomain entry
					 json.response.recs.objs.findAll{ it.name =="${blogDomain}" }.each {
						 log.info("DNS configuration found for ${blogDomain} of type ${it.type}")
						 hasMore = false
						 postBody = [a: 'rec_delete', tkn: "${key}", email : "${user}", z: "${domainName}", id: "${it.rec_id}"]
						 http.post( path: CLOUDFLARE_APIPATH, body: postBody, requestContentType: URLENC ) { resp1, json1 ->
							 if (resp1.statusLine.statusCode == 200) {
								 if (json1.result == "success") {
									 log.info("DNS CNAME deleted ${blogDomain}")
								 } else {
								   log.error("configureCloudFlareDomainName (rec_delete): Cloudflare JSON Response NOK: '${json1.result}' - ${json1.msg}");
								   error = true
								 }
							 } else {
								log.error("configureCloudFlareDomainName (rec_delete): Cloudflare HTTP Response NOK: '${resp1.statusLine.statusCode}'")
								error = true
							 }
						 }
						 
					 }
				  } else {
					log.error("configureCloudFlareDomainName (rec_load_all): Cloudflare JSON Response NOK: '${json.result}' - ${json.msg}");
					error = true
					hasMore = false
					return
				  }
				  
			  } else {
			   log.error("configureCloudFlareDomainName (rec_load_all): Cloudflare HTTP Response NOK: '${resp.statusLine.statusCode}'")
			   error = true
			   hasMore = false
			   return
			  }
			}
			return !error
		}
	}
	
	
}
