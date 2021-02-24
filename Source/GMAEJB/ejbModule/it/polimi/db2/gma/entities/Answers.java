package it.polimi.db2.gma.entities;


import java.io.Serializable;
import javax.persistence.*;

@Entity
@Table(name = "answers", schema = "db_gma")
public class Answers implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	private int question_id;
	
	private String text;
	
	private char sex;
	
	private int age;
	
	private String exp_level;
	
	private int player_id;
	
	/*Gives relationship: every answer is given by a single user*/
	@ManyToOne
	@JoinColumn(name = "player_id" , insertable = false, updatable =false)
	private Player user;
	
	/*Associated to relationship: every answer is associated to a single question*/
	@ManyToOne
	@JoinColumn(name = "question_id" , insertable = false, updatable =false)
	private Questions question;
	
	
	public Answers() {}
	
	public Answers(int questionId, String text, char sex, int age, String exp_level, int playerid) {
		
		
		this.question_id = questionId;
		this.text = text;
		this.sex = sex;
		this.age = age;
		this.exp_level = exp_level;
		this.player_id = playerid;
		
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @return the questionId
	 */
	public int getQuestionId() {
		return question_id;
	}

	/**
	 * @param questionId the questionId to set
	 */
	public void setQuestionId(int questionId) {
		this.question_id = questionId;
	}

	/**
	 * @return the text
	 */
	public String getText() {
		return text;
	}

	/**
	 * @param text the text to set
	 */
	public void setText(String text) {
		this.text = text;
	}

	/**
	 * @return the sex
	 */
	public char getSex() {
		return sex;
	}

	/**
	 * @param sex the sex to set
	 */
	public void setSex(char sex) {
		this.sex = sex;
	}

	/**
	 * @return the age
	 */
	public int getAge() {
		return age;
	}

	/**
	 * @param age the age to set
	 */
	public void setAge(int age) {
		this.age = age;
	}

	/**
	 * @return the exp_level
	 */
	public String getExp_level() {
		return exp_level;
	}

	/**
	 * @param exp_level the exp_level to set
	 */
	public void setExp_level(String exp_level) {
		this.exp_level = exp_level;
	}

	/**
	 * @return the player_id
	 */
	public int getPlayer_id() {
		return player_id;
	}
	
	public void setPlayer(Player player) {
		this.user = player;
	}
	
	public Player getPlayer() {
		return this.user;
	}

	/**
	 * @param player_id the player_id to set
	 */
	public void setPlayer_id(int player_id) {
		this.player_id = player_id;
	}

	public void setQuestion(Questions question) {
		this.question = question;
		
	}

	public Questions getQuestion() {
		return this.question;
		
	}
	
	
	
}
