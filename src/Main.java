/*
 * Here will be the brief code summary:
 *
 * First of all, I am reading the input file and keep the data in arrays and arrayLists.
 * Then, my algorithm works the way that the following:
 * Starting from initial station, searchs its neighbours and then apply recursion them until find the end statiton.
 * when passing each station, I am adding it to named visited and path ArrayLists
 * if we can not reach the end station in one of the recursions, then remove the that specific path
 * And my code has also some helper-functions
 * @author Burak Korkmaz
 */

// importing
import java.util.Scanner;
import java.util.Arrays;
import java.util.ArrayList;
import java.io.File;
import java.io.FileNotFoundException;
import java.awt.*;
public class Main{

    public static ArrayList<String> path = new ArrayList<String>(); // to take data of path, I use an arraylist because it is easy to add and remove elements.
    public static ArrayList<String> visited = new ArrayList<>(); // also I take data of visited stations because I don't want to rerun functions.

    public static void main(String[] args) throws FileNotFoundException{
        //Input preparation
        String fileName = "coordinates.txt";
        File file = new File(fileName);
        Scanner inputFile = new Scanner(file);

        // I am creating some arrays(some of which are multi-dimensional) because I want to separate the data.
        String[] metroLines = new String[10]; // metroLines will take the data of lines e.i. B1, M1A, ...
        String[][] metroStations = new String[10][]; //metroStations will take the data of stations e.i. Yenikapi, Halkali,...
        int[][][] stationCoordinates = new int[10][][]; // station's coordinates
        String[] lineColors = new String[10]; // their colors
        String[] breaks = new String[7]; // name of break points
        String[][] breakPoints = new String[7][]; // which breakpoint connects which lines?
        String[][] metroStations2 = new String[10][]; // this is the same as metroStations but this will have stations with "*"(asterix)

        // firstly, I am taking the input of metro lines
        int i = 0;

        // reading until breakpoints and adding elements to proper arrays
        while (i <10){
            String line = inputFile.nextLine();
            String[] parts = line.split(" ");
            metroLines[i] = parts[0];
            lineColors[i] = parts[1];

            String line2 = inputFile.nextLine();
            String[] parts2 = line2.split(" ");
            int j = parts2.length;
            metroStations[i] = new String[j/2];
            stationCoordinates[i] = new int[j/2][];

            for (int a = 0; a < j ; a+=2){
                metroStations[i][a/2] = parts2[a];
                String[] parts4 = parts2[a + 1].split(",");
                int x_coordinate = Integer.parseInt(parts4[0]);
                int y_coordinate = Integer.parseInt(parts4[1]);
                int[] coordinates = {x_coordinate, y_coordinate};
                stationCoordinates[i][a/2] = coordinates;
            }
            i++;
        }



        //now reading breakpoints
        for (int a= 0;a < 7 ; a++){
            String line3 = inputFile.nextLine();
            String[] parts3 = line3.split(" ");
            breaks[a] = parts3[0];
            breakPoints[a] = new String[parts3.length-1];
            for (int b = 0; b < parts3.length-1; b++){
                breakPoints[a][b] = parts3[b+1];
            }
        }

        // I am copying the metroStations because in the latter for loop you will see
        for (int a = 0; a < metroStations.length; a++){
            metroStations2[a] = new String[metroStations[a].length];
            for (int b = 0; b < metroStations[a].length; b++){
                metroStations2[a][b] = metroStations[a][b];
            }
        }

        // removing "*"
        for (int a = 0; a < metroStations.length; a++){
            for (int b = 0; b < metroStations[a].length; b++){
                if (metroStations[a][b].startsWith("*")){
                    String[] stringList = metroStations[a][b].split("");
                    String newStation = "";
                    for (int c = 1; c < metroStations[a][b].length(); c ++){
                        newStation += stringList[c];
                    }
                    metroStations[a][b] = newStation;
                }
            }
        }


        //user input part
        Scanner input = new Scanner(System.in);
        String current = input.next();
        String end = input.next();
        boolean isContainsCurrent = isContains(current, metroStations); // I am checking whether the station exists or not
        boolean isContainsEnd = isContains(end, metroStations);
        String[][] neighbours = neighboursOfBreakPoints(metroStations, metroLines, breaks, breakPoints); // finding the all neighbours of breakpoints

        findPath(current, end, metroStations, metroLines, breaks, neighbours); // this function gives us the path


        if (!(isContainsCurrent && isContainsEnd)){ // if at least one of them is not exist
            System.out.println("The station names provided are not present in this map.");
        }else {
            if (!path.isEmpty()){ // if path is not filled
                for (int a= 0; a < path.size(); a++){
                    System.out.println(path.get(a));
                }

                // from this line  I am handling StdDraw
                StdDraw.setCanvasSize(1024, 482);
                StdDraw.setXscale(0, 1024);
                StdDraw.setYscale(0, 482);
                StdDraw.setPenRadius(0.012);
                StdDraw.enableDoubleBuffering();


                StdDraw.picture(512, 241, "background.jpg"); // adding the image
                for (int a = 0; a < 10; a++) {
                    String[] colors = lineColors[a].split(",");
                    Color c = new Color(Integer.parseInt(colors[0]), Integer.parseInt(colors[1]), Integer.parseInt(colors[2]));
                    StdDraw.setPenColor(c); // specific line colors
                    for (int b = 0; b < stationCoordinates[a].length - 1; b++) {
                        StdDraw.line(stationCoordinates[a][b][0], stationCoordinates[a][b][1], stationCoordinates[a][b + 1][0], stationCoordinates[a][b + 1][1]); // drawing metro lines
                    }
                }

                for (int a = 0; a < metroStations.length; a++) {
                    for (int b = 0; b < metroStations[a].length; b++) {
                        StdDraw.setPenColor(StdDraw.WHITE);
                        StdDraw.point(stationCoordinates[a][b][0], stationCoordinates[a][b][1]);
                        if (metroStations2[a][b].startsWith("*")) {
                            StdDraw.setPenColor(StdDraw.BLACK);
                            StdDraw.setFont(new Font("Helvetica", Font.BOLD, 8));
                            StdDraw.text(stationCoordinates[a][b][0], stationCoordinates[a][b][1] + 5, metroStations[a][b]); // texting the station names
                        }
                    }
                }

                int pauseDuration = 300;


                // now animation part
                StdDraw.setPenColor(StdDraw.PRINCETON_ORANGE);
                for (int l = 0; l < path.size(); l++){
                    for (int m = 0; m <= l; m++){
                        String nameOfStation = path.get(m);
                        int indexOfLine = getLineIndex(nameOfStation, metroStations, metroLines);
                        int indexOfStation = getStationIndex(nameOfStation, metroStations, metroLines);
                        int xCoordinate = stationCoordinates[indexOfLine][indexOfStation][0];
                        int yCoordinate = stationCoordinates[indexOfLine][indexOfStation][1];
                        StdDraw.point(xCoordinate, yCoordinate);
                        if (l == m){
                            StdDraw.setPenRadius(0.02);
                            StdDraw.point(xCoordinate, yCoordinate);
                            StdDraw.setPenRadius(0.012);
                        }
                    }
                    StdDraw.show();
                    StdDraw.pause(pauseDuration);
                    StdDraw.clear();
                    StdDraw.picture(512, 241, "background.jpg");
                    for (int a = 0; a < 10; a++) {
                        String[] colors = lineColors[a].split(",");
                        Color c = new Color(Integer.parseInt(colors[0]), Integer.parseInt(colors[1]), Integer.parseInt(colors[2]));
                        StdDraw.setPenColor(c);
                        for (int b = 0; b < stationCoordinates[a].length - 1; b++) {
                            StdDraw.line(stationCoordinates[a][b][0], stationCoordinates[a][b][1], stationCoordinates[a][b + 1][0], stationCoordinates[a][b + 1][1]);
                        }
                    }

                    for (int a = 0; a < metroStations.length; a++) {
                        for (int b = 0; b < metroStations[a].length; b++) {
                            StdDraw.setPenColor(StdDraw.WHITE);
                            StdDraw.point(stationCoordinates[a][b][0], stationCoordinates[a][b][1]);
                            if (metroStations2[a][b].startsWith("*")) {
                                StdDraw.setPenColor(StdDraw.BLACK);
                                StdDraw.setFont(new Font("Helvetica", Font.BOLD, 8));
                                StdDraw.text(stationCoordinates[a][b][0], stationCoordinates[a][b][1] + 5, metroStations[a][b]);
                                //StdDraw.show();
                            }
                        }
                    }
                    StdDraw.setPenColor(StdDraw.PRINCETON_ORANGE);

                }
            }else{
                System.out.println("These two stations are not connected");
            }
        }
    }

    /*
     * This method gives us the right path
     * @param1 current first String
     * @param2 current second String
     * @array some helper arrays
     * It works by using recursion
     * Does not return
     */

    public static void findPath(String current, String end, String[][] metroStations, String[] metroLines, String[] breaks, String[][] neighbours){
        visited.add(current);
        path.add(current);

        if (current.equals(end)){
            return;
        }
        String[] allNeighbours = findNeighbour(current, metroStations, metroLines, breaks, neighbours);



        for (String neighbour: allNeighbours){
            if (neighbour != null) {
                if (!visited.contains(neighbour)) {
                    findPath(neighbour, end, metroStations, metroLines, breaks, neighbours);
                    if (path.contains(end)) {
                        return;
                    }
                }
            }
        }

        path.remove(current);
    }


    /*
     *This method returns all neighbours of a given station (uses neighboursOfBreakPoints function to find breakPoints neighbours)
     *because to find the neighbours of break points we need a different algorithm than normal stations.
     * @param1 current String
     * @array some helper arrays
     * @return neihgbours array
     */
    public static String[] findNeighbour(String current, String[][] metroStations, String[] metroLines, String[] breaks, String[][] neighbours){
        if (!isBreakPoint(current, breaks)) {
            int indexOfStation = getStationIndex(current, metroStations, metroLines);
            int indexOfLine = getLineIndex(current, metroStations, metroLines);
            if (indexOfStation == 0) {
                String[] neighboursIns = new String[1];
                neighboursIns[0] = metroStations[indexOfLine][indexOfStation + 1];
                return neighboursIns;

            }else if (indexOfStation == metroStations[indexOfLine].length-1){
                String[] neighboursIns = new String[1];
                neighboursIns[0] = metroStations[indexOfLine][indexOfStation-1];
                return neighboursIns;
            }else{
                String[] neighboursIns = new String[2];
                if (!visited.contains(metroStations[indexOfLine][indexOfStation-1]))
                    neighboursIns[0] = metroStations[indexOfLine][indexOfStation-1];
                if (!visited.contains(metroStations[indexOfLine][indexOfStation+1]))
                    neighboursIns[1] = metroStations[indexOfLine][indexOfStation+1];
                return neighboursIns;
            }
        }else{
            int indexOfBreakPoint = 0;
            for (int i = 0; i < breaks.length; i++){
                if (current.equals(breaks[i])){
                    indexOfBreakPoint = i;
                }
            }
            String[] neighboursIns = neighbours[indexOfBreakPoint];
            return neighboursIns;
        }
    }

    /*
     *This method returns index of given station's metro line(which metro line does the given station belong?)
     *@param1 current String
     *@array some helper arrays
     *@return index of metro line
     */

    public static int getLineIndex(String current, String[][] metroStations, String[] metroLines){
        for (int i = 0; i < metroLines.length ; i++){
            for (int j = 0; j < metroStations[i].length ; j++){
                if (current.equals(metroStations[i][j])){
                    //System.out.println("returned i= " + i);
                    return i;
                }
            }
        }
        return 0;
    }


    /*this method returns index of given station in the line.
     *if it is a break point, this function fails.
     *@param1 current String
     *@array some helper arrays
     *@return index of station in the line
     */
    public static int getStationIndex(String current, String[][] metroStations, String[] metroLines){
        int lineIndexOfStation = getLineIndex(current, metroStations, metroLines);
        for (int i = 0; i < metroStations[lineIndexOfStation].length; i++){
            if (current.equals(metroStations[lineIndexOfStation][i])){
                return i;
            }
        }
        return 0;
    }

    /*
     *This method checks whether a station is breakPoint or not.
     * @param1 current String
     * @param2 breaks Array (which contains breakPoints)
     * @return true or false
     */
    public static boolean isBreakPoint(String current, String[] breaks){

        for (int i = 0; i < breaks.length; i++){
            if (current.equals(breaks[i])){
                return true;
            }
        }
        return false;
    }

    /*
     *this method checks whether station exists or not.
     *@param1 station String
     *@param2 metroStations Array
     * @return true or false
     */
    public static boolean isContains(String station, String[][] metroStations){
        for (int a = 0; a < metroStations.length; a++){
            for (int b = 0; b < metroStations[a].length; b++){
                if (station.equals(metroStations[a][b])){
                    return true;
                }
            }
        }
        return false;
    }

    /*
     *This method finds the neighbours of breakPoints (different from findNeighbours method)
     * @no special parameter, it applies all breakpoints in the method
     * @return neihgbours of all breakpoints , 2D Array
     */
    public static String[][] neighboursOfBreakPoints(String[][] metroStations, String[] metroLines, String[] breaks, String[][] breakPoints){
        String[][] allNeihgbours = new String[7][];
        for (int i = 0; i < breaks.length; i++) {
            String nameOfBreakStation = breaks[i];
            String[] namesOfLines = breakPoints[i];
            int count = 0;
            for (String line : namesOfLines) {
                int indexOfThisLine = 0;
                for (int j = 0; j < metroLines.length ;j++){
                    if (metroLines[j].equals(line)){
                        indexOfThisLine = j;
                    }
                }
                int indexOfBreakStation = 0;
                for (int k = 0; k < metroStations[indexOfThisLine].length ; k++){
                    if (nameOfBreakStation.equals(metroStations[indexOfThisLine][k])){
                        indexOfBreakStation = k;
                    }
                }
                if (indexOfBreakStation == 0){
                    count++;
                }else if(indexOfBreakStation == metroStations[indexOfThisLine].length-1){
                    count++;
                }else{
                    count += 2;
                }
            }
            allNeihgbours[i] = new String[count];
        }
        for (int i = 0; i < breaks.length; i++) {
            String nameOfBreakStation = breaks[i];
            String[] namesOfLines = breakPoints[i];
            int l = 0;
            while (l < allNeihgbours[i].length){
                for (String line : namesOfLines) {
                    int indexOfThisLine = 0;
                    for (int j = 0; j < metroLines.length; j++) {
                        if (metroLines[j].equals(line)) {
                            indexOfThisLine = j;
                        }
                    }
                    int indexOfBreakStation = 0;
                    for (int k = 0; k < metroStations[indexOfThisLine].length; k++) {
                        if (nameOfBreakStation.equals(metroStations[indexOfThisLine][k])) {
                            indexOfBreakStation = k;
                        }
                    }
                    if (indexOfBreakStation== 0){
                        allNeihgbours[i][l] = metroStations[indexOfThisLine][indexOfBreakStation+1];
                        l++;
                    }else if(indexOfBreakStation == metroStations[indexOfThisLine].length-1){
                        allNeihgbours[i][l] = metroStations[indexOfThisLine][indexOfBreakStation-1];
                        l++;
                    }else{
                        allNeihgbours[i][l] = metroStations[indexOfThisLine][indexOfBreakStation+1];
                        l++;
                        allNeihgbours[i][l] = metroStations[indexOfThisLine][indexOfBreakStation-1];
                        l++;
                    }
                }
            }
        }
        return allNeihgbours;
    }
}