package com.dpdocter.services;

import java.util.List;

import com.dpdocter.beans.Blog;
import com.dpdocter.beans.BlogImageUploadReqest;
import com.dpdocter.enums.BlogCategoryType;
import com.dpdocter.response.BlogResponse;
import com.dpdocter.response.TagResponse;

import common.util.web.Response;

public interface BlogService {
	public String saveBlogArticle(String article);

	public String getBlogArticle(String id);

	public Blog saveBlog(Blog blog);

	public BlogResponse getBlogs(int size, int page, String category, String userId, String title, Boolean discarded,
			Boolean isSmilebirdBlog);

	public List<String> uploadBlogImages(BlogImageUploadReqest request);

	public Blog deleteBlog(String blogId, String userId);

	public List<String> getBlogImagesUrl(int size, int page, String category, String userId);

	Blog getBlog(String blogId, String slugURL);

	public BlogCategoryType[] getBlogCategory();

	public Boolean updateBlogSuperCategory(String blogId, String userId, String category);

	public TagResponse saveTag(TagResponse request);

	public Boolean deleteTag(String tagId);

	public Response<Object> getTags(int size, int page, Boolean discarded);
}
