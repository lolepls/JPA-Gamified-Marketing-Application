package it.polimi.db2.gma.services;

import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

import it.polimi.db2.gma.entities.Player;
import it.polimi.db2.gma.entities.Session;


@Stateless
public class SessionService {
	@PersistenceContext(unitName = "GMAEJB")
	private EntityManager em;
	
	@EJB(name = "it.polimi.db2.gma.services/PlayerService")
	PlayerService playerService;
	
	public SessionService() {}
	
	public Session createSession(Player player) {
		Session newsession = new Session();
		Date today =  new Date(System.currentTimeMillis());
		Time now = new Time(System.currentTimeMillis());
		newsession.setPlayer_id(player.getId());
		newsession.setDate(today);
		newsession.setTime(now);
		newsession.setHascancelled(false);
		
		em.persist(newsession);
		return newsession;
	}

	public void setSessionCancelled(Player player, int sessionId) {
		
		//Ricavo tutte le sessioni del player
		List<Session> playerSessions = player.getSessions();
		
		//Imposto il flag a true per la sessione corrente
		for(Session session : playerSessions) {
			
			if(session.getId() == sessionId) {
				session.setHascancelled(true);
				em.persist(session);
			}
		}
		
		
	}
	
	public List<Player> getPlayersWhoCancelled(Date date) {
		
		List<Session> cancelledSessionsList = null;
		//checks if, for the selected date, there are players who cancelled their questionnaire

		List<Player> playersWhoCancelled = new ArrayList<>();
		try {
			cancelledSessionsList = em.createNamedQuery("Session.getPlayersWhoCancelled", Session.class).setParameter(1, date).getResultList();

		}catch (NoResultException e) {

			return playersWhoCancelled;
			
		}
		
		List<Integer> idList = new ArrayList<>();
		//collects the IDs of those players
		for (Session s:cancelledSessionsList) {
			
			if (!idList.contains(s.getPlayer_id())) {
				
				idList.add(s.getPlayer_id());
				
			}
			
			
		}
		
		//gets those players from their id
		for (int id:idList) {
			
			playersWhoCancelled.add(em.createNamedQuery("Player.findById", Player.class).setParameter(1, id).getSingleResult());
			
		}
		
		return playersWhoCancelled;
	}
}

