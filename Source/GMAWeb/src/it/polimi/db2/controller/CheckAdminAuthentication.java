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

import it.polimi.db2.gma.entities.Administrator;
import it.polimi.db2.gma.services.AdministratorService;



/**
 * Servlet implementation class CheckAdminAuthentication
 */
@WebServlet("/CheckAdminAuthentication")
public class CheckAdminAuthentication extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	@EJB(name = "it.polimi.db2.gma.services/AdministratorService")
	private AdministratorService administratorService;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CheckAdminAuthentication() {
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
		
		Administrator administrator = null;
		String path;
		
		try {
			// query db to authenticate for admin
			administrator = administratorService.checkAdminAuthentication(username, password);
		} catch (Exception e) {
			
			ServletContext servletContext = getServletContext();
			final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
			ctx.setVariable("errorMsg", "Database error");
			path = "/WEB-INF/logadmin.html";
			templateEngine.process(path, ctx, response.getWriter());
			return;
		}
		
		
		if(administrator!=null) {
			request.getSession().setAttribute("admin", username);
			path = getServletContext().getContextPath() + "/LoadHomePageAdmin";
			System.out.println(path);
			response.sendRedirect(path);
		}
		
		else {
			
			ServletContext servletContext = getServletContext();
			final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
			ctx.setVariable("errorMsg", "Incorrect username or password");
			path = "/WEB-INF/logadmin.html";
			templateEngine.process(path, ctx, response.getWriter());
			
		}

		
		
	}

}
