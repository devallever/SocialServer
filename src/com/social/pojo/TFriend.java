package com.social.pojo;

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
public class TFriend {
	private Long id;
	private TUser user;
	private TUser friend;
	private int state;
	private TFriendgroup friendgroup;
	private String second_name;
	private int show_location;
	
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
	public TUser getFriend() {
		return friend;
	}
	public void setFriend(TUser friend) {
		this.friend = friend;
	}
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}
	
	@ManyToOne(fetch=FetchType.LAZY)
	@OnDelete(action=OnDeleteAction.CASCADE)
	public TFriendgroup getFriendgroup() {
		return friendgroup;
	}
	public void setFriendgroup(TFriendgroup friendgroup) {
		this.friendgroup = friendgroup;
	}
	public String getSecond_name() {
		return second_name;
	}
	public void setSecond_name(String second_name) {
		this.second_name = second_name;
	}
	public int getShow_location() {
		return show_location;
	}
	public void setShow_location(int show_location) {
		this.show_location = show_location;
	}
	
	
	
	
	

	

}
