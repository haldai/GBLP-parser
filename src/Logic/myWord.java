/**
 * 
 */
package Logic;

import jpl.Atom;
/**
 * @author daiwz
 *
 */
public class myWord {

	/**
	 * myWord is a word in input data, e.g. "刘德华_1_nr"，"刘德华"is name,
	 * "1" is its position in sentence, "nr" is its POS tag.
	 * 
	 * Otherwise, the class myWorld also defines variable. When the "num" 
	 * position > 0 it is a constant, or atom; when it is less than 0 it 
	 * defines a variable, and the POS tag is "var", e.g. "X_-1_var, X_-2_var",
	 * -1 or -2 means different variables. In convenience, the "name" field 
	 * should always be "X".
	 * 
	 * If meet word that start with upper case or number, simply add a "c" 
	 * in front of it
	 */
	String name;
	int num;
	String pos;
	
	public myWord(String s) {
		// Generative function of myWord(word_#_pos)
		String[] args = s.split("_");
		name = args[0];
		name = name.replaceAll("？", "question");
		name = name.replaceAll("。", "period");
		name = name.replaceAll("，", "comma");
		name = name.replaceAll("《", "bookLeft");
		name = name.replaceAll("》", "bookRight");
		name = name.replaceAll("（", "parLeft");
		name = name.replaceAll("）", "parRight");
		name = name.replaceAll("”", "quoteRight");
		name = name.replaceAll("“", "quoteLeft");
		name = name.replaceAll("’", "quoteRight");
		name = name.replaceAll("‘", "quoteLeft");
		name = name.replaceAll("、", "backslash");
		name = name.replaceAll("[?]", "question");
		name = name.replaceAll("[.]", "period");
		name = name.replaceAll(",", "comma");
		name = name.replaceAll("<", "lessThan");
		name = name.replaceAll(">", "largerThan");
		name = name.replaceAll("[(]", "parLeft");
		name = name.replaceAll("[)]", "parRight");
		name = name.replaceAll("\"", "quote");
		name = name.replaceAll("\'", "quote");
		name = name.replaceAll("\\\\", "backslash");
		name = name.replaceAll("/", "slash");
		num = Integer.parseInt(args[1]);
		pos = args[2];
	}
	public myWord() {
		name = null;
		num = -1;
		pos = null;
	}
	
	public String getName() {
		return name;
	}
	
	public int getNum() {
		return num;
	}
	
	public String getPos() {
		return pos;
	}
	
	public String toString() {
		String str = String.format("%s_%s_%s", name, num, pos);
		return str;
	}
	
	public String toPrologString() {
		char c = name.charAt(0);
		String new_name = name;
		if (Character.isDigit(c) || (Character.isUpperCase(c)) || Character.isSpace(c)) {
		new_name = "d" + new_name;
		}
		new_name = new_name.replaceAll(" ", "SPACE");
		String str = String.format("%s_%s_%s", new_name, num, pos);
		return str;
	}

	public Atom toAtom() {
		return new Atom(this.toString());
	}
	// to judge if the word is a variable
	public boolean isVar() {
		if ((name.equals("X")) && (pos == "var") && (num < 0)) 
			return true;
		else return false;
	}
	
	public boolean equals(myWord w) {
		if ((this.name.equals(w.name)) && (this.num == w.num) && (this.pos == w.pos))
			return true;
		else
			return false;
	}
}
