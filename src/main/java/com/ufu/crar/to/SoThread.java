package com.ufu.crar.to;

import java.util.List;


public class SoThread {
	private static final long serialVersionUID = -11115190111815641L;
	
    private Post question;
	
	private List<Post> answers;
	
	
	
	public SoThread() {
		
	}



	public SoThread(Post question, List<Post> answers) {
		super();
		this.question = question;
		this.answers = answers;
	}



	public Post getQuestion() {
		return question;
	}



	public void setQuestion(Post question) {
		this.question = question;
	}



	public List<Post> getAnswers() {
		return answers;
	}



	public void setAnswers(List<Post> answers) {
		this.answers = answers;
	}



	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((question == null) ? 0 : question.hashCode());
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
		SoThread other = (SoThread) obj;
		if (question == null) {
			if (other.question != null)
				return false;
		} else if (!question.equals(other.question))
			return false;
		return true;
	}



	@Override
	public String toString() {
		return "SoThread [question=" + question + ", answers=" + answers + "]";
	}
	
	

	

	
	
    
}