package it.polimi.db2.gma.entities;

import java.io.Serializable;
import javax.persistence.*;
import java.sql.Date;
import java.util.List;

@Entity
@Table(name = "product", schema = "db_gma")
@NamedQueries({
	
	@NamedQuery(name = "Product.loadPOD", query = "SELECT p FROM Product p WHERE p.PODdate = ?1"),
	@NamedQuery(name = "Product.checkName", query = "SELECT p FROM Product p WHERE p.name = ?1"),
	@NamedQuery(name = "Product.loadPODfromID" , query = "SELECT p FROM Product p WHERE p.id = ?1"),
	@NamedQuery(name = "Product.getAllPastProducts", query = "SELECT p FROM Product p WHERE p.PODdate < ?1")

})
public class Product implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	private String name;
	
	private byte[] image;
	
	private Date PODdate;
	
	private int pointsgiven;
	
	/*Evaluates relationship: every product has a list of reviews*/
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "product", cascade = CascadeType.ALL)
	private List<Review> reviews;
	
	/*Related to relationship: every product has a list of questions*/
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "product", cascade = CascadeType.ALL)
	private List<Questions> questions;
	
	public Product() {}
	
	public Product(String name, byte[] image, Date date, int pointsgiven) {
		
		this.name = name;
		this.image = image;
		this.PODdate = date;
		this.pointsgiven = pointsgiven;
		
	}
	
	public int getId(){
		return this.id;
	}
	
	public String getName() {
		return this.name;
	}
	
	public byte[] getImage(){
		
		return this.image;
	}
	
	public Date getDate() {
		return this.PODdate;
	}
	
	public void setName(String newname) {
		this.name = newname;
	}
	
	public void setDate (Date newdate) {
		this.PODdate = newdate;
	}
	
	public void setImage (byte[] newimage) {
		this.image = newimage;
	}

	
	public int getPointsgiven() {
		return pointsgiven;
	}

	
	public void setPointsgiven(int pointsgiven) {
		this.pointsgiven = pointsgiven;
	}

	/**
	 * @return the reviews
	 */
	public List<Review> getReviews() {
		return reviews;
	}

	/**
	 * @param adds a review
	 */
	public void AddReview(Review review) {
		this.getReviews().add(review);
	}

	/**
	 * @return the questions
	 */
	public List<Questions> getQuestions() {
		return questions;
	}

	/**
	 * @param questions the questions to set
	 */
	public void setQuestions(Questions question) {
		this.getQuestions().add(question);
	}
	
	
	public int getPointsGiven() {
		return this.pointsgiven;
	}
	
	
}

