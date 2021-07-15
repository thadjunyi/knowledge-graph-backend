package sg.gov.csit.knowledgeGraph.domain.Response;

import java.util.List;

public class Data {

	private Graph graph;
	
	private List<Object> row;
    
    public void setGraph(Graph graph) {
    	this.graph = graph;
    }
    
    public Graph getGraph() {
    	return this.graph;
    }

	public List<Object> getRow() {
		return row;
	}

	public void setRow(List<Object> row) {
		this.row = row;
	}
}
