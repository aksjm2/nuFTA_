package model;

public class Edge {

	private int source_id;
	private String source_name;
	
	public Edge(int source_id, String source_name){
		this.source_id = source_id;
		this.source_name = source_name;
	}
	public int getSourceID(){
		return this.source_id;
	}
	public String getSourceName(){
		return this.source_name;
	}
	public void setSourceID(int source_id){
		this.source_id = source_id;
	}
	public void setSourceName(String source_name){
		this.source_name = source_name;
	}
}
