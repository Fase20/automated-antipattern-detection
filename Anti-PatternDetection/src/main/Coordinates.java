package main;

public class Coordinates {
	private double X;
	private double Y;
	private String name;
	
	public Coordinates(double dexp, double dent, String currentAntipattern) {
		this.X=dexp;
		this.Y=dent;
		this.name=currentAntipattern;
	}
	@Override
	public String toString() {
		return name+" ["+X+","+Y+"]";
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public double getX() {
		return X;
	}
	public void setX(double x) {
		X = x;
	}
	public double getY() {
		return Y;
	}
	public void setY(double y) {
		Y = y;
	}
	
	@Override
	public int hashCode() {
	    int result = name.hashCode();
	    result = 31 * result + Double.hashCode(X);
	    result = 31 * result + Double.hashCode(Y);
	    return result;
	}

	@Override
	public boolean equals(Object obj) {
	    return obj instanceof Coordinates && ((Coordinates) obj).getX() == X 
	        && ((Coordinates) obj).getY() == Y
	        && ((Coordinates) obj).getName().equals(name);
	}
	
}
