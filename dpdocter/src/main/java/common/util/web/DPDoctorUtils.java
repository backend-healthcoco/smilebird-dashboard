package common.util.web;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Random;
import java.util.TimeZone;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.jcajce.provider.digest.SHA3.DigestSHA3;
import org.bson.types.ObjectId;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;

import com.dpdocter.beans.DOB;
import com.dpdocter.beans.URLShortnerResponse;
import com.dpdocter.enums.Resource;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;

public class DPDoctorUtils {

	@Context
	private UriInfo uriInfo;

	@Value(value = "${OTP_VALIDATION_TIME_DIFFERENCE}")
	private String otpTimeDifference;

	public static boolean anyStringEmpty(String... values) {
		boolean result = false;
		for (String value : values) {
			if (StringUtils.isEmpty(value)) {
				result = true;
				break;
			}
		}
		return result;
	}

	public static Date parseDate(String dateStr) {
		// Example: "2024-07-01"
		try {
			return new SimpleDateFormat("yyyy-MM-dd").parse(dateStr);
		} catch (ParseException e) {
			return null;
		}
	}

	public static boolean allStringsEmpty(String... values) {
		boolean result = true;
		for (String value : values) {
			if (!StringUtils.isEmpty(value)) {
				result = false;
				break;
			}
		}
		return result;
	}

	public static boolean anyStringEmpty(ObjectId... values) {
		boolean result = false;
		for (ObjectId value : values) {
			if (value == null) {
				result = true;
				break;
			}
		}
		return result;
	}

	public static boolean allStringsEmpty(ObjectId... values) {
		boolean result = true;
		for (ObjectId value : values) {
			if (value != null) {
				result = false;
				break;
			}
		}
		return result;
	}

	public static String getPrefixedNumber(int number) {
		String result = String.valueOf(number);
		if (number < 10) {
			result = "0" + result;
		}
		return result;
	}

	public static String formatAsSolrDate(Date date) {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		df.setTimeZone(TimeZone.getTimeZone("UTC"));
		String solrDate = df.format(date);
		return solrDate;
	}

	public static char[] getSHA3SecurePassword(char[] password) throws UnsupportedEncodingException {
		DigestSHA3 md = new DigestSHA3(256);
		byte[] buffer = new byte[password.length];
		for (int i = 0; i < buffer.length; i++) {
			buffer[i] = (byte) password[i];
		}
		md.update(buffer);
		byte[] digest = md.digest();

		BigInteger bigInt = new BigInteger(1, digest);
		return bigInt.toString(16).toCharArray();
	}

	public static String generateRandomId() {
		char[] chars = "ABSDEFGHIJKLMNOPQRSTUVWXYZ1234567890".toCharArray();
		Random r = new Random(System.currentTimeMillis());
		char[] id = new char[8];
		for (int i = 0; i < 8; i++) {
			id[i] = chars[r.nextInt(chars.length)];
		}
		return new String(id);
	}

	public static double distance(double lat1, double lon1, double lat2, double lon2, String string) {
		double theta = lon1 - lon2;
		double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2))
				+ Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
		dist = Math.acos(dist);
		dist = rad2deg(dist);
		dist = dist * 60 * 1.1515;
		if (string == "K") {
			dist = dist * 1.609344;
		} else if (string == "N") {
			dist = dist * 0.8684;
		}
		return (dist);
	}

	public static double deg2rad(double deg) {
		return (deg * Math.PI / 180.0);
	}

	public static double rad2deg(double rad) {
		return (rad * 180.0 / Math.PI);
	}

	public static char[] generateSalt() throws NoSuchAlgorithmException, NoSuchProviderException {
		SecureRandom sr = SecureRandom.getInstance("SHA1PRNG", "SUN");
		byte[] salt = new byte[16];
		sr.nextBytes(salt);
		char[] result = new char[salt.length];
		for (int i = 0; i < result.length; i++) {
			result[i] = (char) salt[i];
		}
		return result;
	}

	public static long getAgeInDaysFromDOB(DOB dob) {
		LocalDate now = LocalDate.now();
		LocalDate birthDate = LocalDate.of(dob.getYears(), dob.getMonths(), dob.getDays());

		long days = ChronoUnit.DAYS.between(birthDate, now);
		return days;
	}

	public static SearchQuery createGlobalQuery(Resource resource, int page, int size, String updatedTime,
			Boolean discarded, String sortBy, String searchTerm, Collection<String> specialities,
			String... searchTermFieldName) {
		BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder()
				.must(QueryBuilders.rangeQuery("updatedTime").from(Long.parseLong(updatedTime)))
				.mustNot(QueryBuilders.existsQuery("doctorId")).mustNot(QueryBuilders.existsQuery("locationId"))
				.mustNot(QueryBuilders.existsQuery("hospitalId"));

		if (!DPDoctorUtils.anyStringEmpty(searchTerm) && searchTermFieldName.length > 0) {
			if (searchTermFieldName.length == 1)
				boolQueryBuilder.must(QueryBuilders.matchPhrasePrefixQuery(searchTermFieldName[0], searchTerm));
			else
				boolQueryBuilder.must(QueryBuilders.multiMatchQuery(searchTerm, searchTermFieldName));
		}
		if (!discarded)
			boolQueryBuilder.must(QueryBuilders.termQuery("discarded", discarded));

		if (resource.equals(Resource.COMPLAINT) || resource.equals(Resource.OBSERVATION)
				|| resource.equals(Resource.INVESTIGATION) || resource.equals(Resource.DIAGNOSIS)
				|| resource.equals(Resource.NOTES)) {
			boolQueryBuilder.must(QueryBuilders.termsQuery("speciality", specialities));
		}
		SearchQuery searchQuery = null;
		if (anyStringEmpty(sortBy)) {
			if (size > 0)
				searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
						.withPageable(PageRequest.of(page, size, Direction.DESC, "updatedTime")).build();
			else
				searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
						.withSort(SortBuilders.fieldSort("updatedTime").order(SortOrder.DESC)).build();
		} else {
			if (size > 0)
				searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
						.withPageable(PageRequest.of(page, size, Direction.ASC, sortBy)).build();
			else
				searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
						.withSort(SortBuilders.fieldSort(sortBy).order(SortOrder.ASC)).build();
		}

		return searchQuery;
	}

	public static SearchQuery createCustomQuery(int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded, String sortBy, String searchTerm,
			String... searchTermFieldName) {

		BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder()
				.must(QueryBuilders.rangeQuery("updatedTime").from(Long.parseLong(updatedTime)))
				.must(QueryBuilders.termQuery("doctorId", doctorId));

		if (!DPDoctorUtils.anyStringEmpty(locationId, hospitalId))
			boolQueryBuilder.must(QueryBuilders.termQuery("locationId", locationId))
					.must(QueryBuilders.termQuery("hospitalId", hospitalId));
		if (!DPDoctorUtils.anyStringEmpty(searchTerm) && searchTermFieldName.length > 0) {
			if (searchTermFieldName.length == 1)
				boolQueryBuilder.must(QueryBuilders.matchPhrasePrefixQuery(searchTermFieldName[0], searchTerm));
			else
				boolQueryBuilder.must(QueryBuilders.multiMatchQuery(searchTerm, searchTermFieldName));
		}
		if (!discarded)
			boolQueryBuilder.must(QueryBuilders.termQuery("discarded", discarded));

		SearchQuery searchQuery = null;
		if (anyStringEmpty(sortBy)) {
			if (size > 0)
				searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
						.withPageable(PageRequest.of(page, size, Direction.DESC, "updatedTime")).build();
			else
				searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
						.withSort(SortBuilders.fieldSort("updatedTime").order(SortOrder.DESC)).build();
		} else {
			if (size > 0)
				searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
						.withPageable(PageRequest.of(page, size, Direction.ASC, sortBy)).build();
			else
				searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
						.withSort(SortBuilders.fieldSort(sortBy).order(SortOrder.ASC)).build();
		}

		return searchQuery;
	}

	@SuppressWarnings("deprecation")
	public static SearchQuery createCustomGlobalQuery(Resource resource, int page, int size, String doctorId,
			String locationId, String hospitalId, String updatedTime, Boolean discarded, String sortBy,
			String searchTerm, Collection<String> specialities, String... searchTermFieldName) {

		BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder()
				.must(QueryBuilders.rangeQuery("updatedTime").from(Long.parseLong(updatedTime)));

		if (!DPDoctorUtils.anyStringEmpty(doctorId))
			boolQueryBuilder.must(QueryBuilders.boolQuery()
					.should(QueryBuilders.boolQuery().mustNot(QueryBuilders.existsQuery("doctorId")))
					.should(QueryBuilders.termQuery("doctorId", doctorId)).minimumShouldMatch(1));

		if (!DPDoctorUtils.anyStringEmpty(locationId, hospitalId)) {
			boolQueryBuilder
					.must(QueryBuilders.boolQuery()
							.should(QueryBuilders.boolQuery().mustNot(QueryBuilders.existsQuery("locationId")))
							.should(QueryBuilders.termQuery("locationId", locationId)).minimumShouldMatch(1))
					.must(QueryBuilders.boolQuery()
							.should(QueryBuilders.boolQuery().mustNot(QueryBuilders.existsQuery("hospitalId")))
							.should(QueryBuilders.termQuery("hospitalId", hospitalId)).minimumShouldMatch(1));
		}

		if (!DPDoctorUtils.anyStringEmpty(searchTerm) && searchTermFieldName.length > 0) {
			if (searchTermFieldName.length == 1)
				boolQueryBuilder.must(QueryBuilders.matchPhrasePrefixQuery(searchTermFieldName[0], searchTerm));
			else
				boolQueryBuilder.must(QueryBuilders.multiMatchQuery(searchTerm, searchTermFieldName));
		}
		if (!discarded)
			boolQueryBuilder.must(QueryBuilders.termQuery("discarded", discarded));

		if (resource.equals(Resource.COMPLAINT) || resource.equals(Resource.OBSERVATION)
				|| resource.equals(Resource.INVESTIGATION) || resource.equals(Resource.DIAGNOSIS)
				|| resource.equals(Resource.NOTES)) {
			if (specialities != null && !specialities.isEmpty())
				boolQueryBuilder.must(QueryBuilders.termsQuery("speciality", specialities));
		}
		SearchQuery searchQuery = null;
		if (anyStringEmpty(sortBy)) {
			if (size > 0)
				searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
						.withPageable(PageRequest.of(page, size, Direction.DESC, "updatedTime")).build();
			else
				searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
						.withSort(SortBuilders.fieldSort("updatedTime").order(SortOrder.DESC)).build();
		} else {
			if (size > 0)
				searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
						.withPageable(PageRequest.of(page, size, Direction.ASC, sortBy)).build();
			else
				searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
						.withSort(SortBuilders.fieldSort(sortBy).order(SortOrder.ASC)).build();
		}
		return searchQuery;
	}

	public static Aggregation createGlobalAggregation(int page, int size, String updatedTime, Boolean discarded,
			String sortBy, String searchTerm, Collection<String> specialities, String... searchTermFieldName) {

		long createdTimeStamp = Long.parseLong(updatedTime);

		Criteria criteria = new Criteria("updatedTime").gte(new Date(createdTimeStamp)).and("doctorId").is(null)
				.and("locationId").is(null).and("hospitalId").is(null);
		if (specialities != null && !specialities.isEmpty())
			criteria.and("speciality").in(specialities);
		if (!discarded)
			criteria.and("discarded").is(discarded);
		Aggregation aggregation = null;
		if (anyStringEmpty(sortBy)) {
			if (size > 0)
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")),
						Aggregation.skip((long) (page) * size), Aggregation.limit(size));
			else
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")));
		} else {
			if (size > 0)
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.ASC, sortBy)), Aggregation.skip((long) (page) * size),
						Aggregation.limit(size));
			else
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.ASC, sortBy)));
		}

		return aggregation;
	}

	public static Aggregation createCustomAggregation(int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded, String sortBy, String searchTerm,
			String... searchTermFieldName) {

		long createdTimeStamp = Long.parseLong(updatedTime);

		Criteria criteria = new Criteria("updatedTime").gte(new Date(createdTimeStamp));
		if (!discarded)
			criteria.and("discarded").is(discarded);
		if (!DPDoctorUtils.anyStringEmpty(doctorId))
			criteria.and("doctorId").is(new ObjectId(doctorId));
		if (!DPDoctorUtils.anyStringEmpty(locationId))
			criteria.and("locationId").is(new ObjectId(locationId));
		if (!DPDoctorUtils.anyStringEmpty(hospitalId))
			criteria.and("hospitalId").is(new ObjectId(hospitalId));

		Aggregation aggregation = null;
		if (anyStringEmpty(sortBy)) {
			if (size > 0)
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")),
						Aggregation.skip((long) (page) * size), Aggregation.limit(size));
			else
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")));
		} else {
			if (size > 0)
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.ASC, sortBy)), Aggregation.skip((long) (page) * size),
						Aggregation.limit(size));
			else
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.ASC, sortBy)));
		}

		return aggregation;
	}

	public static Aggregation createCustomGlobalAggregation(int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded, String sortBy, String searchTerm,
			Collection<String> specialities, String... searchTermFieldName) {

		long createdTimeStamp = Long.parseLong(updatedTime);

		Criteria criteria = new Criteria("updatedTime").gte(new Date(createdTimeStamp));
		if (!discarded)
			criteria.and("discarded").is(discarded);
		if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
			if (!DPDoctorUtils.anyStringEmpty(locationId, hospitalId)) {
				criteria.orOperator(
						new Criteria("doctorId").is(new ObjectId(doctorId)).and("locationId")
								.is(new ObjectId(locationId)).and("hospitalId").is(new ObjectId(hospitalId)),
						new Criteria("doctorId").is(null).and("locationId").is(null).and("hospitalId").is(null));
			} else {
				criteria.orOperator(new Criteria("doctorId").is(new ObjectId(doctorId)),
						new Criteria("doctorId").is(null));
			}
		} else
			criteria.and("doctorId").is(null);

		if (specialities != null && !specialities.isEmpty())
			criteria.and("speciality").in(specialities);
		Aggregation aggregation = null;
		if (anyStringEmpty(sortBy)) {
			if (size > 0)
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")),
						Aggregation.skip((long) (page) * size), Aggregation.limit(size));
			else
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")));
		} else {
			if (size > 0)
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.ASC, sortBy)), Aggregation.skip((long) (page) * size),
						Aggregation.limit(size));
			else
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.ASC, sortBy)));
		}

		return aggregation;
	}

	public static Aggregation createGlobalAggregationForAdmin(int page, int size, String updatedTime, Boolean discarded,
			String searchTerm, String searchBy, String speciality, String category, String disease) {

		long createdTimeStamp = Long.parseLong(updatedTime);

		Criteria criteria = new Criteria("updatedTime").gte(new Date(createdTimeStamp)).and("doctorId").is(null)
				.and("locationId").is(null).and("hospitalId").is(null);
		if (!discarded)
			criteria.and("discarded").is(discarded);
		if (!DPDoctorUtils.anyStringEmpty(searchTerm))
			criteria.and(searchBy).regex(searchTerm, "i");
		if (!DPDoctorUtils.anyStringEmpty(speciality))
			criteria.and("speciality").is(speciality);
		if (!DPDoctorUtils.anyStringEmpty(disease))
			criteria.and("disease").regex("^" + disease + "i");
		if (!DPDoctorUtils.anyStringEmpty(category)) {
			criteria.and("categories").is(category);
		}
		Aggregation aggregation = null;
		if (size > 0) {
			aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
					Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")),
					Aggregation.skip((long) (page) * size), Aggregation.limit(size));
		} else {
			aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
					Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")));
		}
		return aggregation;
	}

	public static Aggregation createCustomAggregationForAdmin(int page, int size, String updatedTime, Boolean discarded,
			String searchTerm, String searchBy, String speciality, String category, String disease) {

		long createdTimeStamp = Long.parseLong(updatedTime);

		Criteria criteria = new Criteria("updatedTime").gte(new Date(createdTimeStamp)).and("doctorId").ne(null)
				.and("locationId").ne(null).and("hospitalId").ne(null);
		if (!discarded)
			criteria.and("discarded").is(discarded);
		if (!DPDoctorUtils.anyStringEmpty(searchTerm))
			criteria.and(searchBy).regex(searchTerm, "i");
		if (!DPDoctorUtils.anyStringEmpty(speciality))
			criteria.and("speciality").is(speciality);
		if (!DPDoctorUtils.anyStringEmpty(disease))
			criteria.and("disease").regex("^" + disease + "i");
		if (!DPDoctorUtils.anyStringEmpty(category)) {
			criteria.and("categories").is(category);
		}
		Aggregation aggregation = null;
		if (size > 0) {
			aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
					Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")),
					Aggregation.skip((long) (page) * size), Aggregation.limit(size));
		} else {
			aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
					Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")));
		}
		return aggregation;
	}

	public static Aggregation createCustomGlobalAggregationForAdmin(int page, int size, String updatedTime,
			Boolean discarded, String searchTerm, String searchBy, String speciality, String category, String disease) {

		long createdTimeStamp = Long.parseLong(updatedTime);

		Criteria criteria = new Criteria("updatedTime").gte(new Date(createdTimeStamp));
		if (!discarded)
			criteria.and("discarded").is(discarded);
		if (!DPDoctorUtils.anyStringEmpty(searchTerm))
			criteria.and(searchBy).regex(searchTerm, "i");
		if (!DPDoctorUtils.anyStringEmpty(speciality))
			criteria.and("speciality").is(speciality);
		if (!DPDoctorUtils.anyStringEmpty(disease))
			criteria.and("disease").regex("^" + disease + "i");
		if (!DPDoctorUtils.anyStringEmpty(category)) {
			criteria.and("categories").is(category);
		}
		Aggregation aggregation = null;
		if (size > 0) {
			aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
					Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")),
					Aggregation.skip((long) (page) * size), Aggregation.limit(size));
		} else {
			aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
					Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")));
		}
		return aggregation;
	}

	public static Date addmonth(Date current, int months) {

		System.out.println(current);
		Calendar cal = Calendar.getInstance();
		cal.setTime(current);
		cal.set(Calendar.MONTH, (cal.get(Calendar.MONTH) + months));
		current = cal.getTime();
		return current;

	}

	public static String randomString(int length) {
		final String GENERATOR_STRING = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
		SecureRandom rnd = new SecureRandom();
		StringBuilder sb = new StringBuilder(length);
		for (int i = 0; i < length; i++) {
			sb.append(GENERATOR_STRING.charAt(rnd.nextInt(GENERATOR_STRING.length())));
		}
		return sb.toString();
	}

	public static Date getToTime(Date date) {

		Calendar localCalendar = Calendar.getInstance(TimeZone.getTimeZone("IST"));
		localCalendar.setTime(date);
		int currentDay = localCalendar.get(Calendar.DATE);
		int currentMonth = localCalendar.get(Calendar.MONTH);
		int currentYear = localCalendar.get(Calendar.YEAR);
		localCalendar.set(currentYear, currentMonth, currentDay, 23, 59, 59);
		return localCalendar.getTime();

	}

	public static DateTime getEndTime(Date date) {

		Calendar localCalendar = Calendar.getInstance(TimeZone.getTimeZone("IST"));
		localCalendar.setTime(date);
		int currentDay = localCalendar.get(Calendar.DATE);
		int currentMonth = localCalendar.get(Calendar.MONTH) + 1;
		int currentYear = localCalendar.get(Calendar.YEAR);

		return new DateTime(currentYear, currentMonth, currentDay, 23, 59, 59,
				DateTimeZone.forTimeZone(TimeZone.getTimeZone("IST")));

	}

	public static Date getFormTime(Date date) {

		Calendar localCalendar = Calendar.getInstance(TimeZone.getTimeZone("IST"));
		localCalendar.setTime(date);
		int currentDay = localCalendar.get(Calendar.DATE);
		int currentMonth = localCalendar.get(Calendar.MONTH);
		int currentYear = localCalendar.get(Calendar.YEAR);
		localCalendar.set(currentYear, currentMonth, currentDay, 0, 0, 0);
		return localCalendar.getTime();

	}

	public static String urlShortner(String link) {
		String shortUrl = null;

		try {
			HttpResponse<JsonNode> response = Unirest.post("https://url-shortener-service.p.rapidapi.com/shorten")
					.header("X-RapidAPI-Host", "url-shortener-service.p.rapidapi.com")
					.header("X-RapidAPI-Key", "75cbae716dmsh3e15bc0ab75221ep189f87jsnf7a1406117b4")
					.header("Content-Type", "application/x-www-form-urlencoded").field("url", link).asJson();
			URLShortnerResponse shortnerResponse = JacksonUtil.json2Object(response.getBody().toString(),
					URLShortnerResponse.class);
			// System.out.println(shortnerResponse.getResult_url());
			if (shortnerResponse != null) {
				shortUrl = shortnerResponse.getResult_url();
			} else {
				throw new BusinessException(ServiceError.Unknown, "Something went wrong");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return shortUrl;
	}

	public static Aggregation createGlobalAggregation(int page, int size, String updatedTime, Boolean discarded,
			String ratelistId, String sortBy, String searchTerm, Collection<String> specialities,
			String... searchTermFieldName) {

		long createdTimeStamp = Long.parseLong(updatedTime);

		Criteria criteria = new Criteria("updatedTime").gte(new Date(createdTimeStamp)).and("doctorId").is(null)
				.and("locationId").is(null).and("hospitalId").is(null);
		if (specialities != null && !specialities.isEmpty())
			criteria.and("specialPatientVisitServiceImplity").in(specialities);
		if (!discarded)
			criteria.and("discarded").is(discarded);
		if (!DPDoctorUtils.anyStringEmpty(ratelistId))
			criteria.and("ratelistId").is(new ObjectId(ratelistId));
		Aggregation aggregation = null;
		if (anyStringEmpty(sortBy)) {
			if (size > 0)
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")),
						Aggregation.skip((long) (page) * size), Aggregation.limit(size));
			else
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")));
		} else {
			if (size > 0)
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.ASC, sortBy)), Aggregation.skip((long) (page) * size),
						Aggregation.limit(size));
			else
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.ASC, sortBy)));
		}

		return aggregation;
	}

	public static Aggregation createCustomAggregation(int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded, String sortBy, String ratelistId,
			String searchTerm, String... searchTermFieldName) {

		long createdTimeStamp = Long.parseLong(updatedTime);

		Criteria criteria = new Criteria("updatedTime").gte(new Date(createdTimeStamp));
		if (!discarded)
			criteria.and("discarded").is(discarded);
		if (!DPDoctorUtils.anyStringEmpty(doctorId))
			criteria.and("doctorId").is(new ObjectId(doctorId));
		if (!DPDoctorUtils.anyStringEmpty(locationId))
			criteria.and("locationId").is(new ObjectId(locationId));
		if (!DPDoctorUtils.anyStringEmpty(hospitalId))
			criteria.and("hospitalId").is(new ObjectId(hospitalId));
		if (!DPDoctorUtils.anyStringEmpty(ratelistId))
			criteria.and("ratelistId").is(new ObjectId(ratelistId));
		Aggregation aggregation = null;
		if (anyStringEmpty(sortBy)) {
			if (size > 0)
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")),
						Aggregation.skip((long) (page) * size), Aggregation.limit(size));
			else
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")));
		} else {
			if (size > 0)
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.ASC, sortBy)), Aggregation.skip((long) (page) * size),
						Aggregation.limit(size));
			else
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.ASC, sortBy)));
		}

		return aggregation;
	}

	public static Aggregation createGlobalAggregationForAdmin(int page, int size, String updatedTime, Boolean discarded,
			String searchTerm, String searchBy, String speciality, String category, String disease, String ratelistId) {

		long createdTimeStamp = Long.parseLong(updatedTime);

		Criteria criteria = new Criteria("updatedTime").gte(new Date(createdTimeStamp)).and("doctorId").is(null)
				.and("locationId").is(null).and("hospitalId").is(null);
		if (!discarded)
			criteria.and("discarded").is(discarded);
		if (!DPDoctorUtils.anyStringEmpty(searchTerm))
			criteria.and(searchBy).regex(searchTerm, "i");
		if (!DPDoctorUtils.anyStringEmpty(speciality))
			criteria.and("speciality").is(speciality);
		if (!DPDoctorUtils.anyStringEmpty(disease))
			criteria.and("disease").regex("^" + disease + "i");
		if (!DPDoctorUtils.anyStringEmpty(category)) {
			criteria.and("categories").is(category);
		}
		if (!DPDoctorUtils.anyStringEmpty(ratelistId))
			criteria.and("ratelistId").is(new ObjectId(ratelistId));
		Aggregation aggregation = null;
		if (size > 0) {
			aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
					Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")),
					Aggregation.skip((long) (page) * size), Aggregation.limit(size));
		} else {
			aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
					Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")));
		}
		return aggregation;
	}

	public static Aggregation createCustomAggregationForAdmin(int page, int size, String updatedTime, Boolean discarded,
			String searchTerm, String searchBy, String speciality, String category, String disease, String ratelistId) {

		long createdTimeStamp = Long.parseLong(updatedTime);

		Criteria criteria = new Criteria("updatedTime").gte(new Date(createdTimeStamp)).and("doctorId").ne(null)
				.and("locationId").ne(null).and("hospitalId").ne(null);
		if (!discarded)
			criteria.and("discarded").is(discarded);
		if (!DPDoctorUtils.anyStringEmpty(searchTerm))
			criteria.and(searchBy).regex(searchTerm, "i");
		if (!DPDoctorUtils.anyStringEmpty(speciality))
			criteria.and("speciality").is(speciality);
		if (!DPDoctorUtils.anyStringEmpty(disease))
			criteria.and("disease").regex("^" + disease + "i");
		if (!DPDoctorUtils.anyStringEmpty(category)) {
			criteria.and("categories").is(category);
		}
		if (!DPDoctorUtils.anyStringEmpty(ratelistId))
			criteria.and("ratelistId").is(new ObjectId(ratelistId));
		Aggregation aggregation = null;
		if (size > 0) {
			aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
					Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")),
					Aggregation.skip((long) (page) * size), Aggregation.limit(size));
		} else {
			aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
					Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")));
		}
		return aggregation;
	}

	public static Aggregation createCustomGlobalAggregationForAdmin(int page, int size, String updatedTime,
			Boolean discarded, String searchTerm, String searchBy, String speciality, String category, String disease,
			String ratelistId) {

		long createdTimeStamp = Long.parseLong(updatedTime);

		Criteria criteria = new Criteria("updatedTime").gte(new Date(createdTimeStamp));
		if (!discarded)
			criteria.and("discarded").is(discarded);
		if (!DPDoctorUtils.anyStringEmpty(searchTerm))
			criteria.and(searchBy).regex(searchTerm, "i");
		if (!DPDoctorUtils.anyStringEmpty(speciality))
			criteria.and("speciality").is(speciality);
		if (!DPDoctorUtils.anyStringEmpty(disease))
			criteria.and("disease").regex("^" + disease + "i");
		if (!DPDoctorUtils.anyStringEmpty(category)) {
			criteria.and("categories").is(category);
		}
		if (!DPDoctorUtils.anyStringEmpty(ratelistId))
			criteria.and("ratelistId").is(new ObjectId(ratelistId));
		Aggregation aggregation = null;
		if (size > 0) {
			aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
					Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")),
					Aggregation.skip((long) (page) * size), Aggregation.limit(size));
		} else {
			aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
					Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")));
		}
		return aggregation;
	}
}
