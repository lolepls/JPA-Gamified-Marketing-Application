package it.polimi.db2.gma.entities;

import java.io.Serializable;
import javax.persistence.*;
import java.sql.Date;
import java.sql.Time;

@Entity
@Table(name = "session", schema = "db_gma")
@NamedQuery(name = "Session.getPlayersWhoCancelled", query = "SELECT s FROM Session s WHERE s.logdate = ?1 and s.hascancelled = true")
public class Session implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	private int player_id;
	private Date logdate;
	private Time logtime;
	private boolean hascancelled;
	
	/*Starts relationship: there can be many sessions associated to a single user*/
	@ManyToOne
	@JoinColumn(name = "player_id", insertable = false, updatable =false)
	private Player user;
	
	public Session() {}
	
	public Session(int playerid, Date date, Time logtime, boolean hascancelled) {
		this.player_id = playerid;
		this.logdate = date;
		this.logtime = logtime;
		this.hascancelled = hascancelled;
	}
	
	
	public Date getDate() {
		return this.logdate;
	}
	
	public Time getTime() {
		return this.logtime;
	}
	
	public int getId() {
		return this.id;
	}
	
	
	public void setDate(Date date) {
		this.logdate = date;
	}
	
	public void setTime(Time time) {
		this.logtime = time;
	}

	/**
	 * @return the hascancelled
	 */
	public boolean isHascancelled() {
		return hascancelled;
	}

	/**
	 * @param hascancelled the hascancelled to set
	 */
	public void setHascancelled(boolean hascancelled) {
		this.hascancelled = hascancelled;
	}

	/**
	 * @return the player_id
	 */
	public int getPlayer_id() {
		return player_id;
	}

	/**
	 * @param player_id the player_id to set
	 */
	public void setPlayer_id(int player_id) {
		this.player_id = player_id;
	}
	
	
	
}