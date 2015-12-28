package com.cloudburo.utility

import java.io.File
import java.security.MessageDigest;

import org.joda.time.DateTime
import org.joda.time.DateTimeZone

import groovy.util.logging.Slf4j

@Slf4j
class Utilities {
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
					String fileText = file.text;
					if (fileText.contains(srcExp)) {
						if (SENSIBLEOUTPUT) 
						  log.debug("==> Replacing text in "+file.path)
						else
						  log.debug("==> Replacing "+srcExp+" in "+file.path)
					    fileText = fileText.replaceAll(srcExp, replaceText)
					    file.write(fileText);
					}
				}
			}
		}
		)

	}
	
	private static String generateMD5(String s) {
		MessageDigest digest = MessageDigest.getInstance("MD5")
		digest.update(s.bytes);
		new BigInteger(1, digest.digest()).toString(16).padLeft(32, '0')
	 }
	
	public static boolean fileChanged(File fi, String newContent) {
		if (generateMD5(fi.text).compareTo(generateMD5(newContent)) !=0) {
			log.info("File changed ${fi.name}")
			fi.write(newContent,'UTF8')
			return true
		}
		else return false
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
		log.debug "Calling '${command}' on directory '${workingDir}'"
		def process = new ProcessBuilder(addShellPrefix(command))
										  .directory(workingDir)
										  .redirectErrorStream(true)
										  .start()
		process.inputStream.eachLine {log.debug "Return from command: ${it}"}
		process.waitFor();
		return process.exitValue()
	} 
	

	
	public static def executeOnShell(String command, File workingDir, StringBuffer out) {
		if (SENSIBLEOUTPUT) {
		  if (command.length()>6)
		    log.debug "Calling '${command.substring(0,5)} <truncated>' on directory '${workingDir}'"
		  else
		    log.debug "Calling '${command}' on directory '${workingDir}'"
		}
		else
		  log.debug "Calling '${command}' on directory '${workingDir}'"
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
	
	public static int getCurrentHour(String tz) {
		DateTimeZone zone = DateTimeZone.forID(tz);
		DateTime dt = new DateTime(zone)
		return dt.getHourOfDay()
	}
	
	public static int getCurrentDay(String tz) {
		DateTimeZone zone = DateTimeZone.forID(tz);
		DateTime dt = new DateTime(zone)
		return dt.getDayOfWeek()
	}
	
	public static boolean isCurrentHourInRange(String tz, int from, int to) {
		int current = getCurrentHour(tz)
		boolean inS = current > from && current < to
	}
	
	public static int getProcessEntryInHour(String path, String processType, String key) {
		String fileName = "${path}/${processType}_${key}.txt";
		boolean created = new File(fileName).createNewFile()
		String txt = new File(fileName).text
		int currentHour = getCurrentHour()
		int count
		
		if (txt.contains(":")) {
		  String[] token = txt.tokenize(':')
		  if (token[0] == "${currentHour}") {
			  count = Integer.parseInt(token[1])
			  return count
		  } else {
		    return 0
		  }
		} else {
		    return 0
		}

	}
	
	public static void updateProcessEntryInHour(String path, String processType, String key, int entries) {
		String fileName = "${path}/${processType}_${key}.txt";
		boolean created = new File(fileName).createNewFile()
		String txt = new File(fileName).text
		int currentHour = getCurrentHour()
		int count
		if (txt.contains(":")) {
		  String[] token = txt.tokenize(':')
		  if (token[0] == "${currentHour}") {
			  count = Integer.parseInt(token[1])
			  count = count + entries
		  } else {
			count = entries
		  }
		} else {
		  count = entries
		}
		new File(fileName).write("${currentHour}:${count}")
	}
	
	public static boolean decreaseProcessEntryInHour(String path, String processType, String key, int entries) {
		String fileName = "${path}/${processType}_${key}.txt";
		boolean created = new File(fileName).createNewFile()
		if (created) return
		String txt = new File(fileName).text
		int currentHour = getCurrentHour()
		if (!txt.contains(":")) return
	    String[] token = txt.tokenize(':')
		if (token[0] != "${currentHour}") return
		int count = Integer.parseInt(token[1])
		count = count - entries
		if (count < 0 ) count = 0
		new File(fileName).write("${currentHour}:${count}")
	}
	
	public static String getEnv() {
		def env = System.getenv('ENV')
		if (!env) {	env = System.properties['ENV'] }
		if (!env) log.error("No ENV found in environment")
		return env
	}
	
}