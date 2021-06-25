package sg.gov.csit.knowledgeGraph.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import sg.gov.csit.knowledgeGraph.dao.NodeRepository;
import sg.gov.csit.knowledgeGraph.domain.Unused.Node;

@Service
public class NodeService {
	
	@Autowired
	private NodeRepository nodeRepository;
	
	public Node insertOne(List<String> labels, String name, String title) {
		Node node = findOne(labels.get(0), name);
		if (node == null) {
			return nodeRepository.save(new Node(labels, name, title));
		}
		return node;
	}

	public Node findOne(Long id) {
		return nodeRepository.findById(id).orElse(null);
	}

	public Node findOne(String label, String name) {
		return nodeRepository.findByLabelAndName(label, name);
	}

	public List<Node> findAll() {
		return nodeRepository.findAll();
	}
	
	public Node deleteOne(Long id) {
		Node node = findOne(id);
		if (node != null) {
			nodeRepository.delete(node);
		}
		return node;
	}
	
	public Node deleteOne(String label, String name) {
		Node node = findOne(label, name);
		if (node != null) {
			nodeRepository.delete(node);
		}
		return node;
	}
	
	public void deleteAll() {
		nodeRepository.deleteAll();
	}
}
