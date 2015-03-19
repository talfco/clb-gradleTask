package com.cloudburo.utility

import java.io.File;

import org.apache.log4j.Logger

class Utilities {
	
	static final Logger logger = Logger.getLogger(Utilities.class)
	static boolean SENSIBLEOUTPUT = false

	/**
	 * Replace a text in a directory structure
	 * @param fdir : The directory from where to recursively search
	 * @param exts : A list of file extensions which must be considered in the search, e.g. .java, .yml
	 * @param srcExp : The expression which you are looking for
	 * @param replaceText : The text to be replaced
	 */
	public static void replaceTextInFiles(File fdir, def exts, String srcExp, String replaceText) {
		fdir.eachFileRecurse({file ->
			for (ext in exts){
				if (file.name.endsWith(ext)) {
					def fileText = file.text;
					if (fileText.contains(srcExp)) {
						if (SENSIBLEOUTPUT) 
						  logger.debug("==> Replacing text in "+file.path)
						else
						  logger.debug("==> Replacing "+srcExp+" in "+file.path)
					    fileText = fileText.replaceAll(srcExp, replaceText)
					    file.write(fileText);
					}
				}
			}
		}
		)
	}
	
	/**
	 * Executes a shell command in the user directory
	 * @param command : The shell command
	 * @return : The exit value of the shell command
	 */
	public static def executeOnShell(String command) {
		return executeOnShell(command, new File(System.properties.'user.dir'))
	}
	
	/**
	 *  Executes a shell command in the user directory 
	 * @param command: The shell command
	 * @param out: The output produced by the commant
	 * @return:  The exit value of the shell command
	 */
	public static def executeOnShell(String command, StringBuffer out) {
		return executeOnShell(command, new File(System.properties.'user.dir'),out)
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
	

	
	public static def executeOnShell(String command, File workingDir, StringBuffer out) {
		if (SENSIBLEOUTPUT) {
		  if (command.length()>6)
		    logger.debug "Calling '${command.substring(0,5)} <truncated>' on directory '${workingDir}'"
		  else
		    logger.debug "Calling '${command}' on directory '${workingDir}'"
		}
		else
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