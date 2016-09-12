package daxclr.doom.server;

import java.io.Serializable;

class IdVarDef {
	long pointer;
	public native String getTypeName();
	public native String getName();
	public native Serializable getValue();
}
