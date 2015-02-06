package com.cloudburo.utility

import java.io.File;

import org.apache.log4j.Logger

class Utilities {
	
	static final Logger logger = Logger.getLogger(Utilities.class)

	public static void replaceTextInFiles(File fdir, def exts, String srcExp, String replaceText) {
		fdir.eachFileRecurse({file ->
			for (ext in exts){
				if (file.name.endsWith(ext)) {
					def fileText = file.text;
					if (fileText.contains(srcExp)) {
						logger.debug("==> Replacing "+srcExp+" in "+file.path)
					    fileText = fileText.replaceAll(srcExp, replaceText)
					    file.write(fileText);
					}
				}
			}
		}
		)
	}
	
	public static def executeOnShell(String command) {
		return executeOnShell(command, new File(System.properties.'user.dir'))
	  }
	
	public static def executeOnShell(String command, File workingDir) {
		logger.debug "Calling '${command}' on directory '${workingDir}'"
		def process = new ProcessBuilder(addShellPrefix(command))
										  .directory(workingDir)
										  .redirectErrorStream(true)
										  .start()
		process.inputStream.eachLine {logger.debug "Return from command: ${it}"}
		process.waitFor();
		return process.exitValue()
	} 
	
	public static def executeOnShell(String command, StringBuffer out) {
		return executeOnShell(command, new File(System.properties.'user.dir'),out)
	  }
	
	public static def executeOnShell(String command, File workingDir, StringBuffer out) {
		logger.debug "Calling '${command}' on directory '${workingDir}'"
		def process = new ProcessBuilder(addShellPrefix(command))
										  .directory(workingDir)
										  .redirectErrorStream(true)
										  .start()
		boolean notFirst = false;
		process.inputStream.eachLine { 
			if (notFirst) { out.append('\n') }
			notFirst = true
			out.append(it); 
		}
		process.waitFor();
		return process.exitValue()
	}
	
	private static def addShellPrefix(String command) {
		def commandArray = new String[3]
		commandArray[0] = "sh"
		commandArray[1] = "-c"
		commandArray[2] = command
		return commandArray
	}
}