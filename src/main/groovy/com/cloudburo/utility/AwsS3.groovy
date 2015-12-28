package com.cloudburo.utility

import org.apache.commons.io.IOUtils
import java.nio.charset.StandardCharsets

import com.amazonaws.regions.Regions
import com.amazonaws.regions.Region
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.GetObjectRequest
import com.amazonaws.services.s3.model.PutObjectRequest
import com.amazonaws.services.s3.model.S3ObjectInputStream
import com.amazonaws.services.s3.model.SSEAwsKeyManagementParams
import com.amazonaws.services.s3.model.S3Object
import com.amazonaws.SDKGlobalConfiguration

import groovy.util.logging.Slf4j

@Slf4j
class AwsS3 {
	
	static AmazonS3Client awsS3Client = null

	public AwsS3() {}
	
	public static AmazonS3Client getS3Client() {
		System.setProperty(SDKGlobalConfiguration.ENABLE_S3_SIGV4_SYSTEM_PROPERTY, "true");
		if (awsS3Client == null) {
			awsS3Client = new AmazonS3Client (Cryptor.getAWSCredentials())
			awsS3Client.setRegion(Region.getRegion(Regions.fromName(Cryptor.getAWSRegion())))
		}
		return awsS3Client;
	} 
	
	public static int s3sync (String from, String to) {
		String cmd = "aws s3 sync "+from+" "+to+" --region "+Cryptor.getAWSRegion()
		s3://tst.ms.curationplatform.us-west-2/siteconfigs . --dryrun
		StringBuffer buf = new StringBuffer()
		return Utilities.executeOnShell(cmd, new File("."),buf)
	}
	
	public static boolean syncNecessary(String from, String to) {
		String cmd = "aws s3 sync "+from+" "+to+" --region "+Cryptor.getAWSRegion()+" --dryrun"
		StringBuffer buf = new StringBuffer()
		Utilities.executeOnShell(cmd, new File("."),buf)
		log.debug("Output: "+buf.toString())
		return buf.contains("download")
	}
	
	public static String getObject (String bucket, String key ) {
		AmazonS3Client client = getS3Client()
		GetObjectRequest req = new GetObjectRequest(bucket,key)
		S3Object data= client.getObject(req)
		StringWriter writer = new StringWriter();
		IOUtils.copy(data.getObjectContent(), writer, StandardCharsets.UTF_8.toString());
		return writer.toString();
	}
	
	public static String putEncryptedObject (String bucket, String key, File file) {
		AmazonS3Client client = getS3Client()
		PutObjectRequest req = new PutObjectRequest(bucket,key, file).withSSEAwsKeyManagementParams(new SSEAwsKeyManagementParams(Cryptor.getAWSEncryptionKey()))
		client.putObject(req)
	}
	
	public static void putObject ( String bucket, String key, File file) {
		AmazonS3Client client = getS3Client()
		PutObjectRequest req = new PutObjectRequest(bucket,key, file)
		client.putObject(req)
	}
	
}
