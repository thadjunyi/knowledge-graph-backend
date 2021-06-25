package sg.gov.csit.knowledgeGraph.web.dto.response;

import sg.gov.csit.knowledgeGraph.domain.fieldObject.GraphObject;

public class GraphResponseDTO {

	private GraphObject graph;
    
    public void setGraph(GraphObject graph) {
    	this.graph = graph;
    }
    
    public GraphObject getGraph() {
    	return this.graph;
    }
}
