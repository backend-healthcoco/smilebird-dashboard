package com.dpdocter.tests;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;

import org.bson.BasicBSONObject;
import org.bson.Document;

import com.dpdocter.beans.CustomAggregationOperation;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.mongodb.BasicDBObject;



public class GeneralTests {
	
//    public static boolean backupDataWithOutDatabase(String dumpExePath, String host, String port, String database, String backupPath) {
//    	boolean status = false;
//    	try {
//    	Process p = null;
//    	 
//    	DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
//    	Date date = new Date();
//    	String filepath = "backup-" + database + "-(" + dateFormat.format(date) + ").bson";
//    	 
//    	String batchCommand = "";
//    	
//    	batchCommand = dumpExePath + " -h " + host + " --port " + port + " -d "+database + " -o \"" + backupPath + "" + filepath + "\"";
//    	
//    	 System.out.println(batchCommand);
//    	Runtime runtime = Runtime.getRuntime();
//    	p = runtime.exec(batchCommand);
//    	int processComplete = p.waitFor();
//    	 
//    	if (processComplete == 0) {
//    	status = true;
////    	log.info("Backup created successfully for without DB " + database + " in " + host + ":" + port);
//    	} else {
//    	status = false;
////    	log.info("Could not create the backup for without DB " + database + " in " + host + ":" + port);
//    	}
//    	 
//    	} catch (IOException ioe) {
////    	log.error(ioe, ioe.getCause());
//    	} catch (Exception e) {
////    	log.error(e, e.getCause());
//    	}
//    	return status;
//    	}
	
	    	private static String bucketName     = "healthcoco";
	    	private static String keyName        = "records/circle.jpg";
	    	private static String uploadFileName = "/home/suresh/Pictures/circle.jpg";
	    	
//	    	public static void main(String[] args) throws IOException {
//	            AmazonS3 s3client = new AmazonS3Client(credentials);
//	            try {
//	                System.out.println("Uploading a new object to S3 from a file\n");
//	                File file = new File(uploadFileName);
//	                s3client.putObject(new PutObjectRequest(bucketName, keyName, file));
//
//	             } catch (AmazonServiceException ase) {
//	                System.out.println("Caught an AmazonServiceException, which " +
//	                		"means your request made it " +
//	                        "to Amazon S3, but was rejected with an error response" +
//	                        " for some reason.");
//	                System.out.println("Error Message:    " + ase.getMessage());
//	                System.out.println("HTTP Status Code: " + ase.getStatusCode());
//	                System.out.println("AWS Error Code:   " + ase.getErrorCode());
//	                System.out.println("Error Type:       " + ase.getErrorType());
//	                System.out.println("Request ID:       " + ase.getRequestId());
//	            } catch (AmazonClientException ace) {
//	                System.out.println("Caught an AmazonClientException, which " +
//	                		"means the client encountered " +
//	                        "an internal error while trying to " +
//	                        "communicate with S3, " +
//	                        "such as not being able to access the network.");
//	                System.out.println("Error Message: " + ace.getMessage());
//        }
//	    	}
	    		 
	    		 public static void main(String[] args) throws NoSuchAlgorithmException, JsonGenerationException, JsonMappingException, IOException, ParseException  {
	    			 new CustomAggregationOperation(new Document("$elemMatch",	new BasicDBObject("$userState", 
	    					 new BasicDBObject("$ne","ADMIN"))));
	    			
	    			 System.out.println(new BasicBSONObject("totalNoOfRecords",new BasicBSONObject("$size", "$records")).toString());
	    		 }
	    		 
}  	
