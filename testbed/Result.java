package testbed;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Spliterator;
import java.util.function.Consumer;

/**
 * Represents the result of a method called with the given parameters. Also
 * represents an Object presumably resulting from a function invocation 
 * without the context of that invocation.
 * 
 * <p>
 * 
 * The double meaning of this type wasn't really intended; it just happened
 * organically. This class should have been split into two different types.
 * 
 * @author michael
 *
 */
public class Result {
	
	// The Object that resulted from calling the method
	private Object result;
	// The method that was called
	private Method method;
	// The Object whose method was called
	private Object object;
	// The parameters with which the method was called
	private Object[] params;
	
	/**
	 * constructs a new result
	 * 
	 * @param method  The method that was called
	 * @param object  The object whose method was called
	 * @param params  The parameters with which the method was called
	 * @param result  The result of the method call
	 */
	public Result(Method method, Object object, Object[] params, Object result) {
		this.result = result;
		this.method = method;
		this.object = object;
		this.params = params;
	}
	
	/**
	 * constructs a new result without context
	 * 
	 * @param result  The result of some method that was called
	 */
	public Result(Object result) {
		this.result = result;
	}
	
	
	/**
	 * get the String representation of this Result
	 * 
	 */
	@Override
	public String toString() {
		String ret = "";
		
		if (method == null && params == null && object == null) {
			if (result == null) {
				return "null";
			}
			else {
				return result.toString();
			}
		}
		
		ret += method.getName() + "(";
		
		String paramString = "";
		
		if (params != null) {
			for (Object p : params) {
				if (p instanceof Consumer) {
					paramString += "x -> ()  ";
				}
				else {
					paramString += p + ", ";
				}
			}
			paramString = paramString.substring(0, paramString.length() - 2);
		}
		
		ret += paramString + ") -> ";
		
		if (result instanceof Spliterator) {
			ret += "Spliterator";
		}
		else if (result instanceof ListIterator) {
			ret += "ListIterator";
		}
		else if (result instanceof Iterator) {
			ret += "Iterator";
		}
		else {
			ret += result;
		}
		
		return ret;
	}
	
	
	/**
	 * Determine if this Result is equal to another result
	 * 
	 * @return  A boolean representing whether or not this Result matches
	 *          the other specified Result
	 * 
	 * <p>
	 * 
	 * I intended for this to be independent of the implementation of my 
	 * testbed. Unfortunately for me, it is essentially impossible to 
	 * compare Exceptions and Iterators by value. Any new result types 
	 * expected that can't be compared by value have to be put here.
	 * 
	 */
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Result)) {
			return false;
		}
		Result other = (Result) o;
		
		Object x = this.getResult();
		Object y = other.getResult();		
		
		if (x instanceof Exception && y instanceof Exception) {
			return x.getClass().equals(y.getClass());
		}
		else if (x instanceof ListIterator && y instanceof ListIterator) {
			return true;
		}
		else if (x instanceof Iterator && y instanceof Iterator) {
			return true;
		}
		else if (x instanceof Spliterator && y instanceof Spliterator) {
			return true;
		}
		else if (x == null && y == null) {
			return true;
		}
		else if (x == null && y != null) {
			return false;
		}
		else if (x != null && y == null) {
			return false;
		}
		else {						
			return x.equals(y);
		}
	}
	
	
	
	/**
	 * get the object returned by the method invocation of this Result
	 * 
	 * @return  the result of the method invocation
	 */
	public Object getResult() {
		return result;
	}
	/**
	 * get the method invoked to create this Result
	 * 
	 * @return  the method invoked to create this Result
	 */
	public Method getMethod() {
		return method;
	}
	/**
	 * get the Object that was invoked to call the method that
	 * caused this Result
	 * 
	 * @return  the Object that caused this Result
	 */
	public Object getObject() {
		return object;
	}
	/**
	 * get the parameters that were used by the method that
	 * caused this Result
	 * 
	 * @return  the set of parameters used by the method that
	 *          caused this result
	 */
	public Object[] getParams() {
		return params;
	}
	
	
	
		
	
	
}

















