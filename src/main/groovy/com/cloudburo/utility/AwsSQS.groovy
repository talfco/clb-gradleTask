package com.cloudburo.utility

import com.amazonaws.regions.Regions
import com.amazonaws.regions.Region
import com.amazonaws.services.sqs.AmazonSQSClient
import com.amazonaws.services.sqs.model.GetQueueUrlResult
import com.amazonaws.services.sqs.model.ReceiveMessageResult
import com.amazonaws.services.sqs.model.ReceiveMessageRequest
import com.amazonaws.services.sqs.model.GetQueueUrlRequest
import com.amazonaws.services.sqs.model.Message
import com.amazonaws.SDKGlobalConfiguration
import com.jcraft.jsch.ChannelForwardedTCPIP.Config;

import groovy.json.JsonSlurper
import groovy.util.logging.Slf4j

@Slf4j
class AwsSQS {
	
	static AmazonSQSClient awsSQSClient = null
	static String queueURL = null
	
	public AwsSQS() {}
	
	public static AmazonSQSClient getSQSClient() {
		SDKGlobalConfiguration cfg = new SDKGlobalConfiguration()
		
		if (awsSQSClient == null) {
			awsSQSClient = new AmazonSQSClient (Cryptor.getAWSCredentials())
			awsSQSClient.setRegion(Region.getRegion(Regions.fromName(Cryptor.getAWSRegion())))
		}
		return awsSQSClient;
	}
	
	public static String getQueueURL(String queueName, String queueOwnerAWSAccountId) {
		GetQueueUrlRequest req = new GetQueueUrlRequest()
		req.queueOwnerAWSAccountId = queueOwnerAWSAccountId
		req.queueName = queueName
		GetQueueUrlResult  url = getSQSClient().getQueueUrl(req)
		return url.queueUrl
	}
	
	public static String receiveMessage(String queueName, String queueOwnerAWSAccountId) {
		ReceiveMessageRequest req = new ReceiveMessageRequest()
		req.setMaxNumberOfMessages(1)
		log.debug ("Got QueueURL: "+ getQueueURL(queueName,queueOwnerAWSAccountId) )
		req.setQueueUrl(getQueueURL(queueName,queueOwnerAWSAccountId))
		ReceiveMessageResult res = getSQSClient().receiveMessage(req)
		List<Message> msgList = res.getMessages()
		if (msgList.empty) {
		    log.debug("SQS Message Queue Empty")
			return null
		}
		else {
		    log.debug("Got SQS Message: "+ msgList.get(0).body)
			return msgList.get(0).body
		}
	}
	
}
