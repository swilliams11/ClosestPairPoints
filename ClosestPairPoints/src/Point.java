
public class Point {
	private int x;
	private int y;
	
	public Point (int x, int y){
		this.x = x;
		this.y = y;
	}
	
	public Point(String x, String y){
		this.x = Integer.parseInt(x);
		this.y = Integer.parseInt(y);
	}
	
	public int getX() { return x;}
	public int getY() { return y;}
	public String toString(){
		return x + " " + y;
	}
}
