package sg.gov.csit.knowledgeGraph.web.rest.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1")
public class TestEndpointController {

	@RequestMapping(method=RequestMethod.GET, path= {"/test"})
	public ResponseEntity<String> testEndpoint(HttpServletRequest request, HttpServletResponse response) {
		
		return new ResponseEntity<String>("Connected", HttpStatus.OK);
	}
}
