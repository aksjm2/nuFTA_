package Controller;

import model.*;
import java.util.ArrayList;

public class MainController {
	
	public static void main(String[] args){
		XML_Parser parser = new XML_Parser();
		parser.openNuSCR("example.xml");

		Variable output = new Variable("f_VAR_OVER_PWR_Trip_Out",VariableType.BOOL,"true");
		
		FaultTreeCreator fc = new FaultTreeCreator();
		
		FomulaMaker fm = new FomulaMaker();
		System.out.println("######################################");
		int count = 0;
		for(SDT sdt : parser.getSdtlist()){
			for(Assignment assign : sdt.getAssignments()){
				if(assign.getOutput().getName().equals(output.getName())){
					count++;
				}
			}
			if(count>0){
			/*	System.out.println(fm.formulaMaker(fc.makeTree(output,sdt)));*/
				count--;
			}
		}
		count=0;
		for(FSM fsm : parser.getFsmlist()){
			if(fsm.getName().equals("h_VAR_OVER_PWR_Int_SP")){
				for(State ts : fsm.getStates()){
					if(ts.getName().equals("Normal")){
						for(Transition tt : ts.getTransitions()){
							System.out.println("====================================");
							System.out.println(tt.getAssignment().getConditions().get(0).getRawCondition());
							System.out.println("====================================");
						}
					}
				}
			}
		}
	}

}
