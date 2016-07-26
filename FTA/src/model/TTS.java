package model;

import java.util.ArrayList;

public class TTS extends FSM{

	private Variable clock;


	public TTS(String name, int id) {
		super(name, id);
	}
	public TTS(String name, int id, Variable clock) {
		super(name, id);
		this.clock = clock;
	}
	public TTS(String name, int id, int initialRefId){
		super(name, id, initialRefId);
	}
	public TTS(String name, int id, int initialRefId, Variable clock){
		super(name, id, initialRefId);
		this.clock = clock;
	}
	public TTS(String name, int id, ArrayList<Edge> edges){
		super(name, id, edges);
	}
	public TTS(String name, int id, ArrayList<Edge> edges, int initialRefId){
		super(name, id, edges, initialRefId);
	}
	public TTS(String name, int id, ArrayList<Edge> edges, int initialRefId, Variable clock){
		super(name, id, edges, initialRefId);
		this.clock = clock;
	}
	public Variable getClock() {
		return clock;
	}
	public void setClock(Variable clock) {
		this.clock = clock;
	}
}
