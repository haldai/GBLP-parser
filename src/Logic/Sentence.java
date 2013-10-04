/**
 * 
 */
package Logic;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.ArrayList;
import java.util.List;

/**
 * @author daiwz
 *
 */
public class Sentence {

	/**
	 * Sentence is a bunch of dependencies, i.e., myTerms, and 
	 * its word list
	 */
	int length;
	myTerm[] termList;
	myWord[] wordList;
	
	public Sentence(String deps) {
		// TODO read sentence from string
		Pattern p = Pattern.compile("\\(.*?\\)");
		String[] dep = deps.substring(0, deps.length() - 1).split(";");
		// use dynamic array to initialize the termlist and wordlist
		List<myWord> wd = new ArrayList<myWord>();
		List<myTerm> tm = new ArrayList<myTerm>();
		// processing each term
		List<Integer> l = new ArrayList<Integer>();
		l.add(-1);
		for (int i = 0; i < dep.length; i++) {
//			System.out.print(i);
//			System.out.println(dep[i]);
			List<myWord> arg_words = new ArrayList<myWord>();
			Matcher m = p.matcher(dep[i]);
			boolean found = m.find(); // if matched (content)
			if (found) {
				String args = m.group();
				args = args.substring(1, args.length() - 1);
				System.out.println(dep[i]);
				String pred = dep[i].split("\\(")[0];

				String[] words = args.split(",");
				for (int j = 0; j < words.length; j++) {
					myWord tmp = new myWord(words[j]);
					arg_words.add(tmp);
					if (!l.contains(tmp.num)) {
						wd.add(tmp);
						l.add(tmp.num);
					}
				}
				tm.add(new myTerm(dep[i]));
				arg_words = null;
			} else {
				System.out.println("Parsing dependency error in Sentence:" + deps);
				System.exit(1);
			}
		}
		myTerm[] buff_terms = new myTerm[tm.size()];
		myWord[] buff_words = new myWord[wd.size()];
		for (int i = 0; i < tm.size(); i++ ) {
			buff_terms[i] = tm.get(i);
		}
		
		for (int i = 0; i < wd.size(); i++ ) {
			buff_words[i] = wd.get(i);
		}
		tm = null;
		wd = null;
		termList = buff_terms;
		wordList = buff_words;
	}
}