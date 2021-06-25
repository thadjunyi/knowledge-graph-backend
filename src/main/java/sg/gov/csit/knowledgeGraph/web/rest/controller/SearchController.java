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
import sg.gov.csit.knowledgeGraph.domain.Unused.Node;
import sg.gov.csit.knowledgeGraph.service.Neo4jService;
import sg.gov.csit.knowledgeGraph.service.NodeService;
import sg.gov.csit.knowledgeGraph.web.dto.response.GraphResponseDTO;
import sg.gov.csit.knowledgeGraph.web.transformer.DTOToDomainTransformer;

@RestController
@RequestMapping("/v1/search")
public class SearchController {
	
	@Autowired
	private NodeService nodeService;
	
	@Autowired
	private Neo4jService neo4jService;
	
	@Autowired
	DTOToDomainTransformer dTOToDomainTransformer;
	
	@RequestMapping(method=RequestMethod.POST, path= {""})
	public ResponseEntity<?> search(HttpServletRequest request, HttpServletResponse response) {
		
		HttpStatus httpStatus = HttpStatus.OK;
		return new ResponseEntity<>("result is out", httpStatus);
	}
}
