package com.dpdocter.services;

import java.util.List;

import org.apache.velocity.VelocityContext;

import com.dpdocter.collections.UserCollection;

public interface MailBodyGenerator {
	public String generateActivationEmailBody(String fName, String tokenId, String templatePath, String doctorName,
			String clinicName) throws Exception;

	public String generateForgotPasswordEmailBody(String fName, String tokenId);

	public String generateForgotUsernameEmailBody(List<UserCollection> userCollection);

	public String generateIssueTrackEmailBody(String userName, String firstName, String middleName, String lastName);

	public String generateResetPasswordSuccessEmailBody(String firstName);

	public String generateRecordsShareOtpBeforeVerificationEmailBody(String emailAddress, String firstName,
			String doctorName);

	public String generateRecordsShareOtpAfterVerificationEmailBody(String emailAddress, String firstName,
			String doctorName);

	String generateAppointmentEmailBody(String doctorName, String patientName, String dateTime, String clinicName,
			String templatePath);

	String generateEmailBody(String userName, String resumeType, String templatePath) throws Exception;

	public String generateEMREmailBody(String patientName, String doctorName, String clinicName, String clinicAddress,
			String mailRecordCreatedDate, String medicalRecordType, String templatePath);

	public String generateFeedbackEmailBody(String patientName, String doctorName, String locationName,
			String uniqueFeedbackId, String templatePath);

	String generateAppLinkEmailBody(String appType, String bitLink, String templatePath);

	public String generateAppLinkEmailBody(String appType, String appBitLink, String appDeviceType, String string);

	String generateActivationEmailBodyForPharmaCompany(String fName, String tokenId, String templatePath,
			String doctorName, String clinicName) throws Exception;

	String doctorWelcomeEmailBody(String fName, String tokenId, String templatePath, String doctorName,
			String clinicName) throws Exception;

	public String conferenceEmailBody(String fName, String link, String templatePath);

	String superStartActivateAccount(String fName, String link, String templatePath, String type);
	
	public String generateAdminActivationEmailBody(String fname,String userName, String tokenId, String templatePath);

	String subscriptionEmailBody(String title, String fName,String value, String noOfDays, String packageName, String templatePath);

	
	String subscriptionPaymentEmailBody(String title, String fName, String noOfDays, String packageName,
			String paymentMode, String totalCost, String templatePath);

	String generateAppointmentEmailBody(String doctorName, String patientName, String dateTime, String clinicName,
			String templatePath, String branch);

	String generateOnlineAppointmentEmailBody(String doctorName, String patientName, String dateTime, String clinicName,
			String templatePath, String branch, String consultationType);

	public String followUpPatientEmailBody(String emailBody, String string);

	public String generateSmilebirdAppointmentEmailBody(String emailBody, String string);


}
