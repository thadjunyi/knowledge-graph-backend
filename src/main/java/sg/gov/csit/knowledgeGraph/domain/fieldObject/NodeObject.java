package sg.gov.csit.knowledgeGraph.domain.fieldObject;

public class NodeObject {

	private Long id;
	
	private String type;
	
	private String label;
	
	private Integer link;
	
	private PropertiesObject properties;
    
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
    
    public void setLink(Integer link) {
    	this.link = link;
    }
    
    public Integer getLink() {
    	return this.link;
    }
    
    public void setLabel(String label) {
    	this.label = label;
    }
    
    public String getLabel() {
    	return this.label;
    }
    
    public void setProperties(PropertiesObject properties) {
    	this.properties = properties;
    }
    
    public PropertiesObject getProperties() {
    	return this.properties;
    }
    
    public NodeObject() {
    	
    }
    
    public NodeObject(Long id, String type, String label, Integer link, PropertiesObject properties) {
    	this.id = id;
    	this.type = type;
    	this.label = label;
    	this.link = link;
    	this.properties = properties;
    }
}
