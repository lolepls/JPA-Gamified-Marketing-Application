package it.polimi.db2.controller;

import java.io.IOException;
import java.sql.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import it.polimi.db2.gma.entities.Player;
import it.polimi.db2.gma.entities.Product;
import it.polimi.db2.gma.exceptions.NoPODException;
import it.polimi.db2.gma.services.PlayerService;
import it.polimi.db2.gma.services.ProductService;


/**
 * Servlet implementation class GoToLeaderboard
 */
@WebServlet("/GoToLeaderboard")
public class GoToLeaderboard extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GoToLeaderboard() {
        super();
        // TODO Auto-generated constructor stub
    }
    
	
	@EJB(name = "it.polimi.db2.gma.services/PlayerService")
	private PlayerService playerService;
	@EJB(name = "it.polimi.db2.gma.services/ProductService")
	private ProductService productService;

    
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
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	
		//Recupero il prodotto del giorno
		Date today = new Date(System.currentTimeMillis());
		Product POD = new Product();
		
		try {
			POD = productService.loadPOD(today);
			
		}catch (NoPODException e) {
			
			String path = "/WEB-INF/HomePage.html";
			ServletContext servletContext = getServletContext();
			final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
			ctx.setVariable("errormsg", "Sorry, no product avalaible for today!  :(");
			templateEngine.process(path, ctx, response.getWriter());
			return;
		}
		
		
		List<Player> leaderboardPlayers = playerService.getLeaderboardPlayers(POD);
		
		String path = "/WEB-INF/leaderboard.html";
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		ctx.setVariable("players", leaderboardPlayers);
		templateEngine.process(path, ctx, response.getWriter());
	}

}
