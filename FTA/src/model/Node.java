package model;

import java.util.ArrayList;

public class Node {

	private String name;
	private int id;
	private ArrayList<Edge> edges;
	
	public Node(){
		this.edges = new ArrayList<Edge>();
	}
	public Node(String name, int id){
		this.name = name;
		this.id = id;
		this.edges = new ArrayList<Edge>();
	}
	public Node(String name, int id, ArrayList<Edge> edges){
		this.name = name;
		this.id = id;
		this.edges = edges;
	}
	public String getName(){
		return this.name;
	}
	public int getId(){
		return this.id;
	}
	public ArrayList<Edge> getEdges(){
		return this.edges;
	}
	public void setName(String name){
		this.name = name;
	}
	public void setId(int id){
		this.id = id;
	}
	public void setEdges(ArrayList<Edge> edges){
		this.edges = edges;
	}
	public void addEdge(Edge e){
		this.edges.add(e);
	}
}
