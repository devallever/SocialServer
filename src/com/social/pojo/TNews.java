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
public class TNews {
	private Long id;
	private String content;
	private TUser user;
	private Date Date;
	private double longitude;
	private double latitude;
	private String city;
	private int lickcount;
	private int commentcount;
	private int visited_count;
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
	public TUser getUser() {
		return user;
	}
	public void setUser(TUser user) {
		this.user = user;
	}
	public Date getDate() {
		return Date;
	}
	public void setDate(Date date) {
		Date = date;
	}
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public int getLickcount() {
		return lickcount;
	}
	public void setLickcount(int lickcount) {
		this.lickcount = lickcount;
	}
	public int getCommentcount() {
		return commentcount;
	}
	public void setCommentcount(int commentcount) {
		this.commentcount = commentcount;
	}
	public int getVisited_count() {
		return visited_count;
	}
	public void setVisited_count(int visited_count) {
		this.visited_count = visited_count;
	}
	public String getAudio_path() {
		return audio_path;
	}
	public void setAudio_path(String audio_path) {
		this.audio_path = audio_path;
	}
	
	
	
	
	

	
	
	
	

}
