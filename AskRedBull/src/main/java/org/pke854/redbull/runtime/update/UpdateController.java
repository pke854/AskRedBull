package org.pke854.redbull.runtime.update;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;

import javax.servlet.http.HttpServletRequest;


//@Controller
public class UpdateController {

	private static final java.util.logging.Logger logger = java.util.logging.Logger
			.getLogger(UpdateController.class.getName());

	private static Map<String, Update> updateMap = new ConcurrentHashMap<String, Update>();
	private static Map<String, Semaphore> semaphoreMap = new ConcurrentHashMap<String, Semaphore>();

	private static final int DEF_TIMEOUT_MS=30000;
	
//	@RequestMapping(value = "/getupdate", method = RequestMethod.GET)
//	public @ResponseBody
//	Update getUpdate(@RequestParam(value="id", required=true, defaultValue="000") String id) {
//		Update result=null;
//		logger.info("on /getupdate id: " + id);
//		if(isValid(id)) {
//			Semaphore sem = new Semaphore(0);
//			if(!semaphoreMap.containsKey(id)) {
//				semaphoreMap.put(id, sem);
//				logger.info("created sem for id: " + id);
//			}else {
//				sem = semaphoreMap.get(id);
//				try {
//					if(!sem.tryAcquire(DEF_TIMEOUT_MS, TimeUnit.MILLISECONDS)) {
//						// timeout
//						logger.info("a timeout "+DEF_TIMEOUT_MS+"ms occured on id: "+id);
//					}
//					result=updateMap.remove(id);
//				}catch(InterruptedException ie) {
//					logger.log(Level.SEVERE, "CLIENT ERROR: " + ie.getMessage(), ie);
//				}
//			}
//		}else {
//			logger.warning("id '"+id+" is not valid.");
//		}
//		logger.info("return "+result);
//		return result;
//	}

//	@RequestMapping(value = "/putupdate", method = RequestMethod.PUT)
//	@ResponseStatus(HttpStatus.NO_CONTENT)
//	public void putUpdate(@RequestBody Update anUpdate) {
//		
//		logger.info("on /putupdate : " + anUpdate);
//		if(anUpdate!=null) {
//			updateMap.put(anUpdate.getId(), anUpdate);
//			Semaphore sem = new Semaphore(0);
//			if(semaphoreMap.containsKey(anUpdate.getId())) {
//				sem = semaphoreMap.get(anUpdate.getId());
//			}else {
//				semaphoreMap.put(anUpdate.getId(), sem);
//				logger.info("created sem for id: " + anUpdate.getId());
//			}
//			sem.release(sem.getQueueLength());
//		}
//		
//	}

//	@ExceptionHandler(IllegalArgumentException.class)
//	@ResponseStatus(HttpStatus.BAD_REQUEST)
//	@ResponseBody
	public String handleClientErrors(Exception ex) {
		// logger.info("request body : "+body);
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

	private boolean isValid(String id) {
		return "0".equals(id);
	}
}