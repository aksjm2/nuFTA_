package model;

public class Transition {
	private int id;
	private int targetRefId;
	private String targetName;
	private Assignment assignment;
	private boolean isTTS;
	private Variable timeStart;
	private Variable timeEnd;
	
	public Transition(){}
	public Transition(int id, int targetRefId, Assignment assignment, boolean isTTS){
		this.id = id;
		this.targetRefId = targetRefId;
		this.assignment = assignment;
		this.isTTS = isTTS;
	}
	public Transition(int id, int targetRefId, Assignment assignment, boolean isTTS, Variable timeStart, Variable timeEnd)
	{
		this.id = id;
		this.targetRefId = targetRefId;
		this.assignment = assignment;
		this.isTTS = isTTS;
		this.timeStart = timeStart;
		this.timeEnd = timeEnd;
	}
	public String getTargetName() {
		return targetName;
	}
	public void setTargetName(String targetName) {
		this.targetName = targetName;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getTargetRefId() {
		return targetRefId;
	}
	public void setTargetRefId(int targetRefId) {
		this.targetRefId = targetRefId;
	}
	public Assignment getAssignment() {
		return assignment;
	}
	public void setAssignment(Assignment assignment) {
		this.assignment = assignment;
	}
	public boolean isTTS() {
		return isTTS;
	}
	public void setTTS(boolean isTTS) {
		this.isTTS = isTTS;
	}
	public Variable getTimeStart() {
		return timeStart;
	}
	public void setTimeStart(Variable timeStart) {
		this.timeStart = timeStart;
	}
	public Variable getTimeEnd() {
		return timeEnd;
	}
	public void setTimeEnd(Variable timeEnd) {
		this.timeEnd = timeEnd;
	}

}
