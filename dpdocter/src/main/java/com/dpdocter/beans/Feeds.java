package com.dpdocter.beans;

import java.util.List;

import com.dpdocter.collections.GenericCollection;
import com.dpdocter.enums.CommunityType;
import com.dpdocter.enums.UserType;

public class Feeds extends GenericCollection {

	private String id;

	private UserPost user;

	private List<ArticleDetails> multilingual;

	private CommunityType type;

	private String userId;

	private String userName;
	
	private UserType userType;

	private String userImageUrl;
	
	private String speciality;
	

	private List<String> userIds;

	private Boolean isSaved;

	private Long likes=0l;

	private Boolean discarded = false;

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the userId
	 */

	/**
	 * @return the discarded
	 */
	public Boolean getDiscarded() {
		return discarded;
	}

	/**
	 * @param discarded the discarded to set
	 */
	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

	public List<String> getUserIds() {
		return userIds;
	}

	public void setUserIds(List<String> userIds) {
		this.userIds = userIds;
	}

	public Long getLikes() {
		return likes;
	}

	public void setLikes(Long likes) {
		this.likes = likes;
	}

	public UserPost getUser() {
		return user;
	}

	public void setUser(UserPost user) {
		this.user = user;
	}

	public List<ArticleDetails> getMultilingual() {
		return multilingual;
	}

	public void setMultilingual(List<ArticleDetails> multilingual) {
		this.multilingual = multilingual;
	}

	public CommunityType getType() {
		return type;
	}

	public void setType(CommunityType type) {
		this.type = type;
	}

	
	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public UserType getUserType() {
		return userType;
	}

	public void setUserType(UserType userType) {
		this.userType = userType;
	}

	public String getSpeciality() {
		return speciality;
	}

	public void setSpeciality(String speciality) {
		this.speciality = speciality;
	}

	public Boolean getIsSaved() {
		return isSaved;
	}

	public void setIsSaved(Boolean isSaved) {
		this.isSaved = isSaved;
	}

	public String getUserImageUrl() {
		return userImageUrl;
	}

	public void setUserImageUrl(String userImageUrl) {
		this.userImageUrl = userImageUrl;
	}
	
	

}
