package it.polimi.db2.controller;

import java.io.IOException;

import javax.ejb.EJB;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringEscapeUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import it.polimi.db2.gma.services.PlayerService;
import it.polimi.db2.gma.exceptions.*;


/**
 * Template per creare un servlet di GMA.
 * Sostituire qui il nome con quello del servlet da creare.
 */
@WebServlet("/CreateUser")
public class CreateUser extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	
	
	@EJB(name = "it.polimi.db2.gma.services/PlayerService")
	private PlayerService playerService;
	
       
	
    /**
     * Cambiare il nome di questo costruttore con quello del servlet.
     */
    public CreateUser() {
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
	 * Metodo che viene chiamato quando si fa una HTTP POST.
	 * Si inviano informazioni.
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		
		String username = null;
		String email = null;
		String password = null;
		
		try {
			
			username = StringEscapeUtils.escapeJava(request.getParameter("username"));
			email = StringEscapeUtils.escapeJava(request.getParameter("email"));
			password = StringEscapeUtils.escapeJava(request.getParameter("password"));
			
			if (username == null || password == null || username.isEmpty() || password.isEmpty() || email == null || email.isEmpty() ) {
				throw new Exception("Missing or empty credential value");
			}
		}
		catch (Exception e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing credential value");
			return;
			
		}
		
		
		String path;
		
		
		try {
			//Call to the service that creates a new player
			playerService.createPlayer(username, password, email);
		} 
		//If the chosen username or email are already taken, rise an exception.
		catch (AlreadyExistingUserException e) {
			
			ServletContext servletContext = getServletContext();
			final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
			ctx.setVariable("errorMsg", "The username '" + username +  "' is not available!");
			path = "/WEB-INF/SignUp.html";
			templateEngine.process(path, ctx, response.getWriter());
			return;
		}
		catch (AlreadyExistingEmailException e) {
			
			ServletContext servletContext = getServletContext();
			final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
			ctx.setVariable("errorMsg", "The email '" + email +  "' is not available!");
			path = "/WEB-INF/SignUp.html";
			templateEngine.process(path, ctx, response.getWriter());
			return;
		}
		
		catch (Exception e) {
			
			ServletContext servletContext = getServletContext();
			final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
			ctx.setVariable("errorMsg", "Database connection error.");
			path = "/WEB-INF/SignUp.html";
			templateEngine.process(path, ctx, response.getWriter());
			return;
		}
		
		path = "/index.html";
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		templateEngine.process(path, ctx, response.getWriter());
		
	}

}
