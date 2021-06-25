package sg.gov.csit.knowledgeGraph.web.transformer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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

@Component
public class DTOToDomainTransformer {
	
	@Autowired
	ModelMapper modelMapper;

	public GraphResponseDTO convertQueryResponseToGraphResponseDTO(QueryResponse queryResponse) {
		
		java.lang.reflect.Type targetType = new TypeToken<PropertiesObject>() {}.getType();
//		GraphResponseDTO graphResponseDTO =  modelMapper.map(queryResponse, targetType);
//		return graphResponseDTO;
		
		GraphResponseDTO graphResponseDTO = new GraphResponseDTO();
		Map<Long, String> map = new HashMap<Long, String>();
		
		GraphObject graphObject = new GraphObject();
		List<NodeObject> nodes = new ArrayList<NodeObject>();
		List<EdgeObject> edges = new ArrayList<EdgeObject>();
		
		for (Result result : queryResponse.getResults()) {
			for (Data data: result.getData()) {
				
				for (Node nodeData : data.getGraph().getNodes()) {
					
					if (!map.containsKey(nodeData.getId())) {
						map.put(nodeData.getId(), nodeData.getProperties().getName());
						PropertiesObject propertiesObject =  modelMapper.map(nodeData.getProperties(), targetType);
						List<String> labels = nodeData.getLabels();
						labels.remove("Node");
						
						NodeObject node = new NodeObject(
								nodeData.getId(), 
								labels.get(0),
								nodeData.getProperties().getName(),
								propertiesObject
						);
						nodes.add(node);
					}
				}
				
				for (Relationship relationship : data.getGraph().getRelationships()) {
					EdgeObject edge = new EdgeObject(
						relationship.getId(), 
						relationship.getType(),
						relationship.getStartNode(),
						map.get(relationship.getStartNode()),
						relationship.getEndNode(),
						map.get(relationship.getEndNode())
					);
					edges.add(edge);
				}
			}
		}
		
		graphObject.setNodes(nodes);
		graphObject.setEdges(edges);
		graphResponseDTO.setGraph(graphObject);
		return graphResponseDTO;
	} 
}
