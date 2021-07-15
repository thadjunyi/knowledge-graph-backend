package sg.gov.csit.knowledgeGraph.web.rest.controller;

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
	public ResponseEntity<GraphResponseDTO> getAll(HttpServletRequest request, HttpServletResponse response, @RequestParam(required=false) String search, @RequestParam(required=false) String filter) {
		
		List<QueryResponse> queryResponse = neo4jService.findAll();
		GraphResponseDTO graphResponseDTO = dTOToDomainTransformer.convertQueryResponseToGraphResponseDTO(search, queryResponse);
		graphResponseDTO = neo4jService.findFilters(graphResponseDTO, filter);
		return new ResponseEntity<GraphResponseDTO>(graphResponseDTO, HttpStatus.OK);
	}
	
	@RequestMapping(method=RequestMethod.GET, path= {"/findNeighbors"})
	public ResponseEntity<GraphResponseDTO> findNeighbors(HttpServletRequest request, HttpServletResponse response, @RequestParam(required=true) String search) {
		
		List<QueryResponse> queryResponse = neo4jService.findNeighbors(search);
		GraphResponseDTO graphResponseDTO = dTOToDomainTransformer.convertQueryResponseToGraphResponseDTO(search, queryResponse);
		return new ResponseEntity<GraphResponseDTO>(graphResponseDTO, HttpStatus.OK);
	}
	
	@RequestMapping(method=RequestMethod.GET, path= {"/findSearchGraph"})
	public ResponseEntity<GraphResponseDTO> findSearchGraph(HttpServletRequest request, HttpServletResponse response, @RequestParam(required=true) String search, @RequestParam(defaultValue="1") Integer degree, @RequestParam(required=true) String filter) {
		
		List<QueryResponse> queryResponse = neo4jService.findSearchGraph(search, degree);
		GraphResponseDTO graphResponseDTO = dTOToDomainTransformer.convertQueryResponseToGraphResponseDTO(search, queryResponse);
		graphResponseDTO = neo4jService.findFilters(graphResponseDTO, filter);
		return new ResponseEntity<GraphResponseDTO>(graphResponseDTO, HttpStatus.OK);
	}
	
	@RequestMapping(method=RequestMethod.GET, path= {"/findPageRankGraph"})
	public ResponseEntity<GraphResponseDTO> findPageRankGraph(HttpServletRequest request, HttpServletResponse response, @RequestParam(required=true) String search, @RequestParam(required=true) String filter) {
		
		GraphResponseDTO graphResponseDTO = neo4jService.findPageRankGraph(search);
		graphResponseDTO = neo4jService.findFilters(graphResponseDTO, filter);
		return new ResponseEntity<GraphResponseDTO>(graphResponseDTO, HttpStatus.OK);
	}
}
