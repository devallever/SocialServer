package com.social.pojo;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;



@Entity
@Table(catalog = "dbsocial")
public class TComment {
	private Long id;
	private String content;
	private TNews news;
	private TUser user;
	private TComment comment;
	private Date date;
	private String audio_path;
	
	private Long optlockversion1;
	private Long optlockversion2;
	
	@Version
	@Column(name = "optlock1")
	public Long getOptlockversion1() {
		return optlockversion1;
	}
	public void setOptlockversion1(Long optlockversion1) {
		this.optlockversion1 = optlockversion1;
	}
	@Version
	@Column(name = "optlock2")
	public Long getOptlockversion2() {
		return optlockversion2;
	}
	public void setOptlockversion2(Long optlockversion2) {
		this.optlockversion2 = optlockversion2;
	}
	
	@Id
    @GeneratedValue(strategy=GenerationType.AUTO)
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	 
	@ManyToOne(fetch=FetchType.LAZY)
	@OnDelete(action=OnDeleteAction.CASCADE)
	public TNews getNews() {
		return news;
	}
	public void setNews(TNews news) {
		this.news = news;
	}
	@ManyToOne(fetch=FetchType.LAZY)
	@OnDelete(action=OnDeleteAction.CASCADE)
	public TUser getUser() {
		return user;
	}
	public void setUser(TUser user) {
		this.user = user;
	}
	@ManyToOne(fetch=FetchType.LAZY)
	@OnDelete(action=OnDeleteAction.CASCADE)
	public TComment getComment() {
		return comment;
	}
	public void setComment(TComment comment) {
		this.comment = comment;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public String getAudio_path() {
		return audio_path;
	}
	public void setAudio_path(String audio_path) {
		this.audio_path = audio_path;
	}
	
	

	
}
