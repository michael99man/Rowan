/*
 * Person 1: 29710R
 * Person 2: 0M273
 * Room 302: B5,B6
 */

//import Java standard libraries used in this program
import java.io.*;
import java.util.*;

//This class represents a simulated robot within a given data set
public class RobotNavigator {
    // position of the robot
    private int x, y;
    // 2D array representation of grid
    private int[][] grid;
    // robot strength
    private int strength;
    // robot agility
    private int agility;

    // constructor takes in initial position, strength, agility as well as grid
    // pointer
    public RobotNavigator(int x, int y, int str, int agil, int[][] grid) {
        // initialize all of the instance variables
        this.x = x;
        this.y = y;
        strength = str;
        agility = agil;
        this.grid = grid; // non-defensive copy
    }

    // method determines if a x,y position is within the grid's bounds
    public boolean withinGrid(int newX, int newY) {
        // check if x is within [0, width] and y within [0, height]
        if (newX < 0 || newX >= grid[0].length || newY < 0 || newY >= grid.length) {
            return false;
        }
        return true;
    }

    // returns a String representation of the 2D grid array
    public String printGrid() {
        String output = "\tTable configuration:\n";
        for (int[] rows : grid) {
            output += "\t\t"; // add two tab characters
            // print out each value
            for (int num : rows) {
                output += String.format(" %2d", num);
            }
            // add a new line
            output += "\n";
        }
        return output;
    }

    // if we start at a hole (- number) that has abs value > agility
    private boolean inHole() {
        // value at position x,y -- method is only called if x,y within bounds
        int val = grid[y][x];
        if (val < 0 && Math.abs(val) > agility) {
            return true;
        }
        return false;
    }

    // takes in a char[] and processes each one of the directions
    public String move(char[] directions) {
        // check the starting position of the robot
        if (!withinGrid(x, y)) // return error if starting position is off grid
            return "\trobot is off table\n";
        
        if (inHole()) // return error if we start in a hole that's too deep
            return "\trobot is in a hole\n";

        // if robot is placed on a hill, flatten it
        if (grid[y][x] > 0)
            grid[y][x] = 0;

        // loop over each direction and process it
        for (int i = 0; i < directions.length; i++) {
            // this is the future position of the robot
            int newX = x;
            int newY = y;
            // read the direction from the array
            char move = directions[i];
            
            // analyze char and determine how it would change newX, newY
            switch (move) {
                case 'N':
                    newY--;
                    break;
                case 'E':
                    newX++;
                    break;
                case 'S':
                    newY++;
                    break;
                case 'W':
                    newX--;
                    break;
            }
            // return error if the new position is off the grid and append grid
            if (!withinGrid(newX, newY)) {
                return String.format("\tInstruction %d unsafe: %c at %d %d\n",
                        (i + 1), move, x, y) + printGrid();
            }
            // value on the square where the robot is trying to move to
            int val = grid[newY][newX];

            // process the value to see if robot can move there
            if (val > 0) {
                // if it is a hill
                if (val <= strength) {
                    // we can move there, so set x, y to newX, newY
                    x = newX;
                    y = newY;
                    if (val == strength) // flatten the hill
                        grid[newY][newX] = 0;
                } else if (val > strength) {
                    grid[newY][newX] -= strength;
                }
            } else if (val < 0) {
                // if it is a hole
                val = Math.abs(val);
                if (val <= agility) {
                    // we can move there
                    x = newX;
                    y = newY;
                }
                // else: we can't move there, so don't change x, y
            } else if (val == 0) {
                // we can move there, so update x, y
                x = newX;
                y = newY;
            }
        }
        /* return the final message if all the instructions were processed
           append the grid to the end */
        return String.format("\t%d instructions processed; robot at %d %d\n",
                directions.length, x, y) + printGrid();
    }

    /* reads in the data sets from input file specified by user and prints all
     output to file specified by user */
    public static void main(String[] args) throws IOException {
        // create a reader to read from the input file specified by user
        FileReader fr = new FileReader(args[0]);
        BufferedReader in = new BufferedReader(fr);

        // create a writer to write to the output file specified by user
        FileWriter fw = new FileWriter(args[1]);
        PrintWriter out = new PrintWriter(new BufferedWriter(fw));

        // read in the number of datasets
        int numDataSets = Integer.parseInt(in.readLine());
        out.printf("Analyzing %d data set(s)\n", numDataSets);

        // iterate over each data set
        for (int i = 0; i < numDataSets; i++) {
            // read in the dimensions of the grid
            // tokenizer breaks up the line using space as a delimiter
            StringTokenizer st = new StringTokenizer(in.readLine());
            // determines the maximum bounds for the grid
            int xSize = Integer.parseInt(st.nextToken());
            int ySize = Integer.parseInt(st.nextToken());

            // initializes a new 2D array to represent the grid
            int[][] grid = new int[ySize][xSize];
            // read in each integer value that comprises the grid
            // we read in each row and then each column within that row
            for (int y = 0; y < ySize; y++) {
                // read the next line
                st = new StringTokenizer(in.readLine());
                for (int x = 0; x < xSize; x++) {
                    // read in the value for each square
                    grid[y][x] = Integer.parseInt(st.nextToken());
                }
            }
            

            // read in the starting location of the robot
            st = new StringTokenizer(in.readLine());
            int startX = Integer.parseInt(st.nextToken());
            int startY = Integer.parseInt(st.nextToken());

            // read in the strength and agility of the robot
            st = new StringTokenizer(in.readLine());
            int strength = Integer.parseInt(st.nextToken());
            int agility = Integer.parseInt(st.nextToken());

            // read in the number of moves
            int numMoves = Integer.parseInt(in.readLine());

            // create a Direction array to represent these commands
            char[] directions = new char[numMoves];
            // read in the directions and add them to the array
            st = new StringTokenizer(in.readLine());
            for (int j = 0; j < numMoves; j++) {
                char move = st.nextToken().charAt(0);
                directions[j] = move;
            }

            // initialize a new Robot object with the data we read in
            RobotNavigator robot = new RobotNavigator(startX, startY, strength,
                    agility, grid);

            // run the moves on the robot
            String moveOutput = robot.move(directions);

            // format the output and print everything to the output stream
            out.printf("Data set %d\n", (i + 1));
            out.printf("\tRobot Start: %d %d\n", startX, startY);
            out.printf("\tRobot Strength and Agility: %d %d\n", strength, agility);
            out.printf("\tInstructions: %d\n", numMoves);
            out.printf(moveOutput);
        }
        // close the IO streams
        in.close();
        out.close();
    }
}
