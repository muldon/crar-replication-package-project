package com.ufu.crar.to;

import java.util.HashMap;

public class SearchResults {
	public HashMap<String, String> relevantHeaders;
    public String jsonResponse;
    
    public SearchResults(HashMap<String, String> headers, String json) {
        relevantHeaders = headers;
        jsonResponse = json;
    }
}
