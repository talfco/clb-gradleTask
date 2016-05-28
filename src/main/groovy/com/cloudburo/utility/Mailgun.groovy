package com.cloudburo.utility

import groovy.util.logging.Slf4j
import groovy.json.JsonBuilder;
import groovy.json.JsonSlurper

@Slf4j
class Mailgun {
	
	public Cloudflare() {}
	
	public String sendEmail(String key,String mgdomain,String from,String to,String subject,String text) {
		String curlAction =  "curl -s --user 'api:${key}'  https://api.mailgun.net/v3/${mgdomain}/messages "
		curlAction += "-F from='${from}' "
		curlAction += "-F to='${to}' "
		curlAction += "-F subject='${subject}' "
		curlAction += "-F text='${text}' "
		doApiCallSingleEntryReturn(curlAction)
	}

	
	private boolean doApiCallSingleEntryReturn(curlAction) {
		//Utilities.SENSIBLEOUTPUT = true;
		StringBuffer out = new StringBuffer()
		def ret = Utilities.executeOnShellWithoutErrorRedirect(curlAction, out)
		// Utilities.SENSIBLEOUTPUT = false;
		if (ret == 0) {
			def siteObj = new JsonSlurper().parseText(out.toString())
			log.debug("MailgunAPI: 'Queued Email ${siteObj.id}' ")
			return true
		} else
			log.error("MailgunAPI: Curl command failed ${ret}")
		return false;
	}
	
}
