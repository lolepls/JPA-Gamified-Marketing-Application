package it.polimi.db2.gma.entities;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "administrator", schema = "db_gma")
@NamedQuery(name = "Administrator.checkCredentials", query = "SELECT a FROM Administrator a WHERE a.username = ?1 AND a.pwd = ?2")

public class Administrator implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	
	private String username;
	
	private String pwd;
	
	private String email;
	
	/*Estabilish relationship: every admin can set several badwords*/
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "admin", cascade = CascadeType.REFRESH)
	private List<BadWords> badwords;
	
	public Administrator() {}
	
	public Administrator(String username, String pwd, String email) {
		
		this.username = username;
		this.pwd = pwd;
		this.email = email;
	}
	
	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the badwords
	 */
	public List<BadWords> getBadwords() {
		return badwords;
	}

	/**
	 * @param badwords the badwords to set
	 */
	public void setBadwords(List<BadWords> badwords) {
		this.badwords = badwords;
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
	public String getPwd() {
		return pwd;
	}

	/**
	 * @param password the password to set
	 */
	public void setPwd(String pwd) {
		this.pwd = pwd;
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
	
}
