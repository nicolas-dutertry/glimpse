package org.glimpse.server;

import java.io.File;
import java.io.FileInputStream;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SimpleConnectionManager implements ConnectionManager {
	private static final Log logger = LogFactory.getLog(SimpleConnectionManager.class);
	
	private File usersDirectory;
	private File passwordsFile;
	private transient Map<String, String>connectionsCache = new ConcurrentHashMap<String, String>(); 
	
	public SimpleConnectionManager(File usersDirectory) {
		this.usersDirectory = usersDirectory;
		passwordsFile = new File(usersDirectory, "passwords");
	}
	
	public String connect(String login, String password)
			throws AuthenticationException {
		File userDir = new File(usersDirectory, login);
		FileInputStream fis = null;
		
		try {
			if(passwordsFile.exists()) {
				Properties properties = new Properties();
				fis = new FileInputStream(passwordsFile);
				properties.load(fis);				
				String rightPassword = properties.getProperty(login);
				if(StringUtils.isNotEmpty(rightPassword) && rightPassword.equals(password)) {
					 String connectionId = RandomStringUtils.randomAlphanumeric(64);
					 File connectionDir = new File(userDir, "connections");
					 connectionDir.mkdirs();
					 File connectionFile = new File(connectionDir, connectionId); 
					 connectionFile.createNewFile();
					 
					 connectionsCache.put(connectionId, login);
					 
					 return connectionId;
				}
			}
		} catch(Exception e) {
			logger.error("Error", e);
		} finally {
			IOUtils.closeQuietly(fis);
		}
		throw new AuthenticationException("Unable to authenticate " + login);
	}

	public void disconnect(String connectionId) {
		connectionsCache.remove(connectionId);
		String userName = getUserId(connectionId);
		if(StringUtils.isNotBlank(userName)) {
			File userDir = new File(usersDirectory, userName);
			File connectionDir = new File(userDir, "connections");
			File connectionFile = new File(connectionDir, connectionId);
			if(connectionFile.exists()) {
				connectionFile.delete();
			}
		}
	}

	public String getUserId(String connectionId) {
		String userId = connectionsCache.get(connectionId);
		if(userId != null) {
			return userId;
		}
		
		File[] files = usersDirectory.listFiles();
		for (File userDir : files) {
			File connectionDir = new File(userDir, "connections");
			if(connectionDir.exists() && connectionDir.isDirectory()) {
				File[] connectionFiles = connectionDir.listFiles();
				for (File connectionFile : connectionFiles) {
					if(connectionFile.getName().equals(connectionId)) {
						userId = userDir.getName();
						connectionsCache.put(connectionId, userId);
						return userId;
					}
				}
			}
		}
		return null;
	}

}
