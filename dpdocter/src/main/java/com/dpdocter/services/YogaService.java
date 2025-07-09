package com.dpdocter.services;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.web.multipart.MultipartFile;

import com.dpdocter.beans.CuratedYogaSession;
import com.dpdocter.beans.Essentials;
import com.dpdocter.beans.Injury;
import com.dpdocter.beans.Yoga;
import com.dpdocter.beans.YogaClasses;
import com.dpdocter.beans.YogaDisease;
import com.dpdocter.beans.YogaSession;
import com.dpdocter.beans.YogaTeacher;


public interface YogaService {
	
	YogaTeacher addEditTeacher(YogaTeacher request);
	
	List<YogaTeacher> getTeachers(int page,int size,Boolean discarded,String searchTerm,List<ObjectId>teacherIds);
	
	YogaTeacher getTeacherById(String id);
	
	YogaTeacher deleteTeacher(String id,Boolean discarded);
	
	public Integer countYogaTeacher(Boolean discarded, String searchTerm);
	
	Injury addEditInjury(Injury request);
	List<Injury> getInjury(int page,int size,Boolean discarded,String searchTerm,List<ObjectId>injuryIds);
	Injury getInjuryById(String id);
	Injury deleteInjury(String id,Boolean discarded);
	public Integer countInjury(Boolean discarded, String searchTerm);
	
	
	Yoga addEditYoga(Yoga request);
	List<Yoga> getYoga(int page,int size,Boolean discarded,String searchTerm,List<ObjectId>yogaIds);
	Yoga getYogaById(String id);
	Yoga deleteYoga(String id,Boolean discarded);
	public Integer countYoga(Boolean discarded, String searchTerm);
	
	
	YogaClasses addEditYogaClasses(YogaClasses request);
	List<YogaClasses> getYogaClasses(int page,int size,Boolean discarded,String searchTerm,List<ObjectId>yogaClassesIds);
	YogaClasses getYogaClassesById(String id);
	YogaClasses deleteYogaClasses(String id,Boolean discarded);
	public Integer countYogaClasses(Boolean discarded, String searchTerm);
	
	
	YogaSession addEditYogaSession(YogaSession request);
	List<YogaSession> getYogaSession(int page,int size,Boolean discarded,String searchTerm,List<ObjectId>yogaSessionIds);
	YogaSession getYogaSessionById(String id);
	YogaSession deleteYogaSession(String id,Boolean discarded);
	public Integer countYogaSession(Boolean discarded, String searchTerm);
	
	YogaDisease addEditYogaDisease(YogaDisease request);
	List<YogaDisease> getYogaDisease(int page,int size,Boolean discarded,String searchTerm,List<ObjectId>yogaDiseaseIds);
	YogaDisease getYogaDiseaseById(String id);
	YogaDisease deleteYogaDisease(String id,Boolean discarded);
	public Integer countYogaDisease(Boolean discarded, String searchTerm);
	
	
	CuratedYogaSession addEditCuratedYogaSession(CuratedYogaSession request);
	List<CuratedYogaSession> getCuratedYogaSession(int page,int size,Boolean discarded,String searchTerm);
	CuratedYogaSession getCuratedYogaSessionById(String id);
	CuratedYogaSession deleteCuratedYogaSession(String id,Boolean discarded);
	public Integer countCuratedYogaSession(Boolean discarded, String searchTerm);

	Essentials addEditEssentials(Essentials request);
	List<Essentials> getEssentials(int page,int size,Boolean discarded,String searchTerm);
	Essentials getEssentialById(String id);
	Essentials deleteEssentials(String id,Boolean discarded);
	public Integer countEssentials(Boolean discarded, String searchTerm);

	
	
	public Yoga uploadImage(MultipartFile file);
	public Yoga uploadVideo(MultipartFile file);

	
}
