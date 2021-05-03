package it.polito.tdp.metroparis.model;

import java.util.List;

public class TestModel {
	
	public static void main(String[] args) {
		Model m= new Model();
		m.creaGrafo();
		
		//creo un metodo nel Model per ricevere la fermata con il nome che sto cercando
		   //HO CREATO "TROVA FERMATA"
		Fermata partenza= m.trovaFermata("La Fourche");
		if(partenza==null) {
			System.out.println("Fermata non trovata");
			
		} else {
			
		List<Fermata>raggiungibili = m.fermateRaggiungibili(partenza);
	 	System.out.println(raggiungibili);
	  }
		
		//DATA LA FERMATA DI ARRIVO E DI PARTENZA, TROVO IL CAMMINO PER ARRIVARCI
		//IL CAMMINO LO TROVO ATTRAVERSO IL METODO CHE MI RESTITUISCE LA LISTA PERCORSO 
		Fermata arrivo= m.trovaFermata("Temple");
		List<Fermata> percorso = m.trovaCammino(partenza, arrivo);
		System.out.println(percorso);
	}

}

