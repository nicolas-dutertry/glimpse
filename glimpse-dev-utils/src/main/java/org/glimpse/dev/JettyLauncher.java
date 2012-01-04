/* *******************************************************
 * © 1996-2009 HR Access Solutions. All rights reserved
 * ******************************************************/

package org.glimpse.dev;

import java.io.File;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;


public class JettyLauncher {

	public static void main(String[] args) {
		try {
			if (args.length != 3) {
				printUsage();
				return;
			}
			String webAppPath = args[0];
			String appName = args[1];

			Server jetty = new Server(Integer.parseInt(args[2]));
			WebAppContext context = new WebAppContext(webAppPath, "/" + appName);

			File jettyDir = new File(new File(System.getProperty("java.io.tmpdir")), "jetty");
			File appDir = new File(jettyDir, appName);
			appDir.mkdirs();

			context.setTempDirectory(appDir);

			jetty.setHandler(context);
			
			jetty.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void printUsage() {
		System.out
				.println("Usage : org.glimpse.dev.JettyLauncher webappDir wabappName port");
		System.out
				.println("   - webappDir : path to the directory containing the web application");
		System.out
				.println("   - webappName : web application name (context path)");
		System.out.println("   - port : listening port");
	}

}