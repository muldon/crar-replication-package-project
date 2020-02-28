package com.ufu.crar.repository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.ufu.crar.to.Bucket;
import com.ufu.crar.to.Evaluation;
import com.ufu.crar.to.ExternalQuestion;
import com.ufu.crar.to.Pair;
import com.ufu.crar.to.Post;
import com.ufu.crar.to.PostLink;
import com.ufu.crar.to.SoContentWordVector;
import com.ufu.crar.to.Tags.TagEnum;
import com.ufu.crar.to.ThreadContent;
import com.ufu.crar.util.CrarUtils;

@Repository
public class GenericRepositoryImpl implements GenericRepository {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@PersistenceContext
	private EntityManager em;

	
	
	

	
	@Override
	public List<Post> findAllQuestions() {
		Query q = em.createNativeQuery("select * from postsmin p where p.posttypeid =1 order by id desc", Post.class);
		return (List<Post>) q.getResultList();
	}

		

	

	private List<Post> getAnwersForParentsIds(String questionsIds) {
		Query q = em.createNativeQuery("select * from postsmin p where p.parentid in "+questionsIds+ " and p.body like '%<pre><code>%'", Post.class);
		return (List<Post>) q.getResultList();
	}



	public List<Post> getAnswersWithCodeForQuestion(Integer id) {
		//Query q = em.createNativeQuery("select * from posts p where p.parentid = "+id+ " and p.body like '%<pre><code>%'", Post.class);
		
		Query q = em.createNativeQuery("select * from postsmin p where p.parentid = "+id+ " and p.body like '%<pre><code>%'", Post.class);
		
		//Query q = em.createNativeQuery("select * from posts p where p.parentid = "+id, Post.class);
		
		return (List<Post>) q.getResultList();
	}



	

	



	@Override
	public List<Post> getSomePosts() {
		Query q = em.createNativeQuery("select * from postsmin p where p.tagssyn like '%java%' and p.tagssyn not like '%javascript%' limit 1000", Post.class);
		return (List<Post>) q.getResultList();
	}



	
	


	@Override
	public Set<Post> getPostsByFilters(String tagFilter) {
		
		logger.info("getPostsByFilters: "+tagFilter);
		String sql = " select * from postsmin  p " 
				+ " WHERE   1=1 ";
		
		sql += CrarUtils.getQueryComplementByTag(tagFilter);
				
		sql += " order by p.id ";
		
		
		Query q = em.createNativeQuery(sql, Post.class);
		
		List<Post> postsTmp = q.getResultList();
		Set<Post> postsSet = new LinkedHashSet<>();
				
		logger.info("Post in getPostsByFilters step 1: "+postsTmp.size());
		for(Post post:postsTmp){
			if(StringUtils.isBlank(post.getTitle()) || StringUtils.isBlank(post.getBody())) {
				continue;
			}	
			postsSet.add(post);
		}
		logger.info("Post in getPostsByFilters step 2: "+postsSet.size());
		postsTmp = null;
		return postsSet;
	}



	@Override
	public Set<Integer> findRelatedQuestionsIds(Set<Integer> allQuestionsIds,Integer linkTypeId) {
		Set<Integer> relatedQuestionsIds = new LinkedHashSet<>();
		
		String inCommand = "(";
		for(Integer questionId:allQuestionsIds) {
			inCommand+= questionId+ ",";
		}
		inCommand+= "#";
		inCommand = inCommand.replaceAll(",#", ")");
		
		//logger.info("In command: "+inCommand);
		
		String query = "select * from postlinksmin where linktypeid = "+linkTypeId+" and postid in " + inCommand+  
				" union " + 
				" select * from postlinksmin where linktypeid = "+linkTypeId+" and relatedpostid in " + inCommand; 
		
		logger.info(query);
		
		Query q = em.createNativeQuery(query, PostLink.class);
		Set<PostLink> postsLinks = new LinkedHashSet(q.getResultList());
		for(PostLink postLink:postsLinks) {
			relatedQuestionsIds.add(postLink.getPostId());
			relatedQuestionsIds.add(postLink.getRelatedPostId());
		}
		
		return relatedQuestionsIds;
	}





	@Override
	public List<Post> findRankedList(Integer externalQuestionId, int userId, int phaseNum) {
			
		String sql = "select p.*  " + 
				"  from postsmin p, relatedpost rp, rank r  " + 
				"  where p.id = rp.postid  " + 
				"  and rp.id = r.relatedpostid	 " + 
				"  and rp.externalquestionid =  " + externalQuestionId+
				"  and r.phase =  " + phaseNum+
				"  and r.id not in (" + 
				"  select r2.id" + 
				"	  from relatedpost rp2, rank r2, evaluation e2" + 
				"      where rp2.id = r2.relatedpostid	 " + 
				"      and r2.id = e2.rankid" + 
				"	  and rp2.externalquestionid = " + externalQuestionId+
				"     and r2.phase =  " + phaseNum+
				"	  and e2.surveyuserid = " +userId+ 
				"  )" + 
				"  order by r.rankorder";
		
		Query q = em.createNativeQuery(sql, Post.class);
		return (List<Post>) q.getResultList();
		
		
	}



	@Override
	public List<Post> findRankedPosts(Integer externalQuestionId, Integer userId, int phaseNum) {
		
		String sql = "select p.*  " + 
				"  from postsmin p, relatedpost rp, rank r  " + 
				"  where p.id = rp.postid  " + 
				"  and rp.id = r.relatedpostid	 " + 
				"  and rp.externalquestionid =  " + externalQuestionId+
				"  and r.phase =  " + phaseNum+
				"  order by r.rankorder";
		
		Query q = em.createNativeQuery(sql, Post.class);
		return (List<Post>) q.getResultList();
	}
	

	@Override
	public List<Post> findRankedEvaluatedPosts(Integer externalQuestionId, Integer userId, int phaseNum) {
		String sql = "  select p.*" + 
				"	  from postsmin p,relatedpost rp2, rank r2, evaluation e2" + 
				"     where rp2.id = r2.relatedpostid	 " + 
				"	  and p.id = rp2.postid	" +	
				"     and r2.id = e2.rankid" + 
				"	  and rp2.externalquestionid = " + externalQuestionId+
				"     and r2.phase =  " + phaseNum+
				"	  and e2.surveyuserid = " +userId;
				
		
		Query q = em.createNativeQuery(sql, Post.class);
		return (List<Post>) q.getResultList();
	}
	



	@Override
	public List<ExternalQuestion> findNextExternalQuestionInternalSurveyUser(Integer userId, Integer phaseNumber) {
		String sql = "select * " + 
				" from externalquestion eq" 
				+ " where eq.id not in "  
				+ " (select rp.externalquestionid"  
				+ "  from evaluation e, rank r, relatedpost rp"  
				+ "  where e.rankid = r.id "
				+ " and r.relatedpostid = rp.id "
				+ " and e.surveyuserid = "+userId  
				+ " ) order by externalid  "
				+ " limit 2";  
				
		/*if(bringTwoQuestions) {
			sql+= " limit 2";
		}*/
				
	
		Query q = em.createNativeQuery(sql, ExternalQuestion.class);
		return (List<ExternalQuestion>) q.getResultList();
		
	}





	@Override
	public List<Evaluation> getEvaluationByPhaseAndRelatedPost(Integer externalQuestionId, Integer phaseNumber) {
		String sql = " select rp.postid, e.surveyuserid, e.likertscale" + 
				" from relatedpost rp, rank r, evaluation e" + 
				" where rp.id=r.relatedpostid" + 
				" and e.rankid = r.id" + 
				" and rp.externalquestionid = " + externalQuestionId+
				" and r.phase=" + phaseNumber+ 
				" order by rp.postid, e.surveyuserid";  
			
		Query q = em.createNativeQuery(sql);
		
		List<Object[]> rows = q.getResultList();
		List<Evaluation> result = new ArrayList<>(rows.size());
		for (Object[] row : rows) {
			Evaluation evaluation = new Evaluation();
			evaluation.setPostId((Integer) row[0]);
			evaluation.setSurveyUserId((Integer) row[1]);
			evaluation.setLikertScale((Integer) row[2]);
			result.add(evaluation);			
		}
		
		return result;
	}





	@Override
	public List<ExternalQuestion> getExternalQuestionsByPhase(Integer phaseNumber) {
		String sql = " select distinct(e.*)" + 
				" from externalquestion e, relatedpost rp, rank r" + 
				" where e.id = rp.externalquestionid " + 
				" and r.relatedpostid = rp.id" + 
				" and r.phase = " +phaseNumber+ 
				" order by e.id ";  
			
		Query q = em.createNativeQuery(sql, ExternalQuestion.class);
		return (List<ExternalQuestion>) q.getResultList();
	}





	@Override
	public List<Post> getAnswersWithCode(String startDate, Integer tagId) {
		
		String sql = " select * "
				+ " from postsmin po"  
				+ " where po.tagid="+tagId
				+ " and po.processedcode!=''"
				+ " and po.posttypeid = 2 ";
		
		if(!StringUtils.isBlank(startDate)) {
			sql += " and po.creationdate > '" + startDate + "'";
		}
			
		Query q = em.createNativeQuery(sql, Post.class);
		List<Post> posts = (List<Post>) q.getResultList();
		logger.info("getAnswersWithCode: "+posts.size()+ " posts retrieved");
		return posts;
	}





	@Override
	public List<Post> getPostsByIds(Collection<Integer> soAnswerIds) {
		String idsIn = " ";
		for(Integer soId: soAnswerIds) {
			idsIn+= soId+ ",";
		}
		idsIn+= "#end";
		idsIn = idsIn.replace(",#end", "");
		
		String sql = " select * "
				+ " from postsmin po"  
				+ " where po.id in ("+idsIn+")";
		
			
		Query q = em.createNativeQuery(sql, Post.class);
		List<Post> posts = (List<Post>) q.getResultList();
		//logger.info("getPostsByIds: "+posts.size());
		return posts;
	}


	
	

	@Override
	public Map<Integer, String> getQuestionsIdsTitles() {
		String sql = " select po.id,po.processedTitle "
				+ " from postsmin po"
				+ " where po.posttypeid=1"
				+ " and po.processedTitle != ''"
				+ " and po.answercount>0 "
				+ " and po.score>0";
			
		Query q = em.createNativeQuery(sql);
		List<Object[]> rows = q.getResultList();
		Map<Integer, String> questionsIdsTitles = new LinkedHashMap<>();
		for (Object[] row : rows) {
			questionsIdsTitles.put((Integer)row[0], (String)row[1]);
		}
		
		return questionsIdsTitles;
	}


	@Override
	public List<Bucket> getUpvotedQuestionsWithAnswersForTag(Integer tagId, boolean useLemma) {
		String sql = "";
				if(useLemma) {
					sql+= " select po.id,po.processedtitlelemma,po.processedbodylemma,po.processedcode, po.commentcount, po.score, po.answercount"; 
				}else {
					sql+= " select po.id,po.processedtitle,po.processedbody,po.processedcode, po.commentcount, po.score, po.answercount";
				}
				sql+= " from postsmin po "
				+ " where po.posttypeid=1"
				+ " and po.tagid ="+tagId
				+ " and po.score>0 "
				+ " and po.answercount>0 "		
				+ " and po.processedtitlelemma != ''";
				
		
			
		Query q = em.createNativeQuery(sql);
		List<Object[]> rows = q.getResultList();
		List<Bucket> result = new ArrayList<>(rows.size());
		for (Object[] row : rows) {
			Bucket bucket = new Bucket();
			bucket.setId((Integer) row[0]);
			if(useLemma) {
				bucket.setProcessedTitleLemma((String) row[1]);
				bucket.setProcessedBodyLemma((String) row[2]);
			}else {
				bucket.setProcessedTitle((String) row[1]);
				bucket.setProcessedBody((String) row[2]);
			}
			
			bucket.setProcessedCode((String) row[3]);
			bucket.setCommentCount((Integer) row[4]);
			bucket.setScore((Integer) row[5]);
			bucket.setAnswerCount((Integer) row[6]);
			result.add(bucket);			
		}
		
		return result;
	}
	
	
	@Override
	public List<Bucket> getUpvotedAnswersForQuestion(Integer questionId,boolean useLemma) {
		String sql = "";
				if(useLemma) {
					sql+= " select po.id,po.processedbodylemma,po.processedcode,po.commentcount, po.score "; 
				}else {
					sql+= " select po.id,po.processedbody,po.processedcode,po.commentcount, po.score";
				}
				sql+= " from postsmin po "
				+ " where po.parentid ="+questionId
				+ " and po.score>0 "
				+ " and po.processedcode!=''";
				
		Query q = em.createNativeQuery(sql);
		List<Object[]> rows = q.getResultList();
		List<Bucket> result = new ArrayList<>(rows.size());
		for (Object[] row : rows) {
			Bucket bucket = new Bucket();
			bucket.setId((Integer) row[0]);
			if(useLemma) {
				bucket.setProcessedBodyLemma((String) row[1]);
			}else {
				bucket.setProcessedBody((String) row[1]);
			}			
			bucket.setProcessedCode((String) row[2]);
			//bucket.setContainCode(StringUtils.isBlank(bucket.getProcessedCode())?false:true);
			bucket.setCommentCount((Integer) row[3]);
			bucket.setScore((Integer) row[4]);
			result.add(bucket);			
		}
		
		return result;
	}






	

	@Override
	public Map<Integer, Integer> getAnswersIdsParentIds() {
		String sql = " select po.id,po.parentid "
				+ " from postsmin po"
				+ " where po.posttypeid=2"
				+ " and po.score>0";  
			
			
		Query q = em.createNativeQuery(sql);
		List<Object[]> rows = q.getResultList();
		Map<Integer, Integer> answersIdsParentIds = new LinkedHashMap<>();
		for (Object[] row : rows) {
			answersIdsParentIds.put((Integer)row[0], (Integer)row[1]);
		}
		
		return answersIdsParentIds;
	}



	@Override
	public List<Post> getUpvotedPostsWithCode(String startDate,Integer tagId) {
		String sql = " select * "
				+ " from postsmin po"  
				+ " where po.tagid="+tagId
				+ " and po.score>0 "
				+ " and po.processedcode!=''";
				
		
		if(!StringUtils.isBlank(startDate)) {
			sql += " and po.creationdate > '" + startDate + "'";
		}
			
		Query q = em.createNativeQuery(sql, Post.class);
		List<Post> posts = (List<Post>) q.getResultList();
		logger.info("getUpvotedPostsWithCode: "+posts.size()+ " posts retrieved");
		return posts;
	}


    
	@Override
	public List<Bucket> getBucketsByIds(Set<Integer> soAnswerIds) {
		String idsIn = " ";
		for(Integer soId: soAnswerIds) {
			idsIn+= soId+ ",";
		}
		idsIn+= "#end";
		idsIn = idsIn.replace(",#end", "");
		
		String sql = " select po.id,po.body,po.code,u.reputation,po.commentcount,po.viewcount,po.score,parent.acceptedanswerid, parent.score as parentscore,po.processedbody,po.processedcode,parent.id as parentid,parent.processedtitle,parent.processedbody as parentBody,parent.processedcode as parentCode "  
				+ " from postsmin po, users u, postsmin parent  "  
				+ " where po.owneruserid=u.id" 
				+ " and po.parentid = parent.id"  
				+ " and po.id in ("+idsIn+")";
		
			
		Query q = em.createNativeQuery(sql);
		List<Object[]> rows = q.getResultList();
		List<Bucket> result = new ArrayList<>(rows.size());
		for (Object[] row : rows) {
			Bucket bucket = new Bucket();
			bucket.setId((Integer) row[0]);
			//bucket.setProcessedTitle((String) row[1]);
			bucket.setBody((String) row[1]);
			bucket.setCode((String) row[2]);
			bucket.setUserReputation((Integer) row[3]);
			bucket.setCommentCount((Integer) row[4]);
			bucket.setViewCount((Integer) row[5]);
			bucket.setUpVotesScore((Integer) row[6]);
			bucket.setAcceptedAnswer( ((Integer) row[7]) != null ? true: false);
			bucket.setParentUpVotesScore((Integer) row[8]);
			bucket.setProcessedBody((String) row[9]);
			bucket.setProcessedCode((String) row[10]);
			bucket.setParentId((Integer) row[11]);
			bucket.setParentProcessedTitle((String) row[12]);
			bucket.setParentProcessedBody((String) row[13]);
			bucket.setParentProcessedCode((String) row[14]);
			result.add(bucket);			
		}
		
		return result;
	}

//----------------------------------------------------New dupe-------------------------------------------------------
	

	@Override
	/**
	 * Get non masters whose masters have answers and are valid (not deleted)
	 */
	public Set<Bucket> findClosedDuplicatedNonMasters() {
		//String sql = " select p.id,p.processedtitle,p.processedbody,p.processedcode,p.hottopics "+
		String sql = " select p.id,p.processedtitlelemma,p.processedbodylemma,p.processedcode,p.hottopics "+
				" from postsmin p " +
				" WHERE  p.posttypeId = 1 " + 
				" and p.closeddate is not null" + 
				//" and p.answercount>0 "+
				" and p.score>0"+
				" and p.id in " + 
				"   ( select pl.postid" + 
				"     from postlinksmin pl, postsmin  p2  " + 
				"     where p2.id = pl.relatedpostid" + 
				"     and pl.linktypeid = 3 " + 
				"	  and p2.answercount > 0 "+
				"     and p2.score>0 "+
				"     and p2.processedtitlelemma != ''"+
				"     and p2.processedbodylemma != ''"+
				"    ) "+
				" and p.processedtitlelemma != ''"+
				" and p.processedbodylemma != ''";
		
		
		
		Query q = em.createNativeQuery(sql);
		List<Object[]> rows = q.getResultList();
		Set<Bucket> result = new LinkedHashSet<>(rows.size());
		for (Object[] row : rows) {
			Bucket bucket = new Bucket();
			bucket.setId((Integer) row[0]);
			/*bucket.setProcessedTitle((String) row[1]);
			bucket.setProcessedBody((String) row[2]);*/
			bucket.setProcessedTitleLemma((String) row[1]);
			bucket.setProcessedBodyLemma((String) row[2]);
			bucket.setProcessedCode((String) row[3]);
			bucket.setHotTopics((String) row[4]);
			result.add(bucket);			
		}
		
		logger.info("Posts in findClosedDuplicatedNonMasters: "+result.size());
				
		return result;
	}

	
	@Override
	public Set<Pair> getDuplicatedPairsClosedNonMasters(TagEnum tag) {
		//String sql = " select nonmaster.id as nonmasterid, master.id asmasterid, nonmaster.processedtitle as title1, master.processedtitle as title2, nonmaster.processedbody as body1, master.processedbody as body2"  
		String sql = " select nonmaster.id as nonmasterid, master.id asmasterid, nonmaster.processedtitlelemma as title1, master.processedtitlelemma as title2"
				+ " from postlinks pl, postsmin nonmaster, postsmin master"  
				+ " where nonmaster.id = pl.postid"  
				+ " and master.id = pl.relatedpostid"
				+ " and pl.linktypeid=3"            //3==dupe
				+ " and nonmaster.closeddate is not null"
				+ " and nonmaster.posttypeid=1 " 
				+ " and master.tags like '%"+tag.getDescricao()+"%'"
				+ " and master.posttypeid=1"
				+ " and master.score>0"
				+ " and master.answercount>0 "
				+ " and nonmaster.processedbody!=''"
				+ " and nonmaster.processedtitle!=''"
				+ " and master.processedbody!=''"
				+ " and master.processedtitle!=''";
					
			
		Query q = em.createNativeQuery(sql);
		List<Object[]> rows = q.getResultList();
		Set<Pair> result = new LinkedHashSet<>(rows.size());
		for (Object[] row : rows) {
			Pair pair = new Pair();
			pair.setPostId1((Integer) row[0]);
			pair.setPostId2((Integer) row[1]);
			pair.setNonMasterProcessedTitle((String) row[2]);
			pair.setMasterProcessedTitle((String) row[3]);
			//pair.setNonMasterProcessedBody((String) row[4]);
			//pair.setMasterProcessedBody((String) row[5]);
			result.add(pair);			
		}
		
		logger.info("Posts in getPossibleDupesInBodies: "+result.size());
		
		return result;
	}
	
	@Override
	public Set<Pair> getDuplicatedPairs() {
		String sql = " select p.postid,p.relatedPostid,pm.topics as topics1, pm2.topics as topics2, pm.hottopics as hottopics1, pm2.hottopics as hottopics2"  
				+ " from postlinksmin p, postsmin pm, postsmin pm2"  
				+ " where pm.id = p.postid"  
				+ " and pm2.id = p.relatedpostid"
				+ " and p.linktypeid=3"            //3==dupe
				+ " and pm.score>0"
				+ " and pm2.score>0"
				+ " and pm2.answercount>0 "
				+ " and pm.posttypeid=1 "  
				+ " and pm2.posttypeid=1"
				+ " and pm.processedbodylemma!=''"
				+ " and pm.processedtitlelemma!=''"
				+ " and pm2.processedbodylemma!=''"
				+ " and pm2.processedtitlelemma!=''";
					
			
		Query q = em.createNativeQuery(sql);
		List<Object[]> rows = q.getResultList();
		Set<Pair> result = new LinkedHashSet<>(rows.size());
		for (Object[] row : rows) {
			Pair bucket = new Pair((Integer) row[0],(Integer) row[1],(String) row[2],(String) row[3], (String) row[4],(String) row[5]);
			result.add(bucket);			
		}
		
		logger.info("Posts in getPossibleDupesInBodies: "+result.size());
		
		return result;
	}
	
	
	@Override
	public Map<Integer, Set<Integer>> getAllPostLinks() {
		
		Query q = em.createNativeQuery("select p.* from postlinksmin p, postsmin pm, postsmin pm2 "
				+ " where p.postid = pm.id "
				+ " and p.relatedpostid = pm2.id "
				+ " and p.linktypeid=3"            //3==dupe
				+ " and pm.score>0"
				+ " and pm2.score>0"
				+ " and pm2.answercount>0 "
				+ " and pm.posttypeid=1 "  
				+ " and pm2.posttypeid=1"
				+ " and pm.processedbodylemma!=''"
				+ " and pm.processedtitlelemma!=''"
				+ " and pm2.processedbodylemma!=''"
				+ " and pm2.processedtitlelemma!=''"
				, PostLink.class);
		
		Set<PostLink> postsLinks = new LinkedHashSet(q.getResultList());
		
		Map<Integer, Set<Integer>> postsLinksMap = new LinkedHashMap<>();
		
		for(PostLink postLink: postsLinks) {
			//postsLinksMap.put(postLink.getPostId(), postLink.getRelatedPostId());
			
			Integer postId = postLink.getPostId();
			Integer relatedPostId = postLink.getRelatedPostId(); 
			
			if(postsLinksMap.containsKey(postId)) {
				Set<Integer> relatedPostIds = postsLinksMap.get(postId);
				relatedPostIds.add(relatedPostId);
			}else {
				Set<Integer>  relatedPostIds= new LinkedHashSet<Integer>();
				relatedPostIds.add(relatedPostId);
				postsLinksMap.put(postId,relatedPostIds);
			}
			
		}
		
		return postsLinksMap;
	}


	@Override
	public List<Bucket> getUpvotedAnswersIdsContentsAndParentContents(Integer tagId) {
		System.out.println("getUpvotedAnswersIdsContentsAndParentContents for tag: "+tagId+ "...");
		//String sql = " select po.id,parent.processedtitlelemma,parent.processedbodylemma as parentBody,parent.processedcode as parentCode,po.processedbodylemma,po.processedcode,po.code,parent.id as parentId, parent.acceptedanswerid as acceptedanswerid "
		//String sql = "   select po.id,parent.processedtitle,parent.processedbody as parentBody,parent.processedcode as parentCode,po.processedbody,po.processedcode,po.code,parent.id as parentId, parent.acceptedanswerid as acceptedanswerid, parent.hottopics "
		String sql = "   select po.id,parent.processedtitle,parent.processedbody as parentBody,parent.processedcode as parentCode,po.processedbody,po.processedcode,po.code,parent.id as parentId, parent.acceptedanswerid as acceptedanswerid, po.sentencevectors"
				+ " from postsmin po, postsmin parent"
				+ " where po.parentid = parent.id"
				+ " and po.tagid="+tagId
				+ " and po.posttypeid=2"  
				+ " and po.score>0"
				+ " and parent.score>0"
				//+ " and po.processedcode!=''"
				+ " and po.processedcode is not null"
				//+ " and parent.processedbody!=''"
				+ " and parent.processedbody is not null"
				+ " order by po.id";
				
		Query q = em.createNativeQuery(sql);
		List<Object[]> rows = q.getResultList();
		List<Bucket> result = new ArrayList<>(rows.size());
		for (Object[] row : rows) {
			Bucket bucket = new Bucket();
			bucket.setId((Integer) row[0]);
			//bucket.setParentProcessedTitleLemma((String) row[1]);
			//bucket.setParentProcessedBodyLemma((String) row[2]);
			bucket.setParentProcessedTitle((String) row[1]);
			bucket.setParentProcessedBody((String) row[2]);
			bucket.setParentProcessedCode((String) row[3]);
			//bucket.setProcessedBodyLemma((String) row[4]);
			bucket.setProcessedBody((String) row[4]);
			bucket.setProcessedCode((String) row[5]);
			bucket.setCode((String) row[6]);
			bucket.setParentId((Integer) row[7]);
			bucket.setAcceptedAnswerId((Integer) row[8]);
			bucket.setSentenceVectors(CrarUtils.getVectorsFromString((String) row[9]));
			//bucket.setHotTopics((String) row[9]);
			result.add(bucket);			
		}
		
		return result;
	}
	
	@Override
	public List<Bucket> getUpvotedAnswersIdsContentsAndParentContentsDycrokage(Integer tagId) {
		//System.out.println("getUpvotedAnswersIdsContentsAndParentContentsDycrokage for tag: "+tagId);
		String sql = " select po.id,parent.processedtitlelemma,parent.processedbodylemma as parentBody,parent.processedcode as parentCode,po.processedbodylemma,po.processedcode,po.code,parent.id as parentId, parent.acceptedanswerid as acceptedanswerid "
		//String sql = "   select po.id,parent.processedtitle,parent.processedbody as parentBody,parent.processedcode as parentCode,po.processedbody,po.processedcode,po.code,parent.id as parentId, parent.acceptedanswerid as acceptedanswerid"
				+ " from postsmin po, postsmin parent"
				+ " where po.parentid = parent.id"
				+ " and po.posttypeid=2"  
				+ " and po.tagid="+tagId
				+ " and po.score>0"
				+ " and parent.score>0";
				//+ " and po.processedcode is not null" //all not null
				//+ " and parent.processedbody is not null"; //all not null
				
		Query q = em.createNativeQuery(sql);
		List<Object[]> rows = q.getResultList();
		List<Bucket> result = new ArrayList<>(rows.size());
		for (Object[] row : rows) {
			Bucket bucket = new Bucket();
			bucket.setId((Integer) row[0]);
			bucket.setParentProcessedTitleLemma((String) row[1]);
			bucket.setParentProcessedBodyLemma((String) row[2]);
			//bucket.setParentProcessedTitle((String) row[1]);
			//bucket.setParentProcessedBody((String) row[2]);
			bucket.setParentProcessedCode((String) row[3]);
			bucket.setProcessedBodyLemma((String) row[4]);
			//bucket.setProcessedBody((String) row[4]);
			bucket.setProcessedCode((String) row[5]);
			bucket.setCode((String) row[6]);
			bucket.setParentId((Integer) row[7]);
			bucket.setAcceptedAnswerId((Integer) row[8]);
			//bucket.setHotTopics((String) row[9]);
			result.add(bucket);			
		}
		
		return result;
	}
	

	@Override
	public Set<Bucket> getUpvotedAnswersWithCodeForThreadsIds(Set<Integer> thredsIds,boolean useLemma) {
		
		String inCommand = "(";
		for(Integer questionId:thredsIds) {
			inCommand+= questionId+ ",";
		}
		inCommand+= "#";
		inCommand = inCommand.replaceAll(",#", ")");
		
		//logger.info("In command: "+inCommand);
		
		String query = "";
				if(useLemma) {
					query+= "select po.id,parent.processedtitlelemma,parent.processedbodylemma as parentBody,parent.processedcode as parentCode,po.processedbodylemma,po.processedcode,parent.id as parentId, po.score, po.code";
				}else {
					query+= "select po.id,parent.processedtitle,parent.processedbody as parentBody,parent.processedcode as parentCode,po.processedbody,po.processedcode,parent.id as parentId, po.score, po.code";
				}
				 query+= " from postsmin po, postsmin parent"  
				+ " where po.parentid = parent.id"
				+ " and parent.id in "+inCommand
				+ " and po.processedcode!=''"
/*				+ " and parent.score>0"
				+ " and po.score>0";*/
				+ " and po.score>0"
				+ " and parent.score>0"
				+ " and po.processedcode is not null"
				+ " and parent.processedbody is not null";
				
		Query q = em.createNativeQuery(query);
		List<Object[]> rows = q.getResultList();
		Set<Bucket> result = new LinkedHashSet<>(rows.size());
		for (Object[] row : rows) {
			Bucket bucket = new Bucket();
			bucket.setId((Integer) row[0]);
			if(useLemma) {
				bucket.setParentProcessedTitleLemma((String) row[1]);
				bucket.setParentProcessedBodyLemma((String) row[2]);
				bucket.setProcessedBodyLemma((String) row[4]);
			}else { 
				bucket.setParentProcessedTitle((String) row[1]);
				bucket.setParentProcessedBody((String) row[2]);
				bucket.setProcessedBody((String) row[4]);
			}
			
			bucket.setParentProcessedCode((String) row[3]);
			bucket.setProcessedCode((String) row[5]);
			bucket.setParentId((Integer) row[6]);
			bucket.setScore((Integer) row[7]);
			bucket.setCode((String) row[8]);
			result.add(bucket);			
		}
		
		
		return result;
	}

/*
	@Override
	public Map<Integer,String> getThreadsIdsTitlesForUpvotedAnswersWithCode() {
		System.out.println("getThreadsIdsForUpvotedAnswersWithCode ...");
		String sql = " select parent.id as parentId, parent.processedtitle "
				+ " from postsmin po, postsmin parent"
				+ " where po.posttypeid=2"
				+ " and po.parentid = parent.id"  
				+ " and po.score>0"
				+ " and po.processedcode!=''"
				+ " ";
		
			
		Query q = em.createNativeQuery(sql);
		List<Object[]> rows = q.getResultList();
		Map<Integer,String> result = new LinkedHashMap<>(rows.size());
		for (Object[] row : rows) {
			result.put(((Integer) row[0]),(String) row[1]);			
		}
		
		return result;
	}*/



	


	

	@Override
	public Bucket getBucketById(int id, boolean useLemma) {
				
		String sql = "";
		if(useLemma) {
			sql+= " select po.id,parent.processedtitlelemma as parenttitlelemma,parent.processedbodylemma as parentbodylemma, po.processedtitlelemma, po.processedbodylemma"; 
		}else {
			sql+= " select po.id,parent.processedtitle as parenttitle,parent.processedbody as parentbody, po.processedtitle, po.processedbody ";
		}
		sql+= " ,parent.processedcode as parentprocessedcode, po.processedcode, po.parentid, po.score "
		+ " from postsmin po, postsmin parent "
		+ " where po.parentid=parent.id"
		+ " and po.id ="+id
		+ " and po.processedcode!='' "
		+ " and po.code != ''";
		
		Query q = em.createNativeQuery(sql);
		List<Object[]> rows = q.getResultList();
		if(rows.isEmpty()) {
			return null;
		}
		List<Bucket> result = new ArrayList<>(rows.size());
		for (Object[] row : rows) {
			Bucket bucket = new Bucket();
			bucket.setId((Integer) row[0]);
			/*bucket.setProcessedTitle((String) row[1]);
			bucket.setProcessedBody((String) row[2]);*/
			if(useLemma) {
				bucket.setParentProcessedTitleLemma((String) row[1]);
				bucket.setParentProcessedBodyLemma((String) row[2]);
				bucket.setProcessedTitleLemma((String) row[3]);
				bucket.setProcessedBodyLemma((String) row[4]);
			}else {
				bucket.setParentProcessedTitle((String) row[1]);
				bucket.setParentProcessedBody((String) row[2]);
				bucket.setProcessedTitle((String) row[3]);
				bucket.setProcessedBody((String) row[4]);
			}
			bucket.setParentProcessedCode((String) row[5]);
			bucket.setProcessedCode((String) row[6]);
			bucket.setParentId((Integer) row[7]);
			bucket.setScore((Integer) row[8]);
			result.add(bucket);				
		}
		return result.get(0);
	}
	

	@Override
	public Set<Bucket> getAllUpvotedScoredBuckets(int postType) {
		//String sql = " select po.id,po.processedtitle,po.processedbody,po.processedcode,po.hottopics "
		String sql = " select po.id,po.processedtitlelemma,po.processedbodylemma,po.processedcode,po.hottopics,po.score,po.parentid,po.topscoredanswers "
				+ " from postsmin po "
				+ " where po.posttypeid="+postType;
				if(postType==1) { //question
					sql+= " and po.processedtitlelemma != ''"
					    + " and po.answercount>0 ";
				}
				sql+= " and po.score>0 "
			      	+ " and po.processedbodylemma != ''";
		
			
		Query q = em.createNativeQuery(sql);
		List<Object[]> rows = q.getResultList();
		Set<Bucket> result = new LinkedHashSet<>(rows.size());
		for (Object[] row : rows) {
			Bucket bucket = new Bucket();
			bucket.setId((Integer) row[0]);
			/*bucket.setProcessedTitle((String) row[1]);
			bucket.setProcessedBody((String) row[2]);*/
			bucket.setProcessedTitleLemma((String) row[1]);
			bucket.setProcessedBodyLemma((String) row[2]);
			bucket.setProcessedCode((String) row[3]);
			bucket.setHotTopics((String) row[4]);
			bucket.setUpVotesScore((Integer) row[5]);
			bucket.setParentId((Integer) row[6]);
			bucket.setTopScoredAnswers((String) row[7]);
			//bucket.setTopics((String) row[8]);
			result.add(bucket);			
		}
		
		logger.info("Posts in getAllUpvotedScoredBuckets: "+result.size());
		
		return result;
	}
	
	
	
	
/*
	@Override
	public List<Bucket> getBucketsLemmaByParentsIds(Set<Integer> soQuestionsIds) {
		String idsIn = " ";
		for(Integer soId: soQuestionsIds) {
			idsIn+= soId+ ",";
		}
		idsIn+= "#end";
		idsIn = idsIn.replace(",#end", "");
		
		String sql = " select po.id,po.processedbodylemma,po.processedcode,po.parentid "  
				+ " from postsmin po "  
				+ " where po.posttypeid=2"
				+ " and po.processedbodylemma != ''";
				if(MIN_ANSWER_UPVOTES_FOR_THREADS!=null){
					sql+= " and po.score>"+MIN_ANSWER_UPVOTES_FOR_THREADS;
				}
				sql+= " and po.parentid in ("+idsIn+")";
				if(TOP_SCORED_ANSWERS_FOR_THREADS!=null) {
					sql+= " order by po.score desc "
					    + " limit "+TOP_SCORED_ANSWERS_FOR_THREADS;
				}
		System.out.println(sql);
			
		Query q = em.createNativeQuery(sql);
		List<Object[]> rows = q.getResultList();
		List<Bucket> result = new ArrayList<>(rows.size());
		for (Object[] row : rows) {
			Bucket bucket = new Bucket();
			bucket.setId((Integer) row[0]);
			bucket.setProcessedBodyLemma((String) row[1]);
			bucket.setProcessedCode((String) row[2]);
			bucket.setParentId((Integer) row[3]);
			result.add(bucket);			
		}
		
		return result;
	}

	*/



	@Override
	public Set<Bucket> getAllQuestionsLemma() {
		String sql = " select po.id,po.processedtitlelemma,po.processedbodylemma,po.processedcode "
				+ " from postsmin po "
				+ " where po.posttypeid=1"
				+ " and po.processedbodylemma != ''";
				//+ " and po.answercount>0 "
				//+ " and po.score>0";
		
			
		Query q = em.createNativeQuery(sql);
		List<Object[]> rows = q.getResultList();
		Set<Bucket> result = new LinkedHashSet<>(rows.size());
		for (Object[] row : rows) {
			Bucket bucket = new Bucket();
			bucket.setId((Integer) row[0]);
			bucket.setProcessedTitleLemma((String) row[1]);
			bucket.setProcessedBodyLemma((String) row[2]);
			bucket.setProcessedCode((String) row[3]);
			result.add(bucket);			
		}
		
		logger.info("Posts in getAllUpvotedScoredQuestionsLemma: "+result.size());
		
		return result;
	}

	@Override
	public Set<Bucket> getAllBucketsAndTopics() {
		String sql = " select po.id,po.topics "
				+ " from postsmin po "
				+ " where po.posttypeid=1"
				+ " and po.processedbodylemma != ''";
				//+ " and po.answercount>0 "
				//+ " and po.score>0";
		
			
		Query q = em.createNativeQuery(sql);
		List<Object[]> rows = q.getResultList();
		Set<Bucket> result = new LinkedHashSet<>(rows.size());
		for (Object[] row : rows) {
			Bucket bucket = new Bucket();
			bucket.setId((Integer) row[0]);
			bucket.setTopics((String) row[1]);
			result.add(bucket);			
		}
		
		logger.info("Posts in getAllUpvotedScoredQuestions: "+result.size());
		
		return result;
	}



	@Override
	public void saveTopic(Integer postId, String topic) {
		String sql = " update postsmin set topics = '"+topic+"' where id = "+postId;
			
		Query q = em.createNativeQuery(sql);
		int result = q.executeUpdate();
		if(result!=1) {
			System.out.println("Error in updating ... id: "+postId);
		}
		
	}


	@Override
	public void saveHotTopic(Integer id, String hotTopics) {
		String sql = " update postsmin set hottopics = '"+hotTopics+"' where id = "+id;
		if(StringUtils.isBlank(hotTopics)) {
			throw new RuntimeException("hot topic should not be null");
		}
		
		Query q = em.createNativeQuery(sql);
		int result = q.executeUpdate();
		if(result!=1) {
			System.out.println("Error in updating ... id: "+id);
		}
		
	}


	@Override
	public void saveTopScoredAnswers(Integer id, String topScoredAnswers) {
		String sql = " update postsmin set topscoredanswers = '"+topScoredAnswers+"' where id = "+id;
		if(!StringUtils.isBlank(topScoredAnswers)) {
			Query q = em.createNativeQuery(sql);
			int result = q.executeUpdate();
			if(result!=1) {
				System.out.println("Error in updating ... id: "+id);
			}

		}
	}





	@Override
	public Set<Bucket> getPossibleDupesInBodies() {
		String sql = " select po.id,po.processedbody,po.processedbodylemma "
				+ " from postsmin po "
				+ " where po.processedbody like 'possible duplicate%'" ;
				//+ " and po.answercount>0 "
				//+ " and po.score>0";
		
			
		Query q = em.createNativeQuery(sql);
		List<Object[]> rows = q.getResultList();
		Set<Bucket> result = new LinkedHashSet<>(rows.size());
		for (Object[] row : rows) {
			Bucket bucket = new Bucket();
			bucket.setId((Integer) row[0]);
			bucket.setProcessedBody((String) row[1]);
			bucket.setProcessedBodyLemma((String) row[2]);
			result.add(bucket);			
		}
		
		logger.info("Posts in getPossibleDupesInBodies: "+result.size());
		
		return result;
	}





	@Override
	public void updateBodies(Bucket bucket) {
		String sql = " update postsmin set processedbody = '"+bucket.getProcessedBody()+"', processedbodylemma='"+bucket.getProcessedBodyLemma()+"' where id = "+bucket.getId();
		
		Query q = em.createNativeQuery(sql);
		int result = q.executeUpdate();
		if(result!=1) {
			System.out.println("Error in updating ... id: "+bucket.getId());
		}
	}





	@Override
	public void findAllWordVectorsByTagId(Integer tagId,Map<String, SoContentWordVector> wordVectorsMap) {
		long initTime = System.currentTimeMillis();
		String sql = " select w.word,w.vectors,w.idf,w.id "
				+ " from wordvectors w "
				+ " where w.tagid="+tagId ;
				//+ " and po.answercount>0 "
				//+ " and po.score>0";
		
			
		Query q = em.createNativeQuery(sql);
		List<Object[]> rows = q.getResultList();
		CrarUtils.reportElapsedTime(initTime,"query executed... now parsing...");
		initTime = System.currentTimeMillis();
		String fastTextVectorsStr, sent2VecStr;
		String[] fastTextStrArray, sent2VecStrArray;
		for (Object[] row : rows) {
			fastTextVectorsStr = (String)row[1];
			fastTextStrArray = fastTextVectorsStr.trim().split(" ");
			double[] fastTextDoubleValues = Arrays.stream(fastTextStrArray).mapToDouble(Double::parseDouble).toArray();
			
			SoContentWordVector soContentWordVector = new SoContentWordVector(null,null,null,(Double)row[2],fastTextDoubleValues,(Integer)row[3]);
			wordVectorsMap.put((String)row[0], soContentWordVector);
				
		}
		CrarUtils.reportElapsedTime(initTime,"wordvectors size: "+rows.size()+ " for tagid: "+tagId);
		
	}





	@Override
	public Set<Integer> findParentIdsByPostIds(Set<Integer> answersIds) {
		String inCommand = "(";
		for(Integer questionId:answersIds) {
			inCommand+= questionId+ ",";
		}
		inCommand+= "#";
		inCommand = inCommand.replaceAll(",#", ")");
		
		String sql = " select p.parentid from postsmin p where p.id in "+inCommand;  
			
		Query q = em.createNativeQuery(sql);
		Set<Integer> parentIds = new LinkedHashSet<>(q.getResultList());
		
		return parentIds;
	}





	@Override
	public List<ThreadContent> getThreadContentsByIds(Set<Integer> questionsIds) {
		String idsIn = " ";
		for(Integer soId: questionsIds) {
			idsIn+= soId+ ",";
		}
		idsIn+= "#end";
		idsIn = idsIn.replace(",#end", "");
		
		String sql = " select t.id, t.title, t.questionbody,t.code,t.answersbody "  
				+ " from threadscontents t "  
				+ " where t.id in ("+idsIn+")";
		
			
		Query q = em.createNativeQuery(sql);
		List<Object[]> rows = q.getResultList();
		List<ThreadContent> result = new ArrayList<>(rows.size());
		for (Object[] row : rows) {
			ThreadContent thread = new ThreadContent();
			thread.setId((Integer) row[0]);
			thread.setTitle((String) row[1]);
			thread.setQuestionBody((String) row[2]);
			thread.setCode((String) row[3]);
			thread.setAnswersBody((String) row[4]);
			result.add(thread);			
		}
		
		return result;
	}









	





	
}
