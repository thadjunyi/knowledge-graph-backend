package sg.gov.csit.knowledgeGraph.domain.Response;

import java.util.List;

public class Node {

	private Long id;
	
	private List<String> labels;
	
	private Properties properties;
    
    public void setId(Long id) {
    	this.id = id;
    }
    
    public Long getId() {
    	return this.id;
    }
    
    public void setLabels(List<String> labels) {
    	this.labels = labels;
    }
    
    public List<String> getLabels() {
    	return this.labels;
    }
    
    public void setProperties(Properties properties) {
    	this.properties = properties;
    }
    
    public Properties getProperties() {
    	return this.properties;
    }
}
