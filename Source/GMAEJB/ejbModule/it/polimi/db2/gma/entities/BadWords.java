package it.polimi.db2.gma.entities;

import java.io.Serializable;
import javax.persistence.*;

@Entity
@NamedQueries({
	@NamedQuery(name = "BadWords.getAll", query = "SELECT b FROM BadWords b"),
	@NamedQuery(name = "BadWords.checkBadword", query = "SELECT b FROM BadWords b WHERE b.word = ?1")
})
@Table(name = "badwords", schema = "db_gma")
public class BadWords implements Serializable {
	private static final long serialVersionUID = 1L;
	
	
	@Id
	private String word;
	
	private String estabilished_by;
	
	/*Estabilish relationship: every badword is estabilished by an admin*/
	@ManyToOne
	@JoinColumn(name = "estabilished_by", insertable = false, updatable =false)
	private Administrator admin;
	
	public BadWords() {}
	
	public BadWords(String badword, String establishedby) {
		
		this.word = badword;
		this.estabilished_by = establishedby;
		
	}

	/**
	 * @return the badword
	 */
	public String getBadword() {
		return word;
	}

	/**
	 * @param badword the badword to set
	 */
	public void setBadword(String badword) {
		this.word = badword;
	}

	/**
	 * @return the establishedby
	 */
	public String getEstablishedby() {
		return estabilished_by;
	}

	/**
	 * @param establishedby the establishedby to set
	 */
	public void setEstablishedby(String establishedby) {
		this.estabilished_by = establishedby;
	}
	
	
}
