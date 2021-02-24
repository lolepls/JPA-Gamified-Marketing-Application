package it.polimi.db2.controller;

import java.io.IOException;
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

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;


import it.polimi.db2.gma.entities.Questions;
import it.polimi.db2.gma.services.ProductService;


/**
 * Template per creare un servlet di GMA.
 * Sostituire qui il nome con quello del servlet da creare.
 */
@WebServlet("/GoToStatQuestionnaire")
public class GoToStatQuestionnaire extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	

	@EJB(name = "it.polimi.db2.gma.services/ProductService")
	private ProductService productService;
	
       
	
    /**
     * Cambiare il nome di questo costruttore con quello del servlet.
     */
    public GoToStatQuestionnaire() {
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
	@SuppressWarnings("unchecked")
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String loginpath = getServletContext().getContextPath() + "/index.html";
		HttpSession session = request.getSession();
		if (session.isNew() || session.getAttribute("user") == null) {
			response.sendRedirect(loginpath);
			return;
		}
		
		//Recupero l'array di domande che era salvato nella sessione
		List<Questions> productQuestions = (List<Questions>)request.getSession().getAttribute("QuestionsArray");
		
		//Creo un array di risposte
		List<String> answers = new ArrayList<String>();
		
		//Con un loop e per ogni domanda pesco la relativa risposta dal form ricevuto
		for(Questions question : productQuestions) {
			
			String questionid = String.valueOf(question.getId());
			String answer = request.getParameter(questionid);
			answers.add(answer);
		}
		
		//Infine salvo le risposte nella sessione. Servirà sia per submittarle più avanti sia per reinserirle nel form col back button.
		request.getSession().setAttribute("AnswersArray", answers);
		
		String path = "/WEB-INF/questionnaires.html";
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		//Serve per debug e stampare le risposte
		ctx.setVariable("useranswers", answers);
		templateEngine.process(path, ctx, response.getWriter());
				
		
	}

}
