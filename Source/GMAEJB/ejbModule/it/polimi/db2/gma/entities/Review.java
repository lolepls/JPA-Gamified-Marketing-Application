package it.polimi.db2.gma.entities;

import java.io.Serializable;
import javax.persistence.*;

@Entity
@Table(name = "review", schema = "db_gma")
public class Review implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	private int product_id;
	
	private int player_id;
	
	private String text;
	
	/*Writes relationship: every review is written by a single user*/
	@ManyToOne
	@JoinColumn(name = "player_id", insertable = false, updatable =false)
	private Player user;
	
	/*Evaluates relationship: every review is associated to a single product*/
	@ManyToOne
	@JoinColumn(name = "product_id", insertable = false, updatable =false)
	private Product product;
	
	public Review() {}
	
	public Review(int productid, int playerid, String newtext) {
		this.product_id = productid;
		this.player_id = playerid;
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
	
	public int getPlayerId() {
		return this.player_id;
	}
	
	public void setProductId(int id) {
		this.product_id = id;
	}
	
	public void setText(String newtext) {
		this.text = newtext;
	}
	
	public void setPlayerId(int newplayerid) {
		this.player_id = newplayerid;
	}

	/**
	 * @return the user
	 */
	public Player getUser() {
		return user;
	}

}