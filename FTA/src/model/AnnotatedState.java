package model;

import java.util.ArrayList;

public class AnnotatedState {
	private String name;
	private State orignState;
	private Assignment assignment;
	private Transition prevTransition;
	private ArrayList<AnnotatedState> prevAS;
	private ArrayList<Transition> nextTransition;

	public AnnotatedState(){
		this.prevAS = new ArrayList<AnnotatedState>();
		this.nextTransition = new ArrayList<Transition>();
	};
	public AnnotatedState(String name, State orignState, Assignment assignment){
		this.name = name;
		this.orignState = orignState;
		this.assignment = assignment;
		this.prevAS = new ArrayList<AnnotatedState>();
		this.nextTransition = new ArrayList<Transition>();
	}
	public String getName() {
		return name;
	}
	public void addPrevAs(AnnotatedState prev){
		this.prevAS.add(prev);
	}
	public void addNextTransition(Transition next){
		this.nextTransition.add(next);
	}
	public void setName(String name) {
		this.name = name;
	}
	public State getOrignState() {
		return orignState;
	}
	public void setOrignState(State orignState) {
		this.orignState = orignState;
	}
	public Assignment getAssignment() {
		return assignment;
	}
	public void setAssignment(Assignment assignment) {
		this.assignment = assignment;
	}
	public ArrayList<AnnotatedState> getPrevAS() {
		return prevAS;
	}
	public ArrayList<Transition> getNextTransition() {
		return nextTransition;
	}
	public Transition getPrevTransition() {
		return prevTransition;
	}
	public void setPrevTransition(Transition prevTransition) {
		this.prevTransition = prevTransition;
	}

}
