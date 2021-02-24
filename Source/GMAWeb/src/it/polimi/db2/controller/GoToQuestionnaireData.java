package it.polimi.db2.controller;

import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringEscapeUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import it.polimi.db2.gma.entities.Answers;
import it.polimi.db2.gma.entities.Player;
import it.polimi.db2.gma.entities.Product;
import it.polimi.db2.gma.entities.Questions;
import it.polimi.db2.gma.exceptions.IncorrectDateException;
import it.polimi.db2.gma.exceptions.NoPODException;
import it.polimi.db2.gma.exceptions.NoQuestionsException;
import it.polimi.db2.gma.services.PlayerService;
import it.polimi.db2.gma.services.ProductService;
import it.polimi.db2.gma.services.SessionService;

/**
 * Servlet implementation class SeeQuestionnaireData
 */
@WebServlet("/GoToQuestionnaireData")
public class GoToQuestionnaireData extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;

	
	@EJB(name = "it.polimi.db2.gma.services/ProductService")
	private ProductService productService;

	
	@EJB(name = "it.polimi.db2.gma.services/PlayerService")
	PlayerService playerService;
	
	
	@EJB(name = "it.polimi.db2.gma.services/SessionService")
	SessionService sessionService;
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GoToQuestionnaireData() {
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
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String loginpath = getServletContext().getContextPath() + "/index.html";
		HttpSession session = request.getSession();
		if (session.isNew() || session.getAttribute("admin") == null) {
			response.sendRedirect(loginpath);
			return;
		}
		
		
		String dateString = null;
		
		try {
			
			dateString = StringEscapeUtils.escapeJava(request.getParameter("date"));
			
			if (dateString == null || dateString.isEmpty()) {
				throw new Exception("Missing or empty date");
			}
		}
		catch (Exception e) {
			
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing date");
			return;
			
		}
		
		
		//valueOf converts a string (format yyyy-mm-dd) in a sqlDate
		Date date = Date.valueOf(dateString);

		
		Product POD = new Product();
		
		try {
			//checks if the date inserted is related to a POD with questions and returns the POD
			POD = productService.checkDate(date);
			
		}catch(NoPODException e) {
			
			String path = "/WEB-INF/InspectionPage.html";
			ServletContext servletContext = getServletContext();
			final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
			ctx.setVariable("errormsg", "Sorry, there isn't a POD for the inserted date :(");
			templateEngine.process(path, ctx, response.getWriter());
			return;
			
		}catch(NoQuestionsException e) {
			
			String path = "/WEB-INF/InspectionPage.html";
			ServletContext servletContext = getServletContext();
			final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
			ctx.setVariable("errormsg", "Sorry, the POD referring to the inserted date doesn't have questions :(");
			templateEngine.process(path, ctx, response.getWriter());
			return;
			
		}catch(IncorrectDateException e) {
			
			String path = "/WEB-INF/InspectionPage.html";
			ServletContext servletContext = getServletContext();
			final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
			ctx.setVariable("errormsg", "Sorry, you have to insert a date in the past :(");
			templateEngine.process(path, ctx, response.getWriter());
			return;
			
		}
		
		//they are the players we will show on the questionnaire data page
		List<Player> playersWithAnswers = null;

		playersWithAnswers = playerService.getLeaderboardPlayers(POD);
		
		
		List<Answers> answers = new ArrayList<>();;
		//for each player who answered we save his answers
		for (Player p:playersWithAnswers) {
			
			answers.addAll(playerService.getPlayerPODAnswers(p, POD));
			
		}
		
		//they are the players who cancelled their questionnaire for that date;
		List<Player> playersWhoCancelled = new ArrayList<>();
		
		playersWhoCancelled = sessionService.getPlayersWhoCancelled(date);
		
		
		
		//questions related to the POD
		List<Questions> questions = null;
		questions = POD.getQuestions();

		
		String path = "/WEB-INF/QuestionnaireDataPage.html";
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		ctx.setVariable("playersWithAnswers", playersWithAnswers);
		ctx.setVariable("playersWhoCancelled", playersWhoCancelled);
		ctx.setVariable("questions", questions);
		ctx.setVariable("answers", answers);
		ctx.setVariable("date", dateString);

		templateEngine.process(path, ctx, response.getWriter());
		return;
		
		
	}

}
