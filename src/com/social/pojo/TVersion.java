package com.social.pojo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;


@Entity
@Table(catalog = "dbsocial")
public class TVersion {
	private Long id;
	private int version_code;
	private String version_name;
	private String description;
	private String app_path;
	private int download_count;
	
//	private Long optlockversion1;
//	private Long optlockversion2;
//	
//	@Version
//	@Column(name = "optlock1")
//	public Long getOptlockversion1() {
//		return optlockversion1;
//	}
//	public void setOptlockversion1(Long optlockversion1) {
//		this.optlockversion1 = optlockversion1;
//	}
//	@Version
//	@Column(name = "optlock2")
//	public Long getOptlockversion2() {
//		return optlockversion2;
//	}
//	public void setOptlockversion2(Long optlockversion2) {
//		this.optlockversion2 = optlockversion2;
//	}
	
	@Id
    @GeneratedValue(strategy=GenerationType.AUTO)
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public int getVersion_code() {
		return version_code;
	}
	public void setVersion_code(int version_code) {
		this.version_code = version_code;
	}
	public String getVersion_name() {
		return version_name;
	}
	public void setVersion_name(String version_name) {
		this.version_name = version_name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getApp_path() {
		return app_path;
	}
	public void setApp_path(String app_path) {
		this.app_path = app_path;
	}
	public int getDownload_count() {
		return download_count;
	}
	public void setDownload_count(int download_count) {
		this.download_count = download_count;
	}
	

	
	
	

}
