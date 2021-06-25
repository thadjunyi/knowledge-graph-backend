package sg.gov.csit.knowledgeGraph.domain.Request;

import java.util.List;

public class Statement {

	private String statement;

	private Parameters parameters;

	private List<String> resultDataContents;
    
    public void setStatement(String statement) {
    	this.statement = statement;
    }
    
    public String getStatement() {
    	return this.statement;
    }
    
    public void setParameters(Parameters parameters) {
    	this.parameters = parameters;
    }
    
    public Parameters getParameters() {
    	return this.parameters;
    }
    
    public void setResultDataContents(List<String> resultDataContents) {
    	this.resultDataContents = resultDataContents;
    }
    
    public List<String> getResultDataContents() {
    	return this.resultDataContents;
    }
}
