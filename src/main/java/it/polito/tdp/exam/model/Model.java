package it.polito.tdp.exam.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.exam.db.BaseballDAO;

public class Model {
	private SimpleWeightedGraph<Integer, DefaultWeightedEdge> grafo;
	private Map<Integer, List<People>> annoToPlayers;
	private BaseballDAO dao;
	
	public Model() {
		this.dao = new BaseballDAO();
		this.annoToPlayers= new HashMap<Integer, List<People>>();
	}
	
	public void buildGraph(String name) {
		this.grafo = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		
		//Creazione vertici
		List<Integer> vertici = this.dao.getVertici(name);
		Graphs.addAllVertices(this.grafo, vertici);
		
		annoToPlayers.clear();
		for (Integer anno: this.grafo.vertexSet()) {
			this.annoToPlayers.put(anno, this.dao.getPlayersTeamYear(anno, name));
		}
		
		//Creazione edges
		for (int i=0; i<vertici.size();i++) {
			for(int j =i+1; j< vertici.size(); j++) {
				List<People> giocatori1 = new ArrayList<>(this.annoToPlayers.get(vertici.get(i)));
				List<People> giocatori2 = this.annoToPlayers.get(vertici.get(j));
				giocatori1.retainAll(giocatori2);
				int peso = giocatori1.size();
				Graphs.addEdgeWithVertices(this.grafo, vertici.get(i), vertici.get(j), peso);
			}
		}				
	}
	public Set<Integer> getVertici(){
		return this.grafo.vertexSet();
	}
	
	public Set<DefaultWeightedEdge> getArchi(){
		return this.grafo.edgeSet();
	}
	public List<String> getTeamsName(){
		return this.dao.getTeamsName();
	}
	public List<Dettaglio> getDettagli(Integer anno){
		List<Dettaglio> result = new ArrayList<Dettaglio>();
		List<Integer> adiacenti = Graphs.neighborListOf(this.grafo, anno);
		
		
		for(Integer nodo : adiacenti) {
			DefaultWeightedEdge arco = this.grafo.getEdge(anno, nodo);
			result.add(new Dettaglio(nodo, (int)this.grafo.getEdgeWeight(arco)) );
		}
		Collections.sort(result);
		return result;
	}
}
