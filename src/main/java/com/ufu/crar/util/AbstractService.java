package com.ufu.crar.util;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import com.ufu.crar.repository.ApiAnswersRepository;
import com.ufu.crar.repository.CommentsRepository;
import com.ufu.crar.repository.EvaluationRepository;
import com.ufu.crar.repository.ExperimentRepository;
import com.ufu.crar.repository.ExternalQuestionRepository;
import com.ufu.crar.repository.GenericRepository;
import com.ufu.crar.repository.MetricResultRepository;
import com.ufu.crar.repository.PostApisRepository;
import com.ufu.crar.repository.PostsRepository;
import com.ufu.crar.repository.QueryRepository;
import com.ufu.crar.repository.RankRepository;
import com.ufu.crar.repository.RelatedPostRepository;
import com.ufu.crar.repository.ResultEvaluationRepository;
import com.ufu.crar.repository.ResultRepository;
import com.ufu.crar.repository.SoContentWordVectorsRepository;
import com.ufu.crar.repository.SurveyRepository;
import com.ufu.crar.repository.SurveyUserRepository;
import com.ufu.crar.repository.ThreadContentRepository;
import com.ufu.crar.repository.UsersRepository;
import com.ufu.crar.to.Comment;
import com.ufu.crar.to.Post;
import com.ufu.crar.to.User;

public abstract class AbstractService {
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	protected PostsRepository postsRepository;
	
	@Autowired
	protected CommentsRepository commentsRepository;	
	
	@Autowired
	protected GenericRepository genericRepository;
	
	@Autowired
	protected ExperimentRepository experimentRepository;
	
	@Autowired
	protected ExternalQuestionRepository externalQuestionRepository; 
	
	@Autowired
	protected RelatedPostRepository relatedPostRepository;
	
	@Autowired
	protected EvaluationRepository evaluationRepository;
	
	@Autowired
	protected ResultRepository resultRepository;
	
	@Autowired
	protected RankRepository rankRepository;
	
		
	@Autowired
	protected UsersRepository usersRepository;
	
	@Autowired
	protected SurveyRepository surveyRepository;
	
	@Autowired
	protected SurveyUserRepository surveyUserRepository;
	
	@Autowired
	protected MetricResultRepository metricResultRepository;
	
	@Autowired
	protected SoContentWordVectorsRepository soContentWordVectorsRepository;
	
	@Autowired
	protected ApiAnswersRepository apiAnswersRepository;
	
	@Autowired
	protected PostApisRepository postApisRepository;
	
	@Autowired
	protected ThreadContentRepository threadContentRepository;
	
	@Autowired
	protected QueryRepository queryRepository;
	
	@Autowired
	protected ResultEvaluationRepository resultEvaluationRepository;
	
	
	@Autowired
	public CrarUtils crarUtils;
	
	protected int countExcludedPosts;
	protected int countPostIsAnAnswer;
	
	public Timestamp getCurrentDate(){
		Timestamp ts_now = new Timestamp(Calendar.getInstance().getTimeInMillis());
		return ts_now;
	}
	

	
	private void setCommentsUsers(List<Comment> questionComments) {
		for(Comment comment: questionComments) {
			if(comment.getUserId()!=null) {
				comment.setUser(findUserById(comment.getUserId()));
			}
			//allCommentsIds.add(comment.getId());
		}
		
	}
	
	@Transactional(readOnly = true)
	public User findUserById(Integer userId) {
		return usersRepository.findById(userId).orElse(null);
	}
	

	/*
	 * Answers have postTypeId = 2
	 */
	@Transactional(readOnly = true)
	public List<Post> findUpVotedAnswersByQuestionId(Integer questionId) {
		//return postsRepository.findByParentIdAndPostTypeId(questionId,2,new Sort(Sort.Direction.ASC, "id"));
		//return postsRepository.findByParentId(questionId,new Sort(Sort.Direction.ASC, "id"));
		return postsRepository.findUpVotedAnswersByQuestionId(questionId);
	}
	
	@Transactional(readOnly = true)
	public Post findPostById(Integer id) {
		return postsRepository.findById(id).orElse(null);
	}
	

	@Transactional(readOnly = true)
	public List<Comment> getCommentsByPostId(Integer postId) {
		return commentsRepository.findByPostId(postId,new Sort(Sort.Direction.ASC, "id"));
	}

}
