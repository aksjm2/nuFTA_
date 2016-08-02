package Controller;

import model.AnnotatedState;
import model.Assignment;
import model.FSM;
import model.FaultTreeNode;
import model.GateType;
import model.Transition;
import model.Variable;
import model.VariableType;

public class FSM_Template {
	private FSM makeFSM;
	public FSM_Template(){
		
	}
	private void fsm_template_Init(FaultTreeNode root, AnnotatedState annotatedState, Variable output){
		FaultTreeNode out = new FaultTreeNode(
				annotatedState.getName() + "\n (" + annotatedState.getOrignState().getName() + ", out:="
						+ annotatedState.getAssignment().getOutput().getValue() + ")");
		FaultTreeNode and = new FaultTreeNode("&", GateType.AND);
		root.addChild(out);
		out.addChild(and);
		FaultTreeNode tmp_state_at_t = new FaultTreeNode(annotatedState.getName() + " at t" + "\n {"
				+ annotatedState.getOrignState().getName() + ", out:=" + annotatedState.getAssignment().getOutput().getValue() + "}");
		tmp_state_at_t.setFormula(true);
		FaultTreeNode tmp_transition = new FaultTreeNode("transitions");
		and.addChild(tmp_state_at_t);
		and.addChild(tmp_transition);
		FaultTreeNode or = new FaultTreeNode("|", GateType.OR);
		tmp_transition.addChild(or);
		or.addChild(this.setEnterTheState(annotatedState, output));
		or.addChild(this.setReaminTheState(annotatedState, output));
	}
	
	private FaultTreeNode setEnterTheState(AnnotatedState annotatedState, Variable output){
		FaultTreeCreator ft = new FaultTreeCreator();
		FaultTreeNode tmp_Enter = new FaultTreeNode("Enter the state via state transition");
		FaultTreeNode tmp_or = new FaultTreeNode("|", GateType.OR);
		tmp_Enter.addChild(tmp_or);
		for (AnnotatedState prevAnnotatedState : annotatedState.getPrevAS()) {
			FaultTreeNode tmp_tran = new FaultTreeNode(
					"transition " + prevAnnotatedState.getName() + " -> " + annotatedState.getName());
			tmp_or.addChild(tmp_tran);
			FaultTreeNode tmp_tran_and = new FaultTreeNode("&", GateType.AND);
			tmp_tran.addChild(tmp_tran_and);
			FaultTreeNode tmp_tran_t_p = new FaultTreeNode(
					prevAnnotatedState.getName() + " at t-p \n {" + prevAnnotatedState.getOrignState().getName()
							+ ", out:=" + prevAnnotatedState.getAssignment().getOutput().getValue() + "}");
			FaultTreeNode tmp_tran_condition = new FaultTreeNode(
					annotatedState.getAssignment().getConditions().get(0).getRawCondition() + " at t-p");
			tmp_tran_and.addChild(tmp_tran_t_p);
			tmp_tran_and.addChild(tmp_tran_condition);
			tmp_tran_condition.addChild(ft.conditionToLogic(makeFSM, output,
			annotatedState.getAssignment().getConditions().get(0).getRawCondition()));
		}
		if(tmp_or.getChilds().size() ==1){
			tmp_Enter.getChilds().remove(tmp_or);
			tmp_Enter.getChilds().add(tmp_or.getChilds().get(0));
		}
		return tmp_Enter;
	}
	
	private FaultTreeNode setReaminTheState(AnnotatedState annotatedState, Variable output){
		FaultTreeCreator ft = new FaultTreeCreator();
		FaultTreeNode tmp_Remain = new FaultTreeNode("Remain at the state because of\n disabled outgoing transition");
		FaultTreeNode tmp_and = new FaultTreeNode("&", GateType.AND);
		tmp_Remain.addChild(tmp_and);
		for (Transition t : annotatedState.getNextTransition()) {
			FaultTreeNode tmp_tran = new FaultTreeNode(
					"not transition " + annotatedState.getName() + " -> " + t.getTargetName());
			tmp_and.addChild(tmp_tran);
			FaultTreeNode tmp_tran_and = new FaultTreeNode("&", GateType.AND);
			tmp_tran.addChild(tmp_tran_and);
			FaultTreeNode tmp_tran_t_p = new FaultTreeNode(
					annotatedState.getName() + " at t-p \n {" + annotatedState.getOrignState().getName() + ", out:="
							+ annotatedState.getAssignment().getOutput().getValue() + "}");
			FaultTreeNode tmp_tran_condition = new FaultTreeNode(
					"not " + t.getAssignment().getConditions().get(0).getRawCondition() + " at t-p");
			tmp_tran_and.addChild(tmp_tran_t_p);
			tmp_tran_and.addChild(tmp_tran_condition);
			String notCondition = ft.notTranslation(t.getAssignment().getConditions().get(0).getRawCondition());
			tmp_tran_condition.addChild(ft.conditionToLogic(makeFSM, output,
					notCondition));
		}
		if(tmp_and.getChilds().size() == 1){
			tmp_Remain.getChilds().remove(tmp_and);
			tmp_Remain.addChild(tmp_and.getChilds().get(0));
		}
		return tmp_Remain;
	}

	public void fsm_template(FaultTreeNode root, Variable output, FSM fsm) {
		FaultTreeNode or = new FaultTreeNode("|", GateType.OR);
		root.addChild(or);
		makeFSM = fsm;
		for(AnnotatedState annotatedState : fsm.getAnnotatedStates()){
			Assignment tassign = annotatedState.getAssignment();
			if (tassign.getOutput().getType() == VariableType.CONSTANT) {
				if (output.getType() == VariableType.CONSTANT) {
					if (tassign.getOutput().getValue().equals(output.getValue())) {
						this.fsm_template_Init(or, annotatedState, tassign.getOutput());
					}
				} else if (output.getType() == VariableType.RANGE) {
					if (output.getMin() <= Integer.parseInt(tassign.getOutput().getValue())
							&& output.getMax() >= Integer.parseInt(tassign.getOutput().getValue())) {
						this.fsm_template_Init(or, annotatedState, tassign.getOutput());
					}
				}
			} else if (tassign.getOutput().getType() == VariableType.RANGE) {
				if (output.getType() == VariableType.CONSTANT) {
					
					if (tassign.getOutput().getMin() <= Integer.parseInt(output.getValue())
							&& tassign.getOutput().getMax() >= Integer.parseInt(output.getValue())) {
						tassign.getOutput().setType(VariableType.CONSTANT);
						tassign.getOutput().setMax(-1);
						tassign.getOutput().setMin(-1);
						tassign.getOutput().setValue(output.getValue());
						this.fsm_template_Init(or, annotatedState, tassign.getOutput());
					}
				} else if (output.getType() == VariableType.RANGE) {
					if (output.getMin() >= tassign.getOutput().getMin()) {
						tassign.getOutput().setMin(output.getMin());
						if (output.getMax() <= tassign.getOutput().getMax()) {
							tassign.getOutput().setMax(output.getMax());
							this.fsm_template_Init(or, annotatedState, tassign.getOutput());
						}
					} else {
						if (output.getMax() <= tassign.getOutput().getMax()) {
							tassign.getOutput().setMax(output.getMax());
							this.fsm_template_Init(or, annotatedState, tassign.getOutput());
						}
					}
				}
			}
		}
		// or 밑에 1개가 있을 경우에는 or를 없애고 or 아래에 있는 node를 root에 연결
		if(or.getChilds().size() == 1){
			root.addChild(or.getChilds().get(0));
			root.getChilds().remove(or);
		}

	}
}
