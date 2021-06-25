package sg.gov.csit.knowledgeGraph.dao;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Service;

import sg.gov.csit.knowledgeGraph.domain.Unused.Edge;

@Service
public interface EdgeRepository extends Neo4jRepository<Edge, Long> {

	Edge findByLabel(String label);
}