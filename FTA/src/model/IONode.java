package model;

import java.util.ArrayList;

public class IONode extends Node{
	private boolean isInput;
	private Variable value;
	
	public IONode(String name, int id){
		super(name, id);
	}
	public IONode(String name, int id, ArrayList<Edge> edges){
		super(name, id, edges);
	}
	public IONode(String name, int id, ArrayList<Edge> edges, boolean isInput, Variable value){
		super(name, id, edges);
		this.isInput = isInput;
		this.value = value;
	}
	public IONode(String name, int id, boolean isInput, Variable value){
		super(name, id);
		this.isInput = isInput;
		this.value = value;
	}
	public boolean isInput() {
		return isInput;
	}
	public void setInput(boolean isInput) {
		this.isInput = isInput;
	}
	public Variable getValue() {
		return value;
	}
	public void setValue(Variable value) {
		this.value = value;
	}
	

}
