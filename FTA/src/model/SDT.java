package model;

import java.util.ArrayList;

public class SDT extends Node{
	
	private ArrayList<Assignment> assignments;
	private ArrayList<Variable> variables;
	
	public SDT(String name, int id){
		super(name, id);
		this.assignments = new ArrayList<Assignment>();
		this.variables = new ArrayList<Variable>();
	}
	public SDT(String name, int id, ArrayList<Edge> edges){
		super(name, id, edges);
		this.assignments = new ArrayList<Assignment>();
		this.variables = new ArrayList<Variable>();
	}
	public void addAssignment(Assignment assignment){
		this.assignments.add(assignment);
	}
	public void addVariable(Variable variable)
	{
		this.variables.add(variable);
	}
	public ArrayList<Assignment> getAssignments() {
		return assignments;
	}
	public void setAssignments(ArrayList<Assignment> assignments) {
		this.assignments = assignments;
	}
	public ArrayList<Variable> getVariables() {
		return variables;
	}
	public void setVariables(ArrayList<Variable> variables) {
		this.variables = variables;
	}
}
