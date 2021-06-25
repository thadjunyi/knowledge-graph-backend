package sg.gov.csit.knowledgeGraph.domain.Response;

import java.util.List;

public class Graph {

	private List<Node> nodes;
	
	private List<Relationship> relationships;
    
    public void setNodes(List<Node> nodes) {
    	this.nodes = nodes;
    }
    
    public List<Node> getNodes() {
    	return this.nodes;
    }
    
    public void setRelationships(List<Relationship> relationships) {
    	this.relationships = relationships;
    }
    
    public List<Relationship> getRelationships() {
    	return this.relationships;
    }
}
