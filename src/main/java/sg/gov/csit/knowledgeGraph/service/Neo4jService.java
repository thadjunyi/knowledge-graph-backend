package sg.gov.csit.knowledgeGraph.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import sg.gov.csit.knowledgeGraph.domain.Response.Data;
import sg.gov.csit.knowledgeGraph.domain.Response.Node;
import sg.gov.csit.knowledgeGraph.domain.Response.QueryResponse;
import sg.gov.csit.knowledgeGraph.domain.Response.Relationship;
import sg.gov.csit.knowledgeGraph.domain.Response.Result;
import sg.gov.csit.knowledgeGraph.domain.fieldObject.EdgeObject;
import sg.gov.csit.knowledgeGraph.domain.fieldObject.GraphObject;
import sg.gov.csit.knowledgeGraph.domain.fieldObject.NodeObject;
import sg.gov.csit.knowledgeGraph.domain.fieldObject.PropertiesObject;
import sg.gov.csit.knowledgeGraph.web.dto.response.GraphResponseDTO;

@Service
public class Neo4jService {
	
	@Autowired
	ApiService apiService;
	
	@Autowired
	ObjectMapper objectMapper;
	
	@Autowired
	ModelMapper modelMapper;
	
	public void insertOne(List<String> labels, String name, String title) {
	}

	public void findOne(Long id) {
	}

	public void findOne(String label, String name) {
	}

	public QueryResponse findAll() {
		List<String> queryStatements = new ArrayList<String>();
		//get all nodes and relationships
		queryStatements.add(
				"MATCH (n)-[r]->(m) " +
				"RETURN n,r,m"
		);
		//get all nodes without relationship
		queryStatements.add(
				"MATCH (n) WHERE NOT (n)-[]-() " +
				"RETURN n");
		QueryResponse queryResponse = apiService.queryWithoutCommit(queryStatements);		
		return queryResponse;
	}
	
	public List<String> getOneDegreeQueryStatement(String name) {
		
		List<String> oneDegreeQueryStatement = new ArrayList<String>();
		//get all relationship for the node
		oneDegreeQueryStatement.add(
				"MATCH (n) WHERE n.name = '" + name + "' " + 
				"MATCH (n)-[r]-(m) " + 
				"RETURN n,r,m"
		);
		//get node with no relationship
		oneDegreeQueryStatement.add(
				"MATCH (n) WHERE n.name = '" + name + "' " + 
				"RETURN n"
		);
		return oneDegreeQueryStatement;
	}

	public QueryResponse findNeighbors(String name) {
		
		QueryResponse queryResponse = apiService.queryWithoutCommit(getOneDegreeQueryStatement(name));
		return queryResponse;
	}
	
	public QueryResponse findGraphHistory(String search, Integer degree) {
		
		String[] names = search.split(", ");
		
		List<String> queryStatements = new ArrayList<String>();
		String arrayString = getArrayString(names); 
		queryStatements.add(
				"MATCH (n) where n.name IN " + arrayString + " " + 
				"WITH collect(n) as nodes " + 
				"UNWIND nodes as n " + 
				"UNWIND nodes as m " + 
				"WITH * WHERE id(n) > id(m) " + 
				"MATCH path = (n)-[*.." + degree.toString() + "]-(m) " + 
//				"RETURN path ORDER BY length(path)" 
				"RETURN path"
		);
		for (String name : names) {
			queryStatements.addAll(getOneDegreeQueryStatement(name));
		}
		QueryResponse queryResponse = apiService.queryWithoutCommit(queryStatements);
		return queryResponse;
	}
	
	public String getArrayString(String[] names) {
		
		String result = "[";
		for (int i=0; i<names.length; i++) {
			if (i != 0) {
				result += ", ";
			}
			result += "'" + names[i].replace("'", "\\'") + "'";
		}
		result += "]";
		return result;
	}
	
	public void deleteOne(Long id) {
	}
	
	public void deleteOne(String label, String name) {
	}
	
	public void deleteAll() {
	}
}
