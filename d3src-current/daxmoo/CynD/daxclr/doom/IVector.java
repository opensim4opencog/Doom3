package daxclr.doom;

public interface IVector {
	public abstract boolean equals(Object o);
	public abstract int compareTo(Object o);
	public abstract int size();
	public abstract int type();
	public static final byte VECT_UNKNOWN = 0;
	public abstract double get(int i0);
	public abstract double set(int i0, double v);
	public static final byte VECT_ANGLES = 2;
	public abstract double yaw();
	public abstract double pitch();
	public static final byte VECT_POINT = 3;
	public abstract double x();
	public abstract double y();
	public abstract double z();
	public abstract void x(double v);
	public abstract void y(double v);
	public abstract void z(double v);
	public static final byte VECT_COLOR = 4;
	public abstract double red();
	public abstract double green();
	public abstract double blue();
	public abstract double alpha();
	public abstract void red(double v);
	public abstract void green(double v);
	public abstract void blue(double v);
	public abstract void alpha(double v);
}