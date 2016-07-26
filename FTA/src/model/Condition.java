package model;

public class Condition {
	private String rawCondition;
	private boolean isNot;
	
	public Condition(String rawCondition, boolean isNot){
		this.rawCondition = rawCondition;
		this.isNot = isNot;
	}

	public String getRawCondition() {
		return rawCondition;
	}

	public void setRawCondition(String rawCondition) {
		this.rawCondition = rawCondition;
	}

	public boolean isNot() {
		return isNot;
	}

	public void setNot(boolean isNot) {
		this.isNot = isNot;
	}
	
	
}
