package daxclr.doom.server;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

import org.opencyc.cycobject.CycFort;
import org.opencyc.cycobject.CycList;
import org.opencyc.cycobject.CycNart;

import daxclr.bsf.ObjectRepository;
import daxclr.doom.IVector;
import daxclr.inference.CycAPI;
import daxclr.inference.NativeManager;

public class IdVector extends CycNart implements Comparable, IVector, Remote {
	public static final long serialVersionUID = -3885658680356392397L;

	public static CycFort[] typeFort = new CycFort[5];

	public static byte nameType(String name) {
		if (name.contains("Point"))
			return VECT_POINT;
		if (name.contains("Color") || name.contains("RGB")
				|| name.contains("Munsell"))
			return VECT_COLOR;
		if (name.contains("Angle") || name.contains("Orie"))
			return VECT_ANGLES;
		return VECT_UNKNOWN;
	}

	static public String typeName(byte type) {
		switch (type) {
		case VECT_POINT:
			return "Point3Fn";
		case VECT_ANGLES:
			return "Angles2Fn";
		case VECT_COLOR:
			return "RGBFn";
		default:
			return "TheList";
		}
	}

	static public CycFort typeFunctor(byte type) {
		if (typeFort[(int) type] == null) {
			typeFort[(int) type] = CycAPI.c(typeName(type));
		}
		return typeFort[(int) type];
	}

	static public byte functorType(CycFort f) {
		return nameType("" + f);
	}

	public IdVector(byte type, int len) {
		super();
		setFunctor(typeFunctor(type));
		vtype = type;
		setArguments(new CycList(len));
	}

	public IdVector(CycNart cycl) {
		super(cycl.toDeepCycList());
		vtype = nameType("" + cycl.getFunctor());
	}

	public IdVector(byte type) {
		this(type, (int) type);
	}

	public IdVector(String s) throws NumberFormatException {
		super();
		if (s != null || s.length() > 0) {
			String[] ss = ("" + s).trim().split(" ");
			CycList args = new CycList(ss.length);
			vtype = (byte) ss.length;
			setFunctor(typeFunctor(vtype));
			for (int i = 0; i < ss.length; i++)
				args.set(i, Double.parseDouble(ss[i]));
			this.setArguments(args);
		}
	}

	public IdVector(double[] v) {
		this((byte) v.length, v.length);
		for (int i = v.length - 1; i > -1; i--)
			set(i, v[i]);
	}

	public IdVector(double vx, double vy, double vz) {
		this(VECT_POINT, 3);
		set(2, vz);
		set(0, vx);
		set(1, vy);
	}

	public IdVector(float vx, float vy, float vz) {
		this(VECT_POINT, 3);
		set(2, vz);
		set(0, vx);
		set(1, vy);
	}

	public IdVector(double vx, double vy, double vz, double valpha) {
		this(VECT_COLOR, 4);
		set(3, valpha);
		set(2, vz);
		set(0, vx);
		set(1, vy);
	}

	public byte vtype = VECT_UNKNOWN;

	/*
	 * (non-Javadoc)
	 * 
	 * @see daxclr.doom.IVector#equals(java.lang.Object)
	 */
	public boolean equals(Object o) {
		return coerceToCycNart(o).equals(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see daxclr.doom.IVector#compareTo(java.lang.Object)
	 */
	public int compareTo(Object o) {
		return coerceToCycNart(o).compareTo(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see daxclr.doom.IVector#size()
	 */
	public int size() {
		return getArguments().size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see daxclr.doom.IVector#type()
	 */
	public int type() {
		if (vtype == VECT_UNKNOWN) {
			return nameType("" + getFunctor());
		}
		return vtype;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see daxclr.doom.IVector#get(int)
	 */
	public double get(int i0) {
		ArrayList<Double> args = (ArrayList<Double>) getArguments();
		int needs = 1 + i0 - args.size();
		while (needs-- > 0)
			args.add(new Double(0.0));
		Object v = args.get(i0);
		if (v == null)
			return 0.0;
		if (v != null && v instanceof Number)
			return ((Number) v).doubleValue();
		return 0.0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see daxclr.doom.IVector#set(int, double)
	 */
	public double set(int i0, double v) {
		double retval = get(i0);
		getArguments().set(i0, v);
		return retval;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see daxclr.doom.IVector#yaw()
	 */
	public double yaw() {
		return get(0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see daxclr.doom.IVector#pitch()
	 */
	public double pitch() {
		return get(1);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see daxclr.doom.IVector#x()
	 */
	public double x() {
		return get(0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see daxclr.doom.IVector#y()
	 */
	public double y() {
		return get(1);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see daxclr.doom.IVector#z()
	 */
	public double z() {
		return get(2);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see daxclr.doom.IVector#red()
	 */
	public double red() {
		return get(0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see daxclr.doom.IVector#blue()
	 */
	public double blue() {
		return get(1);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see daxclr.doom.IVector#green()
	 */
	public double green() {
		return get(2);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see daxclr.doom.IVector#alpha()
	 */
	public double alpha() {
		return get(3);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see daxclr.doom.IVector#x(double)
	 */
	public void x(double v) {
		set(0, v);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see daxclr.doom.IVector#y(double)
	 */
	public void y(double v) {
		set(1, v);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see daxclr.doom.IVector#z(double)
	 */
	public void z(double v) {
		set(2, v);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see daxclr.doom.IVector#red(double)
	 */
	public void red(double v) {
		set(0, v);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see daxclr.doom.IVector#blue(double)
	 */
	public void blue(double v) {
		set(1, v);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see daxclr.doom.IVector#green(double)
	 */
	public void green(double v) {
		set(2, v);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see daxclr.doom.IVector#alpha(double)
	 */
	public void alpha(double v) {
		set(3, v);
	}

	final static public IVector toVector(final double x, final double y,
			final double z) {
		return new IdVector(x, y, z);
	}

	final static public IVector toVector(final double x, final double y,
			final double z, final double a) {
		return new IdVector(x, y, z, a);
	}

	public static double distanceBetween(IdVector one, IVector two) {
		return vectorLength(vectorMinus(one, two));
	}

	public static IVector pointFrom(IdVector origin, IVector angles,
			double distance) {
		IdVector newpoint = new IdVector(origin);
		newpoint.vtype = VECT_POINT;
		newpoint.x(distance * Math.sin(angles.yaw()) * Math.sin(angles.pitch())
				+ origin.x());
		newpoint.y(distance * Math.cos(angles.yaw()) * Math.sin(angles.pitch())
				+ origin.y());
		newpoint.z(distance * Math.cos(angles.pitch()) * Math.sin(angles.yaw())
				+ origin.z());
		return newpoint;
	}

	public static IVector angleBetween(IVector origin, IdVector point) {
		IVector dif = vectorMinus(point, origin);
		IVector angles = new IdVector(VECT_ANGLES, 2);
		// angles.x(Math.tan(dif.x())); // Rallu Help
		// angles.y(Math.atan(dif.y()));
		return angles;
	}

	public static IVector vectorMinus(IdVector one, IVector two) {
		IdVector r = new IdVector(one.x() - two.x(), one.y() - two.y(), one.z()
				- two.z());
		r.vtype = one.vtype;
		return r;
	}

	public static IVector vectorPlus(IdVector one, IVector two) {
		int size = one.size() > two.size() ? one.size() : two.size();
		int len = one.size() < two.size() ? one.size() : two.size();
		IVector r = new IdVector(one.vtype, size);
		for (int i = 0; i < len; i++)
			r.set(i, Double.parseDouble("" + one.get(i))
					+ Double.parseDouble("" + two.get(i)));
		return r;
	}

	public static double vectorLength(IVector v) {
		double len = v.x() * v.x() + v.y() * v.y() + v.z() * v.z();
		return Math.sqrt(Math.abs(len));
	}

	public static double vectorDotProduct(IVector v1, IVector v2) {
		return v1.x() * v2.x() + v1.y() * v2.y() + v1.z() * v2.z();
	}

	public static boolean vectorPerpendicular(IVector v1, IVector v2) {
		return (vectorDotProduct(v1, v2) == 0);
	}

	public static double vectorAngle(IVector v1, IVector v2) {
		return (vectorDotProduct(v1, v2) / (vectorLength(v1) * vectorLength(v2)));
	}

	public String getName() throws RemoteException {
		return ObjectRepository.getResolverMap().toString(this);
	}

	public Serializable invokeObject(String cmd, Object[] cmdArgs)
			throws RemoteException, NoSuchMethodException, Exception, Error {
		Class[] classes = NativeManager.getClasses(cmdArgs);
		Object res = getClass().getMethod(cmd, classes).invoke(this, cmdArgs);
		return ObjectRepository.resolverMap.toObject(res);
	}
}
