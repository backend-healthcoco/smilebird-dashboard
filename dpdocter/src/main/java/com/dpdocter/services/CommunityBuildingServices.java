package com.dpdocter.services;

import java.util.List;


import com.dpdocter.beans.Comment;
import com.dpdocter.beans.CommentRequest;
import com.dpdocter.beans.Feeds;
import com.dpdocter.beans.FeedsRequest;
import com.dpdocter.beans.FeedsResponse;
import com.dpdocter.beans.Forum;
import com.dpdocter.beans.ForumRequest;
import com.dpdocter.beans.ForumResponse;

public interface CommunityBuildingServices {

	ForumResponse addEditForumResponse(ForumRequest request);

	List<Forum> getForumResponse(int page, int size, String searchTerm, Boolean discarded);
	
	ForumResponse getForumResponseById(String id);

	List<FeedsResponse> getArticles(int size, int page, String searchTerm, Boolean discarded, String languageId);


	List<Feeds> getFeeds(int size, int page, Boolean discarded, String type, String languageId,String searchTerm);

	FeedsResponse getArticleById(String id,String languageId);

	



	Comment addEditComment(CommentRequest request);

	List<FeedsResponse> getLearningSession(int page, int size, Boolean discarded, String searchTerm, String languageId,
			String type);

	Feeds addEditLikes(String userId, String postId);

	Feeds savePost(String postId, boolean isSaved);

	

	

	

	FeedsResponse addEditPost(FeedsRequest request);

	List<Comment> getComments(int size, int page, Boolean discarded, String searchTerm,String feedId);

	Comment deleteCommentsById(String id, Boolean discarded);

	FeedsResponse deleteFeedsById(String id, Boolean discarded);

	Boolean deleteForumResponseById(String id, Boolean discarded);

	
	
	
	
}
