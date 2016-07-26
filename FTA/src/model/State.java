package model;

import java.util.ArrayList;

public class State {
	private String name;
	private int id;
	private ArrayList<Transition> transitions;
	private int annotatedCount;

	public State(){
		this.name = "";
		this.id = -1;
		this.annotatedCount = 0;
		this.transitions = new ArrayList<Transition>();
	}
	public State(String name, int id){
		this.name = name;
		this.id = id;
		this.annotatedCount = 0;
		this.transitions = new ArrayList<Transition>();
	}
	public State(String name, int id, ArrayList<Transition> transitions){
		this.name = name;
		this.id = id;
		this.transitions = transitions;
		this.annotatedCount = 0;
	}
	public void addTransition(Transition t){
		this.transitions.add(t);
	}
	public void addTransition(int id, int targetRefId, Assignment assignment, boolean isTTS){
		Transition t = new Transition(id, targetRefId, assignment, isTTS);
		this.transitions.add(t);
	}
	public void addTransition(int id, int targetRefId, Assignment assignment, boolean isTTS, Variable timeStart, Variable timeEnd){
		Transition t = new Transition(id, targetRefId, assignment, isTTS, timeStart, timeEnd);
		this.transitions.add(t);
	}
	public String getName() {
		return name;
	}
	public int getAnnotatedCount(){
		return this.annotatedCount;
	}
	public ArrayList<Transition> getTransitions(){
		return this.transitions;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public void setAnnotatedCountInc(){
		this.annotatedCount++;
	}
	
}
