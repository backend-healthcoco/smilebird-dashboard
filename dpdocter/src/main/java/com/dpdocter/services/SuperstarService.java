package com.dpdocter.services;

import java.util.List;

import com.dpdocter.beans.DoctorSchoolAssociation;
import com.dpdocter.beans.NutritionSchoolAssociation;
import com.dpdocter.beans.School;
import com.dpdocter.beans.SchoolBranch;
import com.dpdocter.beans.SuperStarHealthCamp;

public interface SuperstarService {

	School addEditSchool(School request);

	SchoolBranch addEditSchoolBranch(SchoolBranch request);

	School getSchoolById(String id);

	List<School> getSchoolList(String updatedTime, String searchTerm, int page, int size, Boolean isActivated);

	List<SchoolBranch> getSchoolBranchesForSchool(String schoolId, String updatedTime, String searchTerm, int page,
			int size, Boolean isActivated);

	School activateSchool(String id, Boolean isActivated);

	School verifySchool(String id, Boolean isVerified);

	SchoolBranch getSchoolBranchById(String id);

	SchoolBranch activateSchoolBranch(String id, Boolean isActivated);

	SchoolBranch verifySchoolBranch(String id, Boolean isVerified);

	SchoolBranch discardSchoolBranch(String id, Boolean discarded);

	List<SchoolBranch> getSchoolBranches(String updatedTime, String searchTerm, int page, int size, Boolean isActivated,
			Boolean isDiscarded);

	Integer getSchoolListCount(String updatedTime, String searchTerm, Boolean isActivated);

	Integer getSchoolBranchesForSchoolCount(String schoolId, String updatedTime, String searchTerm,
			Boolean isActivated);

	Integer getSchoolBranchesCount(String updatedTime, String searchTerm, Boolean isActivated, Boolean isDiscarded);

	Boolean checkUserNameExists(String userName);

	SuperStarHealthCamp addEditHealthcamp(SuperStarHealthCamp request);

	SuperStarHealthCamp getHealthcampById(String id);

	Boolean discardHealthCamp(String id, Boolean discarded);

	List<SuperStarHealthCamp> getHealthcampByDoctorId(String doctorId);

	List<SuperStarHealthCamp> getHealthcamps(String updatedTime, String searchTerm, int page, int size);

	Integer getHealthcampCount(String updatedTime, String searchTerm, int page, int size);

	NutritionSchoolAssociation addEditNutritionSchoolAssociation(NutritionSchoolAssociation request);

	Integer getNutritionSchoolAssociationCount(String branchId, String updatedTime, String searchTerm, int page,
			int size);

	List<NutritionSchoolAssociation> getNutritionSchoolAssociation(String branchId, String updatedTime,
			String searchTerm, int page, int size);

	DoctorSchoolAssociation addEditDoctorSchoolAssociation(DoctorSchoolAssociation request);

	List<DoctorSchoolAssociation> getDoctorSchoolAssociation(String branchId, String updatedTime, String searchTerm,
			int page, int size);

	Integer getDoctorSchoolAssociationCount(String branchId, String updatedTime, String searchTerm);

	
	
}
