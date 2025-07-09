package com.dpdocter.collections;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.BlogParagraph;
import com.dpdocter.enums.BlogCategoryType;
import com.dpdocter.enums.BlogSuperCategoryType;

@Document(collection = "blog_cl")
public class BlogCollection extends GenericCollection {

	@Id
	private ObjectId id;

	@Field
	private String title;

	@Field
	private String titleImage;

	@Field
	private Boolean isActive = true;

	@Field
	private BlogSuperCategoryType superCategory;
	
	@Field
	private BlogCategoryType category;

	@Field
	private ObjectId articleId;

	@Field
	private ObjectId userId;

	@Field
	private Integer views;

	@Field
	private Boolean discarded = false;

	@Field
	private String postBy;

	@Field
	private Integer noOfLikes = 0;

	@Field
	private String shortDesc;

	@Field
	private String metaKeyword;

	@Field
	private String metaDesc;

	@Field
	@Indexed(unique = true)
	private String slugURL;
	
	@Field
	private Boolean isSmilebirdBlog = false;
	
	@Field
	private List<BlogParagraph> blogParagraph;
	
	@Field
	private List<ObjectId> relatedBlogIds;

	@Field
	private List<ObjectId> tagIds;

	@Field
	private String readTime;
	
	public String getSlugURL() {
		return slugURL;
	}

	public void setSlugURL(String slugURL) {
		this.slugURL = slugURL;
	}

	public String getShortDesc() {
		return shortDesc;
	}

	public void setShortDesc(String shortDesc) {
		this.shortDesc = shortDesc;
	}

	public String getMetaKeyword() {
		return metaKeyword;
	}

	public void setMetaKeyword(String metaKeyword) {
		this.metaKeyword = metaKeyword;
	}

	public String getMetaDesc() {
		return metaDesc;
	}

	public void setMetaDesc(String metaDesc) {
		this.metaDesc = metaDesc;
	}

	public String getPostBy() {
		return postBy;
	}

	public void setPostBy(String postBy) {
		this.postBy = postBy;
	}

	public Integer getNoOfLikes() {
		return noOfLikes;
	}

	public void setNoOfLikes(Integer noOfLikes) {
		this.noOfLikes = noOfLikes;
	}

	public ObjectId getUserId() {
		return userId;
	}

	public void setUserId(ObjectId userId) {
		this.userId = userId;
	}

	public String getTitleImage() {
		return titleImage;
	}

	public void setTitleImage(String titleImage) {
		this.titleImage = titleImage;
	}

	public void setArticleId(ObjectId articleId) {
		this.articleId = articleId;
	}

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public BlogCategoryType getCategory() {
		return category;
	}

	public void setCategory(BlogCategoryType category) {
		this.category = category;
	}

	public ObjectId getArticleId() {
		return articleId;
	}

	public void setArticleiId(ObjectId articleId) {
		this.articleId = articleId;
	}

	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

	public Integer getViews() {
		return views;
	}

	public void setViews(Integer views) {
		this.views = views;
	}

	public BlogSuperCategoryType getSuperCategory() {
		return superCategory;
	}

	public void setSuperCategory(BlogSuperCategoryType superCategory) {
		this.superCategory = superCategory;
	}

	public Boolean getIsSmilebirdBlog() {
		return isSmilebirdBlog;
	}

	public void setIsSmilebirdBlog(Boolean isSmilebirdBlog) {
		this.isSmilebirdBlog = isSmilebirdBlog;
	}

	public List<BlogParagraph> getBlogParagraph() {
		return blogParagraph;
	}

	public void setBlogParagraph(List<BlogParagraph> blogParagraph) {
		this.blogParagraph = blogParagraph;
	}

	public List<ObjectId> getRelatedBlogIds() {
		return relatedBlogIds;
	}

	public void setRelatedBlogIds(List<ObjectId> relatedBlogIds) {
		this.relatedBlogIds = relatedBlogIds;
	}

	public List<ObjectId> getTagIds() {
		return tagIds;
	}

	public void setTagIds(List<ObjectId> tagIds) {
		this.tagIds = tagIds;
	}

	public String getReadTime() {
		return readTime;
	}

	public void setReadTime(String readTime) {
		this.readTime = readTime;
	}

	@Override
	public String toString() {
		return "BlogCollection [id=" + id + ", title=" + title + ", titleImage=" + titleImage + ", isActive=" + isActive
				+ ", category=" + category + ", articleId=" + articleId + ", userId=" + userId + ", views=" + views
				+ ", discarded=" + discarded + ", postBy=" + postBy + ", noOfLikes=" + noOfLikes + ", shortDesc="
				+ shortDesc + ", metaKeyword=" + metaKeyword + ", metaDesc=" + metaDesc + ", slugURL=" + slugURL + "]";
	}

}
