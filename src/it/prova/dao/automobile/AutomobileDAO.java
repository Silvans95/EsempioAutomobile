package it.prova.dao.automobile;

import java.util.List;

import it.prova.dao.IBaseDAO;
import it.prova.model.Automobile;

public interface AutomobileDAO extends IBaseDAO<Automobile> {


	List<Automobile> findAllByOwnerCF(String iniziale) throws Exception;

	
}
