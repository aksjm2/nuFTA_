package Controller;

import model.AnnotatedState;
import model.Assignment;
import model.FSM;
import model.FaultTreeNode;
import model.GateType;
import model.TTS;
import model.Transition;
import model.Variable;
import model.VariableType;

public class TTS_Template {
	private TTS makeTTS;

	private void tts_template_Init(FaultTreeNode root, AnnotatedState annotatedState, Variable output) {
		FaultTreeNode out = new FaultTreeNode(
				annotatedState.getName() + "\n (" + annotatedState.getOrignState().getName() + ", out:="
						+ annotatedState.getAssignment().getOutput().getValue());
		FaultTreeNode and = new FaultTreeNode("&", GateType.AND);
		root.addChild(and);

		FaultTreeNode tmp_state_at_t = new FaultTreeNode(annotatedState.getOrignState().getName() + " at t" + "\n {"
				+ annotatedState.getOrignState().getName() + ", out:=" + annotatedState.getAssignment().getOutput());

		tmp_state_at_t.setFormula(true);

		FaultTreeNode tmp_transition = new FaultTreeNode("transitions");
		and.addChild(tmp_state_at_t);
		and.addChild(tmp_transition);

		FaultTreeNode or = new FaultTreeNode("|", GateType.OR);
		tmp_transition.addChild(or);
		or.addChild(this.setEnterTheState(annotatedState, output));
		or.addChild(this.setReaminTheState(annotatedState, output));
	}

	private FaultTreeNode setEnterTheState(AnnotatedState annotatedState, Variable output) {
		FaultTreeCreator ft = new FaultTreeCreator();
		FaultTreeNode tmp_Enter = new FaultTreeNode("Enter the state via state transition");
		if (annotatedState.getPrevAS().size() == 1) {
			AnnotatedState prevAnnotatedState = annotatedState.getPrevAS().get(0);
			FaultTreeNode tmp_tran = new FaultTreeNode(
					"transition" + prevAnnotatedState.getName() + " -> " + annotatedState.getName());
			tmp_Enter.addChild(tmp_tran);
			FaultTreeNode tmp_tran_and = new FaultTreeNode("&", GateType.AND);
			tmp_tran.addChild(tmp_tran_and);
			if (prevAnnotatedState.getPrevTransition().isTTS()) {
				FaultTreeNode tmp_tran_t_p = new FaultTreeNode(
						prevAnnotatedState.getName() + "for [" + prevAnnotatedState.getPrevTransition().getTimeStart()
								+ ", " + prevAnnotatedState.getPrevTransition().getTimeEnd() + "]" + "\n out:="
								+ prevAnnotatedState.getAssignment().getOutput().getValue());
				FaultTreeNode tmp_tran_condition = new FaultTreeNode(
						annotatedState.getAssignment().getConditions().get(0).getRawCondition() + "for ["
								+ prevAnnotatedState.getPrevTransition().getTimeStart() + ", "
								+ prevAnnotatedState.getPrevTransition().getTimeEnd() + "]" + "\n out:="
								+ prevAnnotatedState.getAssignment().getOutput().getValue());
				tmp_tran_and.addChild(tmp_tran_t_p);
				tmp_tran_and.addChild(tmp_tran_condition);
				tmp_tran_condition.addChild(ft.conditionToLogic(makeTTS, output,
						annotatedState.getAssignment().getConditions().get(0).getRawCondition()));
			} else {
				FaultTreeNode tmp_tran_t_p = new FaultTreeNode(
						prevAnnotatedState.getName() + "at t-p \n {" + prevAnnotatedState.getOrignState().getName()
								+ ", out:=" + prevAnnotatedState.getAssignment().getOutput().getValue() + "}");
				FaultTreeNode tmp_tran_condition = new FaultTreeNode(
						annotatedState.getAssignment().getConditions().get(0).getRawCondition() + "at t-p");
				tmp_tran_and.addChild(tmp_tran_t_p);
				tmp_tran_and.addChild(tmp_tran_condition);
				tmp_tran_condition.addChild(ft.conditionToLogic(makeTTS, output,
						annotatedState.getAssignment().getConditions().get(0).getRawCondition()));
			}

		} else {
			FaultTreeNode tmp_or = new FaultTreeNode("|", GateType.OR);
			tmp_Enter.addChild(tmp_or);
			for (AnnotatedState prevAnnotatedState : annotatedState.getPrevAS()) {
				FaultTreeNode tmp_tran = new FaultTreeNode(
						"transition" + prevAnnotatedState.getName() + " -> " + annotatedState.getName());
				tmp_or.addChild(tmp_tran);
				FaultTreeNode tmp_tran_and = new FaultTreeNode("&", GateType.AND);
				tmp_tran.addChild(tmp_tran_and);
				if (prevAnnotatedState.getPrevTransition().isTTS()) {
					FaultTreeNode tmp_tran_t_p = new FaultTreeNode(prevAnnotatedState.getName() + "for ["
							+ prevAnnotatedState.getPrevTransition().getTimeStart() + ", "
							+ prevAnnotatedState.getPrevTransition().getTimeEnd() + "]" + "\n out:="
							+ prevAnnotatedState.getAssignment().getOutput().getValue());
					FaultTreeNode tmp_tran_condition = new FaultTreeNode(
							annotatedState.getAssignment().getConditions().get(0).getRawCondition() + "for ["
									+ prevAnnotatedState.getPrevTransition().getTimeStart() + ", "
									+ prevAnnotatedState.getPrevTransition().getTimeEnd() + "]" + "\n out:="
									+ prevAnnotatedState.getAssignment().getOutput().getValue());
					tmp_tran_and.addChild(tmp_tran_t_p);
					tmp_tran_and.addChild(tmp_tran_condition);
					tmp_tran_condition.addChild(ft.conditionToLogic(makeTTS, output,
							annotatedState.getAssignment().getConditions().get(0).getRawCondition()));
				} else {
					FaultTreeNode tmp_tran_t_p = new FaultTreeNode(
							prevAnnotatedState.getName() + "at t-p \n {" + prevAnnotatedState.getOrignState().getName()
									+ ", out:=" + prevAnnotatedState.getAssignment().getOutput().getValue() + "}");
					FaultTreeNode tmp_tran_condition = new FaultTreeNode(
							annotatedState.getAssignment().getConditions().get(0).getRawCondition() + "at t-p");
					tmp_tran_and.addChild(tmp_tran_t_p);
					tmp_tran_and.addChild(tmp_tran_condition);
					tmp_tran_condition.addChild(ft.conditionToLogic(makeTTS, output,
							annotatedState.getAssignment().getConditions().get(0).getRawCondition()));
				}
			}
		}
		return tmp_Enter;
	}

	private FaultTreeNode setReaminTheState(AnnotatedState annotatedState, Variable output) {
		FaultTreeCreator ft = new FaultTreeCreator();
		FaultTreeNode tmp_Remain = new FaultTreeNode("Remain at the state because of\n disabled outgoing transition");
		if (annotatedState.getNextTransition().size() == 1) {
			Transition t = annotatedState.getNextTransition().get(0);
			FaultTreeNode tmp_tran = new FaultTreeNode(
					"not transition" + annotatedState.getName() + " -> " + t.getTargetRefId());
			tmp_Remain.addChild(tmp_tran);
			if (t.isTTS()) {
				FaultTreeNode tmp_tran_or = new FaultTreeNode("|", GateType.OR);
				tmp_tran.addChild(tmp_tran_or);
				FaultTreeNode tmp_disabled = new FaultTreeNode(
						"Remain at the state because of\n disabled outgoing transition");
				FaultTreeNode tmp_though = new FaultTreeNode("Remain at the state though enabled outgoing transition");
				tmp_tran_or.addChild(tmp_disabled);
				tmp_tran_or.addChild(tmp_though);
				// disabled 추가
				FaultTreeNode tmp_tran_and = new FaultTreeNode("&", GateType.AND);
				tmp_disabled.addChild(tmp_tran_and);
				FaultTreeNode tmp_tran_t_p = new FaultTreeNode(
						annotatedState.getName() + "for [" + t.getTimeStart() + ", " + t.getTimeEnd() + "]" + "\n out:="
								+ annotatedState.getAssignment().getOutput().getValue());

				FaultTreeNode tmp_tran_condition = new FaultTreeNode(
						"Exists not " + annotatedState.getAssignment().getConditions().get(0).getRawCondition()
								+ "for [" + t.getTimeStart() + ", " + t.getTimeEnd() + "]" + "\n out:="
								+ annotatedState.getAssignment().getOutput().getValue());
				tmp_tran_and.addChild(tmp_tran_t_p);
				tmp_tran_and.addChild(tmp_tran_condition);
				t.getAssignment().getConditions().get(0)
						.setRawCondition(ft.notTranslation(t.getAssignment().getConditions().get(0).getRawCondition()));
				tmp_tran_condition.addChild(ft.conditionToLogic(makeTTS, output,
						t.getAssignment().getConditions().get(0).getRawCondition()));

				FaultTreeNode tmp_tran_and_though = new FaultTreeNode("&", GateType.AND);
				tmp_disabled.addChild(tmp_tran_and_though);
				FaultTreeNode tmp_tran_t_p_though = new FaultTreeNode(
						annotatedState.getName() + "for [" + t.getTimeStart() + ", " + t.getTimeEnd() + "]" + "\n out:="
								+ annotatedState.getAssignment().getOutput().getValue());

				FaultTreeNode tmp_tran_condition_though = new FaultTreeNode(
						annotatedState.getAssignment().getConditions().get(0).getRawCondition() + "for ["
								+ t.getTimeStart() + ", " + t.getTimeEnd() + "]" + "\n out:="
								+ annotatedState.getAssignment().getOutput().getValue());
				tmp_tran_and.addChild(tmp_tran_t_p_though);
				tmp_tran_and.addChild(tmp_tran_condition_though);
				tmp_tran_condition.addChild(ft.conditionToLogic(makeTTS, output,
						t.getAssignment().getConditions().get(0).getRawCondition()));
			} else {
				FaultTreeNode tmp_tran_and = new FaultTreeNode("&", GateType.AND);
				tmp_tran.addChild(tmp_tran_and);
				FaultTreeNode tmp_tran_t_p = new FaultTreeNode(
						annotatedState.getName() + "at t-p \n {" + annotatedState.getOrignState().getName() + ", out:="
								+ annotatedState.getAssignment().getOutput().getValue() + "}");
				FaultTreeNode tmp_tran_condition = new FaultTreeNode(
						"not " + t.getAssignment().getConditions().get(0).getRawCondition() + "at t-p");
				tmp_tran_and.addChild(tmp_tran_t_p);
				tmp_tran_and.addChild(tmp_tran_condition);
				t.getAssignment().getConditions().get(0)
						.setRawCondition(ft.notTranslation(t.getAssignment().getConditions().get(0).getRawCondition()));
				tmp_tran_condition.addChild(ft.conditionToLogic(makeTTS, output,
						annotatedState.getAssignment().getConditions().get(0).getRawCondition()));
			}
		} else {
			FaultTreeNode tmp_and = new FaultTreeNode("&", GateType.AND);
			tmp_Remain.addChild(tmp_and);
			for (Transition t : annotatedState.getNextTransition()) {
				FaultTreeNode tmp_tran = new FaultTreeNode(
						"not transition" + annotatedState.getName() + " -> " + t.getTargetRefId());
				tmp_and.addChild(tmp_tran);
				if (t.isTTS()) {
					FaultTreeNode tmp_tran_or = new FaultTreeNode("|", GateType.OR);
					tmp_tran.addChild(tmp_tran_or);
					FaultTreeNode tmp_disabled = new FaultTreeNode(
							"Remain at the state because of\n disabled outgoing transition");
					FaultTreeNode tmp_though = new FaultTreeNode(
							"Remain at the state though enabled outgoing transition");
					tmp_tran_or.addChild(tmp_disabled);
					tmp_tran_or.addChild(tmp_though);
					// disabled 추가
					FaultTreeNode tmp_tran_and = new FaultTreeNode("&", GateType.AND);
					tmp_disabled.addChild(tmp_tran_and);
					FaultTreeNode tmp_tran_t_p = new FaultTreeNode(
							annotatedState.getName() + "for [" + t.getTimeStart() + ", " + t.getTimeEnd() + "]"
									+ "\n out:=" + annotatedState.getAssignment().getOutput().getValue());

					FaultTreeNode tmp_tran_condition = new FaultTreeNode(
							"Exists not " + annotatedState.getAssignment().getConditions().get(0).getRawCondition()
									+ "for [" + t.getTimeStart() + ", " + t.getTimeEnd() + "]" + "\n out:="
									+ annotatedState.getAssignment().getOutput().getValue());
					tmp_tran_and.addChild(tmp_tran_t_p);
					tmp_tran_and.addChild(tmp_tran_condition);
					t.getAssignment().getConditions().get(0).setRawCondition(
							ft.notTranslation(t.getAssignment().getConditions().get(0).getRawCondition()));
					tmp_tran_condition.addChild(ft.conditionToLogic(makeTTS, output,
							t.getAssignment().getConditions().get(0).getRawCondition()));

					FaultTreeNode tmp_tran_and_though = new FaultTreeNode("&", GateType.AND);
					tmp_disabled.addChild(tmp_tran_and_though);
					FaultTreeNode tmp_tran_t_p_though = new FaultTreeNode(
							annotatedState.getName() + "for [" + t.getTimeStart() + ", " + t.getTimeEnd() + "]"
									+ "\n out:=" + annotatedState.getAssignment().getOutput().getValue());

					FaultTreeNode tmp_tran_condition_though = new FaultTreeNode(
							annotatedState.getAssignment().getConditions().get(0).getRawCondition() + "for ["
									+ t.getTimeStart() + ", " + t.getTimeEnd() + "]" + "\n out:="
									+ annotatedState.getAssignment().getOutput().getValue());
					tmp_tran_and.addChild(tmp_tran_t_p_though);
					tmp_tran_and.addChild(tmp_tran_condition_though);
					tmp_tran_condition.addChild(ft.conditionToLogic(makeTTS, output,
							t.getAssignment().getConditions().get(0).getRawCondition()));
				} else {
					FaultTreeNode tmp_tran_and = new FaultTreeNode("&", GateType.AND);
					tmp_tran.addChild(tmp_tran_and);
					FaultTreeNode tmp_tran_t_p = new FaultTreeNode(
							annotatedState.getName() + "at t-p \n {" + annotatedState.getOrignState().getName()
									+ ", out:=" + annotatedState.getAssignment().getOutput().getValue() + "}");
					FaultTreeNode tmp_tran_condition = new FaultTreeNode(
							"not " + t.getAssignment().getConditions().get(0).getRawCondition() + "at t-p");
					tmp_tran_and.addChild(tmp_tran_t_p);
					tmp_tran_and.addChild(tmp_tran_condition);
					t.getAssignment().getConditions().get(0).setRawCondition(
							ft.notTranslation(t.getAssignment().getConditions().get(0).getRawCondition()));
					tmp_tran_condition.addChild(ft.conditionToLogic(makeTTS, output,
							annotatedState.getAssignment().getConditions().get(0).getRawCondition()));
				}
			}
		}
		return tmp_Remain;
	}

	public void tts_template(FaultTreeNode root, Variable output, TTS tts) {
		FaultTreeNode or = new FaultTreeNode("|", GateType.OR);
		root.addChild(or);
		makeTTS = tts;
		for (AnnotatedState annotatedState : tts.getAnnotatedStates()) {
			Assignment tassign = annotatedState.getAssignment();
			if (tassign.getOutput().getType() == VariableType.CONSTANT) {
				if (output.getType() == VariableType.CONSTANT) {
					if (tassign.getOutput().getValue().equals(output.getValue())) {
						this.tts_template_Init(or, annotatedState, tassign.getOutput());
					}
				} else if (output.getType() == VariableType.RANGE) {
					if (output.getMin() <= Integer.parseInt(tassign.getOutput().getValue())
							&& output.getMax() >= Integer.parseInt(tassign.getOutput().getValue())) {
						this.tts_template_Init(or, annotatedState, tassign.getOutput());
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
						this.tts_template_Init(or, annotatedState, tassign.getOutput());
					}
				} else if (output.getType() == VariableType.RANGE) {
					if (output.getMin() >= tassign.getOutput().getMin()) {
						tassign.getOutput().setMin(output.getMin());
						if (output.getMax() <= tassign.getOutput().getMax()) {
							tassign.getOutput().setMax(output.getMax());
							this.tts_template_Init(or, annotatedState, tassign.getOutput());
						}
					} else {
						if (output.getMax() <= tassign.getOutput().getMax()) {
							tassign.getOutput().setMax(output.getMax());
							this.tts_template_Init(or, annotatedState, tassign.getOutput());
						}
					}
				}
			}
		}
		// or 밑에 1개가 있을 경우에는 or를 없애고 or 아래에 있는 node를 root에 연결
		if (or.getChilds().size() == 1) {
			root.addChild(or.getChilds().get(0));
			root.getChilds().remove(or);
		}
	}
}
