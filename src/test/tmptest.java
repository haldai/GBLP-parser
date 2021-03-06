package test;

import java.util.ArrayList;
import java.util.LinkedList;

import Logic.*;
import ILP.*;
import Tree.*;
import utils.*;
import Boosting.*;

public class tmptest {
	static Prolog prolog;
	public static void main(String[] args) {
		Document doc = new Document("data/questions/questions.pred", 
				"data/test_new/test.dep", true);
		
		for (int i = 0; i < doc.length(); i++) {
			System.out.println(doc.getSent(i).toString());
			System.out.println(doc.getLabel(i).toString());
			System.out.println(doc.getSent(i).getFeatures().toString());
		}
		
		prolog = new Prolog();
		
//		testEvaluation(doc, prolog);
//		testPathFind(doc);
//		testRuleTree(doc, prolog);
//		testTuple();
//		System.out.println(Math.log(0.0000000000000000000001));
//		testClone();
		testAdaBoost(prolog, doc);
		

	}
	
	public static void testAdaBoost(Prolog prolog, Document doc) {
		AdaBoost boost = new AdaBoost(prolog);
		AdaBoostOutput boost_out = boost.train(doc);
		System.out.println("Finished training, testing output:");
		BoostingEval boost_eval = new BoostingEval(prolog);
		boost_eval.addPredicates(doc.getPredList());
		boost_eval.evalAndPrintAll(boost_out.getWeakRules(), boost_out.getWeights(), doc.getSentences());
	}
	
	public static void testClone() {
		System.out.println("testing clone:");
		myWord wd0 = new myWord("answer_42_yes");
		myWord wd1 = wd0.clone();
		wd1.setNumZero();
		wd1.setPos("wrong");
		System.out.println(wd0.toString() + ":::" + wd1.toString());
		Predicate p0 = new Predicate("dead/2");
		Predicate p1 = p0.clone();
		p1.setName("phuck");
		System.out.println(p0.toString() + ":::" + p1.toString());
		myTerm t0 = new myTerm("dead(answer_42_yes,answer_0_yes)");
		myTerm t1 = t0.clone();
		t1.setPred(p1);
		t1.setNegative();
		t1.setArg(0, wd1);
		System.out.println(t0.toPrologString() + ":::" + t1.toPrologString());
		Formula f0 = new Formula("sem(X_1,X_2):-att(X_2,X_3);de(X_3,X_1);postag(X_1,v_POS).");
		Formula f1 = f0.clone();
		f1.popBody();
		f1.pushBody(new myTerm("not(postag(X_1,u_POS))"));
		System.out.println(f0.toPrologString() + ":::" + f1.toPrologString());
		ArrayList<myTerm> tl0 = new ArrayList<myTerm>();
		tl0.add(t0);
		tl0.add(t1);
		System.out.println(tl0);
		ArrayList<myTerm> tl1 = (ArrayList<myTerm>) tl0.clone();
		t0.setNegative();
		tl1.get(1).setPositive();
		System.out.println(tl0.toString() + ":::" + tl1.toString());
	}
	
	public static void testTuple() {
		System.out.println("Test Tuple!");
		Tuple<Integer, Integer> tup1 = new Tuple<Integer, Integer>(0, 0);
		Tuple<Integer, Integer> tup2 = new Tuple<Integer, Integer>(0, 2);
		System.out.println(tup1.equals(tup2));
	}
	
	public static void testEvaluation(Document doc, Prolog prolog) {
		System.out.println("Test Evaluation!");
		Formula f = new Formula("sem(X_1,X_2):-att(X_2,X_3);de(X_3,X_1);postag(X_1,v_POS)."); 
		LogicProgram p = new LogicProgram();
//		p.addRule(f);
		f = new Formula("sem(X_1,X_2):- sbv(X_2,X_1); \\+(==(X_2,X_1));==(X_1,谁_r); \\+(==(X_2,是_v)).");
//		f = new Formula("sem(X_1,X_2):- sbv(X_2,X_1);\\==(X_2,X_1);\\==(X_1,谁_r);==(X_1,高_a).");
//		f = new Formula("sem(X_1,X_2):- sbv(X_2,X_1);\\==(X_2,X_1);\\==(X_1,谁_r);\\==(X_1,高_a).");
//		f = new Formula("sem(X_1_var,X_2_var):- att(X_2_var,X_1_var); \\+(postag(X_1_var,u_POS)).");
//		f = new Formula("sem(X_2_var,X_1_var):- not(postag(X_2_var,u_POS));att(X_1_var,X_2_var).");
//		f = new Formula("sem(X_2_var,X_1_var):- not(==(X_2_var,的_0_u));att(X_1_var,X_2_var).");
//		f = new Formula("sem(X_2_var,X_1_var):- att(X_1_var,X_2_var);\\==(X_2_var,的_0_u)."); // do not use \=/2(unification)
		p.addRule(f);
		Eval eval = new Eval(prolog, p, doc.getPredList());
		ArrayList<LinkedList<myTerm>> sems = eval.evalAll(doc);
		int cnt = 0;
		for (LinkedList<myTerm> l : sems) {
			if (l == null) 
				continue;
			else {
				for (myTerm t : l) {
					cnt++;
					System.out.println(t.toPrologString());
				}
				System.out.println("==============");
			}
		}
		System.out.println("num of semantics: " + cnt);
		try {
			eval.unEval();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
	
	private static ArrayList<LinkedList<myTerm>> findPath(myTerm label, Sentence sent) {
		HyperGraph graph = new HyperGraph();
		myTerm[] terms = sent.getTerms();
		myWord[] words = sent.getWords();
		for (myWord word : words) {
			graph.addHyperVertex(word);
		}
		
		for (myTerm term : terms) {
			graph.addHyperEdge(term);
		}
		
		HyperVertex start = new HyperVertex(label.getArg(0));
		HyperVertex end = new HyperVertex(label.getArg(1));
		HyperPathFind pf = new HyperPathFind(graph, start, end);
		LinkedList<HyperEdge> visitedEdges = new LinkedList<HyperEdge>();
		pf.Search(visitedEdges);
		
		return pf.getPaths();
	}
	
	public static void testPathFind(Document doc) {
		System.out.println("Test PathFind!");
		Sentence[] sentences = doc.getSentences();
		int cnt = 0;
       for (Sentence sent : sentences) {
    	   HyperGraph graph = new HyperGraph();
    	   myTerm[] terms = sent.getTerms();
    	   myWord[] words = sent.getWords();
    	   for (myWord word : words) {
    		   graph.addHyperVertex(word);
    	   }
    	   
    	   for (myTerm term : terms) {
    		   graph.addHyperEdge(term);
    	   }
    	   
    	   for (myTerm label : doc.getLabel(cnt)) {
    		   HyperVertex start = new HyperVertex(label.getArg(0));
    		   HyperVertex end = new HyperVertex(label.getArg(1));
    		   // following 2 lines testing searching paths with variables
//    		   HyperVertex start = new HyperVertex(new myWord("刘德华_nr"));
//    		   HyperVertex end = new HyperVertex(new myWord("爸爸_n"));
    		   HyperPathFind pf = new HyperPathFind(graph, start, end);
    		   LinkedList<HyperEdge> visitedEdges = new LinkedList<HyperEdge>();
    		   pf.Search(visitedEdges);
//           	 	System.out.format("num of paths: %d\n", pf.Search(visitedEdges).size());
        		// test substitution
    		   for (LinkedList<myTerm> path : pf.getPaths()) {
    			   Substitute sub = new Substitute(new ArrayList<myTerm>(path));
    			   for (myWord w : sub.getWordList()) {
    				   System.out.print(String.format("%s ", w.toPrologString()));
    			   }
    			   System.out.println();
    			   for (myWord w : sub.getVarList()) {
    				   System.out.print(String.format("%s ", w.toPrologString()));
    			   }
    			   System.out.println();
    			   for (myTerm t : sub.getOriginTerms()) {
    				   System.out.print(String.format("%s ", t.toPrologString()));
    			   }
    			   System.out.println();
    			   for (myTerm t : sub.getSubTerms()) {
    				   System.out.print(String.format("%s ", t.toPrologString()));
    			   }
    			   System.out.println();
    		   }
    	   }
    	   cnt++;
          	
//        	System.out.format("edge len %d, vertex len %d\n", graph.getEdgeLen(), graph.getVertexLen());
//        	if (graph.getEdgeLen() - graph.getVertexLen() != -1)
//        		System.out.println("ERROR!!");
//        	for (HyperEdge edge : graph.getEdges()) {
//        		System.out.println(edge.toMyTerm().toString());
//        	}
//        	for (HyperVertex vertex : graph.getVertices()) {
//        		System.out.println(vertex.toMyWord().toString());
//        	}
        	graph = null;
        }
	}
	
	public static void testRuleTree(Document doc, Prolog prolog) {
		System.out.println("Test RuleTree!");
		// find all paths;
		for (int i = 0; i < doc.length(); i++) {
//			i = 1;
			ArrayList<myTerm> labels = doc.getLabel(i);
			Sentence sent = doc.getSent(i);
			for (myTerm label : labels) {
				ArrayList<LinkedList<myTerm>> paths = findPath(label, sent);
				for (LinkedList<myTerm> path : paths) {
					RuleTree tree = new RuleTree(prolog, doc.getPredList());
					tree.buildTree(new Data(doc), label, path);
				}
			}
//			System.exit(0);
		}
	}
}
