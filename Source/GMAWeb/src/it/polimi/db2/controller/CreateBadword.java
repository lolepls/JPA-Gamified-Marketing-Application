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

import it.polimi.db2.gma.entities.BadWords;
import it.polimi.db2.gma.exceptions.AlreadyExistingBadwordException;
import it.polimi.db2.gma.services.BadwordService;

/**
 * Servlet implementation class CreateBadword
 */
@WebServlet("/CreateBadword")
public class CreateBadword extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	
	@EJB(name = "it.polimi.db2.gma.services/BadwordService")
	private BadwordService badwordService;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CreateBadword() {
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
		
		String estabilishedBy = (String) session.getAttribute("admin");

		String word = null;
		try {
			
			word = StringEscapeUtils.escapeJava(request.getParameter("badword"));

			
			if (word == null || word.isEmpty()) {
				throw new Exception("Missing badword");
			}
		}
		catch (Exception e) {
			
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing badword");
			return;
			
		}
		
		BadWords badword = new BadWords();
		badword.setBadword(word);
		badword.setEstablishedby(estabilishedBy);
		
		try {
			badwordService.addBadword(badword);
		}catch (AlreadyExistingBadwordException e) {
			
			
			String path = "/WEB-INF/BadwordsPage.html";
			ServletContext servletContext = getServletContext();
			final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
			ctx.setVariable("errormsg", "Sorry, this badword is already in the system!  :(");
			templateEngine.process(path, ctx, response.getWriter());
			return;
			
		}
		
		String path = "/WEB-INF/HomePageAdmin.html";
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		templateEngine.process(path, ctx, response.getWriter());
		
		
	}

}
