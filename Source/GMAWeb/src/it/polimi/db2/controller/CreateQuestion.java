package it.polimi.db2.controller;

import java.io.IOException;

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

import it.polimi.db2.gma.entities.Questions;
import it.polimi.db2.gma.exceptions.AlreadyExistingQuestionException;
import it.polimi.db2.gma.exceptions.NoPODException;
import it.polimi.db2.gma.services.QuestionService;

/**
 * Servlet implementation class CreateQuestions
 */
@WebServlet("/CreateQuestion")
public class CreateQuestion extends HttpServlet {
	private static final long serialVersionUID = 1L;
private TemplateEngine templateEngine;
	
	@EJB(name = "it.polimi.db2.gma.services/QuestionService")
	private QuestionService questionService;
	
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CreateQuestion() {
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
		int productID = (int) session.getAttribute("productID");
		 
		String questionText = null;
		try {
			
			questionText = StringEscapeUtils.escapeJava(request.getParameter("question"));

			
			if (questionText == null || questionText.isEmpty()) {
				throw new Exception("Missing question");
			}
		}
		catch (Exception e) {
			
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing question");
			return;
			
		}
		
		Questions question = new Questions();
		question.setProductId(productID);
		question.setText(questionText);
		
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());

		
		try {
			
			questionService.addQuestion(question);
			ctx.setVariable("submittedMsg", "The question '" + question + "' has been correctly submitted.");
			
		}catch (NoPODException e) {
			
			String path = "/WEB-INF/QuestionsCreationPage.html";
			ctx.setVariable("errormsg", "Sorry, no existing product with this ID!  :(");
			templateEngine.process(path, ctx, response.getWriter());
			return;
			
		}catch (AlreadyExistingQuestionException e) {
			
			String path = "/WEB-INF/QuestionsCreationPage.html";
			servletContext = getServletContext();
			ctx.setVariable("errormsg", "This question is already in the system!  :(");
			templateEngine.process(path, ctx, response.getWriter());
			return;
			
		}
		
		
		String path = "/WEB-INF/QuestionsCreationPage.html";
		templateEngine.process(path, ctx, response.getWriter());
		
	}
	
	

}
