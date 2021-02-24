package it.polimi.db2.gma.entities;


import java.io.Serializable;
import java.util.List;

import javax.persistence.*;

@Entity
@Table(name = "questions", schema = "db_gma")
@NamedQueries({
	@NamedQuery(name = "Question.checkText", query = "SELECT q from Questions q WHERE q.text = ?1"),
	@NamedQuery(name = "Question.loadQuestions", query = "SELECT q from Questions q WHERE q.product_id = ?1")
	
})
public class Questions implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	private int product_id;
	
	private String text;
	
	/*Related to relationship: every question is related to a product*/
	@ManyToOne
	@JoinColumn(name = "product_id", insertable = false, updatable =false)
	private Product product;
	
	/*Associated to relationship: every question has several answers*/
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "question", cascade = CascadeType.ALL)
	private List<Answers> answers;
	
	public Questions() {}
	
	public Questions(int productid, String newtext) {
		this.product_id = productid;
		this.text = newtext;
	}
	
	public int getId() {
		return this.id;
	}
	
	public int getProductId() {
		return this.product_id;
	}
	
	public String getText() {
		return this.text;
	}
	
	public List<Answers> getAnswers(){
		return this.answers;
	}
	
	public void setProductId(int id) {
		this.product_id = id;
		
	}
	
	public void setText(String newtext) {
		this.text = newtext;
	}

	public void setProduct(Product product) {
		this.product = product;
	}
	

}
