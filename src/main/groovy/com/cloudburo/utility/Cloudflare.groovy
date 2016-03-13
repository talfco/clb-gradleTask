package com.cloudburo.utility

import groovy.util.logging.Slf4j
import groovy.json.JsonBuilder;
import groovy.json.JsonSlurper

@Slf4j
class Cloudflare {
	
	public Cloudflare() {}
	
	public static String CLOUDFLARE_URL="https://www.cloudflare.com/"
	public static String CLOUDFLARE_APIPATH="api_json.html"
	
	private String generateCurl(verb,action,user,key) {
		return "curl -X ${verb} 'https://api.cloudflare.com/client/v4/${action}' -H 'X-Auth-Email: ${user}' -H 'X-Auth-Key: ${key}' -H 'Content-Type: application/json'"
	}

	
	public Object getZone(user,key,zoneName) {
		String curlAction = generateCurl("GET","zones?name=${zoneName}",user,key)
		doApiCallSingleEntryReturn( curlAction, "getZone", "${zoneName}",true)
	}
	
	public Object getDNSEntry(user,key,zoneName,type,dnsName) {
		def zoneObj = getZone(user,key,zoneName)
		if (zoneObj == null) return null
		String curlAction = generateCurl("GET","/zones/${zoneObj.id}/dns_records?type=${type}&name=${dnsName}",user,key)
		doApiCallSingleEntryReturn(curlAction, "getDNSEntry", "${zoneName}-${type}-${dnsName}",true)
	}
	
	public Object deleteDNSEntry(user,key,zoneName,dnsName) {
		def zoneObj = getZone(user,key,zoneName)
		if (zoneObj == null) return null
		def dnsObj = getDNSEntry(user,key,zoneName,"CNAME",dnsName)
		if (dnsObj == null) return null
		String curlAction = generateCurl("DELETE","/zones/${zoneObj.id}/dns_records/${dnsObj.id}",user,key)
		doApiCallSingleEntryReturn(curlAction, "deleteDNSEntry", "${zoneName}--${dnsName}",false)
	}
	
	public Object createUpdateDNSEntry(user,key,zoneName,type,dnsName,content) {
		def zoneObj = getZone(user,key,zoneName)
		if (zoneObj == null) return null
		def dnsObj = getDNSEntry(user,key,zoneName,type,dnsName) 
		if (dnsObj == null) {
		  log.debug("CloudflareAPI-createUpdateDNSEntry: creating entry ${dnsName}")
		  String curlAction = generateCurl("POST","/zones/${zoneObj.id}/dns_records",user,key)
		  String cnt = '{"type":"'+type+'","name":"'+dnsName+'","content":"'+content+'", "ttl": "1", "proxied": true}'
		  curlAction += " --data '${cnt}'"
		  return doApiCallSingleEntryReturn(curlAction, "createUpdateDNSEntry","${dnsName}-${content}",false)
		} else {
			log.debug("CloudflareAPI-createDNSEntry: udating entry ${dnsName}")
			String curlAction = generateCurl("PUT","/zones/${zoneObj.id}/dns_records/${dnsObj.id}",user,key)
			String cnt = '{"type":"'+type+'","name":"'+dnsName+'","content":"'+content+'", "ttl": "1", "proxied": true}'
			curlAction += " --data '${cnt}'"
			return doApiCallSingleEntryReturn(curlAction, "createUpdateDNSEntry","${dnsName}-${content}",false)
		}
	}
	
	private Object doApiCallSingleEntryReturn(curlAction,msg1,msg2,arr) {
		Utilities.SENSIBLEOUTPUT = true;
		StringBuffer out = new StringBuffer()
		def ret = Utilities.executeOnShellWithoutErrorRedirect(curlAction, out)
		Utilities.SENSIBLEOUTPUT = false;
		if (ret == 0) {
			def siteObj = new JsonSlurper().parseText(out.toString())
			if (siteObj.success) {
				log.debug("CloudflareAPI-${msg1}: '${msg2}' call successful")
				if (arr) {
					if (siteObj.result.size > 0)  return siteObj.result[0]
					log.debug("ClouflareAPI-${msg1}: ${msg2} not found")
				} else {
					return siteObj.result
				}
			}
			else {
				def errMsg = new JsonBuilder( siteObj.errors ).toPrettyString()
				log.error("ClouflareAPI-${msg1}: ${msg2} failed: ${errMsg}")
			}
		} else
			log.error("ClouflareAPI-${msg1}: Curl command failed ${ret}")
		return null;
	}
	
	public boolean configureCloudFlareDomainName(String key, String user,String domainName,String subDomain, String cnameTarget) {
		createUpdateDNSEntry(user,key,domainName,"CNAME",domainName+"."+subDomain, cnameTarget)
	}
	

	
	
}
