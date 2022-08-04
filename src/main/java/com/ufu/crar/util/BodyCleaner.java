package com.ufu.crar.util;

import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;


public class BodyCleaner {

	String orgFolder;
	String normFolderText;
	String normFolderCode;

	/*public BodyCleaner() {
		this.orgFolder = StaticData.EXP_HOME + "/dataset/answer";
		this.normFolderCode = StaticData.EXP_HOME + "/dataset/answer-norm-code";
		this.normFolderText = StaticData.EXP_HOME + "/dataset/answer-norm-text";
	}*/

	protected void saveBodyText(String outFile, String content) {
		ContentWriter.writeContent(outFile, content);
	}

	protected void saveBodyCode(String outFile, String content) {
		ContentWriter.writeContent(outFile, content);
	}

	/*protected static Set<String> extractClassesFromCode(String postHTML) {
		Document doc = Jsoup.parse(postHTML);
		Elements elems = doc.select("code,pre");
		String codeText = elems.text();
		return new TextNormalizer(codeText).normalizeSimpleCodeDiscardSmall();
	}*/

	protected static String extractText(String postHTML) {
		Document doc = Jsoup.parse(postHTML);
		doc.select("code,pre").remove();
		return doc.text();
	}

	
	public static void main(String[] args) {
		
		//new BodyCleaner().cleanTheBody();
	}
}
