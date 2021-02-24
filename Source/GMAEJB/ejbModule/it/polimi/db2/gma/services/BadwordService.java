package it.polimi.db2.gma.services;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

import it.polimi.db2.gma.entities.BadWords;
import it.polimi.db2.gma.exceptions.AlreadyExistingBadwordException;
@Stateless
public class BadwordService {
	
	@PersistenceContext(unitName = "GMAEJB")
	private EntityManager em;
	
	public BadwordService() {}
	
	
	public void addBadword(BadWords badword) throws AlreadyExistingBadwordException{
		
		try {
			
			em.createNamedQuery("BadWords.checkBadword", BadWords.class).setParameter(1, badword.getBadword()).getSingleResult();
			
		}catch(NoResultException e) {
			
			em.persist(badword);
			return;
			
		}
		
		throw new AlreadyExistingBadwordException("this badword is already in the system!");
		
	}

}
