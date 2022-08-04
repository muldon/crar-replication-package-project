package com.ufu.crar.to;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

import com.ufu.crar.to.Survey.SurveyEnum;

@XmlRootElement
@Entity
@Table(name = "surveyuser")
public class SurveyUser {
	private static final long serialVersionUID = -111252191111815641L;
	@Id
    @SequenceGenerator(name="surveyuser_id", sequenceName="surveyuser_id_seq",allocationSize=1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="surveyuser_id")
	private Integer id;
	
    private String login;
	
	private String nick;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getNick() {
		return nick;
	}

	public void setNick(String nick) {
		this.nick = nick;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	@Override
	public String toString() {
		return "SurveyUser [id=" + id + ", login=" + login + ", nick=" + nick + "]";
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
		SurveyUser other = (SurveyUser) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	public SurveyUser() {
		super();
	}

	public SurveyUser(String login, String nick) {
		super();
		this.login = login;
		this.nick = nick;
	}

	
	


	public enum SurveyUserEnum {
		INTERNAL_USER_RODRIGO (1,"Rodrigo"), 
		INTERNAL_USER_KLERISSON (2,"Klerisson");
		
	   
		private final Integer id;
		private final String name;
		
		
		
		SurveyUserEnum(Integer id,String name){
			this.id = id;
			this.name = name;
		
		}
		
		SurveyUserEnum(SurveyUserEnum surveyEnum){
			this.id = surveyEnum.id;
			this.name = surveyEnum.name;
		}
		
	   
	
		public Integer getId() {
			return id;
		}
	
		public String getName() {
			return name;
		}
	
	
	}	
	
	
	public static Boolean isInternalSurveyUser(Integer userId) {
		if(userId.equals(SurveyUserEnum.INTERNAL_USER_RODRIGO.getId()) 
			|| userId.equals(SurveyUserEnum.INTERNAL_USER_KLERISSON.getId())){
			return true;
		}
		return false;
	}
	
	
    
}