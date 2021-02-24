package it.polimi.db2.gma.services;


import javax.ejb.Stateless;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

import it.polimi.db2.gma.entities.Product;
import it.polimi.db2.gma.entities.Questions;
import it.polimi.db2.gma.exceptions.AlreadyExistingQuestionException;
import it.polimi.db2.gma.exceptions.NoPODException;

@Stateless
public class QuestionService {
	
	@PersistenceContext(unitName = "GMAEJB")
	private EntityManager em;
	
	public QuestionService() {}
	
	
	
	public void addQuestion(Questions question) throws NoPODException, AlreadyExistingQuestionException{
			
		
		try {
			em.createNamedQuery("Product.loadPODfromID", Product.class).setParameter(1, question.getProductId()).getSingleResult();
		}catch (NoResultException e) {
			throw new NoPODException("No POD found!");
		}
		
		try {
			em.createNamedQuery("Question.checkText", Questions.class).setParameter(1, question.getText()).getSingleResult();
		}catch (NoResultException e) {
			em.persist(question);

		}
			
		
		throw new AlreadyExistingQuestionException("This question is already in the system!");

		
		
		
		
			
			
	}

}
