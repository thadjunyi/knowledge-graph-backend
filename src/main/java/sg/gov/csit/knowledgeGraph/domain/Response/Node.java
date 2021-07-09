package sg.gov.csit.knowledgeGraph.domain.Response;

import java.util.List;
import java.util.Map;

public class Node {

	private Long id;
	
	private List<String> labels;
	
	private Map<String, Object> properties;
    
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
    
    public void setProperties(Map<String, Object> properties) {
    	this.properties = properties;
    }
    
    public Map<String, Object> getProperties() {
    	return this.properties;
    }
}
