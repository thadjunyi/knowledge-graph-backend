package sg.gov.csit.knowledgeGraph.domain.fieldObject;

public class EdgeObject {
	
	private Long id;
	
    private String label;
	
    private Long from;
	
    private String fromLabel;
	
    private Long to;
	
    private String toLabel;
    
    public Long getId() {
    	return this.id;
    }
    
    public void setLabel(String label) {
    	this.label = label;
    }
    
    public String getLabel() {
    	return this.label;
    }
    
    public void setFrom(Long from) {
    	this.from = from;
    }
    
    public Long getFrom() {
    	return this.from;
    }
    
    public String getFromLabel() {
    	return this.fromLabel;
    }
    
    public void setFromLabel(String fromLabel) {
    	this.fromLabel = fromLabel;
    }
    
    public void setTo(Long to) {
    	this.to = to;
    }
    
    public Long getTo() {
    	return this.to;
    }
    
    public String getToLabel() {
    	return this.toLabel;
    }
    
    public void setToLabel(String toLabel) {
    	this.toLabel = toLabel;
    }
    
    public EdgeObject() {
    	
    }
    
    public EdgeObject(Long id, String label, Long from , String fromLabel, Long to, String toLabel) {
    	this.id = id;
    	this.label = label;
    	this.from = from;
    	this.fromLabel = fromLabel;
    	this.to = to;
    	this.toLabel = toLabel;
    }
}
