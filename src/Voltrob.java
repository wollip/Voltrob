import java.util.ArrayList;
import java.util.Scanner;


public class Voltrob {
	
	private static int[][] clue = {{4,3},{5,2},{2,4},{7,2},{5,2},
								   {8,1},{2,4},{4,2},{6,2},{3,4}};
	private static ArrayList<int[][]> maps = new ArrayList<int[][]>();
	private static int[][] blankMap = {{4,4,4,4,4},{4,4,4,4,4},{4,4,4,4,4},{4,4,4,4,4},{4,4,4,4,4}};
	private static double[][][] possibility;
	private static double survival = 1;
	//private static long runTime=  0L; 
	/*
	 *  0 = bomb
	 *  1 = 1
	 *  2 = 2
	 *  3 = 3
	 *  4 = unknown
	 */
	
	
	public static void main(String[] args){
		
		boolean check = recurGuess(1, blankMap);
		System.out.println(maps.size());
		tabulation();
		printPoss();
		
		int row, col, val, mapSize, curMapSize;
		Scanner scan = new Scanner(System.in);
		while(true){
			//System.out.println("enter new information");
			System.out.println("enter row:");
			row = scan.nextInt();
			System.out.println("enter column:");
			col = scan.nextInt();
			System.out.println("enter value of square:");
			val = scan.nextInt();
			
			survival *= (1-possibility[row][col][0]);
			System.out.println("my RNG level: " + survival);
			
			mapSize = 0;
			while(true){
				curMapSize = update(row, col, val);
				if(curMapSize == mapSize){
					break;
				}else{
					mapSize = curMapSize;
				}
			}
			
			tabulation();
			printPoss();
			
			if(maps.size() == 1){
				break;
			}
		}
		
		System.out.println("Game Exited");
	}
	// true = no error, false = error;
	public static boolean checkPuzzle(int[][] map){
		int sum;
		int bombs;
		int unknown;
		// check for rows
		for(int i = 0; i < 5; i++){
			bombs = 0;
			sum = 0;
			unknown = 0;
			// adding up each row
			for(int i2 = 0; i2 < 5; i2++){
				
				//System.out.println(map[i][i2]);
				if(map[i][i2] == 0){
					bombs++;
				}else if(map[i][i2] == 4){
					unknown++;
				}else{
					sum  +=  map[i][i2];
				}
				//System.out.println("bombs: " + bombs + "unknowns: " + unknown + "sum: " + sum);
			}
			//System.out.println("bombs: " + bombs + "unknowns: " + unknown + "sum: " + sum);
			// checking if it make sense
			if( bombs > clue[i][1]){
				return false;
			}else{
				unknown -= (clue[i][1] - bombs);
				if((sum + unknown) > clue[i][0] || (sum + 3*unknown) < clue[i][0]){
					return false;
				}
			}
		}
		// check for columns
		for(int i = 0; i<5; i++){
			bombs = 0;
			sum = 0;
			unknown = 0;
			for(int i2 = 0; i2 < 5; i2++){
				
				if(map[i2][i] == 0){
					bombs++;
				}else if(map[i2][i] == 4){
					unknown++;
				}else{
					sum  +=  map[i2][i];
				}
			}
			//System.out.println("bombs: " + bombs + "unknowns: " + unknown + "sum: " + sum);
			// checking if it make sense
			if( bombs > clue[i+5][1]){
				return false;
			}else{
				unknown -= (clue[i+5][1] - bombs);
				if((sum + unknown) > clue[i+5][0] || (sum + 3*unknown) < clue[i+5][0]){
					return false;
				}
			}
		}
		
		return true;		
	}

	// depth starts at 1 and ends at 25 inclusive;
	public static boolean recurGuess(int depth, int[][] currentMap){
		//runTime++;
		if( depth > 25){
			System.out.println("error in depth");
			return false;
		}
		//System.out.println(depth);
		int row = (depth-1) /5;
		int column = (depth-1)%5;
		for(int i = 0; i < 4; i++){
			//System.out.println("guess: " + i + " at depth: " + depth);
			currentMap[row][column] = i;
			if(checkPuzzle(currentMap)){
				if(depth == 25){
					int [][] copy = new int[5][5];
					for(int o = 0; o < 5; o++){
					    copy[o] = currentMap[o].clone();
					} 
					/*
					for(int z = 0; z<5; z++){
						for(int z2 = 0; z2< 5; z2++){
							System.out.print(copy[z][z2] + "  ");
						}
						System.out.println();
					}
					System.out.println();
					*/
					maps.add(copy);
					
				}else{
					recurGuess(depth+1, currentMap);		
				}	
			}
			// otherwise we check other values
		}
		currentMap[row][column] = 4;
		if(depth == 1){
			return true;
		}
		// if it gets to here
		// that means all values are tried and none of them work
		// that means all values don't work
		// so we must go up 1 depth and find values that do work
		return false;
	}

	public static void tabulation(){
		System.out.println("tabulation mapSize: "+  maps.size());
		possibility = new double[5][5][4];
		for(int i = 0; i < maps.size(); i++){
			for(int row = 0; row < 5; row++){
				for(int col = 0; col< 5; col++){
					possibility[row][col][maps.get(i)[row][col]]++;
				}
			}
		}
		//maps.trimToSize();
		double size = (double) maps.size();
		//System.out.println("current map size in tabulation:" + maps.size());
		for(int row = 0; row < 5; row++){
			for(int col = 0; col<5; col++){
				for(int i = 0; i<4; i++){
					possibility[row][col][i] /= size;
				}
			}
		}
	
	}

	public static void printPoss(){
		for(int row = 0; row < 5; row++){
			for(int subr = 0; subr<2; subr++){
				for(int col = 0; col <5; col++){
					for(int subc = 0; subc<2; subc++){
						if(subr == 0){
							System.out.print(String.format("%.2f ", possibility[row][col][subc]));
						}else{
							System.out.print(String.format("%.2f ", possibility[row][col][subc+2]));
						}
						
					}
					System.out.print("| ");
				}
				System.out.println();
			}
			System.out.println("-----------------------------------------------------------");
		}
	}

	public static int update(int row, int col, int val){
		int o;
		for(int i = 0; i < maps.size(); i++){
			//System.out.println("1");
			if(maps.get(i)[row][col] != val){
				//System.out.println("removing things");
				o = maps.indexOf(maps.get(i));
				//System.out.println(o);
				maps.remove(o);
				//System.out.println("successful removal");
			}
		}
		return maps.size();
	}
}
