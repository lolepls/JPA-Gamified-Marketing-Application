package it.polimi.db2.controller;

import java.io.IOException;
import java.sql.Date;
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
import it.polimi.db2.gma.services.ProductService;

/**
 * Servlet implementation class DeleteProduct
 */
@WebServlet("/DeleteProduct")
public class DeleteProduct extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	
	
	@EJB(name = "it.polimi.db2.gma.services/ProductService")
	private ProductService productService;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DeleteProduct() {
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
	 * Questo servlet usa get request perché nella pagina deletionpage c'è href=...
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		// If the user is not logged in (not present in session) redirect to the login
				String loginpath = getServletContext().getContextPath() + "/index.html";
				HttpSession session = request.getSession();
				if (session.isNew() || session.getAttribute("admin") == null) {
					response.sendRedirect(loginpath);
					return;
				}
			
		//Recupero i prodotti che possono essere eliminati
		Date today = new Date(System.currentTimeMillis());
	    List<Product> suitableForDeletion = productService.getProductsForDeletion(today);
	    //Recupero l'id del prodotto di cui si vuole eliminare il questionario
		int productDeleteId = Integer.parseInt(request.getParameter("productid"));
		
		//Controllo se questo sia effettivamente eliminabile e non sia frutto di una
		//GET request craftata artificialmente
		
		boolean found = false;
		for(Product product : suitableForDeletion) {
			if(productDeleteId==product.getId()) {
				found = true;
			}
		}
		
		if(!found) {
			
			 
			String path = "/WEB-INF/deletionpage.html";
			ServletContext servletContext = getServletContext();
			final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
			ctx.setVariable("deletionmsg", "Something went wrong. Choose a product from the list above.");
			ctx.setVariable("products", suitableForDeletion);
			templateEngine.process(path, ctx, response.getWriter());
			return;
			
		}
		
		
		//Recupero il prodotto di cui eliminare il questionario
		Product productDelete = productService.getProduct(productDeleteId);
		
		//Chiamo la funzione per eliminare il questionario
		productService.deleteQuestionnaire(productDelete);
		
		//Recupero nuovamente i prodotti per aggiornare la pagina
	    suitableForDeletion = productService.getProductsForDeletion(today);
	    
	    //Aggiorno la pagina
		String path = "/WEB-INF/deletionpage.html";
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		ctx.setVariable("products", suitableForDeletion);
		ctx.setVariable("deletionmsg", "You have deleted the questionnaire for the product " + productDelete.getName());
		templateEngine.process(path, ctx, response.getWriter());
		
	}


}
