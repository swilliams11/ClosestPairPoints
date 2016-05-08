import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.TreeMap;

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils.Collections;

public class ClosestPair {
	private Point [] points;
	private int numOfPoints;
	private Point [] pointsSortedByX;
	private Point [] pointsSortedByY;
	private ClosestPair  cp;
	private String fileName;
	private int iteration = 0;
	
	public static void main(String[] args) {
		String filenameS = "10points.txt"; 
		System.out.println("\n" + filenameS);
		ClosestPair cp = new ClosestPair(filenameS, 10);
		cp.readPointFile();
		cp.findClosestPairStart();
		
		filenameS = "100points.txt";
		System.out.println("\n" + filenameS);
		ClosestPair cp2 = new ClosestPair(filenameS, 100);
		cp2.readPointFile();
		cp2.findClosestPairStart();
		
		filenameS = "1000points.txt";
		System.out.println("\n" + filenameS);
		ClosestPair cp3 = new ClosestPair(filenameS, 1000);
		cp3.readPointFile();
		cp3.findClosestPairStart();
		
	}
	
	public ClosestPair(String fileName, int numOfPoints){
		this.numOfPoints = numOfPoints;
		this.fileName = fileName;
	}
	
	/*
	 * Read the file and create the points array.
	 */
	public void readPointFile(){
		points = new Point[numOfPoints];
		int i = 0;
		URL path = ClosestPair.class.getResource(fileName);
		try (BufferedReader br = new BufferedReader(new FileReader(new File(path.getFile()))))
		{
			String sCurrentLine;
			//read points store in array
			while ((sCurrentLine = br.readLine()) != null) {
				points[i] = readPoint(sCurrentLine);
				i++;
			}
			sortArrays();
			
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
	
	/*
	 * Read the point string and convert to Point.
	 */
	public Point readPoint(String line){
		String [] pointArray = line.split(" ");
		return new Point(pointArray[0], pointArray[1]);
	}
	
	/*
	 * Print the array
	 */
	public void printArray(Point [] pointsLocal){
		for(int i = 0; i < pointsLocal.length; i++){
			System.out.println(pointsLocal[i]);
		}
	}
	
	/*
	 * Create two copies of the points array and sort both the arrays by X and Y
	 */
	public void sortArrays(){
		//printArray(points);
		//sort the array by x values
		pointsSortedByX = Arrays.copyOf(points, numOfPoints);
		Arrays.sort(pointsSortedByX, Comparator.comparing(Point::getX));
		//System.out.println("pointsSortedByX");
		//printArray(pointsSortedByX);
		
		pointsSortedByY = Arrays.copyOf(points, numOfPoints);
		Arrays.sort(pointsSortedByY, Comparator.comparing(Point::getY));
		//System.out.println("pointsSortedByY");
		//printArray(pointsSortedByY);
	}
	
	/*
	 * Start the recursive method
	 * @returns closest pair of points
	 */
	public void findClosestPairStart() {
		Point [] p = findClosestPair(points, pointsSortedByX, pointsSortedByY);
		System.out.println("The minimum distance is: " );
		System.out.println(distance(p[0], p[1]) + ": " + p[0] + "<----->" + p[1]);
	}
	
	/*
	 * find the closest pair of points.
	 * Recursive method:
	 * When the number of points is <= 3 then brute force search the points,
	 * which only requires 3 comparisons.  
	 * In all other cases divide points in half.
	 * 
	 * runs in O(n lg n) time
	 */
	public Point [] findClosestPair(Point [] p, Point [] x, Point [] y){
		
		//System.out.println("**************iteration " + iteration + "******************");
		iteration++;
		if(p.length <= 3){
			//brute force algorithm here
			Point [] closestPoints = determineClosestPoints(p);
			return closestPoints;
			//int minDistance = calcDistance(closestPoints);
		} else {
			//DIVIDE
			int mid = x.length / 2;
			int xl = findVerticalLine(x, mid);
			//int yl = findHorizontalLine(mid);
			//System.out.println(xl);
			//System.out.println(yl);
			Point [] p_l = splitPL(p, 0, mid, xl);
			//System.out.println("\np_l");
			//printArray(p_l);
			
			Point [] p_r = splitPR(p, mid + 1, x.length, xl);
			//System.out.println("\np_r");
			//printArray(p_r);
			//System.out.println("p_l.length=" + p_l.length);
			//System.out.println("p_r.length=" + p_r.length);
			
			Point [] x_l = splitPL(x, 0, mid, xl);
			Point [] x_r = splitPR(x, mid + 1, x.length, xl);
			//System.out.println("\nx_l");
			//printArray(x_l);
			
			//System.out.println("\nx_r");
			//printArray(x_r);
			
			//divide the array into y left and y right
			Point [] y_l = createSortedSubsetArrayY(p_l, y);
			Point [] y_r = createSortedSubsetArrayY(p_r, y);
			//System.out.println("\ny_l");
			//printArray(y_l);
			//System.out.println("\ny_r");
			//printArray(y_r);
			
			//CONQUER
			//recurse on left and right
			Point [] closestPointsL = findClosestPair(p_l, x_l, y_l);
			Point [] closestPointsR = findClosestPair(p_r, x_r, y_r);
			//System.out.println("1st min");
			//printArray(closestPointsL);
			//printArray(closestPointsR);
			
			//COMBINE
			//find the min of the closest points of the left and the right 
			Point [] closestPoints = min(closestPointsL, closestPointsR);
			double delta = distance(closestPoints[0], closestPoints[1]);
			//System.out.println("delta = " + delta);
			
			//check the points that are between 2Delta of l
			Point [] yPrime = createYPrime(y, xl, delta);
			Point [] yPrimeClosestPoints = findPointsWithinDelta(yPrime);
			//System.out.println("distance of yPrimeClosestPoints is " 
			//		+ distance(yPrimeClosestPoints[0], yPrimeClosestPoints[1]));
			//System.out.println("2nd min");
			//printArray(yPrimeClosestPoints);
			if(yPrimeClosestPoints != null && yPrimeClosestPoints.length > 1){
				closestPoints = min(closestPoints, yPrimeClosestPoints);
			}
			return closestPoints;
		}		
	}
	
	/*
	 * Find the vertical line (x-axis) to divide the points into two subsets.
	 */
	public int findVerticalLine(Point [] x, int middle) {
		return x[middle].getX();
	}

	
	/*
	 * Create a new array with n/2 points.
	 * All of these points are ON or to the Left the vertical line.
	 * You could have the same point in Sr or Sl
	 */
	public Point [] splitPL(Point [] p, int start, int end, int l){
		ArrayList<Point> ptemp = new ArrayList<>();
		for (int i = 0; i < p.length; i++){
			if (p[i].getX() <= l) {
				ptemp.add(p[i]);				
			}
		}
		Point [] returnArray = new Point[ptemp.size()];
		return ptemp.toArray(returnArray);
	}
	
	/*
	 * Create a new array with n/2 points.
	 * All of these points are ON or to the right the vertical line.
	 * You could have the same point in Sr or Sl
	 */
	public Point [] splitPR(Point [] p, int start, int end, int l){
		ArrayList<Point> ptemp = new ArrayList<>(end);
		for (int i = 0; i < p.length; i++){
			if (p[i].getX() >= l) {
				ptemp.add(p[i]);				
			}
		}
		Point [] returnArray = new Point[ptemp.size()];
		return ptemp.toArray(returnArray);
	}
	
	/*
	 * Create Y' which includes all the points in p, but they are sorted
	 * by the y value.
	 * @returns new array 
	 */
	public Point [] createSortedSubsetArrayY(Point [] p, Point [] sortedArray) {
		ArrayList<Point> pArrayList = new ArrayList<>(Arrays.asList(p));
		ArrayList<Point> ptemp = new ArrayList<>(p.length);
		
		for (int i = 0; i < sortedArray.length; i++ ){
			Point tempPoint = sortedArray[i];
			int index = pArrayList.indexOf(tempPoint);
			if(index >= 0){
				ptemp.add(tempPoint);
			}
		}
		Point [] returnArray = new Point [ptemp.size()];
		return ptemp.toArray(returnArray);
	}
	
	/*
	 * return the distance between two points;
	 * sqrt((x1 - x2)^2 + (y1 - y2)^2)
	 */
	public double distance(Point p1, Point p2){
		double xdiff = p1.getX() - p2.getX();
		double ydiff = p1.getY() - p2.getY();
		double x2 = Math.pow(xdiff, 2);
		double y2 = Math.pow(ydiff, 2);
		return Math.sqrt(x2 + y2);
	}
	
	/*
	 * If the length is 2 then it returns the two points.
	 * If the length is 3 then it compares distance between all three points
	 * and returns the closest pair of points.  
	 * Items added to TreeMap remain in sorted order.
	 * 
	 * worst case is 3 comparison.
	 * @returns closest points as an array.
	 */
	public Point [] determineClosestPoints(Point [] p) {
		if(p.length == 2){
			return p;
			//at most 3
		//} else if(p.length == 3){
		} else {
			int i = 0;
			//TreeMap is sorted
			TreeMap<Double, Point[]> map = new TreeMap<>();
			double distance = distance(p[i], p[i+1]);
			map.put(distance, new Point [] {p[i], p[i+1]});
			distance = distance(p[i], p[i+2]);
			map.put(distance, new Point [] {p[i], p[i+2]});
			distance = distance(p[i+1], p[i+2]);
			map.put(distance, new Point [] {p[i+1], p[i+2]});
			return map.get(map.firstKey());
		}
	}
	
	/*
	 * Return the pair of points with the minimum distance.
	 * @returns pair of points as an array
	 */
	public Point [] min( Point [] pair1, Point [] pair2){
		double d1 = distance(pair1[0], pair1[1]);
		double d2 = distance(pair2[0], pair2[1]);
		if (d1 <= d2) {
			return pair1;
		} else {
			return pair2;
		}
	}
	
	/*
	 * Create the array of all the points within 2Delta.
	 * This creates Y'
	 * So the x value must be > l - delta and x must be < l + delta
	 */
	public Point [] createYPrime(Point [] y, int l, double delta) {
		double lDelta = l - delta;
		double rDelta = l + delta;
		ArrayList<Point> arrayTemp = new ArrayList<>();
		for (int i = 0; i < y.length; i++){
			if(y[i].getX() > lDelta && y[i].getX() < rDelta) {
				arrayTemp.add(y[i]);
			}
		}
		Point [] returnArray = new Point [arrayTemp.size()];
		return arrayTemp.toArray(returnArray);
	}
	
	/*
	 * find the closest points within distance delta.
	 * This compares each point in Y' to the next 7 points (at most).
	 * If there are less than 7 points then it only compares up the the length Y'
	 * 
	 * returns closest pair of points.
	 */
	public Point [] findPointsWithinDelta(Point [] yd){
		double minDistance = Double.POSITIVE_INFINITY;
		Point [] closestPoints = new Point[2];
		if(yd.length == 1) {
			closestPoints = null;
		} else {
			for(int i = 0; i < yd.length; i++){
				for(int j = i + 1; j <= j + 7 && j < yd.length; j++){
					double distance = distance(yd[i], yd[j]);
					if(distance <= minDistance){
						minDistance = distance;
						closestPoints[0] = yd[i];
						closestPoints[1] = yd[j];
					}
				}
			}
		}
		return closestPoints;
	}
}
