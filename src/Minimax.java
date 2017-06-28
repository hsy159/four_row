/**
 * Minimax.java
 *
 *
 *  Computer calculates the best move by looking ahead by a specified
 * number of moves.
 *
 * color 1 wants to take the highest of its recursive generated values,
 *	but return the lowest.
 * color -1 wants to take the lowest of its recursive generated values,
 *	but return the highest.
 *
 *
 */

import java.util.*;

//Create Minimax object from root node
public class Minimax
{
	private int[][] grid = new int[7][6];
	private int maxDepth;

	//Copy grid from given game grid, set max depth;
	// If maxDepth is set higher, game is harder
	public Minimax(int[][] game,int depth)
	{
    	grid = copyGrid(game);
    	maxDepth = depth;
	}

	//Function to copy grid.
	private int[][] copyGrid(int[][]copy)
	{
		int[][] newgrid = new int [7][6];
		for (int row =0; row<6; row++)
    	{
    		for (int col=0; col<7; col++)
    		{
    			newgrid[col][row]=copy[col][row];
    		}
    	}
    	return newgrid;
	}

	//Return col value that the computer wants.
	public int calcValue()
	{
		
		// if player's first turn was col3, return  col2.
		// Do this by looking at the board.
		int count = 0;
		for (int row =0; row<6; row++)
    	{
    		for (int col=0; col<7; col++)
    		{
    			if (grid[col][row]==1)
    			{
    				count++;
    			}
    		}
    	}
		if (count ==1)
		{
			if (grid[2][5]==1)
			{
				return 4;
			}
			else if (grid[4][5]==1)
			{
				return 2;
			}
			
			else
			{
				return 3;
			}
		}
		else if(count ==0){
			return 1; // 첫번 째 턴이 3,4,5칸 두면 안되기 때문에 그냥 2번째 칸으로 고정
		}
		


		// If not first turn, return negamax
		return negamax(grid,-100000000,0,1);
	}
	
	
	

	//Recursively calls itself and returns the best col that computer should choose.
	private int negamax(int[][] game, int alpha, int depth, int color)
	{
		
		
		int bestPath = 0;
		int bestValue = alpha;
		int player;
		
		int[][] newGrid = copyGrid(game);

		if (color == 1)
		{
			player = 2;
		}
		else
		{
			player = 1;
		}
		ConnectFourModel cfm = new ConnectFourModel();
		cfm.setGrid(newGrid,player);

		// Determine if game is over in current state;
		// return 1000000 if comp win; else return -1000000
		if (cfm.win())
		{
			if(cfm.getPlayer()== player)
			{bestValue = color*(100000000-depth);}
			else
			{bestValue = color*(-100000000+depth);}
		}
		//Determine if game is a draw

		else if(cfm.full()==true&&cfm.win()==false)
		{
			bestValue = 0;
		}
		//Determine pathValue using eval() if depth is reached.
		else if (depth==maxDepth) 
		{ 
			int mid = (eval(newGrid,player));
			if (mid!=0)
			{
				bestValue = (color*(mid-depth));
			}
			else
			{
				bestValue = mid;
			}
		}



		else
		{
			//Generate moves for each col and find the
			// best score from each of the generated moves.
			for(int c=0;c<7;c++) 
			{

				//Create a cfm for this column and attempt to drop.
				ConnectFourModel newcfm = new ConnectFourModel();
				newcfm.setGrid(newGrid,player);
				int r = newcfm.drop(c);

				//Recursive call the generated game grid and compare the current value to move value
				// If move is higher, make it the new current value.

				//If drop is successful
				if (r!=-1)
				{
					int[][] nGrid = new int[7][6];


					// Only if depth is < maxDepth recursive call
					if (depth < maxDepth)
					{
					nGrid = copyGrid(newGrid);
					nGrid[c][r] = player;
					int v = -negamax(nGrid,-100000000,depth+1,color*-1); 
					if (v >= bestValue)
					{
						bestPath = c;
						bestValue = v;
					}
					}
				}
			}
		}
		if (depth==0)
		{
			//System.out.println("Turn end");
			return bestPath;
		}
		else
		{
			//System.out.println(bestValue);
			return bestValue;
		}

	}

	//Evaluates the current board based on current player and returns a value based on how good
	// current position is.
	// Neutral is given a score of 0.
	// 2 in a row is 10. (00__) or (0_0_) or (0__0) or (_0_0) or (__00)
	// Open-ended 2-in-a-row is 20. (_00_)
	// 3 in a row is 1000. (00_0) or (0_00) or (000_) or (_000)
	// Open-ended 3-in-a-row is 2000 (_000_).
	// return sum of these values.

	//Modifier
	// Vertical = *1
	// Diagonal = *2
	// Horizontal = *3
	private int eval(int[][] board,int player)
	{
		int v = 1;
		int d = 2;
		int h = 3;

		int twoIn = 10;
		int threeIn = 1000;
		
		int reverseTwoIn = 10;
		int reverseThreeIn = 1000;

		int val = 0;		
		
		//Check for horizontal 2-in-a-row.
    	for (int row=0;row<6;row++)
    	{
    		for (int col = 0;col<4;col++)
    		{
    			//(xx00)
    			if (board[col][row] == player &&
    				board[col][row] == board[col+1][row] &&
    				board[col+2][row] == 0 &&
    				board[col+3][row] == 0)
    				{
    					val+= twoIn*h;
    				}
    			//(x0x0)
    			else if (board[col][row] == player &&
    				board[col+2][row] == player &&
    				board[col+1][row] == 0 &&
    				board[col+3][row] == 0)
    				{
    					val+= twoIn*h;
    				}
    			//(x00x)
    			else if (board[col][row] == player &&
    				board[col+3][row] == player &&
    				board[col+1][row] == 0 &&
    				board[col+2][row] == 0)
    				{
    					val+= twoIn*h;
    				}
    			//(0xx0)
    			else if (board[col][row] == 0 &&
    				board[col+1][row] == player &&
    				board[col+2][row] == player &&
    				board[col+3][row] == 0)
    				{
    					val+= 2*twoIn*h;
    				}
    			//(0x0x)
    			else if (board[col][row] == 0 &&
    				board[col+1][row] == player &&
    				board[col+2][row] == 0 &&
    				board[col+3][row] == player)
    				{
    					val+= twoIn*h;
    				}
    			//(00xx)
    			else if (board[col][row] == 0 &&
    				board[col][row] == board[col+1][row] &&
    				board[col+2][row] == player &&
    				board[col+3][row] == player)
    				{
    					val+= twoIn*h;
    				}
			       				   			    			
    		}
    	}

    	
    	// 상대방 수평 양쪽 뚫린 2 in a row check
    	for (int row=0;row<6;row++)
    	{
    		for (int col = 0;col<4;col++)
    		{
    			//(xx00)
    			if (board[col][row] == 0 &&
   					 	 board[col+1][row] == 3-player &&
   					 	 board[col+2][row] == 3-player &&
   					 	 board[col+3][row] == 0
   					){val-= 2*reverseTwoIn*h;}
    			else if (board[col][row] == 3-player &&
  					 	 board[col+1][row] == 3-player &&
  					 	 board[col+2][row] == 0 &&
  					 	 board[col+3][row] == 0
   					){val-= reverseTwoIn*h;}
    			else if (board[col][row] == 3-player &&
 					 	 board[col+1][row] == 0 &&
 					 	 board[col+2][row] == 3-player &&
 					 	 board[col+3][row] == 0
  					){val-= reverseTwoIn*h;}
    			else if (board[col][row] == 0 &&
 					 	 board[col+1][row] == 0 &&
 					 	 board[col+2][row] == 3-player &&
 					 	 board[col+3][row] == 3-player
   					){val-= reverseTwoIn*h;}
    			else if (board[col][row] == 0 &&
    					 board[col+1][row] == 3-player &&  
    					 board[col+2][row] == 0 &&
     					 board[col+3][row] == 3-player
     				){val-= reverseTwoIn*h;}
    			else if (board[col][row] == 3-player &&
   					 	 board[col+1][row] == 0 &&  
   					 	 board[col+2][row] == 0 &&
    					 board[col+3][row] == 3-player
    				){val-= reverseTwoIn*h;}
    			
    			  				       				   			    			
    		}
    	}
    	

    	
    	
    	//Check for vertical spaced 2-in-a-row.
    	// 0
    	// x
    	// x

    	for (int row=5;row>1;row--)
    	{
    		for (int col = 0;col<7;col++)
    		{
    			if (board[col][row] == player &&
    				board[col][row] == board[col][row-1] &&
    				board[col][row-2] == 0)
    				{
    					val+= twoIn*v;
    				}
 
    			
    		}
    	}    	
    	// 상대방 수직으로 2개 있을 경우
    	for (int row=5;row>1;row--)
    	{
    		for (int col = 0;col<7;col++)
    		{
    		
    			if(board[col][row] == 3-player &&
    					board[col][row-1] == 3-player &&
    					board[col][row-2] == 0
    					){val-= reverseTwoIn*v;}
    			
    		}
    	}
    	//Check for diagonal spaced 2-in-a-row (/).
    	//    0     x      x     x      0      0
    	//   0     0      x     0      x      x
    	//  x     0      0     x      0      x
    	// x     x      0     0      x      0
    	for (int row=5;row>2;row--)
    	{
    		for (int col = 0;col<4;col++)
    		{
    			if (board[col][row] == player &&
    				board[col][row] == board[col+1][row-1] &&
    				board[col+2][row-2] == 0 &&
    				board[col+3][row-3] == 0)
    				{
    					val+= twoIn*d;
    				}
    			else if (board[col][row] == player &&
    				board[col+1][row-1] == 0 &&
    				board[col+2][row-2] == 0 &&
    				board[col][row] == board[col+3][row-3])
    				{
    					val+= twoIn*d;
    				}
    			else if (board[col][row] == 0 &&
    				board[col+1][row-1] == 0 &&
    				board[col+2][row-2] == player &&
    				board[col+3][row-3] == player)
    				{
    					val+= twoIn*d;
    				}
    			else if (board[col][row] == 0 &&
    				board[col+1][row-1] == player &&
    				board[col][row] == board[col+2][row-2] &&
    				board[col+1][row-1] == board[col+3][row-3])
    				{
    					val+= twoIn*d;
    				}
    			else if (board[col][row] == player &&
    				board[col+1][row-1] == 0 &&
    				board[col][row] == board[col+2][row-2] &&
    				board[col+1][row-1] == board[col+3][row-3])
    				{
    					val+= twoIn*d;
    				}
    			else if (board[col][row] == 0 &&
    				board[col+1][row-1] == player &&
    				board[col+1][row-1] == board[col+2][row-2] &&
    				board[col][row] == board[col+3][row-3])
    				{
    					val+= 2*twoIn*d;
    				}
    					
    		}
    	}
    	// 상대방 /모양 대각선 2줄
    	for (int row=5;row>2;row--)
    	{
    		for (int col = 0;col<4;col++)
    		{
    			if (board[col][row] == 3-player &&
    					 board[col+1][row-1] == 3-player &&
    					 board[col+2][row-2] == 0 &&
    					 board[col+3][row-3] == 0){val-= reverseTwoIn*d;}
   			
    			else if (board[col][row] == 0 &&
    					 board[col+1][row-1] == 3-player &&
    					 board[col+2][row-2] == 3-player &&
    					 board[col+3][row-3] == 0){val-= 2*reverseTwoIn*d;}
    			
    			else if (board[col][row] == 3-player &&
    					 board[col+1][row-1] == 0 &&
    					 board[col+2][row-2] == 0 &&
    					 board[col+3][row-3] == 3-player){val-= 2*reverseTwoIn*d;}
    			
    			else if (board[col][row] == 3-player &&
   					 	 board[col+1][row-1] == 0 &&
   					 	 board[col+2][row-2] == 3-player &&
   					 	 board[col+3][row-3] == 0){val-= 2*reverseTwoIn*d;}
    			
    			else if (board[col][row] == 0 &&
   					 	 board[col+1][row-1] == 3-player &&
   					 	 board[col+2][row-2] == 0&&
   					 	 board[col+3][row-3] == 3-player){val-= reverseTwoIn*d;}
    			
    			else if (board[col][row] == 0 &&
   					 	 board[col+1][row-1] == 0 &&
   					 	 board[col+2][row-2] == 3-player &&
   					 	 board[col+3][row-3] == 3-player){val-= reverseTwoIn*d;}
   			
    					
    		}
    	}
    	//Check for diagonal spaced 3-in-a-row (\).
    	// 0     x     x     x
    	//  x     0     x     x
    	//   x     x     0     x
    	//    x     x     x     0
    	for (int row=0;row<3;row++)
    	{
    		for (int col = 0;col<4;col++)
    		{
    			if (board[col][row] == player &&
    				board[col][row] == board[col+1][row+1] &&
    				board[col+2][row+2] == 0 &&
    				board[col+3][row+3] == 0)
    				{
    					val+= twoIn*d;
    				}
    			else if (board[col][row] == player &&
    				board[col+1][row+1] == 0 &&
    				board[col+2][row+2] == 0 &&
    				board[col][row] == board[col+3][row+3])
    				{
    					val+= twoIn*d;
    				}
    			else if (board[col][row] == 0 &&
    				board[col+1][row+1] == 0 &&
    				board[col+2][row+2] == player &&
    				board[col+3][row+3] == player)
    				{
    					val+= twoIn*d;
    				}
    			else if (board[col][row] == 0 &&
    				board[col+1][row+1] == player &&
    				board[col][row] == board[col+2][row+2] &&
    				board[col+1][row+1] == board[col+3][row+3])
    				{
    					val+= twoIn*d;
    				}
    			else if (board[col][row] == player &&
    				board[col+1][row+1] == 0 &&
    				board[col][row] == board[col+2][row+2] &&
    				board[col+1][row+1] == board[col+3][row+3])
    				{
    					val+= twoIn*d;
    				}
    			else if (board[col][row] == 0 &&
    				board[col+1][row+1] == player &&
    				board[col+1][row+1] == board[col+2][row+2] &&
    				board[col][row] == board[col+3][row+3])
    				{
    					val+= twoIn*5*d;
    				}
    		
    		}
    	}
    	// 상대방 \모양 대각선 2줄
    	for (int row=0;row<3;row++)
    	{
    		for (int col = 0;col<4;col++)
    		{
    			if (board[col][row] == 3-player &&
    					 board[col+1][row+1] == 3-player &&
    					 board[col+2][row+2] ==0 &&
    					 board[col+3][row+3] ==0){val-= reverseTwoIn*d;}

    			else if (board[col][row] == 0 &&
   					 	 board[col+1][row+1] == 3-player &&
   					 	 board[col+2][row+2] == 3-player &&
   					 	 board[col+3][row+3] == 0){val-= 2*reverseTwoIn*d;}
    			else if (board[col][row] == 0 &&
     					 board[col+1][row+1] == 0 &&
     					 board[col+2][row+2] == 3-player &&
     					 board[col+3][row+3] == 3-player){val-= reverseTwoIn*d;}
   			
    			else if (board[col][row] == 0 &&
     					 board[col+1][row+1] == 3-player &&
     					 board[col+2][row+2] == 0 &&
     					 board[col+3][row+3] == 3-player){val-= reverseTwoIn*d;}
    			
    			else if (board[col][row] == 3-player &&
    					 board[col+1][row+1] == 0 &&
    					 board[col+2][row+2] == 0 &&
    					 board[col+3][row+3] == 3-player){val-= reverseTwoIn*d;}
    			
    			else if (board[col][row] == 3-player &&
    					 board[col+1][row+1] == 0 &&
    					 board[col+2][row+2] == 3-player &&
    					 board[col+3][row+3] == 0){val-= reverseTwoIn*d;}
    		
    		}
    	}
		//Check for horizontal 3-in-a-row.
    	for (int row=0;row<6;row++)
    	{
    		for (int col = 0;col<4;col++)
    		{
    			//(xx0x)
    			if (board[col][row] == player &&
    				board[col][row] == board[col+1][row] &&
    				board[col+2][row] == 0 &&
    				board[col][row] == board[col+3][row])
    				{
    					val+= threeIn*h;
    				}
    			//(x0xx)
    			else if (board[col][row] == player &&
    				board[col+1][row] == 0 &&
    				board[col][row] == board[col+2][row] &&
    				board[col][row] == board[col+3][row])
    				{
    					val+= threeIn*h;
    				}
    			//(0xxx)
    			else if (board[col][row] == 0 &&
    				board[col+1][row] == player &&
    				board[col+1][row] == board[col+2][row] &&
    				board[col+1][row] == board[col+3][row])
    				{
    					val+= threeIn*h;
    				}
    			//(xxx0)
    			else if (board[col][row] == player &&
    				board[col][row] == board[col+1][row] &&
    				board[col][row] == board[col+2][row] &&
    				board[col+3][row] == 0)
    				{
    					val+= threeIn*h;
    				}
    			
    		}
    	}
    	// 상대방 수평 3줄
    	for (int row=0;row<6;row++)
    	{
    		for (int col = 0;col<4;col++)
    		{
    			if(board[col][row] == 0 &&
    					board[col+1][row] == 3-player &&
    					board[col+2][row] == 3-player &&
    					board[col+3][row] == 3-player
    					){val-= reverseThreeIn*h;}
    			else if(board[col][row] == 3-player &&
    					board[col+1][row] == 0 &&
    					board[col+2][row] == 3-player &&
    					board[col+3][row] == 3-player
    					){val-= reverseThreeIn*h;}
    			else if(board[col][row] == 3-player &&
    					board[col+1][row] == 3-player &&
    					board[col+2][row] == 0&&
    					board[col+3][row] == 3-player
    					){val-= reverseThreeIn*h;}
    			else if(board[col][row] == 3-player &&
    					board[col+1][row] == 3-player &&
    					board[col+2][row] == 3-player &&
    					board[col+3][row] == 0
    					){val-= reverseThreeIn*h;}
    			
    		}
    	}
    	// 내가 3줄이고 바로 공격할 수 있는 경우
    	for (int row=0;row<5;row++)
    	{
    		for (int col = 0;col<4;col++)
    		{
    			if(board[col][row] == 0 &&
    					board[col+1][row] == player &&
    					board[col+2][row] == player &&
    					board[col+3][row] == player &&
    					board[col+3][row+1] != 0
    					){val+= 5*threeIn*h;}
    			else if(board[col][row] == player &&
    					board[col+1][row] == 0 &&
    					board[col+2][row] == player &&
    					board[col+3][row] == player &&
    					board[col+1][row+1] !=0
    					){val+= 5*threeIn*h;}
    			else if(board[col][row] == player &&
    					board[col+1][row] == player &&
    					board[col+2][row] == 0&&
    					board[col+3][row] == player &&
    					board[col+2][row+1]!=0
    					){val+= 5*threeIn*h;}
    			else if(board[col][row] == player &&
    					board[col+1][row] == player &&
    					board[col+2][row] == player &&
    					board[col+3][row] == 0 &&
    					board[col+3][row+1]!=0
    					){val+= 5*threeIn*h;}
    			
    		}
    	}
    	// 맨 밑줄에서 내가 3줄이고 공격할 수 있는 경우
    	for (int col = 0;col<4;col++)
		{
			if(board[col][5] == 0 &&
					board[col+1][5] == player &&
					board[col+2][5] == player &&
					board[col+3][5] == player 					
					){val+= 5*threeIn*h;}
			else if(board[col][5] == player &&
					board[col+1][5] == 0 &&
					board[col+2][5] == player &&
					board[col+3][5] == player 
					){val+= 5*threeIn*h;}
			else if(board[col][5] == player &&
					board[col+1][5] == player &&
					board[col+2][5] == 0&&
					board[col+3][5] == player 
					){val+= 5*threeIn*h;}
			else if(board[col][5] == player &&
					board[col+1][5] == player &&
					board[col+2][5] == player &&
					board[col+3][5] == 0 
					){val+= 5*threeIn*h;}
			
		}
    	


    	//Check for vertical spaced 3-in-a-row.
    	// 0
    	// x
    	// x
    	// x
    	for (int row=5;row>2;row--)
    	{
    		for (int col = 0;col<7;col++)
    		{
    			if (board[col][row] == player &&
    				board[col][row] == board[col][row-1] &&
    				board[col][row] == board[col][row-2] &&
    				board[col][row-3] == 0)
    				{
    					val+= threeIn*v;
    				}
 
    		}
    	}
    	// 상대방 3줄 수직 쌓음
    	for (int row=5;row>2;row--)
    	{
    		for (int col = 0;col<7;col++)
    		{
    			if(board[col][row] == 3-player &&
    					board[col][row-1] == 3-player &&
    					board[col][row-2] == 3-player &&
    					board[col][row-3] == 0
    					){val-= 5*reverseThreeIn*v;}
    		}
    	}
    	//Check for diagonal spaced 3-in-a-row (/).
    	//    0     x      x     x
    	//   x     0      x     x
    	//  x     x      0     x
    	// x     x      x     0
    	for (int row=5;row>2;row--)
    	{
    		for (int col = 0;col<4;col++)
    		{
    			if (board[col][row] == player &&
    				board[col][row] == board[col+1][row-1] &&
    				board[col][row] == board[col+2][row-2] &&
    				board[col+3][row-3] == 0)
    				{
    					val+= threeIn*d;
    				}
    			else if (board[col][row] == player &&
    				board[col][row] == board[col+1][row-1] &&
    				board[col+2][row-2] == 0 &&
    				board[col][row] == board[col+3][row-3])
    				{
    					val+= threeIn*d;
    				}
    			else if (board[col][row] == player &&
    				board[col+1][row-1] == 0 &&
    				board[col][row] == board[col+2][row-2] &&
    				board[col][row] == board[col+3][row-3])
    				{
    					val+= threeIn*d;
    				}
    			else if (board[col][row] == 0 &&
    				board[col+1][row-1] == player &&
    				board[col+1][row-1] == board[col+2][row-2] &&
    				board[col+1][row-1] == board[col+3][row-3])
    				{
    					val+= threeIn*d;
    				}
    		}
    	}
    	// 상대방 /대각선 3줄
    	for (int row=5;row>2;row--)
    	{
    		for (int col = 0;col<4;col++)
    		{
    			if (board[col][row] == 3-player &&
    				board[col][row] == board[col+1][row-1] &&
    				board[col][row] == board[col+2][row-2] &&
    				board[col+3][row-3] == 0){val-= reverseThreeIn*d;}
    			else if (board[col][row] == 3-player &&
    				board[col][row] == board[col+1][row-1] &&
    				board[col+2][row-2] == 0 &&
    				board[col][row] == board[col+3][row-3]){val-= reverseThreeIn*d;}
    			else if (board[col][row] == 3-player &&
    				board[col+1][row-1] == 0 &&
    				board[col][row] == board[col+2][row-2] &&
    				board[col][row] == board[col+3][row-3]){val-= reverseThreeIn*d;}
    			else if (board[col][row] == 0 &&
    				board[col+1][row-1] == 3-player &&
    				board[col+1][row-1] == board[col+2][row-2] &&
    				board[col+1][row-1] == board[col+3][row-3]){val-= reverseThreeIn*d;}
    		}
    	}
    	
    	// 내가 (/) 대각선으로 3줄 연결되어 있고, 바로 공격할 수 있는 경우
    	for (int row=5;row>2;row--)
    	{
    		for (int col = 0;col<4;col++){
    			if (board[col][row] == player &&
        			board[col+1][row-1] == player &&
        			board[col+2][row-2] == player &&
        			board[col+3][row-3] == 0 &&
        			board[col+3][row-2] !=0){val+= 5*threeIn*d;}
    			else if (board[col][row] == player &&
            			 board[col+1][row-1] == player &&
            			 board[col+2][row-2] == 0 &&
            			 board[col+3][row-3] == player &&
            			 board[col+2][row-1] !=0){val+= 5*threeIn*d;}
    			else if (board[col][row] == player &&
           			 board[col+1][row-1] == 0 &&
           			 board[col+2][row-2] == player &&
           			 board[col+3][row-3] == player &&
           			 board[col+1][row] !=0){val+= 5*threeIn*d;}
    			
    		}
    	}
    	//Check for diagonal spaced 3-in-a-row (\).
    	// 0     x     x     x
    	//  x     0     x     x
    	//   x     x     0     x
    	//    x     x     x     0
    	for (int row=0;row<3;row++)
    	{
    		for (int col = 0;col<4;col++)
    		{
    			if (board[col][row] == 0 &&
    				board[col+1][row+1] == player &&
    				board[col+1][row+1] == board[col+2][row+2] &&
    				board[col+1][row+1] == board[col+3][row+3])
    				{
    					val+= threeIn*d;
    				}
    			else if (board[col][row] == player &&
    				board[col+1][row+1] == 0 &&
    				board[col][row] == board[col+2][row+2] &&
    				board[col][row] == board[col+3][row+3])
    				{
    					val+= threeIn*d;
    				}
    			else if (board[col][row] == player &&
    				board[col][row] == board[col+1][row+1] &&
    				board[col+2][row+2] == 0 &&
    				board[col][row] == board[col+3][row+3])
    				{
    					val+= threeIn*d;
    				}
    			else if (board[col][row] == player &&
    				board[col][row] == board[col+1][row+1] &&
    				board[col][row] == board[col+2][row+2] &&
    				board[col+3][row+3] == 0)
    				{
    					val+= threeIn*d;
    				}

    		}
    	}
    	// 내가 (\) 대각선으로 3줄 연결되어 있고, 바로 공격할 수 있는 경우 
    	for (int row=0;row<3;row++)
    	{
    		for (int col = 0;col<4;col++){
    			if (board[col][row] == player &&
        			board[col+1][row+1] == player &&
        			board[col+2][row+2] == player &&
        			board[col+3][row+3] == 0 &&
        			board[col+3][row+2] !=0){val+= 5*threeIn*d;}
    			else if (board[col][row] == player &&
            			 board[col+1][row+1] == player &&
            			 board[col+2][row+2] == 0 &&
            			 board[col+3][row+3] == player &&
            			 board[col+2][row+1] !=0){val+= 5*threeIn*d;}
    			else if (board[col][row] == player &&
           			 board[col+1][row+1] == 0 &&
           			 board[col+2][row+2] == player &&
           			 board[col+3][row+3] == player &&
           			 board[col+1][row] !=0){val+= 5*threeIn*d;}
    			
    		}
    	}
    	for (int row=0;row<3;row++)
    	{
    		for (int col = 0;col<4;col++)
    		{
    			if (board[col][row] == 0 &&
    				board[col+1][row+1] == 3-player &&
    				board[col+1][row+1] == board[col+2][row+2] &&
    				board[col+1][row+1] == board[col+3][row+3]){val-= reverseThreeIn*d;}
    			else if (board[col][row] == 3-player &&
    				board[col+1][row+1] == 0 &&
    				board[col][row] == board[col+2][row+2] &&
    				board[col][row] == board[col+3][row+3]){val-= reverseThreeIn*d;}
    			else if (board[col][row] == 3-player &&
    				board[col][row] == board[col+1][row+1] &&
    				board[col+2][row+2] == 0 &&
    				board[col][row] == board[col+3][row+3]){val-= reverseThreeIn*d;}
    			else if (board[col][row] == 3-player &&
    				board[col][row] == board[col+1][row+1] &&
    				board[col][row] == board[col+2][row+2] &&
    				board[col+3][row+3] == 0){val-= reverseThreeIn*d;}

    		}
    	}

    	//Check for open-ended 3-in-a-row. (0xxx0)
    	for (int row=0;row<6;row++)
    	{
    		for (int col = 0;col<3;col++)
    		{
    			//horizontal
    			if (board[col][row] == 0 &&
    				board[col+1][row] == player &&
    				board[col+2][row] == player &&
    				board[col+3][row] == player &&
    				board[col][row] == board[col+4][row])
    				{
    					val+= 2*threeIn*h;
    				}
    			
    		}
    	}
    	// 상대방 수평 뚫린 3줄
    	for (int row=0;row<6;row++)
    	{
    		for (int col = 0;col<3;col++)
    		{
    			//horizontal
    			if (board[col][row] == 0 &&
    				board[col+1][row] == 3 - player &&
        			board[col+2][row] == 3 - player &&
        			board[col+3][row] == 3 - player &&
        			board[col][row] == board[col+4][row]){val-= 5*reverseThreeIn*h;}
    		}
    	}
    	for (int row=0;row<2;row++)
    	{
    		for (int col = 0;col<3;col++)
    		{
    			//diag(\)
    			if (board[col][row] == 0 &&
    				board[col+1][row+1] == player &&
    				board[col][row] == board[col+2][row+2] &&
    				board[col][row] == board[col+3][row+3] &&
    				board[col+4][row+4] == 0)
    				{
    					val+= 2*threeIn*d;
    				}
    		}
    	}
    	// 상대방 \ 대각선 양쪽 뚫린 3줄
    	for (int row=0;row<2;row++)
    	{
    		for (int col = 0;col<3;col++)
    		{
    			 if(board[col][row] == 0 &&
    				board[col+1][row+1] == 3-player &&
        			board[col][row] == board[col+2][row+2] &&
        			board[col][row] == board[col+3][row+3] &&
        			board[col+4][row+4] == 0){val-= 2*reverseThreeIn*d;}
    		}
    	}
    	//diag(/)
		for (int row=5;row>3;row--)
    		{
    			for (int col = 0;col<3;col++)
    			{
    				if (board[col][row] == 0 &&
    					board[col+1][row-1] == player &&
    					board[col+2][row-2] == player &&
    					board[col+3][row-3] == player &&
    					board[col+4][row-4] == 0)
    					{
    						val+= 5*threeIn*d;
    					}
    			}
    		}
		
		//상대방 (/) 뚫린 대각선 3줄
		for (int row=5;row>3;row--)
		{
			for (int col = 0;col<3;col++)
			{
				 if(board[col][row] == 0 &&
					board[col+1][row-1] == 3-player &&
					board[col+2][row-2] == 3-player &&
					board[col+3][row-3] == 3-player &&
					board[col+4][row-4] == 0){val-= 5*reverseThreeIn*d;}
			}
		}
	
		System.out.println(val);
    	return val;
	}

}