package model;

import java.util.ArrayList;

public class VariableNode extends Node {
	private ArrayList<Variable> variables;
	
	public VariableNode(){
		super();
		this.variables = new ArrayList<Variable>();
	}
	public VariableNode(String name, int id){
		super(name,id);
		this.variables = new ArrayList<Variable>();
	}
	public VariableNode(String name, int id, ArrayList<Edge> edges){
		super(name,id,edges);
		this.variables = new ArrayList<Variable>();
	}
	public void addVariable(Variable v){
		this.variables.add(v);
	}
	public Variable getVariable(int index){
		return this.variables.get(index);
	}
	public void setVariables(ArrayList<Variable> variables){
		this.variables = variables;
	}
	public ArrayList<Variable> getVariables(){
		return this.variables;
	}
}
