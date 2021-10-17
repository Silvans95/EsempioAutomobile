package it.prova.dao.proprietario;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import it.prova.model.Proprietario;


public class ProprietarioDAOImpl implements ProprietarioDAO {

	private EntityManager entityManager;

	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	@Override
	public List<Proprietario> list() throws Exception {
		return entityManager.createQuery("from Proprietario", Proprietario.class).getResultList();
	}

	@Override
	public Proprietario get(Long id) throws Exception {
		return entityManager.find(Proprietario.class, id);
	}

	@Override
	public Proprietario getEagerAutomobile(Long id) throws Exception {
		TypedQuery<Proprietario> query = entityManager
				.createQuery("from Proprietario m left join fetch m.automobili where m.id = ?1", Proprietario.class);
		query.setParameter(1, id);
		return query.getResultStream().findFirst().orElse(null);
	}

	@Override
	public void update(Proprietario proprietarioInstance) throws Exception {
		if (proprietarioInstance == null) {
			throw new Exception("Problema valore in input");
		}

		proprietarioInstance = entityManager.merge(proprietarioInstance);
	}

	@Override
	public void insert(Proprietario proprietarioInstance) throws Exception {
		if (proprietarioInstance == null) {
			throw new Exception("Problema valore in input");
		}

		entityManager.persist(proprietarioInstance);
	}

	@Override
	public void delete(Proprietario proprietarioInstance) throws Exception {
		if (proprietarioInstance == null) {
			throw new Exception("Problema valore in input");
		}

		entityManager.remove(entityManager.merge(proprietarioInstance));
	}

//	###########################à METODI NON CRUDE ##############################################

	@Override
	public int countByImmatricolazione() throws Exception {
		// TODO Auto-generated method stub		
	
		TypedQuery<Proprietario> query = entityManager
					.createQuery("select count(annoimmatricolazione) from Proprietario p join p.automobile a ", Proprietario.class);

			
			//return query.getSingleResult() ha il problema che se non trova elementi lancia NoResultException
			return query.getMaxResults();
	}
	
}
