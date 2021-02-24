package it.polimi.db2.controller;

import java.io.IOException;
import java.sql.Date;
import java.util.Base64;
import java.util.List;

import javax.ejb.EJB;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import it.polimi.db2.gma.entities.Player;
import it.polimi.db2.gma.entities.Product;
import it.polimi.db2.gma.entities.Review;
import it.polimi.db2.gma.exceptions.NoPODException;
import it.polimi.db2.gma.services.PlayerService;
import it.polimi.db2.gma.services.ProductService;


/**
 * Servlet implementation class HomePageController
 */
@WebServlet("/LoadHomePage")
public class LoadHomePage extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	
	@EJB(name = "it.polimi.db2.gma.services/ProductService")
	private ProductService productService;
	@EJB(name = "it.polimi.db2.gma.services/PlayerService")
	private PlayerService playerService;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public LoadHomePage() {
        super();
        // TODO Auto-generated constructor stub
    }
    
    
    public void init() throws ServletException {
		ServletContext servletContext = getServletContext();
		ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(servletContext);
		templateResolver.setTemplateMode(TemplateMode.HTML);
		this.templateEngine = new TemplateEngine();
		this.templateEngine.setTemplateResolver(templateResolver);
		templateResolver.setSuffix(".html");
	}
    

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String loginpath = getServletContext().getContextPath() + "/index.html";
		HttpSession session = request.getSession();
		if (session.isNew() || session.getAttribute("user") == null) {
			response.sendRedirect(loginpath);
			return;
		}
		
		//carico il prodotto del giorno
		
		String productName = null;
		byte[] productImage = null;
		List<Review> productReviews = null;
		
		Date today = new Date(System.currentTimeMillis());
		
		Product POD = new Product();
		
		try {
			POD = productService.loadPOD(today);
			productName = POD.getName();
			productImage = POD.getImage();
			productReviews = POD.getReviews();
			
		}catch (NoPODException e) {
			
			String path = "/WEB-INF/HomePage.html";
			ServletContext servletContext = getServletContext();
			final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
			ctx.setVariable("errormsg", "Sorry, no product avalaible for today!  :(");
			templateEngine.process(path, ctx, response.getWriter());
			return;
		}
		
		String path = "/WEB-INF/HomePage.html";
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		

		String user = (String) session.getAttribute("user");
		Player player = playerService.retrievePlayer(user);
		ctx.setVariable("productName", productName);
		ctx.setVariable("productImage", Base64.getEncoder().encodeToString(productImage));
		ctx.setVariable("productReviews", productReviews);
		ctx.setVariable("buttonEnabled", true);
		
		//Controllo se l'utente è stato bloccato o se ha già risposto, e imposto un messaggio per i due casi.
		
		if(playerService.hasAnswered(player, POD)) {
			
			ctx.setVariable("disableMessage", "You have already answered your questionnaire today. Come back tomorrow for more points!");
			ctx.setVariable("buttonEnabled", false);
		}
		
		if(player.isBlocked()) {
			
			ctx.setVariable("disableMessage", "You have been banned. You can not take questionnaires anymore.");
			ctx.setVariable("buttonEnabled", false);
		}
		
		//Infine imposto le ultime variabili e disegno la pagina.
		
		templateEngine.process(path, ctx, response.getWriter());

	}



	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
