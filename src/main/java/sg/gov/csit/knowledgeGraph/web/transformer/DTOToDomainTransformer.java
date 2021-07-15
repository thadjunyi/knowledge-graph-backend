package sg.gov.csit.knowledgeGraph.web.transformer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
import sg.gov.csit.knowledgeGraph.web.dto.response.GraphResponseDTO;

@Component
public class DTOToDomainTransformer {
	
	@Autowired
	ModelMapper modelMapper;
	
	@Autowired
	ObjectMapper objectMapper;

	public GraphResponseDTO convertQueryResponseToGraphResponseDTO(String search, List<QueryResponse> queryResponses) {
		
//		java.lang.reflect.Type targetType = new TypeToken<Map<String, Object>>() {}.getType();
		List<String> names = search != null ? new ArrayList<String>(Arrays.asList(search.split(","))) : new ArrayList<String>();

		GraphResponseDTO graphResponseDTO = new GraphResponseDTO();
		Map<Long, NodeObject> nodeMap = new HashMap<Long, NodeObject>();
		Map<Long, String> edgeMap = new HashMap<Long, String>();
		
		GraphObject graphObject = new GraphObject();
		List<NodeObject> nodes = new ArrayList<NodeObject>();
		List<EdgeObject> edges = new ArrayList<EdgeObject>();
		
		for (QueryResponse queryResponse : queryResponses) {
			for (Result result : queryResponse.getResults()) {
				for (Data data: result.getData()) {
					
					for (Node nodeData : data.getGraph().getNodes()) {
						
						if (!nodeMap.containsKey(nodeData.getId())) {
//							Map<String, Object> map = modelMapper.map(nodeData.getProperties(), targetType);
							NodeObject node = new NodeObject(
									nodeData.getId(), 
									nodeData.getLabels().get(0),
									(String) nodeData.getProperties().get("name"),
									nodeData.getProperties()
							);
							for (String name : names) {
								if (name.equals(node.getLabel())) {
									node.setSize(30);
									break;
								}
							}
							nodes.add(node);
							nodeMap.put(nodeData.getId(), node);
						}
					}
					
					for (Relationship relationship : data.getGraph().getRelationships()) {
						
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
		graphObject.setNodes(nodes);
		graphObject.setEdges(edges);
		graphResponseDTO.setGraph(graphObject);
		return graphResponseDTO;
	}
		
	public void printResult(Object object) {
		
		try {
			String messageJsonString = objectMapper.writeValueAsString(object);
			System.out.println("Message: " + messageJsonString);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
