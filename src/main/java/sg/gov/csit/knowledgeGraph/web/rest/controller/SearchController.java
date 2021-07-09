package sg.gov.csit.knowledgeGraph.web.rest.controller;

import java.util.ArrayList;
import java.util.Arrays;
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
	public ResponseEntity<GraphResponseDTO> getAll(HttpServletRequest request, HttpServletResponse response, @RequestParam(required=false) String search) {
		
		List<String> names = new ArrayList<String>(Arrays.asList(search.split(",")));
		List<QueryResponse> queryResponse = neo4jService.findAll();
		GraphResponseDTO graphResponseDTO = dTOToDomainTransformer.convertQueryResponseToGraphResponseDTO(names, queryResponse);
		return new ResponseEntity<GraphResponseDTO>(graphResponseDTO, HttpStatus.OK);
	}
	
	@RequestMapping(method=RequestMethod.GET, path= {"/findNeighbors"})
	public ResponseEntity<GraphResponseDTO> findNeighbors(HttpServletRequest request, HttpServletResponse response, @RequestParam(required=true) String search) {
		
		List<String> names = new ArrayList<String>(Arrays.asList(search.split(",")));
		List<QueryResponse> queryResponse = neo4jService.findNeighbors(names);
		GraphResponseDTO graphResponseDTO = dTOToDomainTransformer.convertQueryResponseToGraphResponseDTO(names, queryResponse);
		return new ResponseEntity<GraphResponseDTO>(graphResponseDTO, HttpStatus.OK);
	}
	
	@RequestMapping(method=RequestMethod.GET, path= {"/findGraph"})
	public ResponseEntity<GraphResponseDTO> findGraph(HttpServletRequest request, HttpServletResponse response, @RequestParam(required=true) String search, @RequestParam(defaultValue="1") Integer degree, @RequestParam(required=true) String blacklist) {
		
		//The Matrix Reloaded,The Matrix Revolutions,The Devil's Advocate,Taylor Hackford
		//Lana Wachowski,Lilly Wachowski
		//ACTED_IN
		
		List<String> names = new ArrayList<String>(Arrays.asList(search.split(",")));
		List<String> blacklisted = new ArrayList<String>(Arrays.asList(blacklist.split(",")));
		List<QueryResponse> queryResponse = neo4jService.findGraph(names, degree);
		GraphResponseDTO graphResponseDTO = dTOToDomainTransformer.convertQueryResponseToGraphResponseDTO(names, queryResponse);
		graphResponseDTO = neo4jService.findBlacklist(graphResponseDTO, blacklisted);
		return new ResponseEntity<GraphResponseDTO>(graphResponseDTO, HttpStatus.OK);
	}
}
