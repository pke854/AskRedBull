package org.pke854.redbull.runtime;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

public class NoCacheFilter {
	public void doFilter(ServletRequest req, ServletResponse res,
			FilterChain chain) throws IOException, ServletException {
		HttpServletResponse response = (HttpServletResponse) res;
		response.setHeader("Cache-Control",
				"max-age=0, private, must-revalidate");
		chain.doFilter(req, res);
	}

	public void destroy() {
	}

	public void init(FilterConfig arg0) throws ServletException {
	}
}
