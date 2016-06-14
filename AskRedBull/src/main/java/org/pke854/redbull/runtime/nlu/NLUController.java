package org.pke854.redbull.runtime.nlu;

import java.util.logging.Level;

import javax.servlet.http.HttpServletRequest;

//@Controller
public class NLUController {

	private static final java.util.logging.Logger logger = java.util.logging.Logger
			.getLogger(NLUController.class.getName());
	
	//@Autowired ServletContext context;
	
//	@RequestMapping(value = "/interpret", method = RequestMethod.GET)
//	public @ResponseBody
//	Update getUpdate(
//			@RequestParam(value="id", required=true, defaultValue="000") String id,
//			@RequestParam(value="utt", required=true, defaultValue="Hello") String utt) {
//		logger.info("on /nlu if: "+id+";utt: " + utt);
//		//Update result=new Update(id, "overview({\"dbpprop:form\":\"Tablet_computer\", \"ont:manufacturer\":\"Samsung\"});");
//		//Update result=new Update(id, "overview({\"dbpprop:type\":\"Laptop or Notebook\"});");
//		
//		String nluDir=context.getRealPath("/nlu");
//		logger.info("nluDir: "+nluDir);
//		PatternMatcher pm = (PatternMatcher)context.getAttribute("PatternMatcher");
//		Update result=new Update(id, (pm).interpret(utt));
//		logger.info("return "+result);
//		return result;
//	}

//	@ExceptionHandler(IllegalArgumentException.class)
//	@ResponseStatus(HttpStatus.BAD_REQUEST)
//	@ResponseBody
	public String handleClientErrors(Exception ex) {
		logger.log(Level.SEVERE, "CLIENT ERROR: " + ex.getMessage(), ex);
		return ex.getMessage();
	}

//	@ExceptionHandler(Exception.class)
//	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
//	@ResponseBody
	public String handleServerErrors(Exception ex,
			HttpServletRequest httpRequest) {
		logger.log(Level.SEVERE, "SERVER ERROR: " + ex.getMessage(), ex);
		return ex.getMessage();
	}
}