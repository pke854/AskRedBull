package org.pke854.redbull.runtime;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.LogManager;

import javax.servlet.ServletContext;

import org.pke854.redbull.runtime.nlu.PatternMatcher;


//@ComponentScan
//@EnableAutoConfiguration
public class WebAppInitializer 
//implements WebApplicationInitializer 
{

//	private static Logger LOG = LoggerFactory
//			.getLogger(WebAppInitializer.class);

	private static final java.util.logging.Logger logger = java.util.logging.Logger
			.getLogger(WebAppInitializer.class.getName());
	
	public static final String SPARQL_CACHE_FILE="sparql.cache.file";
	public static final String USE_SPARQL_CACHE="use.sparql.cache";
	public static final String SPARQL_CACHE="sparql.cache";

	
	public void onStartup(ServletContext servletContext) {
		configureLogging(servletContext);
		configure(servletContext);
		String nluDir = servletContext.getRealPath("/nlu");
		logger.info("nluDir: " + nluDir);
		PatternMatcher pm = new PatternMatcher(nluDir);
		logger.info("pm created.");
		String sparqlDir=servletContext.getRealPath("/sparqltemplates");
		servletContext.setAttribute("sparqlDir", sparqlDir);
		logger.info("sparqlDir: " + sparqlDir);
		servletContext.setAttribute("PatternMatcher", pm);
		String sparqlCacheFile=(String)servletContext.getAttribute(SPARQL_CACHE_FILE);
		
		try{
			servletContext.addListener(RedBullRuntimeServletContextListener.class);
		}catch(Exception e){
			logger.log(Level.SEVERE, e.toString(), e);
		}
//		WebApplicationContext rootContext = createRootContext(servletContext);
//		configureSpringMvc(servletContext, rootContext);
	}

//	private WebApplicationContext createRootContext(
//			ServletContext servletContext) {
//		AnnotationConfigWebApplicationContext rootContext = new AnnotationConfigWebApplicationContext();
//		rootContext.register(CoreConfig.class);
//		rootContext.refresh();
//		servletContext.addListener(new ContextLoaderListener(rootContext));
//		servletContext.setInitParameter("defaultHtmlEscape", "true");
////		servletContext.addFilter("securityFilter", new SimpleCORSFilter())
////				.addMappingForUrlPatterns(null, false, "/*","/frontend/js/*");
//		return rootContext;
//	}

//	private void configureSpringMvc(ServletContext servletContext,
//			WebApplicationContext rootContext) {
//		AnnotationConfigWebApplicationContext mvcContext = new AnnotationConfigWebApplicationContext();
//		mvcContext.register(MVCConfig.class);
//
//		mvcContext.setParent(rootContext);
//		
//		ServletRegistration.Dynamic appServlet = servletContext.addServlet(
//				"webservice", new DispatcherServlet(mvcContext));
//		
//		appServlet.setLoadOnStartup(1);
//		
//		Set<String> mappingConflicts = appServlet.addMapping("/");
//		mappingConflicts.addAll(appServlet.addMapping("/frontend"));
//		mappingConflicts.addAll(appServlet.addMapping("/frontend/js"));
//		mappingConflicts.addAll(appServlet.addMapping("/mespeak"));
//		mappingConflicts.addAll(appServlet.addMapping("/mespeak/voices"));
//		mappingConflicts.addAll(appServlet.addMapping("/mespeak/voices/en"));
//		mappingConflicts.addAll(appServlet.addMapping("/jquery.sparql"));
//		mappingConflicts.addAll(appServlet.addMapping("/ide"));
//		mappingConflicts.addAll(appServlet.addMapping("/ide/codemirror"));
//		mappingConflicts.addAll(appServlet.addMapping("/ide/js"));
//		servletContext.getServletRegistration("default").addMapping("*.html",
//				"*.js", "*.json", "*.css", "*.jpg", "*.gif", "*.png");
//		if (!mappingConflicts.isEmpty()) {
//			for (String s : mappingConflicts) {
////				LOG.error("Mapping conflict: " + s);
//			}
//			throw new IllegalStateException(
//					"'webservice' cannot be mapped to '/'");
//		}
//	}

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
