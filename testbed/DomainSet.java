package testbed;

import java.util.Arrays;
import java.util.HashMap;

/**
 * represents a set of domains for testing
 * 
 * @author michael
 *
 */
public class DomainSet {

	// the set of domains
	private HashMap<String, Object[][]> domains;
	
	
	/**
	 * create a new DomainSet with the given method/domain pairs (Tuple<String, Object[][]>s)
	 * 
	 * <p>
	 * 
	 * Tuples so that static initialization in resource files looks prettier
	 * (static code blocks make me feel like I'm being watched)
	 * 
	 * @param doms
	 */
	@SafeVarargs
	public DomainSet(Tuple<String, Object[][]>... doms) {
		this.domains = new HashMap<String, Object[][]>();
		for (Tuple<String, Object[][]> d : doms) {
			this.domains.put(d.x, d.y);
		}
	}
	
	/**
	 * Get the domain associated with the given method name
	 * 
	 * @param methodName  the (formatted) name of the method
	 * @return            the domain of the method
	 */
	public Object[][] getDomain(String methodName) {
		return domains.get(methodName);
	}
	
	
	/**
	 * returns a string representation of this DomainSet
	 * 
	 */
	@Override
	public String toString() {
		StringBuilder s = new StringBuilder();
		
		for (String key : domains.keySet()) {
			for (Object[] o : domains.get(key)) {
				s.append(key + "(" + Arrays.deepToString(o) + ")\n");
			}
		}
		
		return s.toString();
	}
	
	
}
