package com.dpdocter.services;

import java.util.List;

import com.dpdocter.beans.Suggestion;
import com.dpdocter.enums.SuggestionState;

public interface SuggestionService {
	public List<Suggestion> getSuggestion(int page, int size, String userId, String suggetionType, String state,
			String searchTerm);

	public Suggestion updateSuggestionState(String suggestionId, SuggestionState state);

}
