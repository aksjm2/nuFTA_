package Controller;

import model.*;

public class ExtendState {
	public void init(FSM test) {
		//State Max 셋팅
		State s1 = new State("Max", 1);
		Assignment s1_assignment = new Assignment();
		Variable s1_variable = new Variable("h_Spt", VariableType.CONSTANT, "k_Spt_Ini");
		s1_assignment.setOutput(s1_variable);
		s1_assignment.addCondition("Initial", false);
		
		Assignment s1_assignment2 = new Assignment();
		s1_assignment2.addCondition("A and D", false);
		
		Transition s1_transition1 = new Transition(10, 1, s1_assignment, false);
		Transition s1_transition2 = new Transition(11, 2, s1_assignment2, false);
		s1.addTransition(s1_transition1);		
		s1.addTransition(s1_transition2);
		
		//State Falling 셋팅
		State s2 = new State("Falling", 2);
		Assignment s2_assignment = new Assignment();
		Variable s2_variable = new Variable("h_Spt", VariableType.RANGE, "f_X - k_Int");
		s2_assignment.setOutput(s2_variable);
		s2_assignment.addCondition("A and B and C", false);
		Transition s2_transition1 = new Transition(20, 2, s2_assignment, false);
		
		Assignment s2_assignment2 = new Assignment();
		Variable s2_variable2 = new Variable("h_Spt", VariableType.CONSTANT, "k_LLimit");
		s2_assignment2.setOutput(s2_variable2);
		s2_assignment2.addCondition("A and B and not C", false);
		Transition s2_transition2 = new Transition(21, 3, s2_assignment2, false);
		
		Assignment s2_assignment3 = new Assignment();
		Variable s2_variable3 = new Variable("h_Spt", VariableType.RANGE, "f_X - k_Int");
		s2_assignment3.setOutput(s2_variable3);
		s2_assignment3.addCondition("not A and E", false);
		Transition s2_transition3 = new Transition(22, 4, s2_assignment3, false);
		
		s2.addTransition(s2_transition1);
		s2.addTransition(s2_transition2);
		s2.addTransition(s2_transition3);
		
		State s3 = new State("Reset_Limit", 3);
		Assignment s3_assignment = new Assignment();
		Variable s3_variable = new Variable("h_Spt", VariableType.RANGE, "f_X - k_Int");
		s3_assignment.setOutput(s3_variable);
		s3_assignment.addCondition("C", false);
		Transition s3_transition = new Transition(30, 4, s3_assignment, false);
		
		/*Assignment s3_assignment2 = new Assignment();
		Variable s3_variable2 = new Variable("h_Spt", VariableType.CONSTANT, "k_Spt_Ini");
		s3_assignment2.setOutput(s3_variable2);
		s3_assignment2.addCondition("A and B", false);
		Transition s3_transition2 = new Transition(31, 1, s3_assignment2, false);*/
		
		s3.addTransition(s3_transition);
		//s3.addTransition(s3_transition2);
		
		State s4 = new State("Rising", 4);
		Assignment s4_assignment = new Assignment();
		s4_assignment.addCondition("A and D", false);
		Transition s4_transition = new Transition(40, 2, s4_assignment, false);
		
		Assignment s4_assignment2 = new Assignment();
		Variable s4_variable2 = new Variable("h_Spt", VariableType.RANGE, "f_X - k_Int");
		s4_assignment2.setOutput(s4_variable2);
		s4_assignment2.addCondition("not A and D", false);
		Transition s4_transition2 = new Transition(41, 4, s4_assignment2, false);
		
		Assignment s4_assignment3 = new Assignment();
		Variable s4_variable3 = new Variable("h_Spt", VariableType.RANGE, "k_Spt_Ini");
		s4_assignment3.setOutput(s4_variable3);
		s4_assignment3.addCondition("not A and not D", false);
		Transition s4_transition3 = new Transition(42, 1, s4_assignment3, false);
		
		s4.addTransition(s4_transition);
		s4.addTransition(s4_transition2);
		s4.addTransition(s4_transition3);
		
		test.addState(s1);
		test.addState(s2);
		test.addState(s3);
		test.addState(s4);
	}
	
	public void printAll(FSM test){
		System.out.println("=========================");
		for(State s : test.getStates()){
			System.out.println("State Name : " + s.getName() + " State id : "+s.getId());
			for(Transition t : s.getTransitions()){
				System.out.println("Target Id : " + t.getTargetRefId());
				if(t.getAssignment().getOutput() != null){
					System.out.println("Output : " + t.getAssignment().getOutput().getValue());
				}else{
					System.out.println("Output : Null");
				}
				
				for(Condition c : t.getAssignment().getConditions()){
					System.out.println("Condition : " + c.getRawCondition());
				}
			}
			System.out.println("=========================");
		}
	}
	public void extendFSM(FSM test) {
		boolean checkInit = false;
		for(State s : test.getStates()){
			for(Transition t : s.getTransitions()){
				for(Condition c : t.getAssignment().getConditions()){
					if(c.getRawCondition().equals("Initial")){
						AnnotatedState exState = new AnnotatedState();
						exState.setOrignState(s);
						exState.setPrevTransition(t);
						exState.setAssignment(t.getAssignment());
						exState.setName(s.getName() +"_"+ String.valueOf(s.getAnnotatedCount()));
						for(Transition tmp_t : s.getTransitions()){
							exState.addNextTransition(tmp_t);
						}
						test.addAnnotatedStates(exState);
						checkInit = true;
						break;
					}
				}
				if(checkInit){
					break;
				}
			}
			if(checkInit){
				break;
			}
		}
		this.extendFSM(test, test.getAnnotatedStates().get(0));
	}
	
	public void extendFSM(FSM test, AnnotatedState ext) {
		for(Transition t : ext.getNextTransition()){
			if(ext.getPrevTransition().equals(t)){
				continue;
			}
			
			boolean exist = false;
			//현재의 transition의 output 값이 없을 경우 그 전 AnnotatedState의 output 값이 현재  
			if (t.getAssignment().getOutput() == null) {
				t.getAssignment().setOutput(ext.getPrevTransition().getAssignment().getOutput());
			}
			
			for(AnnotatedState extTmp : test.getAnnotatedStates()){
				//현재 만들려는 AnnotatedState가 이미 만들어져 있고 그 것을 A 라고 하면 A의 이전 확장상태를 ext로 설정
				if (extTmp.getPrevTransition().equals(t) && (extTmp.getAssignment().getOutput()
						.equals(t.getAssignment().getOutput()))) {
					exist = true;
					extTmp.addPrevAs(ext);
					break;
				}
			}
			
			if(!exist){
				AnnotatedState tmp = new AnnotatedState();
				tmp.setPrevTransition(t);
				tmp.setAssignment(t.getAssignment());
				for(State s : test.getStates()){
					if(t.getTargetRefId() == s.getId()){
						tmp.setName(s.getName()+"_"+String.valueOf(s.getAnnotatedCount()));
						tmp.setOrignState(s);
						for(Transition add_t : s.getTransitions()){
							tmp.addNextTransition(add_t);
						}
						break;
					}
				}
				tmp.addPrevAs(ext);
				test.addAnnotatedStates(tmp);
				this.extendFSM(test, tmp);
			}
		}
	}
	public void mergeInitialExtendState(FSM test){
		boolean flag = false;
		AnnotatedState ext = test.getAnnotatedStates().get(0);
		if(ext.getAssignment().getOutput().getValue().startsWith("k_")){
			for(AnnotatedState tmp : test.getAnnotatedStates()){
				if(tmp.equals(ext)){
					continue;
				}
				if(ext.getOrignState().getId() == tmp.getOrignState().getId()){
					if(tmp.getAssignment().getOutput().getValue().equals(ext.getAssignment().getOutput().getValue())){
						String setting = tmp.getPrevTransition().getAssignment().getConditions().get(0)
								.getRawCondition() + " | " + ext.getPrevTransition().getAssignment().getConditions().get(0).getRawCondition();
						tmp.getPrevTransition().getAssignment().getConditions().get(0).setRawCondition(setting);
						flag = true;
					}
				}
			}
		}
		if(flag){
			for(AnnotatedState tmp : test.getAnnotatedStates()){
				for(AnnotatedState tmp2 : tmp.getPrevAS()){
					if(tmp2.equals(ext)){
						tmp.getPrevAS().remove(tmp2);
					}
				}
			}
			test.getAnnotatedStates().remove(0);
		}
	}
	public static void main(String[] args) {
		ExtendState exState = new ExtendState();
		FSM test = new FSM("Test", 0);
		exState.init(test);
		exState.printAll(test);
		exState.extendFSM(test);
		exState.mergeInitialExtendState(test);
		System.out.println("===============================================");

		for (AnnotatedState ext : test.getAnnotatedStates()) {
			System.out.print("Name : " + ext.getName() + " Origin State :" + ext.getOrignState().getName()+" PrevState : ");
			for(AnnotatedState tmp : ext.getPrevAS()){
				System.out.print(tmp.getName() + ", ");
			}
			System.out.println(" prev Cond : " + ext.getPrevTransition().getAssignment().getConditions().get(0).getRawCondition()
					+ " Output : " + ext.getAssignment().getOutput().getValue());
		}
		System.out.println("=============================================");
		
	}
}
