package it.polimi.db2.gma.services;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import it.polimi.db2.gma.entities.Administrator;


@Stateless
public class AdministratorService {
	@PersistenceContext(unitName = "GMAEJB")
	private EntityManager em;
	
	public AdministratorService() {}
	
	public Administrator checkAdminAuthentication(String username, String pwd) {
		Administrator a = null;
		a = em.createNamedQuery("Administrator.checkCredentials", Administrator.class).setParameter(1, username).setParameter(2, pwd)
				.getSingleResult();
		return a;
	}

}