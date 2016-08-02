package model;

public class Variable {
	
	private String name;
	private int type;
	private String value;
	private int max;
	private int min;
	
	public Variable(String name, int type, String value){
		this.name = name;
		this.type = type;
		this.value = value;
		this.max = -1;
		this.min = -1;
	}
	public Variable(String name, int type, int max, int min){
		this.name = name;
		this.type = type;
		this.max = max;
		this.min = min;
		this.value = " ";
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public int getMax() {
		return max;
	}
	public void setMax(int max) {
		this.max = max;
	}
	public int getMin() {
		return min;
	}
	public void setMin(int min) {
		this.min = min;
	}

}
