package Controller;

import java.util.ArrayList;

import org.apache.xerces.parsers.DOMParser;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import model.Assignment;
import model.Condition;
import model.FSM;
import model.SDT;
import model.State;
import model.Transition;
import model.Variable;
import model.VariableType;

public class XML_Parser {
	private ArrayList<Variable> typeTableList = new ArrayList<Variable>();
	private ArrayList<SDT> sdtlist = new ArrayList<SDT>();
	private ArrayList<FSM> fsmlist = new ArrayList<FSM>();

	public void openNuSCR(String url) {
		DOMParser dom = new DOMParser();

		try {
			dom.parse(url);
			Document doc = dom.getDocument();
			NodeList node = doc.getElementsByTagName("FOD");
			Node root = node.item(1);

			NodeList type = doc.getElementsByTagName("TypeTable");
			Node typeTable = type.item(0);
			getTypeTable(typeTable);
			// for(int i = 0; i < typeTableList.size(); i++)
			// {
			// System.out.println(typeTableList.get(i).getName() + " " +
			// typeTableList.get(i).getType());
			// }
			parser(root);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void getTypeTable(Node typeTable) {
		NodeList entryList = typeTable.getChildNodes();
		
		for (int i = 0; i < entryList.getLength(); i++) {
			if (entryList.item(i).getNodeName().equals("entry")) {
				NamedNodeMap attr = entryList.item(i).getAttributes();

				String[] tempType = attr.item(1).getNodeValue().split(" : ");

				if (tempType[1].equals("boolean")) {
					Variable tempVar = new Variable(attr.item(0).getNodeValue(), 2, "t");
					typeTableList.add(tempVar);
				} else {
					String[] range = tempType[1].split("\\..");
					Variable tempVar = new Variable(attr.item(0).getNodeValue(), 1, Integer.parseInt(range[1]),
							Integer.parseInt(range[0]));
					typeTableList.add(tempVar);
				}
			}
		}
	}

	public void parser(Node list) {
		NodeList child = list.getChildNodes();

		for (int i = 0; i < child.getLength(); i++) {
			parser(child.item(i));

			if (child.item(i).getNodeName().equals("FSM")) {
				Node fsmnode = child.item(i);
				NamedNodeMap attr = fsmnode.getAttributes();

				FSM fsm = new FSM(attr.item(6).getNodeValue(), Integer.parseInt(attr.item(3).getNodeValue()));
				fsm.setInitialRefId(Integer.parseInt(attr.item(4).getNodeValue()));
				fsmlist.add(fsm);
				/*System.out.println("FSM name : "+fsm.getName());*/
				if (!attr.item(1).getNodeValue().isEmpty()) {
					String[] constants = attr.item(1).getNodeValue().split("; ");
					for (int j = 0; j < constants.length; j++) {
						String[] stringConstant = constants[j].split(" := ");
						Variable varConstant = new Variable(stringConstant[0], 0, String.valueOf(stringConstant[1]));
						fsm.addVariable(varConstant);
					}
				}
				if (!attr.item(7).getNodeValue().isEmpty()) {
					String[] prevStateVar = attr.item(7).getNodeValue().split("; ");
					for (int j = 0; j < prevStateVar.length; j++) {
						String[] stringPrev = prevStateVar[j].split(" : ");
						if (stringPrev[1].equals("boolean")) {
							Variable varPrev = new Variable(stringPrev[0], 2, "true");
							fsm.addVariable(varPrev);
						} else {
							String[] range = stringPrev[1].split("\\..");
							Variable varPrev = new Variable(stringPrev[0], 1, Integer.parseInt(range[1]),
									Integer.parseInt(range[0]));
							fsm.addVariable(varPrev);
						}
					}
				}
				if (!attr.item(5).getNodeValue().isEmpty()) {
					String[] memoVar = attr.item(5).getNodeValue().split("; ");
					for (int j = 0; j < memoVar.length; j++) {
						String[] stringMemo = memoVar[j].split(" : ");
						if (stringMemo[1].equals("boolean")) {
							Variable varMemo = new Variable(stringMemo[0], 2, "true");
							fsm.addVariable(varMemo);
						} else {
							String[] range = stringMemo[1].split("\\..");
							Variable varMemo = new Variable(stringMemo[0], 1, Integer.parseInt(range[1]),
									Integer.parseInt(range[0]));
							fsm.addVariable(varMemo);
						}
					}
				}

				NodeList stateTransitions = fsmnode.getChildNodes();
				for (int j = 0; j < stateTransitions.getLength(); j++) {
					Node states = stateTransitions.item(j);
					if (states.getNodeName().equals("states")) {
						NodeList stateschild = states.getChildNodes();
						for (int k = 0; k < stateschild.getLength(); k++) {
							Node state = stateschild.item(k);
							if (state.getNodeName().equals("state")) {
								NamedNodeMap st = state.getAttributes();
								State s = new State(st.item(1).getNodeValue(),
										Integer.parseInt(st.item(0).getNodeValue()));
								fsm.addState(s);
							}
						}
					} else if (states.getNodeName().equals("transitions")) {
						for (int k = 0; k < states.getChildNodes().getLength(); k++) {
							Node transID = states.getChildNodes().item(k);
							if (transID.getNodeName().equals("transition")) {
								NamedNodeMap transIDAttr = transID.getAttributes();
								Transition tr = new Transition();
								State s = null;
								Condition cond = null;
								tr.setId(Integer.parseInt(transIDAttr.item(0).getNodeValue()));
								for (int l = 0; l < transID.getChildNodes().getLength(); l++) {
									Node transInfo = transID.getChildNodes().item(l);
									if (transInfo.getNodeName().equals("source")) {
										NamedNodeMap source = transInfo.getAttributes();
										for (int m = 0; m < fsm.getStates().size(); m++) {
											State temp = fsm.getStates().get(m);
											if (temp.getId() == Integer.parseInt(source.item(0).getNodeValue())) {
												s = temp;
												break;
											}
										}
									} else if (transInfo.getNodeName().equals("target")) {
										NamedNodeMap target = transInfo.getAttributes();
										tr.setTargetName(target.item(1).getNodeValue());
										tr.setTargetRefId(Integer.parseInt(target.item(0).getNodeValue()));
									} else if (transInfo.getNodeName().equals("conditions")) {
										cond = new Condition(transInfo.getChildNodes().item(0).getNodeValue(), false);
									} else if(transInfo.getNodeName().equals("assignments")){
										Assignment assign = new Assignment();
										Variable output = null;
										String[] outputString = transInfo.getChildNodes().item(0).getNodeValue().trim().split(" :=");
										String temp = outputString[1];
										
										if (temp.startsWith(" ")) {
											temp = temp.substring(1, temp.length());
										}
										if(outputString[1].contains("\n")){
											outputString[1] = outputString[1].trim().replace("\n", "");
										}
										output = new Variable(outputString[0],VariableType.CONSTANT,temp);

										if (temp.equals("true") || temp.equals("false")) {
											output = new Variable(outputString[0], 2, temp);
										} else {

											String[] checkType = temp.split("_");

											if (checkType[checkType.length - 1].startsWith("t")) {
												ArrayList<Variable> varList = fsm.getVariables();
												for (int m = 0; m < varList.size(); m++) {
													if (varList.get(m).getName().equals(temp)) {
														output = new Variable(outputString[0], 1,
																varList.get(m).getMax(), varList.get(m).getMin());
														break;
													}
												}
											} else if (checkType[0].startsWith("k")) {
												ArrayList<Variable> varList = fsm.getVariables();
												for (int m = 0; m < varList.size(); m++) {
													if (varList.get(m).getName().equals(temp)) {
														output = new Variable(outputString[0], 0,
																varList.get(m).getMax(), varList.get(m).getMin());
														break;
													}
												}
											} else {
												if (temp.matches(".*\\-.*")) {
													String[] minus = temp.split(" - ");

													if (minus.length == 1) {
														for (int m = 0; m < typeTableList.size(); m++) {
															if (typeTableList.get(m).getName().equals(temp)) {
																output = new Variable(outputString[0], 1,
																		typeTableList.get(m).getMax(),
																		typeTableList.get(m).getMin());
																break;
															}
														}
													} else {
														for (int m = 0; m < typeTableList.size(); m++) {
															if (typeTableList.get(m).getName().equals(minus[0])) {
																output = new Variable(outputString[0], 1,
																		typeTableList.get(m).getMax(),
																		typeTableList.get(m).getMin());
																break;
															}
														}
														ArrayList<Variable> varList = fsm.getVariables();
														for (int m = 0; m < varList.size(); m++) {
															if (varList.get(m).getName().equals(minus[1])) {
																output.setMax(
																		output.getMax() - varList.get(m).getMax());
																output.setMin(
																		output.getMin() - varList.get(m).getMin());
																if (output.getMin() < 0) {
																	output.setMin(0);
																}
																break;
															}
														}
													}
												} else if (temp.matches(".*\\+.*")) {
													String[] plus = temp.split(" \\+ ");

													if (plus.length == 1) {
														for (int m = 0; m < typeTableList.size(); m++) {
															if (typeTableList.get(m).getName().equals(temp)) {
																output = new Variable(outputString[0], 1,
																		typeTableList.get(m).getMax(),
																		typeTableList.get(m).getMin());
																break;
															}
														}
													} else {
														for (int m = 0; m < typeTableList.size(); m++) {
															if (typeTableList.get(m).getName().equals(plus[0])) {
																output = new Variable(outputString[0], 1,
																		typeTableList.get(m).getMax(),
																		typeTableList.get(m).getMin());
																break;
															}
														}
														ArrayList<Variable> varList = fsm.getVariables();
														for (int m = 0; m < varList.size(); m++) {
															if (varList.get(m).getName().equals(plus[1])) {
																output.setMax(
																		output.getMax() + varList.get(m).getMax());
																output.setMin(
																		output.getMin() + varList.get(m).getMin());
																break;
															}
														}
													}
												} else {
													for (int m = 0; m < typeTableList.size(); m++) {
														if (typeTableList.get(m).getName().equals(temp)) {
															output = new Variable(outputString[0], 1,
																	typeTableList.get(m).getMax(),
																	typeTableList.get(m).getMin());
															break;
														}
													}
												}
											}
										}
										assign.setOutput(output);
										assign.addCondition(cond);
										tr.setAssignment(assign);
										s.addTransition(tr);
									}
								}
							}
						}
					}
				}
			} else if (child.item(i).getNodeName().equals("SDT")) {
				String conditionTable[][] = null;

				Node sdtnode = child.item(i);
				NamedNodeMap attr = sdtnode.getAttributes();
				// for (int j = 0; j < attr.getLength(); j++) {
				// System.out.println(attr.item(j).getNodeName() + " : " +
				// attr.item(j).getNodeValue());
				// }

				SDT sdt = new SDT(attr.item(5).getNodeValue(), Integer.parseInt(attr.item(3).getNodeValue()));
				this.sdtlist.add(sdt);
				if (!attr.item(1).getNodeValue().isEmpty()) {
					String[] constants = attr.item(1).getNodeValue().split("; ");
					for (int j = 0; j < constants.length; j++) {
						String[] stringConstant = constants[j].split(" := ");
						Variable varConstant = new Variable(stringConstant[0], 0, String.valueOf(stringConstant[1]));
						sdt.addVariable(varConstant);
					}
				}

				if (!attr.item(4).getNodeValue().isEmpty()) {
					String[] memoVar = attr.item(4).getNodeValue().split("; ");
					for (int j = 0; j < memoVar.length; j++) {
						String[] stringMemo = memoVar[j].split(" : ");
						if (stringMemo[1].equals("boolean")) {
							Variable varMemo = new Variable(stringMemo[0], 2, "true");
							sdt.addVariable(varMemo);
						} else {
							String[] range = stringMemo[1].split("\\..");
							Variable varMemo = new Variable(stringMemo[0], 1, Integer.parseInt(range[1]),
									Integer.parseInt(range[0]));
							sdt.addVariable(varMemo);
						}
					}
				}

				if (!attr.item(6).getNodeValue().isEmpty()) {
					String[] prevStateVar = attr.item(6).getNodeValue().split("; ");
					for (int j = 0; j < prevStateVar.length; j++) {
						String[] stringPrev = prevStateVar[j].split(" : ");
						if (stringPrev[1].equals("boolean")) {
							Variable varPrev = new Variable(stringPrev[0], 2, "true");
							sdt.addVariable(varPrev);
						} else {
							String[] range = stringPrev[1].split("\\..");
							Variable varPrev = new Variable(stringPrev[0], 1, Integer.parseInt(range[1]),
									Integer.parseInt(range[0]));
							sdt.addVariable(varPrev);
						}
					}
				}

				NodeList conac = sdtnode.getChildNodes();
				for (int j = 0; j < conac.getLength(); j++) {
					Node con = conac.item(j);
					if (con.getNodeName().equals("condition")) {
						NamedNodeMap rowCol = con.getAttributes();

						int nRow = Integer.parseInt(rowCol.item(1).getNodeValue());
						int nCol = Integer.parseInt(rowCol.item(0).getNodeValue());
						conditionTable = new String[nRow][nCol];

						NodeList cellList = con.getChildNodes();
						for (int k = 0; k < cellList.getLength(); k++) {
							Node cell = cellList.item(k);
							if (cell.getNodeName().equals("cell")) {
								NamedNodeMap value = cell.getAttributes();
								conditionTable[Integer.parseInt(value.item(1).getNodeValue())][Integer
										.parseInt(value.item(0).getNodeValue())] = value.item(2).getNodeValue();
							}
						}
					} else if (con.getNodeName().equals("action")) {
						NodeList cellList = con.getChildNodes();
						NamedNodeMap rowCol = con.getAttributes();
						int nRow = Integer.parseInt(rowCol.item(1).getNodeValue());
						int nCol = Integer.parseInt(rowCol.item(0).getNodeValue());

						String[][] actionTable = new String[nRow][nCol];

						for (int k = 0; k < cellList.getLength(); k++) {
							Node cell = cellList.item(k);
							if (cell.getNodeName().equals("cell")) {
								NamedNodeMap value = cell.getAttributes();
								actionTable[Integer.parseInt(value.item(1).getNodeValue())][Integer
										.parseInt(value.item(0).getNodeValue())] = value.item(2).getNodeValue();
							}
						}

						for (int k = 0; k < actionTable.length; k++) {
							for (int l = 1; l < actionTable[k].length; l++) {
								if (actionTable[k][l].equals("O") || actionTable[k][l].equals("o")) {
									Variable output = null;
									String[] outputString = actionTable[k][0].split(" := ");

									String temp = outputString[1];
									if (temp.startsWith(" ")) {
										temp = temp.substring(1, temp.length());
									}

									if (temp.equals("true") || temp.equals("false")) {
										output = new Variable(outputString[0], 2, temp);
									} else {

										String[] checkType = temp.split("_");

										if (checkType[checkType.length - 1].startsWith("t")) {
											ArrayList<Variable> varList = sdt.getVariables();
											for (int m = 0; m < varList.size(); m++) {
												if (varList.get(m).getName().equals(temp)) {
													output = new Variable(outputString[0], 1, varList.get(m).getMax(),
															varList.get(m).getMin());
													break;
												}
											}
										} else if (checkType[0].startsWith("k")) {
											ArrayList<Variable> varList = sdt.getVariables();
											for (int m = 0; m < varList.size(); m++) {
												if (varList.get(m).getName().equals(temp)) {
													output = new Variable(outputString[0], 0, varList.get(m).getMax(),
															varList.get(m).getMin());
													break;
												}
											}
										} else {
											if (temp.matches(".*\\-.*")) {
												String[] minus = temp.split(" - ");

												if (minus.length == 1) {
													for (int m = 0; m < typeTableList.size(); m++) {
														if (typeTableList.get(m).getName().equals(temp)) {
															output = new Variable(outputString[0], 1,
																	typeTableList.get(m).getMax(),
																	typeTableList.get(m).getMin());
															break;
														}
													}
												} else {
													for (int m = 0; m < typeTableList.size(); m++) {
														if (typeTableList.get(m).getName().equals(minus[0])) {
															output = new Variable(outputString[0], 1,
																	typeTableList.get(m).getMax(),
																	typeTableList.get(m).getMin());
															break;
														}
													}
													ArrayList<Variable> varList = sdt.getVariables();
													for (int m = 0; m < varList.size(); m++) {
														if (varList.get(m).getName().equals(minus[1])) {
															output.setMax(output.getMax() - varList.get(m).getMax());
															output.setMin(output.getMin() - varList.get(m).getMin());
															if (output.getMin() < 0) {
																output.setMin(0);
															}
															break;
														}
													}
												}
											} else if (temp.matches(".*\\+.*")) {
												String[] plus = temp.split(" \\+ ");

												if (plus.length == 1) {
													for (int m = 0; m < typeTableList.size(); m++) {
														if (typeTableList.get(m).getName().equals(temp)) {
															output = new Variable(outputString[0], 1,
																	typeTableList.get(m).getMax(),
																	typeTableList.get(m).getMin());
															break;
														}
													}
												} else {
													for (int m = 0; m < typeTableList.size(); m++) {
														if (typeTableList.get(m).getName().equals(plus[0])) {
															output = new Variable(outputString[0], 1,
																	typeTableList.get(m).getMax(),
																	typeTableList.get(m).getMin());
															break;
														}
													}
													ArrayList<Variable> varList = sdt.getVariables();
													for (int m = 0; m < varList.size(); m++) {
														if (varList.get(m).getName().equals(plus[1])) {
															output.setMax(output.getMax() + varList.get(m).getMax());
															output.setMin(output.getMin() + varList.get(m).getMin());
															break;
														}
													}
												}
											} else {
												for (int m = 0; m < typeTableList.size(); m++) {
													if (typeTableList.get(m).getName().equals(temp)) {
														output = new Variable(outputString[0], 1,
																typeTableList.get(m).getMax(),
																typeTableList.get(m).getMin());
														break;
													}
												}
											}
										}
									}
									Assignment assignment = new Assignment(output);
									/*System.out.println(output.getName());*/
									for (int m = 0; m < conditionTable.length; m++) {
										Condition condition = new Condition(conditionTable[m][0], true);
										if (conditionTable[m][l].equals("F")) {
											condition.setNot(false);
										}
										assignment.addCondition(condition);
									}
									sdt.addAssignment(assignment);
								}
							}
						}

					}
				}

			} else if (child.item(i).getNodeName().equals("TTS")) {

			} else if (child.item(i).getNodeName().equals("input")) {

			} else if (child.item(i).getNodeName().equals("ouputs")) {

			}
		}
	}

	public ArrayList<SDT> getSdtlist() {
		return sdtlist;
	}

	public void setSdtlist(ArrayList<SDT> sdtlist) {
		this.sdtlist = sdtlist;
	}
	
	public ArrayList<FSM> getFsmlist() {
		return fsmlist;
	}

	public void setFsmlist(ArrayList<FSM> fsmlist) {
		this.fsmlist = fsmlist;
	}

}
