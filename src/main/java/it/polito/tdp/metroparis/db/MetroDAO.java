package it.polito.tdp.metroparis.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.javadocmd.simplelatlng.LatLng;

import it.polito.tdp.metroparis.model.Connessione;
import it.polito.tdp.metroparis.model.Fermata;
import it.polito.tdp.metroparis.model.Linea;

public class MetroDAO {

	public List<Fermata> getAllFermate() {

		final String sql = "SELECT id_fermata, nome, coordx, coordy FROM fermata ORDER BY nome ASC";
		List<Fermata> fermate = new ArrayList<Fermata>();

		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				Fermata f = new Fermata(rs.getInt("id_Fermata"), rs.getString("nome"),
						new LatLng(rs.getDouble("coordx"), rs.getDouble("coordy")));
				fermate.add(f);
			}

			st.close();
			conn.close();

		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("Errore di connessione al Database.");
		}

		return fermate;
	}

	public List<Linea> getAllLinee() {
		final String sql = "SELECT id_linea, nome, velocita, intervallo FROM linea ORDER BY nome ASC";

		List<Linea> linee = new ArrayList<Linea>();

		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				Linea f = new Linea(rs.getInt("id_linea"), rs.getString("nome"), rs.getDouble("velocita"),
						rs.getDouble("intervallo"));
				linee.add(f);
			}

			st.close();
			conn.close();

		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("Errore di connessione al Database.");
		}

		return linee;
	}

	/**
	 * METODO BOOLEAN PER VERIFICARE CON HEIDI SE DUE FERMATE SONO COLLEGATE
	 * se sono collegate metto l'arco tra le stazioni altrimenti no
	 * vedo se la tabella connessione contiene sia id di f1 sia id di f2 come stazione partenza e arrivo
	 * @param f1
	 * @param f2
	 * @return
	 */

	public boolean fermateCollegate(Fermata f1, Fermata f2) {
		
		//creo query dove mi conta le righe che escono --> se non ho righe allora non c'è connessione tra le fermate
		String sql="SELECT COUNT(*) AS count "
				+ "FROM connessione "
				+ "WHERE (id_stazP=? AND id_stazA=?) OR (id_stazP=? AND id_stazA=?) ";
		
		
		try {
		Connection conn=DBConnect.getConnection();
		PreparedStatement st=conn.prepareStatement(sql);
st.setInt(1, f1.getIdFermata());
st.setInt(2, f2.getIdFermata());
st.setInt(3, f2.getIdFermata());
st.setInt(4, f1.getIdFermata());

ResultSet rs= st.executeQuery();
rs.first(); //mi posiziono sulla prima riga
int conteggio= rs.getInt("count");
	
       conn.close();
		
		return (conteggio>0); //ritorno se il conteggio è maggiore di 0
		
		} catch(SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("Errore di connessione al Database.");
		

		}
		
		
	}

	/**
	 * CREO METODO PER OTTENERE TUTTE LE CONNESSIONI 
	 * @param fermate 
	 * @return
	 */
	public List<Connessione>getAllConnessioni(){
		List<Connessione>result= new ArrayList<Connessione>();
		
		String sql="SELECT id_connessione, id_linea, id_stazP, id_stazA "
				+ "FROM connessione "
				+ "WHERE id_stazP> id_stazA ";
		
		try {
			Connection conn=DBConnect.getConnection();
			PreparedStatement st=conn.prepareStatement(sql);
	        ResultSet rs= st.executeQuery();
	        
	        //QUI AVRO UN CICLO E INSERISCO NELLA LISTA DI CONNESSIONE
	      //devo convertire id nell'oggetto corrispondente per i parametri della connessione
	        //estraggo dalla query
	        while(rs.next()) {
	        	int id_partenza= rs.getInt("id_stazP");
	        	Fermata fermata_partenza=null;
	        	for(Fermata f: this.getAllFermate()) {
	        		if(f.getIdFermata()==id_partenza)
	        			fermata_partenza=f;
	        	}
	        	int id_arrivo= rs.getInt("id_stazA");
	        	Fermata fermata_arrivo=null;
	        	for(Fermata f: this.getAllFermate()) {
	        		if(f.getIdFermata()==id_arrivo)
	        			fermata_arrivo=f;
	        	}
	       Connessione c= new Connessione(rs.getInt("id_connessione"),null,fermata_partenza,fermata_arrivo);
	        		
	        		
	        	result.add(c);
	        	
	        }
	
	       conn.close();
		
			
			} catch(SQLException e) {
				e.printStackTrace();
				throw new RuntimeException("Errore di connessione al Database.");
			

			}
		return result;
	}
}

