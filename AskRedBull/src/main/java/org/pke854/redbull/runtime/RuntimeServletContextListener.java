package org.pke854.redbull.runtime;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.LogManager;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.pke854.redbull.runtime.nlu.PatternMatcher;

public class RuntimeServletContextListener implements ServletContextListener {
	
	private static final java.util.logging.Logger logger = java.util.logging.Logger
			.getLogger(RuntimeServletContextListener.class.getName());
	
	public static final String SPARQL_CACHE_FILE="sparql.cache.file";
	public static final String USE_SPARQL_CACHE="use.sparql.cache";
	public static final String SPARQL_CACHE="sparql.cache";


	public void contextInitialized(ServletContextEvent sce) {
		ServletContext servletContext = sce.getServletContext();
		configureLogging(servletContext);
		configure(servletContext);
		String nluDir = servletContext.getRealPath("/nlu");
		logger.info("nluDir: " + nluDir);
		Map<String, PatternMatcher> pmMap = new HashMap<String, PatternMatcher>();
		logger.info("pmMap created.");
		String sparqlDir=servletContext.getRealPath("/sparqltemplates");
		servletContext.setAttribute("sparqlDir", sparqlDir);
		logger.info("sparqlDir: " + sparqlDir);
		servletContext.setAttribute("PatternMatcherMap", pmMap);
		String sparqlCacheFile=(String)servletContext.getAttribute(SPARQL_CACHE_FILE);
		
		try{
			servletContext.addListener(RedBullRuntimeServletContextListener.class);
		}catch(Exception e){
			logger.log(Level.SEVERE, e.toString(), e);
		}
	}


	public void contextDestroyed(ServletContextEvent sce) {
		
	}
	
	private void configureLogging(ServletContext servletContext) {
		String realPath = servletContext.getRealPath("");
		try {
			String loggingF = realPath + "/config/logging.properties";
			logger.info("loggingF: " + loggingF);
			System.err.println("loggingF: " + loggingF);
			InputStream ins = new FileInputStream(loggingF);
			logger.info("logging ins: " + ins);
			LogManager.getLogManager().readConfiguration(ins);
			logger.info("logging config read from " + loggingF);
		} catch (Exception e) {
			e.printStackTrace();
			logger.log(Level.SEVERE, e.toString(), e);
		}
	}
	
	private void configure(ServletContext servletContext) {
		String realPath = servletContext.getRealPath("");
		try {
			String configF = realPath + "/config/config.properties";
			logger.info("configF: " + configF);
			System.err.println("configF: " + configF);
			InputStream ins = new FileInputStream(configF);
			Properties props= new Properties();
			props.load(ins);
			logger.info("config read from " + configF);
			ins.close();
			servletContext.setAttribute(SPARQL_CACHE_FILE, props.get(SPARQL_CACHE_FILE));
			boolean useCache=Boolean.parseBoolean(""+props.getProperty(USE_SPARQL_CACHE, "false"));
			servletContext.setAttribute(USE_SPARQL_CACHE, useCache);
		} catch (Exception e) {
			e.printStackTrace();
			logger.log(Level.SEVERE, e.toString(), e);
		}
	}

}
