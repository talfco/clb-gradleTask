# clb-gradleTask

## Overview

Gradle Utilities Library required by the Cloudburo Microservices for working with 

  * Amazon Webservices (AWS)
  * Cloudflare Services
  * OpenSSL  
  
## Usage Instructions

### Environment Variables

The library is dependent on System.properties or Environment variables, which are used for to establish connectivity to AWS, as well are providing are AWS KMS encryption key.

S3 Buckets, as well as KMS encryption keys must be configured in the **same region**   

    export AWS_DEFAULT_REGION=us-west-2
    export AWS_ACCESS_KEY_ID=....
    export AWS_SECRET_ACCESS_KEY=...
    export AWS_KMS_KEY_us_west_2=arn:aws:kms:us-west-2:...
    export AWS_KMS_KEY_TEST_us_west_2=arn:aws:kms:us-west-2:...

The library will use the following code sequence to retrieve the relevant values

    static public String getAWSAccessKeyId () {
		def sslkey = System.getenv('AWS_ACCESS_KEY_ID')
		if (!sslkey) {	sslkey = System.properties['AWS_ACCESS_KEY_ID'] }
		if (!sslkey) log.error("No AWS_ACCESS_KEY_ID found in environment")
		return sslkey
    }

The environment variable handling is implemented in the `com.cloudburo.utility.Cryptor`

## Build Instructions (internal)

##To deploy as Maven Library on Github

#### Clone the Maven Repositories to the project directory

    git clone https://github.com/talfco/clb_mvnrepo.git -b snapshots  maven-github-snapshots
    git clone https://github.com/talfco/clb_mvnrepo.git -b releases  maven-github-releases

#### Either use snapshots or releases, modify the build.gradle

Change to the project directory.

    cd clb-gradleTask
	gradle publishMavenPublicationToMavenRepository
	cd ../maven-github-snapshots
	git status
	git add -A
	git commit -m "Release"
	git push
	
Change in the gradle build file in case you deploy to release instead of snapshot (TODO)
	