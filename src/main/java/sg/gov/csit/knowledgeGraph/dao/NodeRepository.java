package sg.gov.csit.knowledgeGraph.dao;

import java.util.List;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Service;

import sg.gov.csit.knowledgeGraph.domain.Unused.Node;

@Service
public interface NodeRepository extends Neo4jRepository<Node, Long> {
	
	@Query("MATCH(node) WHERE ($label in labels(node)) RETURN node")
	List<Node> findByLabel(String label);
//	List<Entity> findByLabel(String label);

	@Query("MATCH(node) WHERE ($label in labels(node) AND node.name = $name) RETURN node")
	Node findByLabelAndName(String label, String name);
//	Entity findByLabelAndName(String label, String name);
	
//	Node save(Node node);
}