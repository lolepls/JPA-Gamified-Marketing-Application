package it.polimi.db2.controller;

import java.io.IOException;

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


/**
 * Template per creare un servlet di GMA.
 * Sostituire qui il nome con quello del servlet da creare.
 */
@WebServlet("/GoToAdminLogin")
public class GoToAdminLogin extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	
    /**
     * Cambiare il nome di questo costruttore con quello del servlet.
     */
    public GoToAdminLogin() {
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

		String path = "/WEB-INF/logadmin.html";
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		templateEngine.process(path, ctx, response.getWriter());
	}

}

