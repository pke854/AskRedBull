package org.pke854.redbull.runtime.update;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;

public class GetUpdateServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final java.util.logging.Logger logger = java.util.logging.Logger
			.getLogger(GetUpdateServlet.class.getName());

	protected static Map<String, Update> updateMap = new ConcurrentHashMap<String, Update>();
	protected static Map<String, Semaphore> semaphoreMap = new ConcurrentHashMap<String, Semaphore>();

	private static final int DEF_TIMEOUT_MS = 30000;

	protected synchronized void doGet(HttpServletRequest req,
			HttpServletResponse resp) throws ServletException, IOException {
		Update result = null;
		String id = req.getParameter("id");
		String utt = req.getParameter("utt");
		if (utt != null)
			utt = URLDecoder.decode(utt, "utf-8");
		logger.info("on /getupdate id: " + id);
		if (isValid(id)) {
			Semaphore sem = new Semaphore(0);
			if (!semaphoreMap.containsKey(id)) {
				semaphoreMap.put(id, sem);
				logger.info("created sem for id: " + id);
			} else {
				sem = semaphoreMap.get(id);
				try {
					if (!sem.tryAcquire(DEF_TIMEOUT_MS, TimeUnit.MILLISECONDS)) {
						// timeout
						logger.info("a timeout " + DEF_TIMEOUT_MS
								+ "ms occured on id: " + id);
					}
					result = updateMap.remove(id);
				} catch (InterruptedException ie) {
					logger.log(Level.SEVERE,
							"CLIENT ERROR: " + ie.getMessage(), ie);
				}
			}
		} else {
			logger.warning("id '" + id + " is not valid.");
		}
		String res = "{}";
		if (result != null) {
			ObjectMapper mapper = new ObjectMapper();
			res = mapper.writeValueAsString(result);
		}
		logger.info("return " + result);
		resp.setContentType("application/json");
		resp.setContentLength(res.getBytes("utf-8").length);
		
		resp.getWriter().print(res);
		logger.info("return " + res);

	}

	private boolean isValid(String id) {
		return "0".equals(id);
	}

}
