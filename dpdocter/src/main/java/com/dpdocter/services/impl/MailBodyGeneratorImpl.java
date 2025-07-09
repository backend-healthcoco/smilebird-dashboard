package com.dpdocter.services.impl;

import java.io.StringWriter;
import java.util.List;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dpdocter.collections.UserCollection;
import com.dpdocter.services.MailBodyGenerator;

@Service
public class MailBodyGeneratorImpl implements MailBodyGenerator {

	@Value(value = "${verify.link}")
	private String link;

	@Value(value = "${login.link}")
	private String loginLink;

	@Value(value = "${set.pharma.password.link}")
	private String pharmaPasswordLink;

	@Value(value = "${reset.password.link}")
	private String RESET_PASSWORD_LINK;

	@Value(value = "${web.link}")
	private String RESET_PASSWORD_WEB_LINK;

	@Value(value = "${image.path}")
	private String imagePath;

	@Value(value = "${contact.us.email}")
	private String contactUsEmail;

	@Value(value = "${fb.link}")
	private String fbLink;

	@Value(value = "${twitter.link}")
	private String twitterLink;

	@Value(value = "${linkedIn.link}")
	private String linkedInLink;

	@Value(value = "${googlePlus.link}")
	private String googlePlusLink;

	@Value(value = "${set.password.link}")
	private String setPasswordLink;

	@Value(value = "${welcome.link}")
	private String welcomeLink;

	@Value(value = "${conference.welcome.link}")
	private String conferenceWelcomeLink;

	@Value(value = "${set.password.link}")
	private String setconferencePasswordLink;

	@Value(value = "${verify.link}")
	private String conferencelink;

	@Value(value = "${reset.password.link}")
	private String resetconferencePasswordLink;

	@Value(value = "${superstar.activate.school.link}")
	private String superStarActivateSchoolLink;

	@Value(value = "${superstar.activate.branch.link}")
	private String superStarActivateBranchLink;

	@Value(value = "${set.safeplace.password.link}")
	private String setSafePasswordLink;

	@Override
	@Transactional
	public String generateActivationEmailBody(String fName, String tokenId, String templatePath, String doctorName,
			String clinicName) throws Exception {

		VelocityContext context = new VelocityContext();
		context.put("fName", fName);
		context.put("doctorName", doctorName);
		context.put("clinicName", clinicName);
		// context.put("link", link + "/" + tokenId);?uid=
		context.put("link", link + "?uid=" + tokenId);
		context.put("loginLink", loginLink);
		context.put("imageURL", imagePath + "templatesImage");
		context.put("contactUsEmail", contactUsEmail);
		context.put("fbLink", fbLink);
		context.put("twitterLink", twitterLink);
		context.put("linkedInLink", linkedInLink);
		context.put("googlePlusLink", googlePlusLink);
		context.put("setPasswordLink", setPasswordLink + "?uid=" + tokenId);

		String text = mergeTemplate(context, templatePath);
		return text;
	}

	@Override
	@Transactional
	public String generateActivationEmailBodyForPharmaCompany(String fName, String tokenId, String templatePath,
			String doctorName, String clinicName) throws Exception {

		VelocityContext context = new VelocityContext();
		context.put("fName", fName);
		context.put("doctorName", doctorName);
		context.put("clinicName", clinicName);
		context.put("link", pharmaPasswordLink + "/" + tokenId);
		context.put("loginLink", loginLink);
		context.put("imageURL", imagePath + "templatesImage");
		context.put("contactUsEmail", contactUsEmail);
		context.put("fbLink", fbLink);
		context.put("twitterLink", twitterLink);
		context.put("linkedInLink", linkedInLink);
		context.put("googlePlusLink", googlePlusLink);
		context.put("setPasswordLink", pharmaPasswordLink + "?uid=" + tokenId);

		String text = mergeTemplate(context, templatePath);
		return text;
	}

	@Override
	@Transactional
	public String generateForgotPasswordEmailBody(String fName, String tokenId) {
		VelocityContext context = new VelocityContext();
		context.put("fName", fName);
		context.put("link", RESET_PASSWORD_LINK + "?uid=" + tokenId);
		context.put("imageURL", imagePath + "templatesImage");
		context.put("contactUsEmail", contactUsEmail);
		context.put("fbLink", fbLink);
		context.put("twitterLink", twitterLink);
		context.put("linkedInLink", linkedInLink);
		context.put("googlePlusLink", googlePlusLink);

		String text = mergeTemplate(context, "forgotPasswordTemplate.vm");
		return text;
	}

	@Override
	@Transactional
	public String generateForgotUsernameEmailBody(List<UserCollection> userCollection) {
		StringBuffer body = new StringBuffer();
		body.append("Hi, \n Below are your usernames \n");
		for (UserCollection user : userCollection) {
			body.append(" - " + user.getUserName() + "\n");
		}
		return body.toString();
	}

	@Override
	@Transactional
	public String generateIssueTrackEmailBody(String userName, String firstName, String middleName, String lastName) {
		VelocityContext context = new VelocityContext();
		context.put("fName", firstName);
		context.put("link", RESET_PASSWORD_WEB_LINK);
		context.put("imageURL", imagePath + "templatesImage");
		context.put("contactUsEmail", contactUsEmail);
		context.put("fbLink", fbLink);
		context.put("twitterLink", twitterLink);
		context.put("linkedInLink", linkedInLink);
		context.put("googlePlusLink", googlePlusLink);

		String text = mergeTemplate(context, "addIssueTemplate.vm");
		return text;
	}

	@Override
	@Transactional
	public String generateResetPasswordSuccessEmailBody(String firstName) {
		VelocityContext context = new VelocityContext();
		context.put("fName", firstName);
		context.put("link", RESET_PASSWORD_WEB_LINK);
		context.put("imageURL", imagePath + "templatesImage");
		context.put("contactUsEmail", contactUsEmail);
		context.put("fbLink", fbLink);
		context.put("twitterLink", twitterLink);
		context.put("linkedInLink", linkedInLink);
		context.put("googlePlusLink", googlePlusLink);

		String text = mergeTemplate(context, "resetPasswordSuccess.vm");
		return text;
	}

	@Override
	@Transactional
	public String generateRecordsShareOtpBeforeVerificationEmailBody(String emailAddress, String firstName,
			String doctorName) {
		VelocityContext context = new VelocityContext();
		context.put("fName", firstName);
		context.put("doctorName", doctorName);
		context.put("imageURL", imagePath + "templatesImage");
		context.put("contactUsEmail", contactUsEmail);
		context.put("fbLink", fbLink);
		context.put("twitterLink", twitterLink);
		context.put("linkedInLink", linkedInLink);
		context.put("googlePlusLink", googlePlusLink);

		String text = mergeTemplate(context, "recordShareOtpBeforeVerificationTemplate.vm");
		return text;
	}

	@Override
	@Transactional
	public String generateRecordsShareOtpAfterVerificationEmailBody(String emailAddress, String firstName,
			String doctorName) {
		VelocityContext context = new VelocityContext();
		context.put("fName", firstName);
		context.put("doctorName", doctorName);
		context.put("imageURL", imagePath + "templatesImage");
		context.put("contactUsEmail", contactUsEmail);
		context.put("fbLink", fbLink);
		context.put("twitterLink", twitterLink);
		context.put("linkedInLink", linkedInLink);
		context.put("googlePlusLink", googlePlusLink);

		String text = mergeTemplate(context, "recordShareOtpAfterVerificationTemplate");
		return text;
	}

	@Override
	@Transactional
	public String generateAppointmentEmailBody(String doctorName, String patientName, String dateTime,
			String clinicName, String templatePath) {
		VelocityContext context = new VelocityContext();
		context.put("doctorName", doctorName);
		context.put("patientName", patientName);
		context.put("dateTime", dateTime);
		context.put("clinicName", clinicName);
		context.put("imageURL", imagePath + "templatesImage");
		context.put("contactUsEmail", contactUsEmail);
		context.put("fbLink", fbLink);
		context.put("twitterLink", twitterLink);
		context.put("linkedInLink", linkedInLink);
		context.put("googlePlusLink", googlePlusLink);

		String text = mergeTemplate(context, templatePath);
		return text;
	}

	@Override
	@Transactional
	public String generateEmailBody(String userName, String resumeType, String templatePath) throws Exception {

		VelocityContext context = new VelocityContext();
		context.put("fName", userName);
		context.put("resumeType", resumeType);
		context.put("imageURL", imagePath + "templatesImage");
		context.put("contactUsEmail", contactUsEmail);
		context.put("fbLink", fbLink);
		context.put("twitterLink", twitterLink);
		context.put("linkedInLink", linkedInLink);
		context.put("googlePlusLink", googlePlusLink);

		String text = mergeTemplate(context, templatePath);
		return text;
	}

	@Override
	public String generateEMREmailBody(String patientName, String doctorName, String clinicName, String clinicAddress,
			String mailRecordCreatedDate, String medicalRecordType, String templatePath) {
		VelocityContext context = new VelocityContext();
		context.put("fName", patientName);
		context.put("doctorName", doctorName);
		context.put("clinicName", clinicName);
		context.put("clinicAddress", clinicAddress);
		context.put("mailRecordCreatedDate", mailRecordCreatedDate);
		context.put("medicalRecordType", medicalRecordType);
		context.put("imageURL", imagePath + "templatesImage");
		context.put("contactUsEmail", contactUsEmail);
		context.put("fbLink", fbLink);
		context.put("twitterLink", twitterLink);
		context.put("linkedInLink", linkedInLink);
		context.put("googlePlusLink", googlePlusLink);

		String text = mergeTemplate(context, templatePath);
		return text;
	}

	@Override
	public String generateFeedbackEmailBody(String patientName, String doctorName, String clinicName,
			String uniqueFeedbackId, String templatePath) {
		VelocityContext context = new VelocityContext();
		context.put("fName", patientName);
		context.put("doctorName", doctorName);
		context.put("clinicName", clinicName);
		context.put("uniqueFeedbackId", uniqueFeedbackId);
		context.put("imageURL", imagePath + "templatesImage");
		context.put("contactUsEmail", contactUsEmail);
		context.put("fbLink", fbLink);
		context.put("twitterLink", twitterLink);
		context.put("linkedInLink", linkedInLink);
		context.put("googlePlusLink", googlePlusLink);

		String text = mergeTemplate(context, templatePath);
		return text;

	}

	@Override
	public String generateAppLinkEmailBody(String appType, String bitLink, String templatePath) {
		VelocityContext context = new VelocityContext();
		context.put("appType", appType);
		context.put("bitLink", bitLink);
		context.put("imageURL", imagePath + "templatesImage");
		context.put("contactUsEmail", contactUsEmail);
		context.put("fbLink", fbLink);
		context.put("twitterLink", twitterLink);
		context.put("linkedInLink", linkedInLink);
		context.put("googlePlusLink", googlePlusLink);

		String text = mergeTemplate(context, templatePath);
		return text;

	}

	@Override
	public String generateAppLinkEmailBody(String appType, String bitLink, String appDeviceType, String templatePath) {
		VelocityContext context = new VelocityContext();
		context.put("appDeviceType", appDeviceType);
		context.put("appType", appType);
		context.put("bitLink", bitLink);
		context.put("imageURL", imagePath + "templatesImage");
		context.put("contactUsEmail", contactUsEmail);
		context.put("fbLink", fbLink);
		context.put("twitterLink", twitterLink);
		context.put("linkedInLink", linkedInLink);
		context.put("googlePlusLink", googlePlusLink);

		String text = mergeTemplate(context, templatePath);
		return text;

	}

	@Override
	@Transactional
	public String doctorWelcomeEmailBody(String fName, String tokenId, String templatePath, String doctorName,
			String clinicName) throws Exception {

		VelocityContext context = new VelocityContext();
		context.put("fName", fName);
		context.put("doctorName", doctorName);
		context.put("clinicName", clinicName);
		context.put("link", welcomeLink + "/" + tokenId);
		context.put("loginLink", loginLink);
		context.put("imageURL", imagePath + "templatesImage");
		context.put("contactUsEmail", contactUsEmail);
		context.put("fbLink", fbLink);
		context.put("twitterLink", twitterLink);
		context.put("linkedInLink", linkedInLink);
		context.put("googlePlusLink", googlePlusLink);
		// context.put("setPasswordLink", setPasswordLink + "?uid=" + tokenId);

		String text = mergeTemplate(context, templatePath);
		return text;
	}

	@Override
	@Transactional
	public String conferenceEmailBody(String fName, String link, String templatePath) {

		VelocityContext context = new VelocityContext();
		context.put("fName", fName);
		context.put("serverUrl", link);

		String text = mergeTemplate(context, templatePath);
		return text;
	}

	@Override
	@Transactional
	public String superStartActivateAccount(String fName, String link, String templatePath, String type) {

		VelocityContext context = new VelocityContext();

		context.put("fName", fName);

		if (type.equalsIgnoreCase("School")) {
			context.put("link", superStarActivateSchoolLink + "/" + link);
		}

		if (type.equalsIgnoreCase("Branch")) {
			context.put("link", superStarActivateSchoolLink + "/" + link);
		}

		String text = mergeTemplate(context, templatePath);

		return text;
	}

	private String mergeTemplate(VelocityContext context, String templatePath) {
		VelocityEngine velocityEngine = new VelocityEngine();
		velocityEngine.setProperty("input.encoding", "UTF-8");
		velocityEngine.setProperty("output.encoding", "UTF-8");
		velocityEngine.setProperty(RuntimeConstants.RUNTIME_LOG_LOGSYSTEM_CLASS,
				"org.apache.velocity.runtime.log.Log4JLogChute");
		velocityEngine.setProperty("runtime.log", "/var/log/dpdocter/velocity.log");
		velocityEngine.setProperty("resource.loader", "class, file");
		velocityEngine.setProperty("class.resource.loader.class",
				"org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
		velocityEngine.setProperty("file.resource.loader.class",
				"org.apache.velocity.runtime.resource.loader.FileResourceLoader");
		velocityEngine.setProperty("file.resource.loader.path", "/opt/tomcat/latest/webapps/dpdocter/WEB-INF/classes");
		velocityEngine.setProperty("class.resource.loader.cache", "false");
		velocityEngine.setProperty("file.resource.loader.cache", "true");
		velocityEngine.init();

		Template template = velocityEngine.getTemplate(templatePath);

		StringWriter stringWriter = new StringWriter();

		template.setEncoding("UTF-8");
		template.merge(context, stringWriter);
		String text = stringWriter.toString();

		return text;
	}

	@Override
	@Transactional
	public String generateAdminActivationEmailBody(String fName, String userName, String tokenId, String templatePath) {

		VelocityContext context = new VelocityContext();
		context.put("fName", fName);
		context.put("userName", userName);
		context.put("setSafePasswordLink", setSafePasswordLink + "?uid=" + tokenId);
		context.put("loginLink", loginLink);
		context.put("imageURL", imagePath + "templatesImage");
		context.put("contactUsEmail", contactUsEmail);
		context.put("fbLink", fbLink);
		context.put("twitterLink", twitterLink);
		context.put("linkedInLink", linkedInLink);
		context.put("googlePlusLink", googlePlusLink);

		String text = mergeTemplate(context, templatePath);
		return text;
	}

	@Override
	@Transactional
	public String subscriptionEmailBody(String title,String fName,String isValue,String noOfDays,String packageName, String templatePath) {
		
		VelocityContext context = new VelocityContext();
		context.put("title", title);
		context.put("fName", fName);
		context.put("noOfDays", noOfDays);
		context.put("value", isValue);
		context.put("packageName", packageName);		
		
		String text = mergeTemplate(context, templatePath);
		return text;
	}
	
	@Override
	@Transactional
	public String subscriptionPaymentEmailBody(String title,String fName,String noOfDays,
			String packageName,String paymentMode,String totalCost, 
			String templatePath) {
		
		VelocityContext context = new VelocityContext();
		context.put("title", title);
		context.put("fName", fName);
		context.put("noOfDays", noOfDays);
		context.put("packageName", packageName);		
		context.put("paymentMode", paymentMode);				
		context.put("totalCost", totalCost);		

		String text = mergeTemplate(context, templatePath);
		return text;
	}
	
	@Override
	@Transactional
	public String generateAppointmentEmailBody(String doctorName, String patientName, String dateTime,
			String clinicName, String templatePath, String branch) {
		
		if(branch == null)
			branch =  "";
		
		VelocityContext context = new VelocityContext();
		context.put("doctorName", doctorName);
		context.put("patientName", patientName);
		context.put("branch", branch);
		context.put("dateTime", dateTime);
		context.put("clinicName", clinicName);
		context.put("imageURL", imagePath + "templatesImage");
		context.put("contactUsEmail", contactUsEmail);
		context.put("fbLink", fbLink);
		context.put("twitterLink", twitterLink);
		context.put("linkedInLink", linkedInLink);
		context.put("googlePlusLink", googlePlusLink);
		
		String text = mergeTemplate(context, templatePath);
		return text;
	}
	
	@Override
	public String generateOnlineAppointmentEmailBody(String doctorName, String patientName, String dateTime,
			String clinicName, String templatePath, String branch, String consultationType) {
		VelocityContext context = new VelocityContext();
		context.put("doctorName", doctorName);
		context.put("patientName", patientName);
		context.put("dateTime", dateTime);
		context.put("clinicName", clinicName);
		context.put("consultationType", consultationType);
		context.put("imageURL", imagePath + "templatesImage");
		context.put("contactUsEmail", contactUsEmail);
		context.put("fbLink", fbLink);
		context.put("twitterLink", twitterLink);
		context.put("linkedInLink", linkedInLink);
		context.put("googlePlusLink", googlePlusLink);
		
		String text = mergeTemplate(context, templatePath);
		return text;
	}

	@Override
	public String followUpPatientEmailBody(String emailBody, String templatePath) {
		VelocityContext context = new VelocityContext();
		context.put("emailBody", emailBody);
		String text = mergeTemplate(context, templatePath);
		return text;
	}

	@Override
	public String generateSmilebirdAppointmentEmailBody(String emailBody, String templatePath) {
		VelocityContext context = new VelocityContext();
		context.put("emailBody", emailBody);
		context.put("imageURL", imagePath + "templatesImage");
		context.put("contactUsEmail", contactUsEmail);
		context.put("fbLink", fbLink);
		context.put("twitterLink", twitterLink);
		context.put("linkedInLink", linkedInLink);
		context.put("googlePlusLink", googlePlusLink);

		String text = mergeTemplate(context, templatePath);
		return text;
	}

}
