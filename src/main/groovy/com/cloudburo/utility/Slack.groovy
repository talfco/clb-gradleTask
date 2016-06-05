package com.cloudburo.utility

class Slack {

	public Slack() {}
	
	public String sendSlackMessage(String webhookURL, String text, String channel, String userName, String iconURL, String iconEmoji) {
		String json = "payload={"
		String icon = ""
		String chan = ""
		String name = ""
		if (channel !="") {
			chan = "'channel' : '#${channel}'"
			json+="${chan}, "
			
		}
		if (name !="") {
			name = "'username' : '#${userName}'"
			json+="${name}, "
			
		}
		if (iconEmoji != "") 
			icon = "'icon_emoji' : '${iconEmoji}'"
		else if  (iconURL != "")
			icon = "'icon_url' : '${iconURL}'"
		if (icon != "")
		  json+="${icon}, "
		  
			
		json+= "'text': '${text}' }";
		json = json.replace("'", '"')
		String curlAction = "curl -X POST --data-urlencode '${json}' ${webhookURL}"
		doApiCallSingleEntryReturn(curlAction);
	}
		
	private boolean doApiCallSingleEntryReturn(curlAction) {
		StringBuffer out = new StringBuffer()
		//Utilities.SENSIBLEOUTPUT = true;
		def ret = Utilities.executeOnShellWithoutErrorRedirect(curlAction, out)
		//Utilities.SENSIBLEOUTPUT = false;
		if (ret == 0) {
			return true
		} else{ 
			log.error("MailgunAPI: Curl command failed ${ret}")
		   return false
		}
	}

}
