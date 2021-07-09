package sg.gov.csit.knowledgeGraph.domain.fieldObject;

import java.util.Map;

public class NodeObject {

	private Long id;
	
	private String type;
	
	private String label;
	
	private Integer size;
	
	private String color;
	
	private Map<String, Object> properties;
    
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
    
    public void setLabel(String label) {
    	this.label = label;
    }
    
    public String getLabel() {
    	return this.label;
    }
    
    public void setSize(Integer size) {
    	this.size = size;
    }
    
    public Integer getSize() {
    	return this.size;
    }
    
    public void setColor(String color) {
    	this.color = color;
    }
    
    public String getColor() {
    	return this.color;
    }
    
    public void setProperties(Map<String, Object> properties) {
    	this.properties = properties;
    }
    
    public Map<String, Object> getProperties() {
    	return this.properties;
    }
    
    public NodeObject() {
    	
    }
    
    public NodeObject(Long id, String type, String label, Map<String, Object> properties) {
    	this.id = id;
    	this.type = type;
    	this.label = label;
    	this.size = 10;
    	this.properties = properties;
    }
}
