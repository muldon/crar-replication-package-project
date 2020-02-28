package com.ufu.crar.repository;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ufu.crar.to.Bucket;
import com.ufu.crar.to.Evaluation;
import com.ufu.crar.to.ExternalQuestion;
import com.ufu.crar.to.Pair;
import com.ufu.crar.to.Post;
import com.ufu.crar.to.SoContentWordVector;
import com.ufu.crar.to.Tags.TagEnum;
import com.ufu.crar.to.ThreadContent;


public interface GenericRepository {
	
	
	public List<Post> findAllQuestions();

	public Map<Integer, Set<Integer>> getAllPostLinks();

	//public Map<Post, List<Post>> getQuestionsByFilters(String tagFilter,Integer limit, String maxCreationDate);
	
	public List<Post> getSomePosts();

	
	//public Set<Post> findClosedDuplicatedNonMastersByTagExceptProcessedQuestions(String tagFilter);

	public Set<Post> getPostsByFilters(String tagFilter);

	public Set<Integer> findRelatedQuestionsIds(Set<Integer> allQuestionsIds, Integer linkTypeId);

	public List<Post> findRankedList(Integer id, int userId, int phaseNum);

	List<ExternalQuestion> findNextExternalQuestionInternalSurveyUser(Integer userId, Integer phaseNumber);

	public List<Evaluation> getEvaluationByPhaseAndRelatedPost(Integer externalQuestionId, Integer phaseNumber);

	public List<ExternalQuestion> getExternalQuestionsByPhase(Integer phaseNumber);

	public List<Post> findRankedPosts(Integer id, Integer userId, int phaseNum);

	public List<Post> findRankedEvaluatedPosts(Integer id, Integer userId, int phaseNum);

	public List<Post> getAnswersWithCode(String startDate, Integer tagId);

	public List<Post> getPostsByIds(Collection<Integer> postsListIds);

	public List<Bucket> getBucketsByIds(Set<Integer> postsListIds);

	public Map<Integer, String> getQuestionsIdsTitles();

	public Map<Integer, Integer> getAnswersIdsParentIds();

	List<Bucket> getUpvotedQuestionsWithAnswersForTag(Integer tagId, boolean useLemma);

	public List<Post> getUpvotedPostsWithCode(String startDate, Integer tagId);

	public List<Bucket> getUpvotedAnswersIdsContentsAndParentContents(Integer tagId);

	/*Map<Integer, String> getThreadsIdsTitlesForUpvotedAnswersWithCode();*/

	public Set<Bucket> findClosedDuplicatedNonMasters();

	public Set<Bucket> getAllUpvotedScoredBuckets(int postType);

	public List<Post> getAnswersWithCodeForQuestion(Integer id);
	//public List<Bucket> getBucketsLemmaByParentsIds(Set<Integer> questionsIds);

	public void saveTopic(Integer postId, String topic);

	public Set<Bucket> getAllBucketsAndTopics();

	public Set<Bucket> getAllQuestionsLemma();

	public void saveHotTopic(Integer id, String hotTopic);

	public void saveTopScoredAnswers(Integer id, String topScoredAnswers);

	public Set<Bucket> getPossibleDupesInBodies();

	public void updateBodies(Bucket bucket);

	public Bucket getBucketById(int id, boolean useLemma);

	public Set<Pair> getDuplicatedPairs();

	public void findAllWordVectorsByTagId(Integer tagId, Map<String, SoContentWordVector> wordVectorsMap);

	public List<Bucket> getUpvotedAnswersIdsContentsAndParentContentsDycrokage(Integer tagId);

	public List<Bucket> getUpvotedAnswersForQuestion(Integer id, boolean useLemma);

	public Set<Integer> findParentIdsByPostIds(Set<Integer> answersIds);

	public Set<Bucket> getUpvotedAnswersWithCodeForThreadsIds(Set<Integer> thredsIds, boolean useLemma);

	public Set<Pair> getDuplicatedPairsClosedNonMasters(TagEnum tag);

	public List<ThreadContent> getThreadContentsByIds(Set<Integer> questionsIds);
	
	
	
	
    
}
