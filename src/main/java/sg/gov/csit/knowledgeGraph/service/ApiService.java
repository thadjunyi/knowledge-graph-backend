package sg.gov.csit.knowledgeGraph.service;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import sg.gov.csit.knowledgeGraph.domain.Request.QueryRequest;
import sg.gov.csit.knowledgeGraph.domain.Request.Statement;
import sg.gov.csit.knowledgeGraph.domain.Response.Data;
import sg.gov.csit.knowledgeGraph.domain.Response.QueryResponse;
import sg.gov.csit.knowledgeGraph.domain.Response.Result;

@Service
public class ApiService {
	
	private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ApiService.class);
	
	@Value("${spring.neo4j.server.uri}")
	String neo4jServerUri;
	
	@Value("${spring.data.neo4j.username}")
	String neo4jUsername;
	
	@Value("${spring.data.neo4j.password}")
	String neo4jPassword;
	
	@Autowired
	ObjectMapper objectMapper;
	
	@Autowired
	ModelMapper modelMapper;
	
	public <T> QueryResponse queryWithoutCommit(List<String> queryStatements) {
				
		String url = neo4jServerUri;
		return query(url, queryStatements);
	}
	
	public <T> QueryResponse queryWithCommit(List<String> queryStatements) {
				
		String url = neo4jServerUri + "/commit";
		return query(url, queryStatements);
	}
	
	public <T> QueryResponse query(String url, List<String> queryStatements) {
		
		QueryResponse queryResponse = new QueryResponse();
		
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.setBasicAuth(neo4jUsername, neo4jPassword);
		headers.setContentType(MediaType.APPLICATION_JSON);
		
		QueryRequest queryRequest = getQueryRequest(queryStatements);
			
		try {
			ResponseEntity<QueryResponse> response = restTemplate.exchange(
					url, 
					HttpMethod.POST,
					new HttpEntity<>(queryRequest, headers), 
					new ParameterizedTypeReference<QueryResponse>() {}
			);
//			logger.info("Method: {}, Url: {}, Status: {}", "GET", url, response.getStatusCode());
			queryResponse = getQueryResponse(response);
			
		} catch (ResourceAccessException e) {
			logger.debug("Unable to connect to {}:", url, e);
		}
		
//		printResult(queryResponse);
		return queryResponse;
	}
	
	public QueryRequest getQueryRequest(List<String> queryStatements) {
		
		QueryRequest queryRequest = new QueryRequest();
		List<String> resultDataContents = new ArrayList<String>();
		resultDataContents.add("graph");

		List<Statement> statements = new ArrayList<Statement>();
		for (String queryStatement : queryStatements) {
			Statement statement = new Statement();
			statement.setStatement(queryStatement);
			statement.setResultDataContents(resultDataContents);
			statements.add(statement);
		}
		queryRequest.setStatements(statements);
		return queryRequest;
	}
	
	public QueryResponse getQueryResponse(ResponseEntity<QueryResponse> response) {
		
		java.lang.reflect.Type targetType = new TypeToken<QueryResponse>() {}.getType();
		QueryResponse queryResponse =  modelMapper.map(response.getBody(), targetType);
		return queryResponse;
	}
	
	public void printResult(QueryResponse queryResponse) {
		
		for (Result result : queryResponse.getResults()) {
			for (Data data : result.getData()) {
				try {
					String messageJsonString = objectMapper.writeValueAsString(data.getGraph());
					System.out.println(messageJsonString);
				} catch (JsonProcessingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
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
