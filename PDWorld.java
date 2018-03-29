import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class PDWorld {
	//Defining alpha and gamma
	final double learning_rate=0.3;
	final double discount_rate=0.5;
	//Defining States
	final static int state11 = 0,state12 = 1, state13 = 2, state14 = 3, state15 = 4;
	final static int state21 = 5,state22 = 6,state23 = 7,state24 = 8,state25 = 9;
	final static int state31 = 10,state32 = 11,state33 = 12,state34 = 13,state35 = 14;
	final static int state41 = 15,state42 = 16,state43 = 17,state44 = 18,state45 = 19;
	final static int state51 =20,state52 =21,state53=22,state54=23,state55=24;
	//Defining pickup and dropoff state
	final static int statesCount = 25;
	static int total = 0;
	static int balance=0; //Bank Balance

	static ArrayList<int[]> operationsFrom = new ArrayList<int[]>();

	/** Two Q-tables one for pickup and hold ***/
	static double[][] QHold = new double[statesCount][statesCount];
	static double[][] QPickup = new double[statesCount][statesCount];
	int[][] R = new int[statesCount][statesCount]; //Reward Matrix

	public PDWorld() {
		init();
	}

	public void init() {
		R[state11][state11]=12;
		R[state33][state33]=12;
		R[state55][state55]=12;
		R[state41][state41]=12;
		R[state44][state44]=12;
		R[state51][state51]=12;
	}



	public static void main(String[] args) {
		// TODO Auto-generated method stub

		/** List of operations that can be performed from a particular state 
		 *  north,south,east,west
		 */
		operationsFrom.add(new int[] {0,state21,state12,0});
		operationsFrom.add(new int[] {0,state22,state13,state11});
		operationsFrom.add(new int[] {0,state23,state14,state12});
		operationsFrom.add(new int[] {0,state24,state15,state13});
		operationsFrom.add(new int[] {0,state25,0,state14});
		operationsFrom.add(new int[] {state11,state31,state22,0});
		operationsFrom.add(new int[] {state12,state32,state23,state21});
		operationsFrom.add(new int[] {state13,state33,state24,state22});
		operationsFrom.add(new int[] {state14,state34,state25,state23});
		operationsFrom.add(new int[] {state15,state35,0,state24});
		operationsFrom.add(new int[] {state21,state41,state32,0});
		operationsFrom.add(new int[] {state22,state42,state33,state31});
		operationsFrom.add(new int[] {state23,state43,state34,state32});
		operationsFrom.add(new int[] {state24,state44,state35,state33});
		operationsFrom.add(new int[] {state25,state45,0,state34});
		operationsFrom.add(new int[] {state31,state51,state42,0});
		operationsFrom.add(new int[] {state32,state52,state43,state41});
		operationsFrom.add(new int[] {state33,state53,state44,state42});
		operationsFrom.add(new int[] {state34,state54,state45,state43});
		operationsFrom.add(new int[] {state35,state55,0,state44});
		operationsFrom.add(new int[] {state41,0,state52,0});
		operationsFrom.add(new int[] {state42,0,state53,state51});
		operationsFrom.add(new int[] {state43,0,state54,state52});
		operationsFrom.add(new int[] {state44,0,state55,state53});
		operationsFrom.add(new int[] {state45,0,0,state54});

		PDWorld obj = new PDWorld();
		obj.operators(state15, 0, 4,4,4,4,0,0,1);
		//System.out.println("Balance == "+balance);
		obj.operators(state15, 0, 4,4,4,4,0,0,2);
		//System.out.println("b222==="+balance);
		obj.operators(state15, 0, 4,4,4,4,0,0,3);
		System.out.println(" FINAL PICKUP TABLE");
		for(int i=0;i<statesCount;i++) {
			for(int j=0;j<6;j++) {
				System.out.print(QPickup[i][j]);
				System.out.print("\t");
			}
			System.out.println("\n");

		}
		System.out.println("FINAL DROPOFF TABLE");
		for(int i=0;i<statesCount;i++) {
			for(int j=0;j<6;j++) {
				System.out.print(QHold[i][j]);
				System.out.print("\t");
			}
			System.out.println("\n");

		}

	}


	public void operators(int state,int hold,int p1,int p2,int p3,int p4,int d1,int d2,int experimentNo) {
		/*** For defining policy - Random-0,PGreedy-1, PExploit(Q-learning)-2,PExploit(SARSA)-3 ***/
		int policy=0;
		/**** For displaying steps after restart ***/
		int j = 0;

		/*** Loop for 6000 steps***/
		for(int i=0;i<6000;i++) {
			//System.out.println("d1== "+d1+"  d2===  "+d2+"  index==="+i);
			/**** to change policy after certain steps ***/
			if(experimentNo == 1) {
				/** changing policy to PGREEDY  using Q-learning**/
				if(i>=3000) {
					policy = 1;
				}
			}else if(experimentNo == 2) {
				/** changing policy to PEXPLOIT using Q-learning  **/
				if(i>199) {
					policy = 2;
				}
			}else if(experimentNo == 3) {
				if(i>199) {
					/** changing policy to PEXPLOIT using SARSA**/
					policy = 3;
				}
			}

			/****** if goal state is reached restart it    ****/
			//System.out.println("action==="+state);
			if(d1 > 7 && d2 >7){

				if(j > 0)
					System.out.println("Steps before restart =="+(i-j));
				else
					System.out.println("Steps before restart == "+i);
				j = i;
				System.out.println("Bank Balance == "+balance);
				
				/** Reseting all values to initial state **/
				d1 =0;d2=0;
				p1=4;p2=4;p3=4;p4=4;
				total += balance;
				balance = 0;
				state = state15;
			}

			/**** if pickup operation is available  *****/
			else if(state == state11 || state == state41 || state == state33 || state == state55) {
				if(hold == 0) { 
					if(p1 >=1 && state == state11) {
						p1 -= 1;
					}else if(p2 >=1 && state == state41) {
						p2 -=1;
					}else if(p3 >=1 && state == state33) {
						p3 -=1;
					}else if(p4 >=1 && state == state55) {
						p4 -=1;
					}
					balance += R[state][state];
					hold = 1;
				}else {
					state= northSouthEastWest(state,policy,experimentNo,hold);
				}
			}
			/***** if dropoff operation is available   ****/
			else if(state == state51 || state == state44) {
				if(hold == 1) {
					if(d1<8 && state == state51) {
						d1 +=1;
					}else if(d2<8 && state ==  state44) {
						d2 +=1;
					}
					balance += R[state][state];
					hold=0;
				}else {
					state = northSouthEastWest(state,policy,experimentNo,hold);
				}
			}


			/****** if pickup and dropoff not available do choose between n-s-e-w    ****/
			else{
				state = northSouthEastWest(state,policy,experimentNo,hold);		
			}
			if(i == 5999 && experimentNo == 1)
				System.out.println("BALANCE FOR EXPERIMENT 1 === "+total);
			else if(i == 5999 && experimentNo == 2)
				System.out.println("BALANCE FOR EXPERIMENT 2 === "+total);
			else if(i == 5999 && experimentNo == 3)
				System.out.println("BALANCE FOR EXPERIMENT 3 === "+total);
		}
	}

	int northSouthEastWest(int state,int policy,int expNo, int hold) {

		Random random = new Random();
		int[] actionsFromState = operationsFrom.get(state);
		double q=0,maxQ=0,value=0;
		int nextState=0,action=0,index=0,r=-1;

		/*** RANDOM POLICY  ***/
		if(policy == 0) {
			while(action ==0){
				index = random.nextInt(actionsFromState.length);
				action = actionsFromState[index];
			}
			if(hold ==0)
				q = QPickup(state,action);
			else
				q = QHold(state,action);
			nextState= action;
			/*** RANDOM POLICY USING Q-LEARNING  ***/ 

			maxQ = maxQNextState(nextState,hold);
			value= ((1-learning_rate)*q)+(learning_rate*(r+discount_rate*(maxQ)));
			/*** RANDOM POLICY USING SARSA   ***/
		}
		/***** GREEDY POLICY  USING Q-LEARNING  ***/
		else if(policy ==1) {
			List<Double> qValuesList = new ArrayList<Double>();
			List<Integer> randomAction = new ArrayList<Integer>();

			for(int i=0;i<actionsFromState.length;i++) {
				action = actionsFromState[i];
				if(action == 0) {
					continue;
				}
				if(hold == 0)
					q = QPickup(state,action);
				else
					q = QHold(state, action);
				qValuesList.add(q);
			}
			double maxValue = Collections.max(qValuesList);

			for(int i=0;i<actionsFromState.length;i++) {
				action = actionsFromState[i];
				if(action == 0) {
					continue;
				}
				if(hold == 0)
					q = QPickup(state,action);
				else
					q = QHold(state, action);
				if(q == maxValue) {
					randomAction.add(action);
				}

			}
			if(randomAction.size()>1) {
				index = random.nextInt(randomAction.size()-1);
				action = randomAction.get(index);
			}else {
				action = randomAction.get(0);
			}

			/*** To determine Q-values of all state   ****/

			maxQ = maxQNextState(nextState,hold);
			value= ((1-learning_rate)*q)+(learning_rate*(r+discount_rate*(maxQ)));

		}
		/*** PEXPLOIT POLICY USING Q-LEARNING  ***/
		else if(policy ==2 || policy ==3) {

			nextState = pExploit(state, actionsFromState, hold);
			if(hold == 0)
				q = QPickup(state,nextState);
			else
				q = QHold(state, nextState);

			if(policy == 2) {

				maxQ = maxQNextState(nextState,hold);
				value= ((1-learning_rate)*q)+(learning_rate*(r+discount_rate*(maxQ)));
			}
			/*** PEXPLOIT POLICY USING SARSA   ***/
			else if(policy == 3) {
				int actionOfAction = pExploit(nextState, actionsFromState, hold);
				double qNextState =0;
				if(hold == 0)
					qNextState = QPickup(nextState,actionOfAction);
				else
					qNextState = QHold(nextState,actionOfAction);

				value = ((1-learning_rate)*q)+(learning_rate*(r+discount_rate*(qNextState)));
			}
		}

		setQ(state,nextState,value,hold);
		state= nextState;
		//System.out.println("state==="+state);

		//System.out.println("state==="+state);
		balance -=1;
		return state;
	}


	/*** This method is for retreiving Q-value from the table   ****/
	double QHold(int state,int action) {
		return QHold[state][action];
	}

	double QPickup(int state, int action) {
		return QPickup[state][action];
	}

	/***** This method helps to retrieve max Qvalue for the next state   ***/
	double maxQNextState(int nextState, int hold) {
		int[] actionsFromState = operationsFrom.get(nextState);
		double maxValue =0;
		if(actionsFromState[0] != 0 && hold == 0) {
			maxValue = QPickup[nextState][actionsFromState[0]];
		}else if(actionsFromState[0] !=0 && hold ==1){
			maxValue = QHold[nextState][actionsFromState[0]];
		}else if(actionsFromState[1] != 0 && hold == 0) {
			maxValue = QPickup[nextState][actionsFromState[0]];
		}else if(actionsFromState[1] != 0 && hold == 1) {
			maxValue = QHold[nextState][actionsFromState[1]];
		}
		double value  = 0;
		for(int i=0;i<actionsFromState.length;i++) {
			int action = actionsFromState[i];
			if(action == 0) {
				continue;
			}
			if(hold == 0)
				value = QPickup[nextState][action];
			else
				value = QHold[nextState][action];
			if(value > maxValue) {
				maxValue = value;
			}
		}
		return maxValue;
	}

	/**** This method sets the Q value in the Q table  ***/
	void setQ(int state,int action,double value,int hold) {
		if(hold == 0) {
			QPickup[state][action]=value;
		}else {
			QHold[state][action]=value;
		}

	}

	int pExploit(int state,int[] actionsFromState,int hold) {
		Random random = new Random();
		List<Integer> randomAction = new ArrayList<Integer>();
		List<Double> qValuesList = new ArrayList<Double>();
		int probablity = random.nextInt(100);
		int action=0,nextState=0,index=0;
		double q=0;
		for(int i=0;i<actionsFromState.length;i++) {
			action = actionsFromState[i];
			if(action == 0) {
				continue;
			}
			if(hold == 0)
				q = QPickup(state,action);
			else
				q = QHold(state, action);
			qValuesList.add(q);
		}
		double maxValue = Collections.max(qValuesList);

		for(int i=0;i<actionsFromState.length;i++) {
			action = actionsFromState[i];
			if(action == 0) {
				continue;
			}
			if(hold == 0)
				q = QPickup(state,action);
			else
				q = QHold(state, action);
			if(q == maxValue) {
				randomAction.add(action);
			}

		}
		if(randomAction.size()>1) {
			index = random.nextInt(randomAction.size()-1);
			action = randomAction.get(index);
		}else {
			action = randomAction.get(0);
		}


		if(probablity<16) {
			List<Integer> lowRandomAction = new ArrayList<Integer>();
			for(int a : actionsFromState) {
				if(!randomAction.contains(a) && a !=0) {
					lowRandomAction.add(a);
				}
			}
			//System.out.println("SIZ+++++"+lowRandomAction.size());
			if(lowRandomAction.size()>1) {
				index = random.nextInt(lowRandomAction.size()-1);
				nextState = lowRandomAction.get(index);
			}else if(lowRandomAction.size() == 1) {
				nextState = lowRandomAction.get(0);
			}else {
				nextState = action;
			}


		}else {
			nextState = action;
		}

		return nextState;
	}



}
