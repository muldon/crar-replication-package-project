package com.ufu.crar.tfidf;
//package ir;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Set;

/**
 * This class represents one document.
 * It will keep track of the term frequencies.
 * @author swapneel
 *
 */
public class Document implements Comparable<Document>{
	
	/**
	 * A hashmap for term frequencies.
	 * Maps a term to the number of times this terms appears in this document. 
	 */
	private HashMap<String, Integer> termFrequency;
	private Integer id;
	
	
	/**
	 * The constructor.
	 * It takes in the name of a file to read.
	 * It will read the file and pre-process it.
	 * @param filename the name of the file
	 */
	/*public Document(String filename) {
		this.filename = filename;
		termFrequency = new HashMap<String, Integer>();
		
		readFileAndPreProcess();
	}*/
	
	public Document(String content,Integer id) {
		this.id= id;
		termFrequency = new HashMap<String, Integer>();
		
		String[] splited = content.split("\\s+");
		
		for(int i=0; i<splited.length; i++) {
			
			String filteredWord = splited[i];
			
			if (termFrequency.containsKey(filteredWord)) {
				int oldCount = termFrequency.get(filteredWord);
				termFrequency.put(filteredWord, ++oldCount);
			} else {
				termFrequency.put(filteredWord, 1);
			}
		}
	}
	
	/**
	 * This method will read in the file and do some pre-processing.
	 * The following things are done in pre-processing:
	 * Every word is converted to lower case.
	 * Every character that is not a letter or a digit is removed.
	 * We don't do any stemming.
	 * Once the pre-processing is done, we create and update the 
	 */
	/*private void readFileAndPreProcess() {
		try {
			Scanner in = new Scanner(new File(filename));
			System.out.println("Reading file: " + filename + " and preprocessing");
			
			while (in.hasNext()) {
				String nextWord = in.next();
				
				String filteredWord = nextWord.replaceAll("[^A-Za-z0-9]", "").toLowerCase();
				
				if (!(filteredWord.equalsIgnoreCase(""))) {
					if (termFrequency.containsKey(filteredWord)) {
						int oldCount = termFrequency.get(filteredWord);
						termFrequency.put(filteredWord, ++oldCount);
					} else {
						termFrequency.put(filteredWord, 1);
					}
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}*/
	
	/**
	 * This method will return the term frequency for a given word.
	 * If this document doesn't contain the word, it will return 0
	 * @param word The word to look for
	 * @return the term frequency for this word in this document
	 */
	public double getTermFrequency(String word) {
		if (termFrequency.containsKey(word)) {
			return termFrequency.get(word);
		} else {
			return 0;
		}
	}
	
	/**
	 * This method will return a set of all the terms which occur in this document.
	 * @return a set of all terms in this document
	 */
	public Set<String> getTermList() {
		return termFrequency.keySet();
	}



	@Override
	public int compareTo(Document o) {
		return this.id.compareTo(o.getId());
		
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Document other = (Document) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	
	
	
}