package it.polimi.db2.gma.services;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import it.polimi.db2.gma.entities.Answers;
import it.polimi.db2.gma.entities.BadWords;
import it.polimi.db2.gma.entities.Player;
import it.polimi.db2.gma.entities.Product;
import it.polimi.db2.gma.entities.Questions;
import it.polimi.db2.gma.exceptions.CurseException;
import it.polimi.db2.gma.exceptions.NoPODException;

import javax.persistence.NoResultException;

@Stateless
public class AnswerService {
	@PersistenceContext(unitName = "GMAEJB")
	private EntityManager em;
	
	@EJB(name = "it.polimi.db2.gma.services/ProductService")
	ProductService productService;
	@EJB(name = "it.polimi.db2.gma.services/PlayerService")
	PlayerService playerService;
	
	
	public AnswerService() {}
	
	private boolean checkBadWords(String answer) {
		
		List<String> bannedWords = new ArrayList<String>();
		
		List<BadWords> badwords = em.createNamedQuery("BadWords.getAll", BadWords.class).getResultList();
		
		for(BadWords badword : badwords) {
			bannedWords.add(badword.getBadword());
		}
		
		for(String bannedword : bannedWords) {
			if (answer.contains(bannedword)) {
				return true;
			}
		}
		
		return false;
		
	}
		
	
	public void submitAnswers(List<String> answers_text, char sex, String explevel, int age, String user ) throws CurseException {
		
		int pointsGained = 0;
		Date today = new Date(System.currentTimeMillis());
		
		try{
			Product POD = productService.loadPOD(today);
			List<Questions> questions = POD.getQuestions();
			pointsGained = pointsGained + POD.getPointsGiven();
			
			Player player = null;
			
			//Ottengo l'utente
			try {
			player = em.createNamedQuery("Player.checkusername", Player.class).setParameter(1, user).getSingleResult();
			}
			catch(NoResultException e){
				System.out.println("Utente non trovato: " + user);
				return;
			}
	
			//Prima di tutto controllo che non ci siano parole proibite nel testo inviato dall'utente
			for(String answer : answers_text) {
				if(checkBadWords(answer)) {
					playerService.blockUser(player);
					throw new CurseException("A banned word was found in the answer!");
				}
			}
			
			int index = 0;
			
			for(Questions question : questions) {

				//System.out.println("Inserico risposta: " + answers_text.get(index));
				Answers newanswer = new Answers();
				
				newanswer.setText(answers_text.get(index));
				newanswer.setQuestionId(question.getId());
				newanswer.setPlayer_id(player.getId());
				
				newanswer.setSex(sex);
				
				
				if (explevel.equals("null")) {
					newanswer.setExp_level(null);
				}
				else {
					newanswer.setExp_level(explevel);
				}
				
				
				if (age == -1) {
					newanswer.setAge(0);
				}
				else {
					newanswer.setAge(age);
				}
				
				
				//Update delle relazioni tra domanda e risposta con i nuovi elementi
				newanswer.setQuestion(question);
				question.getAnswers().add(newanswer);
				
				//Associo il player
				newanswer.setPlayer(player);
				player.getAnswers().add(newanswer);
				
				
				//Inserisco le nuove risposte
				em.persist(newanswer);
				
				//Aumento l'indice
				index++;
				
			}
			
			//Conta dei punti facoltativi
			
			if (sex!='u') {
				pointsGained = pointsGained+2;
			}
			
			if (!explevel.equals("null")) {
				pointsGained = pointsGained+2;
			}
			
			if (age != -1) {
				pointsGained = pointsGained+2;
				
			}
			
			//Assegno i punti al player e lo inserisco nel db
			player.setScore(player.getScore() + pointsGained);
			em.persist(player);
		}
		
		catch(NoPODException e) {
			return;
		}
		
		
	}

		
		
	}
	
