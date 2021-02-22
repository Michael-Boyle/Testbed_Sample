package testbed;

import java.lang.reflect.Method;
import java.util.function.Supplier;

/**
 * A worker thread to invoke a method and make its result
 * available to the main thread. A black box to execute functions
 * without fear of infinite loops.
 * 
 * <p>
 * 
 * The main thread can poll this thread for completion and, after
 * a reasonable amount of time, determine if it has gone into an
 * infinite loop and use an InfiniteLoopException as a result.
 * 
 * @author michael
 *
 */
public class Executor extends Thread {
	
	// boolean representing whether or not 
	// this Executor is done executing the method
	private volatile boolean finished;
	// the result achieved by the method
	private volatile Result result;
	
	// the Supplier supplying the object to be tested
	private Supplier<?> obj;
	// the method to be invoked
	private Method method;
	// the parameters with which to call the method
	private Object[] params;
	
	
	/**
	 * create a new Executor to invoke the given method with the given 
	 * params on the object produced by the given Supplier
	 * 
	 * @param obj
	 * @param method
	 * @param params
	 */
	public Executor(Supplier<?> obj, Method method, Object... params) {
		this.obj = obj;
		this.method = method;
		this.params = params;
	}
	
	
	/**
	 * begin executing the method
	 * 
	 */
	public void run() {
		Object result = null;		
		Object object = null;
		
		if (params != null && params.length != 0) {
			try {
				object = obj.get();
				result = method.invoke(object, params);
			} catch (Exception e) {
				// check for Exception chaining
				if (e.getCause() == null && object == null) {
					result = e;
				}
				else {
					result = e.getCause();
				}
			}
		} else {
		    try {
				object = obj.get();
				result = method.invoke(object, (Object[]) null);
			} catch (Exception e) {
				// check for Exception chaining
				if (e.getCause() == null && object == null) {
					result = e;
				}
				else {
					result = e.getCause();
				}
			}
		}
		
		Result resToRet = new Result(method, object, params, result);
		this.result = resToRet;
		finished = true;
	}
	
	
	/**
	 * determines whether this Executor is finished executing
	 * 
	 * @return  boolean representing the finish state of this Executor
	 */
	public synchronized boolean isFinished() {
		return finished;
	}
	
	/**
	 * get the Result achieved by the method of this Executor
	 * 
	 * @return  the Result achieved by the method of this Executor
	 */
	public synchronized Result result() {
		return result;
	}
	
	
	
	// I like this thing
}



