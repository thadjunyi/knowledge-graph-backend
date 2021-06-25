package sg.gov.csit.knowledgeGraph.domain.fieldObject;

import java.util.List;

public class GraphObject {

	private List<NodeObject> nodes;
	
	private List<EdgeObject> edges;
    
    public void setNodes(List<NodeObject> nodes) {
    	this.nodes = nodes;
    }
    
    public List<NodeObject> getNodes() {
    	return this.nodes;
    }
    
    public void setEdges(List<EdgeObject> edges) {
    	this.edges = edges;
    }
    
    public List<EdgeObject> getEdges() {
    	return this.edges;
    }
}
