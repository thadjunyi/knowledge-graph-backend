package sg.gov.csit.knowledgeGraph.domain.Unused;

import java.util.List;

public class Relationship {
	
    private Node sourceNode;
    
    private List<Edge> edge;
	
    private Node destinationNode;
    
    public void setSourceNode(Node sourceNode) {
    	this.sourceNode = sourceNode;
    }
    
    public Node getSourceNode() {
    	return this.sourceNode;
    }
    
    public void setEdge(List<Edge> edge) {
    	this.edge = edge;
    }
    
    public List<Edge> getEdge() {
    	return this.edge;
    }
    
    public void setDestinationNode(Node destinationNode) {
    	this.destinationNode = destinationNode;
    }
    
    public Node getDestinationNode() {
    	return this.destinationNode;
    }
    
    public Relationship() {
    
    }
    
    public Relationship(Node sourceNode, List<Edge> edge, Node destinationNode) {
    	this.sourceNode = sourceNode;
    	this.edge = edge;
    	this.destinationNode = destinationNode;
	}
}
