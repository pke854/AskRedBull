package org.pke854.redbull.runtime.nlu;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.pke854.redbull.runtime.update.Update;

import com.fasterxml.jackson.databind.ObjectMapper;

public class NLUServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static final java.util.logging.Logger logger = java.util.logging.Logger
			.getLogger(NLUServlet.class.getName());
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		ServletContext context = req.getServletContext();
		String id=req.getParameter("id");
		String lang=req.getParameter("lang");
		String utt=req.getParameter("utt");
		if(utt!=null)utt=URLDecoder.decode(utt, "utf-8");
		String nluDir=context.getRealPath("/nlu");
		logger.info("nluDir: "+nluDir);
		PatternMatcher pm = ((Map<String, PatternMatcher>) context
				.getAttribute("PatternMatcherMap")).get(id);
		if(pm==null){
			pm=new PatternMatcher(nluDir);
			((Map<String, PatternMatcher>) context
					.getAttribute("PatternMatcherMap")).put(id, pm);
		}
		ObjectMapper mapper = new ObjectMapper();
		String result = mapper.writeValueAsString(new Update(id, (pm).interpret(utt)));
		logger.info("return "+result);
		resp.setContentType("application/json");
		resp.setContentLength(result.getBytes("utf-8").length);
		resp.getWriter().print(result.toString());
	}

	
}
