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
public class TGroup {
	private Long id;
	private String groupname;
	private String description;
	private String groupimg;
	private TUser user;
	private Date date;
	private double longitude;
	private double latitude;
	private String point;
	private int state;
	private int grouplevel;
	private String attention;
	private String limited;
	private Long optlockversion1;
	private Long optlockversion2;
	private String hx_group_id;
	private int red_pocket_count;
	private int group_type;
	
	
	@Id
    @GeneratedValue(strategy=GenerationType.AUTO)
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getGroupname() {
		return groupname;
	}
	public void setGroupname(String groupname) {
		this.groupname = groupname;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getGroupimg() {
		return groupimg;
	}
	public void setGroupimg(String groupimg) {
		this.groupimg = groupimg;
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
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
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
	public String getPoint() {
		return point;
	}
	public void setPoint(String point) {
		this.point = point;
	}
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}
	public int getGrouplevel() {
		return grouplevel;
	}
	public void setGrouplevel(int grouplevel) {
		this.grouplevel = grouplevel;
	}
	public String getAttention() {
		return attention;
	}
	public void setAttention(String attention) {
		this.attention = attention;
	}
	public String getLimited() {
		return limited;
	}
	public void setLimited(String limited) {
		this.limited = limited;
	}
	
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
	public String getHx_group_id() {
		return hx_group_id;
	}
	public void setHx_group_id(String hx_group_id) {
		this.hx_group_id = hx_group_id;
	}
	public int getRed_pocket_count() {
		return red_pocket_count;
	}
	public void setRed_pocket_count(int red_pocket_count) {
		this.red_pocket_count = red_pocket_count;
	}
	public int getGroup_type() {
		return group_type;
	}
	public void setGroup_type(int group_type) {
		this.group_type = group_type;
	}
	
	
	
	
	
	

}
