package test;

import java.util.ArrayList;
import java.util.LinkedList;

import Logic.*;
import ILP.*;

public class tmptest {


	public static void main(String[] args) {
		Document doc = new Document("data/questions/questions.pred", 
				"data/questions/test/query_v2.dep.bak", false);
		
		for (int i = 0; i < doc.length(); i++) {
			System.out.println(doc.getSent(i).toString());
		}
		testEvaluation(doc);
//		testPathFind(doc);
	}
	
	public static void testEvaluation(Document doc) {
		
        Formula f = new Formula("sem(X_1_var,X_2_var):-att(X_2_var,X_3_var);de(X_3_var,X_1_var).");
        LogicProgram p = new LogicProgram();
        p.addRule(f);
        f = new Formula("sem(X_1_var,X_2_var):-att(X_1_var,X_2_var);\\=(X_2_var,'的_4_u').");
        p.addRule(f);
        Eval eval = new Eval(p, doc);
        ArrayList<LinkedList<myTerm>> sems = eval.evalAll();
        for (LinkedList<myTerm> l : sems) {
        	if (l == null) 
        		continue;
        	else {
        		for (myTerm t : l) {
        			System.out.println(t.toString());
        		}
        		System.out.println("==============");
        	}
        }
	}
	
	public static void testPathFind(Document doc) {	    
		Sentence[] sentences = doc.getSentences();
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
        	HyperPathFind pf = new HyperPathFind(graph, graph.getVertex(0), graph.getVertex(graph.getVertexLen() - 1));
        	LinkedList<HyperEdge> visitedEdges = new LinkedList<HyperEdge>();
        	System.out.format("num of paths: %d\n", pf.Search(visitedEdges).size());
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
}