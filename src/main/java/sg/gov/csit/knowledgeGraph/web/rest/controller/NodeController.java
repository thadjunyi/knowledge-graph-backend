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
@RequestMapping("/v1/node")
public class NodeController {
	
	@Autowired
	private NodeService nodeService;
	
	@Autowired
	private Neo4jService neo4jService;
	
	@Autowired
	DTOToDomainTransformer dTOToDomainTransformer;
	
	@RequestMapping(method=RequestMethod.POST, path= {"/insert"})
	public ResponseEntity<Node> insertNode(HttpServletRequest request, HttpServletResponse response, @RequestParam(required=true) List<String> labels, @RequestParam(required=true) String name, @RequestParam(required=false) String title) {
		
		HttpStatus httpStatus = HttpStatus.OK;
		System.out.println(labels);
		Node node = nodeService.insertOne(labels, name, title);
		if (node == null) {
			httpStatus = HttpStatus.FORBIDDEN;
		}
		return new ResponseEntity<Node>(node, httpStatus);
	}

	@RequestMapping(method=RequestMethod.GET, path= {"/getById"})
	public ResponseEntity<Node> getNodeById(HttpServletRequest request, HttpServletResponse response, @RequestParam(required=true) Long id) {
		
		HttpStatus httpStatus = HttpStatus.OK;
		Node node = nodeService.findOne(id);
		if (node == null) {
			httpStatus = HttpStatus.NOT_FOUND;
		}
		return new ResponseEntity<Node>(node, httpStatus);
	}

	@RequestMapping(method=RequestMethod.GET, path= {"/get"})
	public ResponseEntity<Node> getNode(HttpServletRequest request, HttpServletResponse response, @RequestParam(required=true) String label, @RequestParam(required=true) String name) {
		
		HttpStatus httpStatus = HttpStatus.OK;
		Node node = nodeService.findOne(label, name);
		if (node == null) {
			httpStatus = HttpStatus.NOT_FOUND;
		}
		return new ResponseEntity<Node>(node, httpStatus);
	}

	@RequestMapping(method=RequestMethod.GET, path= {"/getAll"})
	public ResponseEntity<GraphResponseDTO> getAll(HttpServletRequest request, HttpServletResponse response) {
		
//		List<Node> entities = nodeService.findAll();
//		return new ResponseEntity<List<Node>>(entities, HttpStatus.OK);
		QueryResponse queryResponse = neo4jService.findAll();
		GraphResponseDTO graphResponseDTO = dTOToDomainTransformer.convertQueryResponseToGraphResponseDTO(queryResponse);
		return new ResponseEntity<GraphResponseDTO>(graphResponseDTO, HttpStatus.OK);
	}

	@RequestMapping(method=RequestMethod.DELETE, path= {"/deleteById"})
	public ResponseEntity<?> deleteNodeById(HttpServletRequest request, HttpServletResponse response, @RequestParam(required=true) Long id) {
		
		HttpStatus httpStatus = HttpStatus.OK;
		Node node = nodeService.deleteOne(id);
		if (node == null) {
			httpStatus = HttpStatus.NOT_FOUND;
		}
		return new ResponseEntity<Node>(node, httpStatus);
	}

	@RequestMapping(method=RequestMethod.DELETE, path= {"/delete"})
	public ResponseEntity<?> deleteNode(HttpServletRequest request, HttpServletResponse response, @RequestParam(required=true) String label, @RequestParam(required=true) String name) {
		
		HttpStatus httpStatus = HttpStatus.OK;
		Node node = nodeService.deleteOne(label, name);
		if (node == null) {
			httpStatus = HttpStatus.NOT_FOUND;
		}
		return new ResponseEntity<Node>(node, httpStatus);
	}

	@RequestMapping(method=RequestMethod.DELETE, path= {"/deleteAll"})
	public ResponseEntity<?> deleteAllNode(HttpServletRequest request, HttpServletResponse response) {
		
		nodeService.deleteAll();
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
