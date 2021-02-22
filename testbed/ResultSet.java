package testbed;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;


/**
 * represents a set of Results to be expected from a TestCase
 * 
 * @author michael
 *
 */
public class ResultSet {

	// the expected results associated with each function
	private HashMap<String, Result[]> results;
	
	/**
	 * create a new ResultSet with the given function name/expected Results
	 * pairs (Tuples)
	 * 
	 * <p>
	 * 
	 * Tuples so that static initialization in resource files looks prettier
	 * (static code blocks cause great distress)
	 * 
	 * @param tuples  the name/Result pairs to be used as a ResultSet
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@SafeVarargs
	public ResultSet(Tuple... tuples) {
		this.results = new HashMap<String, Result[]>();
		for (Tuple<String, Result[]> t : tuples) {
			results.put(t.x, t.y);
		}
	}
	
	/**
	 * add the given method name/Result pair to this ResultSet
	 * 
	 * @param methodName  the Name of the method associated with the
	 *                    expected Results
	 * @param result      the expected Results associated with the 
	 *                    method name
	 */
	public void addResult(String methodName, Result... result) {
		results.put(methodName, result);
	}
	
	/**
	 * get the results associated with the given method name
	 * 
	 * @param methodName  the (formatted) name of the method associated
	 *                    with the results
	 * @return            the Result set associated with the given method
	 */
	public Result[] getResults(String methodName) {
		return results.get(methodName);
	}
	
	/**
	 * get the keyset (list of all method names) 
	 * of the HashMap underlying this ResultSet
	 * 
	 * <p>
	 * 
	 * for iteration purposes.
	 * 
	 * @return  the keyset of this ResultSet
	 */
	public Set<String> getKeySet() {
		return results.keySet();
	}
	
	/**
	 * get a String representation of this ResultSet
	 */
	@Override
	public String toString() {
		StringBuilder s = new StringBuilder();
		if (results.keySet().size() != 0) {
			for (String key : results.keySet()) {
				s.append(key + ": " + Arrays.deepToString(results.get(key)) + "\n");
			}
		}
		else {
			s.append("[]");
		}
		
		return s.toString();
	}
	
	
}
