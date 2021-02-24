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

import it.polimi.db2.gma.entities.Product;
import it.polimi.db2.gma.entities.Questions;
import it.polimi.db2.gma.exceptions.NoPODException;
import it.polimi.db2.gma.services.ProductService;


/**
 * Template per creare un servlet di GMA.
 * Sostituire qui il nome con quello del servlet da creare.
 */
@WebServlet("/BackToMarketing")
public class BackToMarketing extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	
	@EJB(name = "it.polimi.db2.gma.services/ProductService")
	private ProductService productService;
       
	
    /**
     * Cambiare il nome di questo costruttore con quello del servlet.
     */
    public BackToMarketing() {
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
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	
		String loginpath = getServletContext().getContextPath() + "/index.html";
		HttpSession session = request.getSession();
		if (session.isNew() || session.getAttribute("user") == null) {
			response.sendRedirect(loginpath);
			return;
		}
		
		//Recupero il prodotto e le sue domande
		
		Date today = new Date(System.currentTimeMillis());
		Product POD = new Product();
		String productName = null;
		byte[] productImage = null;
		List<Questions> productQuestions = null;
		
		try {
			POD = productService.loadPOD(today);
			productQuestions = POD.getQuestions();
			productName = POD.getName();
			productImage = POD.getImage();
			
		}catch (NoPODException e) {
			
			String path = "/WEB-INF/HomePage.html";
			ServletContext servletContext = getServletContext();
			final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
			ctx.setVariable("errormsg", "Sorry, no product avalaible for today!  :(");
			templateEngine.process(path, ctx, response.getWriter());
			return;
		}
		
		String path = "/WEB-INF/questionnairem.html";
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		ctx.setVariable("productName", productName);
		ctx.setVariable("productImage", Base64.getEncoder().encodeToString(productImage));
		ctx.setVariable("productQuestions", productQuestions);
		//Riempio l'array di risposte del contesto con le risposte dell'utente che sono conservate nella sessione
		ctx.setVariable("useranswers", request.getSession().getAttribute("AnswersArray"));
		templateEngine.process(path, ctx, response.getWriter());
		
	}

}
