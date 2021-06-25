package sg.gov.csit.knowledgeGraph.domain.Request;

import java.util.List;

public class QueryRequest {

	private List<Statement> statements;
    
    public void setStatements(List<Statement> statements) {
    	this.statements = statements;
    }
    
    public List<Statement> getStatements() {
    	return this.statements;
    }

}
