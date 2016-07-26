package Controller;

import model.Assignment;
import model.FaultTreeNode;
import model.GateType;
import model.Node;
import model.SDT;
import model.Variable;
import model.VariableType;

public class SDT_Template {
	
	public void sdt_template(FaultTreeNode root, Variable output, SDT sdt){
		FaultTreeCreator ft = new FaultTreeCreator();
		FaultTreeNode or = new FaultTreeNode("|",GateType.OR);
		root.addChild(or);
		or.setParent(root);
		for(int i=0; i<sdt.getAssignments().size(); i++){
			Assignment tassign = sdt.getAssignments().get(i);
			tassign.getOutput();
			tassign.getOutput().getName();
			tassign.getOutput().getType();
			if(tassign.getOutput().getType()==VariableType.CONSTANT){
				if(output.getType()==VariableType.CONSTANT){
					if(tassign.getOutput().getValue().equals(output.getValue())){
						FaultTreeNode temp = ft.conditionGenerateNode((Node)sdt,output,tassign);
						or.addChild(temp);
						temp.setParent(or);
					}
				}else if(output.getType()==VariableType.RANGE){
					if(output.getMin()<=Integer.parseInt(tassign.getOutput().getValue()) && output.getMax()>=Integer.parseInt(tassign.getOutput().getValue())){
						FaultTreeNode temp = ft.conditionGenerateNode((Node)sdt,output,tassign);
						or.addChild(temp);
						temp.setParent(or);
					}
				}
			}else if(tassign.getOutput().getType()==VariableType.RANGE){
				if(output.getType()==VariableType.CONSTANT){
					if(tassign.getOutput().getMin()<=Integer.parseInt(output.getValue()) && tassign.getOutput().getMax()>=Integer.parseInt(output.getValue())){
						tassign.getOutput().setType(VariableType.CONSTANT);
						tassign.getOutput().setMax(-1);
						tassign.getOutput().setMin(-1);
						tassign.getOutput().setValue(output.getValue());
						
						FaultTreeNode temp = ft.conditionGenerateNode((Node)sdt,output,tassign);
						or.addChild(temp);
						temp.setParent(or);
					}
				}else if(output.getType()==VariableType.RANGE){
					if(output.getMin()>=tassign.getOutput().getMin()){
						tassign.getOutput().setMin(output.getMin());
						if(output.getMax()<=tassign.getOutput().getMax()){
							tassign.getOutput().setMax(output.getMax());	
						}
						FaultTreeNode temp = ft.conditionGenerateNode((Node)sdt,output,tassign);
						or.addChild(temp);
						temp.setParent(or);
					}else{
						if(output.getMax()<=tassign.getOutput().getMax()){
							tassign.getOutput().setMax(output.getMax());
							FaultTreeNode temp = ft.conditionGenerateNode((Node)sdt,output,tassign);
							or.addChild(temp);
							temp.setParent(or);
						}
					}
				}
			}
		}
	}
}
