package model;

import java.util.ArrayList;

public class FaultTreeNode {

	private int gateType;
	private String text;
	private boolean isFormula;
	private Variable variable;
	private ArrayList<FaultTreeNode> childs;
	private FaultTreeNode parent;
	
	public FaultTreeNode(String text){
		this.text = text;
		this.childs = new ArrayList<FaultTreeNode>();
		this.parent = null;
		this.gateType = -1;
	}
	public FaultTreeNode(String text, int gateType){
		this.text = text;
		this.gateType = gateType;
		this.childs = new ArrayList<FaultTreeNode>();
		this.parent = null;
	}
	public FaultTreeNode(String text, int gateType, boolean isFormula){
		this.gateType = gateType;
		this.text = text;
		this.isFormula = isFormula;
		this.childs = new ArrayList<FaultTreeNode>();
		this.parent = null;
	}
	
	public Variable getVariable() {
		return variable;
	}
	public void setVariable(Variable variable) {
		this.variable = variable;
	}
	public FaultTreeNode getParent() {
		return parent;
	}
	public void setParent(FaultTreeNode parent) {
		this.parent = parent;
	}
	public void addChild(FaultTreeNode child){
		this.childs.add(child);
	}
	public int getGateType() {
		return gateType;
	}
	public void setGateType(int gateType) {
		this.gateType = gateType;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public boolean isFormula() {
		return isFormula;
	}
	public void setFormula(boolean isFormula) {
		this.isFormula = isFormula;
	}
	public ArrayList<FaultTreeNode> getChilds() {
		return childs;
	}
	public void setChilds(ArrayList<FaultTreeNode> childs) {
		this.childs = childs;
	}
}
