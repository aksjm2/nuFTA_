package Controller;

import model.*;
import java.util.ArrayList;

public class MainController {
	
	public static void main(String[] args){
		XML_Parser parser = new XML_Parser();
		parser.openNuSCR("example.xml");

		Variable output = new Variable("testoutput",VariableType.CONSTANT,"1");
		
		
		
		FaultTreeCreator fc = new FaultTreeCreator();
		
		FomulaMaker fm = new FomulaMaker();
		System.out.println("######################################");
		System.out.println(fm.formulaMaker(fc.makeTree(output,parser.getSdtlist().get(1))));
	}

}
