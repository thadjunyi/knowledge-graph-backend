package sg.gov.csit.knowledgeGraph.domain.Unused;

import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.DynamicLabels;

import java.util.List;

@Node
public class Entity {

	@Id
	@GeneratedValue
	private Long id;
	
    @DynamicLabels
    private List<String> labels;
	
    private String name;
	
    private String title;
    
    public Long getId() {
    	return this.id;
    }
    
    public void setLabels(List<String> labels) {
    	this.labels = labels;
    }
    
    public List<String> getLabels() {
    	return this.labels;
    }
    
    public void setName(String name) {
    	this.name = name;
    }
    
    public String getName() {
    	return this.name;
    }
    
    public void setTitle(String title) {
    	this.title = title;
    }
    
    public String getTitle() {
    	return this.title;
    }
    
    public Entity() {
    
    }
    
    public Entity(List<String> labels, String name) {
    	this.labels = labels;
    	this.name = name;
	}
    
    public Entity(List<String> labels, String name, String title) {
    	this.labels = labels;
    	this.name = name;
    	this.title = title;
	}
}