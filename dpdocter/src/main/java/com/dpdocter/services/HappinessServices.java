package com.dpdocter.services;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.dpdocter.beans.Activity;
import com.dpdocter.beans.ActivityAssign;
import com.dpdocter.beans.ExerciseImage;
import com.dpdocter.beans.Language;
import com.dpdocter.beans.Mindfulness;
import com.dpdocter.beans.MindfulnessAssign;
import com.dpdocter.beans.Stories;
import com.dpdocter.beans.StoriesAssign;
import com.dpdocter.beans.TodaySession;


public interface HappinessServices {

	public Language addEditLanguage(Language request);

	public Language getLanguage(String id);

	public List<Language> getLanguages(int size, int page, Boolean discarded,String searchTerm);

	public Language deleteLanguage(String id,Boolean discarded);
	
	public Integer countLanguage(Boolean discarded,String searchTerm);
	
	public Activity addEditActivity(Activity request);
	
	public Activity deleteActivity(String id, Boolean discarded);
	
	public List<Activity> getActivity(int size, int page, Boolean discarded,String searchTerm,String languageId);
	
	public Integer countActivity(Boolean discarded,String searchTerm);
	
	
	public Mindfulness addEditMindfulness(Mindfulness request);
	
	public Mindfulness uploadImage(MultipartFile file);
	
	public Mindfulness uploadVideo(MultipartFile file);
	
	public Mindfulness saveImage(MultipartFile file, String recordPath, Boolean createThumbnail);

	public Mindfulness saveVideo(MultipartFile file, String recordPath);
	
	
	
	public List<Mindfulness> getMindfulness(int size,int page,String searchTerm,Boolean discarded);
	
	public Mindfulness deleteMindfulness(String id,Boolean discarded);
	
	public Integer countMindfulness(Boolean discarded,String searchTerm);
	
	public Stories addEditStories(Stories request);
	
	public List<Stories> getStories(int size,int page,String searchTerm,Boolean discarded,String languageId);
	
	public Stories deleteStories(String id,Boolean discarded);
	
	public Integer countStories(Boolean discarded,String searchTerm);
	
	
	public TodaySession addEditSession(TodaySession request) ;
	
	public List<TodaySession> getSession(int size,int page,String searchTerm,Boolean discarded);
	
	public TodaySession deleteSession(String id,Boolean discarded);

	Integer countSession(Boolean discarded, String searchTerm);
	
	

	public ActivityAssign addEditActivityAssign(ActivityAssign request);
	
	public List<ActivityAssign> getActivityAssign(int size,int page,String searchTerm,Boolean discarded);
	
	public ActivityAssign deleteActivityAssign(String id,Boolean discarded);
	
	public StoriesAssign addEditStoriesAssign(StoriesAssign request);
	
	public List<StoriesAssign> getStoriesAssign(int size,int page,String searchTerm,Boolean discarded);
	
	public StoriesAssign deleteStoriesAssign(String id,Boolean discarded);
	
	public MindfulnessAssign addEditMindfulnessAssign(MindfulnessAssign request);
	
	public List<MindfulnessAssign> getMindfulnessAssign(int size,int page,String searchTerm,Boolean discarded);
	
	public MindfulnessAssign deleteMindfulnessAssign(String id,Boolean discarded);
}
