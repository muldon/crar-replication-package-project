package com.ufu.crar;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.stereotype.Component;

import com.ufu.crar.config.CrarParameters;
import com.ufu.crar.tfidf.Corpus;
import com.ufu.crar.tfidf.Document;
import com.ufu.crar.tfidf.VectorSpaceModel;
import com.ufu.crar.to.AnswerParentPair;
import com.ufu.crar.to.Baseline;
import com.ufu.crar.to.Bucket;
import com.ufu.crar.to.ContentType.ContentTypeEnum;
import com.ufu.crar.to.MetricResult;
import com.ufu.crar.to.Query;
import com.ufu.crar.to.SoContentWordVector;
import com.ufu.crar.to.Tags.TagEnum;
import com.ufu.crar.to.ThreadContent;
import com.ufu.crar.util.CosineMeasure;
import com.ufu.crar.util.CosineSimilarity;
import com.ufu.crar.util.CrarUtils;
import com.ufu.crar.util.CrokageComposer;
import com.ufu.crar.util.LuceneSearcher;
import com.ufu.crar.util.SearcherParams;

@Component
@SuppressWarnings("unused")
public class CrarApp extends AppAuxSolutionBuilder {
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());

	private static final int SIZE_TOKENS = 10;
	public static final int SIZE_EMBEDDING = 100;
	private static final int SIZE_VECTOR = 100;
	private static final int EPOCHS = 20;

	private MultiLayerNetwork model;
	private CnnFeatureExtractor learner;	

	@Value("${DL_CNN_INTPUT_TRAINING}")
	public String DL_CNN_INTPUT_TRAINING;

	@Value("${DL_CNN_OUTPUT_TRAINING}")
	public String DL_CNN_OUTPUT_TRAINING;

	@PostConstruct
	public void init() throws Exception {
		long initTime = System.currentTimeMillis();
		System.out.println("Initializing CRAR app...");
		initializeVariables();
		action = StringUtils.trim(action);

		System.out.println("\nConsidering parameters: \n" + "\n useLemma: " + useLemma + "\n action: " + action + "\n");

		switch (action) {

		case "compareAllBaselines":
			compareAllBaselines();
			break;

		case "loadMapsAndIndices":
			loadModelsMapsAndIndices();
			break;				
		
		case "trainDL":
			trainDL();
			break;

		default:
			break;
		}

		CrarUtils.reportElapsedTime(initTime, action);

		if (!action.equals("loadMapsAndIndices") && !action.equals("dummy")) {
			System.out.println("Now shutting down " + action);
			int exitCode = SpringApplication.exit(appContext, (ExitCodeGenerator) () -> 0);
			System.exit(exitCode);
		}
	}


	public void trainDL() throws Exception {
		TagEnum languages[] = { TagEnum.Java };
		List<TagEnum> languagesList = Arrays.asList(languages);
		readSOContentWordAndVectorsForTags(languages);
		Map<String, SoContentWordVector> embMap = wordVectorsMapByTag.get(1);
		int counter = 0;
		loadValidThreadsForLanguages(languagesList);

		StringBuilder builder = new StringBuilder();
		StringBuilder tokens = new StringBuilder();

		Set<Integer> languagesDLL = allValidThreadsMapByTag.keySet();
		for (Integer language : languagesDLL) {
			Set<Integer> threadsIds = allValidThreadsMapByTag.get(language).keySet();
			for (Integer threadId : threadsIds) {
				ThreadContent tc = allValidThreadsMapByTag.get(language).get(threadId);
				List<String> methodBodyTokens = Arrays
						.asList((tc.getTitle()).split(" "));
				builder.append(CrarUtils.getVectorsFromDouble(CrarUtils.vectorizeDouble(methodBodyTokens, embMap, SIZE_TOKENS, SIZE_EMBEDDING))).append("\n");

				if (counter % 5000 == 0) {
					System.out.println("writing to file " + counter);
					FileHelper.outputToFile(DL_CNN_INTPUT_TRAINING, builder, true);
					builder.setLength(0);
				}
				counter++;
			}
		}

		System.gc();
		FileHelper.outputToFile(DL_CNN_INTPUT_TRAINING, builder, true);

		int batchSize = 1024;

		learner = new CnnFeatureExtractor(SIZE_TOKENS, SIZE_EMBEDDING, batchSize, SIZE_VECTOR);
		learner.setNumberOfEpochs(EPOCHS);
		learner.setSeed(123);
		learner.setNumOfOutOfLayer1(20);
		learner.setNumOfOutOfLayer2(50);

		learner.extracteFeaturesWithCNN(DL_CNN_INTPUT_TRAINING, DL_CNN_OUTPUT_TRAINING);
	}

	private void generateMetricsForRNN() throws Exception {
		Map<String, SoContentWordVector> embMap = wordVectorsMapByTag.get(1);
		System.gc();
		int counter = 0;
		int batchSize = 1;
		learner = new CnnFeatureExtractor(SIZE_TOKENS, SIZE_EMBEDDING, batchSize, SIZE_VECTOR);
		learner.setNumberOfEpochs(10);
		learner.setSeed(123);
		learner.setNumOfOutOfLayer1(20);
		learner.setNumOfOutOfLayer2(50);

		this.model = learner.getModel(DL_CNN_OUTPUT_TRAINING);
		Set<Integer> languagesDLL = allValidThreadsMapByTag.keySet();
		for (Integer language : languagesDLL) {
			Set<Integer> threadsIds = allValidThreadsMapByTag.get(language).keySet();
			for (Integer threadId : threadsIds) {
				counter++;
				if (counter % 10000 == 0) {
					System.out.println("CNN processed for " + counter + " threads");
				}
				ThreadContent t = allValidThreadsMapByTag.get(language).get(threadId);
				List<String> queryBodyTokens = Arrays
						.asList((t.getTitle()).split(" "));
				double[] vectorizedTokenVector = CrarUtils.vectorizeDouble(queryBodyTokens, embMap, SIZE_TOKENS, SIZE_VECTOR);
				double[] output = learner.getOutput(this.model, vectorizedTokenVector);
				t.setEmbeddedDLOutput(output);
			}
		}
		System.gc();
	}

	public double[] getCnnOutput(String query, int sizeTokens, int sizeEmbedding) {
		Map<String, SoContentWordVector> embMap = wordVectorsMapByTag.get(1);
		double[] vectorizedTokenVector = CrarUtils.vectorizeDouble(Arrays.asList(query.split(" ")), embMap, sizeTokens,
				sizeEmbedding);
		return learner.getOutput(this.model, vectorizedTokenVector);

	}

	private void compareAllBaselines() throws Exception {
		Map<String, Set<Integer>> groundTruthThreadsMap = new LinkedHashMap<>();
		Map<String, Set<Integer>> groundTruthAnswersMap = new LinkedHashMap<>();

		// long initTime = System.currentTimeMillis();
		// Map<String, Set<Integer>> crokageRecommendedResults = null;
		// Map<Integer,List<Baseline>> baselinesMap = new LinkedHashMap<>();
		/*
		 * List<Baseline> javaBaselines = new ArrayList(); javaBaselines.add(new
		 * Baseline("CRAR-Java", "Java",TagEnum.Java.getId(),100,100));
		 */

		/*
		 * Map<String, Set<Integer>> groundTruthJava =
		 * CrarUtils.readQueriesAndIds(GROUND_TRUTH_ANSWERS_FOR_QUERIES+
		 * "-java-test-57.txt"); groundTruthJava.putAll(CrarUtils.readQueriesAndIds(
		 * GROUND_TRUTH_ANSWERS_FOR_QUERIES+"-java-training-58.txt")); Map<String,
		 * Set<Integer>> groundTruthPython =
		 * CrarUtils.readQueriesAndIds(GROUND_TRUTH_ANSWERS_FOR_QUERIES+
		 * "-conala-test-840.txt"); Map<String, Set<Integer>> groundTruthPhp =
		 * CrarUtils.readQueriesAndIds(GROUND_TRUTH_ANSWERS_FOR_QUERIES+
		 * "-java-test-57.txt");
		 */

		// TagEnum languages[] = {TagEnum.Java, TagEnum.Python};
		TagEnum languages[] = { TagEnum.Java };
		// TagEnum languages[] = {TagEnum.Python};

		String dataSetTypes[] = { // training or test, example: groundTruthAnswersForQueries-java-test-57 or
									// groundTruthAnswersForQueries-java-training-57
				// "training",
				"test", };

		List<Baseline> baselines = new ArrayList();

		if (!luceneSearchAsClient) {
			loadModelsMapsAndIndices();
		}

		for (TagEnum language : languages) {

			// Temp = template baseline
			String templateName = "Temp";

			// CROKAGE
			/*
			 * Como no baseline do EMSE-REPLICATION-PACKAGE com API class factor
			 */
			 baselines.add(new Baseline("CROKAGE", language, true, "None", "", false,
			 CrarParameters.roundsOnlyTRM));

			// Temp
			 baselines.add(new Baseline(templateName, language, false, "None", "", false,
			 CrarParameters.roundsWithSocialFactors));

			// TempWithoutSF: without social factors
			 baselines.add(new Baseline(templateName + "WithoutSF", language, false,
			 "None", "", false,
			 CrarParameters.roundsWithoutSocialFactors));

			// TempCC: Code Coverage or queryCoverageScoreMinThreshold>0
			 baselines.add(new Baseline(templateName + "CC", language, false, "None", "",
			 true,
			 CrarParameters.roundsWithSocialFactors));

			// Ant-POS-Place: antonyms threads&answers
			 baselines.add(new Baseline(templateName + "Ant-NN_VB-TR_ANS", language,
			 false, "All", "threads&answers",
			 false, CrarParameters.roundsWithSocialFactors));
			 baselines.add(new Baseline(templateName + "Ant-VB-TR_ANS", language, false,
			 "VB", "threads&answers", false,
			 CrarParameters.roundsWithSocialFactors));
			 baselines.add(new Baseline(templateName + "Ant-NN-TR_ANS", language, false,
			 "NN", "threads&answers", false,
			 CrarParameters.roundsWithSocialFactors));

			// antonyms threads
			 baselines.add(new Baseline(templateName + "Ant-NN_VB-TR", language, false,
			 "All", "threads", false,
			 CrarParameters.roundsWithSocialFactors));
			 baselines.add(new Baseline(templateName + "Ant-VB-TR", language, false, "VB",
			 "threads", false,
			 CrarParameters.roundsWithSocialFactors));
			 baselines.add(new Baseline(templateName + "Ant-NN-TR", language, false, "NN",
			 "threads", false,
			 CrarParameters.roundsWithSocialFactors));

			// antonyms answers
			// Temp
			 baselines.add(new Baseline(templateName + "Ant-NN_VB-ANS", language, false,
			 "All", "answers", false,
			 CrarParameters.roundsWithSocialFactors));
			 baselines.add(new Baseline(templateName + "Ant-VB-ANS", language, false,
			 "VB", "answers", false,
			 CrarParameters.roundsWithSocialFactors));
			baselines.add(new Baseline(templateName + "Ant-NN-ANS (CRAR)", language, false, "NN", "answers", false,
					CrarParameters.roundsWithSocialFactors));

			// tCCAnt-NN_VB - threads&answers: ant All - threads&answers +
			// queryCoverageScoreMinThreshold>0
			 baselines.add(new Baseline(templateName + "CCAnt-NN_VB-TR_ANS", language,
			 false, "All", "threads&answers",
			 true, CrarParameters.roundsWithSocialFactors));

			 baselines.add(new Baseline(templateName + "Ant-NN-ANS (CRAR-CNN)", language,
			 false, "NN", "answers", false,
			 CrarParameters.roundsWithSocialFactors));
			
			baselines.add(new Baseline("BM25+CNN", language,
			 false, "NN", "answers", false,
					 CrarParameters.roundsWithSocialFactors));
			 
			baselines.add(new Baseline("SENT2VEC", language,
					 false, "NN", "answers", false,
							 CrarParameters.roundsWithSocialFactors));
			baselines.add(new Baseline("ASSIMETRIC", language,
					 false, "NN", "answers", false,
							 CrarParameters.roundsWithSocialFactors));
			baselines.add(new Baseline("BODYASS", language,
					 false, "NN", "answers", false,
							 CrarParameters.roundsWithSocialFactors));
			baselines.add(new Baseline("TFIDFCOS", language,
					 false, "NN", "answers", false,
							 CrarParameters.roundsWithSocialFactors));							
		}

		if (baselines.stream().anyMatch(e -> e.getIsCrokage() == true)) {// contains CRAR - load API class factor
																			// dependencies
			configureTagIdByDataSet();
			loadJavaVariables();
			loadUpvotedAnswersIdsWithCodeContentsAndParentContents();
		}

		// String datasets[] = {"java-test"};
		// Integer tagsIds[] = {TagEnum.Java.getId(), TagEnum.Python.getId()};
		// List<Integer> tagsList = Arrays.asList(tagsIds);
		// Integer tagsIds[] = loadTagsIdsByDatasets(dataSets);
		// String resultsCacheFile = RECOMMENDED_ANSWERS_QUERIES_CACHE+"-top100";

		readSOContentWordAndVectorsForTags(languages);

		//

		Map<String, Set<String>> antonymsMap = crarUtils.loadAntonyms();

		long initTime2 = 0l;
		String languageDataSet = "";
		for (TagEnum language : languages) {
			for (String dataSetType : dataSetTypes) {
				groundTruthThreadsMap.clear();
				groundTruthAnswersMap.clear();
				initTime2 = System.currentTimeMillis();
				System.out.println("\n\n");
				// System.out.println("\n\n\nLoading ground truth for
				// "+language.name().toLowerCase()+ " and "+dataSetType);
				languageDataSet = language.name().toLowerCase() + "-" + dataSetType;
				System.out.println("compareAllBaselines para "+language);
				int tagId = TagEnum.getTagIdByTagEnum(language);
				Map<String, double[]> queriesAndSent2Vectors = CrarUtils.readQueriesAndVectors(
						CRAR_HOME + "/data/groundTruthSentenceQueriesAndVectors" + tagId + ".txt");

				System.out.println("compareAllBaselines mapa "+queriesAndSent2Vectors);
				List<Baseline> baselinesToEvaluate = CrarUtils.getBaselinesForTag(tagId, baselines);
				if (baselinesToEvaluate.isEmpty()) {
					continue;
				}

				System.out.println("Evaluating - language: " + language + " dataset: " + dataSetType + " - baselines: "
						+ baselinesToEvaluate.size());
				loadGroundTruthSelectedQueriesForQuestions(languageDataSet, groundTruthThreadsMap);
				loadGroundTruthSelectedQueries(languageDataSet, groundTruthAnswersMap);
				
				containsCNNBaselines(baselinesToEvaluate);

				for (Baseline baseline : baselinesToEvaluate) {										
					System.out.println("compareAllBaselines baseline "+baseline);

					// initTime = System.currentTimeMillis();
					System.out.println("\nEvaluating baseline: " + baseline.getName());
					// resultsCacheFile =
					// RECOMMENDED_ANSWERS_QUERIES_CACHE+"-"+dataSet+"-"+baseline.getName()+".txt";
					dynamicRankInner(baseline, groundTruthThreadsMap, groundTruthAnswersMap, tagId, languageDataSet,
							antonymsMap, queriesAndSent2Vectors);
					// CrarUtils.reduceSetV2(crokageRecommendedResults, 10);
					// CrarUtils.writeMapToFile(crokageRecommendedResults,resultsCacheFile,">>");
				}

				CrarUtils.reportElapsedTime(initTime2, "time for " + languageDataSet);
			}

			flushVariablesForTag();
		}

	}

	private void containsCNNBaselines(List<Baseline> baselinesToEvaluate) throws Exception {
		for (Baseline b: baselinesToEvaluate) {
			if (b.getName().contains("CNN")) {
				trainDL();
				generateMetricsForRNN();
				return;
			}			
		}
		
	}


	private Map<String, Set<Integer>> dynamicRankInner(Baseline baseline,
			Map<String, Set<Integer>> groundTruthThreadsMap, Map<String, Set<Integer>> groundTruthAnswersMap,
			Integer tagId, String languageDataSet, Map<String, Set<String>> antonymsMap,
			Map<String, double[]> queriesAndSent2Vectors) throws Exception {
		BufferedWriter bw = null;
		List<String> queries = null;
		String processedQuery;
		Set<ThreadContent> candidateThreads = null;
		Set<ThreadContent> filteredCandidateThreads = null;
		Map<String, Set<Integer>> recommendedResults = new LinkedHashMap<>();
		Map<Integer, ThreadContent> filteredCandidateThreadsMap = new LinkedHashMap<>();
		Set<Integer> thredsIds = null;
		Set<Bucket> candidateBuckets = new LinkedHashSet<>();
		int run = 0;
		String obsTmp;
		String firstObs;
		Set<String> queryAntonyms = new LinkedHashSet<>();
		Map<String, Set<String>> queryAntonymsMap = new LinkedHashMap<>();
		Map<String, Set<Integer>> groundTruth;

		try {

			initTime = System.currentTimeMillis();
			if (CrarParameters.evaluationOf.equals("Threads")) {
				groundTruth = groundTruthThreadsMap;
				if (CrarParameters.generateFeaturesFile) {
					bw = new BufferedWriter(new FileWriter(GROUND_TRUTH_THREADS_FOR_QUERIES + "-" + baseline.getName()
							+ "-" + languageDataSet + "-trmLimit1-" + baseline.getTrmLimit1Threads() + " - round-"
							+ baseline.getNumberOfRoundsInReducingThreads() + " - topThreadsLimitRound1-"
							+ baseline.getTopRelevantThreadsLimit1() + " - topThreadsLimitRound2-"
							+ baseline.getTopRelevantThreadsLimit2() + ".csv"));
					bw.write(
							"Query;Thread Id;Rank;threadFinalScore;threadTitleAsymSim;threadTitleAsymSimNorm;threadBodyAsymSim;threadBodyAsymSimNorm;queryTitleCoverageScore;queryCodeCoverageScore;jaccard;2-gramsScore;3-gramsScore;bm25Score;bm25ScoreNorm;tf-idf;tf-idfNorm;AnswerCount;AnswerCountNorm;ThreadUpScore;ThreadTotalUpScore;ThreadTotalUpScoreNorm;\n\n");
				}

			} else { // Answers
				groundTruth = groundTruthAnswersMap;
				if (CrarParameters.generateFeaturesFile) {
					bw = new BufferedWriter(new FileWriter(GROUND_TRUTH_ANSWERS_FOR_QUERIES + "-" + baseline.getName()
							+ "-" + languageDataSet + "-trmLimit1-" + baseline.getTrmLimit1Threads() + " - round-"
							+ baseline.getNumberOfRoundsInReducingThreads() + " - topThreadsLimitRound1-"
							+ baseline.getTopRelevantThreadsLimit1() + " - topThreadsLimitRound2-"
							+ baseline.getTopRelevantThreadsLimit2() + " - trmLimitAnswers-"
							+ baseline.getTrmLimitAnswers() + " .csv"));
					bw.write(
							"Query;Answer Id;Rank;answerFinalScore;threadFinalScore;AsymSim;AsymSimNorm;jaccard;methodScore;queryCoverage;bm25Score;bm25ScoreNorm;tf-idf;tf-idfNorm;\n\n");
				}
			}

			queries = new ArrayList(groundTruthThreadsMap.keySet());
			// luceneTopIdsMap.clear();
			recommendedResults.clear();
			int count = 0;
			// System.out.println("Running "+baseline);
			for (String rawQuery : queries) { // for each query
				// Map<Integer, Double> threadSimMap = new LinkedHashMap<>();
				long initTime2 = System.currentTimeMillis();
				count++;
				// System.out.println("Evaluating query "+count+" of "+queries.size()+" :
				// "+rawQuery);
				processedQuery = CrarUtils.processQuery(rawQuery, useLemma);

				boolean processAntonyms = CrarUtils.fillQueryAntonymsMap(queryAntonymsMap, antonymsMap, rawQuery,
						processedQuery, baseline.getAntonymsFor(), queryAntonyms, baseline);

				if (CrarParameters.evaluationOf.equals("Threads")
						|| (CrarParameters.evaluationOf.equals("Answers") && baseline.getReduceThreads())) {
					if (luceneSearchAsClient) {
						candidateThreads = getCandidadeThreadsClient(processedQuery, baseline.getTrmLimit1Threads(),
								tagId);
					} else {
						Query query = new Query();
						query.setTagId(tagId);
						query.setQueryText(processedQuery);
						query.setTrmLimit1(baseline.getTrmLimit1Threads());
						candidateThreads = getCandidadeThreadsServer(query);
						query = null;
					}

					filteredCandidateThreadsMap = reduceThreads(candidateThreads, processedQuery, tagId, rawQuery,
							baseline.getTopRelevantThreadsLimit1(), baseline.getTopRelevantThreadsLimit2(),
							baseline.getNumberOfRoundsInReducingThreads(), CrarParameters.generateFeaturesFile, bw,
							CrarParameters.evaluationOf, groundTruthThreadsMap, queryAntonymsMap, queryAntonyms,
							processAntonyms, baseline, queriesAndSent2Vectors);
				}

				if (CrarParameters.evaluationOf.equals("Threads")) {
					recommendedResults.put(rawQuery, filteredCandidateThreadsMap.keySet());
				} else {
					if (baseline.getReduceThreads()) {
						candidateBuckets = crarService
								.getUpvotedAnswersWithCodeForThreadsIds(filteredCandidateThreadsMap.keySet(), useLemma);
						// index and use BM25
						luceneSearcher = new LuceneSearcher();
						luceneSearcher.buildSearchManager(candidateBuckets, baseline.getSearchParam());
						Set<Integer> luceneSmallSetIds = luceneSearcher.search(processedQuery,
								baseline.getTrmLimitAnswers(), bm25ScoreAnswerIdMap);
						// System.out.println("size before:"+candidateBuckets.size());
						candidateBuckets.removeIf(e -> !luceneSmallSetIds.contains(e.getId()));
						// System.out.print(" - size after:"+candidateBuckets.size());

					} else { // crokage
						if (luceneSearchAsClient) {
							candidateBuckets = getCandidadeAnswersClient(processedQuery, baseline.getTrmLimitAnswers(),
									tagId);
						} else {
							Query query = new Query();
							query.setTagId(tagId);
							query.setQueryText(processedQuery);
							query.setTrmLimit1(baseline.getTrmLimitAnswers());
							candidateBuckets = getCandidadeAnswersServer(query);
							query = null;
						}

						/*
						 * if(rawQuery.contains("How do I parse a text string into date and time")) {
						 * //if(rawQuery.contains("insert an element in array at a given position")) {
						 * Set<Integer> topAnswersIds = candidateBuckets.stream() .map(e -> e.getId())
						 * .collect(Collectors.toCollection(LinkedHashSet::new));
						 * System.out.println("Returned by lucene: "+topAnswersIds); }
						 */
					}

					CrokageComposer.filterAnswersWithAntonyms(rawQuery, groundTruthAnswersMap, candidateBuckets,
							queryAntonymsMap, queryAntonyms, processAntonyms, baseline);

					// luceneSearcher = new LuceneSearcher();
					// luceneSearcher.buildSearchManager(candidateBuckets, searchParam);
					// Set<Integer> luceneSmallSetIds = luceneSearcher.search(processedQuery,
					// trmLimitAnswers,bm25ScoreAnswerIdMap);
					// System.out.println("size before:"+candidateBuckets.size());
					// candidateBuckets.removeIf(e-> !luceneSmallSetIds.contains(e.getId()));
					// System.out.println("size after:"+candidateBuckets.size());

					if (baseline.isCrokage) {
						topClasses.addAll(recommendedApis.get(count));
						CrarUtils.setLimitV2(topClasses, numberOfAPIClasses);
					}

					Set<Integer> topKRelevantAnswersIds = getTopKRelevantBuckets(candidateBuckets, processedQuery,
							tagId, rawQuery, filteredCandidateThreadsMap, bm25ScoreAnswerIdMap, bw,
							CrarParameters.generateFeaturesFile, groundTruthAnswersMap, baseline);

					recommendedResults.put(rawQuery, topKRelevantAnswersIds);
					/*
					 * if(rawQuery.contains("How do I parse a text string into date and time")) {
					 * //if(rawQuery.contains("insert an element in array at a given position")) {
					 * System.out.println("recommended ids: "+topKRelevantAnswersIds); }
					 */
				}

				// filteredCandidateThreadsMap.clear();
				luceneSearcher = null;

				// searchParam=null;
			}
			/*
			 * if(generateRemainingForClassifier) { return null; }
			 */
			candidateBuckets = null;
			String obsComp = null;
			MetricResult metricResult = null;
			boolean saveAll = false;
			// String searchParamsStr = baseline.toString();

			for (int topk : CrarParameters.topkArr) {

				Map<String, Set<Integer>> recommendedResultsTmp = CrarUtils.copy(recommendedResults);
				// obsComp = "tagid:"+tagId+" -" + languageDataSet+ "
				// -trmLimit1Threads:"+baseline.getTrmLimit1Threads()+"
				// -topRelevantThreadsLimitRound1:"+baseline.getTopRelevantThreadsLimit1()+"
				// -topRelevantThreadsLimitRound2:"+baseline.getTopRelevantThreadsLimit2()+"
				// -trmLimitAnswers:"+baseline.getTrmLimitAnswers()+ " -rounds:
				// "+baseline.getNumberOfRoundsInReducingThreads()+ "
				// -isCrokage:"+baseline.getIsCrokage();
				obsComp = "tagId=" + tagId + " -" + languageDataSet + " -limits:" + baseline.getTrmLimit1Threads() + "-"
						+ baseline.getTopRelevantThreadsLimit1() + "-" + baseline.getTopRelevantThreadsLimit2()
						+ "-Ans:" + baseline.getTrmLimitAnswers() + "-withSocfac?:" + baseline.getUseSocialFactors()
						+ " -isCrokage:" + baseline.getIsCrokage();
				String approachName = "Baseline: " + baseline.getName();
				if (saveQueryMetric) {
					approachName = "saveQueryMetric - Baseline: " + baseline.getName();
				}
				metricResult = new MetricResult(approachName, baseline.getTrmLimit1Threads(), null, null, null, cutoff,
						topk, obsComp, 0, languageDataSet, tagId);
				metricResult.setVectorsType(baseline.getVectorsTypeId());
				analyzeResults(recommendedResultsTmp, groundTruth, metricResult, approachName);

				crarService.saveMetricResult(metricResult);
				recommendedResultsTmp = null;
				metricResult = null;
			}

			CrarUtils.reportElapsedTime(initTime, "for run");
			queries = null;

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (bw != null)
				bw.close();
		}

		return recommendedResults;
	}

	/*
	 * 
	 * private void generateRemainingFeaturesForThreads(Map<String, Set<Integer>>
	 * groundTruthMap, Set<ThreadContent> candidateThreads, String rawQuery, String
	 * processedQuery, BufferedWriter bw, String postType) throws IOException {
	 * 
	 * String comparingContent; documents.clear(); Document queryDocument = new
	 * Document(processedQuery, 0); documents.add(queryDocument);
	 * 
	 * List<ThreadContent> candidateThreadsList = new ArrayList<>(candidateThreads);
	 * for(ThreadContent bucket:candidateThreads) { comparingContent =
	 * loadThreadContent(bucket,ContentTypeEnum.title_questionBody_body_code.getId()
	 * ); Document document = new Document(comparingContent, bucket.getId());
	 * documents.add(document); bucket.setDocument(document); }
	 * 
	 * Corpus corpus = new Corpus(documents); VectorSpaceModel vectorSpace = new
	 * VectorSpaceModel(corpus);
	 * 
	 * 
	 * bw.write(rawQuery+";");
	 * 
	 * Set<Integer> groundTruthThreadsIds = groundTruthMap.get(rawQuery);
	 * 
	 * for(Integer groundTruthThreadId: groundTruthThreadsIds) {
	 * bw.write(groundTruthThreadId+";;;;;;"); //System.out.println(threadId);
	 * 
	 * boolean groundTruthThreadFoundInCandidates =
	 * candidateThreads.stream().anyMatch(item -> threadId.equals(item.getId()));
	 * if(groundTruthThreadFoundInCandidates) { ThreadContent groundTruthThread =
	 * candidateThreads.stream() .filter((thread) ->
	 * thread.getId().equals(threadId)).findAny().get(); double tfIdfCosineSimScore
	 * = vectorSpace.cosineSimilarity(queryDocument,
	 * groundTruthThread.getDocument());
	 * 
	 * } boolean found = false; for(ThreadContent candidateThread:candidateThreads)
	 * { if(candidateThread.getId().equals(groundTruthThreadId)) { found=true;
	 * double tfIdfCosineSimScore =
	 * CrarUtils.round(vectorSpace.cosineSimilarity(queryDocument,
	 * candidateThread.getDocument()),2); bw.write(tfIdfCosineSimScore+";"); }
	 * if(found) { break; } } if(found==false) { //ground truth not found in
	 * candidates bw.write("0;"); } //parei aqui, pq o bm25 ta zero ?
	 * 
	 * //BM25 score for(ThreadContent candidateThread:candidateThreads) {
	 * if(candidateThread.getId().equals(groundTruthThreadId)) { found=true; double
	 * bm25Score = CrarUtils.round(candidateThread.getBm25Score(),2);
	 * bw.write(bm25Score+";"); } if(found) { break; } } if(found==false) { //ground
	 * truth not found in candidates bw.write("0;"); } bw.write("\n;");
	 * 
	 * 
	 * }
	 * 
	 * 
	 * 
	 * }
	 */

	private Map<Integer, ThreadContent> reduceThreads(Set<ThreadContent> candidateThreads, String processedQuery,
			Integer tagId, String rawQuery, int topRelevantThreadsLimit1, int topRelevantThreadsLimit2, int rounds,
			Boolean generateFeaturesFile, BufferedWriter bw, String evaluationOf,
			Map<String, Set<Integer>> groundTruthThreadsMap, Map<String, Set<String>> queryAntonymsMap,
			Set<String> queryAntonyms, boolean processAntonyms, Baseline baseline,
			Map<String, double[]> queriesAndSent2Vectors) throws Exception {

		// System.out.println("-"+processedQuery);
		/*
		 * if(processedQuery.equals("compare contents streams determine equal")) {
		 * System.out.println("-here"); }
		 */

		// CrokageComposer.filterBadCandidateThreads(candidateThreads,queryAntonymsMap,queryAntonyms,rawQuery,groundTruthThreadsMap,processAntonyms,
		// baseline);

		CrokageComposer.filterThreadsWithAntonyms(candidateThreads, queryAntonymsMap, queryAntonyms, rawQuery,
				groundTruthThreadsMap, processAntonyms, baseline);

		ThreadContent maxFeaturesThread = new ThreadContent();

		calculateFeatures(candidateThreads, processedQuery, maxFeaturesThread, false, tagId,
				baseline.getVectorsTypeId(), queriesAndSent2Vectors, baseline);

		calculateFeaturesNormalizedAndFinalScore(candidateThreads, rawQuery, processedQuery, maxFeaturesThread, 1,
				baseline);

		CrokageComposer.filterBadCandidateThreadsWithMinFeatures(candidateThreads, maxFeaturesThread, queryAntonymsMap,
				queryAntonyms, rawQuery, groundTruthThreadsMap, processAntonyms, baseline);

		if (rounds == 0) { // only BM25 threads - sorted
			Set<ThreadContent> topThreads = candidateThreads.stream()
					.sorted(Comparator.comparingDouble(ThreadContent::getThreadFinalScore).reversed())
					.collect(Collectors.toCollection(LinkedHashSet::new));

			Map<Integer, ThreadContent> topThreadsMap = topThreads.stream()
					.collect(Collectors.toMap(ThreadContent::getId, c -> c, (e1, e2) -> e1, LinkedHashMap::new));
			// .collect(Collectors.toMap(ThreadContent::getId, c -> c));

			if (evaluationOf.equals("Threads") && generateFeaturesFile) {
				generateThreadsCoverage(topThreads, groundTruthThreadsMap, rawQuery, processedQuery, bw, topThreadsMap,
						rounds, tagId, maxFeaturesThread, baseline);
			}
			return topThreadsMap;
		}

		Set<ThreadContent> topThreads = candidateThreads.stream()
				.sorted(Comparator.comparingDouble(ThreadContent::getThreadFinalScore).reversed())
				.limit(topRelevantThreadsLimit1).collect(Collectors.toCollection(LinkedHashSet::new));

		if (rounds == 1) {
			Map<Integer, ThreadContent> topThreadsMap = topThreads.stream()
					.sorted(Comparator.comparingDouble(ThreadContent::getThreadFinalScore).reversed())
					.limit(topRelevantThreadsLimit2)
					.collect(Collectors.toMap(ThreadContent::getId, c -> c, (e1, e2) -> e1, LinkedHashMap::new));
			// .collect(Collectors.toMap(ThreadContent::getId, c -> c));

			if (evaluationOf.equals("Threads") && generateFeaturesFile) {
				generateThreadsCoverage(topThreads, groundTruthThreadsMap, rawQuery, processedQuery, bw, topThreadsMap,
						rounds, tagId, maxFeaturesThread, baseline);
			}

			return topThreadsMap;
		}

		// round 2
		calculateFeatures(topThreads, processedQuery, maxFeaturesThread, true, tagId, baseline.getVectorsTypeId(), null,
				baseline);

		calculateFeaturesNormalizedAndFinalScore(topThreads, rawQuery, processedQuery, maxFeaturesThread, 2, baseline);

		Map<Integer, ThreadContent> topThreadsMap = topThreads.stream()
				.sorted(Comparator.comparingDouble(ThreadContent::getThreadFinalScore).reversed())
				.limit(topRelevantThreadsLimit2)
				.collect(Collectors.toMap(ThreadContent::getId, c -> c, (e1, e2) -> e1, LinkedHashMap::new));
		// .collect(Collectors.toMap(ThreadContent::getId, c -> c));

		if (evaluationOf.equals("Threads") && generateFeaturesFile) {
			generateThreadsCoverage(new LinkedHashSet(topThreadsMap.values()), groundTruthThreadsMap, rawQuery,
					processedQuery, bw, topThreadsMap, rounds, tagId, maxFeaturesThread, baseline);
		}

		return topThreadsMap;
	}

	private void generateAnswersCoverage(Set<Bucket> topAnswers, Map<String, Set<Integer>> groundTruthMap,
			String rawQuery, String processedQuery, BufferedWriter bw, Map<Integer, Bucket> topAnswersMap,
			Integer tagId) throws IOException {

		List<Integer> recommendedIds = topAnswers.stream().map(e -> e.getId())
				.collect(Collectors.toCollection(ArrayList::new));

		List<Integer> groundTruthIds = new ArrayList(groundTruthMap.get(rawQuery));

		List<Integer> trashList = new ArrayList<>();
		List<Integer> foundList = new ArrayList<>();

		bw.write(rawQuery + "\n\n");

		for (Integer recommendedId : recommendedIds) {
			if (!groundTruthIds.contains(recommendedId)) {
				trashList.add(recommendedId);
			} else {
				foundList.add(recommendedId);
			}
		}

		bw.write("\nFound Answers: (" + foundList.size() + ")");
		if (rawQuery.equals("How to terminate process from Python using pid?")) {
			System.out.println();
		}
		for (Integer foundId : foundList) {
			int pos = recommendedIds.indexOf(foundId);
			CrarUtils.writeFeatureRowAnswers(topAnswersMap.get(foundId), bw, pos, foundId);
		}

		groundTruthIds.removeAll(recommendedIds);
		bw.write("\nMissed Answers: (" + groundTruthIds.size() + ")");
		if (!groundTruthIds.isEmpty()) { // remaining ground truth missed
			for (Integer groundTruthId : groundTruthIds) {
				Bucket candidateAnswer = crarService.getBucketById(groundTruthId, useLemma);
				if (candidateAnswer == null) {
					System.out.println("Missed answer null for: " + groundTruthId
							+ " - probably null processed code or zero upvotes");
					continue;
				}
				fillFeaturesForAnswer(candidateAnswer, processedQuery, tagId);
				try {
					CrarUtils.writeFeatureRowAnswers(candidateAnswer, bw, -1, groundTruthId);
				} catch (Exception e2) {
					System.out.println("error here: " + candidateAnswer);
					System.out.println("");
				}
			}
		}
		bw.write("\n\nTrash: (" + trashList.size() + ")");

		if (!trashList.isEmpty()) {
			int k = 0;
			for (Integer trashId : trashList) {
				int pos = recommendedIds.indexOf(trashId);
				CrarUtils.writeFeatureRowAnswers(topAnswersMap.get(trashId), bw, pos, trashId);
				k++;
				if (k == 20)
					break;
			}
		}

		bw.write("\n\n-----;-----;-----;-----;-----;-----;-----;-----;-----;-----;-----;-----;-----;-----;-----;\n\n");
	}

	private void calculateFeatures(Set<ThreadContent> candidateThreads, String processedQuery,
			ThreadContent maxFeaturesThread, boolean socialFeatures, Integer tagId, Integer vectorsTypeId,
			Map<String, double[]> queriesAndSent2Vectors, Baseline baseline) throws Exception {
		double maxTitleAsymmSim = 0;
		double maxBodyAsymmSim = 0;
		double maxTfIdfScore = 0;
		double maxBm25Score = 0;
		// double maxScore=0;
		double maxAnswerCount = 0;
		double maxViewCount = 0;
		double maxThreadTotalUpvotes = 0;
		double maxQuestionUpvotes = 0;
		double maxSent2vecScore = 0;

		/*
		 * n-grams
		 */
		int twoGramsNumMax = 0;
		int threeGramsNumMax = 0;
		int fourGramsNumMax = 0;

		String comparingContent;

		/**
		 * TF-IDF
		 */
		/*
		 * documents.clear(); Document queryDocument = new Document(processedQuery, 0);
		 * documents.add(queryDocument);
		 * 
		 * for(ThreadContent bucket:candidateThreads) { comparingContent =
		 * loadThreadContent(bucket,ContentTypeEnum.title_questionBody_body_code.getId()
		 * ); Document document = new Document(comparingContent, bucket.getId());
		 * documents.add(document); bucket.setDocument(document); }
		 * 
		 * Corpus corpus = new Corpus(documents); VectorSpaceModel vectorSpace = new
		 * VectorSpaceModel(corpus);
		 */

		/*
		 * List<Collection<String>> candidateDocumentGrams; List<Collection<String>>
		 * queryGrams = Lists.newArrayList(NgramTfIdf.ngramDocumentTerms(
		 * Lists.newArrayList(2,3,4), Arrays.asList(processedQuery))); Bucket
		 * queryGramBucket = new Bucket();
		 * CrarUtils.generateNGrams(queryGramBucket,queryGrams);
		 */

		if (!socialFeatures) {
			for (ThreadContent candidateThread : candidateThreads) {

				/*
				 * if(rawQuery.equals("How do I parse a text string into date and time?") &&
				 * candidateThread.getId().equals(18915075)) { System.out.println("xx here"); }
				 */
				/*
				 * Asymmetric Relevance
				 */
				/*
				 * if(candidateThread.getId().equals(13461393) &&
				 * processedQuery.equals("compress zip directory recursively")) {
				 * 
				 * System.out.println("thread here"); }
				 */

				// sent2vec between queries and titles
				/*
				 * Sentence Vectors
				 */
				if (queriesAndSent2Vectors != null && !queriesAndSent2Vectors.isEmpty()) {
					double[] querySent2vectors = queriesAndSent2Vectors.get(processedQuery);
					double[] bucketSent2vectors = candidateThread.getSentenceVectors();
					
					if (baseline.getName().contains("CNN")) {
						bucketSent2vectors = candidateThread.getEmbeddedDLOutput();
						querySent2vectors = getCnnOutput(processedQuery, SIZE_TOKENS, 100);
					}
					if (querySent2vectors == null) {
						System.out.println("Query null for " + processedQuery);
						throw new RuntimeException();
					}
					if (bucketSent2vectors == null) {
						System.out.println("candidateThread null: " + candidateThread.getId());
						throw new RuntimeException();
					}
					double sent2VecScore = CosineSimilarity.cosineSimilarity(querySent2vectors, bucketSent2vectors);
					candidateThread.setSent2VecScore(sent2VecScore);
					if (sent2VecScore > maxSent2vecScore) {
						maxSent2vecScore = sent2VecScore;
					}
				} else {
					System.out.println("queriesAndSent2Vectors null - " + candidateThread.getId());
				}

				comparingContent = candidateThread.getTitle();
				double threadTitleAsymmetricSim = getAsymmetricSimilarity(processedQuery, comparingContent, tagId,
						vectorsTypeId);
				candidateThread.setThreadTitleAsymmetricSim(threadTitleAsymmetricSim);
				if (threadTitleAsymmetricSim > maxTitleAsymmSim) {
					maxTitleAsymmSim = threadTitleAsymmetricSim;
				}

				comparingContent = loadThreadContent(candidateThread,
						ContentTypeEnum.question_body_answers_body.getId());
				double threadBodyAsymmetricSim = getAsymmetricSimilarity(processedQuery, comparingContent, tagId,
						vectorsTypeId);
				candidateThread.setThreadQuestionBodyAnswersBodyAsymmetricSim(threadBodyAsymmetricSim);
				if (threadBodyAsymmetricSim > maxBodyAsymmSim) {
					maxBodyAsymmSim = threadBodyAsymmetricSim;
				}

				/*
				 * TF-IDF
				 */
				/*
				 * double tfIdfCosineSimScore = vectorSpace.cosineSimilarity(queryDocument,
				 * candidateThread.getDocument());
				 * candidateThread.setTfIdfCosineSimScore(tfIdfCosineSimScore);
				 * if(tfIdfCosineSimScore>maxTfIdfScore) { maxTfIdfScore=tfIdfCosineSimScore; }
				 */

				// TF
				double tfCosineSimScore = CosineSimilarity.cosineSimilarity(processedQuery,
						loadThreadContent(candidateThread, ContentTypeEnum.title_questionBody_body_code.getId()));
				candidateThread.setTfIdfCosineSimScore(tfCosineSimScore);
				if (tfCosineSimScore > maxTfIdfScore) {
					maxTfIdfScore = tfCosineSimScore;
				}

				/*
				 * BM25-score
				 */
				double bm25Score = candidateThread.getBm25Score();
				// candidateThread.setBm25Score(bm25Score);
				if (bm25Score > maxBm25Score) {
					maxBm25Score = bm25Score;
				}

				/*
				 * N-Grams
				 */
				/*
				 * comparingContent=
				 * loadThreadContent(candidateThread,ContentTypeEnum.title.getId());
				 * candidateDocumentGrams = Lists.newArrayList(NgramTfIdf.ngramDocumentTerms(
				 * Lists.newArrayList(2,3,4), Arrays.asList(comparingContent)));
				 * CrarUtils.generateNGrams(candidateThread,candidateDocumentGrams);
				 * CrarUtils.countCommonNGrams(queryGramBucket,candidateThread);
				 * if(candidateThread.getTwoGramsCount()>twoGramsNumMax) {
				 * twoGramsNumMax=candidateThread.getTwoGramsCount(); }
				 * if(candidateThread.getThreeGramsCount()>threeGramsNumMax) {
				 * threeGramsNumMax=candidateThread.getThreeGramsCount(); }
				 */
				/*
				 * if(candidateThread.getFourGramsCount()>fourGramsNumMax) {
				 * fourGramsNumMax=candidateThread.getFourGramsCount(); }
				 */

				/*
				 * View count
				 */
				/*
				 * double viewCount = candidateThread.getViewCount(); if(viewCount>maxViewCount)
				 * { maxViewCount=viewCount; }
				 */
			}

			maxFeaturesThread.setBm25ScoreMax(maxBm25Score);
			maxFeaturesThread.setTfIdfCosineSimScoreMax(maxTfIdfScore);
			maxFeaturesThread.setThreadBodyAsymmetricSimMax(maxBodyAsymmSim);
			maxFeaturesThread.setThreadTitleAsymmetricSimMax(maxTitleAsymmSim);
			maxFeaturesThread.setThreadSent2VecScoreMax(maxSent2vecScore);
			// maxFeaturesThread.setNGramsMax(twoGramsNumMax,threeGramsNumMax,0);

		} else { // social features
			for (ThreadContent candidateThread : candidateThreads) {
				/*
				 * Answer count
				 */
				int answerCount = candidateThread.getAnswerCount();
				if (answerCount > maxAnswerCount) {
					maxAnswerCount = answerCount;
				}

				/*
				 * question upvotes
				 */
				int questionUpvotes = candidateThread.getUpvotes();
				if (questionUpvotes > maxQuestionUpvotes) {
					maxQuestionUpvotes = questionUpvotes;
				}

				/*
				 * Thread total upvotes
				 */
				int threadTotalUpvotes = candidateThread.getThreadTotalUpvotes();
				if (threadTotalUpvotes > maxThreadTotalUpvotes) {
					maxThreadTotalUpvotes = threadTotalUpvotes;
				}

				// double threadUpVotesScore =
				// CrokageComposer.calculateUpScore(candidateThread.getUpvotes());

			}
			maxFeaturesThread.setAnswerCountMax(maxAnswerCount);
			maxFeaturesThread.setUpvotesMax(maxQuestionUpvotes);
			maxFeaturesThread.setThreadTotalUpvotesMax(maxThreadTotalUpvotes);
		}

		// candidateDocumentGrams=null;
		// queryGrams=null;
		// queryGramBucket=null;

	}

	private void calculateFeaturesNormalizedAndFinalScore(Set<ThreadContent> candidateThreads, String rawQuery,
			String processedQuery, ThreadContent maxFeaturesThread, int round, Baseline baseline) throws IOException {

		String comparingContent;

		if (round < 2) {

			for (ThreadContent candidateThread : candidateThreads) {

				/*
				 * if (processedQuery.equals("insert element array position")) { if
				 * (candidateThread.getTitle().
				 * contains("insert object arraylist specific position")) {
				 * System.out.println("xx here"); }
				 * 
				 * }
				 */

				/*
				 * if(rawQuery.equals("How do I parse a text string into date and time?") &&
				 * candidateThread.getId().equals(18915075)) { System.out.println("xx here"); }
				 */
				// candidateThread.setThreadSim(simPair);
				// threadSimMap.put(candidateThread.getId(),simPair);

				/*
				 * if(candidateThread.getId().equals(13461393) &&
				 * processedQuery.equals("compress zip directory recursively")) {
				 * 
				 * System.out.println("thread here"); }
				 */

				double sent2vecScore = candidateThread.getSent2VecScore();
				sent2vecScore = (sent2vecScore / maxFeaturesThread.getThreadSent2VecScoreMax()); // normalization
				candidateThread.setSent2VecScoreNormalized(sent2vecScore);

				comparingContent = candidateThread.getTitle();
				double threadTitleAsymmetricSim = candidateThread.getThreadTitleAsymmetricSim();
				threadTitleAsymmetricSim = (threadTitleAsymmetricSim
						/ maxFeaturesThread.getThreadTitleAsymmetricSimMax()); // normalization
				candidateThread.setThreadTitleAsymmetricSimNormalized(threadTitleAsymmetricSim);
				/*
				 * if(threadTitleAsymmetricSim==0d) { System.out.println("title here"); }
				 */

				double threadBodyAsymmetricSim = candidateThread.getThreadQuestionBodyAnswersBodyAsymmetricSim();
				threadBodyAsymmetricSim = (threadBodyAsymmetricSim / maxFeaturesThread.getThreadBodyAsymmetricSimMax()); // normalization
				candidateThread.setThreadQuestionBodyAnswersBodyAsymmetricSimNormalized(threadBodyAsymmetricSim);

				double tfCosineSimScore = candidateThread.getTfIdfCosineSimScore();
				tfCosineSimScore = (tfCosineSimScore / maxFeaturesThread.getTfIdfCosineSimScoreMax()); // normalization
				if (maxFeaturesThread.getTfIdfCosineSimScoreMax() == 0d) {
					tfCosineSimScore = 0d;
				}
				candidateThread.setTfIdfCosineSimScoreNormalized(tfCosineSimScore);

				double bm25Score = candidateThread.getBm25Score();
				bm25Score = (bm25Score / maxFeaturesThread.getBm25ScoreMax()); // normalization
				candidateThread.setBm25ScoreNormalized(bm25Score);

				double queryTitleCoverage = CrarUtils.calculateCoverage(processedQuery,
						loadThreadContent(candidateThread, ContentTypeEnum.title.getId()));
				candidateThread.setQueryTitleCoverageScore(queryTitleCoverage);

				double codeCoverage = CrarUtils.calculateCoverage(processedQuery,
						loadThreadContent(candidateThread, ContentTypeEnum.code.getId()));
				candidateThread.setQueryCodeCoverageScore(codeCoverage);

				double jaccard = CrarUtils.calculateExactJaccard(processedQuery,
						loadThreadContent(candidateThread, ContentTypeEnum.title.getId()));
				candidateThread.setJaccardScore(jaccard);

				/*
				 * n-grams
				 */
				/*
				 * double twoGramsScore = candidateThread.getTwoGramsCount(); twoGramsScore =
				 * (twoGramsScore / maxFeaturesThread.getTwoGramsScoreMax()); //normalization
				 * if(maxFeaturesThread.getTwoGramsScoreMax()==0d) { twoGramsScore=0d; }
				 * candidateThread.setTwoGramsScoreNormalized(twoGramsScore);
				 * 
				 * double threeGramsScore = candidateThread.getThreeGramsCount();
				 * threeGramsScore = (threeGramsScore /
				 * maxFeaturesThread.getThreeGramsScoreMax()); //normalization
				 * if(maxFeaturesThread.getThreeGramsScoreMax()==0d) { threeGramsScore=0d; }
				 * candidateThread.setThreeGramsScoreNormalized(threeGramsScore);
				 */

				/*
				 * double fourGramsScore = candidateThread.getFourGramsCount(); fourGramsScore =
				 * (fourGramsScore / maxFeaturesThread.getFourGramsScoreMax()); //normalization
				 * if(maxFeaturesThread.getFourGramsScoreMax()==0d) { fourGramsScore=0d; }
				 * candidateThread.setFourGramsScoreNormalized(fourGramsScore);
				 */
				candidateThread.setTwoGramsScoreNormalized(0d);
				candidateThread.setThreeGramsScoreNormalized(0d);

				double finalScoreStage1 = CrokageComposer.calculateThreadsRankingScoreRound1(candidateThread, baseline);
				candidateThread.setThreadFinalScore(finalScoreStage1);
			}
		} else { // round 2
			for (ThreadContent candidateThread : candidateThreads) {
				/*
				 * double threadTitleAsymmetricSim =
				 * candidateThread.getThreadTitleAsymmetricSim(); double threadBodyAsymmetricSim
				 * = candidateThread.getThreadBodyAsymmetricSim(); double tfIdfCosineSimScore =
				 * candidateThread.getTfIdfCosineSimScore(); double bm25Score =
				 * candidateThread.getBm25Score();
				 */

				double threadAnswerCountScore = CrarUtils
						.round((candidateThread.getAnswerCount() / maxFeaturesThread.getAnswerCountMax()), 1); // normalization
				candidateThread.setThreadAnswerCountScoreNormalized(threadAnswerCountScore);

				/*
				 * Upvotes
				 */
				double threadUpVotesScore = CrokageComposer.calculateUpScore(candidateThread.getUpvotes());
				candidateThread.setThreadUpVotesScore(threadUpVotesScore);

				/*
				 * Total Upvotes
				 */
				double threadTotalUpvotes = CrarUtils.round(
						(candidateThread.getThreadTotalUpvotes() / maxFeaturesThread.getThreadTotalUpvotesMax()), 1); // normalization
				candidateThread.setThreadTotalUpVotesScoreNormalized(threadTotalUpvotes);

				/*
				 * double queryTitleCoverage = candidateThread.getQueryTitleCoverageScore();
				 * double codeCoverage = candidateThread.getQueryCodeCoverageScore(); double
				 * jaccard = candidateThread.getJaccardScore();
				 */

				double finalScoreStage2 = CrokageComposer.calculateThreadsRankingScoreRound2(candidateThread, baseline);
				candidateThread.setThreadFinalScore(finalScoreStage2);
				/*
				 * if(processedQuery.equals("insert element array position")) {
				 * if(candidateThread.getTitle().
				 * contains("insert object arraylist specific position")) {
				 * System.out.println("xx here"); } if(candidateThread.getId().equals(7384908))
				 * { System.out.println("xx here33"); } } if(finalScoreStage2>2.5) {
				 * System.out.println("xxx here2"); }
				 */

			}

		}

	}

	private Set<Integer> getTopKRelevantBuckets(Set<Bucket> candidateBuckets, String processedQuery, Integer tagId,
			String rawQuery, Map<Integer, ThreadContent> filteredCandidateThreadsMap,
			Map<Integer, Float> bm25ScoreAnswerIdMap, BufferedWriter bw, Boolean generateFeaturesFile,
			Map<String, Set<Integer>> groundTruthMap, Baseline baseline) throws IOException {

		String comparingTitle;
		String parentTitle;
		String comparingContent;
		bucketsIdsScores.clear();
		double maxAsymmScore = 0;
		double maxApiScore = 0;
		double maxTfIdfScore = 0;
		double maxBm25Score = 0;
		double maxThreadScore = 0;
		methodsCounterMap.clear();
		// classesCounterMap.clear();
		// topicsCounterMap.clear();
		documents.clear();
		Document queryDocument = new Document(processedQuery, 0);
		documents.add(queryDocument);
		VectorSpaceModel vectorSpace = null;
		Corpus corpus;
		// topAnswerParentPairsAnswerIdScoreMap.clear();

		ContentTypeEnum contentType = null;
		if (baseline.getIsCrokage()) {
			contentType = ContentTypeEnum.title_questionBody_body;

			// Api Score
			for (String topClass : topClasses) {
				Set<Integer> answersIdsFromBigMap = filteredSortedMapAnswersIds.get(topClass);
				if (answersIdsFromBigMap == null) {
					continue;
				}
				Set<Integer> candidateAnswersIds = candidateBuckets.stream().map(e -> e.getId())
						.collect(Collectors.toCollection(LinkedHashSet::new));

				for (Integer relevantAnswerId : candidateAnswersIds) {
					if (answersIdsFromBigMap.contains(relevantAnswerId)
							&& allBucketsWithUpvotesMap.containsKey(relevantAnswerId)) { // filtered by lucene and is
																							// upvoted answer
						AnswerParentPair answerParentPair = new AnswerParentPair(relevantAnswerId,
								allBucketsWithUpvotesMap.get(relevantAnswerId).getParentId());
						CrokageComposer.calculateApiScore(answerParentPair, topClasses, upvotedPostsIdsWithCodeApisMap);
						topAnswerParentPairsAnswerIdScoreMap.put(relevantAnswerId, answerParentPair.getApiScore());
					}
				}
			}

		} else {
			contentType = ContentTypeEnum.title_questionBody_body_code;
		}

		if (baseline.getWeightingScheme().equals("TF-IDF")) {
			// TD-IDF vocabulary
			for (Bucket bucket : candidateBuckets) {
				// comparingContent =
				// loadBucketContent(bucket,ContentTypeEnum.title_questionBody_body.getId());
				comparingContent = loadBucketContent(bucket, contentType.getId());
				Document document = new Document(comparingContent, bucket.getId());
				documents.add(document);
				bucket.setDocument(document);
			}

			corpus = new Corpus(documents);
			vectorSpace = new VectorSpaceModel(corpus);
		}

		for (Bucket candidateBucket : candidateBuckets) {
			try {

				/*
				 * Thread similarity
				 * 
				 */
				Integer threadId = candidateBucket.getParentId();
				/*
				 * if(threadId.equals(7384908) &&
				 * processedQuery.equals("insert element array position")) {
				 * System.out.println("here 33"); }
				 */
				if (!filteredCandidateThreadsMap.isEmpty()) { // or if(reduceThreads)
					double threadScore = filteredCandidateThreadsMap.get(threadId).getThreadFinalScore();
					candidateBucket.setThreadSim(threadScore);
					if (threadScore > maxThreadScore) {
						maxThreadScore = threadScore;
					}
				} else {
					candidateBucket.setThreadSim(0d);
				}

				/*
				 * Asymmetric Relevance
				 */
				comparingContent = loadBucketContent(candidateBucket, ContentTypeEnum.title_body.getId());
				double asymmScore = getAsymmetricSimilarity(processedQuery, comparingContent, tagId,
						baseline.getVectorsTypeId());
				candidateBucket.setSimPair(asymmScore);
				if (asymmScore > maxAsymmScore) {
					maxAsymmScore = asymmScore;
				}

				double tfIdfCosineSimScore = 0d;
				/*
				 * TF-IDF or TF
				 */
				if (baseline.getWeightingScheme().equals("TF-IDF")) {
					tfIdfCosineSimScore = vectorSpace.cosineSimilarity(queryDocument, candidateBucket.getDocument());
				} else { // TF
					tfIdfCosineSimScore = CosineSimilarity.cosineSimilarity(processedQuery,
							loadBucketContent(candidateBucket, ContentTypeEnum.title_questionBody_body_code.getId()));
				}
				candidateBucket.setTfIdfCosineSimScore(tfIdfCosineSimScore);
				if (tfIdfCosineSimScore > maxTfIdfScore) {
					maxTfIdfScore = tfIdfCosineSimScore;
				}

				/*
				 * BM-25
				 */
				if (baseline.getReduceThreads()) { // then we have bm25 again
					double bm25Score = bm25ScoreAnswerIdMap.get(candidateBucket.getId());
					// double bm25Score = candidateBucket.getBm25Score();
					candidateBucket.setBm25Score(bm25Score);
					if (bm25Score > maxBm25Score) {
						maxBm25Score = bm25Score;
					}
				} else {
					candidateBucket.setBm25Score(0d);
				}
				// double bm25Score = bm25ScoreAnswerIdMap.get(candidateBucket.getId());
				/*
				 * double bm25Score = candidateBucket.getBm25Score();
				 * //candidateBucket.setBm25Score(bm25Score); if(bm25Score>maxBm25Score) {
				 * maxBm25Score=bm25Score; }
				 */

				/*
				 * 4- Method score
				 */
				CrarUtils.countMethods(candidateBucket, tagId, methodsCounterMap);

				/*
				 * 6- Topics score
				 */
				/*
				 * if(CrokageComposer.getTopicWeight()>0) { countTopics(candidateBucket); }
				 */

				/*
				 * if(rawQuery.contains("How do I parse a text string into date and time")) {
				 * //if(rawQuery.contains("insert an element in array at a given position")) {
				 * System.out.println("\nBefore\n tf-idf: "+cosineSimScore);
				 * System.out.println("SimPair: "+asymmScore+" for: "+comparingContent); }
				 */

				/*
				 * 4- Class Score
				 */
				if (baseline.getIsCrokage()) {
					if (!topAnswerParentPairsAnswerIdScoreMap.isEmpty()
							&& topAnswerParentPairsAnswerIdScoreMap.get(candidateBucket.getId()) != null) {
						double apiAnswerPairScore = topAnswerParentPairsAnswerIdScoreMap.get(candidateBucket.getId());
						if (apiAnswerPairScore > maxApiScore) {
							maxApiScore = apiAnswerPairScore;
						}
					}
				}

			} catch (Exception e) {
				System.out.println("error here... post: " + candidateBucket.getId());
				throw e;
			}

		}

		/*
		 * Methods score
		 */
		Map<String, Integer> topMethodsCounterMap = methodsCounterMap.entrySet().stream()
				.sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

		/*
		 * Topics score
		 */
		/*
		 * Map<Integer,Integer> topTopicsCounterMap =
		 * topicsCounterMap.entrySet().stream()
		 * .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
		 * .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) ->
		 * e1, LinkedHashMap::new));
		 */

		// normalization and other relevance boosts
		for (Bucket candidateBucket : candidateBuckets) {
			if (!filteredCandidateThreadsMap.isEmpty()) { // or if(reduceThreads)
				double threadScore = candidateBucket.getThreadSim();
				threadScore = (threadScore / maxThreadScore); // normalization
				candidateBucket.setThreadSimNormalized(threadScore);
			} else {
				candidateBucket.setThreadSimNormalized(0d);
			}

			double asymmScore = candidateBucket.getSimPair();
			asymmScore = (asymmScore / maxAsymmScore); // normalization
			candidateBucket.setSimPairNormalized(asymmScore);

			double tfIdfScore = candidateBucket.getTfIdfCosineSimScore();
			tfIdfScore = (tfIdfScore / maxTfIdfScore); // normalization
			if (maxTfIdfScore == 0d) {
				tfIdfScore = 0d;
			}
			candidateBucket.setTfIdfCosineSimScoreNormalized(tfIdfScore);

			if (baseline.getReduceThreads()) {
				double bm25Score = bm25ScoreAnswerIdMap.get(candidateBucket.getId());
				bm25Score = (bm25Score / maxBm25Score); // normalization
				candidateBucket.setBm25ScoreNormalized(bm25Score);
			} else {
				candidateBucket.setBm25ScoreNormalized(0d);
			}
			// double bm25Score = bm25ScoreAnswerIdMap.get(candidateBucket.getId());
			/*
			 * double bm25Score = candidateBucket.getBm25Score(); bm25Score = (bm25Score /
			 * maxBm25Score); //normalization
			 * candidateBucket.setBm25ScoreNormalized(bm25Score);
			 */

			// double methodFreqScore =
			// CrokageComposer.calculateScoreForCommonMethods(bucket,methodsCounterMap,topFrequencyMethods);
			double methodFreqScore = CrokageComposer.calculateScoreForCommonMethodsDynamic(candidateBucket.getCode(),
					topMethodsCounterMap);
			candidateBucket.setMethodScore(methodFreqScore);

			double queryCoverage = CrarUtils.round(CrarUtils.calculateCoverage(processedQuery,
					loadBucketContent(candidateBucket, ContentTypeEnum.body_code.getId())), 2);
			candidateBucket.setQueryCoverageScore(queryCoverage);

			double codeCoverage = CrarUtils.round(CrarUtils.calculateCoverage(processedQuery,
					loadBucketContent(candidateBucket, ContentTypeEnum.code.getId())), 2);
			candidateBucket.setCodeCoverageScore(codeCoverage);

			/*
			 * Jaccard
			 */
			double jaccard = CrarUtils.round(CrarUtils.calculateExactJaccard(processedQuery,
					loadBucketContent(candidateBucket, ContentTypeEnum.title_body.getId())), 2);
			candidateBucket.setJaccardScore(jaccard);

			// double queryCoverage = CrarUtils.calculateCoverage(processedQuery,
			// loadBucketContent(candidateBucket,ContentTypeEnum.body.getId()));

			// double codeCoverage = CrarUtils.calculateCoverage(processedQuery,
			// loadBucketContent(candidateBucket,ContentTypeEnum.code.getId()));

			double apiAnswerPairScore = 0;
			if (!topAnswerParentPairsAnswerIdScoreMap.isEmpty()
					&& topAnswerParentPairsAnswerIdScoreMap.get(candidateBucket.getId()) != null) {
				apiAnswerPairScore = topAnswerParentPairsAnswerIdScoreMap.get(candidateBucket.getId());
				apiAnswerPairScore = (apiAnswerPairScore / maxApiScore); // normalization
			}
			if (maxApiScore == 0d) {
				apiAnswerPairScore = 0d;
			}
			candidateBucket.setApiScoreNormalized(apiAnswerPairScore);

			/*
			 * if(processedQuery.equals("insert element array position")) {
			 * if(candidateBucket.getParentId().equals(7074402)) {
			 * //getTitle().contains("insert object arraylist specific position")) {
			 * System.out.println("xx here"); } }
			 */
			double finalScore = CrokageComposer.calculateAnswersDynamicRankingScore(candidateBucket, baseline);
			// double finalScore =
			// CrokageComposer.calculateAnswersDynamicRankingScore(threadScore,asymmScore,tfIdfScore,methodFreqScore);
			// bucketsIdsScores.put(candidateBucket.getId(), finalScore);
			candidateBucket.setFinalScore(finalScore);

			/*
			 * if(rawQuery.contains("How do I parse a text string into date and time")) {
			 * //if(rawQuery.contains("insert an element in array at a given position")) {
			 * System.out.println("\n\n\nAfter \n bucketid: "+candidateBucket.getId()
			 * +"\ntf-idf: "+tfIdfScore); System.out.println("SimPair: "+asymmScore);
			 * System.out.println("methodFreqScore: "+methodFreqScore);
			 * System.out.println("Final Score: "+finalScore);
			 * System.out.println("Weights: sem:"+CrokageComposer.getAnswersAsymmScoreWeight
			 * ()+" - tf-idf:"+CrokageComposer.getAnswersTfIdfScoreWeight()+" -method:"
			 * +CrokageComposer.getAnswersMethodFreqScoreWeight()); }
			 */

			bucketsIdsScores.put(candidateBucket.getId(), finalScore);

		}

		Map<Integer, Double> topSimilarAnswers = bucketsIdsScores.entrySet().stream()
				.sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
				// .limit(topSimilarAnswersNumber)
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

		Set<Bucket> topAnswers = candidateBuckets.stream()
				.sorted(Comparator.comparingDouble(Bucket::getFinalScore).reversed())
				// .limit(topRelevantThreadsLimitRound1)
				.collect(Collectors.toCollection(LinkedHashSet::new));

		/*
		 * Set<Integer> topAnswersIds = topAnswers.stream() .map(e -> e.getId())
		 * .collect(Collectors.toCollection(LinkedHashSet::new));
		 */

		Map<Integer, Bucket> topAnswersMap = topAnswers.stream()
				.collect(Collectors.toMap(Bucket::getId, c -> c, (e1, e2) -> e1, LinkedHashMap::new));
		// .collect(Collectors.toMap(Bucket::getId, c -> c, LinkedHashMap::new));

		/*
		 * Map<Integer,Double> topSimilarAnswers = bucketsIdsScores.entrySet().stream()
		 * .sorted(Comparator.comparingDouble(Bucket::getFinalScore).reversed())
		 * //.sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
		 * //.limit(topSimilarAnswersNumber)
		 * .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) ->
		 * e1, LinkedHashMap::new));
		 */
		/*
		 * if(rawQuery.contains("Apply variable transparency to an image")){
		 * System.out.println("topSimilarAnswers: "+mapJoiner.join(topSimilarAnswers));
		 * }
		 */

		if (generateFeaturesFile) {
			Set<Bucket> topAnswersK = topAnswers.stream().limit(10)
					.collect(Collectors.toCollection(LinkedHashSet::new));
			Map<Integer, Bucket> topAnswersMapK = topAnswersK.stream()
					.collect(Collectors.toMap(Bucket::getId, c -> c, (e1, e2) -> e1, LinkedHashMap::new));

			generateAnswersCoverage(topAnswersK, groundTruthMap, rawQuery, processedQuery, bw, topAnswersMapK, tagId);
		}

		corpus = null;
		vectorSpace = null;
		candidateBuckets = null;
		queryDocument = null;
		// return topAnswersMap.keySet();
		return topAnswersMap.keySet();
	}

	/*
	 * 
	 * protected Set<Integer> getTopKRelevantBuckets(Set<Bucket> candidateBuckets,
	 * String processedQuery, Integer contentTypeTFIDF, Integer
	 * contentTypeSemanticSim, Integer tagId, String rawQuery) throws IOException {
	 * String comparingTitle; String parentTitle; String comparingContent;
	 * bucketsIdsScores.clear(); double maxSimPair=0; double maxApiScore=0; double
	 * maxTfIdfScore=0; double maxBm25Score=0; methodsCounterMap.clear();
	 * //classesCounterMap.clear(); //topicsCounterMap.clear(); documents.clear();
	 * Document queryDocument = new Document(processedQuery, 0);
	 * documents.add(queryDocument); //topAnswerParentPairsAnswerIdScoreMap.clear();
	 * 
	 * 
	 * //TD-IDF vocabulary for(Bucket bucket:candidateBuckets) { comparingContent =
	 * loadBucketContent(bucket,contentTypeTFIDF); Document document = new
	 * Document(comparingContent, bucket.getId()); documents.add(document);
	 * bucket.setDocument(document); }
	 * 
	 * Corpus corpus = new Corpus(documents); VectorSpaceModel vectorSpace = new
	 * VectorSpaceModel(corpus);
	 * 
	 * for(Bucket candidateBucket:candidateBuckets) { try {
	 * 
	 * 1- TF-IDF
	 * 
	 * double cosineSimScore = vectorSpace.cosineSimilarity(queryDocument,
	 * candidateBucket.getDocument());
	 * candidateBucket.setTfIdfCosineSimScore(cosineSimScore);
	 * if(cosineSimScore>maxTfIdfScore) { maxTfIdfScore=cosineSimScore; }
	 * 
	 * 
	 * 2- BM-25
	 * 
	 * double bm25Score = candidateBucket.getBm25Score();
	 * //candidateBucket.setBm25Score(bm25Score); //set before
	 * if(bm25Score>maxBm25Score) { maxBm25Score=bm25Score; }
	 * 
	 * 
	 * 3- Asymmetric Relevance
	 * 
	 * comparingContent = loadBucketContent(candidateBucket,contentTypeSemanticSim);
	 * double simPair = getAsymmetricSimilarity(processedQuery,comparingContent,
	 * tagId);
	 * 
	 * if(simPair==0d) { System.out.println("Huston, we have a problem !!"); throw
	 * new RuntimeException("Huston, we have a problem !!"); }
	 * candidateBucket.setSimPair(simPair); if(simPair>maxSimPair) {
	 * maxSimPair=simPair; }
	 * 
	 * 
	 * 
	 * 
	 * 4- Method score
	 * 
	 * if(CrokageComposer.getMethodFreqWeight()>0) {
	 * CrarUtils.countMethods(candidateBucket,tagId,methodsCounterMap); }
	 * 
	 * 
	 * 
	 * 6- Topics score
	 * 
	 * if(CrokageComposer.getTopicWeight()>0) { countTopics(candidateBucket); }
	 * 
	 * 
	 * 
	 * } catch (Exception e) {
	 * System.out.println("error here... post: "+candidateBucket.getId()); throw e;
	 * }
	 * 
	 * }
	 * 
	 * 
	 * 
	 * 
	 * Methods score
	 * 
	 * Map<String,Integer> topMethodsCounterMap =
	 * methodsCounterMap.entrySet().stream()
	 * .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
	 * .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) ->
	 * e1, LinkedHashMap::new));
	 * 
	 * 
	 * Topics score
	 * 
	 * Map<Integer,Integer> topTopicsCounterMap =
	 * topicsCounterMap.entrySet().stream()
	 * .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
	 * .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) ->
	 * e1, LinkedHashMap::new));
	 * 
	 * 
	 * 
	 * //normalization and other relevance boosts for(Bucket
	 * candidateBucket:candidateBuckets) { double simPair =
	 * candidateBucket.getSimPair(); simPair = (simPair / maxSimPair);
	 * //normalization
	 * 
	 * double tfIdfScore = candidateBucket.getTfIdfCosineSimScore(); tfIdfScore =
	 * (tfIdfScore / maxTfIdfScore); //normalization
	 * 
	 * double bm25Score = candidateBucket.getBm25Score(); bm25Score = (bm25Score /
	 * maxBm25Score); //normalization
	 * 
	 * 
	 * //double methodFreqScore =
	 * CrokageComposer.calculateScoreForCommonMethods(bucket,methodsCounterMap,
	 * topFrequencyMethods); double methodFreqScore = 0;
	 * if(CrokageComposer.getMethodFreqWeight()>0) { methodFreqScore =
	 * CrokageComposer.calculateScoreForCommonMethods(candidateBucket.getCode(),
	 * topMethodsCounterMap); }
	 * 
	 * double finalScore =
	 * CrokageComposer.calculateDynamicRankingScore(simPair,methodFreqScore,
	 * tfIdfScore,bm25Score); bucketsIdsScores.put(candidateBucket.getId(),
	 * finalScore);
	 * 
	 * }
	 * 
	 * Map<Integer,Double> topSimilarAnswers = bucketsIdsScores.entrySet().stream()
	 * .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
	 * //.limit(topSimilarAnswersNumber)
	 * .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) ->
	 * e1, LinkedHashMap::new));
	 * 
	 * if(rawQuery.contains("Apply variable transparency to an image")){
	 * System.out.println("topSimilarAnswers: "+mapJoiner.join(topSimilarAnswers));
	 * }
	 * 
	 * corpus=null; vectorSpace=null; candidateBuckets=null; queryDocument=null;
	 * return topSimilarAnswers.keySet(); }
	 * 
	 */

	public void loadModelsMapsAndIndices() throws Exception {
		System.out.println("loading models, maps and indices...");
		// TagEnum languages[] = {TagEnum.Java, TagEnum.Python, TagEnum.PHP};
		TagEnum languages[] = { TagEnum.Java };
		List<TagEnum> languagesList = Arrays.asList(languages);

		/*
		 * if(loadModels) { readSOContentWordAndVectorsForTags(languages); }
		 */

		// wordVectorsMapByTag will have vectors for all tags

		// loadUpvotedAnswersIdsWithCodeContentsAndParentContentsDyCrokageforTags(languagesList);
		// allBucketsWithUpvotesMapByTag will have answers for all tags
		loadValidThreadsForLanguages(languagesList);
		System.gc();
		loadValidAnswersForLanguages(languagesList);
		System.gc();
		// SearcherParams searchParam = new SearcherParams("AxiomaticF1LOG", new
		// AxiomaticF1LOG());
		System.out.println("indexing with BM25");

		if (languagesList.contains(TagEnum.Java)) {
			SearcherParams searchParam = new SearcherParams("BM25Similarity Java", new BM25Similarity(1.2f, 0.9f));
			// SearcherParams searchParam = new SearcherParams("AxiomaticF1LOG", new
			// AxiomaticF1LOG());
			// allBucketsWithUpvotesMapJava =
			// allBucketsWithUpvotesMapByTag.get(TagEnum.Java.getId());
			System.out.println("indexing documents on lucene for " + TagEnum.Java.getDescricao());
			// luceneSearcherThreadsJava.buildSearchManagerLemma(allBucketsWithUpvotesMapJava,searchParam
			// );
			luceneSearcherThreadsJava.buildSearchManagerForThreads(allValidThreadsMapByTag.get(TagEnum.Java.getId()),
					searchParam);
			System.gc();
			luceneSearcherAnswersJava.buildSearchManager(allBucketsWithUpvotesMapByTag.get(TagEnum.Java.getId()),
					searchParam);
			System.gc();
		}
		if (languagesList.contains(TagEnum.PHP)) {
			SearcherParams searchParam = new SearcherParams("BM25Similarity Php", new BM25Similarity(1.3f, 0.6f));
			// allBucketsWithUpvotesMapPhp =
			// allBucketsWithUpvotesMapByTag.get(TagEnum.PHP.getId());
			System.out.println("indexing documents on lucene for " + TagEnum.PHP.getDescricao());
			// luceneSearcherThreadsPhp.buildSearchManagerLemma(allBucketsWithUpvotesMapPhp,searchParam
			// );
			luceneSearcherThreadsPhp.buildSearchManagerForThreads(allValidThreadsMapByTag.get(TagEnum.PHP.getId()),
					searchParam);
			luceneSearcherAnswersPhp.buildSearchManager(allBucketsWithUpvotesMapByTag.get(TagEnum.PHP.getId()),
					searchParam);
		}
		if (languagesList.contains(TagEnum.Python)) {
			SearcherParams searchParam = new SearcherParams("BM25Similarity Python", new BM25Similarity(0.5f, 0.9f));
			// allBucketsWithUpvotesMapPython =
			// allBucketsWithUpvotesMapByTag.get(TagEnum.Python.getId());
			System.out.println("indexing documents on lucene for " + TagEnum.Python.getDescricao());
			// luceneSearcherThreadsPython.buildSearchManagerLemma(allBucketsWithUpvotesMapPython,searchParam
			// );
			luceneSearcherThreadsPython
					.buildSearchManagerForThreads(allValidThreadsMapByTag.get(TagEnum.Python.getId()), searchParam);
			luceneSearcherAnswersPython.buildSearchManager(allBucketsWithUpvotesMapByTag.get(TagEnum.Python.getId()),
					searchParam);
		}

	}

	/*
	 * public Set<Bucket> getCandidadeBucketsServer(Query query) throws Exception {
	 * String processedQuery; Set<Integer> luceneSmallSetIds = new
	 * LinkedHashSet<>(); Map<Integer, Bucket> allBucketsWithUpvotesMap = null;
	 * 
	 * Map<Integer, Float> bm25ScoreAnswerIdMap = new LinkedHashMap<>();
	 * processedQuery = CrarUtils.processQuery(query.getQueryText(),true);
	 * if(query.getTagId().equals(TagEnum.Java.getId())) { luceneSmallSetIds =
	 * luceneSearcherThreadsJava.search(processedQuery,
	 * query.getBm25TopNSmallLimit(),bm25ScoreAnswerIdMap); allBucketsWithUpvotesMap
	 * = allBucketsWithUpvotesMapJava; }else
	 * if(query.getTagId().equals(TagEnum.Python.getId())) { luceneSmallSetIds =
	 * luceneSearcherThreadsPython.search(processedQuery,
	 * query.getBm25TopNSmallLimit(),bm25ScoreAnswerIdMap); allBucketsWithUpvotesMap
	 * = allBucketsWithUpvotesMapPython; }else
	 * if(query.getTagId().equals(TagEnum.PHP.getId())) { luceneSmallSetIds =
	 * luceneSearcherThreadsPhp.search(processedQuery,
	 * query.getBm25TopNSmallLimit(),bm25ScoreAnswerIdMap); allBucketsWithUpvotesMap
	 * = allBucketsWithUpvotesMapPhp; }
	 * 
	 * Set<Bucket> candidateBuckets = new LinkedHashSet<>(); for(Integer answerId:
	 * luceneSmallSetIds) { Bucket bucket = allBucketsWithUpvotesMap.get(answerId);
	 * bucket.setBm25Score((double)bm25ScoreAnswerIdMap.get(answerId));
	 * candidateBuckets.add(bucket); } luceneSmallSetIds=null;
	 * bm25ScoreAnswerIdMap=null; allBucketsWithUpvotesMap = null; return
	 * candidateBuckets; }
	 */

	public Set<ThreadContent> getCandidadeThreadsServer(Query query) throws Exception {
		String processedQuery = query.getQueryText(); // already preprocessed
		Set<Integer> threadsIds = new LinkedHashSet<>();
		Map<Integer, ThreadContent> validThreadsMap = allValidThreadsMapByTag.get(query.getTagId());
		LuceneSearcher searcher = null;

		Map<Integer, Float> idScoreMap = new LinkedHashMap<>();
		// processedQuery = CrarUtils.processQuery(query.getQueryText(),useLemma);
		if (query.getTagId().equals(TagEnum.Java.getId())) {
			searcher = luceneSearcherThreadsJava;
		} else if (query.getTagId().equals(TagEnum.Python.getId())) {
			searcher = luceneSearcherThreadsPython;
		} else if (query.getTagId().equals(TagEnum.PHP.getId())) {
			searcher = luceneSearcherThreadsPhp;
		}
		threadsIds = searcher.search(processedQuery, query.getTrmLimit1(), idScoreMap);

		Set<ThreadContent> candidateThreads = new LinkedHashSet<>();
		for (Integer threadId : threadsIds) {
			ThreadContent thread = validThreadsMap.get(threadId);
			thread.setBm25Score((double) idScoreMap.get(threadId));
			candidateThreads.add(thread);
		}
		threadsIds = null;
		idScoreMap = null;
		validThreadsMap = null;
		searcher = null;
		return candidateThreads;
	}

	public Set<Bucket> getCandidadeAnswersServer(Query query) throws Exception {
		String processedQuery = query.getQueryText(); // already preprocessed
		Set<Integer> answersIds = new LinkedHashSet<>();
		Map<Integer, Bucket> bucketsMap = allBucketsWithUpvotesMapByTag.get(query.getTagId());
		LuceneSearcher searcher = null;

		Map<Integer, Float> idScoreMap = new LinkedHashMap<>();
		// processedQuery = CrarUtils.processQuery(query.getQueryText(),useLemma);
		if (query.getTagId().equals(TagEnum.Java.getId())) {
			searcher = luceneSearcherAnswersJava;
		} else if (query.getTagId().equals(TagEnum.Python.getId())) {
			searcher = luceneSearcherAnswersPython;
		} else if (query.getTagId().equals(TagEnum.PHP.getId())) {
			searcher = luceneSearcherAnswersPhp;
		}
		answersIds = searcher.search(processedQuery, query.getTrmLimit1(), idScoreMap);

		Set<Bucket> candidateBuckets = new LinkedHashSet<>();
		for (Integer id : answersIds) {
			Bucket thread = bucketsMap.get(id);
			thread.setBm25Score((double) idScoreMap.get(id));
			candidateBuckets.add(thread);
		}
		answersIds = null;
		idScoreMap = null;
		bucketsMap = null;
		searcher = null;
		return candidateBuckets;
	}

	public Double getAsymmetricSimilarityServer(Query query) {
		double simPair = CrarUtils.getSimPair(query.getComparingContent(), query.getQueryText(),
				wordVectorsMapByTag.get(query.getTagId()), query.getVectorsTypeId());
		return simPair;
	}

	protected void loadValidAnswersForLanguages(List<TagEnum> languagesList) {
		allBucketsWithUpvotesMapByTag.clear();
		long initTime2 = System.currentTimeMillis();
		for (TagEnum language : languagesList) {
			int tagId = TagEnum.getTagIdByTagEnum(language);
			System.out.println("loading answers for tag:" + tagId + "...");
			Map<Integer, Bucket> filteredAnswersMap = new LinkedHashMap<>();
			List<Bucket> buckets = crarService.getUpvotedAnswersIdsContentsAndParentContents(tagId);
			System.out.println("total answers for tag:" + tagId + " - " + buckets.size());
			for (Bucket bucket : buckets) {
				filteredAnswersMap.put(bucket.getId(), bucket);
			}
			allBucketsWithUpvotesMapByTag.put(tagId, filteredAnswersMap);
			System.out.println("upvoted answers filtered for tag:" + tagId + " - " + filteredAnswersMap.size());
			CrarUtils.reportElapsedTime(initTime2, "loading answers for tag " + tagId);
		}
	}

	protected void loadValidThreadsForLanguages(List<TagEnum> languagesList) {
		long initTime2 = System.currentTimeMillis();
		for (TagEnum language : languagesList) {
			int tagId = TagEnum.getTagIdByTagEnum(language);
			System.out.println("loading threads for tag:" + tagId + "...");
			Map<Integer, ThreadContent> filteredThreadsMap = new LinkedHashMap<>();
			List<ThreadContent> threads = crarService.getThreadsByTagId(tagId);
			System.out.println("upvoted threads with answers for tag:" + tagId + " - " + threads.size()
					+ ". Now converting sentence vectors...");
			for (ThreadContent thread : threads) {
				/*
				 * if(thread.getUpvotes()>0) { filteredThreadsMap.put(thread.getId(), thread); }
				 */
				thread.setSentenceVectors(CrarUtils.getVectorsFromString((String) thread.getSentenceVectorsStr()));
				thread.setSentenceVectorsStr(null);
				filteredThreadsMap.put(thread.getId(), thread);
			}
			System.out.println("converted vectors !");
			allValidThreadsMapByTag.put(tagId, filteredThreadsMap);
			// System.out.println("upvoted threads filtered for tag:"+tagId+" -
			// "+filteredThreadsMap.size());
			CrarUtils.reportElapsedTime(initTime2, "loading threads for tag " + tagId);
		}
	}

	/*
	 * Build threads in such a way that: 1- At least one upvoted answer must exist
	 * 2- Only containing upvoted answers 3- At least one answer must contain code
	 * 4- Only upvoted questions
	 */
	private void buildThreads() {
		Integer tagsIds[] = { TagEnum.Java.getId(), TagEnum.Python.getId(), TagEnum.PHP.getId() };

		for (Integer tagId : tagsIds) {
			System.out.println("\n\nPreparing threads for tag: " + TagEnum.getPostType(tagId).getDescricao());
			int count = 0;
			List<Bucket> parents = crarService.getUpvotedQuestionsWithAnswersForTag(tagId, useLemma);
			System.out.println("Number of upvoted questions with answers: " + parents.size() + " for: "
					+ TagEnum.getPostType(tagId).getDescricao());

			for (Bucket parent : parents) {
				StringBuilder answersBodyContent = new StringBuilder();
				StringBuilder codeContent = new StringBuilder();
				int totalUpvotes = parent.getScore();
				int totalComments = parent.getCommentCount();

				// answersBodyContent.append(parent.getProcessedBodyLemma());
				// answersBodyContent.append(" ");
				codeContent.append(parent.getProcessedCode());
				codeContent.append(" ");

				if (count % 100000 == 0) {
					System.out.println("Processed " + count + " of " + parents.size());
				}
				count++;
				List<Bucket> upvotedAnswersContainingCode = crarService.getUpvotedAnswersForQuestion(parent.getId(),
						useLemma);

				if (!upvotedAnswersContainingCode.isEmpty()) {
					// at least one must contain code
					/*
					 * long containCode = upvotedAnswers.stream() .filter(c ->
					 * c.getContainCode().equals(true)) .count();
					 */
					// if(containCode>0) {
					// if(!upvotedAnswersWithCode.isEmpty()) {

					for (Bucket answer : upvotedAnswersContainingCode) {
						if (useLemma) {
							answersBodyContent.append(answer.getProcessedBodyLemma());
						} else {
							answersBodyContent.append(answer.getProcessedBody());
						}
						answersBodyContent.append(" ");
						codeContent.append(answer.getProcessedCode());
						codeContent.append(" ");
						totalUpvotes += answer.getScore();
						totalComments += answer.getCommentCount();
					}
					ThreadContent threadContent = new ThreadContent(parent.getId(),
							useLemma ? parent.getProcessedTitleLemma() : parent.getProcessedTitle(),
							useLemma ? parent.getProcessedBodyLemma() : parent.getProcessedBody(),
							StringUtils.normalizeSpace(answersBodyContent.toString()),
							StringUtils.normalizeSpace(codeContent.toString()), parent.getAnswerCount(), totalUpvotes,
							totalComments, tagId, parent.getScore());
					crarService.saveThread(threadContent);
					// }
				}
				answersBodyContent = null;
				codeContent = null;
				upvotedAnswersContainingCode = null;
			}
			parents = null;

		}

	}

	/*
	 * private Map<Integer, ThreadContent> reduceThreadsDetached(Set<ThreadContent>
	 * candidateThreads, String processedQuery, Integer tagId, String rawQuery, int
	 * topRelevantThreadsLimitRound1,int topRelevantThreadsLimitRound2, Integer
	 * contentToMatch, int rounds) { double maxTitleAsymmSim=0; double
	 * maxBodyAsymmSim=0; double maxTfIdfScore=0; double maxBm25Score=0; //double
	 * maxScore=0; double maxAnswerCount=0; double maxViewCount=0; double
	 * maxTotalUpvotes=0; String comparingContent;
	 * 
	 * documents.clear(); Document queryDocument = new Document(processedQuery, 0);
	 * documents.add(queryDocument);
	 * 
	 * for(ThreadContent bucket:candidateThreads) { comparingContent =
	 * loadThreadContent(bucket,contentToMatch); Document document = new
	 * Document(comparingContent, bucket.getId()); documents.add(document);
	 * bucket.setDocument(document); }
	 * 
	 * Corpus corpus = new Corpus(documents); VectorSpaceModel vectorSpace = new
	 * VectorSpaceModel(corpus);
	 * 
	 * for(ThreadContent candidateThread:candidateThreads) {
	 * 
	 * Asymmetric Relevance
	 * 
	 * comparingContent=candidateThread.getTitle(); double threadTitleAsymmetricSim
	 * = getAsymmetricSimilarity(processedQuery,comparingContent, tagId);
	 * candidateThread.setThreadTitleAsymmetricSim(threadTitleAsymmetricSim);
	 * if(threadTitleAsymmetricSim>maxTitleAsymmSim) {
	 * maxTitleAsymmSim=threadTitleAsymmetricSim; }
	 * 
	 * comparingContent=candidateThread.getQuestionBody()+" "+candidateThread.
	 * getAnswersBody(); double threadBodyAsymmetricSim =
	 * getAsymmetricSimilarity(processedQuery,comparingContent, tagId);
	 * candidateThread.setThreadQuestionBodyAnswersBodyAsymmetricSim(
	 * threadBodyAsymmetricSim); if(threadBodyAsymmetricSim>maxBodyAsymmSim) {
	 * maxBodyAsymmSim=threadBodyAsymmetricSim; }
	 * 
	 * 
	 * TF-IDF
	 * 
	 * double tfIdfCosineSimScore = vectorSpace.cosineSimilarity(queryDocument,
	 * candidateThread.getDocument());
	 * candidateThread.setTfIdfCosineSimScore(tfIdfCosineSimScore);
	 * if(tfIdfCosineSimScore>maxTfIdfScore) { maxTfIdfScore=tfIdfCosineSimScore; }
	 * 
	 * }
	 * 
	 * for(ThreadContent candidateThread:candidateThreads) {
	 * //candidateThread.setThreadSim(simPair);
	 * //threadSimMap.put(candidateThread.getId(),simPair); double
	 * threadTitleAsymmetricSim = candidateThread.getThreadTitleAsymmetricSim();
	 * threadTitleAsymmetricSim = (threadTitleAsymmetricSim / maxTitleAsymmSim);
	 * //normalization
	 * candidateThread.setThreadTitleAsymmetricSim(threadTitleAsymmetricSim);
	 * 
	 * double threadBodyAsymmetricSim =
	 * candidateThread.getThreadQuestionBodyAnswersBodyAsymmetricSim();
	 * threadBodyAsymmetricSim = (threadBodyAsymmetricSim / maxBodyAsymmSim);
	 * //normalization
	 * candidateThread.setThreadTitleAsymmetricSim(threadBodyAsymmetricSim);
	 * 
	 * double tfIdfCosineSimScore = candidateThread.getTfIdfCosineSimScore();
	 * tfIdfCosineSimScore = (tfIdfCosineSimScore / maxTfIdfScore); //normalization
	 * candidateThread.setTfIdfCosineSimScore(tfIdfCosineSimScore);
	 * 
	 * }
	 * 
	 * 
	 * 
	 * 
	 * Set<ThreadContent> topThreadsTitleAsymm = candidateThreads.stream()
	 * .sorted(Comparator.comparingDouble(ThreadContent::getThreadTitleAsymmetricSim
	 * ).reversed()) .limit(topRelevantThreadsLimitRound1)
	 * .collect(Collectors.toCollection(LinkedHashSet::new));
	 * 
	 * Set<ThreadContent> topThreadsBodyAsymm = candidateThreads.stream()
	 * .sorted(Comparator.comparingDouble(ThreadContent::
	 * getThreadQuestionBodyAnswersBodyAsymmetricSim).reversed())
	 * .limit(topRelevantThreadsLimitRound1)
	 * .collect(Collectors.toCollection(LinkedHashSet::new));
	 * 
	 * Set<ThreadContent> topThreadsTFIDFAsymm = candidateThreads.stream()
	 * .sorted(Comparator.comparingDouble(ThreadContent::getTfIdfCosineSimScore).
	 * reversed()) .limit(topRelevantThreadsLimitRound1)
	 * .collect(Collectors.toCollection(LinkedHashSet::new));
	 * 
	 * Set<ThreadContent> topThreads = new LinkedHashSet<>();
	 * topThreads.addAll(topThreadsTitleAsymm);
	 * topThreads.addAll(topThreadsBodyAsymm);
	 * topThreads.addAll(topThreadsTFIDFAsymm);
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * if(rounds==1) { Map<Integer, ThreadContent> topThreadsMap =
	 * topThreads.stream() //.limit(3*topRelevantThreadsLimitRound1)
	 * .collect(Collectors.toMap(ThreadContent::getId, c -> c)); return
	 * topThreadsMap; }
	 * 
	 * //round 2
	 * 
	 * for(ThreadContent candidateThread:topThreads) {
	 * 
	 * Answer count
	 * 
	 * double answerCount = candidateThread.getAnswerCount();
	 * if(answerCount>maxAnswerCount) { maxAnswerCount=answerCount; }
	 * 
	 * 
	 * Thread upvotes
	 * 
	 * double threadTotalUpvotes = candidateThread.getThreadTotalUpvotes();
	 * if(threadTotalUpvotes>maxTotalUpvotes) { maxTotalUpvotes=threadTotalUpvotes;
	 * }
	 * 
	 * 
	 * }
	 * 
	 * for(ThreadContent candidateThread:topThreads) { double
	 * threadTitleAsymmetricSim = candidateThread.getThreadTitleAsymmetricSim();
	 * double threadBodyAsymmetricSim =
	 * candidateThread.getThreadQuestionBodyAnswersBodyAsymmetricSim(); double
	 * tfIdfCosineSimScore = candidateThread.getTfIdfCosineSimScore();
	 * 
	 * double threadAnswerCountScore =
	 * CrarUtils.round((candidateThread.getAnswerCount() / maxAnswerCount),1);
	 * //normalization
	 * //candidateThread.setThreadAnswerCountScore(threadAnswerCountScore);
	 * 
	 * 
	 * Upvotes
	 * 
	 * double threadUpVotesScore =
	 * CrokageComposer.calculateUpScore(candidateThread.getUpvotes());
	 * 
	 * 
	 * Total Upvotes
	 * 
	 * double threadTotalUpvotes =
	 * CrarUtils.round((candidateThread.getThreadTotalUpvotes() /
	 * maxTotalUpvotes),1); //normalization
	 * //candidateThread.setThreadTotalUpVotesScore(threadTotalUpvotes);
	 * 
	 * 
	 * 
	 * // double finalScoreStage2 =
	 * CrokageComposer.calculateThreadsRankingScore(threadTitleAsymmetricSim,
	 * threadBodyAsymmetricSim,tfIdfCosineSimScore,threadAnswerCountScore,
	 * threadUpVotesScore,threadTotalUpvotes); //
	 * candidateThread.setThreadFinalScore(finalScoreStage2);
	 * 
	 * }
	 * 
	 * Map<Integer, ThreadContent> topThreadsMap = topThreads.stream()
	 * .sorted(Comparator.comparingDouble(ThreadContent::getThreadFinalScore).
	 * reversed()) .limit(topRelevantThreadsLimitRound2)
	 * .collect(Collectors.toMap(ThreadContent::getId, c -> c));
	 * 
	 * 
	 * return topThreadsMap; }
	 */
	
	/*
	 	private void exportThreadContent() throws IOException {
		Map<String, Set<Integer>> groundTruthQuestionsIdsTrainingMap = new LinkedHashMap<>();
		Map<String, Set<Integer>> groundTruthQuestionsIdsTestMap = new LinkedHashMap<>();

		Map<String, List<ThreadContent>> groundTruthQuestionsTrainingMap = new LinkedHashMap<>();
		Map<String, List<ThreadContent>> groundTruthQuestionsTestMap = new LinkedHashMap<>();

		loadGroundTruthSelectedQueriesForQuestions("java-training", groundTruthQuestionsIdsTrainingMap);
		loadGroundTruthSelectedQueriesForQuestions("java-test", groundTruthQuestionsIdsTestMap);

		Set<ThreadContent> threads = getAllThreadsClient(1); // 1-java
		StringBuilder builder = new StringBuilder();
		StringBuilder tokens = new StringBuilder();

		System.out.println("ThreadContent " + threads.size());
		for (ThreadContent tc : threads) {
			builder.append(tc.getId() + " >> " + tc.getTitle()).append("\n");
			tokens.append(tc.getTitle()).append("\n");
		}

		for (String query : groundTruthQuestionsIdsTrainingMap.keySet()) {
			Set<Integer> questionsIds = groundTruthQuestionsIdsTrainingMap.get(query);
			List<ThreadContent> tc = crarService.getThreadContentsByIds(questionsIds);
			for (ThreadContent t : tc) {
				tokens.append(CrarUtils.processQuery(query, false) + " " + t.getTitle() + "\n");
			}
		}

		FileHelper.outputToFile(DL_EMBEDDING_CORPUS, tokens, false);
		FileHelper.outputToFile(DL_EMBEDDED_INPUT_TRAINING, tokens, false);
		FileHelper.outputToFile(DL_EMBEDDING_CORPUS_ID, builder, false);

		builder.setLength(0);
		tokens.setLength(0);

		for (String query : groundTruthQuestionsIdsTestMap.keySet()) {
			tokens.append(query + "\n");
		}

		FileHelper.outputToFile(DL_EMBEDDED_INPUT_TESTING, tokens, false);
	}

	private void generateEmbeddings() throws IOException {
		TokensEmbedder embedder = new TokensEmbedder();
		embedder.inputPath = DL_EMBEDDING_INPUT_PATH;
		embedder.outputPath = DL_EMBEDDING_OUTPUT_PATH;
		embedder.mergeData(false);
		embedder.embedTokens();
		embedder.vectorizedData(false);
	}

	private void trainDL() throws Exception {
		/*
		 * long initTime = 0l; initTime = System.currentTimeMillis();
		 * CrarUtils.reportElapsedTime(initTime, "trainRNN");
		 * System.out.println("Training the RNN...");
		 * 
		 * int batchSize = 1024;
		 * 
		 * CnnFeatureExtractor learner = new CnnFeatureExtractor(SIZE_TOKENS,
		 * SIZE_EMBEDDING, batchSize, SIZE_VECTOR); learner.setNumberOfEpochs(EPOCHS);
		 * learner.setSeed(123); learner.setNumOfOutOfLayer1(20);
		 * learner.setNumOfOutOfLayer2(50);
		 * 
		 * learner.extracteFeaturesWithCNN(DL_CNN_INTPUT_TRAINING,
		 * DL_CNN_OUTPUT_TRAINING);
		 * 
		 * initTime = System.currentTimeMillis();
		 */
/*	}

	public void analyseDL() throws Exception {
		StringBuilder output = new StringBuilder();
		Map<String, double[]> testingDL = CrarUtils.readQueriesAndVectors(DL_CNN_INTPUT_TESTING, ",");

		Map<String, Set<Integer>> groundTruthQuestionsIdsTestMap = new LinkedHashMap<>();
		Map<String, List<ThreadContent>> groundTruthQuestionsTestMap = new LinkedHashMap<>();
		loadGroundTruthSelectedQueriesForQuestions("java-test", groundTruthQuestionsIdsTestMap);

		for (String query : groundTruthQuestionsIdsTestMap.keySet()) {
			Set<Integer> answersIds = groundTruthQuestionsIdsTestMap.get(query);
			groundTruthQuestionsTestMap.put(query, crarService.getThreadContentsByIds(answersIds));
		}

		CnnFeatureExtractor learner = new CnnFeatureExtractor(SIZE_TOKENS, SIZE_EMBEDDING, 1, SIZE_VECTOR);
		learner.setNumberOfEpochs(EPOCHS);
		learner.setSeed(123);
		learner.setNumOfOutOfLayer1(20);
		learner.setNumOfOutOfLayer2(50);
		MultiLayerNetwork model = learner.getModel(DL_CNN_OUTPUT_TRAINING);

		List<ThreadNota> notas = new ArrayList();
		int cont = 0;
		BufferedReader br = new BufferedReader(new FileReader(
				new File(DL_EMBEDDING_OUTPUT_PATH + "SelectedMethodTokens_id_output.txtSelectedMethodTokens_id.csv")));
		while (br.ready()) {
			String line = br.readLine();
			cont++;
			String[] parts = line.split(">>");
			try {
				double[] vectors = CrarUtils.getVectorsFromString(parts[1].trim(), ",");
				double[] outputCnn = learner.getOutput(model, vectors);
				notas.add(new ThreadNota(new Integer(parts[0].trim()), outputCnn));
			} catch (Exception e) {
				output.append("ih deu pau " + parts[1].trim() + "\n");
				System.out.println("ih deu pau " + parts[1].trim());
			}

			if (cont % 10000 == 0) {
				output.append("Read " + cont + "\n");
				System.out.println("Read " + cont);
			}
		}
		br.close();
		Map<String, Set<Integer>> recommendedResults = new LinkedHashMap<>();
		for (String query : testingDL.keySet()) {
			double[] embeddingsQuery = testingDL.get(query);
			double[] outputCnn = learner.getOutput(model, embeddingsQuery);
			for (ThreadNota thread : notas) {
				thread.nota = CosineMeasure.getCosineSimilarity(outputCnn, thread.embeddings);
			}
			Comparator<ThreadNota> compareById = (ThreadNota o1, ThreadNota o2) -> o2.nota.compareTo(o1.nota);
			Collections.sort(notas, compareById);
			Set<Integer> bestAnswers = new LinkedHashSet();
			for (int i = 0; i < 10; i++) {
				output.append("Adicionando na lista " + notas.get(i).id + " " + notas.get(i).nota + "\n");
				System.out.println("Adicionando na lista " + notas.get(i).id + " " + notas.get(i).nota);
				bestAnswers.add(notas.get(i).id);
			}

			recommendedResults.put(query, bestAnswers);
		}
		String approachName = "RNN by CADU";
		MetricResult metricResult = new MetricResult(approachName, 0, 0, 0, 0, 0, 10, "approach RNN for top 10", 0,
				null, null);
		output.append(analyzeResults(recommendedResults, groundTruthQuestionsIdsTestMap, metricResult, approachName));
		crarService.saveMetricResult(metricResult);
		FileHelper.outputToFile("/home/rodrigo/output.txt", output, false);
	}

	/*
	 * public void analyseDL() throws Exception { /*TagEnum languages[] = {
	 * TagEnum.Java }; List<TagEnum> languagesList = Arrays.asList(languages);
	 * readSOContentWordAndVectorsForTags(languages); Map<String,
	 * SoContentWordVector> embMap = wordVectorsMapByTag.get(1);
	 */
	/*
	 * Map<String,double[]> queriesAndSent2Vectors =
	 * CrarUtils.readQueriesAndVectors(CRAR_HOME+"/data/test.txt");
	 * 
	 * int counter = 0; Set<ThreadContent> threads = getAllThreadsClient(1); //
	 * 1-java StringBuilder builder = new StringBuilder();
	 * 
	 * Map<String, Set<Integer>> groundTruthQuestionsIdsTestMap = new
	 * LinkedHashMap<>(); Map<String, List<ThreadContent>>
	 * groundTruthQuestionsTestMap = new LinkedHashMap<>();
	 * loadGroundTruthSelectedQueriesForQuestions("java-test",
	 * groundTruthQuestionsIdsTestMap);
	 * 
	 * for (String query : groundTruthQuestionsIdsTestMap.keySet()) { Set<Integer>
	 * answersIds = groundTruthQuestionsIdsTestMap.get(query);
	 * groundTruthQuestionsTestMap.put(query,
	 * crarService.getThreadContentsByIds(answersIds)); }
	 * 
	 * CnnFeatureExtractor learner = new CnnFeatureExtractor(SIZE_TOKENS,
	 * SIZE_EMBEDDING, 1, SIZE_VECTOR); learner.setNumberOfEpochs(EPOCHS);
	 * learner.setSeed(123); learner.setNumOfOutOfLayer1(20);
	 * learner.setNumOfOutOfLayer2(50); MultiLayerNetwork model =
	 * learner.getModel(DL_CNN_OUTPUT_TRAINING);
	 * 
	 * for (ThreadContent tc: threads) { //List<String> methodBodyTokens =
	 * Arrays.asList((tc.getTitle()+" "+tc.getQuestionBody()).split(" "));
	 * //double[] vectorizedTokenVector = vectorizeDouble(methodBodyTokens, embMap);
	 * //double[] output = learner.getOutput(model,vectorizedTokenVector); double[]
	 * output = learner.getOutput(model,tc.getSentenceVectors());
	 * tc.setEmbeddedDLOutput(output); }
	 * 
	 * int qtas = 0;
	 * 
	 * //for (String query: groundTruthQuestionsTestMap.keySet()) { for (String
	 * query: queriesAndSent2Vectors.keySet()) { //List<String> queryBodyTokens =
	 * Arrays.asList(CrarUtils.processQuery(query,false).split(" ")); //double[]
	 * vetores = vectorizeDouble(queryBodyTokens, embMap); //double[] embeddedTokens
	 * = learner.getOutput(model,vetores); double[] embeddedTokens =
	 * learner.getOutput(model,queriesAndSent2Vectors.get(query));
	 * System.out.println("finding best titles for "+qtas++);
	 * 
	 * double maior = -1;
	 * 
	 * for (ThreadContent tc: threads) { double nota =
	 * CosineMeasure.getCosineSimilarity(embeddedTokens,tc.getEmbeddedDLOutput());
	 * if (nota > maior) { maior = nota; } tc.setTfIdfCosineSimScore(nota); }
	 * System.out.println("maior nota "+maior); maior = -1;
	 * 
	 * Comparator<ThreadContent> compareById = (ThreadContent o1, ThreadContent o2)
	 * -> o2.getTfIdfCosineSimScore().compareTo( o1.getTfIdfCosineSimScore() );
	 * List<ThreadContent> list = new ArrayList(threads);
	 * Collections.sort(list,compareById);
	 * 
	 * Set<Integer> bestAnswers = new LinkedHashSet(); for (int i = 0; i < 10;i++) {
	 * System.out.println("Adicionando na lista "+list.get(i).getId()+" "+list.get(i
	 * ).getTfIdfCosineSimScore()); bestAnswers.add(list.get(i).getId()); }
	 * 
	 * 
	 * Map<String, Set<Integer>> recommendedResults = new LinkedHashMap<>();
	 * 
	 * recommendedResults.put(query,bestAnswers); String approachName =
	 * "RNN by CADU"; MetricResult metricResult = new
	 * MetricResult(approachName,0,0,0,0,0,10,
	 * "approach RNN for top 10",0,null,null);
	 * analyzeResults(recommendedResults,groundTruthQuestionsIdsTestMap,
	 * metricResult, approachName); crarService.saveMetricResult(metricResult);
	 * 
	 * }
	 * 
	 * }
	 * 
	 * 	private void generateCnnOnlyResult() throws Exception {
		String cnnoutput = "/home/rodrigo/cnnouput.zip";
		TagEnum languages[] = { TagEnum.Java };
		List<TagEnum> languagesList = Arrays.asList(languages);
		readSOContentWordAndVectorsForTags(languages);
		Map<String, SoContentWordVector> embMap = wordVectorsMapByTag.get(1);
		int counter = 0;
		loadValidThreadsForLanguages(languagesList);
		System.gc();

		String languageDataSet = "java" + "-" + "test";
		Map<String, Set<Integer>> groundTruthThreadsMap = new LinkedHashMap<>();
		loadGroundTruthSelectedQueriesForQuestions(languageDataSet, groundTruthThreadsMap);

		/*
		 * for (String query : groundTruthQuestionsIdsTestMap.keySet()) { Set<Integer>
		 * answersIds = groundTruthQuestionsIdsTestMap.get(query);
		 * groundTruthQuestionsTestMap.put(query,
		 * crarService.getThreadContentsByIds(answersIds)); }
		 */
/*
		learner = new CnnFeatureExtractor(SIZE_TOKENS, 100, 1, 100);

		List<ThreadNota> notas = new ArrayList();

		this.model = learner.getModel(cnnoutput);
		Set<Integer> languagesDLL = allValidThreadsMapByTag.keySet();
		for (Integer language : languagesDLL) {
			Set<Integer> threadsIds = allValidThreadsMapByTag.get(language).keySet();
			for (Integer threadId : threadsIds) {
				counter++;
				if (counter % 10000 == 0) {
					System.out.println("CNN processed for " + counter + " threads");
				}
				ThreadContent t = allValidThreadsMapByTag.get(language).get(threadId);
				List<String> queryBodyTokens = Arrays
						.asList((t.getTitle()).split(" "));
				double[] vectorizedTokenVector = CrarUtils.vectorizeDouble(queryBodyTokens, embMap, SIZE_TOKENS, 100);
				double[] output = learner.getOutput(this.model, vectorizedTokenVector);
				// t.setEmbeddedDLOutput(output);
				notas.add(new ThreadNota(threadId, output));
			}
		}
		System.gc();

		Map<String, Set<Integer>> recommendedResults = new LinkedHashMap<>();
		
		for (String query : groundTruthThreadsMap.keySet()) {
			System.out.println("query "+query);
			double[] outputCnn = getCnnOutput(CrarUtils.processQuery(query, false), SIZE_TOKENS, 100);			
			for (ThreadNota thread : notas) {
				thread.nota = CosineMeasure.getCosineSimilarity(outputCnn, thread.embeddings);
			}
			System.out.println(query);
			Comparator<ThreadNota> compareById = (ThreadNota o1, ThreadNota o2) -> o2.nota.compareTo(o1.nota);
			Collections.sort(notas, compareById);
			Set<Integer> bestAnswers = new LinkedHashSet();
			for (int i = 0; i < 10; i++) {
				System.out.println(notas.get(i).id+" "+notas.get(i).nota);
				bestAnswers.add(notas.get(i).id);
			}
			recommendedResults.put(query, bestAnswers);
		}
		String approachName = "RNN by CADU";
		MetricResult metricResult = new MetricResult(approachName, 0, 0, 0, 0, 0, 10, "approach RNN for top 10", 0,
				null, null);
		analyzeResults(recommendedResults, groundTruthThreadsMap, metricResult, approachName);
		crarService.saveMetricResult(metricResult);
	}		

	public void processSent2Vec() throws Exception {
		String languageDataSet = "java" + "-" + "test";
		Map<String, Set<Integer>> groundTruthThreadsMap = new LinkedHashMap<>();
		loadGroundTruthSelectedQueriesForQuestions(languageDataSet, groundTruthThreadsMap);
		
		TagEnum languages[] = { TagEnum.Java };
		List<TagEnum> languagesList = Arrays.asList(languages);
		readSOContentWordAndVectorsForTags(languages);
		Map<String, SoContentWordVector> embMap = wordVectorsMapByTag.get(1);
		loadValidThreadsForLanguages(languagesList);
		System.gc();

		Map<String, double[]> queriesAndSent2Vectors = CrarUtils
				.readQueriesAndVectors(CRAR_HOME + "/data/groundTruthSentenceQueriesAndVectors1" + ".txt");

		Map<String, Set<Integer>> recommendedResults = new LinkedHashMap<>();		
		for (String query : groundTruthThreadsMap.keySet()) {
			List<ThreadNota> notas = new ArrayList();
			double[] outputCnn = queriesAndSent2Vectors.get(CrarUtils.processQuery(query, false));
			Set<Integer> languagesDLL = allValidThreadsMapByTag.keySet();
			for (Integer language : languagesDLL) {
				Set<Integer> threadsIds = allValidThreadsMapByTag.get(language).keySet();
				for (Integer threadId : threadsIds) {
					ThreadContent t = allValidThreadsMapByTag.get(language).get(threadId);
					notas.add(new ThreadNota(threadId,CosineMeasure.getCosineSimilarity(outputCnn,t.getSentenceVectors())));
				}
			}

			Comparator<ThreadNota> compareById = (ThreadNota o1, ThreadNota o2) -> o2.nota
					.compareTo(o1.nota);
			Collections.sort(notas, compareById);
			Set<Integer> bestAnswers = new LinkedHashSet();
			for (int i = 0; i < 10; i++) {
				bestAnswers.add(notas.get(i).id);
			}
			recommendedResults.put(query, bestAnswers);
		}

		String approachName = "RNN by CADU";
		MetricResult metricResult = new MetricResult(approachName, 0, 0, 0, 0, 0, 10, "approach RNN for top 10", 0,
				null, null);
		analyzeResults(recommendedResults, groundTruthThreadsMap, metricResult, approachName);
		crarService.saveMetricResult(metricResult);
	}
	
	public void processAsymmetricSimilarity() throws Exception {
		String languageDataSet = "java" + "-" + "test";
		Map<String, Set<Integer>> groundTruthThreadsMap = new LinkedHashMap<>();
		loadGroundTruthSelectedQueriesForQuestions(languageDataSet, groundTruthThreadsMap);
		
		TagEnum languages[] = { TagEnum.Java };
		List<TagEnum> languagesList = Arrays.asList(languages);
		readSOContentWordAndVectorsForTags(languages);
		Map<String, SoContentWordVector> embMap = wordVectorsMapByTag.get(1);
		loadValidThreadsForLanguages(languagesList);
		System.gc();				
		

		Map<String, double[]> queriesAndSent2Vectors = CrarUtils
				.readQueriesAndVectors(CRAR_HOME + "/data/groundTruthSentenceQueriesAndVectors1" + ".txt");

		Map<String, Set<Integer>> recommendedResults = new LinkedHashMap<>();		
		for (String query : groundTruthThreadsMap.keySet()) {
			List<ThreadNota> notas = new ArrayList();
			Set<Integer> languagesDLL = allValidThreadsMapByTag.keySet();
			for (Integer language : languagesDLL) {
				Set<Integer> threadsIds = allValidThreadsMapByTag.get(language).keySet();
				for (Integer threadId : threadsIds) {
					ThreadContent t = allValidThreadsMapByTag.get(language).get(threadId);
					double threadTitleAsymmetricSim = getAsymmetricSimilarity(CrarUtils.processQuery(query, false), t.getTitle(), 1,
							null);
					notas.add(new ThreadNota(threadId,threadTitleAsymmetricSim));
				}
			}

			Comparator<ThreadNota> compareById = (ThreadNota o1, ThreadNota o2) -> o2.nota
					.compareTo(o1.nota);
			Collections.sort(notas, compareById);
			Set<Integer> bestAnswers = new LinkedHashSet();
			for (int i = 0; i < 10; i++) {
				bestAnswers.add(notas.get(i).id);
			}
			recommendedResults.put(query, bestAnswers);
		}

		String approachName = "RNN by CADU";
		MetricResult metricResult = new MetricResult(approachName, 0, 0, 0, 0, 0, 10, "approach RNN for top 10", 0,
				null, null);
		analyzeResults(recommendedResults, groundTruthThreadsMap, metricResult, approachName);
		crarService.saveMetricResult(metricResult);
	}
	
	class ThreadNota {
		Integer id;
		Double nota;
		double[] embeddings;
		Double titleSentenceVectors;
		Double titleAssimetricSimilarity;
		Double bodyAssimetricSimilarity;		
		Double tfCosineSimScore;		
		Double bm25;
		

		public ThreadNota(Integer id, double[] embeddings) {
			super();
			this.id = id;
			this.embeddings = embeddings;
		}
		
		
		
		public ThreadNota(Integer id, Double titleSentenceVectors, Double titleAssimetricSimilarity,
				Double bodyAssimetricSimilarity, Double tfCosineSimScore, Double bm25) {
			super();
			this.id = id;
			this.titleSentenceVectors = titleSentenceVectors;
			this.titleAssimetricSimilarity = titleAssimetricSimilarity;
			this.bodyAssimetricSimilarity = bodyAssimetricSimilarity;
			this.tfCosineSimScore = tfCosineSimScore;
			this.bm25 = bm25;
		}



		public ThreadNota(Integer id, Double nota) {
			super();
			this.id = id;
			this.nota = nota;
		}
	}
	
	private void processCrarThreads() throws Exception {
		PrintWriter pw = new PrintWriter(new File("/home/rodrigo/box.txt"));
		Set<ThreadContent> threads = getAllThreadsClient(1);
		for (ThreadContent t: threads) {
			int size = t.getTitle().split(" ").length;
			pw.write(size+"\n");
		}
		pw.close();
	}
	
	public void processThreadBaseLines() throws Exception {
		String languageDataSet = "java" + "-" + "test";
		Map<String, Set<Integer>> groundTruthThreadsMap = new LinkedHashMap<>();
		loadGroundTruthSelectedQueriesForQuestions(languageDataSet, groundTruthThreadsMap);
		
		TagEnum languages[] = { TagEnum.Java };
		List<TagEnum> languagesList = Arrays.asList(languages);
		readSOContentWordAndVectorsForTags(languages);
		Map<String, SoContentWordVector> embMap = wordVectorsMapByTag.get(1);
		loadValidThreadsForLanguages(languagesList);
		System.gc();				
		

		Map<String, double[]> queriesAndSent2Vectors = CrarUtils
				.readQueriesAndVectors(CRAR_HOME + "/data/groundTruthSentenceQueriesAndVectors1" + ".txt");

		Map<String, Set<Integer>> recommendedResults1 = new LinkedHashMap<>();		
		Map<String, Set<Integer>> recommendedResults2 = new LinkedHashMap<>();
		Map<String, Set<Integer>> recommendedResults3 = new LinkedHashMap<>();
		Map<String, Set<Integer>> recommendedResults4 = new LinkedHashMap<>();
		Map<String, Set<Integer>> recommendedResults5 = new LinkedHashMap<>();
		for (String query : groundTruthThreadsMap.keySet()) {
			List<ThreadNota> notas = new ArrayList();
			double[] outputCnn = queriesAndSent2Vectors.get(CrarUtils.processQuery(query, false));
			Set<Integer> languagesDLL = allValidThreadsMapByTag.keySet();
			for (Integer language : languagesDLL) {
				Set<Integer> threadsIds = allValidThreadsMapByTag.get(language).keySet();
				for (Integer threadId : threadsIds) {
					ThreadContent t = allValidThreadsMapByTag.get(language).get(threadId);
					
					double titleSentenceVectors = CosineMeasure.getCosineSimilarity(outputCnn,t.getSentenceVectors());
					
					double threadTitleAsymmetricSim = getAsymmetricSimilarity(CrarUtils.processQuery(query, false), t.getTitle(), 1,
							null);
					
					double bodyAssimetricSimilarity = getAsymmetricSimilarity(query, loadThreadContent(t,
							ContentTypeEnum.question_body_answers_body.getId()), 1,
							null);					
					
					double tfCosineSimScore = CosineSimilarity.cosineSimilarity(query,
							loadThreadContent(t, ContentTypeEnum.title_questionBody_body_code.getId()));
					
					double bm25Score = 0d;//t.getBm25Score();										
					
					notas.add(new ThreadNota(threadId,titleSentenceVectors,threadTitleAsymmetricSim,bodyAssimetricSimilarity,tfCosineSimScore,bm25Score));
				}
			}

			Comparator<ThreadNota> compareBy1 = (ThreadNota o1, ThreadNota o2) -> o2.titleSentenceVectors
					.compareTo(o1.titleSentenceVectors);
			Comparator<ThreadNota> compareBy2 = (ThreadNota o1, ThreadNota o2) -> o2.titleAssimetricSimilarity
					.compareTo(o1.titleAssimetricSimilarity);
			Comparator<ThreadNota> compareBy3 = (ThreadNota o1, ThreadNota o2) -> o2.bodyAssimetricSimilarity
					.compareTo(o1.bodyAssimetricSimilarity);
			Comparator<ThreadNota> compareBy4 = (ThreadNota o1, ThreadNota o2) -> o2.tfCosineSimScore
					.compareTo(o1.tfCosineSimScore);
			Comparator<ThreadNota> compareBy5 = (ThreadNota o1, ThreadNota o2) -> o2.bm25
					.compareTo(o1.bm25);
			
			Collections.sort(notas, compareBy1);
			Set<Integer> bestAnswers1 = new LinkedHashSet();
			for (int i = 0; i < 10; i++) {
				bestAnswers1.add(notas.get(i).id);
			}
			recommendedResults1.put(query, bestAnswers1);
			
			Collections.sort(notas, compareBy2);
			Set<Integer> bestAnswers2 = new LinkedHashSet();
			for (int i = 0; i < 10; i++) {
				bestAnswers2.add(notas.get(i).id);
			}
			recommendedResults2.put(query, bestAnswers2);
			
			Collections.sort(notas, compareBy3);
			Set<Integer> bestAnswers3 = new LinkedHashSet();
			for (int i = 0; i < 10; i++) {
				bestAnswers3.add(notas.get(i).id);
			}
			recommendedResults3.put(query, bestAnswers3);
			
			Collections.sort(notas, compareBy4);
			Set<Integer> bestAnswers4 = new LinkedHashSet();
			for (int i = 0; i < 10; i++) {
				bestAnswers4.add(notas.get(i).id);
			}
			recommendedResults4.put(query, bestAnswers4);
			
			Collections.sort(notas, compareBy5);
			Set<Integer> bestAnswers5 = new LinkedHashSet();
			for (int i = 0; i < 10; i++) {
				bestAnswers5.add(notas.get(i).id);
			}
			recommendedResults5.put(query, bestAnswers5);
		}

		String approachName = "RNN by CADU";
		MetricResult metricResult = new MetricResult(approachName, 0, 0, 0, 0, 0, 10, "approach for top 10", 0,
				null, null);
		analyzeResults(recommendedResults1, groundTruthThreadsMap, metricResult, "titleSentenceVectors");
		analyzeResults(recommendedResults2, groundTruthThreadsMap, metricResult, "titleAssimetricSimilarity");
		analyzeResults(recommendedResults3, groundTruthThreadsMap, metricResult, "bodyAssimetricSimilarity");
		analyzeResults(recommendedResults4, groundTruthThreadsMap, metricResult, "tfCosineSimScore");
		analyzeResults(recommendedResults5, groundTruthThreadsMap, metricResult, "bm25");
		crarService.saveMetricResult(metricResult);
	}
*/
	


}
