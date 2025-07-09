package com.dpdocter.services.impl;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.types.ObjectId;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.dpdocter.beans.Districts;
import com.dpdocter.beans.NDHMStates;
import com.dpdocter.beans.NdhmFacility;
import com.dpdocter.beans.NdhmOauthResponse;
import com.dpdocter.collections.DoctorClinicProfileCollection;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.repository.DoctorClinicProfileRepository;
import com.dpdocter.services.NdhmServices;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class NdhmServiceImpl implements NdhmServices {

	private static Logger logger = LogManager.getLogger(NdhmServiceImpl.class.getName());

	@Value(value = "${ndhm.clientId}")
	private String NDHM_CLIENTID;

	@Value(value = "${ndhm.clientSecret}")
	private String NDHM_CLIENT_SECRET;

	@Autowired
	private DoctorClinicProfileRepository doctorClinicProfileRepository;

	@Override
	public Boolean registerFacility(NdhmFacility request) {
		Boolean response = null;
		try {

			NdhmOauthResponse oauth = session();

			String url = "https://dev.abdm.gov.in/devservice/v1/bridges/services";
			JSONObject orderRequest = new JSONObject();
			orderRequest.put("name", request.getName());
			orderRequest.put("type", request.getType());
			orderRequest.put("active", request.getActive());
			orderRequest.put("alias", request.getAlias());
			orderRequest.put("id", request.getLocationId());

			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();

			con.setDoOutput(true);

			System.out.println(con.getErrorStream());
			con.setDoInput(true);
			// optional default is POST
			con.setRequestMethod("PUT");

			con.setRequestProperty("Content-Type", "application/json");
			con.setRequestProperty("Accept-Language", "en-US");
			con.setRequestProperty("Authorization", "Bearer " + oauth.getAccessToken());
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(orderRequest.toString());
			System.out.println("Orderrequest:" + orderRequest.toString());
			wr.flush();
			wr.close();
			con.disconnect();
			InputStream in = con.getInputStream();
			// BufferedReader in = new BufferedReader(new
			// InputStreamReader(con.getInputStream()));
			String inputLine;
			System.out.println(con.getErrorStream());
			/* response = new StringBuffer(); */

			int responseCode = con.getResponseCode();
			StringBuffer output = new StringBuffer();
			int c = 0;
			while ((c = in.read()) != -1) {

				output.append((char) c);

			}
			System.out.println("response:" + output.toString());
			// ObjectMapper mapper = new ObjectMapper();
			if (responseCode == 200) {
				response = true;
				DoctorClinicProfileCollection collection = doctorClinicProfileRepository.findByDoctorIdAndLocationId(
						new ObjectId(request.getDoctorId()), new ObjectId(request.getLocationId()));
				if (collection != null) {
					collection.setIsRegisteredNDHMFacility(true);
					collection.setClinicHipId(request.getLocationId());
					doctorClinicProfileRepository.save(collection);
				}
			}
			// mapper.readValue(output.toString(),Strin.class);
			System.out.println("response" + output.toString());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error : " + e.getMessage());
			throw new BusinessException(ServiceError.Unknown, "Error : " + e.getMessage());
		}
		return response;

	}

	public NdhmOauthResponse session() {
		NdhmOauthResponse response = null;
		try {
			String url = "https://dev.abdm.gov.in/gateway/v0.5/sessions";
			JSONObject orderRequest = new JSONObject();
			orderRequest.put("clientId", NDHM_CLIENTID);
			orderRequest.put("clientSecret", NDHM_CLIENT_SECRET);

			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();

			con.setDoOutput(true);

			System.out.println(con.getErrorStream());
			con.setDoInput(true);
			// optional default is POST
			con.setRequestMethod("POST");

			con.setRequestProperty("Content-Type", "application/json");
			// con.setRequestProperty("Authorization", "Basic " + authStringEnc);
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(orderRequest.toString());
			System.out.println("Orderrequest:" + orderRequest.toString());
			wr.flush();
			wr.close();
			con.disconnect();
			InputStream in = con.getInputStream();
			// BufferedReader in = new BufferedReader(new
			// InputStreamReader(con.getInputStream()));
			String inputLine;
			System.out.println(con.getErrorStream());
			/* response = new StringBuffer(); */
			StringBuffer output = new StringBuffer();
			int c = 0;
			while ((c = in.read()) != -1) {

				output.append((char) c);

			}
			System.out.println("response:" + output.toString());
			ObjectMapper mapper = new ObjectMapper();

			response = mapper.readValue(output.toString(), NdhmOauthResponse.class);
			System.out.println("response" + output.toString());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error : " + e.getMessage());
			throw new BusinessException(ServiceError.Unknown, "Error : " + e.getMessage());
		}
		return response;

	}

	@Override
	public List<NDHMStates> getListforStates() {
		List<NDHMStates> response = null;
		try {

			NdhmOauthResponse oauth = session();

			String url = "https://healthidsbx.abdm.gov.in/api/v1/ha/lgd/states";
//			JSONObject orderRequest = new JSONObject();
//			orderRequest.put("otp",otp);
//			orderRequest.put("txnId",  txnId);

			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();

			con.setDoOutput(true);

			System.out.println(con.getErrorStream());
			con.setDoInput(true);
			// optional default is POST
			con.setRequestMethod("GET");

			con.setRequestProperty("Content-Type", "application/json");
			con.setRequestProperty("Accept-Language", "en-US");
			con.setRequestProperty("Authorization", "Bearer " + oauth.getAccessToken());

			int responseCode = con.getResponseCode();
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			/* response = new StringBuffer(); */
			StringBuffer respons = new StringBuffer();
			while ((inputLine = in.readLine()) != null) {

				respons.append(inputLine);

			}
			in.close();
			System.out.println("response:" + respons.toString());
			ObjectMapper mapper = new ObjectMapper();
			String output = respons.toString();

			JavaType type = mapper.getTypeFactory().constructCollectionType(List.class, NDHMStates.class);
			response = mapper.readValue(output, type);

			System.out.println("response" + respons.toString());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error : " + e.getMessage());
			throw new BusinessException(ServiceError.Unknown, "Error : " + e.getMessage());
		}
		return response;

	}

	@Override
	public List<Districts> getListforDistricts(String statecode) {
		List<Districts> response = null;
		try {

			NdhmOauthResponse oauth = session();

			String url = "https://healthidsbx.abdm.gov.in/api/v1/ha/lgd/districts?stateCode=" + statecode;
//			JSONObject orderRequest = new JSONObject();
//			orderRequest.put("otp",otp);
//			orderRequest.put("txnId",  txnId);

			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();

			con.setDoOutput(true);

			System.out.println(con.getErrorStream());
			con.setDoInput(true);
			// optional default is POST
			con.setRequestMethod("GET");

			con.setRequestProperty("Content-Type", "application/json");
			con.setRequestProperty("Accept-Language", "en-US");
			con.setRequestProperty("Authorization", "Bearer" + oauth.getAccessToken());

			int responseCode = con.getResponseCode();
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			/* response = new StringBuffer(); */
			StringBuffer respons = new StringBuffer();
			while ((inputLine = in.readLine()) != null) {

				respons.append(inputLine);

			}
			in.close();
			System.out.println("response:" + respons.toString());
			ObjectMapper mapper = new ObjectMapper();
			String output = respons.toString();

			JavaType type = mapper.getTypeFactory().constructCollectionType(List.class, Districts.class);
			response = mapper.readValue(output, type);
			System.out.println("response" + respons.toString());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error : " + e.getMessage());
			throw new BusinessException(ServiceError.Unknown, "Error : " + e.getMessage());
		}
		return response;

	}

}
