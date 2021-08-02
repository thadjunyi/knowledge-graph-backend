package sg.gov.csit.knowledgeGraph.web.rest.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import sg.gov.csit.knowledgeGraph.domain.Response.QueryResponse;
import sg.gov.csit.knowledgeGraph.service.Neo4jService;
import sg.gov.csit.knowledgeGraph.web.dto.response.GraphResponseDTO;
import sg.gov.csit.knowledgeGraph.web.transformer.DTOToDomainTransformer;

@RestController
@RequestMapping("/v1/search")
public class SearchController {
	
	@Autowired
	private Neo4jService neo4jService;
	
	@Autowired
	DTOToDomainTransformer dTOToDomainTransformer;

	@RequestMapping(method=RequestMethod.GET, path= {"/getAll"})
	public ResponseEntity<GraphResponseDTO> getAll(HttpServletRequest request, HttpServletResponse response) {
		
		List<QueryResponse> queryResponses = neo4jService.findAll();
		GraphResponseDTO graphResponseDTO = dTOToDomainTransformer.convertQueryResponseToGraphResponseDTO("", queryResponses, new ArrayList<Long>());
		return new ResponseEntity<GraphResponseDTO>(graphResponseDTO, HttpStatus.OK);
	}

	@RequestMapping(method=RequestMethod.GET, path= {"/getAllRecommended"})
	public ResponseEntity<GraphResponseDTO> getAllRecommended(HttpServletRequest request, HttpServletResponse response) {
		
		GraphResponseDTO graphResponseDTO = neo4jService.findAllRecommended();
		return new ResponseEntity<GraphResponseDTO>(graphResponseDTO, HttpStatus.OK);
	}
	
	@RequestMapping(method=RequestMethod.GET, path= {"/findNeighbors"})
	public ResponseEntity<GraphResponseDTO> findNeighbors(HttpServletRequest request, HttpServletResponse response, @RequestParam(required=true) String search) {

		List<QueryResponse> queryResponses = neo4jService.findNeighbors(search);
		GraphResponseDTO graphResponseDTO = dTOToDomainTransformer.convertQueryResponseToGraphResponseDTO(search, queryResponses, new ArrayList<Long>());
		return new ResponseEntity<GraphResponseDTO>(graphResponseDTO, HttpStatus.OK);
	}
	
	@RequestMapping(method=RequestMethod.GET, path= {"/findSearchGraph"})
	public ResponseEntity<GraphResponseDTO> findSearchGraph(HttpServletRequest request, HttpServletResponse response, @RequestParam(required=true) String search, @RequestParam(defaultValue="1") Integer degree) {

		List<QueryResponse> queryResponses = neo4jService.findSearchGraph(search, degree);
		GraphResponseDTO graphResponseDTO = dTOToDomainTransformer.convertQueryResponseToGraphResponseDTO(search, queryResponses, new ArrayList<Long>());
		return new ResponseEntity<GraphResponseDTO>(graphResponseDTO, HttpStatus.OK);
	}
	
	@RequestMapping(method=RequestMethod.GET, path= {"/findPageRankGraph"})
	public ResponseEntity<GraphResponseDTO> findPageRankGraph(HttpServletRequest request, HttpServletResponse response, @RequestParam(required=true) String search, @RequestParam(defaultValue="1") Integer degree) {

		GraphResponseDTO graphResponseDTO = neo4jService.findPersonalizedPageRankGraph(search, degree);
		return new ResponseEntity<GraphResponseDTO>(graphResponseDTO, HttpStatus.OK);
	}
	
	@RequestMapping(method=RequestMethod.GET, path= {"/findLinkageGraph"})
	public ResponseEntity<GraphResponseDTO> findLinkageGraph(HttpServletRequest request, HttpServletResponse response, @RequestParam(required=true) String search) {

		List<QueryResponse> queryResponses = neo4jService.findLinkageGraph(search);
		GraphResponseDTO graphResponseDTO = dTOToDomainTransformer.convertQueryResponseToGraphResponseDTO(search, queryResponses, new ArrayList<Long>());
		return new ResponseEntity<GraphResponseDTO>(graphResponseDTO, HttpStatus.OK);
	}
	
	@RequestMapping(method=RequestMethod.GET, path= {"/findFocusGraph"})
	public ResponseEntity<GraphResponseDTO> findFocusGraph(HttpServletRequest request, HttpServletResponse response, @RequestParam(required=true) String search, @RequestParam(required=true) String typeFilter, @RequestParam(required=true) Integer nodesNum) {

		List<QueryResponse> focusGraphIdQueryResponses = neo4jService.findFocusGraphIDQueryResponse(search, typeFilter, nodesNum);
		List<Long> ids = neo4jService.getIds(search, focusGraphIdQueryResponses, nodesNum);
		List<QueryResponse> queryResponses = neo4jService.getShortestPathQueryResponses(search, ids);
		GraphResponseDTO graphResponseDTO = dTOToDomainTransformer.convertQueryResponseToGraphResponseDTO(search, queryResponses, ids);
		return new ResponseEntity<GraphResponseDTO>(graphResponseDTO, HttpStatus.OK);
	}
}
