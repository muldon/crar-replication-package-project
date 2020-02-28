package com.ufu.crar.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.StrTokenizer;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.util.AttributeFactory;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.google.common.base.Charsets;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;
import com.google.common.io.Resources;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ufu.crar.repository.GenericRepository;
import com.ufu.crar.to.Baseline;
import com.ufu.crar.to.Bucket;
import com.ufu.crar.to.ExternalQuestion;
import com.ufu.crar.to.GenericBucket;
import com.ufu.crar.to.Pair;
import com.ufu.crar.to.Post;
import com.ufu.crar.to.SoContentWordVector;
import com.ufu.crar.to.Tags.TagEnum;
import com.ufu.crar.to.ThreadContent;
import com.ufu.crar.to.UserEvaluation;

import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreSentence;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.simple.Sentence;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.tregex.TregexMatcher;
import edu.stanford.nlp.trees.tregex.TregexPattern;



@Component
public class CrarUtils {
	
	/*
	 * Stores the value obtained from the pathFileEnvFlag file
	 */
	
	@Value("${CRAR_HOME}")
	public String crokageHome;
	
	
	
	/*@Value("${STOP_WORDS_FILE_PATH}")
	public String STOP_WORDS_FILE_PATH;*/
	
	
	@Value("${action}")
	public String action;
	
	private static Logger logger = null;
	private static Map<String, String> sourceToMaster;
	private static CharArraySet stopWords;
	private AttributeFactory factory;
	private static StandardTokenizer standardTokenizer;
	private Boolean configsInitialized = false;
	private int countRecoveredPostsFromLinks=0;
	private static long endTime;
	private static Map<Integer, Set<Integer>> bucketDuplicatiosMap;
	//private Set<Integer> allDuplicatedQuestionsIds;
	//private static Set<PostLink> allPostLinks;
	private static Map<Integer, Set<Integer>> allPostLinks;
	private Map<Integer,Post> parentPostsCache;
	private Map<Integer,Post> answerPostsCache;
	private static DCG_TYPE dcgType = DCG_TYPE.COMB;
	private static List<String> stopWordsList;
	private static Set<String> stopWordsSet;
	private static StanfordCoreNLP pipeline;
	private TregexPattern sentencePattern1; 
	private TregexPattern sentencePattern2;
	private List<CoreSentence> removedSentences;
	private List<String> importantWordsList;
	private static Integer modelVecSize;
	private static Integer hotTopicsIntersectionCutOff; 
	
	@Autowired
	private CosineSimilarity cs1;
	
	@Autowired
	protected GenericRepository genericRepository;
	
	@Autowired
	protected TextNormalizer textNormalizer;
	
	
	public static final String PRE_CODE_REGEX_EXPRESSION = "(?sm)<pre><code>(.*?)</code></pre>";
	//public static final String CODE_REGEX_EXPRESSION = "(?sm)<pre.*?><code>(.*?)</code></pre>";
	public static final Pattern PRE_CODE_PATTERN = Pattern.compile(PRE_CODE_REGEX_EXPRESSION, Pattern.DOTALL); 
	
	public static final String CODE_MIN_REGEX_EXPRESSION = "(?sm)<code>(.*?)</code>";
	public static final Pattern CODE_MIN_PATTERN = Pattern.compile(CODE_MIN_REGEX_EXPRESSION, Pattern.DOTALL); 
	
	public static final String BLOCKQUOTE_EXPRESSION = "(?sm)<blockquote>(.*?)</blockquote>";
	public static final Pattern BLOCKQUOTE_PATTERN = Pattern.compile(BLOCKQUOTE_EXPRESSION, Pattern.DOTALL);
	
	//public static final String LINK_EXPRESSION_OUT = "(?sm)<a href=(.*?) rel=\"nofollow\">";
	//public static final String LINK_EXPRESSION_OUT = "(?sm)<a href=(.*?)>";
	public static final String LINK_EXPRESSION_OUT = "(?sm)<a href=\"(.*?)\"(.*?)</a>";
	public static final Pattern LINK_PATTERN = Pattern.compile(LINK_EXPRESSION_OUT, Pattern.DOTALL);
	
	public static final String LINK_TARGET_EXPRESSION_OUT = "(?sm)http(.*?)\\s";
	public static final Pattern LINK_TARGET_PATTERN = Pattern.compile(LINK_TARGET_EXPRESSION_OUT, Pattern.DOTALL);
	
	
	public static final String IMG_EXPRESSION_OUT = "(?sm)<img (.*?)>";
	public static final Pattern IMG_PATTERN = Pattern.compile(IMG_EXPRESSION_OUT, Pattern.DOTALL);
	
	public static final String ONLY_WORDS_EXPRESSION = "(?<!\\S)\\p{Alpha}+(?!\\S)";
	public static final Pattern ONLY_WORDS_PATTERN = Pattern.compile(ONLY_WORDS_EXPRESSION, Pattern.DOTALL);
	
	public static final String NOT_ONLY_WORDS_EXPRESSION = "(?<!\\S)(?!\\p{Alpha}+(?!\\S))\\S+";
	public static final Pattern NOT_ONLY_WORDS_PATTERN = Pattern.compile(NOT_ONLY_WORDS_EXPRESSION, Pattern.DOTALL);
	
	
	public static final String COMMENTS_REGEX_EXPRESSION = "//.*|(\"(?:\\\\[^\"]|\\\\\"|.)*?\")|(?s)/\\*.*?\\*/";
	public static final String COMMENTS_REPLACEMENT_EXPRESSION = "$1 ";
	
	private final String TMP_PATH = "/home/rodrigo/tmp/";
	
	public static final String CLASSES_CAMEL_CASE_REGEX_EXPRESSION = "\\b[A-Z][a-z]*([A-Z][a-z]*)*\\b";
	public static final Pattern CLASSES_REGEX_PATTERN = Pattern.compile(CLASSES_CAMEL_CASE_REGEX_EXPRESSION, Pattern.DOTALL);
	
	public static final String DOUBLE_QUOTES_REGEX_EXPRESSION = "\"(.*?)\"";
	
	public static final String NUMBERS_REGEX_EXPRESSION = "([\\d]+)";
	public static final Pattern NUMBERS_REGEX_PATTERN = Pattern.compile(NUMBERS_REGEX_EXPRESSION, Pattern.DOTALL);
	
	public static final String METHOD_CALLS_REGEX_EXPRESSION = "\\.\\w+\\(";
	public static final Pattern METHOD_CALLS_PATTERN = Pattern.compile(METHOD_CALLS_REGEX_EXPRESSION, Pattern.DOTALL);
	
	public static final String METHOD_CALLS_REGEX_EXPRESSION_PYTHON = "\\w+\\(";
	public static final Pattern METHOD_CALLS_PATTERN_PYTHON = Pattern.compile(METHOD_CALLS_REGEX_EXPRESSION_PYTHON, Pattern.DOTALL);
		
	
	public static final String keywords[] = { "abstract", "assert", "boolean",
            "break", "byte", "case", "catch", "char", "class", "const",
            "continue", "default", "do", "double", "else", "extends", "false",
            "final", "finally", "float", "for", "goto", "if", "implements",
            "import", "instanceof", "int", "interface", "long", "native",
            "new", "null", "package", "private", "protected", "public",
            "return", "short", "static", "strictfp", "super", "switch",
            "synchronized", "this", "throw", "throws", "transient", "true",
            "try", "void", "volatile", "while" };

	public static final String important_words[] = {"java", "api", "regular", "expression","http", "output",
			"follow","follows", "return","use","code","efficient", "=","-","+","*","/","add","insert","update","remove","delete",
			"map","list","once","alternative", "replace","example","increment","decrement"
			};
	
	
	
	private static String htmlTags[] = {"<p>","</p>","<pre>","</pre>","<blockquote>","</blockquote>", /*"<a href=\"","\">",*/
			"</a>","<img src=","alt=","<ol>","</ol>","<li>","</li>","<ul>",
			"</ul>","<br>","<br />","<br/>","</br>","<h1>","</h1>","<h2>","<h3>","<h3>","</h2>","<strong>","</strong>",
			"<code>","</code>","<em>","</em>","<hr>"};
	
	private static String htmlTagsWithCodeAndBlockquotes[] = {"<p>","</p>", /*"<a href=\"","\">",*/
			"</a>","<img src=","alt=","<ol>","</ol>","<li>","</li>","<ul>",
			"</ul>","<br>","</br>","<h1>","</h1>","<h2>","</h2>","<strong>","</strong>",
			"<em>","</em>","<hr>"};
	
	//stemmed - stopped
	//private static String unncessaryWords[] = {"java", "how", "what"};
	private static String unncessaryWords[] = {}; //td-idf comparates a query with an answer content, but this content is reinforced with its parent title and body
	
	public CrarUtils() throws Exception {
		//initializeConfigs();
	}
	
	@PostConstruct
	public void initializeConfigs() throws Exception {
		logger = LoggerFactory.getLogger(this.getClass());
		/*if(action.equals("queriesAnalysis")) {
			return;
		}*/
		
		if(!configsInitialized){
			configsInitialized = true;
				
			factory = AttributeFactory.DEFAULT_ATTRIBUTE_FACTORY;
			standardTokenizer = new StandardTokenizer(factory);
			stopWords = EnglishAnalyzer.getDefaultStopSet();
			
			standardTokenizer.close();
			//loadTagSynonyms();
			//configureEnvironmentVariables();
		}
		/*
		if(minTokenSize==null) {
			Properties prop = new Properties();
			prop.load(CrarUtils.class.getClassLoader().getResourceAsStream("application.properties")); 
			minTokenSize = new Integer(prop.getProperty("minTokenSize"));
			
		}*/
		parentPostsCache = new LinkedHashMap<>();
		parentPostsCache = new LinkedHashMap<Integer, Post>();
		answerPostsCache = new LinkedHashMap<Integer, Post>();
		
		//String resourceName = "stop_words_english_total.txt";
		String resourceName = "stanford_stop_words.txt";
		
		//File file = new File(getClass().getClassLoader().getResource("stop_words_english_total.txt").getFile()); //works outside jar
		
        // this is the path within the jar file
        InputStream input = getClass().getResourceAsStream("/resources/" + resourceName);
        if (input == null) {
            // this is how we load file within editor (eg eclipse)
            input = getClass().getClassLoader().getResourceAsStream(resourceName);
        }
        //File file = new File(input);
		
		//stopWordsList = new BufferedReader(input.lines().collect(Collectors.toList()); 
		
		
        stopWordsList = IOUtils.readLines(input, StandardCharsets.UTF_8);
        stopWordsSet = new LinkedHashSet<>(stopWordsList);
		/*try (InputStream resource = getClass().getResourceAsStream(resourceName)) {
			stopWordsList =  new BufferedReader(new InputStreamReader(resource, StandardCharsets.UTF_8)).lines().collect(Collectors.toList());
		}*/
		System.out.println("10th stop word: "+stopWordsList.get(10));
		
		
		//stopWordsList = Files.readAllLines(Paths.get(STOP_WORDS_FILE_PATH)); 
		
		Properties props = new Properties();
	    // set the list of annotators to run
	    props.setProperty("annotators", "tokenize,ssplit,pos");
	    pipeline = new StanfordCoreNLP(props);
	    
		/*
		 // set up pipeline properties
	    Properties props = new Properties();
	    // set the list of annotators to run
	    props.setProperty("annotators", "tokenize,ssplit,pos,parse");
	    // set a property for an annotator, in this case the coref annotator is being set to use the neural algorithm
	    props.setProperty("coref.algorithm", "neural");
	   
	    pipeline = new StanfordCoreNLP(props);
	    
	    sentencePattern1 = TregexPattern.compile("VP << (NP < /NN.?/) < /VB.?/");
	    sentencePattern2 = TregexPattern.compile("NP ! < PRP [<< VP | $ VP]");*/
	    
	    
	    
	    removedSentences = new ArrayList<>();
	    
	    importantWordsList = Arrays.asList(important_words);
	    
	    modelVecSize = 100;
	    
	}
	
	

	/*private void configureEnvironmentVariables() {
		if(!StringUtils.isBlank(bikerHome)) {
			BIKER_HOME=bikerHome;
		}
		if(!StringUtils.isBlank(crokageHome)) {
			CRAR_HOME=crokageHome;
		}
		if(!StringUtils.isBlank(tmpDir)) {
			TMP_DIR=tmpDir;
		}
		
		
	}*/


	public static double round4(double pNumero) {
		pNumero = Math.round(pNumero * 10000) / 10000d;
		return pNumero;
	}
	
	public static double round5(double pNumero) {
		pNumero = Math.round(pNumero * 100000) / 100000d;
		return pNumero;
	}

	public static double round(Double pNumero, int pCantidadDecimales) {
		BigDecimal value = null;
		value = new BigDecimal(pNumero);
		value = value.setScale(pCantidadDecimales, RoundingMode.HALF_EVEN);
		return value.doubleValue();
	}
	
	public static double calculateProportionInPercentage(int numerator, int denominator) {
		double proportion = (numerator*100 / (float)denominator);
		BigDecimal value = null;
		value = new BigDecimal(proportion);
		value = value.setScale(2, RoundingMode.HALF_EVEN);
		return value.doubleValue();
	}
	
	/*
	public String tokenizeStopStem(String input) throws Exception {
		String token;
		if (StringUtils.isBlank(input)) {
			return "";
		}
		StringReader sr = new StringReader(input);
		
		standardTokenizer.setReader(sr);
		TokenStream stream = new StopFilter(new LowerCaseFilter(new PorterStemFilter(standardTokenizer)), stopWords);

		CharTermAttribute charTermAttribute = standardTokenizer.addAttribute(CharTermAttribute.class);
		stream.reset();

		StringBuilder sb = new StringBuilder();
		while (stream.incrementToken()) {
			if (sb.length() > 0) {
				sb.append(" ");
			}
			token = charTermAttribute.toString();
			if(token.length()>minTokenSize) {
				sb.append(token);
			}
			
		}

		stream.end();
		stream.close();
		token = null;
		sr = null;
		return sb.toString();
	}
	*/
	/*
	public String removeStopWords(String textFile) throws Exception {
		String token;
		if (StringUtils.isBlank(textFile)) {
			return "";
		}
		StringReader sr = new StringReader(textFile);
		standardTokenizer.setReader(sr);
		
		TokenStream stream = new StopFilter(standardTokenizer, stopWords);

		CharTermAttribute charTermAttribute = standardTokenizer.addAttribute(CharTermAttribute.class);
		stream.reset();

		StringBuilder sb = new StringBuilder();
		while (stream.incrementToken()) {
			if (sb.length() > 0) {
				sb.append(" ");
			}
			token = charTermAttribute.toString();
			if(token.length()>minTokenSize) {
				sb.append(token);
			}
			
		}

		stream.end();
		stream.close();
		token = null;
		sr = null;
		return sb.toString();
	}
	*/
	
	
	
	public static void reportElapsedTime(long initTime, String processName) {
		endTime = System.currentTimeMillis();
		String duration = DurationFormatUtils.formatDuration(endTime-initTime, "HH:mm:ss,SSS");
		System.out.println("Done with "+processName+", duration: "+duration);
	}
	
	
	/*
	 * Remove especial simbols only if isolated. Remove .:;'" if surrounded by spaces in any side. ?!, from all 
	 */
	public static String removePunctuations(String content) {
		content = content.replaceAll("\\s+\\p{Punct}+\\s"," "); 
		content = content.replaceAll("\\s+\\."," ");
		content = content.replaceAll("\\.(\\s+|$)"," ");
		content = content.replaceAll("\\s+\\:"," ");
		content = content.replaceAll("\\:(\\s+|$)"," ");
		content = content.replaceAll("\"\\s+"," ");
		content = content.replaceAll("\\s+\""," ");
		content = content.replaceAll("\'\\s+"," ");
		content = content.replaceAll("\\s+\'"," ");
		
		content = content.replaceAll("\\s+(\\;|$)"," ");
		content = content.replaceAll("\\;\\s+"," ");
		content = content.replaceAll("\\,"," ");
		content = content.replaceAll("\\?"," ");
		content = content.replaceAll("\\!"," ");
		return content;
	}
	
	public static String removeAllPunctuations(String body) {
		body = body.replaceAll("\\p{Punct}+"," "); 
		body = body.replaceAll("[^\\x20-\\x7e]", " "); //non-UTF-8 chars
		return body;
	}
	
	public static String translateHTMLSimbols(String finalContent) {
		finalContent = finalContent.replaceAll("&amp;","&");
		finalContent = finalContent.replaceAll("&lt;", "<");
		finalContent = finalContent.replaceAll("&gt;", ">");
		finalContent = finalContent.replaceAll("&quot;", "\"");
		finalContent = finalContent.replaceAll("&apos;", "\'"); 
		
		return finalContent;
	}
	
	
	public static List<String> getCodeValues(Pattern patter,String str) {
	    final List<String> tagValues = new ArrayList<String>();
	    final Matcher matcher = patter.matcher(str);
	    while (matcher.find()) {
	        tagValues.add(matcher.group(1));
	    }
	    return tagValues;
	}
	
	

	public static void countMethods(Bucket bucket, Integer tagId, Map<String, Integer> methodsCounterMap) {
		Set<String> methods = getMethodCalls(bucket.getCode(),tagId);
		bucket.setMethods(methods);
		for(String method: methods) {
			if(method.equals(".println(") || method.equals(".print(") ) {
			//if(method.equals(".println(")) {
				continue;
			}
			if(methodsCounterMap.containsKey(method)) {
				Integer currentCount = methodsCounterMap.get(method);
				currentCount++;
				methodsCounterMap.put(method,currentCount);
			}else {
				methodsCounterMap.put(method,1);
			}
		}
		methods=null;
	}
	
	public static Set<String> getMethodCalls(String str, Integer tagId) {
		Set<String> methodCalls = new LinkedHashSet<String>();
	    Matcher matcher = null;
	    if(tagId.equals(TagEnum.Java.getId())) {
	    	matcher= METHOD_CALLS_PATTERN.matcher(str);
	    }else {	
	   // }else if(tagId.equals(TagEnum.Python.getId())) {
	    	matcher= METHOD_CALLS_PATTERN_PYTHON.matcher(str);
	    }
	    		
	    		
	    while (matcher.find()) {
	        methodCalls.add(matcher.group(0));
	    }
	    return methodCalls;
	}
	

	public static List<String> getPreCodes(String presentingBody) {
		//String codeContent = "";
		List<String> codes = getCodeValues(PRE_CODE_PATTERN, presentingBody);
		//int i=0;
		/*for(String code: codes){
			//codeContent+= "code "+(i+1)+":"+code+ "\n\n";
			codeContent+= code+ "\n\n";
			//i++;
		}*/
		//System.out.println("\nCodes: \n"+codeContent);
		return codes;
	}
	
	public static List<String> getSimpleCodes(String presentingBody) {
		//first remove pre codes
		
		presentingBody = presentingBody.replaceAll(PRE_CODE_REGEX_EXPRESSION, " ");
		//String codeContent="";
		List<String> smallCodes = getCodeValues(CODE_MIN_PATTERN, presentingBody);
		/*for(String code: smallCodes){
			codeContent+= code+ "\n\n";
		}*/
		
		return smallCodes;
	}
	
	public static boolean isNumeric(String str)
	{
		return str.matches("[+-]?\\d*(\\.\\d+)?");
	}
	
	
	public static boolean containNumber(String str) {
		return str.matches(".*\\d+.*");
	}
	
	
	//public static 

	/**
	 * Remove images and html tags, except codes. For the links, leaves only the target.
	 * @param body
	 * @return
	 */
	public static String buildPresentationBody(String body, boolean removeHtmlTags) {
		
		String finalBody = translateHTMLSimbols(body);
		
		finalBody = finalBody.replaceAll(IMG_EXPRESSION_OUT, " ");
		
		finalBody = extractLinksTargets(finalBody);
		
		if(removeHtmlTags) {
			finalBody = removeHtmlTags(finalBody);
		}
		
		return finalBody;
	}
	

	public Set<String> getClassesNames(List<String> codes) {

		Set<String> classesNames = new LinkedHashSet();
		for(String code: codes) {
			getClassesNamesForString(classesNames,code);
		}
		return classesNames;
				
	}

	public void getClassesNamesForString(Set<String> classesNames, String code) {
		//remove java keywords
		for(String keyword: CrarUtils.keywords){
			code= code.replaceAll(keyword,"");
		}
		
		//remove double quotes contents
		code= code.replaceAll(CrarUtils.DOUBLE_QUOTES_REGEX_EXPRESSION,"");
		
		//remove comments
		code = code.replaceAll( CrarUtils.COMMENTS_REGEX_EXPRESSION, CrarUtils.COMMENTS_REPLACEMENT_EXPRESSION );
		
		//Get classes in camel case
		Pattern pattern = Pattern.compile(CrarUtils.CLASSES_CAMEL_CASE_REGEX_EXPRESSION);
		Matcher matcher = pattern.matcher(code);
		while (matcher.find()) {
			if(matcher.group(0)!=null && matcher.group(0).length()>2) {
				classesNames.add(matcher.group(0));
			}
			
		}
		
	}
	
/*
	public String buildProcessedBodyStemmedStopped(String presentingBody, boolean isAnswer) throws Exception {
		
		String finalStr = presentingBody.replaceAll(LINK_TARGET_EXPRESSION_OUT, "");
		
		//finalStr = finalStr.replaceAll(CODE_MIN_REGEX_EXPRESSION, "");
		//finalStr = finalStr.replaceAll("<code>", " ").replaceAll("</code>", " ");
		
		//blockquotes
		String blockquoteContent = "";
		List<String> blockquotes = getCodeValues(BLOCKQUOTE_PATTERN, finalStr);
		for(String blockquote: blockquotes){
			blockquoteContent+= blockquote+ "\n\n";
		}
		
		String textWithoutCodesLinksAndBlackquotes = finalStr.replaceAll(BLOCKQUOTE_EXPRESSION, " ");
		
		
		String codeContent = "";
		List<String> codes = getCodeValues(PRE_CODE_PATTERN, textWithoutCodesLinksAndBlackquotes);
		//int i=0;
		for(String code: codes){
			codeContent+= code+ "\n\n";
		}
		
		textWithoutCodesLinksAndBlackquotes = textWithoutCodesLinksAndBlackquotes.replaceAll(PRE_CODE_REGEX_EXPRESSION, " ");
		
		List<String> smallCodes = getCodeValues(CODE_MIN_PATTERN, textWithoutCodesLinksAndBlackquotes);
		for(String code: smallCodes){
			codeContent+= code+ "\n\n";
		}
		
		
		textWithoutCodesLinksAndBlackquotes = textWithoutCodesLinksAndBlackquotes.replaceAll(CODE_MIN_REGEX_EXPRESSION, " ");
		
		textWithoutCodesLinksAndBlackquotes = removeOtherSymbolsAccordingToPostPart(textWithoutCodesLinksAndBlackquotes,isAnswer);
		
		String onlyWords = getOnlyWords(textWithoutCodesLinksAndBlackquotes);
		
		onlyWords = onlyWords.toLowerCase()+ " ";
		
		String specificTerms = "";
		List<String> notOnlyWords = getWords(NOT_ONLY_WORDS_PATTERN, textWithoutCodesLinksAndBlackquotes);
		for(String word: notOnlyWords){
			specificTerms+= word+ " ";
		}
		
		specificTerms = removeSpecialSymbols(specificTerms.trim());
		specificTerms = specificTerms.replaceAll("\\b\\w{1,1}\\b\\s?", "");   //remove single small tokens like "s"
		
		String stoppedStemmed = tokenizeStopStem(onlyWords.trim());
		
		//finalStr = stoppedStemmed + " "+specificTerms+ " "+blockquoteContent+ " "+codeContent;
		finalStr = stoppedStemmed + " "+specificTerms+ " "+blockquoteContent;
		//System.out.println(finalStr);
		codeContent = null;
		textWithoutCodesLinksAndBlackquotes= null;
		specificTerms = null;
		stoppedStemmed = null;
		onlyWords = null;
		blockquoteContent = null;
		//replace \n to space
		//replace more than one space by one space only
		finalStr = StringUtils.normalizeSpace(finalStr);
		
		return finalStr;
	}*/

	/*
	public String[] separateWordsCodePerformStemmingStopWords(String content, boolean isAnswer) throws Exception {
		String[] finalContent = new String[4];
		
		//volta simbolos de marcacao HTML para estado original 
		String originalSymbols = translateHTMLSimbols(content);		
		
		String textWithoutCodesLinksAndBlackquotes = originalSymbols.replaceAll(LINK_EXPRESSION_OUT, " ");
		
		textWithoutCodesLinksAndBlackquotes = textWithoutCodesLinksAndBlackquotes.replaceAll(IMG_EXPRESSION_OUT, " ");
		
		//blockquotes
		String blockquoteContent = "";
		List<String> blockquotes = getCodeValues(BLOCKQUOTE_PATTERN, textWithoutCodesLinksAndBlackquotes);
		for(String blockquote: blockquotes){
			blockquoteContent+= blockquote+ " ";
		}
		blockquoteContent = removeHtmlTags(blockquoteContent);
		blockquoteContent = blockquoteContent.replaceAll("\n", " ");
		textWithoutCodesLinksAndBlackquotes = textWithoutCodesLinksAndBlackquotes.replaceAll(BLOCKQUOTE_EXPRESSION, " ");
		
		//codes
		String codeContent = "";
		List<String> codes = getCodeValues(PRE_CODE_PATTERN, textWithoutCodesLinksAndBlackquotes);
		//int i=0;
		for(String code: codes){
			//codeContent+= "code "+(i+1)+":"+code+ "\n\n";
			codeContent+= code+ "\n\n";
			//i++;
		}
		
		//codeContent = codeContent.replaceAll("\n", " ");
		codeContent = removeHtmlTags(codeContent);
		
					
		//textoSemCodigosLinksEBlackquotes = textoSemCodigosLinksEBlackquotes.replaceAll(BLOCKQUOTE_EXPRESSION, " ");
		//String codeToBeJoinedInBody = retiraSimbolosCodeParaBody(codeContent);
				
		//String bloquesAndLinks = blockquoteContent+ " "+linksContent;
		String bloquesAndLinks = blockquoteContent;
		bloquesAndLinks = bloquesAndLinks.replaceAll("\n", " ");
		bloquesAndLinks = retiraHtmlTags(bloquesAndLinks);
		bloquesAndLinks += " "+codeContent;
		
		
		textWithoutCodesLinksAndBlackquotes = textWithoutCodesLinksAndBlackquotes.replaceAll(PRE_CODE_REGEX_EXPRESSION, " ");
		textWithoutCodesLinksAndBlackquotes = removeHtmlTags(textWithoutCodesLinksAndBlackquotes);
		
		//trata antes de pegar somenta as palavras
		
		String separated = removeOtherSymbolsAccordingToPostPart(textWithoutCodesLinksAndBlackquotes,isAnswer);
		
		
		String onlyWords = getOnlyWords(separated);
		List<String> palavras = getWords(ONLY_WORDS_PATTERN, separated);
		for(String word: palavras){
			somentePalavras+= word+ " ";
		}
		onlyWords = onlyWords.toLowerCase()+ " ";
		
		String specificTerms = "";
		List<String> naoPalavras = getWords(NOT_ONLY_WORDS_PATTERN, separated);
		for(String word: naoPalavras){
			specificTerms+= word+ " ";
		}
		specificTerms+= blockquoteContent;
		
		specificTerms = removeSpecialSymbols(specificTerms);
		
		String stoppedStemmed = tokenizeStopStem(onlyWords.trim());
		
		finalContent[0] = stoppedStemmed; //+ " "+somentePalavrasCodeClean;
		finalContent[1] = specificTerms.trim();
		finalContent[2] = codeContent.trim();
		
		return finalContent;
		

	}
	
	*/
	
	
	

	public static String getOnlyWords(String separated) {
		String somentePalavras="";
		List<String> palavras = getWords(ONLY_WORDS_PATTERN, separated);
		for(String word: palavras){
			somentePalavras+= word+ " ";
		}
		return somentePalavras;
		
	}



	public String removeOtherSymbolsAccordingToPostPart(String finalContent, boolean isAnswer) {
		if(!isAnswer){
			finalContent = finalContent.replaceAll("\\?", " ");
		}else {
			finalContent = finalContent.replaceAll("\\:", " ");
		}
		return finalContent;
	}
	
	
	
	/**
	 * Retira tags de marcação do body ou title  
	 */
	public static String removeHtmlTags(String content) {
		if(content==null || content.trim().equals("")){
			return "";
		}
		//boolean startedHtml = false;
		for(String tag: htmlTags){
			content= content.replaceAll(tag," ");
		}
		//content = content.replaceAll("<a href=", " ");
		//content = content.replaceAll("</a>", " ");

		return content;
		
	}
	
	/**
	 * Retira tags de marcação do body ou title  
	 */
	public static String removeHtmlTagsExceptCode(String content) {
		if(content==null || content.trim().equals("")){
			return "";
		}
		//boolean startedHtml = false;
		for(String tag: htmlTagsWithCodeAndBlockquotes){
			content= content.replaceAll(tag," ");
		}
		//content= content.replaceAll("<code>","\n<code>\n");
		//content= content.replaceAll("</code>","\n</code>\n");
		//content = content.replaceAll("<a href=", " ");
		//content = content.replaceAll("</a>", " ");

		return content;
		
	}
	
	
	public static List<String> getWords(Pattern patter,String str) {
	    final List<String> tagValues = new ArrayList<String>();
	    final Matcher matcher = patter.matcher(str);
	    while (matcher.find()) {
	        tagValues.add(matcher.group(0));
	    }
	    return tagValues;
	}
	
	
	
	
	
	public static String removeSpecialSymbols(String finalContent) {
		
		//nao precisam de espaco
		finalContent = finalContent.replaceAll("\\+"," ");
		finalContent = finalContent.replaceAll("\\^", " ");
		finalContent = finalContent.replaceAll(":", " ");
		finalContent = finalContent.replaceAll(";", " ");
		finalContent = finalContent.replaceAll("-", " ");
		finalContent = finalContent.replaceAll("\\+", " ");
		finalContent = finalContent.replaceAll("&", " ");
		finalContent = finalContent.replaceAll("\\*", " ");
		finalContent = finalContent.replaceAll("\\~", " ");
		finalContent = finalContent.replaceAll("\\\\", " ");
		finalContent = finalContent.replaceAll("/", " ");
		//finalContent = finalContent.replaceAll("\\'", "simbaspassimpl");
		finalContent = finalContent.replaceAll("\\`", " ");
		finalContent = finalContent.replaceAll("\"", " ");
		finalContent = finalContent.replaceAll("\\(", " ");
		finalContent = finalContent.replaceAll("\\)", " ");
		finalContent = finalContent.replaceAll("\\[", " ");
		finalContent = finalContent.replaceAll("\\]", " ");
		finalContent = finalContent.replaceAll("\\{", " ");
		finalContent = finalContent.replaceAll("\\}", " ");
		finalContent = finalContent.replaceAll("\\?", " ");
		finalContent = finalContent.replaceAll("\\|", " ");
		finalContent = finalContent.replaceAll("\\%", " ");
		finalContent = finalContent.replaceAll("\\$", " ");
		finalContent = finalContent.replaceAll("\\@", " ");
		finalContent = finalContent.replaceAll("\\<", " ");
		finalContent = finalContent.replaceAll("\\>", " ");
		finalContent = finalContent.replaceAll("\\#", " ");
		finalContent = finalContent.replaceAll("\\=", " ");
		finalContent = finalContent.replaceAll("\\.", " ");
		finalContent = finalContent.replaceAll("\\,", " ");
		finalContent = finalContent.replaceAll("\\_", " ");
		finalContent = finalContent.replaceAll("\\!", " ");
		finalContent = finalContent.replaceAll("\\'", " ");		
		
		finalContent = finalContent.replaceAll("\n", " ");
		
		return finalContent;
	}
	
	
public static String removeSpecialSymbolsTitles(String finalContent) {
		
		finalContent = removeSpecialSymbols(finalContent);
		finalContent = finalContent.replaceAll("\\?", "");
		
		return finalContent;
	}




	public static String getQueryComplementByTag(String tagFilter) {
		String query="";
		if(tagFilter!=null && !"".equals(tagFilter)) {
			if(tagFilter.equals("java")) {
				query += " and tags like '%java%' and tags not like '%javascript%' "; 
			}else {
				query += " and tags like '%"+tagFilter+"%'";
			}
		}
		return query;
		
	}
	
	
	public static String getQueryComplementByTagStrict(String tagFilter) {
		String query="";
		if(tagFilter!=null && !"".equals(tagFilter)) {
			query += " and tags like '%<"+tagFilter+">%'";
			
		}
		return query;
		
	}

	
	public static String tagMastering(String tags) throws Exception {
		if (tags == null) {
			return "";
		}
		StrTokenizer tokenizer = new StrTokenizer(tags);
		Set<String> tagsSet = new LinkedHashSet<>();
		for (String token : tokenizer.getTokenArray()) {

			String master = sourceToMaster.get(token);
			if (master == null) {
				master = token;
			}
			tagsSet.add(master);
		}
		String str = StringUtils.join(tagsSet, " ");
		tokenizer = null;
		tagsSet = null;
		 				
		return str;
	}


	public void loadTagSynonyms() throws Exception {
		sourceToMaster = new LinkedHashMap<>();

		String csvFile = "/tagSynonyms.csv";
		String line = "";
		String cvsSplitBy = ",";

		InputStream in = getClass().getResourceAsStream(csvFile);
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));

		try (BufferedReader br = reader) {
			while ((line = br.readLine()) != null) {
				// use comma as separator
				String[] tags = line.split(cvsSplitBy);
				sourceToMaster.put(tags[0], tags[1]);
			}
		} catch (IOException e) {
			throw e;
		}
	}


	
	
	
	
	public void getPostsLinks() {
		System.out.println("Retrieving PostLinks... ");
		if(allPostLinks==null){
			allPostLinks = genericRepository.getAllPostLinks();
		}
		System.out.println("PostLinks retrieved: " + allPostLinks.size());

	}
	
	public void generateBuckets() {
		if(bucketDuplicatiosMap==null){
			bucketDuplicatiosMap = new LinkedHashMap<Integer, Set<Integer>>();	
			getPostsLinks();
			//Pode haver mais de uma duplicada por questao.. BucketOld structure
			System.out.println("Building buckets");
					
			for(Map.Entry<Integer, Set<Integer>> entry : allPostLinks.entrySet()){
				
				Integer postId = entry.getKey();
				Set<Integer> mastersIds = entry.getValue(); 
				
				for(Integer masterId: mastersIds) {
					Set<Integer> duplicatedOfMaster = bucketDuplicatiosMap.get(masterId);  
					if(duplicatedOfMaster==null){
						duplicatedOfMaster = new LinkedHashSet<Integer>();
						bucketDuplicatiosMap.put(masterId,duplicatedOfMaster);
					}
					duplicatedOfMaster.add(postId);
				}
				
			}
			
			System.out.println("Buckets gerados...");
		}
	}
	

	public static Map<Integer, Set<Integer>> getBucketDuplicatiosMap() {
		return bucketDuplicatiosMap;
	}

		
	
	public static String getDataBase(String fullPath) {
		String dataBaseName[] = fullPath.split("5432/?");
		return dataBaseName[1];
	}
	
	
	public static Set<Integer> getRelatedPostIds(Integer questionId) {
		//Set<Integer> relatedPostIds = new LinkedHashSet<Integer>();
		/*for(PostLink postLink: allPostLinks){
			if(postLink.getPostId().equals(questionId)){
				relatedPostIds.add(postLink.getRelatedPostId());
			}
		}*/
		//Set<Integer> relatedPostsIdsTmp = allPostLinks.get(questionId);
		
		return allPostLinks.get(questionId);
	}

	
	
	
	


	
	
	/*public void removePostsWithIncompleteTitlesOld(Set<ProcessedPostOld> processedPostsByFilter, Set<ProcessedPostOld> closedDuplicatedNonMastersByTag) {
		System.out.println("Removing invalid posts. Processedposts before: "+processedPostsByFilter.size());
		StringTokenizer st = null;
		Set<ProcessedPostOld> invalidPosts = new LinkedHashSet<>();
		
		
			
		for(ProcessedPostOld processedPosts: processedPostsByFilter) {
			st = new StringTokenizer(processedPosts.getTitle());
			if(st.countTokens() <= 3) {
				invalidPosts.add(processedPosts);
			}
			st = null;
		}
		
		processedPostsByFilter.removeAll(invalidPosts);
		System.out.println("Processed posts after removing invalid posts: "+processedPostsByFilter.size()+"\nInvalid posts: "+invalidPosts.size());
		
		
		System.out.println("...now removing invalid posts from closedDuplicatedNonMastersIdsByTag. Before: "+closedDuplicatedNonMastersByTag.size());
		invalidPosts = new LinkedHashSet<>();
		
		for(ProcessedPostOld processedPosts: closedDuplicatedNonMastersByTag) {
			st = new StringTokenizer(processedPosts.getTitle());
			if(st.countTokens() <= 3) {
				invalidPosts.add(processedPosts);
			}
			st = null;
		}
		
		closedDuplicatedNonMastersByTag.removeAll(invalidPosts);
		System.out.println("closedDuplicatedNonMastersIdsByTag after removing invalid posts: "+closedDuplicatedNonMastersByTag.size()+"\nInvalid posts: "+invalidPosts.size());
				
	}*/
	
	
	
	
	
	
	public static <K, V> void printMap(Map<K, V> map) {
        for (Map.Entry<K, V> entry : map.entrySet()) {
            System.out.println("Key : " + entry.getKey() + " Value : " + entry.getValue());
        }
    }


	public static String extractLinksTargets(String strInput) {
		final Matcher matcher = LINK_PATTERN.matcher(strInput);
	    while (matcher.find()) {
	    /*    System.out.println(matcher.group(0));
	        System.out.println(matcher.group(1));
	        System.out.println(matcher.group(2));*/
	        
	        strInput = strInput.replace(matcher.group(0), matcher.group(1));
	        strInput = strInput.replace(matcher.group(2), "");
	        
	        
	    }
		return strInput;
	}
	
	
	/*public static String extractLinksContents(String strInput) {
		final Matcher matcher = LINK_PATTERN.matcher(strInput);
		String linkContent = "";
	    while (matcher.find()) {
	    	linkContent = "group0 : "+matcher.group(0)+ " 1:"+matcher.group(1)+ " 2:"+matcher.group(2);
	    }
		return linkContent;
	}*/
	
	
	public String removeLinkTargets(String strInput) {
		final Matcher matcher = LINK_TARGET_PATTERN.matcher(strInput);
	    while (matcher.find()) {
	        strInput = strInput.replace(matcher.group(0), "");
	    }
		return strInput;
	}



	 public static boolean isJavaKeyword(String keyword) {
	        return (Arrays.binarySearch(keywords, keyword) >= 0);
	    }


	public static String removeDuplicatedTokens(String processedBodyStemmedStopped, String separator) {
		processedBodyStemmedStopped = Arrays.stream(processedBodyStemmedStopped.split(separator)).distinct().collect(Collectors.joining(separator));
		return processedBodyStemmedStopped;
	}


	public String removeUnnecessaryWords(String content) {
		if(content==null || content.trim().equals("")){
			return "";
		}
		//boolean startedHtml = false;
		for(String word: unncessaryWords){
			content= content.replaceAll(word," ");
		}
		return content;
	}


	public static int countLines(String str){
	   String[] lines = str.split("\r\n|\r|\n");
	   return  lines.length;
	}
	
	
	public static String processQuery(String query, Boolean lemmatize) {
		query = query.toLowerCase();
		query = translateHTMLSimbols(query);
		
		//remove punctuations
		query = removeAllPunctuations(query);
		String[] words = query.split("\\s+");
		Set<String> validWords = new LinkedHashSet<>();
		
		//remove stop words or small words or numbers only
		assembleValidWords(validWords,words);
				
		String finalContent = String.join(" ", validWords);
		finalContent = finalContent.replaceAll("\u0000", "");
		
		if(lemmatize) {
			edu.stanford.nlp.simple.Document stfDoc = new edu.stanford.nlp.simple.Document(finalContent);
			List<String> sentList; 
					
			List<Sentence> sentences = stfDoc.sentences();
			if(!sentences.isEmpty()) {
				sentList= sentences.get(0).lemmas();
				finalContent = String.join(" ", sentList);
			}
		}
		
		validWords = null;
		query = null;
		words = null;
		return finalContent;
	}
	
	public static Set<String> getProcessedWords(String query){
		query = query.toLowerCase();
		query = translateHTMLSimbols(query);
		
		//remove punctuations
		query = removeAllPunctuations(query);
		String[] words = query.split("\\s+");
		Set<String> validWords = new LinkedHashSet<>();
		
		//remove stop words or small words or numbers only
		assembleValidWords(validWords,words);
		return validWords;
	}
	
	
	private static void assembleValidWords(Set<String> validWords, String[] words) {
		for(String word:words) {
			word = word.trim();
			if(!stopWordsList.contains(word) && !(word.length()<2) && !StringUtils.isBlank(word) && !isNumeric(word)) {
				validWords.add(word);
			}
			
		}
	}

	
	
	public List<ExternalQuestion> readAnswerBotQuestionsAndAnswersOld() throws IOException {
		List<ExternalQuestion> answerBotQuestionAnswers = new ArrayList<>();
		
		URL url;
		String fileContent="";
		String query="";
		String answer="";
				
		for(int i=0; i<100; i++){
			url = Resources.getResource(i+".txt");
			fileContent = Resources.toString(url, Charsets.UTF_8);
			
			List<String> lines = IOUtils.readLines(new StringReader(fileContent));
			query = lines.get(1);
			query = query.replace("query : ","");
			
			lines.remove(0);
			lines.remove(0);
			lines.remove(0);
			lines.remove(0);
			lines.remove(lines.size()-1);
			
			answer = lines.stream().collect(Collectors.joining("\n"));
			//System.out.println(answer);
			//answerBotQuestionAnswers.add(new ExternalQuestion(i+1,query,answer));
		}
		
		return answerBotQuestionAnswers;
		
	}
	
	
	
	public void storeParentPostInCache(Post post) {
		if(!parentPostsCache.containsKey(post.getId())) {
			parentPostsCache.put(post.getId(), post);
		}
		
	}

	public void storeAnswerPostInCache(Post post) {
		if(!answerPostsCache.containsKey(post.getId())) {
			answerPostsCache.put(post.getId(), post);
		}
		
	}

	public Map<Integer, Post> getParentPostsCache() {
		return parentPostsCache;
	}


	public Map<Integer, Post> getAnswerPostsCache() {
		return answerPostsCache;
	}


	public static boolean containCode(String another7Code) {
		List<String> preCodes = getPreCodes(another7Code);
		List<String> simpleCodes = getSimpleCodes(another7Code);
		
		return (!preCodes.isEmpty() || !simpleCodes.isEmpty()); 
		
	}

	
	
	
	
	

	public static boolean testContainLinkToSo(String text) {
		Set<Integer> soQuestionsIdsInsideTexts = new LinkedHashSet<>();
		List<String> links = getCodeValues(CrarUtils.LINK_PATTERN, text);
		identifyQuestionsIdsFromUrls(links, soQuestionsIdsInsideTexts);
		return !soQuestionsIdsInsideTexts.isEmpty();
	}


	public static void identifyQuestionsIdsFromUrls(List<String> urls, Set<Integer> soQuestionsIds) {
		for(String url: urls){
			if(!url.contains("stackoverflow.com")) {
				//System.out.println("Discarting URL because its is not a SO url: "+url);
				continue;
			}
			//String[] urlPart = url.split("\\/[[:digit:]].*\\/");
			Pattern pattern = Pattern.compile("\\/([\\d]+)");
			Matcher matcher = pattern.matcher(url);
			if (matcher.find()) {
				soQuestionsIds.add(new Integer(matcher.group(1)));
				
			}
			
		}
		
	}
	
	
	public static Integer identifyQuestionIdFromUrl(String url) {
		if(!url.contains("stackoverflow.com")) {
			//System.out.println("Discarting URL because its is not a SO url: "+url);
			return null;
		}
		//String[] urlPart = url.split("\\/[[:digit:]].*\\/");
		Pattern pattern = Pattern.compile("\\/([\\d]+)");
		Matcher matcher = pattern.matcher(url);
		if (matcher.find()) {
			return new Integer(matcher.group(1));
		}
		return null;
		
	}


	public Set<Integer> getStaticIdsForTests() {
		LinkedHashSet<Integer> soPostsIds = new LinkedHashSet<>();
		soPostsIds.add(10117026);
		soPostsIds.add(6416706);
		soPostsIds.add(10117051);
		soPostsIds.add(35593309);
		soPostsIds.add(11018325);
		soPostsIds.add(23329173);
		soPostsIds.add(40064173);
		soPostsIds.add(46107706);
		soPostsIds.add(8174964);
		soPostsIds.add(9740830);
		
		for(Integer id:soPostsIds) {
			System.out.println("id: "+id);
		}
		
		return soPostsIds;
	}


	public void addMapCacheCount(Map<Integer, Integer> map, Integer id) {
		if(map.get(id)==null) {
			map.put(id, 1);
		}else{
			int actualCount = map.get(id);
			map.put(id, actualCount+1);
		}
	}
	
	
	public void buildFileForAgreementPhaseHighlightingDifferences(String evaluationsFileBeforeAgreement, String agreementFile) {
		try {
			FileInputStream excelFile = new FileInputStream(new File(evaluationsFileBeforeAgreement));
			//FileInputStream excelFile = new FileInputStream(new File(agreementFile));
			Workbook workbook = new XSSFWorkbook(excelFile);
			Sheet datatypeSheet = workbook.getSheetAt(0);
			Iterator<Row> iterator = datatypeSheet.iterator();
			iterator.next();
			
			Integer externalQuestionId=0;
			String query=null;
			int differenceCount=0;
			int answers=0;
			while (iterator.hasNext()) {
				Row currentRow = iterator.next();
				Cell currentCellColumnA = currentRow.getCell(0);
				Cell currentCellColumnC = currentRow.getCell(2);
				Cell currentCellColumnD = currentRow.getCell(3);
				
				if(currentCellColumnA!=null && currentCellColumnC==null) {
					
					String currentAValue = currentCellColumnA.getStringCellValue();
					query = currentAValue.trim();
				
				}else {
					if (currentCellColumnC != null && currentCellColumnC.getCellTypeEnum() == CellType.NUMERIC
						&& currentCellColumnD != null && currentCellColumnD.getCellTypeEnum() == CellType.NUMERIC) {
						answers++;
						Integer currentCValue = (int) (currentCellColumnC.getNumericCellValue());
						Integer currentDValue = (int) (currentCellColumnD.getNumericCellValue());
						//System.out.println("Scales: " + currentBValue + " - " + currentCValue);
	
						Cell cellE = currentRow.createCell(4);
						Cell cellF = currentRow.createCell(5);
						
						int diff = Math.abs(currentCValue - currentDValue);
						if(diff>1 && ((currentCValue>3) || (currentDValue>3)) ) {
							cellE.setCellValue("XXX");
							cellF.setCellValue("XXX");
							differenceCount++;
						}else {
							cellE.setCellValue(currentCValue);
							cellF.setCellValue(currentDValue);
						}
						
						
					}
				
				}
			}
			System.out.println("Disagreements: "+differenceCount+ " -out of "+answers+" answers");	
			FileOutputStream outputStream = new FileOutputStream(agreementFile);
	        workbook.write(outputStream);
	        workbook.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	

	public void readGroundTruthFile(List<UserEvaluation> evaluationsWithBothUsersScales, String fileName, Integer firstColumn, Integer secondColumn) {
		try {
			
			FileInputStream excelFile = new FileInputStream(new File(fileName));
			Workbook workbook = new XSSFWorkbook(excelFile);
			Sheet datatypeSheet = workbook.getSheetAt(0);
			Iterator<Row> iterator = datatypeSheet.iterator();
			iterator.next();
			
			Integer externalQuestionId=0;
			String query=null;
			while (iterator.hasNext()) {
				Row currentRow = iterator.next();
				Cell currentCellColumnA = currentRow.getCell(0);
				Cell currentCellColumnB = currentRow.getCell(1);
				Cell firstEvaluationCell = currentRow.getCell(firstColumn);
				Cell secondEvaluationCell = currentRow.getCell(secondColumn);
				
				
				
				if(currentCellColumnA!=null && !StringUtils.isBlank(currentCellColumnA.getStringCellValue()) && (firstEvaluationCell==null || firstEvaluationCell.getNumericCellValue()<1)) {
					
					String currentAValue = currentCellColumnA.getStringCellValue();
					query = currentAValue.trim();
					if(query.contains("given position")) {
						System.out.println();
					}
				
				}else {
					if (firstEvaluationCell != null && firstEvaluationCell.getCellTypeEnum() == CellType.NUMERIC
						&& secondEvaluationCell != null && secondEvaluationCell.getCellTypeEnum() == CellType.NUMERIC) {
						
						String currentBValue = (String) (currentCellColumnB.getStringCellValue());
						Integer currentFirstEvalValue = (int) (firstEvaluationCell.getNumericCellValue());
						Integer currentSecondEvalValue = (int) (secondEvaluationCell.getNumericCellValue());
						
						//System.out.println("Scales: " + currentBValue + " - " + currentCValue);
						Integer postId = identifyQuestionIdFromUrl(currentBValue);
						/*if(postId.equals(7074420)) {
							System.out.println();
						}
						if(postId.equals(9823533)) {
							System.out.println();
						}
						
						if(postId.equals(31307342)) {
							System.out.println();
						}*/
					
						
						UserEvaluation eval = new UserEvaluation();
						eval.setExternalQuestionId(++externalQuestionId);
						eval.setLikertScaleUser1(currentFirstEvalValue);
						eval.setLikertScaleUser2(currentSecondEvalValue);
						eval.setQuery(query);
						eval.setPostId(postId);
						
						evaluationsWithBothUsersScales.add(eval);
					}
				
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	
	
	public void readXlsxToEvaluationList(List<UserEvaluation> evaluationsWithBothUsersScales, String fileName, Integer firstColumn, Integer secondColumn, List<String> queries) {
		try {
	
			FileInputStream excelFile = new FileInputStream(new File(fileName));
			Workbook workbook = new XSSFWorkbook(excelFile);
			Sheet datatypeSheet = workbook.getSheetAt(0);
			Iterator<Row> iterator = datatypeSheet.iterator();
			Integer externalQuestionId=null;
			String query=null;
			while (iterator.hasNext()) {
	
				Row currentRow = iterator.next();
				Cell currentCellColumnA = currentRow.getCell(0);
				Cell currentCellColumnB = currentRow.getCell(firstColumn);
				Cell currentCellColumnC = currentRow.getCell(secondColumn);
				
				if(currentCellColumnA!=null) {
					
					String currentAValue = currentCellColumnA.getStringCellValue();
					Integer postId = identifyQuestionIdFromUrl(currentAValue);
					if(currentAValue.contains("id:")) {
						String parts[] = currentAValue.split("\\|");
						String externalQuestionIdStr = parts[0].replaceAll("\\D+","");
						externalQuestionId = new Integer(externalQuestionIdStr);
						String queryParts[] = currentAValue.split(" - ");
						query = queryParts[1].trim();
						if(queries!=null) {
							queries.add(query);
						}
						
					}
					
					if (currentCellColumnB != null && currentCellColumnB.getCellTypeEnum() == CellType.NUMERIC
						&& currentCellColumnC != null && currentCellColumnC.getCellTypeEnum() == CellType.NUMERIC) {
						
						Integer currentBValue = (int) (currentCellColumnB.getNumericCellValue());
						Integer currentCValue = (int) (currentCellColumnC.getNumericCellValue());
						//System.out.println("Scales: " + currentBValue + " - " + currentCValue);
	
						UserEvaluation eval = new UserEvaluation();
						eval.setExternalQuestionId(externalQuestionId);
						eval.setLikertScaleUser1(currentBValue);
						eval.setLikertScaleUser2(currentCValue);
						eval.setQuery(query);
						eval.setPostId(postId);
						evaluationsWithBothUsersScales.add(eval);
					}
				
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}
	
	
	
	
	public void buildMatrixForKappa(List<UserEvaluation> evaluationsWithBothUsersScales, String fileNameGeneratedMatrix) throws IOException {
		BufferedWriter bw =null;
		try {
		
			bw = new BufferedWriter(new FileWriter(fileNameGeneratedMatrix));
			bw.write(";;1;2;3;4;5");
			
			//Matrix map
			int[] cells[] = new int[5][5];
			
			for(int i=0; i<5; i++) {
				bw.write("\n;"+(i+1)+";");
				for(int j=0; j<5; j++) {
					//System.out.println(cells[i][j]);
					cells[i][j] = getCellNumber(i+1,j+1,evaluationsWithBothUsersScales);
					//System.out.println("cell "+i+"-"+j+"= "+cells[i][j]);
					bw.write(cells[i][j]+";");
				}
			}
			
			
			
		
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			bw.close();
		}
		
	}


	public int getCellNumber(int i, int j, List<UserEvaluation> allEvaluations) {
		int sum=0;
		for(UserEvaluation evaluation: allEvaluations) {
			if(evaluation.getLikertScaleUser1()==i && evaluation.getLikertScaleUser2()==j) {
				sum+=1;				
			}
			
			
		}
		
		return sum;
	}


	public static double computeDCG(final double rel, final int rank) {
		double dcg = 0.0;
		switch (dcgType) {

		case COMB:
			dcg = rel / (Math.log(rank + 1) / Math.log(2));
			break;

		case LIN:
			dcg = rel;
			if (rank > 1) {
				dcg = rel / (Math.log(rank) / Math.log(2));
			}
			break;

		case EXP:
			dcg = (Math.pow(2.0, rel) - 1.0) / ((Math.log(rank + 1) / Math.log(2)));
			break;

		}
	
		
		return round(dcg, 4);
	}
	
	public static double calculateIDCG(final int maxRelevance,int maxRank) {
		double idcg = 0;
		// if can get relevance for every item should replace the relevance score at this point, else
		// every item in the ideal case has relevance of 1
		//int itemRelevance = 1;
		
		for (int posRank = 1; posRank <= maxRank; posRank++){
			idcg += computeDCG(maxRelevance, posRank);
		}
		idcg = round(idcg, 4);
		
		return idcg;
	}
	
	
	public static enum DCG_TYPE {

        /**
         * Linear.
         */
        LIN,
        /**
         * Exponential.
         */
        EXP,
        /*
         * Implemented
         */
        COMB;
		
		
	}
	
	

	public Set<String> extractClassesFromCode(String content) {
		Document doc = Jsoup.parse(content);
		Elements elems = doc.select("code,pre");
		String codeText = elems.text();
		textNormalizer.setContent(codeText);
		return textNormalizer.normalizeSimpleCodeDiscardSmall();
		
	}

	public Set<String> extractClassesFromProcessedCode(String content) {
		textNormalizer.setContent(content);
		return textNormalizer.normalizeSimpleCodeDiscardSmall();
	}

	public static <T> void reduceSetV2(Map<T, Set<Integer>> goldSetQueriesApis, int k) {
		Set<T> keys = goldSetQueriesApis.keySet();
		for(T key: keys) {
			Set<Integer> goldSetApis = goldSetQueriesApis.get(key);
			try {
				setLimitV2(goldSetApis, k);
			} catch (Exception e) {
				System.out.println("Error in reduceSetV2.. ");
			}
			
			
		}
	}

	public static void reduceSet(Map<String, Set<String>> goldSetQueriesApis, int k) {
		Set<String> keys = goldSetQueriesApis.keySet();
		for(String key: keys) {
			Set<String> goldSetApis = goldSetQueriesApis.get(key);
			setLimit(goldSetApis, k);
		}
	}


	public static String loadStream(InputStream s) throws Exception {
		BufferedReader br = new BufferedReader(new InputStreamReader(s));
		StringBuilder sb = new StringBuilder();
		String line;
		while ((line = br.readLine()) != null)
			sb.append(line).append("\n");
		return sb.toString();
	}


	public static void setLimit(Set<String> set, int k) {
		List<String> items = new ArrayList<String>();
	    items.addAll(set);
	    set.clear();
	    int trim = k>items.size() ? items.size() : k;
	    set.addAll(items.subList(0,trim));
		items = null;
	}
	
	public static <K> void setLimitV2(Set<K> set, int k) {
		Set<K> tmp = new LinkedHashSet<>(set);
		
		List<K> items = new ArrayList<K>();
	    items.addAll(set);
	    int trim = k>items.size() ? items.size() : k;
	    items = items.subList(0,trim);
		
	    for(K id: tmp) {
	    	if(!items.contains(id)) {
	    		set.remove(id);
	    	}
	    }
			
		
				
	}


	public static String cleanCode(String body) {
		
		//volta simbolos de marcacao HTML para estado original 
		body = translateHTMLSimbols(body);		
		
		Document doc = Jsoup.parse(body);
		Elements elems = doc.select("code,pre");
		String codeText = elems.text();
		
		body = removeHtmlTags(codeText);
		
		return body;
	}
	

	public static Timestamp getCurrentDate(){
		Timestamp ts_now = new Timestamp(Calendar.getInstance().getTimeInMillis());
		return ts_now;
	}


	public static <K,V> void writeMapToFile(Map<K, Set<V>> map,	String filePath, String separator) throws FileNotFoundException {
		StringBuilder lines = new StringBuilder("");
		Set<K> keys = map.keySet();
		for(K key: keys) {
			lines.append(key+ " "+ separator+" ");
			Set<V> values = map.get(key);
			for(V value: values) {
				lines.append(value+ " ");
			}
			lines.append("\n");
		}
		lines.append("end@");
		String linesStr = lines.toString().replace("\nend@", "");
		try (PrintWriter out = new PrintWriter(filePath)) {
		    out.println(linesStr);
		}
	}
	
	public static <K,V> void writeMapToFile2(Map<K, Set<V>> map, String filePath, String keyValueSeparator,String valuesSeparator) throws FileNotFoundException {
		StringBuilder lines = new StringBuilder("");
		Set<K> keys = map.keySet();
		for(K key: keys) {
			lines.append(key+ keyValueSeparator);
			Set<V> values = map.get(key);
			for(V value: values) {
				lines.append(value+ valuesSeparator);
			}
			lines.append("##\n");
		}
		lines.append("end@");
		String linesStr = lines.toString().replaceAll(valuesSeparator+"##\n", "\n"); 
		linesStr = linesStr.replace("\nend@", "");
		 
		try (PrintWriter out = new PrintWriter(filePath)) {
		    out.println(linesStr);
		}
	}
	
	/*public static void writeMapToFile4(Map<String, Set<Integer>> queriesAndSOIdsMap, String filePath) throws FileNotFoundException {
		StringBuilder lines = new StringBuilder("");
		Set<String> keys = queriesAndSOIdsMap.keySet();
		for(String key: keys) {
			Set<Integer> soQuestionsIds = queriesAndSOIdsMap.get(key);
			lines.append("\n"+key+ " >> ");
			for(Integer id: soQuestionsIds) {
				lines.append(id+ " ");
			}
		}
		String linesStr = lines.toString();
		try (PrintWriter out = new PrintWriter(filePath)) {
		    out.println(linesStr);
		}
	}*/
	
	
	public static void writeMapToFile5(Map<String, double[]> wordsMap, String filePath) throws FileNotFoundException {
		StringBuilder lines = new StringBuilder("");
		Set<String> keys = wordsMap.keySet();
		for(String key: keys) {
			lines.append(key+ " ");
			double[] soWords = wordsMap.get(key);
			for(double word: soWords) {
				lines.append(word+ " ");
			}
			lines.append("\n");
		}
		lines.append("end@");
		String linesStr = lines.toString().replace("\nend@", "");
		try (PrintWriter out = new PrintWriter(filePath)) {
		    out.println(linesStr);
		}
	}
	
	


	public static void writeStringContentToFile(String content, String file) throws IOException {
		FileWriter fw = new FileWriter(file);
		fw.write(content); 
		fw.close();
		
	}


	public static void printMapInfosIntoCVSFile(Map<String, Set<Integer>> mapApis, String file) throws FileNotFoundException {
		StringBuilder lines = new StringBuilder("");
		
		Set<Entry<String, Set<Integer>>> entries = mapApis.entrySet();
		for(Entry<String, Set<Integer>> entry: entries) {
			lines.append(entry.getKey()+ ";");
			Set<Integer> answerIds = entry.getValue();
			lines.append(answerIds.size());
			lines.append("\n");
		}
		lines.append("end@");
		String linesStr = lines.toString().replace("\nend@", "");
		try (PrintWriter out = new PrintWriter(file)) {
		    out.println(linesStr);
		}
		
	}


	


	public static void getVectorsFromLines(List<String> wordsAndVectors, Map<String,double[]> vectorsMap) {
		String[] parts;
		int vecSize=0;
		if(vectorsMap==null) {
			vectorsMap = new LinkedHashMap<>();
		}
		for(String line: wordsAndVectors) {
			parts = line.split(" ");
			vecSize = parts.length;
			int vecCol = 0;
			double[] vectors = new double[vecSize-1];
			for(int i=1;i<vecSize;i++) {
				//vectors[vecCol] = CrarUtils.round(Double.parseDouble(parts[i]),6);
				vectors[vecCol] = Double.parseDouble(parts[i]);
				vecCol++;
			}
			vectorsMap.put(parts[0], vectors);
		}
	}
	
	public static void getVectorsFromLinesParsing(List<String> wordsAndVectors, Map<String,double[]> vectorsMap) {
		String[] parts;
		int vecSize=0;
		if(vectorsMap==null) {
			vectorsMap = new LinkedHashMap<>();
		}
		for(String line: wordsAndVectors) {
			parts = line.split(" ");
			vecSize = parts.length;
			int vecCol = 0;
			double[] vectors = new double[vecSize-1];
			for(int i=1;i<vecSize;i++) {
				vectors[vecCol] = CrarUtils.round4(Double.parseDouble(parts[i]));
				//vectors[vecCol] = Double.parseDouble(parts[i]);
				vecCol++;
			}
			vectorsMap.put(parts[0], vectors);
		}
	}


	public void writeMapToFile(Map<String, Double> idfs, String file) throws FileNotFoundException {
		StringBuilder lines = new StringBuilder("");
		
		Set<String> keys = idfs.keySet();
		for(String key: keys) {
			lines.append(key);
			lines.append(" ");
			lines.append(idfs.get(key));
			lines.append("\n");
		}
		lines.append("end@");
		String linesStr = lines.toString().replace("\nend@", "");
		try (PrintWriter out = new PrintWriter(file)) {
		    out.println(linesStr);
		}
		
	}
	
	public void writeMapToFile2(Map<Integer, String> idsTitles, String file) throws FileNotFoundException {
		StringBuilder lines = new StringBuilder("");
		String[] parts;
		String line;
		Set<Integer> keys = idsTitles.keySet();
		for(Integer key: keys) {
			lines.append(key);
			lines.append(" ");
			line = idsTitles.get(key);
			parts = line.split(" ");
			for(String word: parts) {
				lines.append(word);
				lines.append(" ");
			}
			lines.append("\n");
		}
		lines.append("end@");
		String linesStr = lines.toString().replace("\nend@", "");
		try (PrintWriter out = new PrintWriter(file)) {
		    out.println(linesStr);
		}
		
	}


	public void readSOContentWordAndVectorsLines(List<String> wordsAndVectorsLines, String SO_CONTENT_WORD_VECTORS) throws IOException {
		System.out.println("Reading SO word vectors file...");
		long initTime = System.currentTimeMillis();
		wordsAndVectorsLines.clear();
		wordsAndVectorsLines.addAll(Files.readAllLines(Paths.get(SO_CONTENT_WORD_VECTORS)));
		CrarUtils.reportElapsedTime(initTime,"readSOContentWordAndVectorsLines. Total number of words: "+wordsAndVectorsLines.size());
		
	}
	
	
	
	
	public void readIDFVocabulary(Map<String, Double> soIDFVocabularyMap, String SO_IDF_VOCABULARY) throws Exception {
		if(soIDFVocabularyMap!=null) {
			soIDFVocabularyMap.clear();
			long initTime = System.currentTimeMillis();
			System.out.println("Reading all idfs from file...");
			String[] parts;
			List<String> wordsAndIDFs = Files.readAllLines(Paths.get(SO_IDF_VOCABULARY));
			for(String line: wordsAndIDFs) {
				parts = line.split(" ");
				soIDFVocabularyMap.put(parts[0], Double.parseDouble(parts[1]));
			}
			wordsAndIDFs = null;
			reportElapsedTime(initTime,"readIDFVocabulary");
		}else {
			throw new Exception("IDF vacabulary is null.");
		}
		
	}


	public static double[][] getIDFMatrixForQuery(String query, Map<String, SoContentWordVector> wordVectorsMap) {
		String queryTokens[] = query.trim().split("\\s+");
		double[][] matrix = new double[1][queryTokens.length];
		
		for(int i=0; i<queryTokens.length; i++) {
			String word = queryTokens[i];
			double idfValue = 0d;
			if(wordVectorsMap.get(word)!=null) {
				idfValue = wordVectorsMap.get(word).getIdf();
			}
			matrix[0][i] = idfValue;
		}
				
		return matrix;
	}


	public static double[][] getMatrixVectorsForQuery(String query, Map<String, SoContentWordVector> wordVectorsMap,Integer vectorsTypeId) {
		String queryTokens[] = query.trim().split("\\s+");
		double[][] matrix = new double[queryTokens.length][modelVecSize];
		//int notFound=0;
		
		for(int i=0; i<queryTokens.length; i++) {
			String word = queryTokens[i];
			//double[] vectors = wordVectorsMap.get(word).stream().mapToDouble(Double::doubleValue).toArray();
			double[] vectors = null;
			if(wordVectorsMap.get(word)!=null) {
				vectors=wordVectorsMap.get(word).getFastTextVectorsValues();
			}
			
			if(vectors==null) {
				//System.out.println(" word not found... "+word);
				//notFound++;
				vectors = new double[modelVecSize];
			}
			matrix[i] = vectors;
		}
		/*if(notFound>0) {
			System.out.println("Number of words not found: "+notFound);
		}*/
				
		return matrix;
	}
	
	

	


	public void readWordsFromFileToMap(Map<Integer, String> idsAndContentsMap, List<String> idsAndWords) throws IOException {
		long initTime = System.currentTimeMillis();
		String[] parts;
		String content;
		for(String line: idsAndWords) {
			parts = line.split(" ");
			content = line.replace(parts[0], "").trim();
			idsAndContentsMap.put(Integer.parseInt(parts[0]), content);
		}
		//System.out.println(bigMapApisIds);
		reportElapsedTime(initTime,"readWordsFromFileToMap");
		
	}
	
	public void readWordsFromFileToMap2(Map<Integer, Integer> soIdsIds, List<String> idsAndWords) throws IOException {
		//long initTime = System.currentTimeMillis();
		//System.out.println("Reading all ids and titles from file...");
		String[] parts;
		String title;
		for(String line: idsAndWords) {
			parts = line.split(" ");
			soIdsIds.put(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
		}
		//System.out.println(bigMapApisIds);
		//reportElapsedTime(initTime,"readWordsFromFileToMap2");
		
	}


	public void writeMapToFile3(Map<Integer, Integer> allAnswersIdsParentIdsMap, String file) throws FileNotFoundException {
		StringBuilder lines = new StringBuilder("");
		Set<Integer> keys = allAnswersIdsParentIdsMap.keySet();
		for(Integer key: keys) {
			lines.append(key);
			lines.append(" ");
			lines.append(allAnswersIdsParentIdsMap.get(key));
			lines.append("\n");
		}
		lines.append("end@");
		String linesStr = lines.toString().replace("\nend@", "");
		try (PrintWriter out = new PrintWriter(file)) {
		    out.println(linesStr);
		}
		
	}
	
	public Set<String> getWordsForList(Set<String> listOfWords) {
		Set<String> allWordsSet = new LinkedHashSet<>();
		String words[];
		for(String query: listOfWords) {
			words = query.split("\\s+");
			allWordsSet.addAll(Arrays.asList(words));
		}
		allWordsSet.remove("");
		return allWordsSet;
	}


	public static <T, E> Set<T> getKeysByValue(Map<T, E> map, E value) {
	    return map.entrySet()
	              .stream()
	              .filter(entry -> Objects.equals(entry.getValue(), value))
	              .map(Map.Entry::getKey)
	              .collect(Collectors.toSet());
	}


	public static <K, V extends Comparable<? super V>> List<Entry<K, V>> sortMapByValueDescending(Map<K, V> map) {

		List<Entry<K, V>> sortedEntries = new ArrayList<Entry<K, V>>(map.entrySet());

		Collections.sort(sortedEntries, new Comparator<Entry<K, V>>() {
			@Override
			public int compare(Entry<K, V> e1, Entry<K, V> e2) {
				return e2.getValue().compareTo(e1.getValue());
			}
		});

		return sortedEntries;
	}
	
	public static double log2(int n)
	{
	    return (Math.log(n) / Math.log(2));
	}
	
	public void buildCsvQuestionsForEvaluation(String fileName,Map<String, List<Post>> allQueriesAndUpVotedCodedAnswersMap) throws IOException {
		BufferedWriter bw =null;
		try {
			bw = new BufferedWriter(new FileWriter(fileName));
			bw.write("Parent Title;Link;Rodrigo;Klerisson;Rodrigo agreement;Klerisson agreement\n\n");
			Set<String> queries = allQueriesAndUpVotedCodedAnswersMap.keySet(); 
						
			for(String query:queries){
				
				bw.write("\n"+query+"\n\n");
				List<Post> answers = allQueriesAndUpVotedCodedAnswersMap.get(query);					
				for(Post answer: answers) {
					String title = answer.getParent().getTitle().replaceAll(";", "");
					title = title.replaceAll("\"", "");
					title = title.replaceAll("\'", "");
					bw.write(title+";");
					bw.write("https://stackoverflow.com/questions/"+answer.getId()+"/\n");
				}
				
				bw.write("\n\n");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			bw.close();
		}
		
	}

	
	public static Map<String,Set<Integer>> readQueriesAndIds(String fileToRead) throws IOException {
		//long initTime = System.currentTimeMillis();
		Map<String,Set<Integer>> queriesAndSOIdsMap = new LinkedHashMap<>();
		List<String> queriesAndGoogleSOIds = Files.readAllLines(Paths.get(fileToRead));
		String[] parts;
		int nroAnswers = 0;
		for(String line: queriesAndGoogleSOIds) {
			parts = line.split(">>");
			if(parts.length>1) {
				String ids[]=null;
				ids = parts[1].split(" ");
				Set<Integer> idsInt = new LinkedHashSet<>();
				for(String idStr: ids) {
					if(!StringUtils.isBlank(idStr)) {
						idsInt.add(Integer.parseInt(idStr.trim()));
					}
				}
				nroAnswers+=idsInt.size();
				queriesAndSOIdsMap.put(parts[0].trim(), idsInt);
			}
			
		}
		
		//reportElapsedTime(initTime,"readQueriesAndIds");
		//System.out.println("Number of queries read: "+queriesAndSOIdsMap.size() +" for file: "+fileToRead+ "\nNumber of answers: "+nroAnswers);
		return queriesAndSOIdsMap;
	}
	
	public static Map<String,Set<Integer>> readQueriesAndIdsK(String fileToRead, int k) throws IOException {
		//long initTime = System.currentTimeMillis();
		
		Map<String,Set<Integer>> queriesAndSOIdsMap = new LinkedHashMap<>();
		List<String> queriesAndGoogleSOIds = Files.readAllLines(Paths.get(fileToRead));
		String[] parts;
		int nroAnswers = 0;
		for(String line: queriesAndGoogleSOIds) {
			parts = line.split(">>");
			int count=0;
			if(parts.length>1) {
				String ids[]=null;
				ids = parts[1].split(" ");
				Set<Integer> idsInt = new LinkedHashSet<>();
				for(String idStr: ids) {
					if(!StringUtils.isBlank(idStr)) {
						idsInt.add(Integer.parseInt(idStr.trim()));
					}
					if(count==k) {
						break;
					}
					count++;
				}
				nroAnswers+=idsInt.size();
				queriesAndSOIdsMap.put(parts[0].trim(), idsInt);
			}
			
		}
		
		//reportElapsedTime(initTime,"readQueriesAndIds");
		//System.out.println("Number of queries read: "+queriesAndSOIdsMap.size() +" for file: "+fileToRead+ "\nNumber of answers: "+nroAnswers);
		return queriesAndSOIdsMap;
	}
	
	
	public static Map<Integer,Set<String>> readPostsIdsApisMap(String fileToRead) throws IOException {
		long initTime = System.currentTimeMillis();
		Map<Integer,Set<String>> postsIdsApisMap = new LinkedHashMap<>();
		List<String> lines = Files.readAllLines(Paths.get(fileToRead));
		String[] parts;
		for(String line: lines) {
			parts = line.split(" >> ");
			if(parts.length>1) {
				String apis[]=null;
				apis = parts[1].split(" ");
				Set<String> apisStr = new LinkedHashSet<>();
				for(String idStr: apis) {
					apisStr.add(idStr);
				}
				postsIdsApisMap.put(Integer.parseInt(parts[0]), apisStr);
			}
			
		}
		
		reportElapsedTime(initTime,"readPostsIdsApisMap");
		System.out.println("Size of postsIdsApisMap: "+postsIdsApisMap.size() +" for file: "+fileToRead);
		return postsIdsApisMap;
	}


	/*
	public static Map<String,Set<Integer>> readFileToMap(String fileToRead) throws IOException {
		long initTime = System.currentTimeMillis();
		Map<String,Set<Integer>> contentMap = new LinkedHashMap<>();
		List<String> lines = Files.readAllLines(Paths.get(fileToRead));
		String[] parts;
		for(String line: lines) {
			parts = line.split(":");
			if(parts.length>1) {
				String ids[]=null;
				ids = parts[1].split(" ");
				Set<Integer> intContent = new LinkedHashSet<>();
				for(String idStr: ids) {
					try {
						intContent.add(new Integer(idStr.trim()));
					} catch (Exception e) {
						System.out.println();
					}
					
				}
				contentMap.put(parts[0], intContent);
			}
			
		}
		
		reportElapsedTime(initTime,"readFileToMap");
		System.out.println("Size of readFileToMap: "+contentMap.size() +" for file: "+fileToRead);
		return contentMap;
	}*/

	public static void composeAnswers(String folder, Map<String, Set<Post>> sortedBuckets, Integer numberOfComposedAnswers) throws FileNotFoundException {
		File dir = new File(folder);
		if(!dir.exists()) {
			dir.mkdirs();
		}
		
		
		Set<String> queries = sortedBuckets.keySet();
				
		for(String query:queries) {
			
			Set<Post> posts = sortedBuckets.get(query);
			setLimitV2(posts, numberOfComposedAnswers);
			StringBuilder lines = new StringBuilder("");
			lines.append("Query: "+query+"\n-----------------------------------------------------------------------");
			
			
			//System.out.println("\nAnswers to query: "+query);
			int count=1;
			for(Post bucket: posts) {
				lines.append("\n\nRank:"+count+" (https://stackoverflow.com/questions/"+bucket.getId()+")\n"+buildPresentationBody(bucket.getBody(),true)+"\n\n-------------------------------------next answer-------------------------------------");
				count++;
			}
			lines.append("#end");
			String linesStr = lines.toString();
			linesStr = linesStr.replace("-------------------------------------next answer-------------------------------------#end", "");
			try (PrintWriter out = new PrintWriter(folder+"/"+query.replace("/", "")+".txt")) {
			    out.println(linesStr);
			}
			
			
			
		}
		
	}
	
	
	public static <T> Map<T, Set<Integer>> copy(Map<T, Set<Integer>> original)
	{
		Map<T, Set<Integer>> copy = new LinkedHashMap<T, Set<Integer>>();
	    for (Map.Entry<T, Set<Integer>> entry : original.entrySet())
	    {
	    	LinkedHashSet newSet = new LinkedHashSet<Integer>();
	    	newSet.addAll(entry.getValue());
	        copy.put(entry.getKey(), newSet);
	    }
	    return copy;
	}
	
	public static <T> Map<T, Set<Integer>> copyk(Map<T, Set<Integer>> original, int topk)
	{
		
		Map<T, Set<Integer>> copy = new LinkedHashMap<T, Set<Integer>>();
	    for (Map.Entry<T, Set<Integer>> entry : original.entrySet())
	    {
	    	LinkedHashSet newSet = new LinkedHashSet<Integer>();
	    	int count=0;
	    	Set<Integer> originalRecommendations = entry.getValue();
	    	for(Integer id: originalRecommendations) {
	    		newSet.add(id);
		        count++;
	 	        if(count==topk) {
	 	        	break;
	 	        }
	    	}
	    	copy.put(entry.getKey(), newSet);
	    	
	    	
	       
	    }
	    return copy;
	}

	public Boolean processSentences(Post post, String query) {
		
		Set<String> queryValidWords = getProcessedWords(query);
		String body=post.getBody();
		removedSentences.clear();
		String processedBody = extractSentences(post.getBody());
		if(StringUtils.isBlank(processedBody)) {
			return false;
		}
		CoreDocument document = new CoreDocument(processedBody);
	    // annnotate the document
	    pipeline.annotate(document);
	    
	    List<CoreSentence> sentences = document.sentences();
	    for(CoreSentence eachSentence: sentences) {
	    	//System.out.println("Sentence: "+eachSentence);
	    	
	    	Tree constituencyParseTree = eachSentence.constituencyParse();
	    	/*System.out.println("Example: constituency parse");
	    	System.out.println(constituencyParseTree);*/
	    	
	    	TregexMatcher matcher1 = sentencePattern1.matcher(constituencyParseTree);
	    	TregexMatcher matcher2 = sentencePattern2.matcher(constituencyParseTree);
	    	
	    	// Iterate over all of the subtrees that matched
	    	if (!matcher1.findNextMatchingNode() && !matcher2.findNextMatchingNode()) {
	    		
	    		String sentenceText = eachSentence.text();
	    		Set<String> processedSentenceWords = getProcessedWords(sentenceText);
	    		
	    		if(!containNumber(sentenceText)                     //does not remove if sentence contain any number.
	    			&& !TextNormalizer.isCamelCase(sentenceText)	//does not remove if sentence contains camelCase words (refer to a method or library)
	    		    && !containImportantWords(processedSentenceWords) //does not remove if sentence contain any special word.
	    			&& !containCommonWords(processedSentenceWords,queryValidWords) //does not remove if sentence contain any word present in query.
	    			) {
	    			/*if(post.getId()==30281392) {
	    				System.out.println();
	    			}*/
	    			
	    			sentenceText = sentenceText.replace(".", "");
	    			sentenceText = sentenceText.trim();
	    			if(!StringUtils.isEmpty(sentenceText)) {
	    				removedSentences.add(eachSentence);
	    				body = body.replace(sentenceText, "");
	    			}
	    			post.setBody(body);
	    		}
	    		processedSentenceWords=null;
	      		//voltar links contendo textos dos links e os links , ex: 49655398
	    		
	    	}
	    }
	    
	    if(!removedSentences.isEmpty()) {
	    	System.out.println("Removed sentences from postId:"+post.getId());
		    for(CoreSentence eachSentence: removedSentences) {
		    	System.out.println("Sentence: "+eachSentence);
		    	processedBody = processedBody.replace(eachSentence.text(), "");
		    }
	    }
	    
	    String cleanedBody = removeAllPunctuations(processedBody.trim());
	    boolean isValid = !StringUtils.isEmpty(cleanedBody.trim());
	    if(!isValid) {
	    	System.out.println("Disconsidered answer : "+post.getId());
	    }
	    
	    return isValid;
	}

	public boolean containCommonWords(Set<String> processedSentenceWords, Set<String> queryValidWords) {
		Set<String> intersection = new LinkedHashSet<String>(processedSentenceWords); 
		intersection.retainAll(queryValidWords);
		boolean contain = !intersection.isEmpty();
		intersection = null;
		return contain;
	}

	public boolean containImportantWords(Set<String> processedSentenceWords) {
		/*Set<String> intersection = new LinkedHashSet<String>(processedSentenceWords); // use the copy constructor
		intersection.retainAll(importantWordsList);
		boolean contain = !intersection.isEmpty();
		intersection = null;
		return contain;*/
		String sentence = String.join(" ", processedSentenceWords);
		
		for(String importantWord: importantWordsList) {
			if(sentence.contains(importantWord)) {
				return true;
			}
		}
		return false;
		
	}

	

	public static double getSimPair(String comparingContent, String processedQuery, Map<String, SoContentWordVector> wordVectorsMap, Integer vectorsTypeId) {
		if(StringUtils.isBlank(comparingContent)) {
			return 0d;
		}
		
		/*if(processedQuery.contains("glassfish methodexecutor exception") && comparingContent.equals(anObject)) {
			
		}*/
		
		//get vectors for query words
		double[][] matrix1 = getMatrixVectorsForQuery(processedQuery,wordVectorsMap,vectorsTypeId);
		
		//get idfs for query
		double[][] idf1 = getIDFMatrixForQuery(processedQuery, wordVectorsMap);
	
		//get vectors for query2 words
		double[][] matrix2 = getMatrixVectorsForQuery(comparingContent,wordVectorsMap,vectorsTypeId);
		
		//get idfs for query2
		double[][] idf2 = getIDFMatrixForQuery(comparingContent, wordVectorsMap);
		
		double simPair = 0;
		
		try {
			//simPair = CrarUtils.round5(Matrix.simDocPair(matrix1,matrix2,idf1,idf2));
			simPair = Matrix.simDocPair(matrix1,matrix2,idf1,idf2);
		} catch (Exception e) {
			System.out.println("error in getSimPair...");
		}
		
		
		return simPair;
	}

	public static String prettify(String json_text) {
	    JsonParser parser = new JsonParser();
	    JsonObject json = parser.parse(json_text).getAsJsonObject();
	    Gson gson = new GsonBuilder().setPrettyPrinting().create();
	    return gson.toJson(json);
	}

	public static String extractSentences(String body) {
		body = translateHTMLSimbols(body);
		body = body.replaceAll(IMG_EXPRESSION_OUT, " ");
		body = extractLinksTargets(body);
		body = body.replaceAll(PRE_CODE_REGEX_EXPRESSION, ". ");
		//body = body.replaceAll(CODE_MIN_REGEX_EXPRESSION, ". ");
		body = removeHtmlTags(body);
		body = StringUtils.normalizeSpace(body);
		return body.trim();
	}
	
	
	public static Boolean hasIntersection(Set<Integer> set1, Set<Integer> set2) {
		int numIntersections = Sets.intersection(set1, set2).size(); 
		if(numIntersections>=hotTopicsIntersectionCutOff) {
			return true;
		}
		
		return false;
	}

	public static void setHotTopicsIntersectionCutOff(Integer hotTopicsIntersectionCutOff) {
		CrarUtils.hotTopicsIntersectionCutOff = hotTopicsIntersectionCutOff;
	}

	public static void readHotTopics(Set<Bucket> bucketsAndTopics) {
		for(Bucket bucket: bucketsAndTopics) {
			
			//Map<Integer,Double> numTopicValue = new LinkedHashMap<>();
			
			String[] parts;
			int vecSize=0;
				
			String line= bucket.getTopics();
			parts = line.split(" ");
			vecSize = parts.length;
			
			
			String hotTopicsStr="";
			int vecCol = 0;
			int topic=0;
			double topicValue=0;
			
			//double[] vectors = new double[vecSize-1];
			int count=0;
			
			int i=0;
			int j=1;
			while(j<=vecSize) {
				topic=Integer.parseInt(parts[i]);
				topicValue=CrarUtils.round5(Double.parseDouble(parts[j]));
				
				if(topicValue==0) {
					break;
				}
				
				hotTopicsStr+=topic+":"+topicValue+" ";
				count++;
				
				i=i+2;
				j=j+2;
			}
			
			bucket.setHotTopics(hotTopicsStr);
		}
		
	}
	
	public static void generateHotTopicsMap(Collection<Bucket> buckets) {
		String parts[]=null;
		
		for(Bucket bucket: buckets) {
			parts = bucket.getHotTopics().split(" ");
			int topic=0;
			double topicValue=0;
			Map<Integer,Double> topics = new LinkedHashMap<>();
			
			for(String word: parts) {
				topic=Integer.parseInt(word.split(":")[0]);
				topicValue=Double.parseDouble(word.split(":")[1]);
				topics.put(topic, topicValue);
			}
			bucket.setHotTopicsIdValueMap(topics);
			/*if(bucket.getId().equals(35009350)) {
				System.out.println("here");
			}*/
		}
		
	}
	
	
	
	
	public static Map<Integer,Double> generateHotTopicsMap(String topicStr) {
		String[] parts;
		int vecSize=0;
			
		parts = topicStr.split(" ");
		vecSize = parts.length;
		
		int topic=0;
		double topicValue=0;
		
		Map<Integer,Double> topics = new TreeMap<>();
		
		for(int i=0; i<vecSize; i++) {
			topic=Integer.parseInt(parts[i].split(":")[0]);
			topicValue=Double.parseDouble(parts[i].split(":")[1]);
			topics.put(topic, topicValue);
		}
		
		return topics;
		
	}
	
	public static void generateTopicsMap(Set<Bucket> buckets) {
		String[] parts;
		int vecSize=0;
			
		for(Bucket bucket: buckets) {
			parts = bucket.getTopics().split(" ");
			vecSize = parts.length;
			int topic=0;
			double topicValue=0;
			
			int i=0;
			int j=1;
			
			
			Map<Integer,Double> topics = new TreeMap<>();
			
			while(j<=vecSize) {
				topic=Integer.parseInt(parts[i]);
				topicValue=CrarUtils.round5(Double.parseDouble(parts[j]));
				
				i=i+2;
				j=j+2;
				
				topics.put(topic, topicValue);
			}
			bucket.setTopicsIdValueMap(topics);
		
		}		
		
	}
	
	public static Map<Integer,Double> generateTopicsMap(String topicStr) {
		String[] parts;
		int vecSize=0;
			
		parts = topicStr.split(" ");
		vecSize = parts.length;
		
		int topic=0;
		double topicValue=0;
		
		int i=0;
		int j=1;
		Map<Integer,Double> topics = new TreeMap<>();
		
		while(j<=vecSize) {
			topic=Integer.parseInt(parts[i]);
			topicValue=CrarUtils.round5(Double.parseDouble(parts[j]));
			
			i=i+2;
			j=j+2;
			
			topics.put(topic, topicValue);
		}
		
		return topics;
		
	}
	

	public static void generateTopScoredAnswers(Set<Bucket> allUpvotedScoredQuestions) {
		for(Bucket bucket: allUpvotedScoredQuestions) {
			String answersStr="";
			if(bucket.getAnswers()!=null) {
				for(Bucket children: bucket.getAnswers()) {
					answersStr+=children.getId()+ " ";
				}
				bucket.setTopScoredAnswers(answersStr);
			}
			
		}
		
	}
	
	
	/*public static void generateTopScoredAnswersChildren(Set<Bucket> allUpvotedScoredQuestions) {
		String parts[]=null;
		Set<Integer> answersIds = new LinkedHashSet<>();
		
		for(Bucket bucket: allUpvotedScoredQuestions) {
			if(StringUtils.isBlank(bucket.getTopScoredAnswers())) {
				continue;
			}
			parts = bucket.getTopScoredAnswers().split(" ");
			for(String word: parts) {
				answersIds.add(Integer.parseInt(word));
			}
			bucket.setTopScoredAnswersSet(answersIds);
		}
		
	}*/

	public static void generateChildrenSet(Set<Bucket> buckets) {
		String parts[]=null;
		for(Bucket bucket: buckets) {
			if(!StringUtils.isBlank(bucket.getTopScoredAnswers())) {
				Set<Integer> topScoredAnswersSet = new LinkedHashSet<>();
				parts = bucket.getTopScoredAnswers().split(" ");
				for(String answerId: parts) {
					topScoredAnswersSet.add(Integer.parseInt(answerId));
				}
				bucket.setTopScoredAnswersSet(topScoredAnswersSet);
			}
		}
		
	}

	public static void removePossibleDupesFromBodies(Set<Bucket> possibleDupesBodiesBuckets) {
		for(Bucket bucket: possibleDupesBodiesBuckets) {
			if(bucket.getProcessedBody().startsWith("possible duplicate")) {
				bucket.setProcessedBody(bucket.getProcessedBody().replace("possible duplicate ", " "));
				bucket.setProcessedBodyLemma(bucket.getProcessedBodyLemma().replace("possible duplicate ", " "));
			}
		}
		
	}

	
	public static void buildTopicVectors(Map<Integer,String> postsTopics, String TOPIC_DISTRIBUTION_FILE_NAME) throws Exception {
		FileReader fileReader = null;
		BufferedReader bufferedReader = null;
		
		try {
			logger.info("Building vectors to all questions - reading files and saving vectors in strings");

			File file = new File(TOPIC_DISTRIBUTION_FILE_NAME);
			// int count;
			Integer id = null;
			String line;
			String vectors = "";
						
			fileReader = new FileReader(file);
			bufferedReader = new BufferedReader(fileReader);
			String parts[] = null;
			while ((line = bufferedReader.readLine()) != null) {
				Pattern pattern = Pattern.compile("questionId-(.*?)-");
				Matcher matcher = pattern.matcher(line);
				if (matcher.find()) {
					try {
						id = new Integer(matcher.group(1));
						parts = line.split("-topic.txt ");
						//parts[1] = parts[1].replace("\t", " ");
						
						if (parts.length == 2) {
							vectors = parts[1];
						} else {
							logger.warn("Warning... Empty vector for id: " + id);
						}
								
					} catch (Exception e) {
						logger.error("Error :Id: " + id + ": " + vectors + " --\n" + "\n" + e.getMessage() + "\n");
						e.printStackTrace();
						throw e;
					}

					postsTopics.put(id, vectors);
				} else {
					if(!line.startsWith("#doc")) {
						throw new Exception("Id not found....: " + line+ " -id: "+id);
					}
					
				}

			}

			//reduceVectors(topicVectors);

		} catch (Exception e) {
			logger.error("Error in buildTopicVectors: " + e.getMessage() + " --- \n" + e.getStackTrace());
			e.printStackTrace();
			throw e;
		} finally {
			bufferedReader.close();
			fileReader.close();
		}
		
	}

	public static Set<Pair> readPairsWithZeroProximityFromFile(String file) throws IOException {
		Set<Pair> pairs = new LinkedHashSet<>();
		String[] parts;
		List<String> lines = Files.readAllLines(Paths.get(file));
		
		for(int i=1; i<lines.size(); i++) { //skip header
			parts = lines.get(i).split(" ");
			Pair pair = new Pair();
			pair.setPostId1(Integer.parseInt(parts[1]));
			pair.setPostId2(Integer.parseInt(parts[2]));
			pairs.add(pair);
		}
		System.out.println("Number of pairs read: "+pairs.size());
		return pairs;
		
	}
	
	
	public static void checkCoverage(Set<Bucket> rawClosedDuplicatedNonMasters, Set<Bucket> allUpvotedScoredQuestions, Map<Integer, Set<Integer>> allPostsLinks) {
		 boolean coverage = true;
		 Set<Integer> notCovered = new LinkedHashSet<>();
		 Set<Integer> notCoveredMaster = new LinkedHashSet<>();
		 
		 for(Bucket nonMaster: rawClosedDuplicatedNonMasters) {
			 Set<Integer> masters = allPostsLinks.get(nonMaster.getId());
			 if(masters==null || masters.isEmpty()) {
				 //System.out.println("Not covered: "+nonMaster.getId());
				 coverage = false;
				 notCovered.add(nonMaster.getId());
			 }
			 for(Integer masterId: masters) {
				 Bucket masterBucket = new Bucket();
				 masterBucket.setId(masterId);
				 if(!allUpvotedScoredQuestions.contains(masterBucket)) {
					 coverage = false;
					 notCoveredMaster.add(masterId);
				 }
				 masterBucket=null;
			 }
			 masters=null;
		 }
		 System.out.println("Not covered: "+notCovered);
		 System.out.println("Not covered master size: "+notCoveredMaster.size()+ " - "+notCoveredMaster);
		 
		 System.out.println("Coverage asserted: "+coverage);
		
	}
	
	
	public static <T> Stream<List<T>> batches(List<T> source, int length) {
	    if (length <= 0)
	        throw new IllegalArgumentException("length = " + length);
	    int size = source.size();
	    if (size <= 0)
	        return Stream.empty();
	    int fullChunks = (size - 1) / length;
	    return IntStream.range(0, fullChunks + 1).mapToObj(n -> source.subList(n * length, n == fullChunks ? size : (n + 1) * length));
	}
	
	
	public static double calculateCoverage(String string1, String string2) throws IOException {
		Set<String> words1 = Arrays.stream(string1.split(" +")).collect(Collectors.toSet());
		Set<String> words2 = Arrays.stream(string2.split(" +")).collect(Collectors.toSet());
		
		int a = words1.size();
		
		words1.retainAll(words2);
		int intersect = words1.size();
		
		return((double) intersect / (a));
	}
	
	public static double calculateExactJaccard(String string1, String string2) throws IOException {
		Set<String> words1 = Arrays.stream(string1.split(" +")).collect(Collectors.toSet());
		Set<String> words2 = Arrays.stream(string2.split(" +")).collect(Collectors.toSet());
		
		int a = words1.size();
		int b = words2.size();
		
		words1.retainAll(words2);
		int intersect = words1.size();
		
		return((double) intersect / (a + b - intersect));
	}
	
	public static void generateNGrams(GenericBucket queryGramBucket,List<Collection<String>> candidateDocumentGrams) {
		//Bucket queryGramBucket = new Bucket();
		List<String> twoGrams = new ArrayList<>();
        List<String> threeGrams = new ArrayList<>();
        List<String> fourGrams = new ArrayList<>();
        
        for(Collection<String> ngram : candidateDocumentGrams) {
        	for(String token: ngram) {
        		
        		/*CoreDocument document = new CoreDocument(token);
        	    pipeline.annotate(document);
        		CoreSentence sentence = document.sentences().get(0);
        	    List<String> posTags = sentence.posTags();
        	    
        	    if(!posTags.contains("VB")) { //consider only tokens containing a verb
        	    	continue;
        	    }*/
        		
        		String queryTokens[] = token.trim().split("\\s+");
        		if(queryTokens.length==2) {
            		twoGrams.add(token);
            	}else if(queryTokens.length==3) {
            		threeGrams.add(token);
            	}else {
            		fourGrams.add(token);
            	}
        		/*document=null;
        		sentence=null;
        		posTags=null;*/
        		
        	}
        }
        queryGramBucket.setNGrams(twoGrams, threeGrams, fourGrams);
		//return queryGramBucket;
	}

	public static void countCommonNGrams(GenericBucket queryGramBucket, GenericBucket candidateDocumentGramBucket) {
	    Collection<String> intersectionTwoGrams = CollectionUtils.intersection(queryGramBucket.getTwoGrams(), candidateDocumentGramBucket.getTwoGrams());
	    Collection<String> intersectionThreeGrams = CollectionUtils.intersection(queryGramBucket.getThreeGrams(), candidateDocumentGramBucket.getThreeGrams());
	    Collection<String> intersectionFourGrams = CollectionUtils.intersection(queryGramBucket.getFourGrams(), candidateDocumentGramBucket.getFourGrams());
    	
	    candidateDocumentGramBucket.setThreeGramsCount(intersectionThreeGrams.size());
	    candidateDocumentGramBucket.setTwoGramsCount(intersectionTwoGrams.size());
	    candidateDocumentGramBucket.setFourGramsCount(intersectionFourGrams.size());
	    
	    /*if(intersectionThreeGrams.size()>0) {
	    	System.out.println("Three grams found: "+intersectionThreeGrams.size()+" - "+intersectionThreeGrams.iterator().next());
	    }
	    if(intersectionFourGrams.size()>0) {
	    	System.out.println("Four grams found: "+intersectionFourGrams.size()+" - "+intersectionFourGrams.iterator().next());
	    }*/
	    
	    intersectionTwoGrams=null;
	    intersectionThreeGrams=null;
	    intersectionFourGrams=null;
	}
	
	
	public static void writeFeatureRowThreads(ThreadContent threadContent, BufferedWriter bw, Integer pos, Integer threadId) throws IOException {
		
		StringBuilder str = new StringBuilder();
		
		str.append(";https://stackoverflow.com/questions/"+threadId+";"+pos+";");
		
		if(threadContent.getThreadFinalScore()!=null) {
			str.append(CrarUtils.round(threadContent.getThreadFinalScore(),2));
		}else {
			str.append("-1");
		}
		str.append(";");
		
		if(threadContent.getThreadTitleAsymmetricSim()!=null) {
			str.append(CrarUtils.round(threadContent.getThreadTitleAsymmetricSim(),2));
		}else {
			str.append("-1");
		}
		str.append(";");
		
		if(threadContent.getThreadTitleAsymmetricSimNormalized()!=null) {
			str.append(CrarUtils.round(threadContent.getThreadTitleAsymmetricSimNormalized(),2));
		}else {
			str.append("-1");
		}
		str.append(";");
		
		if(threadContent.getThreadQuestionBodyAnswersBodyAsymmetricSim()!=null) {
			str.append(CrarUtils.round(threadContent.getThreadQuestionBodyAnswersBodyAsymmetricSim(),2));
		}else {
			str.append("-1");
		}
		str.append(";");
		
		if(threadContent.getThreadQuestionBodyAnswersBodyAsymmetricSimNormalized()!=null) {
			str.append(CrarUtils.round(threadContent.getThreadQuestionBodyAnswersBodyAsymmetricSimNormalized(),2));
		}else {
			str.append("-1");
		}
		str.append(";");
		
		if(threadContent.getQueryTitleCoverageScore()!=null) {
			str.append(CrarUtils.round(threadContent.getQueryTitleCoverageScore(),2));
		}else {
			str.append("-1");
		}
		str.append(";");
		
		if(threadContent.getQueryCodeCoverageScore()!=null) {
			str.append(CrarUtils.round(threadContent.getQueryCodeCoverageScore(),2));
		}else {
			str.append("-1");
		}
		str.append(";");
		
		if(threadContent.getJaccardScore()!=null) {
			str.append(CrarUtils.round(threadContent.getJaccardScore(),2));
		}else {
			str.append("-1");
		}
		str.append(";");
		
		if(threadContent.getTwoGramsCount()!=null) {
			str.append(threadContent.getTwoGramsCount());
		}else {
			str.append("-1");
		}
		str.append(";");
		
		if(threadContent.getThreeGramsCount()!=null) {
			str.append(threadContent.getThreeGramsCount());
		}else {
			str.append("-1");
		}
		str.append(";");
		
		if(threadContent.getBm25Score()!=null) {
			str.append(CrarUtils.round(threadContent.getBm25Score(),2));
		}else {
			str.append("-1");
		}
		str.append(";");
		
		if(threadContent.getBm25ScoreNormalized()!=null) {
			str.append(CrarUtils.round(threadContent.getBm25ScoreNormalized(),2));
		}else {
			str.append("-1");
		}
		str.append(";");
		
		if(threadContent.getTfIdfCosineSimScore()!=null) {
			str.append(CrarUtils.round(threadContent.getTfIdfCosineSimScore(),2));
		}else {
			str.append("-1");
		}
		str.append(";");
		
		if(threadContent.getTfIdfCosineSimScoreNormalized()!=null) {
			str.append(CrarUtils.round(threadContent.getTfIdfCosineSimScoreNormalized(),2));
		}else {
			str.append("-1");
		}
		str.append(";");
		
		if(threadContent.getAnswerCount()!=null) {
			str.append(threadContent.getAnswerCount());
		}else {
			str.append("-1");
		}
		str.append(";");
		
		if(threadContent.getThreadAnswerCountScoreNormalized()!=null) {
			str.append(CrarUtils.round(threadContent.getThreadAnswerCountScoreNormalized(),2));
		}else {
			str.append("-1");
		}
		str.append(";");
		
		if(threadContent.getThreadUpVotesScore()!=null) {
			str.append(CrarUtils.round(threadContent.getThreadUpVotesScore(),2));
		}else {
			str.append("-1");
		}
		str.append(";");
		
		if(threadContent.getThreadTotalUpvotes()!=null) {
			str.append(threadContent.getThreadTotalUpvotes());
		}else {
			str.append("-1");
		}
		str.append(";");
		
		if(threadContent.getThreadTotalUpVotesScoreNormalized()!=null) {
			str.append(CrarUtils.round(threadContent.getThreadTotalUpVotesScoreNormalized(),2));
		}else {
			str.append("-1");
		}
		str.append(";");
		
		
		
		str.append("\n");
		bw.write(str.toString());
				
		
	}
	
	public static void writeFeatureRowAnswers(Bucket bucket, BufferedWriter bw, Integer pos, Integer answerId) throws IOException {
		
		StringBuilder str = new StringBuilder();
		
		str.append(";https://stackoverflow.com/questions/"+answerId+";"+pos+";");
		
		if(bucket.getFinalScore()!=null) {
			try {
				str.append(CrarUtils.round(bucket.getFinalScore(),2));
			} catch (Exception e) {
				System.out.println(bucket.getId());
			}
			
		}else {
			str.append("-1");
		}
		str.append(";");
		
		if(bucket.getThreadSim()!=null) {
			str.append(CrarUtils.round(bucket.getThreadSim(),2));
		}else {
			str.append("-1");
		}
		str.append(";");
		
		if(bucket.getSimPair()!=null) {
			str.append(CrarUtils.round(bucket.getSimPair(),2));
		}else {
			str.append("-1");
		}
		str.append(";");
		
		if(bucket.getSimPairNormalized()!=null) {
			str.append(CrarUtils.round(bucket.getSimPairNormalized(),2));
		}else {
			str.append("-1");
		}
		str.append(";");
		
		if(bucket.getJaccardScore()!=null) {
			str.append(CrarUtils.round(bucket.getJaccardScore(),2));
		}else {
			str.append("-1");
		}
		str.append(";");
		
		if(bucket.getMethodScore()!=null) {
			str.append(CrarUtils.round(bucket.getMethodScore(),2));
		}else {
			str.append("-1");
		}
		str.append(";");
		
		if(bucket.getQueryCoverageScore()!=null) {
			str.append(CrarUtils.round(bucket.getQueryCoverageScore(),2));
		}else {
			str.append("-1");
		}
		str.append(";");
		
		if(bucket.getBm25Score()!=null) {
			str.append(CrarUtils.round(bucket.getBm25Score(),2));
		}else {
			str.append("-1");
		}
		str.append(";");
		
		if(bucket.getBm25ScoreNormalized()!=null) {
			str.append(CrarUtils.round(bucket.getBm25ScoreNormalized(),2));
		}else {
			str.append("-1");
		}
		str.append(";");
		
		if(bucket.getTfIdfCosineSimScore()!=null) {
			str.append(CrarUtils.round(bucket.getTfIdfCosineSimScore(),2));
		}else {
			str.append("-1");
		}
		str.append(";");
		
		if(bucket.getTfIdfCosineSimScoreNormalized()!=null) {
			str.append(CrarUtils.round(bucket.getTfIdfCosineSimScoreNormalized(),2));
		}else {
			str.append("-1");
		}
		str.append(";");
						
		
		str.append("\n");
		bw.write(str.toString());
				
		
	}

	/*
	 * POS can be like VB (verbs), NN (nouns), etc
	 */
	public static Set<String> getWordsByPOS(String content,String POS) {
		CoreDocument doc = new CoreDocument(content);
		pipeline.annotate(doc);

		Set<String> verbs = new LinkedHashSet<>();

		for (CoreSentence sent : doc.sentences()) {

			//System.out.println("The parse of the sentence '" + sent + "' is " + sent.constituencyParse());
			// Iterate over every word in the sentence
			for (int i = 0; i < sent.tokens().size(); i++) {
				// Condition: if the word is a noun (posTag starts with "NN")
				if (sent.posTags() != null && sent.posTags().get(i) != null && sent.posTags().get(i).contains(POS)) {
					// Put the word into the Set
					verbs.add(sent.tokens().get(i).originalText());
				}
			}
		}

		//System.out.println("Verbs: " + verbs);
		return verbs;
	}

	public static void appendInMap(String key, Set<String> terms, Map<String, Set<String>> map) {
		Set<String> values = map.get(key);
		if(values==null) {
			//values = new LinkedHashSet();
			map.put(key, terms);
		}else {
			//System.out.println("Term "+key+" already in map: "+values+ " -- adding new terms: "+terms);
			values.addAll(terms);
			map.put(key, values);
			//System.out.println("New map: "+values);
		}
		
	}
	
	public Map<String,Set<String>> loadAntonyms() throws IOException {
		String resourceName;
		List<String> lines;
		InputStream input;
		String[] parts;
		Map<String,Set<String>> antonymsMap = new LinkedHashMap<>();
		
		/*
		 * taikuukaits
		 */
		resourceName = "antonyms.txt";
		input = getClass().getClassLoader().getResourceAsStream(resourceName);
        lines = IOUtils.readLines(input, StandardCharsets.UTF_8);
		
		for(String line:lines) {
			parts = line.split("\\|");
			String word = parts[0].trim();
			Set<String> antonyms = Arrays.stream(parts[1].split(",")).map(String::trim).collect(Collectors.toSet());
			//antonymsMap.put(word, antonyms);
			appendInMap(word,antonyms,antonymsMap);
		}
		
		//System.out.println(antonymsMap);
		//System.out.println(antonymsMap.get("closed interval"));
		//System.out.println(antonymsMap.get("absolution"));
		return antonymsMap;
	}

	public static String prepareQueryForGoogleCrawler(String query, String tag) {
		String completeQuery = "";
		
		Boolean containTagToken = Pattern.compile(".*\\b"+tag+"\\b.*").matcher(query.toLowerCase()).find();
		
		if(!containTagToken) {
			completeQuery += tag+" ";
		}
		//filter for SO posts is executed in another method: executeGoogleSearch
		completeQuery += query;
		
		return completeQuery;
	}

	

	public static Set<String> getWordsToGetAntonymsForQuery(String rawQuery, String antonymsLevel) {
		Set<String> words = null;
				
		if(antonymsLevel.equals("All")) {
			words= Arrays.stream(rawQuery.split("\\s+")).collect(Collectors.toCollection(LinkedHashSet::new));	
		}else {
			words = getWordsByPOS(rawQuery,antonymsLevel);
		}
		return words;
	}

	public static boolean fillQueryAntonymsMap(Map<String, Set<String>> queryAntonymsMap,
			Map<String, Set<String>> antonymsMap, String rawQuery,String processedQuery, String antonymsLevel, Set<String> queryAntonyms, Baseline baseline) {
		if(baseline.getAntonymsFor().equals("None")) {
			return false;
		}
		
		queryAntonymsMap.clear();
		queryAntonyms.clear();
		Set<String> queryWordsToGetAntonyms = getWordsToGetAntonymsForQuery(processedQuery.toLowerCase(),antonymsLevel);
		for(String queryWord: queryWordsToGetAntonyms) {
			Set<String> antonyms = antonymsMap.get(queryWord);
			if(antonyms!=null) {
				queryAntonymsMap.put(queryWord, antonyms);
				queryAntonyms.addAll(antonyms);
			}
		}
		SetView<String> intersection = Sets.intersection(queryWordsToGetAntonyms, queryAntonyms); 
		if(intersection.size()>0) {
			System.out.println("Query has antonyms in its own, disconsidering: "+rawQuery);
			return false;
		}
		
		
		return true;
		
		
		
	}

	public static List<Baseline> getBaselinesForTag(int tagId, List<Baseline> baselines) {
		return baselines.stream().filter(e->e.getTagId().equals(tagId)).collect(Collectors.toList());
	}

	
	 public static boolean isPureAscii(String v) {
		    return Charset.forName("US-ASCII").newEncoder().canEncode(v);
		    // or "ISO-8859-1" for ISO Latin 1
		    // or StandardCharsets.US_ASCII with JDK1.7+
	}

	public static int countStopWords(String query) {
		Set<String> words= Arrays.stream(query.split("\\s+")).collect(Collectors.toCollection(LinkedHashSet::new));
		int numIntersections = Sets.intersection(stopWordsSet, words).size(); 
		return numIntersections;
	}

	public static double[] getVectorsFromString(String line) {
		double[] doubleValues = Arrays.stream(line.split("\\s+")).mapToDouble(Double::parseDouble).toArray();
		return doubleValues;
	}		
	
	public static double[] getVectorsFromString(String line,String space) {
		double[] doubleValues = Arrays.stream(line.split(space)).mapToDouble(Double::parseDouble).toArray();
		return doubleValues;
	}		

	
	public static void addCountToMap(int count, Map<Integer, Integer> map, int max) {
		int nro=0;
		if(count>=max) {
			nro = map.get(max);
			nro++;
			map.put(max, nro);
		}else {
			nro = map.get(count);
			nro++;
			map.put(count, nro);
		}
	}
	
	
	public static Map<String,double[]> readQueriesAndVectors(String fileToRead) throws IOException {
		//long initTime = System.currentTimeMillis();
		Map<String,double[]> queriesAndVectors = new LinkedHashMap<>();
		List<String> queriesAndVectorsLines = Files.readAllLines(Paths.get(fileToRead));
		String[] parts;
		for(String line: queriesAndVectorsLines) {
			parts = line.split(">>");
			double[] vectors = getVectorsFromString(parts[1].trim());
			queriesAndVectors.put(parts[0].trim(), vectors);
		}
		return queriesAndVectors;
	}
	
	public static Map<String,double[]> readQueriesAndVectors(String fileToRead,String sep) throws IOException {
		//long initTime = System.currentTimeMillis();
		Map<String,double[]> queriesAndVectors = new LinkedHashMap<>();
		List<String> queriesAndVectorsLines = Files.readAllLines(Paths.get(fileToRead));
		String[] parts;
		for(String line: queriesAndVectorsLines) {
			parts = line.split(">>");
			double[] vectors = getVectorsFromString(parts[1].trim(),sep);
			queriesAndVectors.put(parts[0].trim(), vectors);
		}
		return queriesAndVectors;
	}
	
	public static String getEmbeddingsString(double[] embeddings) {
		StringBuilder sb = new StringBuilder("");
		for (double e: embeddings) {
			sb.append(e+",");
		}
		return sb.toString().substring(0,sb.toString().length()-1);
	}

}
