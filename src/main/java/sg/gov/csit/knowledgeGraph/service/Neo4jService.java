package sg.gov.csit.knowledgeGraph.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import sg.gov.csit.knowledgeGraph.domain.Response.QueryResponse;

@Service
public class Neo4jService {
	
	@Autowired
	ApiService apiService;
	
	public void insertOne(List<String> labels, String name, String title) {
	}

	public void findOne(Long id) {
	}

	public void findOne(String label, String name) {
	}

	public QueryResponse findAll() {
		List<String> queryStatements = new ArrayList<String>();
		//get all nodes and relationships
		queryStatements.add("MATCH (n)-[r]->(m) RETURN n,r,m");
		//get all nodes without relationship
		queryStatements.add("MATCH (n) WHERE NOT (n)-[]-() RETURN n");
		QueryResponse queryResponse = apiService.queryWithoutCommit(queryStatements);
		return queryResponse;
	}
	
	public void deleteOne(Long id) {
	}
	
	public void deleteOne(String label, String name) {
	}
	
	public void deleteAll() {
	}
}
