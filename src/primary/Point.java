package primary;

public class Point {
	public int x;
	public int y;
	
	/**
	 * 
	 * @param newX
	 * @param newY
	 */
	public Point(int newX, int newY) {
		x = newX;
		y = newY;
	}
	
	public Point(Point copyPoint) {
		x = copyPoint.x;
		y = copyPoint.y;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == null) 
			return false;
		
		if (!(o instanceof Point))
			return false;
		
		Point temp = (Point) o;
		
		if (this.x == temp.x && this.y == temp.y)
			return true;
		
		return false;
	}
}
