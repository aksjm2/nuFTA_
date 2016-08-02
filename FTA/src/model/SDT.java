package model;

import java.util.ArrayList;

public class SDT extends VariableNode{
	
	private ArrayList<Assignment> assignments;
	
	public SDT(String name, int id){
		super(name, id);
		this.assignments = new ArrayList<Assignment>();
	}
	public SDT(String name, int id, ArrayList<Edge> edges){
		super(name, id, edges);
		this.assignments = new ArrayList<Assignment>();
	}
	public void addAssignment(Assignment assignment){
		this.assignments.add(assignment);
	}
	public ArrayList<Assignment> getAssignments() {
		return assignments;
	}
	public void setAssignments(ArrayList<Assignment> assignments) {
		this.assignments = assignments;
	}
}
