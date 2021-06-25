package sg.gov.csit.knowledgeGraph.domain.Response;

import java.util.List;

public class QueryResponse {

	private List<Result> results;
	
	private List<Errors> errors;
    
    public void setResults(List<Result> results) {
    	this.results = results;
    }
    
    public List<Result> getResults() {
    	return this.results;
    }
    
    public void setErrors(List<Errors> errors) {
    	this.errors = errors;
    }
    
    public List<Errors> getErrors() {
    	return this.errors;
    }
}
