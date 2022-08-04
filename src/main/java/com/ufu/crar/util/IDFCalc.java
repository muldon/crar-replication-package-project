package com.ufu.crar.util;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.FSDirectory;

public class IDFCalc {

	String indexFolder;
	Set<String> mykeys;
	public static final String FIELD_CONTENTS = "contents";

	
	public IDFCalc(String indexFolder, Set<String> keys) {
		this.indexFolder = indexFolder;
		this.mykeys = keys;
	}

	protected double getIDF(int N, int DF) {
		// getting the IDF
		if (DF == 0)
			return 0;
		return Math.log(1 + (double) N / DF);
	}

	public HashMap<String, Double> calculateIDFOnly() {
		IndexReader reader = null;
		HashMap<String, Double> inverseDFMap = new HashMap<>();
		try {
			reader = DirectoryReader.open(FSDirectory.open(new File(indexFolder).toPath()));
			// String targetTerm = "breakpoint";

			// now go for the IDF
			int N = reader.numDocs();
			double maxIDF = 0;
			int keyNum = 0;
			System.out.println(mykeys.size()+" keys to get idf...");
			for (String term : mykeys) {
				Term t = new Term(FIELD_CONTENTS, term);
				int docFreq = reader.docFreq(t);
				double idf = getIDF(N, docFreq);
				if (!inverseDFMap.containsKey(term)) {
					inverseDFMap.put(term, idf);
					if (idf > maxIDF) {
						maxIDF = idf;
					}
				}
				if(keyNum%100000==0) {
					System.out.println(keyNum+" keys processed");
				}
				keyNum++;
			}
			// now normalize the IDF scores
			System.out.println(" now normalizing keys... ");
			for (String term : this.mykeys) {
				double idf = inverseDFMap.get(term);
				idf = idf / maxIDF;
				inverseDFMap.put(term, idf);
			}
			System.out.println("keys normalized... ");

		} catch (Exception exc) {
			System.out.println();
		}
		return inverseDFMap;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Set<String> keys = new HashSet<>();
		keys.add("string");
		keys.add("list");
		keys.add("exception");
		keys.add("transport");
		keys.add("rareee");
		//String indexFolder = NlpStaticData.EXP_HOME + "/dataset/qeck-index";
		String indexFolder = "/home/rodrigo/tmp/sodirindex";
		System.out.println(new IDFCalc(indexFolder, keys).calculateIDFOnly());
		
		
	}
}

