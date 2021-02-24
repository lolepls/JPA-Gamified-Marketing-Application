package it.polimi.db2.gma.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.NoResultException;

import it.polimi.db2.gma.entities.Player;
import it.polimi.db2.gma.entities.Product;
import it.polimi.db2.gma.entities.Questions;
import it.polimi.db2.gma.entities.Answers;
import it.polimi.db2.gma.exceptions.*;


@Stateless
public class PlayerService {
	@PersistenceContext(unitName = "GMAEJB")
	private EntityManager em;
	
	@EJB(name = "it.polimi.db2.gma.services/AnswerService")
	AnswerService answerService;
	
	public PlayerService() {}
	
	public Player checkAuthentication(String username, String pwd) throws WrongCredentialsException {
		Player p = null;
		try {
		p = em.createNamedQuery("Player.checkCredentials", Player.class).setParameter(1, username).setParameter(2, pwd)
				.getSingleResult();
		
		}catch(NoResultException e) {
			
			throw new WrongCredentialsException("Invalid username or password");
		}
		
		return p;
	}
	
	//This method must be used only if the player is already authenticated. It is used in LoadHomePage.
	public Player retrievePlayer(String username) {
		
		Player player = null;
		
		try {
		player = em.createNamedQuery("Player.checkusername", Player.class).setParameter(1, username).getSingleResult();
		}
		catch(NoResultException e){
			return player;
		}
		
		return player;
	}
		
	
	public void createPlayer(String username, String pwd, String email) throws AlreadyExistingUserException, AlreadyExistingEmailException {
		
		//Let's check if there is another player with the same username
		@SuppressWarnings("unused")
		Player check = null;
		try {
		check = em.createNamedQuery("Player.checkusername", Player.class).setParameter(1, username).getSingleResult();
		}
		catch(NoResultException e){
		
		//Let's use a query to check if there is another player with the same email
			try {
					check = em.createNamedQuery("Player.checkEmail", Player.class).setParameter(1, email).getSingleResult();
			}
			//If you catch this exception means that there is no player with the same email
			catch(NoResultException e1) {
	
				Player p = new Player(username, pwd, email, false, 0);
				em.persist(p);
				return;
			
			}
		//If you did not catch the exception, it means that the email was already taken
				throw new AlreadyExistingEmailException("The chosen email is unavailable");
		}
		
		//If you did not catch the exception, it means that the username was already taken
		throw new AlreadyExistingUserException("The chosen username is unavailable");
			
		
	
	
	}
 
	public void blockUser(Player player) {
	
		player.setBlocked(true);
		em.persist(player);
	
	}
	
	//Restituisce tutte le risposte che un certo player ha dato ad un certo POD
	public List<Answers> getPlayerPODAnswers(Player player, Product POD) {
		
		//Ricavo le domande associate al POD
		List <Questions> questions = POD.getQuestions();
		
		//Questa lista conterrà le risposte dell'utente alle domande del POD di oggi
		List<Answers> playerPODAnswers = new ArrayList<Answers>();
		
		//Queste sono tutte le risposte che il player ha dato
		List<Answers> playerAnswers = player.getAnswers();
		
		//Adesso cerco, tra tutte le risposte del player,se alcune sono associate alle domande di oggi. 
		//Nel caso, le aggiungo alla lista di risposte di oggi.
		
		for(Answers answer : playerAnswers) {
			
				for(Questions question : questions) {
				
					if(answer.getQuestion().getId() == question.getId()) {
						playerPODAnswers.add(answer);
					}
				}
			}
		
		return playerPODAnswers;
		
		
		
	}

	public boolean hasAnswered(Player player, Product POD) {
		
		List<Answers> answers = getPlayerPODAnswers(player, POD);
		if(answers.isEmpty()) {
			return false;
		}
		return true;
		
	}
	
	//Restituisce tutti i player che hanno risposto al questionario del POD, già ordinati per punteggio decrescente.
	public List<Player> getLeaderboardPlayers(Product POD){
		
		//Ricavo tutti i player
		List<Player> players = em.createNamedQuery("Player.findAll", Player.class).getResultList();
		List<Player> leaderboardPlayers = new ArrayList<Player>();
		
		//Cerco quelli che hanno risposto al POD selezionato
		for(Player player : players) {
			
			if(hasAnswered(player, POD)) {
					leaderboardPlayers.add(player);
			}
			
		}
		
		// Sorting effettuato con l'override del compare. 
		// Ho deciso di farlo qui e non di implementare "comparable" nella classe player perché l'unico caso
		// in cui si vogliono comparare due player è quando si mostra la classifica.
		
		Collections.sort(leaderboardPlayers, new Comparator<Player>() {
		        @Override
		        public int compare(Player player1, Player player2)
		        {
		        	int score1 = player1.getScore();
		        	int score2 = player2.getScore();
		        	
		        	if(score1>score2) {
		        		return 1;
		        	}
		        	
		        	if(score1<score2) {
		        		return -1;
		        	}
		        	
		        	return 0;
		            
		        }
		    });
		
		Collections.reverse(leaderboardPlayers);
		
		return leaderboardPlayers;
		
		
	}
}


