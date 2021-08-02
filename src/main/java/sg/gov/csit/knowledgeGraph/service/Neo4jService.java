package sg.gov.csit.knowledgeGraph.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
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
import sg.gov.csit.knowledgeGraph.domain.Response.PageRank;
import sg.gov.csit.knowledgeGraph.domain.Response.QueryResponse;
import sg.gov.csit.knowledgeGraph.domain.Response.Relationship;
import sg.gov.csit.knowledgeGraph.domain.Response.Result;
import sg.gov.csit.knowledgeGraph.domain.fieldObject.EdgeObject;
import sg.gov.csit.knowledgeGraph.domain.fieldObject.GraphObject;
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
			"MATCH (n) " + 
			"WHERE NOT (n)-[]-() " + 
			"RETURN n"
		);
		
		queryResponses.add(apiService.queryWithoutCommit(queryStatements));
		return queryResponses;
	}

	public GraphResponseDTO findAllRecommended() {
		
		List<QueryResponse> pageRankQueryResponses = new ArrayList<QueryResponse>();
		pageRankQueryResponses.add(apiService.queryWithoutCommit(getPageRankStatements()));
		return getPageRankGraphResponseDTO(pageRankQueryResponses, new ArrayList<String>(), 1);
	}

	
//	public List<String> getPageRankNodesQueryStatements(List<QueryResponse> queryResponses, List<String> names, Integer degree) {
//
//		List<String> queryStatements = new ArrayList<String>();
//		for (QueryResponse queryResponse : queryResponses) {
//			for (Result result : queryResponse.getResults()) {
//				for (Data data: result.getData()) {
//					if (data.getRow().size() == 2) {
//						Integer id = (Integer) data.getRow().get(0);
//					
//						for (String name : names) {
//							queryStatements.addAll(getDegreeQueryStatement(name, id, degree));
//						}
//						queryStatements.add(
//							"MATCH (n)" + 
//							"WHERE id(n) = " + id + " " + 
//							"RETURN n"
//						);
//					}
//				}
//			}
//		}
//		return queryStatements;
//	}
	
	public List<String> getPageRankStatements() {

		List<String> pageRankQueryStatements = new ArrayList<String>();
		//get all relationship for the node
		pageRankQueryStatements.add(
				"CALL gds.pageRank.stream('graph', { " +
				" maxIterations: 20, " +
				" dampingFactor: 0.85 " +
				"}) " +
				"YIELD nodeId, score " +
				"RETURN nodeId, score " +
				"ORDER BY score DESC " +
				"LIMIT 20 "
		);
		return pageRankQueryStatements;
	}
	
	public List<String> getPersonalizedPageRankStatements(List<String> names) {

		List<String> personalizedPageRankQueryStatements = new ArrayList<String>();
		String query = "";
		List<String> sourceNodes = new ArrayList<String>();
		for (int i=0; i<names.size(); i++) {
			//get all relationship for the node
			query += "MATCH (n" + i + ") " +
					 "WHERE n" + i + ".name = " + getFilteredString(names.get(i)) + " ";
			sourceNodes.add("n" + i);
		}
		
		personalizedPageRankQueryStatements.add(
			query +
			"CALL gds.pageRank.stream('graph', { " +
			" maxIterations: 20, " +
			" dampingFactor: 0.85, " +
			" sourceNodes: " + sourceNodes.toString() + " " +
			"}) " +
			"YIELD nodeId, score " +
			"WHERE score > 0 " + 
			"RETURN nodeId, score, gds.util.asNode(nodeId).name AS name " +
			"ORDER BY score DESC " +
			"LIMIT " + (names.size() + 10)
		);
		return personalizedPageRankQueryStatements;
	}
	
	public List<String> getFilteredPageRankStatements(List<String> names, List<String> filters, Integer nodesNum) {

		List<String> filteredPageRankQueryStatements = new ArrayList<String>();
		String query = "";
		List<String> sourceNodes = new ArrayList<String>();
		for (int i=0; i<names.size(); i++) {
			//get all relationship for the node
			query += "MATCH (n" + i + ") " +
					 "WHERE n" + i + ".name = " + getFilteredString(names.get(i)) + " ";
			sourceNodes.add("n" + i);
		}
		
		for (int i=0; i<filters.size(); i++) {
			filteredPageRankQueryStatements.add(
				query +
				"CALL gds.pageRank.stream('graph', { " +
				" maxIterations: 20, " +
				" dampingFactor: 0.85, " +
				" sourceNodes: " + sourceNodes.toString() + " " +
				"}) " +
				"YIELD nodeId, score " +
				"WHERE score > 0 " + 
				(
					filters.get(i).equals("") ? 
						""
						:
						"AND " + getFilteredString(filters.get(i)) + " IN labels(gds.util.asNode(nodeId)) " +
						(
							filters.get(i).equals("Article") ? 
							"AND gds.util.asNode(nodeId).language = 'en' "
							: 
							""
						)
				) +
				"RETURN nodeId, score, gds.util.asNode(nodeId).name AS name " +
				"ORDER BY score DESC " +
				"LIMIT " + (nodesNum + names.size())
			);
		}
//		print(filteredPageRankQueryStatements);
		return filteredPageRankQueryStatements;
	}
	
	public List<String> getDegreeQueryStatement(String source, Integer degree) {

		List<String> degreeQueryStatements = new ArrayList<String>();
		//get all relationship for the node from 0 edge to degree edge
		degreeQueryStatements.add(
			"MATCH (n)-[r*0.." + degree + "]->(m) " + 
			"WHERE n.name = " + getFilteredString(source) + " " +
			"RETURN n,r,m " +
			"LIMIT 1000"
		);
		return degreeQueryStatements;
	}
	
	public List<String> getDegreeQueryStatement(String source, Integer destination, Integer degree) {

		List<String> degreeQueryStatements = new ArrayList<String>();
		//get all relationship for the node from 0 edge to degree edge
		degreeQueryStatements.add(
			"MATCH (n)-[r*0.." + degree + "]->(m) " + 
			"WHERE n.name = " + getFilteredString(source) + " AND " + 
			"id(m) = " + destination + " " +
			"RETURN n,r,m " + 
			"LIMIT 1000"
		);
		return degreeQueryStatements;
	}

	public List<QueryResponse> findNeighbors(String search) {

		List<String> names = search != null ? new ArrayList<String>(Arrays.asList(search.split("\\|"))) : new ArrayList<String>();
		List<QueryResponse> queryResponses = new ArrayList<QueryResponse>();
		
		for (String name : names) {
			QueryResponse queryResponse = apiService.queryWithoutCommit(getDegreeQueryStatement(name, 1));
//			printResult(queryResponse);
			queryResponses.add(queryResponse);
		}
		return queryResponses;
	}
	
	public List<QueryResponse> findSearchGraph(String search, Integer degree) {

		List<String> names = search != null ? new ArrayList<String>(Arrays.asList(search.split("\\|"))) : new ArrayList<String>();
		List<QueryResponse> queryResponses = new ArrayList<QueryResponse>();
		
//		queryStatements.add(
//				"MATCH (n) " + 
//				"where n.name IN " + getArrayString(names) + " " + 
//				"WITH collect(n) as nodes " + 
//				"UNWIND nodes as n " + 
//				"UNWIND nodes as m " + 
//				"WITH * " + 
//				"WHERE id(n) > id(m) " + 
//				"MATCH path = (n)-[*.." + degree.toString() + "]-(m) " + 
////				"RETURN path ORDER BY length(path)" 
//				"RETURN path"
//		);
//		queryResponses.add(apiService.queryWithoutCommit(queryStatements));
		for (String name : names) {
			QueryResponse queryResponse = apiService.queryWithoutCommit(getDegreeQueryStatement(name, degree));
			queryResponses.add(queryResponse);
		}
		return queryResponses;
	}
	
	public GraphResponseDTO findPersonalizedPageRankGraph(String search, Integer degree) {

		List<String> names = search != null ? new ArrayList<String>(Arrays.asList(search.split("\\|"))) : new ArrayList<String>();
		List<QueryResponse> pageRankQueryResponses = new ArrayList<QueryResponse>();
		pageRankQueryResponses.add(apiService.queryWithoutCommit(getPersonalizedPageRankStatements(names)));
		return getPageRankGraphResponseDTO(pageRankQueryResponses, names, degree);
	}
	
	public GraphResponseDTO getPageRankGraphResponseDTO(List<QueryResponse> pageRankQueryResponses, List<String> names, Integer degree) {

		GraphResponseDTO graphResponseDTO = new GraphResponseDTO();
		List<String> queryStatements = new ArrayList<String>();
		Map<Long, NodeObject> nodeMap = new HashMap<Long, NodeObject>();
		Map<Long, String> edgeMap = new HashMap<Long, String>();
		
		GraphObject graphObject = new GraphObject();
		List<NodeObject> nodes = new ArrayList<NodeObject>();
		List<EdgeObject> edges = new ArrayList<EdgeObject>();
		
		List<Long> ids = new ArrayList<Long>();
		
		for (QueryResponse pageRankQueryResponse : pageRankQueryResponses) {
			for (Result result : pageRankQueryResponse.getResults()) {
				
				for (Data tempData : result.getData()) {
					Integer id = (Integer) tempData.getRow().get(0);
					ids.add(Long.valueOf(id));
				}

				for (int i=0; i<result.getData().size(); i++) {
					Data data = result.getData().get(i);
					
					Integer id = (Integer) data.getRow().get(0);
//					Double score = (Double) data.getRow().get(1);
				
					queryStatements.clear();
					for (String name : names) {
						queryStatements.addAll(getDegreeQueryStatement(name, id, degree));
					}
					queryStatements.add(
						"MATCH (n) " +
						"WHERE id(n) = " + id + " " +
						"RETURN n"
					);
						
					QueryResponse queryResponse = apiService.queryWithoutCommit(queryStatements);
					
					for (Result queryResponseResult : queryResponse.getResults()) {
						for (Data queryResponseData : queryResponseResult.getData()) {
							for (Node nodeData : queryResponseData.getGraph().getNodes()) {
								
								if (!nodeMap.containsKey(nodeData.getId())) {
									NodeObject node = new NodeObject(
										nodeData.getId(), 
										nodeData.getLabels().size() == 0 ? "" : nodeData.getLabels().get(0),
										(String) nodeData.getProperties().get("name"),
										nodeData.getProperties()
									);
									if (names.contains(node.getLabel())) {
										node.setSize(30);
									} else if (ids.contains(node.getId())) {
										node.setSize((int) (15 + (result.getData().size()-names.size()-i)));
										node.setColor("#00ff00");
									}
									nodes.add(node);
									nodeMap.put(nodeData.getId(), node);
								}
							}
							
							for (Relationship relationship : queryResponseData.getGraph().getRelationships()) {
								if (!edgeMap.containsKey(relationship.getId())) {
									edgeMap.put(relationship.getId(), relationship.getProperties().getName());
									EdgeObject edge = new EdgeObject(
										relationship.getId(),
										relationship.getType(),
										relationship.getStartNode(),
										nodeMap.get(relationship.getStartNode()).getLabel(),
										relationship.getEndNode(),
										nodeMap.get(relationship.getEndNode()).getLabel()
									);
									edges.add(edge);
								}
							}
						}
					}
				}
			}
		}
		graphObject.setNodes(nodes);
		graphObject.setEdges(edges);
		graphResponseDTO.setGraph(graphObject);
		return graphResponseDTO;
	}
	
	public List<QueryResponse> findLinkageGraph(String search) {

		List<String> names = search != null ? new ArrayList<String>(Arrays.asList(search.split("\\|"))) : new ArrayList<String>();
		List<String> queryStatements = new ArrayList<String>();
		List<QueryResponse> queryResponses = new ArrayList<QueryResponse>();
		
		queryStatements.add(
			"MATCH (n) " + 
			"where n.name IN " + getArrayString(names) + " " + 
			"WITH collect(n) as nodes " + 
			"UNWIND nodes as n " + 
			"UNWIND nodes as m " + 
			"WITH * " + 
			"WHERE id(n) > id(m) " + 
			"MATCH path = (n)-[*..3]-(m) " + 
			"RETURN path ORDER BY length(path) "
//			"LIMIT 1000"
		);
		
		queryStatements.add(
			"MATCH (n) " + 
			"where n.name IN " + getArrayString(names) + " " +
			"RETURN n"
		);
		
		queryResponses.add(apiService.queryWithoutCommit(queryStatements));
		return queryResponses;
	}
	
	
	public List<QueryResponse> findFocusGraphIDQueryResponse(String search, String typeFilter, Integer nodesNum) {

		List<String> names = search != null ? new ArrayList<String>(Arrays.asList(search.split("\\|"))) : new ArrayList<String>();
		List<String> filters = typeFilter != null ? new ArrayList<String>(Arrays.asList(typeFilter.split("\\|"))) : new ArrayList<String>();
		
		List<QueryResponse> queryResponses = new ArrayList<QueryResponse>();
		queryResponses.add(apiService.queryWithoutCommit(getFilteredPageRankStatements(names, filters, nodesNum)));
		return queryResponses;
	}
	
	public List<Long> getIds(String search, List<QueryResponse> queryResponses, Integer nodesNum) {
		
		List<String> names = search != null ? new ArrayList<String>(Arrays.asList(search.split("\\|"))) : new ArrayList<String>();
		List<PageRank> pageRankList = new ArrayList<PageRank>();
		List<Long> ids = new ArrayList<Long>();

		for (QueryResponse queryResponse : queryResponses) {
			for (Result result : queryResponse.getResults()) {
				
				for (Data tempData : result.getData()) {
					pageRankList.add(
						new PageRank(
							(Integer) tempData.getRow().get(0),
							(Double) tempData.getRow().get(1),
							(String) tempData.getRow().get(2)
						)
					);
				}
			}
		}

		Comparator<PageRank> compareByScore = (PageRank o1, PageRank o2) -> 
			o1.getScore().compareTo( o2.getScore() );
		Collections.sort(pageRankList, compareByScore.reversed());
		
		for (int i=0; i<pageRankList.size() && ids.size() < nodesNum; i++) {

			String name = pageRankList.get(i).getName();
			if (!names.contains(name)) {
				Integer id = pageRankList.get(i).getId();
				ids.add(Long.valueOf(id));
			}
		}
		
		return ids;
	}
	
	public List<QueryResponse> getShortestPathQueryResponses(String search, List<Long> ids) {
		
		List<String> names = search != null ? new ArrayList<String>(Arrays.asList(search.split("\\|"))) : new ArrayList<String>();
		List<String> queryStatements = new ArrayList<String>();
		List<QueryResponse> queryResponses = new ArrayList<QueryResponse>();
		
		for (String name : names) {
			for (Long id : ids) {
				queryStatements.add(
					"MATCH (n) " + 
					"WHERE n.name = " + getFilteredString(name) + " " + 
					"MATCH (m) " + 
					"WHERE id(m) = " + id + " " + 
					"MATCH p = shortestPath((n)-[*]-(m)) " + 
//					"WITH p " + 
//					"WHERE length(p) > 1 " + 
					"RETURN p " + 
					"LIMIT 1 " 
				);
			}
		}
		queryResponses.add(apiService.queryWithoutCommit(queryStatements));
		return queryResponses;
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
		if (name != null) {
			return "'" + name.replace("'", "\\'") + "'";
		} else {
			return "''";
		}
	}
	
//	public GraphResponseDTO findFilters(GraphResponseDTO graphResponseDTO, String filterString) {
//		
//		List<String> filters = filterString != null ? new ArrayList<String>(Arrays.asList(filterString.split("\\|"))) : new ArrayList<String>();
//		if (filters.size() == 1 && filters.get(0) == "") {
//			return graphResponseDTO;
//		}
//		
//		List<QueryResponse> queryResponses = new ArrayList<QueryResponse>();
//		Map<Long, NodeObject> nodeMap = new HashMap<Long, NodeObject>();
//		Map<Long, EdgeObject> edgeMap = new HashMap<Long, EdgeObject>();
//		
//		for(NodeObject node : graphResponseDTO.getGraph().getNodes()) {
//			List<String> queryStatements = new ArrayList<String>();
//			nodeMap.put(node.getId(), node);
//			//for each node, check it's properties against the filters
//			for (String filter : filters) {
//				//get node if properties contain filter
//				queryStatements.add(
//					"MATCH (n) " +
//					"WHERE n.name = " + getFilteredString(node.getLabel()) + " " + 
////					"MATCH (n)-[r]-(m) " + 
//					"MATCH (n)-[r]-(m:" + filter + ") " + 
////					"WHERE any(prop in keys(m) WHERE TOSTRING(m[prop]) CONTAINS " + getFilteredString(filter) + ") " +
//					"RETURN m"
//				);
//				//get the relationship of the above node
//				queryStatements.add(
//					"MATCH (n) " + 
//					"WHERE n.name = " + getFilteredString(node.getLabel()) + " " + 
////					"MATCH (n)-[r]-(m) " + 
//					"MATCH (n)-[r]-(m:" + filter + ") " + 
////					"WHERE any(prop in keys(m) WHERE TOSTRING(m[prop]) CONTAINS " + getFilteredString(filter) + ") " +
//					"RETURN r"
//				);
//			}
////			//for filter edge
////			queryStatements.add(
////				"MATCH (n) " + 
////				"WHERE n.name = " + getFilteredString(node.getLabel()) + " " + 
////				"MATCH (n)-[r]-(m) " + 
////				"WHERE any(item in " + getArrayString(filters) + " WHERE type(r) CONTAINS item) " + 
////				"RETURN n,r,m"
////			);
//			queryResponses.add(apiService.queryWithoutCommit(queryStatements));
//		}
//		
//		for(EdgeObject edge : graphResponseDTO.getGraph().getEdges()) {
//			edgeMap.put(edge.getId(), edge);
//		}
//		
//		List<NodeObject> nodes = graphResponseDTO.getGraph().getNodes();
//		List<EdgeObject> edges = graphResponseDTO.getGraph().getEdges();
//		
//		for (QueryResponse currentQueryResponse : queryResponses) {
//			for (Result result : currentQueryResponse.getResults()) {
//				for (Data data: result.getData()) {
//					
//					for (Node nodeData : data.getGraph().getNodes()) {
//						if (!nodeMap.containsKey(nodeData.getId())) {
//							NodeObject node = new NodeObject(
//									nodeData.getId(), 
//									nodeData.getLabels().size() == 0 ? "" : nodeData.getLabels().get(0),
//									(String) nodeData.getProperties().get("name"),
//									nodeData.getProperties()
//							);
//							if (data.getGraph().getNodes().size() == 1) {
//								node.setColor("#ff0000");
//							}
//							nodeMap.put(nodeData.getId(), node);
//							nodes.add(node);
//							
//						} else {
//							NodeObject node = nodeMap.get(nodeData.getId());
//							if (data.getGraph().getNodes().size() == 1) {
//								node.setColor("#ff0000");
//							}
//							for (NodeObject currentNode : nodes) {
//								if (currentNode.getId() == nodeData.getId()) {
//									currentNode = node;
//									break;
//								}
//							}
//						}
//					}
//					
//					for (Relationship relationship : data.getGraph().getRelationships()) {
//						if (!edgeMap.containsKey(relationship.getId())) {
//							EdgeObject edge = new EdgeObject(
//								relationship.getId(), 
//								relationship.getType(),
//								relationship.getStartNode(),
//								nodeMap.get(relationship.getStartNode()).getLabel(),
//								relationship.getEndNode(),
//								nodeMap.get(relationship.getEndNode()).getLabel()
//							);
//							edge.setColor("#ff0000");
//							edge.setWidth(5);
//							edgeMap.put(relationship.getId(), edge);
//							edges.add(edge);
//							
//						} else {
//							EdgeObject edge = edgeMap.get(relationship.getId());
//							edge.setColor("#ff0000");
//							edge.setWidth(5);
//							for (EdgeObject currentEdge : edges) {
//								if (currentEdge.getId() == relationship.getId()) {
//									currentEdge = edge;
//									break;
//								}
//							}
//						}
//					}
//				}
//			}
//		}
//		graphResponseDTO.getGraph().setNodes(nodes);
//		graphResponseDTO.getGraph().setEdges(edges);
//		return graphResponseDTO;
//	}
	
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
