package sg.gov.csit.knowledgeGraph.domain.Response;

public class Relationship {

	private Long id;

	private String type;

	private Long startNode;

	private Long endNode;
	
	private Properties properties;
    
    public void setId(Long id) {
    	this.id = id;
    }
    
    public Long getId() {
    	return this.id;
    }
    
    public void setType(String type) {
    	this.type = type;
    }
    
    public String getType() {
    	return this.type;
    }
    
    public void setStartNode(Long startNode) {
    	this.startNode = startNode;
    }
    
    public Long getStartNode() {
    	return this.startNode;
    }
    
    public void setEndNode(Long endNode) {
    	this.endNode = endNode;
    }
    
    public Long getEndNode() {
    	return this.endNode;
    }
    
    public void setProperties(Properties properties) {
    	this.properties = properties;
    }
    
    public Properties getProperties() {
    	return this.properties;
    }
}
