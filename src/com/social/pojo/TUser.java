package com.social.pojo;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;


@Entity
@Table(catalog = "dbsocial")
public class TUser {
	private Long id;
	private String username;
	private String password;
	private String email;
	private String phone;
	private String headpath;
	private int state;
	private String nickname;
	private double longitude;
	private double latitude;
	private String city;
	private String sex;
	private String signature;
	private int age;
	private String occupation;
	private String constellation;
	private String hight;
	private String weight;
	private String figure;
	private String emotion;
	private int is_vip;
	private int visited_count;
	private int credit;
	private String second_name;
	private String jpush_registration_id;
	private String onlinestate;
	private int messagecount;
	private String qq_open_id;
	private long login_count;
	private Date login_time;
	private String address;
	
	private int video_fee;
	private int voice_fee;
	private int accept_video;
	
	
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
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getHeadpath() {
		return headpath;
	}
	public void setHeadpath(String headpath) {
		this.headpath = headpath;
	}
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
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
	public String getSex() {
		return sex;
	}
	public void setSex(String sex) {
		this.sex = sex;
	}
	public String getSignature() {
		return signature;
	}
	public void setSignature(String signature) {
		this.signature = signature;
	}
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	public String getOccupation() {
		return occupation;
	}
	public void setOccupation(String occupation) {
		this.occupation = occupation;
	}
	public String getConstellation() {
		return constellation;
	}
	public void setConstellation(String constellation) {
		this.constellation = constellation;
	}
	public String getHight() {
		return hight;
	}
	public void setHight(String hight) {
		this.hight = hight;
	}
	public String getWeight() {
		return weight;
	}
	public void setWeight(String weight) {
		this.weight = weight;
	}
	public String getFigure() {
		return figure;
	}
	public void setFigure(String figure) {
		this.figure = figure;
	}
	public String getEmotion() {
		return emotion;
	}
	public void setEmotion(String emotion) {
		this.emotion = emotion;
	}
	public int getIs_vip() {
		return is_vip;
	}
	public void setIs_vip(int is_vip) {
		this.is_vip = is_vip;
	}
	public int getVisited_count() {
		return visited_count;
	}
	public void setVisited_count(int visited_count) {
		this.visited_count = visited_count;
	}
	public int getCredit() {
		return credit;
	}
	public void setCredit(int credit) {
		this.credit = credit;
	}
	public String getSecond_name() {
		return second_name;
	}
	public void setSecond_name(String second_name) {
		this.second_name = second_name;
	}
	public String getJpush_registration_id() {
		return jpush_registration_id;
	}
	public void setJpush_registration_id(String jpush_registration_id) {
		this.jpush_registration_id = jpush_registration_id;
	}
	public String getOnlinestate() {
		return onlinestate;
	}
	public void setOnlinestate(String onlinestate) {
		this.onlinestate = onlinestate;
	}
	public int getMessagecount() {
		return messagecount;
	}
	public void setMessagecount(int messagecount) {
		this.messagecount = messagecount;
	}
	public String getQq_open_id() {
		return qq_open_id;
	}
	public void setQq_open_id(String qq_open_id) {
		this.qq_open_id = qq_open_id;
	}
	public long getLogin_count() {
		return login_count;
	}
	public void setLogin_count(long login_count) {
		this.login_count = login_count;
	}
	public Date getLogin_time() {
		return login_time;
	}
	public void setLogin_time(Date login_time) {
		this.login_time = login_time;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public int getVideo_fee() {
		return video_fee;
	}
	public void setVideo_fee(int video_fee) {
		this.video_fee = video_fee;
	}
	public int getVoice_fee() {
		return voice_fee;
	}
	public void setVoice_fee(int voice_fee) {
		this.voice_fee = voice_fee;
	}
	public int getAccept_video() {
		return accept_video;
	}
	public void setAccept_video(int accept_video) {
		this.accept_video = accept_video;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

	
	
	
	
}
