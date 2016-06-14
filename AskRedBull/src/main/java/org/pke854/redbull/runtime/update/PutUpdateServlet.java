package org.pke854.redbull.runtime.update;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONObject;
import org.json.JSONTokener;

public class PutUpdateServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static final java.util.logging.Logger logger = java.util.logging.Logger
			.getLogger(PutUpdateServlet.class.getName());


	protected void doPut(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		Update anUpdate=null;
		InputStream is = req.getInputStream();
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(is,
					"utf-8"), 8);
			String json = "";
			do {
				json += br.readLine();
			} while (br.ready());
			br.close();
			JSONObject root = (JSONObject) new JSONTokener(json).nextValue();
			if (root.has("id")) {
				anUpdate=new Update();
				anUpdate.setId(root.getString("id"));
			}
			if (root.has("uscript")) {
				anUpdate.setUscript(StringEscapeUtils.unescapeEcmaScript(root.getString("uscript")));
			}
		} catch (Exception e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
		}
		logger.info("on /putupdate : " + anUpdate);
		if(anUpdate!=null) {
			GetUpdateServlet.updateMap.put(anUpdate.getId(), anUpdate);
			Semaphore sem = new Semaphore(0);
			if(GetUpdateServlet.semaphoreMap.containsKey(anUpdate.getId())) {
				sem = GetUpdateServlet.semaphoreMap.get(anUpdate.getId());
			}else {
				GetUpdateServlet.semaphoreMap.put(anUpdate.getId(), sem);
				logger.info("created sem for id: " + anUpdate.getId());
			}
			sem.release(sem.getQueueLength());
		}
	}
	
}
