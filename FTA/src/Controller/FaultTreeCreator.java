package Controller;

import java.util.ArrayList;
import model.Node;
import model.Variable;
import model.VariableType;
import model.FaultTreeNode;
import model.GateType;
import model.SDT;
import model.FSM;
import model.TTS;
import model.Assignment;
import model.Condition;

public class FaultTreeCreator {
	
	private FSM_Template f_template;
	private SDT_Template s_template;
	private TTS_Template t_template;
	
	public FaultTreeCreator(){
		s_template = new SDT_Template();
	}
	
	public FaultTreeNode makeTree(Variable output, Node node){
		FaultTreeNode root = new FaultTreeNode("root");
		root.setVariable(output);
		
		if(node instanceof SDT){
			s_template.sdt_template(root,output,(SDT)node);
		}

		return root;
	}
	public void expendStates(){}
	
	public FaultTreeNode conditionToLogic(Node n, Variable output, String rawCondition){
		//TODO  : ¹üÀ§??
		ArrayList<FaultTreeNode> list = new ArrayList<FaultTreeNode>();
		int oldStart=0;
		int count=0;
		for(int i=0; i<rawCondition.length(); i++){
			if(i==rawCondition.length()-1){
				String temp = rawCondition.substring(oldStart,i+1);
				FaultTreeNode conditionNode = new FaultTreeNode(temp);
				conditionNode.setVariable(strToVariable(temp));
				conditionNode.setFormula(true);
				list.add(conditionNode);
			}else if(rawCondition.charAt(i) == '&' || rawCondition.charAt(i) == '|'){
				if(oldStart<i){
					String temp = rawCondition.substring(oldStart, i);
					FaultTreeNode conditionNode = new FaultTreeNode(temp);
					conditionNode.setVariable(strToVariable(temp));
					conditionNode.setFormula(true);
					list.add(conditionNode);
				}
				FaultTreeNode gate = new FaultTreeNode(rawCondition.charAt(i)+"");
				gate.setGateType(GateType.OR);
				list.add(gate);
				oldStart = i+1;
			}else if(rawCondition.charAt(i) == '('){
				count++;
				for(int index = i+1; index<rawCondition.length(); index++){
					if(rawCondition.charAt(index)=='(')
						count++;
					else if(rawCondition.charAt(index)==')')
						count--;
					if(count==0)
					{
						FaultTreeNode bracket = conditionToLogic(n,output,rawCondition.substring(i+1, index));
						list.add(bracket);
						i = index;
						oldStart = i+1;
						break;
					}
				}
			}
		}
		for(int i = 0; i<list.size(); i++){
			if(i%2!=0){
				if(list.get(i-1).getParent()==null){
					list.get(i).addChild(list.get(i-1));
					list.get(i-1).setParent(list.get(i));
				}else{
					list.get(i).addChild(list.get(i-2));
					list.get(i-2).setParent(list.get(i));
				}
				if(list.size()>i+1){
					if(list.get(i+1).getParent()==null){
						list.get(i).addChild(list.get(i+1));
						list.get(i+1).setParent(list.get(i));
					}
				}
				count = i;
			}
		}
		merge(list.get(count));
		
		return list.get(count);
	}
	public Variable strToVariable(String text)
	{
		Variable result = null;
		for(int i =0; i<text.length(); i++){
			if(!(text.matches("true") || text.matches("false"))){
				if(text.charAt(i) == '=')
				{
					result = new Variable(text.substring(0,i),VariableType.CONSTANT,text.substring(i));
				}
				else if(text.charAt(i) == '>'){
					try{
					result = new Variable(text.substring(0,i),VariableType.RANGE,0,Integer.parseInt(text.substring(i+2)));
					}catch(NumberFormatException e){
						result = new Variable(text.substring(0,i),VariableType.RANGE,0,-1);
					}
				}
				else if(text.charAt(i) == '<'){
					try{
						result = new Variable(text.substring(0,i),VariableType.RANGE,Integer.parseInt(text.substring(i+2)),0);
					}catch(NumberFormatException e){
						result = new Variable(text.substring(0,i),VariableType.RANGE,0,-1);
					}
				}else{
					result = new Variable(text.substring(0,i),-1,text.substring(i));
				}
			}else{
				if(text.charAt(i) == '='){
					result = new Variable(text.substring(0,i),VariableType.BOOL,text.substring(i));
				}else{
					result = new Variable(text.substring(0,i),-1,text.substring(i));
				}
			}
		}
		return result;
	}
	public void merge(FaultTreeNode root){
		if(root.getChilds().size()!=0){
			for(FaultTreeNode ft : root.getChilds()){
				merge(ft);
			}
		}else
		{
			root.setVariable(strToVariable(root.getText()));
			if(root.getVariable().getType()==VariableType.RANGE && root.getParent().getText().equals("&")){
				if(root.getParent().getChilds().size()>=2){
					for(int i=0; i<root.getParent().getChilds().size(); i++){
						FaultTreeNode temp = root.getParent().getChilds().get(i);
						if(!temp.equals(root)){
							if(temp.getVariable().getName().equals(root.getVariable().getName())){
								if(temp.getVariable().getMax()==-1){
									root.getVariable().setMin(temp.getVariable().getMin());
								}else{
									if(temp.getVariable().getMax()<root.getVariable().getMax()){
										root.getVariable().setMax(temp.getVariable().getMax());
									}
									if(temp.getVariable().getMin()>root.getVariable().getMin()){
										root.getVariable().setMin(temp.getVariable().getMin());
									}
								}
								if(temp.getVariable().getMin()==-1){
									root.getVariable().setMax(temp.getVariable().getMax());
								}else{
									if(temp.getVariable().getMax()<root.getVariable().getMax()){
										root.getVariable().setMax(temp.getVariable().getMax());
									}
									if(temp.getVariable().getMin()>root.getVariable().getMin()){
										root.getVariable().setMin(temp.getVariable().getMin());
									}
								}
								temp.setParent(null);
								root.getParent().getChilds().remove(temp);
							} 
						}
					}
				}
				if(root.getParent().getChilds().size()==1){
					FaultTreeNode temp = root.getParent().getParent();
					root.getParent().setParent(null);
					temp.getChilds().remove(root.getParent());
					temp.getChilds().add(root);
					root.setParent(temp);
				}
			}
		}
	}
	public FaultTreeNode conditionGenerateNode(Node node, Variable output, Assignment tassign){
		FaultTreeNode tRoot = new FaultTreeNode(tassign.getOutput().getName());
		FaultTreeNode and = new FaultTreeNode("&",GateType.AND);
		tRoot.addChild(and);
		for(int i=0; i<tassign.getConditions().size(); i++){
			Condition tcond = tassign.getConditions().get(i);
			if(tcond.isNot()){
				tcond.setRawCondition(this.notTranslation(tcond.getRawCondition()));
			}
			FaultTreeNode temp = conditionToLogic(node,output,tcond.getRawCondition());
			and.addChild(temp);
			temp.setParent(and);
		}
		return tRoot;
	}

	public String notTranslation(String text) {
		String translateString = "";
		int check = 0;
		for (int i = 0; i < text.length(); i++) {
			if (text.charAt(i) == '=') {
				if (text.charAt(i - 1) != '<' & text.charAt(i - 1) != '>') {
					translateString += text.substring(check, i);
					translateString += "!=";
					check = i + 1;
				}
			} else if (text.charAt(i) == '<') {
				translateString += text.substring(check, i);
				if (text.charAt(i + 1) == '=') {
					translateString += ">";
					check = i + 2;
				} else {
					translateString += ">=";
					check = i + 1;
				}
			} else if (text.charAt(i) == '>') {
				translateString += text.substring(check, i);
				if (text.charAt(i + 1) == '=') {
					translateString += "<";
					check = i + 2;
				} else {
					translateString += "<=";
					check = i + 1;
				}
			} else if (text.charAt(i) == '&') {
				translateString += text.substring(check, i);
				translateString += "|";
				check = i + 1;
			} else if (text.charAt(i) == '|') {
				translateString += text.substring(check, i);
				translateString += "&";
				check = i + 1;
			}
		}
		translateString += text.substring(check, text.length());
		return translateString;
	}
}
