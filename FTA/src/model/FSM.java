package model;

import java.util.ArrayList;

public class FSM extends Node{

	private ArrayList<State> states;
	private ArrayList<AnnotatedState> annotatedStates;
	private ArrayList<Variable> variables;
	private int initialRefId;
	
	public FSM(String name, int id){
		super(name, id);
		this.states = new ArrayList<State>();
		this.annotatedStates = new ArrayList<AnnotatedState>();
		this.variables = new ArrayList<Variable>();
	}
	public FSM(String name, int id, int initialRefId){
		super(name, id);
		this.initialRefId = initialRefId;
		this.states = new ArrayList<State>();
		this.annotatedStates = new ArrayList<AnnotatedState>();
		this.variables = new ArrayList<Variable>();
	}
	public FSM(String name, int id, ArrayList<Edge> edges){
		super(name, id, edges);
		this.states = new ArrayList<State>();
		this.annotatedStates = new ArrayList<AnnotatedState>();
		this.variables = new ArrayList<Variable>();
	}
	public FSM(String name, int id, ArrayList<Edge> edges, int initialRefId){
		super(name, id, edges);
		this.initialRefId = initialRefId;
		this.states = new ArrayList<State>();
		this.annotatedStates = new ArrayList<AnnotatedState>();
		this.variables = new ArrayList<Variable>();
	}
	public void addState(State s){
		this.states.add(s);
	}
	public void addAnnotatedStates(AnnotatedState as){
		this.annotatedStates.add(as);
	}
	public void addVariable(Variable v){
		this.variables.add(v);
	}
	public ArrayList<State> getStates() {
		return states;
	}
	public void setStates(ArrayList<State> states) {
		this.states = states;
	}
	public ArrayList<AnnotatedState> getAnnotatedStates() {
		return annotatedStates;
	}
	public void setAnnotatedStates(ArrayList<AnnotatedState> annotatedStates) {
		this.annotatedStates = annotatedStates;
	}
	public ArrayList<Variable> getVariables() {
		return variables;
	}
	public void setVariables(ArrayList<Variable> variables) {
		this.variables = variables;
	}
	public int getInitialRefId() {
		return initialRefId;
	}
	public void setInitialRefId(int initialRefId) {
		this.initialRefId = initialRefId;
	}
}
