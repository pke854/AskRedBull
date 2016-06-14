package org.pke854.redbull.runtime.nlu;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ListenToServlet extends HttpServlet {
	public static final int DEF_TIME_TO_REMOVE_UTT_LISTENER_MS=10000;
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final java.util.logging.Logger logger = java.util.logging.Logger
			.getLogger(ListenToServlet.class.getName());

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		ServletContext context = req.getServletContext();
		String id = req.getParameter("id");
		//TODO
		String utteranceListener = req.getParameter("utteranceListener");
		if (utteranceListener != null) {
			int milliseconds=DEF_TIME_TO_REMOVE_UTT_LISTENER_MS;
			
			String nluDir = context.getRealPath("/nlu");
			logger.info("nluDir: " + nluDir);
			PatternMatcher pm = ((Map<String, PatternMatcher>) context
					.getAttribute("PatternMatcherMap")).get(id);
			if(pm!=null){
				pm.setUtteranceListener(utteranceListener);
				pm.removeUtteranceListenerAfterTimeMillis(milliseconds);
			}
		}
	}
}
