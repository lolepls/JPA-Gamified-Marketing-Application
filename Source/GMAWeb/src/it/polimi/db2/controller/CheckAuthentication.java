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

import it.polimi.db2.gma.entities.Player;
import it.polimi.db2.gma.entities.Session;
import it.polimi.db2.gma.services.PlayerService;
import it.polimi.db2.gma.services.SessionService;
import it.polimi.db2.gma.exceptions.*;



/**
 * Servlet implementation class CheckAuthentication
 */
@WebServlet("/CheckAuthentication")
public class CheckAuthentication extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	
	@EJB(name = "it.polimi.db2.gma.services/PlayerService")
	private PlayerService playerService;
	@EJB(name = "it.polimi.db2.gma.services/SessionService")
	private SessionService sessionService;   
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CheckAuthentication() {
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
		
		String username = null;
		String password = null;
		
		try {
			
			username = StringEscapeUtils.escapeJava(request.getParameter("username"));
			password = StringEscapeUtils.escapeJava(request.getParameter("password"));
			
			if (username == null || password == null || username.isEmpty() || password.isEmpty()) {
				throw new Exception("Missing or empty credential value");
			}
		}
		catch (Exception e) {
			
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing credential value");
			return;
			
		}
		
		String path;
		
		try {
			// query db to authenticate for user.
			Player player = null;
			Session playersession = null;
			
			player = playerService.checkAuthentication(username, password);
			playersession = sessionService.createSession(player);
			
			request.getSession().setAttribute("user", username);
			request.getSession().setAttribute("sessionid", playersession.getId());
			path = getServletContext().getContextPath() + "/LoadHomePage";
			System.out.println(path);
			response.sendRedirect(path);
			
		} catch (WrongCredentialsException e) {
			
			ServletContext servletContext = getServletContext();
			final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
			ctx.setVariable("errorMsg", "Invalid username or password!");
			path = "/index.html";
			templateEngine.process(path, ctx, response.getWriter());
			return;
		}
		catch (Exception e) {
			
			ServletContext servletContext = getServletContext();
			final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
			ctx.setVariable("errorMsg", "Database connection error!");
			path = "/index.html";
			templateEngine.process(path, ctx, response.getWriter());
			
		}
		
		
		
	}

}
