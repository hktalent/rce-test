/*
 * Copyright (C) 2006-2014 DLR, Germany
 * 
 * All rights reserved
 * 
 * http://www.rcenvironment.de/
 *
 * Author: Robert Mischke
 */

class MavenIntegration {
	
	private mavenProperties // injected
	
	MavenIntegration(project) {
		mavenProperties = project.properties
	}
	
	def getProperty(String key) {
		return mavenProperties.getProperty(key)
	}

	def setProperty(String key, String value) {
		mavenProperties.setProperty(key, value)
	}

	def abortBuild(String message) {
		throw new IllegalArgumentException("Error in configuration preprocessor: $message")
	}
	
}
