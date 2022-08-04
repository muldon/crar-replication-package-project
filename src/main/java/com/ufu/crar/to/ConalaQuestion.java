package com.ufu.crar.to;

public class ConalaQuestion {
	public String intent;
	public String rewritten_intent;
	public String snippet;
	public Integer question_id;
	
	public String getIntent() {
		return intent;
	}
	public void setIntent(String intent) {
		this.intent = intent;
	}
	public String getRewritten_intent() {
		return rewritten_intent;
	}
	public void setRewritten_intent(String rewritten_intent) {
		this.rewritten_intent = rewritten_intent;
	}
	public String getSnippet() {
		return snippet;
	}
	public void setSnippet(String snippet) {
		this.snippet = snippet;
	}
	public Integer getQuestion_id() {
		return question_id;
	}
	public void setQuestion_id(Integer question_id) {
		this.question_id = question_id;
	}
	@Override
	public String toString() {
		return "ConalaQuestion [intent=" + intent + ", rewritten_intent=" + rewritten_intent + ", snippet=" + snippet + ", question_id=" + question_id + "]";
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((intent == null) ? 0 : intent.hashCode());
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
		ConalaQuestion other = (ConalaQuestion) obj;
		if (intent == null) {
			if (other.intent != null)
				return false;
		} else if (!intent.equals(other.intent))
			return false;
		return true;
	}
	
	
}

