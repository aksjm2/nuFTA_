package Controller;

import model.FaultTreeNode;
import model.GateType;

public class FomulaMaker {
	public String formulaMaker(FaultTreeNode root){
		String result="";
		if(root.getGateType()==GateType.AND || root.getGateType()==GateType.OR){
			result+="(";
			for(FaultTreeNode ft : root.getChilds()){
				result += formulaMaker(ft);
				if(!ft.equals(root.getChilds().get(root.getChilds().size()-1))){
					result += root.getText();
				}
			}
			result+=")";
		}else if(root.isFormula()){
			result+=root.getText();
		}else{
			for(FaultTreeNode ft : root.getChilds()){
				result += formulaMaker(ft);
			}
		}
		return result;
	}
}
