package it.polimi.db2.gma.services;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URLConnection;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

import it.polimi.db2.gma.entities.Answers;
import it.polimi.db2.gma.entities.Player;
import it.polimi.db2.gma.entities.Product;
import it.polimi.db2.gma.entities.Questions;
import it.polimi.db2.gma.exceptions.AlreadyExistingPODException;
import it.polimi.db2.gma.exceptions.ImageWrongFormatException;
import it.polimi.db2.gma.exceptions.IncorrectDateException;
import it.polimi.db2.gma.exceptions.NoPODException;
import it.polimi.db2.gma.exceptions.NoQuestionsException;
import it.polimi.db2.gma.exceptions.UnavailableDateException;

@Stateless
public class ProductService {
	@PersistenceContext(unitName = "GMAEJB")
	private EntityManager em;
	
	@EJB(name = "it.polimi.db2.gma.services/PlayerService")
	PlayerService playerService;
	
	public ProductService() {}
	
	public Product getProduct(int id) {
		Product product = em.find(Product.class, id);
		return product;
	}
	
	public Product loadPOD(Date today) throws NoPODException{
		
		Product POD = new Product();
		
		try {
			POD = em.createNamedQuery("Product.loadPOD", Product.class).setParameter(1, today).getSingleResult();
			return POD;
		}catch (NoResultException e) {
			throw new NoPODException("No POD found!");
		}
		
	}
	
	public void createPOD(Product POD) throws AlreadyExistingPODException, UnavailableDateException, IncorrectDateException, ImageWrongFormatException{
		
		Date today = new Date(System.currentTimeMillis());
		
		//the product date cannot be in the past
		if (POD.getDate().before(today)) {
			throw new IncorrectDateException("Incorrect Date");
		}
		
		//checks if the uploaded file is an image (.png)
		try{
			
			String contentType = URLConnection.guessContentTypeFromStream(new ByteArrayInputStream(POD.getImage()));
			if (contentType != "image/png") {
				throw new ImageWrongFormatException("Wrong Format, please upload an image (.png)");
			}
		}catch (IOException e) {
			throw new ImageWrongFormatException("Error in uploading the image");
		}


		
		//checks in the DB if there exists a product with the same name
		try {
			em.createNamedQuery("Product.checkName", Product.class).setParameter(1, POD.getName()).getSingleResult();
			}
			catch(NoResultException e) {
				//checks in the DB if the selected POD date has already been selected for another product
				try {
					em.createNamedQuery("Product.loadPOD", Product.class).setParameter(1, POD.getDate()).getSingleResult();
					
					}
					catch(NoResultException e1) {
				
						//if there is no product with the same name or date we can insert our new POD in the DB
						em.persist(POD);
						return;
					}
				throw new UnavailableDateException("Incorrect Date");

				
			}
		
		throw new AlreadyExistingPODException("this product is already in the system");
		
		
	}

	public void addPoints(int productID) throws NoPODException{
		
		Product POD = new Product();
		
		try {
			POD = em.createNamedQuery("Product.loadPODfromID", Product.class).setParameter(1, productID).getSingleResult();
		}catch (NoResultException e) {
			throw new NoPODException("No POD found!");
		}
		
		
		List<Questions> questions = null;
		questions = em.createNamedQuery("Question.loadQuestions", Questions.class).setParameter(1, productID).getResultList();
		
		int points = questions.size();
		
		POD.setPointsgiven(points);
		
		em.merge(POD);
		
		
	}

	public List<Product> getProductsForDeletion(Date today){
		
		List<Product> deletableProducts = new ArrayList<Product>();
		
		deletableProducts = em.createNamedQuery("Product.getAllPastProducts", Product.class).setParameter(1, today).getResultList();
		
		//Se un prodotto non ha un questionario, non compare tra quelli eliminabili
		List<Product> noQuestionnaires = new ArrayList<Product>();
		
		for(Product p : deletableProducts) {
			if(p.getQuestions().isEmpty()) {
				noQuestionnaires.add(p);
			}
		}
		
		deletableProducts.removeAll(noQuestionnaires);
		
		return deletableProducts;
		
	}
	
	public void deleteQuestionnaire(Product product_unmanaged) {
		
		//Ottengo il product in modo che sia managed:
		Product product = em.find(Product.class, product_unmanaged.getId());
		
		
		//Ottengo una lista di player: sono quelli che hanno risposto al questionario.
		//Prima di tutto quindi ricavo tutti i player:
		List<Player> allplayers = em.createNamedQuery("Player.findAll", Player.class).getResultList();
		
		//Qui dentro aggiungo solamente quelli che hanno risposto per questo prodotto
		List<Player> players = new ArrayList<Player>();
		for(Player p : allplayers) {
			if(playerService.hasAnswered(p, product)) {
				players.add(p);
			}
		}
		
		//Scalo i punteggi dati dalla rimozione del marketing questionnaire:
		for(Player p : players) {
			p.setScore(p.getScore() - product.getPointsgiven());
		}
		
		//Ora per ogni player scalo i punteggi dinamici dello statisticalQuestionnaire
		for(Player p : players) {
			List<Answers> answers = playerService.getPlayerPODAnswers(p, product);
			Answers oneanswer = answers.get(0);
			
			char sex = oneanswer.getSex();
			int age = oneanswer.getAge();
			String explevel = oneanswer.getExp_level();
			
			if(sex!='u') {
				p.setScore(p.getScore() - 2);
			}
			
			if(age!=0) {
				p.setScore(p.getScore() - 2);
			}
			
			if(explevel != null) {
				p.setScore(p.getScore() - 2);
			}
			
		}
		
		//Ad ogni player rimuovo le risposte alle domande
		for(Player p : players) {
			//Questa � una lista di answers che sono semplici oggetti Java
			List<Answers> answers = playerService.getPlayerPODAnswers(p, product);
			
			for(Answers answer : answers) {
				//Ottengo cos� ogni istanza di answers del DB:
				Answers answermanaged = em.find(Answers.class, answer.getId());
				p.getAnswers().remove(answermanaged);
				em.remove(answermanaged);
			}
		}
		
		//Ora devo rimuovere le domande:
		//Innanzitutto si ottengono le domande associate al prodotto come semplici oggetti Java:
		List<Questions> questions = product_unmanaged.getQuestions();
		
		//Con un ciclo su queste domande, ne ottengo dal DB l'istanza con em.find in modo che siano managed
		for(Questions question : questions) {
			
			Questions questionmanaged = em.find(Questions.class, question.getId());
			//Rimuovo dal prodotto la domanda e poi chiamo l'EM
			product.getQuestions().remove(questionmanaged);
			em.remove(questionmanaged);
		}
		
		//Setto a 0 i pointsgiven del product
		product.setPointsgiven(0);
		
		//persisto ogni player
		for(Player p : players) {
			em.persist(p);
		}
		
		//Flush dell'EM per scrivere tutte le modifiche
		em.flush();
		
	}

	public Product checkDate(Date date) throws IncorrectDateException, NoPODException, NoQuestionsException{
		
		Date yesterday = new Date(System.currentTimeMillis()-24*60*60*1000);
		
		//the product date has to be in the past
		if (date.after(yesterday)) {
			throw new IncorrectDateException("Incorrect Date");
		}
		
		Product POD = new Product();
		try {
			POD = em.createNamedQuery("Product.loadPOD", Product.class).setParameter(1, date).getSingleResult();
		}catch (NoResultException e) {
			throw new NoPODException("No POD found!");
			
		}
		
		if (POD.getQuestions().isEmpty() || POD.getQuestions() == null) {
			
			throw new NoQuestionsException("No questions for the inserted date!");
			
		}
		
		return POD;
	}
	

	
}
	
	
	