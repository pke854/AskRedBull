package org.pke854.redbull.runtime;

import java.util.logging.Level;

import javax.servlet.ServletContextAttributeEvent;
import javax.servlet.ServletContextAttributeListener;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class RedBullRuntimeServletContextListener implements
		ServletContextListener, ServletContextAttributeListener {

	private static final java.util.logging.Logger logger = java.util.logging.Logger
			.getLogger(RedBullRuntimeServletContextListener.class.getName());
	
	@Override
	public void attributeAdded(ServletContextAttributeEvent event) {
		// TODO Auto-generated method stub

	}

	@Override
	public void attributeRemoved(ServletContextAttributeEvent event) {
		// TODO Auto-generated method stub

	}

	@Override
	public void attributeReplaced(ServletContextAttributeEvent event) {
		// TODO Auto-generated method stub

	}

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		// TODO Auto-generated method stub

	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		
	}

}
