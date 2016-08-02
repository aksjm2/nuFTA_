package Controller;

import java.util.ArrayList;

import org.apache.xerces.parsers.DOMParser;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import model.Assignment;
import model.Condition;
import model.Edge;
import model.FSM;
import model.IONode;
import model.SDT;
import model.State;
import model.TTS;
import model.Transition;
import model.Variable;
import model.VariableNode;
import model.VariableType;

public class XML_Parser {
	private ArrayList<Variable> typeTableList = new ArrayList<Variable>();
	private ArrayList<SDT> sdtlist = new ArrayList<SDT>();
	private ArrayList<FSM> fsmlist = new ArrayList<FSM>();
	private ArrayList<TTS> ttslist = new ArrayList<TTS>();
	private ArrayList<IONode> inputlist = new ArrayList<IONode>();
	private ArrayList<IONode> outputlist = new ArrayList<IONode>();
	private ArrayList<Edge> edgelist = new ArrayList<Edge>();

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
			parser(root);
			linkedge();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void getTypeTable(Node typeTable) {
		NodeList entryList = typeTable.getChildNodes();

		for (int i = 0; i < entryList.getLength(); i++) {
			if (entryList.item(i).getNodeName().equals("entry")) {
				NamedNodeMap attr = entryList.item(i).getAttributes();

				String[] tempType = attr.item(1).getNodeValue().split(" : ");

				if (tempType[1].equals("boolean")) {
					Variable tempVar = new Variable(attr.item(0).getNodeValue(), 2, "true");
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

	private void setConstants(NamedNodeMap attr, VariableNode n, int index) {
		if (!attr.item(index).getNodeValue().isEmpty()) {
			String[] constants = attr.item(index).getNodeValue().split("; ");
			for (int i = 0; i < constants.length; i++) {
				String[] stringConstant = constants[i].split(" := ");
				Variable varConstant = new Variable(stringConstant[0], VariableType.CONSTANT,
						String.valueOf(stringConstant[1]));
				n.addVariable(varConstant);
			}
		}
	}

	private void setPrevStateVar(NamedNodeMap attr, VariableNode n, int index) {
		if (!attr.item(index).getNodeValue().isEmpty()) {
			String[] prevStateVar = attr.item(index).getNodeValue().split("; ");
			for (int j = 0; j < prevStateVar.length; j++) {
				Variable varPrev = null;
				String[] stringPrev = prevStateVar[j].split(" : ");
				if (stringPrev[1].equals("boolean")) {
					varPrev = new Variable(stringPrev[0], 2, "true");
				} else {
					String[] range = stringPrev[1].split("\\..");
					varPrev = new Variable(stringPrev[0], 1, Integer.parseInt(range[1]), Integer.parseInt(range[0]));
				}
				n.addVariable(varPrev);
			}
		}
	}

	private void setMemoVar(NamedNodeMap attr, VariableNode n, int index) {
		if (!attr.item(index).getNodeValue().isEmpty()) {
			String[] memoVar = attr.item(index).getNodeValue().split("; ");
			for (int i = 0; i < memoVar.length; i++) {
				Variable varMemo = null;
				String[] stringMemo = memoVar[i].split(" : ");
				if (stringMemo[1].equals("boolean")) {
					varMemo = new Variable(stringMemo[0], 2, "true");
				} else {
					String[] range = stringMemo[1].split("\\..");
					varMemo = new Variable(stringMemo[0], 1, Integer.parseInt(range[1]), Integer.parseInt(range[0]));
				}
				n.addVariable(varMemo);
			}
		}
	}

	private void setAllPrevStateVar(VariableNode node) {
		int flag[] = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, };
		Variable myVar = null;
		for (int i = 0; i < typeTableList.size(); i++) {
			if (typeTableList.get(i).getName().equals(node.getName())) {

				if (typeTableList.get(i).getType() == VariableType.BOOL) {
					myVar = new Variable(typeTableList.get(i).getName(), VariableType.CONSTANT, "true");
				} else {
					myVar = new Variable(typeTableList.get(i).getName(), VariableType.RANGE,
							typeTableList.get(i).getMax(), typeTableList.get(i).getMin());
				}
				break;
			}
		}

		for (int i = 0; i < node.getVariables().size(); i++) {
			Variable tempVar = node.getVariable(i);
			for (int j = 0; j < flag.length; j++) {
				if (tempVar.getName().equals(node.getName() + "_t" + String.valueOf(j))) {
					flag[j] = 1;
				}
			}
		}

		for (int i = 0; i < flag.length; i++) {
			if (flag[i] == 0) {
				Variable var = null;
				if (myVar.getType() == VariableType.BOOL) {
					var = new Variable(myVar.getName() + "_t" + String.valueOf(i), myVar.getType(), "true");
				} else {
					var = new Variable(myVar.getName() + "_t" + String.valueOf(i), myVar.getType(), myVar.getMax(),
							myVar.getMin());
				}
				node.addVariable(var);
			}
		}
	}

	private void setOutput(Variable output, String name, String temp, VariableNode node) {
		String[] checkType = temp.split("_");

		if (checkType[checkType.length - 1].startsWith("t")) {
			ArrayList<Variable> varList = node.getVariables();
			for (int l = 0; l < varList.size(); l++) {
				if (varList.get(l).getName().equals(temp)) {
					output.setName(name);
					output.setType(1);
					output.setMax(varList.get(l).getMax());
					output.setMin(varList.get(l).getMin());
					// output = new Variable(name, 1, varList.get(l).getMax(),
					// varList.get(l).getMin());
					break;
				}
			}
		} else if (checkType[0].startsWith("k")) {
			ArrayList<Variable> varList = node.getVariables();
			for (int l = 0; l < varList.size(); l++) {
				if (varList.get(l).getName().equals(temp)) {
					output.setName(name);
					output.setType(1);
					output.setValue(varList.get(l).getValue());
					// output = new Variable(name, 0,
					// varList.get(l).getValue());
					break;
				}
			}
		} else {
			for (int l = 0; l < typeTableList.size(); l++) {
				if (typeTableList.get(l).getName().equals(temp)) {
					output.setName(name);
					output.setType(1);
					output.setMax(typeTableList.get(l).getMax());
					output.setMin(typeTableList.get(l).getMin());
					// output = new Variable(name, 1,
					// typeTableList.get(l).getMax(),
					// typeTableList.get(l).getMin());
					break;
				}
			}
		}
	}

	private Assignment setAssignment(String assignments, VariableNode node) {
		Variable output = new Variable("", 0, "");
		String[] outputString = assignments.split(":=");

		String temp = outputString[1];
		if (temp.equals("true") || temp.equals("false")) {
			output = new Variable(outputString[0], 2, temp);
		} else {
			if (temp.matches(".*\\-.*")) {
				String[] minus = temp.split("-");
				if (minus.length == 1) {
					setOutput(output, outputString[0], temp, node);
				} else {
					setOutput(output, outputString[0], minus[0], node);

					String[] minusSplit = minus[1].split("_");
					if (minusSplit[0].startsWith("k")) {
						ArrayList<Variable> varList = node.getVariables();
						for (int l = 0; l < varList.size(); l++) {
							if (varList.get(l).getName().equals(minus[1])) {
								output.setMax(output.getMax() - Integer.parseInt(varList.get(l).getValue()));
								output.setMin(output.getMin() - Integer.parseInt(varList.get(l).getValue()));
								if (output.getMin() < 0) {
									output.setMin(0);
								}
								break;
							}
						}
					} else {
						ArrayList<Variable> varList = node.getVariables();
						for (int l = 0; l < varList.size(); l++) {
							if (varList.get(l).getName().equals(minus[1])) {
								output.setMax(output.getMax() - varList.get(l).getMax());
								output.setMin(output.getMin() - varList.get(l).getMin());
								if (output.getMin() < 0) {
									output.setMin(0);
								}
								break;
							}
						}
					}
				}
			} else if (temp.matches(".*\\+.*")) {
				String[] plus = temp.split("\\+");

				if (plus.length == 1) {
					setOutput(output, outputString[0], temp, node);
				} else {
					setOutput(output, outputString[0], plus[0], node);

					String[] plusSplit = plus[1].split("_");
					if (plusSplit[0].startsWith("k")) {
						ArrayList<Variable> varList = node.getVariables();
						for (int l = 0; l < varList.size(); l++) {
							if (varList.get(l).getName().equals(plus[1])) {
								output.setMax(output.getMax() + Integer.parseInt(varList.get(l).getValue()));
								output.setMin(output.getMin() + Integer.parseInt(varList.get(l).getValue()));
								break;
							}
						}
					} else {
						ArrayList<Variable> varList = node.getVariables();
						for (int l = 0; l < varList.size(); l++) {
							if (varList.get(l).getName().equals(plus[1])) {
								output.setMax(output.getMax() + varList.get(l).getMax());
								output.setMin(output.getMin() + varList.get(l).getMin());
								break;
							}
						}
					}
				}
			} else {
				setOutput(output, outputString[0], temp, node);
			}
		}

		Assignment assignment = new Assignment(output);

		return assignment;
	}

	public void parser(Node list) {
		NodeList child = list.getChildNodes();

		for (int i = 0; i < child.getLength(); i++) {
			parser(child.item(i));

			if (child.item(i).getNodeName().equals("SDT")) {
				parseSDT(child.item(i));
			} else if (child.item(i).getNodeName().equals("FSM")) {
				parseFSM(child.item(i));
			} else if (child.item(i).getNodeName().equals("TTS")) {
				parseTTS(child.item(i));
			} else if (child.item(i).getNodeName().equals("input")) {
				NamedNodeMap inputInfo = child.item(i).getAttributes();
				IONode input = new IONode();
				if (!inputInfo.item(0).getNodeValue().isEmpty()) {
					input.setId(Integer.parseInt(inputInfo.item(0).getNodeValue()));
				}
				if (!inputInfo.item(1).getNodeValue().isEmpty()) {
					input.setName(inputInfo.item(1).getNodeValue());
				}
				inputlist.add(input);
			} else if (child.item(i).getNodeName().equals("output")) {
				NamedNodeMap outputInfo = child.item(i).getAttributes();
				IONode output = new IONode();
				if (!outputInfo.item(0).getNodeValue().isEmpty()) {
					output.setId(Integer.parseInt(outputInfo.item(0).getNodeValue()));
				}
				if (!outputInfo.item(1).getNodeValue().isEmpty()) {
					output.setName(outputInfo.item(1).getNodeValue());
				}
				outputlist.add(output);
			} else if (child.item(i).getNodeName().equals("FOD")) {
				NodeList fodchild = child.item(i).getChildNodes();
				makeEdge(fodchild);
			}
		}
	}

	public void parseSDT(Node sdtnode) {
		String conditionTable[][] = null;
		NamedNodeMap attr = sdtnode.getAttributes();

		SDT sdt = new SDT(attr.item(5).getNodeValue(), Integer.parseInt(attr.item(3).getNodeValue()));
		setConstants(attr, sdt, 1);
		setPrevStateVar(attr, sdt, 6);
		setMemoVar(attr, sdt, 4);
		setAllPrevStateVar(sdt);

		NodeList conac = sdtnode.getChildNodes();
		for (int i = 0; i < conac.getLength(); i++) {
			Node con = conac.item(i);
			if (con.getNodeName().equals("condition")) {
				NamedNodeMap rowCol = con.getAttributes();

				int nRow = Integer.parseInt(rowCol.item(1).getNodeValue());
				int nCol = Integer.parseInt(rowCol.item(0).getNodeValue());
				conditionTable = new String[nRow][nCol];

				NodeList cellList = con.getChildNodes();
				for (int j = 0; j < cellList.getLength(); j++) {
					Node cell = cellList.item(j);
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

				for (int j = 0; j < cellList.getLength(); j++) {
					Node cell = cellList.item(j);
					if (cell.getNodeName().equals("cell")) {
						NamedNodeMap value = cell.getAttributes();
						actionTable[Integer.parseInt(value.item(1).getNodeValue())][Integer
								.parseInt(value.item(0).getNodeValue())] = value.item(2).getNodeValue();
					}
				}

				for (int j = 0; j < actionTable.length; j++) {
					for (int k = 1; k < actionTable[j].length; k++) {
						if (actionTable[j][k].equals("O") || actionTable[j][k].equals("o")) {
							Assignment assignment = setAssignment(actionTable[j][0].replace(" ", ""), sdt);

							for (int l = 0; l < conditionTable.length; l++) {
								Condition condition = new Condition(conditionTable[l][0], true);
								if (conditionTable[l][k].equals("F")) {
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
		sdtlist.add(sdt);
	}

	public void parseFSM(Node fsmnode) {
		NamedNodeMap attr = fsmnode.getAttributes();

		FSM fsm = new FSM(attr.item(6).getNodeValue(), Integer.parseInt(attr.item(3).getNodeValue()));
		fsm.setInitialRefId(Integer.parseInt(attr.item(4).getNodeValue()));

		setConstants(attr, fsm, 1);
		setPrevStateVar(attr, fsm, 7);
		setMemoVar(attr, fsm, 5);
		setAllPrevStateVar(fsm);

		NodeList childnode = fsmnode.getChildNodes();
		Node states = null;
		Node transitions = null;
		for (int i = 0; i < childnode.getLength(); i++) {
			if (childnode.item(i).getNodeName().equals("states")) {
				states = childnode.item(i);
			} else if (childnode.item(i).getNodeName().equals("transitions")) {
				transitions = childnode.item(i);
			}
		}

		NodeList tempStates = states.getChildNodes();
		for (int i = 0; i < tempStates.getLength(); i++) {
			if (tempStates.item(i).getNodeName().equals("state")) {
				NamedNodeMap stateAttr = tempStates.item(i).getAttributes();
				State state = new State(stateAttr.item(1).getNodeValue(),
						Integer.parseInt(stateAttr.item(0).getNodeValue()));
				fsm.addState(state);
			}
		}

		NodeList tempTransitions = transitions.getChildNodes();
		for (int i = 0; i < tempTransitions.getLength(); i++) {
			if (tempTransitions.item(i).getNodeName().equals("transition")) {
				NamedNodeMap transitionAttr = tempTransitions.item(i).getAttributes();
				int id = Integer.parseInt(transitionAttr.item(0).getNodeValue());
				int sourceRefId = 0;
				int targetRefId = 0;
				Assignment assignment = null;
				Condition condition = null;

				NodeList transitionChild = tempTransitions.item(i).getChildNodes();
				for (int j = 0; j < transitionChild.getLength(); j++) {
					if (transitionChild.item(j).getNodeName().equals("source")) {
						NamedNodeMap sourceAttr = transitionChild.item(j).getAttributes();
						sourceRefId = Integer.parseInt(sourceAttr.item(0).getNodeValue());
					} else if (transitionChild.item(j).getNodeName().equals("target")) {
						NamedNodeMap targetAttr = transitionChild.item(j).getAttributes();
						targetRefId = Integer.parseInt(targetAttr.item(0).getNodeValue());
					} else if (transitionChild.item(j).getNodeName().equals("conditions")) {
						condition = new Condition(
								transitionChild.item(j).getTextContent().replaceAll("\n", "").replaceAll(" ", ""),
								false);
					} else if (transitionChild.item(j).getNodeName().equals("assignments")) {
						assignment = setAssignment(
								transitionChild.item(j).getTextContent().replaceAll("\n", "").replaceAll(" ", ""), fsm);
						assignment.addCondition(condition);
					}
				}

				Transition transition = new Transition(id, targetRefId, assignment, false);
				for (int j = 0; j < fsm.getStates().size(); j++) {
					if (targetRefId == fsm.getStates().get(j).getId()) {
						transition.setTargetName(fsm.getStates().get(j).getName());
					}
				}
				for (int j = 0; j < fsm.getStates().size(); j++) {
					if (sourceRefId == fsm.getStates().get(j).getId()) {
						fsm.getStates().get(j).addTransition(transition);
					}
				}
			}
		}
		fsmlist.add(fsm);
	}

	public void parseTTS(Node ttsnode) {
		NamedNodeMap attr = ttsnode.getAttributes();

		TTS tts = new TTS(attr.item(6).getNodeValue(), Integer.parseInt(attr.item(3).getNodeValue()));
		tts.setInitialRefId(Integer.parseInt(attr.item(4).getNodeValue()));

		setConstants(attr, tts, 1);
		setPrevStateVar(attr, tts, 7);
		setMemoVar(attr, tts, 5);
		setAllPrevStateVar(tts);

		if (!attr.item(0).getNodeValue().equals("")) {
			String[] split = attr.item(0).getNodeValue().replaceAll(";", "").replaceAll(" ", "").split(":");
			String[] range = split[1].split("\\..");
			Variable clock = new Variable(split[0], VariableType.RANGE, Integer.parseInt(range[1]),
					Integer.parseInt(range[0]));
			tts.setClock(clock);
		}

		NodeList childnode = ttsnode.getChildNodes();
		Node states = null;
		Node transitions = null;
		for (int i = 0; i < childnode.getLength(); i++) {
			if (childnode.item(i).getNodeName().equals("states")) {
				states = childnode.item(i);
			} else if (childnode.item(i).getNodeName().equals("transitions")) {
				transitions = childnode.item(i);
			}
		}

		NodeList tempStates = states.getChildNodes();
		for (int i = 0; i < tempStates.getLength(); i++) {
			if (tempStates.item(i).getNodeName().equals("state")) {
				NamedNodeMap stateAttr = tempStates.item(i).getAttributes();
				State state = new State(stateAttr.item(1).getNodeValue(),
						Integer.parseInt(stateAttr.item(0).getNodeValue()));
				tts.addState(state);
			}
		}

		NodeList tempTransitions = transitions.getChildNodes();
		for (int i = 0; i < tempTransitions.getLength(); i++) {
			if (tempTransitions.item(i).getNodeName().equals("transition")) {
				NamedNodeMap transitionAttr = tempTransitions.item(i).getAttributes();
				int id = Integer.parseInt(transitionAttr.item(0).getNodeValue());
				int sourceRefId = 0;
				int targetRefId = 0;
				Variable timeStart = null;
				Variable timeEnd = null;
				Assignment assignment = null;
				Condition condition = null;
				boolean isTTS = false;
				
				NodeList transitionChild = tempTransitions.item(i).getChildNodes();
				for (int j = 0; j < transitionChild.getLength(); j++) {
					if (transitionChild.item(j).getNodeName().equals("source")) {
						NamedNodeMap sourceAttr = transitionChild.item(j).getAttributes();
						sourceRefId = Integer.parseInt(sourceAttr.item(0).getNodeValue());
					} else if (transitionChild.item(j).getNodeName().equals("target")) {
						NamedNodeMap targetAttr = transitionChild.item(j).getAttributes();
						targetRefId = Integer.parseInt(targetAttr.item(0).getNodeValue());
					} else if (transitionChild.item(j).getNodeName().equals("time")) {
						NamedNodeMap timeAttr = transitionChild.item(j).getAttributes();
						isTTS = true;
						if (timeAttr.item(1).getNodeValue().equals("0")) {
							timeStart = new Variable("timeStart", VariableType.CONSTANT, "0");
						}
						ArrayList<Variable> varList = tts.getVariables();
						for (int k = 0; k < varList.size(); k++) {
							if (varList.get(k).getName().equals(timeAttr.item(0).getNodeValue())) {
								timeEnd = varList.get(k);
							}
							if (varList.get(k).getName().equals(timeAttr.item(1).getNodeValue())) {
								timeStart = varList.get(k);
							}
						}
					} else if (transitionChild.item(j).getNodeName().equals("conditions")) {
						condition = new Condition(
								transitionChild.item(j).getTextContent().replaceAll("\n", "").replaceAll(" ", ""),
								false);
					} else if (transitionChild.item(j).getNodeName().equals("assignments")) {
						assignment = setAssignment(
								transitionChild.item(j).getTextContent().replaceAll("\n", "").replaceAll(" ", ""), tts);
						assignment.addCondition(condition);
					}
				}

				Transition transition = new Transition(id, targetRefId, assignment, isTTS, timeStart, timeEnd);
				for (int j = 0; j < tts.getStates().size(); j++) {
					if (targetRefId == tts.getStates().get(j).getId()) {
						transition.setTargetName(tts.getStates().get(j).getName());
					}
				}
				for (int j = 0; j < tts.getStates().size(); j++) {
					if (sourceRefId == tts.getStates().get(j).getId()) {
						tts.getStates().get(j).addTransition(transition);
					}
				}
			}
		}
		ttslist.add(tts);
	}

	private void linkedge() {
		for (Edge e : this.edgelist) {
			boolean flag = false;
			for (IONode input : this.inputlist) {
				if (e.getDestination_id() == input.getId()) {
					input.addEdge(e);
					flag = true;
					break;
				}
			}
			if (flag)
				continue;
			for (IONode output : this.outputlist) {
				if (e.getDestination_id() == output.getId()) {
					output.addEdge(e);
					flag = true;
					break;
				}
			}
			if (flag)
				continue;
			for (SDT sdt : this.sdtlist) {
				if (e.getDestination_id() == sdt.getId()) {
					sdt.addEdge(e);
					flag = true;
					break;
				}
			}
			if (flag)
				continue;
			for (FSM fsm : this.fsmlist) {
				if (e.getDestination_id() == fsm.getId()) {
					fsm.addEdge(e);
					break;
				}
			}
		}
	}

	private void makeEdge(NodeList fodchilds) {
		int count = 0;
		for (int i = 0; i < fodchilds.getLength(); i++) {
			if (fodchilds.item(i).getNodeName().equals("FOD")) {
				count++;
				makeEdge(fodchilds.item(i).getChildNodes());
			}
		}
		if (count == 0) {
			for (int i = 0; i < fodchilds.getLength(); i++) {
				if (fodchilds.item(i).getNodeName().equals("transitions")) {
					NodeList transitions = fodchilds.item(i).getChildNodes();
					for (int j = 0; j < transitions.getLength(); j++) {
						if (transitions.item(j).getNodeName().equals("transition")) {
							NodeList transition = transitions.item(j).getChildNodes();
							Edge edge = new Edge();
							for (int k = 0; k < transition.getLength(); k++) {
								Node sourcedest = transition.item(k);
								if (sourcedest.getNodeName().equals("source")) {
									NamedNodeMap sourceAttr = sourcedest.getAttributes();
									edge.setSourceID(Integer.parseInt(sourceAttr.item(0).getNodeValue()));
								} else if (sourcedest.getNodeName().equals("target")) {
									NamedNodeMap destinationAttr = sourcedest.getAttributes();
									edge.setDestination_id(Integer.parseInt(destinationAttr.item(0).getNodeValue()));
								}
							}
							edgelist.add(edge);
						}
					}

				}
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

	public ArrayList<IONode> getInputlist() {
		return inputlist;
	}

	public void setInputlist(ArrayList<IONode> inputlist) {
		this.inputlist = inputlist;
	}

	public ArrayList<IONode> getOutputlist() {
		return outputlist;
	}

	public void setOutputlist(ArrayList<IONode> outputlist) {
		this.outputlist = outputlist;
	}

	public ArrayList<Edge> getEdgelist() {
		return edgelist;
	}

	public void setEdgelist(ArrayList<Edge> edgelist) {
		this.edgelist = edgelist;
	}

	public ArrayList<Variable> getTypeTableList() {
		return typeTableList;
	}

	public void setTypeTableList(ArrayList<Variable> typeTableList) {
		this.typeTableList = typeTableList;
	}

	public ArrayList<TTS> getTtslist() {
		return ttslist;
	}

	public void setTtslist(ArrayList<TTS> ttslist) {
		this.ttslist = ttslist;
	}
}