package sg.gov.csit.knowledgeGraph.domain.Response;

import java.util.List;

public class PageRank {

	private Integer id;
	
	private Double score;
	
	private String name;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Double getScore() {
		return score;
	}

	public void setScore(Double score) {
		this.score = score;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public PageRank(Integer id, Double score, String name) {
		super();
		this.id = id;
		this.score = score;
		this.name = name;
	}
}
