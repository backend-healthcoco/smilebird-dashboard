package com.dpdocter.services;

import java.util.List;

import com.dpdocter.beans.Trending;
import com.dpdocter.response.TrendingResponse;

public interface TrendingService {
	public TrendingResponse addEditTrending(Trending request);

	public TrendingResponse getTrending(String id);

	public TrendingResponse deleteTrending(String id, boolean discarded);

	public List<TrendingResponse> getTrendings(int size, int page, Boolean discarded, String searchTerm,
			String trendingType, String resourceType,String toTime,String fromTime);

	public TrendingResponse updateRank(String id, int rank);

	public Integer countTrendings(Boolean discarded, String searchTerm, String trendingType, String resourceType);
}
