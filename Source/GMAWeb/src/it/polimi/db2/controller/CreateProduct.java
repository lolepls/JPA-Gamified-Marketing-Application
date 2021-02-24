package it.polimi.db2.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Date;

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

import it.polimi.db2.gma.entities.Product;
import it.polimi.db2.gma.exceptions.AlreadyExistingPODException;
import it.polimi.db2.gma.exceptions.IncorrectDateException;
import it.polimi.db2.gma.exceptions.NoPODException;
import it.polimi.db2.gma.exceptions.UnavailableDateException;
import it.polimi.db2.gma.exceptions.ImageWrongFormatException;
import it.polimi.db2.gma.services.ProductService;


/**
 * Servlet implementation class CreateProduct
 */
@WebServlet("/CreateProduct")
public class CreateProduct extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	
	@EJB(name = "it.polimi.db2.gma.services/ProductService")
	private ProductService productService;
	
	
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CreateProduct() {
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
		
		
		String productName = null;
		String imagePath = null;
		byte[] productImage = null;
		String date = null;
		
		
		try {
			
			productName = StringEscapeUtils.escapeJava(request.getParameter("productName"));
			imagePath = StringEscapeUtils.escapeJava(request.getParameter("image"));
			date = StringEscapeUtils.escapeJava(request.getParameter("date"));
			
			if (productName == null || imagePath == null || date == null || productName.isEmpty() || date.isEmpty() || imagePath.isEmpty()) {
				throw new Exception("Missing or empty values");
			}
		}
		catch (Exception e) {
			
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing values");
			return;
			
		}
		
		
		//valueOf converts a string (format yyyy-mm-dd) in a sqlDate
		Date PODDate = Date.valueOf(date);
		
		//readAllBytes generates a byte array from a path
		productImage = Files.readAllBytes(Paths.get(imagePath));
		
		Product POD = new Product();
		POD.setDate(PODDate);
		POD.setImage(productImage);
		POD.setName(productName);
		POD.setPointsgiven(0);
		
		try {
			productService.createPOD(POD);
		}catch (AlreadyExistingPODException e) {
			
			String path = "/WEB-INF/CreationPage.html";
			ServletContext servletContext = getServletContext();
			final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
			ctx.setVariable("errormsg", "Sorry, this product is already in the system!  :(");
			templateEngine.process(path, ctx, response.getWriter());
			return;
		}catch (UnavailableDateException e) {
			
			String path = "/WEB-INF/CreationPage.html";
			ServletContext servletContext = getServletContext();
			final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
			ctx.setVariable("errormsg", "Sorry, for this date there is already another POD!  :(");
			templateEngine.process(path, ctx, response.getWriter());
			return;
		}catch (IncorrectDateException e) {
			
			String path = "/WEB-INF/CreationPage.html";
			ServletContext servletContext = getServletContext();
			final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
			ctx.setVariable("errormsg", "Sorry, you cannot choose a date in the past!  :(");
			templateEngine.process(path, ctx, response.getWriter());
			return;
			
		}
		catch (ImageWrongFormatException e) {
		
		String path = "/WEB-INF/CreationPage.html";
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		ctx.setVariable("errormsg", "Sorry, you didn't upload an image! Please upload a .png file :(");
		templateEngine.process(path, ctx, response.getWriter());
		return;
		}
		
		try {
			POD = productService.loadPOD(PODDate);
			String path = "/WEB-INF/QuestionsCreationPage.html";
			ServletContext servletContext = getServletContext();
			final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
			request.getSession().setAttribute("productID", POD.getId());
			templateEngine.process(path, ctx, response.getWriter());
		} catch (NoPODException e) {
			String path = "/WEB-INF/CreationPage.html";
			ServletContext servletContext = getServletContext();
			final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
			ctx.setVariable("errormsg", "Sorry, no product has been created, please retry!  :(");
			templateEngine.process(path, ctx, response.getWriter());
			return;
		}
		
		
		
	}

}
