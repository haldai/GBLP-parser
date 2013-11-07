/**
 * 
 */
package ILP;

import java.util.ArrayList;
import java.util.LinkedList;

import Logic.*;

/**
 * @author daiwz
 *
 */
public class SatisfySamples {

	/**
	 * restore the instances that satisfied by a rule L, departed into Negative and Positive samples
	 */
	
	ArrayList<Formula> formula;
	ArrayList<myTerm> negative = new ArrayList<myTerm>();
	ArrayList<myTerm> positive = new ArrayList<myTerm>();
	
	public SatisfySamples(ArrayList<Formula> f) {
		// TODO Auto-generated constructor stub
		formula = f;
	}
	
	public SatisfySamples(Formula f) {
		ArrayList<Formula> ff = new ArrayList<Formula>();
		ff.add(f);
		formula = ff;
	}

	public void setSatisifySamples(ArrayList<myTerm> label, LinkedList<myTerm> evaled) {
		for (myTerm t : evaled) {
			if (label.contains(t)) {
				positive.add(t);
			}
			else {
				negative.add(t);
			}
		}
	}
	
	public ArrayList<myTerm> getPositive() {
		return positive;
	}
	
	public ArrayList<myTerm> getNegative() {
		return negative;
	}
	
	public void pushPositive(myTerm t) {
		positive.add(t);
	}
	
	public void pushPositive(ArrayList<myTerm> t) {
		positive.addAll(t);
	}
	
	public void pushNegative(myTerm t) {
		negative.add(t);
	}
	
	public void pushNegative(ArrayList<myTerm> t) {
		negative.addAll(t);
	}
}