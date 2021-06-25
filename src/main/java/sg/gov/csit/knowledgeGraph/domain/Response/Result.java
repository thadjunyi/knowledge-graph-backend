package sg.gov.csit.knowledgeGraph.domain.Response;

import java.util.List;

public class Result {

	private List<String> columns;
	
	private List<Data> data;
    
    public void setColumns(List<String> columns) {
    	this.columns = columns;
    }
    
    public List<String> getColumns() {
    	return this.columns;
    }
    
    public void setData(List<Data> data) {
    	this.data = data;
    }
    
    public List<Data> getData() {
    	return this.data;
    }
}
