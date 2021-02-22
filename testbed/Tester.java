package testbed;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.text.DecimalFormat;
import java.util.function.Supplier;

import dll.IUDoubleLinkedList;
import dll.Tests;


/**
 * A general class to test all methods of other classes over given domains
 * and compare the results to a given set of results.
 * 
 * @author michael
 *
 */
public class Tester {
	
	// the type to be tested
	Class<?> clazz;
	// whether or not passes should be included in returned Strings
	private boolean showPasses = true;
	// the number of passed tests
	private int passes = 0;
	// the number of failed tests
	private int failures = 0;
	// the number of total tests
	private int total = 0;
	
	
	/**
	 * Create a new Tester object to test the given class
	 * 
	 * @param clazz       the type to be tested
	 * @param showPasses  whether or not passed tests should be included in returned Strings
	 */
	public Tester(Class<?> clazz, boolean showPasses) {
		this.clazz = clazz;
		this.showPasses = showPasses;
	}
	
	
	/**
	 * tests all given TestCases and returns the String representation of their results
	 * 
	 * @param tested  the testcases to be tested
	 * @return        the String representation of the results of these TestCases
	 */
	public String testAll(TestCase[] tested) {
		StringBuilder ret = new StringBuilder();
		
		int i = 0;
		for (TestCase t : tested) {
			ret.append(test(t));
			i++;
			System.out.print(">" + new String(new char[i / 7]).replace("\0", "~") + "<\r");
		}
		
		// add a final report
		DecimalFormat df = new DecimalFormat("0.00");
		ret.append("\n Total: " + total 
				 + "\nPassed: " + passes 
				 + " (" + df.format((double) passes / (double) total * 100.0)
                 + "%)\nFailed: " + failures + "\n");
				
		return ret.toString();
	}
	
	
	/**
	 * Tests only one of the given testcases at the index c
	 * 
	 * @param tested  The set of TestCases
	 * @param c       The index of the case to be tested
	 * @return        the String representation of the results of these TestCases
	 */
	public String testAll(TestCase[] tested, int c) {
		StringBuilder b = new StringBuilder();
		
		b.append(test(tested[c]));
		b.append("\n Total: " + total 
				+ "\nPassed: " + passes 
				+ " (" + new DecimalFormat("0.00").format((double) passes / (double) total * 100.0)
				+ "%)\nFailed: " + failures + "\n");
		
		return b.toString();
	}
	
	
	/**
	 * test the given TestCase
	 * 
	 * @param tested  The TestCase to be tested
	 * @return        The String representation of the results of the given TestCase
	 */
	public String test(TestCase tested) {
		StringBuilder ret = new StringBuilder();
		
		String description = tested.getDescription();
		Supplier<?> op = tested.getOp();
		DomainSet params = tested.getParams();
		ResultSet expectedResults = tested.getExpected();
		
		// get the results of the TestCase
		ResultSet achievedResults = getAllResults(op, params);
		
		// for every expected method
		for (String methodName : expectedResults.getKeySet()) {
			Result[] r = achievedResults.getResults(methodName);
			Result[] e = expectedResults.getResults(methodName);
			
			// for each achieved result and expected result in the method
			for (int i = 0; i < e.length; i++) {
				
				Result achieved = r[i];
				Result expected = e[i];
				
				// if the results are equal, format and append a pass (if passes are being shown)
				if (achieved.equals(expected)) {
					if (showPasses) {
						ret.append(description + " | PASS: " + r[i] + "\n");
					}
					passes++;
				}
				// if the results are not equal, format and append a fail
				else {
					Object achObject = achieved.getResult();
					
					ret.append(description + " | **** FAIL:     " + r[i] + "\n");
					ret.append(description + " |      Expected: " + e[i] + "\n");
					
					// print the stack trace
					if (achObject instanceof Exception && !(achObject instanceof InfiniteLoopException)) {
						// this is really the only way to get a stacktrace as a String /ugly
						StringWriter sWriter = new StringWriter();
						PrintWriter pWriter = new PrintWriter(sWriter);
						((Throwable) achObject).printStackTrace(pWriter);
						String stackTrace = sWriter.toString();
						
						// Cut off irrelevant parts of the call stack
						if (stackTrace.contains("at sun.")) {
							stackTrace = stackTrace.substring(0, stackTrace.indexOf("at sun."));
						}
						if (stackTrace.contains("at testbed.Tester")) {
							stackTrace = stackTrace.substring(0, stackTrace.indexOf("at testbed.Tester"));
						}
						if (stackTrace.contains("at testbed.Executor")) {
							stackTrace = stackTrace.substring(0, stackTrace.indexOf("at testbed.Executor"));
						}
						
						ret.append("   " + stackTrace + "\n");
					}
					failures++;
				}
				total++;
			}
		}
		return ret.toString();
	}
	
	
	/**
	 * Get the results of calling all methods of the Object given by op with the given params
	 * 
	 * @param op      the Supplier giving the Object to be tested
	 * @param params  the params with which to test the Object given by op
	 * @return        the ResultSet representing the results achieved
	 */
	public ResultSet getAllResults(Supplier<?> op, DomainSet params) {
		ResultSet results = new ResultSet();
		
		Method[] methods = this.clazz.getDeclaredMethods();
		for (Method m : methods) {
			// If the method is public and is not implicitly generated by the compiler
			if (Modifier.isPublic(m.getModifiers()) && !m.isSynthetic()) {
				String funcName = getMethodName(m);
								
				results.addResult(funcName, getResults(op, m, params.getDomain(funcName)));
			}
		}
		return results;
	}
	
	
	/**
	 * get the results of invoking the given method with the given object over the given parameter domains
	 * 
	 * @param obj     the Supplier which provides the Object that will call the method
	 * @param method  the method to call
	 * @param params  the set of domains of the parameters of the methods
	 * @return        an array of the results achieved by the method
	 */
	private Result[] getResults(Supplier<?> obj, Method method, Object[]... params) {
		Result[] results;
		
		// if there are params to use, use them
		if (params != null && params[0] != null) {
			results = new Result[numCombos(params)];
			
			// The params passed to this function are the domains of each parameter.
			// It is intended that all possible combinations of these domains are tested.
			
			// get all combinations of the parameter sets
			Object[][] paramCombos = allCombinations(params);
			
			int i = 0;
			for (Object[] p : paramCombos) {
				results[i] = getResult(obj, method, p);
				i++;
			}
			
		}
		// else call the function with no parameters
		else {
			results = new Result[1];
			results[0] = getResult(obj, method, (Object[]) null);
		}
				
		return results;
	}
	
	
	
	/**
	 * Get the Result of invoking the given method on the given Object with the given parameters
	 * 
	 * @param obj     The supplier to give the Object to be tested
	 * @param method  The method to be called
	 * @param params  The parameters with which to call the method
	 * @return        The result achieved
	 */
	@SuppressWarnings("deprecation")
	private Result getResult(Supplier<?> obj, Method method, Object... params) {
		if (method == null) {
			throw new IllegalArgumentException("method can't be null");
		}
		if (method.getParameterCount() != 0 && params == null) {
			throw new IllegalArgumentException(method.getName() + " was called with the wrong number of parameters");
		}
		if (params != null && (method.getParameterCount() != params.length)) {
			throw new IllegalArgumentException(method.getName() + " was called with the wrong number of parameters");
		}
				
		// Create a worker thread to test the object
		Executor executor = new Executor(obj, method, params);
		executor.start();
		
		// If the object isn't done in a few ms then we've run into an infinite loop. (check every ~ms for speed)
		int i = 0;
		while (!executor.isFinished() && i < 10) {
			try {
				Thread.sleep(1);
				i++;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		if (executor.isFinished()) {
			return executor.result();
		}
		else {
			// kill the infinite loop. However deprecated, this is the only support in Java
			// to kill an infinite loop in another thread. The thread isn't using any 
			// meaningful resources so it should be fine.
			executor.stop();
			Result r = new Result(method, obj, params, new InfiniteLoopException());
			return r;
		}
	}
	
	/**
	 * Helper method to get the name of the method qualified with its parameter types
	 * (to support overloading)
	 * 
	 * <p>
	 * 
	 * method names are formatted according to '$name($type1, $type2, ...)
	 * example:
	 *     add(int, int)
	 * Generic arguments are Objects under the hood:
	 *     add(T element) becomes add(Object)
	 *     
	 * This was a horrible design choice. Truly just the worst.
	 * 
	 * @param f  the method whose name is to be gotten
	 * @return   the formatted name of the method
	 */
	static String getMethodName(Method f) {
		StringBuilder ret = new StringBuilder();
		
		ret.append(f.getName());
		if (f.getParameterCount() == 0) {
			ret.append("()");
		}
		else {
			ret.append("(");
			for (Class<?> p : f.getParameterTypes()) {
				ret.append(p.toString().substring(p.toString().lastIndexOf('.') + 1) + ", ");
			}
			ret = new StringBuilder(ret.substring(0, ret.length() - 2));
			ret.append(")");
		}
		
		return ret.toString();
	}
	
	
	/**
	 * helper method to get all combinations of the set of domains
	 * 
	 * @param doms  the set of domains
	 * @return      all combinations of the given domains
	 */
	private static Object[][] allCombinations(Object[]... doms) {
		if (doms == null) { return null; }
		Object[][] firstCombos = new Object[doms[0].length][1];
		for (int i = 0; i < doms[0].length; i++) {
			firstCombos[i][0] = doms[0][i];
		}
		return _allCombinations(firstCombos, tail(doms));
	}
	
	/**
	 * helper for allCombinations that does the recursive work
	 * 
	 * @param cur        the current set of combinations
	 * @param remaining  the remaining domains to be accounted for
	 * @return           a new set of combinations that accounts for 
	 *                   the first domain in the remaining domains
	 */
	private static Object[][] _allCombinations(Object[][] cur, Object[][] remaining) {
		// base case
		if (remaining.length <= 0) { 
			return cur; 
		}
		else {
			// the next domain to be processed
			Object[] next = remaining[0];
			// the new set of combinations to return
			Object[][] newCur = new Object[cur.length * next.length][cur[0].length + 1];
			
			// for every combination in the current set and every element in the next domain to be processed
			int i = 0;
			for (Object[] l : cur) {
				for (Object o : next) {
					// add to the new set of combinations the concatenation of the combination and the new element
					newCur[i] = concat(l, new Object[]{o});
					i++;
				}
			}
			// call the method again with the new set of combinations and the rest of the domains to be processed
			return _allCombinations(newCur, tail(remaining));
		}
	}
	
	
	/**
	 * Helper for recursion. Gets the tail (index 1 - size - 1) of the array.
	 * 
	 * @param o  the array to get the tail from
	 * @return   the tail of the array
	 */
	private static Object[][] tail(Object[][] o) {
		Object[][] ret = new Object[o.length - 1][];
		for (int i = 0; i < ret.length; i++) {
			ret[i] = o[i + 1];
		}
		return ret;
	}
	
	
	/**
	 * Helper for recursion. Concatenates the two arrays
	 * 
	 * @param o1  The first array to be concatenated
	 * @param o2  The second array to be concatenated
	 * @return    The concatenation of both arrays
	 */
	private static Object[] concat(Object[] o1, Object[] o2) {
		Object[] ret = new Object[o1.length + o2.length];
		for (int i = 0; i < ret.length; i++) {
			if (i < o1.length) {
				ret[i] = o1[i];
			}
			else {
				ret[i] = o2[i - o1.length];
			}
		}
		return ret;
	}
	
	
	/**
	 * Get the total number of combinations of the elements of the given arrays
	 * 
	 * @param arrays  the set of arrays to combine
	 * @return        the number of combinations of the elements in the arrays
	 */
	private static int numCombos(Object[]... arrays) {
		int total = 1;
		for (Object[] l : arrays) {
			total *= l.length;
		}
		return total;
	}
	
	
	
	
	
	

}
