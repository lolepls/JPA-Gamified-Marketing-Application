package it.polimi.db2.gma.entities;


import java.io.Serializable;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "player", schema = "db_gma")
@NamedQueries({
	
	@NamedQuery(name = "Player.checkCredentials", query = "SELECT p FROM Player p WHERE p.username = ?1 AND p.pwd = ?2"),
	@NamedQuery(name = "Player.checkEmail", query = "SELECT p FROM Player p WHERE p.email = ?1"),
	@NamedQuery(name = "Player.checkusername", query = "SELECT p FROM Player p WHERE p.username = ?1"),
	@NamedQuery(name = "Player.findAll", query = "SELECT p FROM Player p"),
	@NamedQuery(name = "Player.findById", query = "SELECT p FROM Player p WHERE p.id = ?1")
})
public class Player implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	
	private String username;
	
	private String pwd;
	
	private String email;
	
	private Boolean blocked;
	
	private int score;
	
	/*Starts relationship: every user can be associated to many sessions*/
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "user", cascade = CascadeType.ALL)
	private List<Session> sessions;
	
	/*Gives relationship: every user can give several answers*/
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "user", cascade = CascadeType.REFRESH)
	private List<Answers> answers;
	
	/*Writes relationship: every user can write several reviews*/
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "user", cascade = CascadeType.ALL)
	private List<Review> reviews;
	
	public Player() {}
	
	public Player(String username, String password, String email, Boolean blocked, int score) {
		
		this.username = username;
		this.pwd = password;
		this.email = email;
		this.blocked = blocked;
		this.score = score;
	}

	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @param username the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return pwd;
	}

	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.pwd = password;
	}

	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * @return the blocked
	 */
	public Boolean isBlocked() {
		return blocked;
	}

	/**
	 * @param blocked the blocked to set
	 */
	public void setBlocked(Boolean blocked) {
		this.blocked = blocked;
	}

	/**
	 * @return the score
	 */
	public int getScore() {
		return score;
	}

	/**
	 * @param score the score to set
	 */
	public void setScore(int score) {
		this.score = score;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	public List<Answers> getAnswers(){
		return this.answers;
	}
	
	public List<Session> getSessions(){
		return this.sessions;
	}

}
