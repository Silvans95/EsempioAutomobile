package it.prova.test;

import java.util.Date;
import java.util.List;

import org.hibernate.LazyInitializationException;

import it.prova.dao.EntityManagerUtil;
import it.prova.model.Automobile;
import it.prova.model.Proprietario;

import it.prova.service.MyServiceFactory;
import it.prova.service.automobile.AutomobileService;
import it.prova.service.proprietario.ProprietarioService;

public class ProprietarioTest {

	public static void main(String[] args) {

		ProprietarioService proprietarioService = MyServiceFactory.getProprietarioServiceInstance();
		AutomobileService automobileService = MyServiceFactory.getAutomobileServiceInstance();

		try {

			testInserisciProprietario(proprietarioService);

			testInserisciAutomobile(proprietarioService, automobileService);

			testRimozioneAbitante(proprietarioService, automobileService);

			testLazyInitExc(proprietarioService, automobileService);

			System.out.println();
			System.out.println();
			testCercaTutteLeAutoConCF(proprietarioService, automobileService);

			System.out.println(proprietarioService.contaImmatricolazione());

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// questa è necessaria per chiudere tutte le connessioni quindi rilasciare il
			// main
			EntityManagerUtil.shutdown();
		}

	}

	private static void testInserisciProprietario(ProprietarioService proprietarioService) throws Exception {
		System.out.println(".......testInserisciProprietario inizio.............");
		// creo nuovo proprietario
		Proprietario nuovoProprietario = new Proprietario("Silvano", "Paloni", "PLNSVN95D08H501R", new Date());

		if (nuovoProprietario.getId() != null)
			throw new RuntimeException("testInserisciProprietario fallito: record già presente ");
		proprietarioService.inserisciNuovo(nuovoProprietario);

		if (nuovoProprietario.getId() == null)
			throw new RuntimeException("testInserisciProprietario fallito ");

		System.out.println(".......testInserisciProprietario fine: PASSED.............");
	}

	private static void testInserisciAutomobile(ProprietarioService proprietarioService,
			AutomobileService automobileService) throws Exception {
		System.out.println(".......testInserisciAutomobile inizio.............");
		List<Proprietario> listaProprietariPresenti = proprietarioService.listAllProprietari();
		if (listaProprietariPresenti.isEmpty())
			throw new RuntimeException("testInserisciAutomobile fallito: non ci sono proprietari a cui collegarci ");

		Automobile nuovaAutomobile = new Automobile("FORD", "Fiesta", "CCNIHOI", new Date());
		nuovaAutomobile.setProprietario(listaProprietariPresenti.get(0));

		automobileService.inserisciNuovo(nuovaAutomobile);
		if (nuovaAutomobile.getId() == null)
			throw new RuntimeException("testInserisciAutomobile fallito ");

		if (nuovaAutomobile.getProprietario() == null)
			throw new RuntimeException("testInserisciAutomobile fallito: non ha collegato il proprietario ");

		System.out.println(".......testInserisciAutomobile fine: PASSED.............");
	}

	private static void testRimozioneAbitante(ProprietarioService proprietarioService,
			AutomobileService automobileService) throws Exception {
		System.out.println(".......testRimozioneAutomobile inizio.............");

		// inserisco un abitante che rimuoverò
		// creo nuovo abitante ma prima mi serve un municipio
		List<Proprietario> listaProprietariPresenti = proprietarioService.listAllProprietari();
		if (listaProprietariPresenti.isEmpty())
			throw new RuntimeException("testRimozioneAutomobile fallito: non ci sono proprietari a cui collegarci ");

		Automobile nuovaAutomobile = new Automobile("FORD", "Fiesta", "CCNIHOI", new Date());
		// lo lego al primo municipio che trovo
		nuovaAutomobile.setProprietario(listaProprietariPresenti.get(0));

		// salvo il nuovo abitante
		automobileService.inserisciNuovo(nuovaAutomobile);

		Long idAbitanteInserito = nuovaAutomobile.getId();
		automobileService.rimuovi(automobileService.caricaSingolaAutomobile(idAbitanteInserito));
		// proviamo a vedere se è stato rimosso
		if (automobileService.caricaSingolaAutomobile(idAbitanteInserito) != null)
			throw new RuntimeException("testRimozioneAbitante fallito: record non cancellato ");
		System.out.println(".......testRimozioneAutomobile fine: PASSED.............");
	}

	private static void testLazyInitExc(ProprietarioService proprietarioService, AutomobileService automobileService)
			throws Exception {
		System.out.println(".......testLazyInitExc inizio.............");

		List<Proprietario> listaProprietariPresenti = proprietarioService.listAllProprietari();
		if (listaProprietariPresenti.isEmpty())
			throw new RuntimeException("testLazyInitExc fallito: non ci sono proprietari a cui collegarci ");

		Proprietario proprietarioSuCuiFareIlTest = listaProprietariPresenti.get(0);
		try {
			proprietarioSuCuiFareIlTest.getAutomobile().size();
			throw new RuntimeException("testLazyInitExc fallito: eccezione non lanciata ");
		} catch (LazyInitializationException e) {

		}

		System.out.println(".......testLazyInitExc fine: PASSED.............");
	}

	// ######################### metodi non CRUDE
	// ###################################################

	private static void testCercaTutteLeAutoConCF(ProprietarioService proprietarioService,
			AutomobileService automobileService) throws Exception {
		System.out.println(".......testCercaTutteLeAutoConCF inizio.............");

		List<Proprietario> listaProprietariPresenti = proprietarioService.listAllProprietari();
		if (listaProprietariPresenti.isEmpty())
			throw new RuntimeException("testCercaTutteLeAutoConCF fallito: non ci sono municipi a cui collegarci ");

		Automobile nuovoAutomobile = new Automobile("FORD", "Bassi", "asf", new Date());
		Automobile nuovoAutomobile2 = new Automobile("FORD", "Nato", "asgq", new Date());

		nuovoAutomobile.setProprietario(listaProprietariPresenti.get(0));
		nuovoAutomobile2.setProprietario(listaProprietariPresenti.get(0));

		automobileService.inserisciNuovo(nuovoAutomobile);
		automobileService.inserisciNuovo(nuovoAutomobile2);
		

		automobileService.cercaTutteAutomobiliConProprietarioCF("PL");

		// clean up code
		automobileService.rimuovi(nuovoAutomobile);
		automobileService.rimuovi(nuovoAutomobile2);

		System.out.println(".......testCercaTutteLeAutoConCF fine: PASSED.............");
	}

}