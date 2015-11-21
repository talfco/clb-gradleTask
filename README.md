# clb-gradleTask

Gradle Tasks required for working with AWS 

##To deploy as Maven Library on Github

Clone the Maven Repositories to the project directory

    git clone https://github.com/talfco/clb_mvnrepo.git -b snapshots  maven-github-snapshots
    git clone https://github.com/talfco/clb_mvnrepo.git -b releases  maven-github-releases

Change to Source Directory

Either use snapshots or releases, modify the build.gradle

    cd clb-gradleTask
	gradle publishMavenPublicationToMavenRepository
	cd ../maven-github-snapshots
	git status
	git add -A
	git commit -m "Release"
	git push
	
