package sg.gov.csit.knowledgeGraph.domain.Unused;

import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;

public class Edge {

	@Id
	@GeneratedValue
	private Long id;
	
    private String label;
	
    private Long from;
	
    private Long to;
    
    private String value;
    
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
    
    public void setTo(Long to) {
    	this.to = to;
    }
    
    public Long getTo() {
    	return this.to;
    }
    
    public void setValue(String value) {
    	this.value = value;
    }
    
    public String getValue() {
    	return this.value;
    }
    
    public Edge() {
    
    }
    
    public Edge(String label, Long from, Long to) {
    	this.label = label;
    	this.from = from;
    	this.to = to;
	}
    
    public Edge(String label, Long from, Long to, String value) {
    	this.label = label;
    	this.from = from;
    	this.to = to;
    	this.value = value;
	}
}
