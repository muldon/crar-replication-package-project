package com.ufu.crar.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import org.springframework.stereotype.Component;

@Component
public class TextNormalizer {

	String content;
/*
	public TextNormalizer(String content) {
		this.content = content;
	}
	*/
	

	public void setContent(String content) {
		this.content = content;
	}



	public TextNormalizer() {
		// default constructor
	}

	public String normalizeSimple() {
		String[] words = this.content.split("\\p{Punct}+|\\d+|\\s+");
		ArrayList<String> wordList = new ArrayList<>(Arrays.asList(words));
		return MiscUtility.list2Str(wordList);
	}

	public Set<String> normalizeSimpleCodeDiscardSmall() {
		Set<String> validTokens = new LinkedHashSet<>();
		try {
		content = CrarUtils.translateHTMLSimbols(content);	
			
		//remove public static void main(String[] args) {
		content = content.replaceAll("public static void main\\(String\\[\\] args\\)", " ");
		
		//remove stacktraces
		content = content.replaceAll("(?m)^.*?Exception.*(?:\\R+^\\s*at .*)+", " ");
		
		//print code
		content = content.replaceAll("System.out.println", "");
				
		//remove comments
		content= content.replaceAll("\\/\\*;","");
		content= content.replaceAll("\\/\\*\"","");
		content= content.replaceAll("\\/\\*\\.","");
		content= content.replaceAll("\\/\\*<","");
		content= content.replaceAll("\\/\\*\\*\"","");
		content= content.replaceAll("\\/\\*\\*\'","");
		
		/*content= content.replaceAll("\\/\\*;","");
		content= content.replaceAll("\\/\\*;","");
		content= content.replaceAll("\\/\\*;","");
		content= content.replaceAll("\\/\\*;","");*/
		
		
		content = content.replaceAll("(?:/\\*(?:[^*]|(?:\\*+[^*/]))*\\*+/)|(?://.*)","");
				
		//remove exceptions
		content = content.replaceAll("([\\w\\.]*Exception)","");
		
		//remove System.out.println 
		content = content.replaceAll("System.out.print","");
		
		//remove <E> types
		//content = content.replaceAll("\\<([^)]+)\\>","");
		
		//remove "anything here" 
		content = content.replaceAll("([\"'])((?:(?=(\\\\?))\\3.)*?)\\1","");
		
		//remove annotations
		content = content.replaceAll("@(\\w+)", "");
		
		
		String[] words = this.content.split("\\p{Punct}+|\\s+");
		ArrayList<String> wordList = new ArrayList<>(Arrays.asList(words));
		// decomposing the camel cases
		wordList = extractCodeItem(wordList);
		//codeItems = decomposeCamelCase(codeItems);
		//wordList.addAll(codeItems);
		// discarding non-important tokens
		wordList = discardSmallTokens(wordList);
		String modified = MiscUtility.list2Str(wordList);
		// discard stop words
		StopWordManager stopManager = new StopWordManager(true);
		//Set<String> validTokens = stopManager.getRefinedSentence(modified);
		
		ArrayList<String> tokens = MiscUtility.str2List(modified);
		for (String token : tokens) {
			if (!stopManager.isAStopWord(token)) {
				validTokens.add(token);
			} 
		}
		
			
		} catch(StackOverflowError t) {
			//System.out.println("Error when trying to parse: "+content);
			throw t;
		}catch (Exception e) {
			//System.out.println("Error when trying to parse: "+content);
			throw e;
		}
		
		return validTokens;
	}

	public String normalizeCodeNKeepCC() {
		String[] words = this.content.split("\\p{Punct}+|\\s+");
		ArrayList<String> wordList = new ArrayList<>(Arrays.asList(words));
		// decomposing the camel cases
		ArrayList<String> codeItems = extractCodeItem(wordList);
		codeItems = decomposeCamelCase(codeItems);
		wordList.addAll(codeItems);
		return MiscUtility.list2Str(wordList);
	}

	public String normalizeCodeNNotKeepCC() {
		String[] words = this.content.split("\\p{Punct}+|\\s+");
		ArrayList<String> wordList = new ArrayList<>(Arrays.asList(words));
		// decomposing the camel cases
		ArrayList<String> codeItems = extractCodeItem(wordList);
		codeItems = decomposeCamelCase(codeItems);
		wordList.addAll(codeItems);
		return MiscUtility.list2Str(codeItems);
		// return MiscUtility.list2Str(wordList);
	}

	
	
	public String normalizeSimpleCode() {
		String[] words = this.content.split("\\p{Punct}+|\\d+|\\s+");
		StopWordManager stopManager = new StopWordManager(true);
		return MiscUtility.list2Str(stopManager.getRefinedList(words));
	}

	public String normalizeSimpleNonCode() {
		String[] words = this.content.split("\\p{Punct}+|\\s+");
		ArrayList<String> wordList = new ArrayList<>(Arrays.asList(words));
		ArrayList<String> codeOnly = extractCodeItem(wordList);
		// only non-code elements
		wordList.removeAll(codeOnly);
		return MiscUtility.list2Str(wordList);
	}

	protected ArrayList<String> discardSmallTokens(ArrayList<String> items) {
		// discarding small tokens
		ArrayList<String> temp = new ArrayList<>();
		for (String item : items) {
			if (item.length() > 2) {
				temp.add(item);
			}
		}
		return temp;
	}

	
	public String normalizeTextLight() {
		// normalize the content
		String[] words = this.content.split("\\p{Punct}+|\\d+|\\s+");
		ArrayList<String> wordList = new ArrayList<>(Arrays.asList(words));
		// discard the small tokens
		wordList = discardSmallTokens(wordList);
		String modifiedContent = MiscUtility.list2Str(wordList);
		return modifiedContent;
	}

	protected ArrayList<String> extractCodeItem(ArrayList<String> words) {
		// extracting camel-case letters
		ArrayList<String> codeTokens = new ArrayList<>();
		for (String token : words) {
			if (isCamelCase(token)) {
				codeTokens.add(token);
			}
		}
		return codeTokens;
	}

	protected ArrayList<String> extractCodeItem(String[] words) {
		// extracting camel-case letters
		ArrayList<String> codeTokens = new ArrayList<>();
		for (String token : words) {
			if (isCamelCase(token)) {
				codeTokens.add(token);
			}
		}
		return codeTokens;
	}

	
	public static boolean isCamelCase(String token) {
		//return token.matches("([A-Z][a-z0-9]+)+|([A-Z]+[a-z0-9]+)+");
		
		return token.matches("([A-Z][a-z0-9]+)+|([A-Z]+[a-z0-9]+)+|(^[A-Z][a-z0-9]+[A-Z]$)|(^[A-Z][a-z0-9]+([A-Z][a-z0-9]+)+$)|(^[A-Z][a-z0-9]+([A-Z][a-z0-9]+)+[A-Z]$)");
		
		
		
	}

	protected ArrayList<String> decomposeCamelCase(String token) {
		// decomposing camel case tokens using regex
		ArrayList<String> refined = new ArrayList<>();
		String camRegex = "([a-z])([A-Z]+)";
		String replacement = "$1\t$2";
		String filtered = token.replaceAll(camRegex, replacement);
		String[] ftokens = filtered.split("\\s+");
		refined.addAll(Arrays.asList(ftokens));
		return refined;
	}

	public ArrayList<String> decomposeCamelCase(ArrayList<String> tokens) {
		// decomposing camel case tokens using regex
		ArrayList<String> refined = new ArrayList<>();
		for (String token : tokens) {
			//String camRegex = "([a-z])([A-Z]+)";
			String camRegex = "([a-z])([A-Z]+)";
			String replacement = "$1\t$2";
			String filtered = token.replaceAll(camRegex, replacement);
			String[] ftokens = filtered.split("\\s+");
			refined.addAll(Arrays.asList(ftokens));
		}
		return refined;
	}

}
