package it.polito.tdp.metroparis.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.event.ConnectedComponentTraversalEvent;
import org.jgrapht.event.EdgeTraversalEvent;
import org.jgrapht.event.TraversalListener;
import org.jgrapht.event.VertexTraversalEvent;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import org.jgrapht.traverse.BreadthFirstIterator;

import it.polito.tdp.metroparis.db.MetroDAO;

public class Model {
	
	//CREIAMO NOI LA CLASSE MODEL
	
	//DEFINIMO QUI I GRAFI
	
	Graph<Fermata, DefaultEdge> grafo;
	Map<Fermata,Fermata> predecessore;
	
	//metodo che crea il grafo
	public void creaGrafo() {
		MetroDAO dao= new MetroDAO();
		this.grafo= new SimpleGraph<>(DefaultEdge.class);
		
		//AGGIUNGO VERTICI--> 
			//chiedo al dao di farlo attraverso il metodo AllFermate
			//getAllFermate contiene già tutte le fermate 
			
		List<Fermata>fermate= dao.getAllFermate();
		Graphs.addAllVertices(this.grafo, fermate);
		
		//AGGIUNGO ARCHI 
		
			List<Connessione> connessioni =dao.getAllConnessioni();
			for(Connessione c: connessioni) {
				this.grafo.addEdge(c.getStazP(), c.getStazA());
					}
			System.out.format("Grafo creato con %d vertici e %d archi\n",
					this.grafo.vertexSet().size(), this.grafo.edgeSet().size()) ;
			
				
	}
		
		//Fermata f;
		/* Set<DefaultEdge> archi = this.grafo.edgesOf(f);
		for(DefaultEdge e: archi) {
			f1= Graphs.getOppositeVertex(this.grafo, e, f);
		}
		
		//TROVO PER UN GRAFO NON ORIENTATO I VERTICI ADIACENTI
		List<Fermata> fermateAdiacenti= Graphs.successorListOf(this.grafo,f);
		*/
		
		/** CREO IL METODO PER APPLICARE L'ITERATORE SCELTO 
		 * ITERATORE DI VISITA --> COSTRUISCO UNA LISTA DI FERMATE DATA DALLA VISITA IN AMPIEZZA DELL'ITERATORE
		 * @param partenza
		 * @return
		 */
		
	public List<Fermata> fermateRaggiungibili(Fermata partenza)	{
	   //VISITA IN AMPIEZZA
			BreadthFirstIterator<Fermata,DefaultEdge> bfv= new BreadthFirstIterator<>(this.grafo, partenza);
		//VISITA IN PROFONDITA' --> ottengo stessi vertici ma in un ordine diverso 
			//DepthFirstIterator<>(this.grafo,partenza);
			
		//CREO UNA LISTA, FINCHE' hasNext è vero
			//allora aggiungo la fermata successiva a result
			this.predecessore=new HashMap<>();
			this.predecessore.put(partenza, null);
			
			
		
		//ASSOCIO AL BFV UN GESTORE DI EVENTI --> addTraversalListener 
			//creo una classe direttamente qui nel codice!
			//addUni.. cioè mi aggiunge automaticamente i metodi della classe  dentro le parentesi (...)
			//per farlo: (){ }); e poi suggerisce eclipse, alcuni metodi saranno inutili e posso cancellarli
			
			bfv.addTraversalListener(new TraversalListener<Fermata,DefaultEdge>(){

				@Override
				public void connectedComponentFinished(ConnectedComponentTraversalEvent e) {
					
				}

				@Override
				public void connectedComponentStarted(ConnectedComponentTraversalEvent e) {
			
				}

				@Override
				public void edgeTraversed(EdgeTraversalEvent<DefaultEdge> e) {
					// TODO Auto-generated method stub
					
					DefaultEdge arco= e.getEdge();
					Fermata a= grafo.getEdgeSource(arco);
					Fermata b= grafo.getEdgeTarget(arco);
					
					//devo fare la ricerca tra fermata a e fermata b
					//ho scoperto a arrivando da b (se b lo conoscevo gia, cioè è una delle chiavi della mappa)
					 if(predecessore.containsKey(b) && !predecessore.containsKey(a)) {
						//è sicuro il vertice sorgente se l'avevo gia visitato perchè è gia presente in mappa
						//quindi b lo raggiungo da a, nella mappa aggiungo che a viene scoperto da b
						predecessore.put(a,b);
						System.out.println(a+ "scoperto da "+b);
					} else if( predecessore.containsKey(a) && !predecessore.containsKey(b)){
						//di sicuro conoscevo a, quindi ho scoperto b 
						predecessore.put(b,a);
						System.out.println(b+ "scoperto da "+a);
						
					}	
					
				}

				@Override
				public void vertexTraversed(VertexTraversalEvent<Fermata> e) {
				
				}

				@Override
				public void vertexFinished(VertexTraversalEvent<Fermata> e) {
					
				}
				
			} );
			
			List<Fermata> result= new ArrayList<>();
			while(bfv.hasNext()) {
				Fermata f= bfv.next();
				result.add(f);
			}
			return result;
			
		}
		
		
		/**
		 * PER VEDERE SE L'OGGETTO PASSATO è TRA I VERTICI CHE HO
		 * ITERIAMO SULLE FERMATE CHE FANNO PARTE DEI VERTICI DEL GRAFO
		 * SE IL NOME DI F E' UGUALE AL NOME CHE STO CERCANDO LO RITORNO
		 * @param nome
		 * @return
		 */
		public Fermata trovaFermata(String nome) {
			for(Fermata f: this.grafo.vertexSet()) {
				if(f.getNome().equals(nome)) {
				 return f;
			}
		}
			return null;
			
		}
		
		
		/** METODO CHE RESTITUISCE IL CAMMINO DATA FERMATA PARTENZA E FERMATA ARRIVO , CIOE CON TUTTE LE FERMATE INTERMEDIE
		 * creo nuovo metodo che trova il cammino tra due fermate , restituisce l'elenco di fermate e ha come primo elemento partenza e come ultimo arrivo
		 * @param partenza
		 * @param arrivo
		 * @return
		 */
		public List<Fermata> trovaCammino (Fermata partenza, Fermata arrivo){
		
			fermateRaggiungibili(partenza);
			
			//parto dall'arrivo 
			List<Fermata> result= new LinkedList<Fermata>();
			//ci metto dentro il punto di arrivo
			result.add(arrivo);
			//vado all'indietro via via che trovo i predecessori
			Fermata f= arrivo; //posiziono un segnaposto f sulla casella dell'arrivo 
			
			//se il predecessore di f non è null allora 
            while(predecessore.get(f)!=null) { 
				//cambio il segnaposto
				f=predecessore.get(f);
				//posso aggiungere f
				result.add(f);
			}
			
            //mi darà la lista che contiene le fermate dall'arrivo alla partenza in questo modo
            //poi dovrò cambiarlo
			return result;
				
		}
			
		
	}
	


