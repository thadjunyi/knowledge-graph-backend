package sg.gov.csit.knowledgeGraph.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.modelmapper.ModelMapper;
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
import sg.gov.csit.knowledgeGraph.domain.fieldObject.NodeObject;
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

	public List<QueryResponse> findAll() {
		
		List<QueryResponse> queryResponses = new ArrayList<QueryResponse>();
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
		queryResponses.add(apiService.queryWithoutCommit(queryStatements));		
		return queryResponses;
	}
	
	public List<String> getOneDegreeQueryStatement(String name) {

		List<String> oneDegreeQueryStatement = new ArrayList<String>();
		//get all relationship for the node
		oneDegreeQueryStatement.add(
				"MATCH (n) " + 
				"WHERE n.name CONTAINS " + getFilteredString(name) + " " + 
				"MATCH (n)-[r]-(m) " + 
				"RETURN n,r,m"
		);
		//get node with no relationship
		oneDegreeQueryStatement.add(
				"MATCH (n) " + 
				"WHERE n.name CONTAINS " + getFilteredString(name) + " " + 
				"RETURN n"
		);
		return oneDegreeQueryStatement;
	}

	public List<QueryResponse> findNeighbors(String search) {

		List<String> names = search != null ? new ArrayList<String>(Arrays.asList(search.split(","))) : new ArrayList<String>();
		List<QueryResponse> queryResponses = new ArrayList<QueryResponse>();
		
		for (String name : names) {
			QueryResponse queryResponse = apiService.queryWithoutCommit(getOneDegreeQueryStatement(name));
//			printResult(queryResponse);
			queryResponses.add(queryResponse);
		}
		return queryResponses;
	}
	
	public List<QueryResponse> findSearchGraph(String search, Integer degree) {

		List<String> names = search != null ? new ArrayList<String>(Arrays.asList(search.split(","))) : new ArrayList<String>();
		List<QueryResponse> queryResponses = new ArrayList<QueryResponse>();
		List<String> queryStatements = new ArrayList<String>();
		
		queryStatements.add(
				"MATCH (n) " + 
				"where n.name IN " + getArrayString(names) + " " + 
				"WITH collect(n) as nodes " + 
				"UNWIND nodes as n " + 
				"UNWIND nodes as m " + 
				"WITH * " + 
				"WHERE id(n) > id(m) " + 
				"MATCH path = (n)-[*.." + degree.toString() + "]-(m) " + 
//				"RETURN path ORDER BY length(path)" 
				"RETURN path"
		);
		queryResponses.add(apiService.queryWithoutCommit(queryStatements));
		
		for (String name : names) {
			QueryResponse queryResponse = apiService.queryWithoutCommit(getOneDegreeQueryStatement(name));
			queryResponses.add(queryResponse);
		}
		return queryResponses;
	}
	
	public List<QueryResponse> findPageRankGraph(String search) {

		List<String> names = search != null ? new ArrayList<String>(Arrays.asList(search.split(","))) : new ArrayList<String>();
		List<QueryResponse> queryResponses = new ArrayList<QueryResponse>();
		List<String> queryStatements = new ArrayList<String>();
		
		return findAll();
	}
	
	public String getArrayString(List<String> names) {
		
		String result = "[";
		for (int i=0; i<names.size(); i++) {
			if (i != 0) {
				result += ", ";
			}
			result += getFilteredString(names.get(i));
		}
		result += "]";
		return result;
	}
	
	public String getFilteredString(String name) {
		String result = "'" + name.replace("'", "\\'") + "'";
//		System.out.println(result);
		return result;
	}
	
	public GraphResponseDTO findFilters(GraphResponseDTO graphResponseDTO, String filterString) {
		
		List<String> filters = filterString != null ? new ArrayList<String>(Arrays.asList(filterString.split(","))) : new ArrayList<String>();
		if (filters.size() == 1 && filters.get(0) == "") {
			return graphResponseDTO;
		}
		
		List<QueryResponse> queryResponses = new ArrayList<QueryResponse>();
		Map<Long, NodeObject> nodeMap = new HashMap<Long, NodeObject>();
		Map<Long, EdgeObject> edgeMap = new HashMap<Long, EdgeObject>();
		
		for(NodeObject node : graphResponseDTO.getGraph().getNodes()) {
			List<String> queryStatements = new ArrayList<String>();
			nodeMap.put(node.getId(), node);
			//for each node, check it's properties against the filters
			for (String filter : filters) {
				//get node if properties contain filter
				queryStatements.add(
					"MATCH (n) " + 
					"WHERE n.name = " + getFilteredString(node.getLabel()) + " " + 
					"MATCH (n)-[r]-(m) " + 
					"WHERE any(prop in keys(m) WHERE TOSTRING(m[prop]) CONTAINS " + getFilteredString(filter) + ") " + 
					"RETURN m"
				);
				//get the relationship of the above node
				queryStatements.add(
					"MATCH (n) " + 
					"WHERE n.name = " + getFilteredString(node.getLabel()) + " " + 
					"MATCH (n)-[r]-(m) " + 
					"WHERE any(prop in keys(m) WHERE TOSTRING(m[prop]) CONTAINS " + getFilteredString(filter) + ") " + 
					"RETURN r"
				);
			}
			//for filter edge
			queryStatements.add(
				"MATCH (n) " + 
				"WHERE n.name = " + getFilteredString(node.getLabel()) + " " + 
				"MATCH (n)-[r]-(m) " + 
				"WHERE any(item in " + getArrayString(filters) + " WHERE type(r) CONTAINS item) " + 
				"RETURN n,r,m"
			);
			queryResponses.add(apiService.queryWithoutCommit(queryStatements));
		}
		
		for(EdgeObject edge : graphResponseDTO.getGraph().getEdges()) {
			edgeMap.put(edge.getId(), edge);
		}
		
		List<NodeObject> nodes = graphResponseDTO.getGraph().getNodes();
		List<EdgeObject> edges = graphResponseDTO.getGraph().getEdges();
		
		for (QueryResponse currentQueryResponse : queryResponses) {
			for (Result result : currentQueryResponse.getResults()) {
				for (Data data: result.getData()) {
					
					for (Node nodeData : data.getGraph().getNodes()) {
						if (!nodeMap.containsKey(nodeData.getId())) {
							NodeObject node = new NodeObject(
									nodeData.getId(), 
									nodeData.getLabels().get(0),
									(String) nodeData.getProperties().get("name"),
									nodeData.getProperties()
							);
							if (data.getGraph().getNodes().size() == 1) {
								node.setColor("#ff0000");
							}
							nodeMap.put(nodeData.getId(), node);
							nodes.add(node);
							
						} else {
							NodeObject node = nodeMap.get(nodeData.getId());
							if (data.getGraph().getNodes().size() == 1) {
								node.setColor("#ff0000");
							}
							for (NodeObject currentNode : nodes) {
								if (currentNode.getId() == nodeData.getId()) {
									currentNode = node;
									break;
								}
							}
						}
					}
					
					for (Relationship relationship : data.getGraph().getRelationships()) {
						if (!edgeMap.containsKey(relationship.getId())) {
							EdgeObject edge = new EdgeObject(
								relationship.getId(), 
								relationship.getType(),
								relationship.getStartNode(),
								nodeMap.get(relationship.getStartNode()).getLabel(),
								relationship.getEndNode(),
								nodeMap.get(relationship.getEndNode()).getLabel()
							);
							edge.setColor("#ff0000");
							edge.setWidth(5);
							edgeMap.put(relationship.getId(), edge);
							edges.add(edge);
							
						} else {
							EdgeObject edge = edgeMap.get(relationship.getId());
							edge.setColor("#ff0000");
							edge.setWidth(5);
							for (EdgeObject currentEdge : edges) {
								if (currentEdge.getId() == relationship.getId()) {
									currentEdge = edge;
									break;
								}
							}
						}
					}
				}
			}
		}
		graphResponseDTO.getGraph().setNodes(nodes);
		graphResponseDTO.getGraph().setEdges(edges);
		return graphResponseDTO;
	}
	
	public void printResult(QueryResponse queryResponse) {
		
		for (Result result : queryResponse.getResults()) {
//			for (Data data : result.getData()) {
				try {
					String messageJsonString = objectMapper.writeValueAsString(result);
					System.out.println(messageJsonString);
				} catch (JsonProcessingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
//			}
		}
	}
	
	public void print(Object object) {
		
		try {
			String messageJsonString = objectMapper.writeValueAsString(object);
			System.out.println(messageJsonString);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void deleteOne(Long id) {
	}
	
	public void deleteOne(String label, String name) {
	}
	
	public void deleteAll() {
	}
}
