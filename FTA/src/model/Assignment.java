package model;

import java.util.ArrayList;

public class Assignment {
	Variable output;
	ArrayList<Condition> conditions;

	public Assignment(){
		this.conditions = new ArrayList<Condition>();
	}
	public Assignment(Variable output){
		this.output = output;
		this.conditions = new ArrayList<Condition>();
	}
	public void setOutput(Variable output){
		this.output = output;
	}
	public void addCondition(Condition condition){
		this.conditions.add(condition);
	}
	public void addCondition(String rawCondition, boolean isNot){
		Condition c = new Condition(rawCondition, isNot);
		this.conditions.add(c);
	}
	public Variable getOutput(){
		return this.output;
	}
	public ArrayList<Condition> getConditions(){
		return this.conditions;
	}
}
