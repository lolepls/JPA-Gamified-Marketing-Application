package it.polimi.db2.controller;

import java.io.IOException;

import javax.ejb.EJB;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import it.polimi.db2.gma.entities.Player;
import it.polimi.db2.gma.services.PlayerService;
import it.polimi.db2.gma.services.SessionService;


/**
 * Template per creare un servlet di GMA.
 * Sostituire qui il nome con quello del servlet da creare.
 */
@WebServlet("/CancelQuestionnaire")
public class CancelQuestionnaire extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	
	@EJB(name = "it.polimi.db2.gma.services/SessionService")
	private SessionService sessionService;
	@EJB(name = "it.polimi.db2.gma.services/PlayerService")
	private PlayerService playerService;
	
	/*
	 * Rimuovere dal commento questa roba se il servlet deve utilizzare un Service
	@EJB(name = "it.polimi.db2.gma.services/NomeService")
	private NomeService metodoService;
	*/
       
	
    /**
     * Cambiare il nome di questo costruttore con quello del servlet.
     */
    public CancelQuestionnaire() {
        super();
    }
    
    /**
     * Questo metodo serve per TemplateEngine. Nulla da toccare qui.
     */
    public void init() throws ServletException {
		ServletContext servletContext = getServletContext();
		ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(servletContext);
		templateResolver.setTemplateMode(TemplateMode.HTML);
		this.templateEngine = new TemplateEngine();
		this.templateEngine.setTemplateResolver(templateResolver);
		templateResolver.setSuffix(".html");
	}

	/**
	 * Metodo che viene chiamato quando si fa una HTTP GET.
	 * Si ricevono informazioni dal browser al servlet.
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		response.getWriter().append("").append(request.getContextPath());
	}

	/**
	 * Metodo che viene chiamato quando si fa una HTTP POST.
	 * Si inviano informazioni.
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		
		String user = (String) request.getSession().getAttribute("user");
		Player player = playerService.retrievePlayer(user);
		int sessionId = (int)request.getSession().getAttribute("sessionid");
		sessionService.setSessionCancelled(player,sessionId);
		
		//Clean the session from the variables
		request.getSession().removeAttribute("AnswersArray");
		request.getSession().removeAttribute("QuestionsArray");
				
		//Redirect to homepage
		String path = getServletContext().getContextPath() + "/LoadHomePage";
		response.sendRedirect(path);
		
		
	}

}