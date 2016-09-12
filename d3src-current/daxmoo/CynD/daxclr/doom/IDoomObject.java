package daxclr.doom;

import java.beans.PropertyChangeListener;


import daxclr.bsf.IScriptObjectProxy;

/* Object is part of the doom game */
public interface IDoomObject extends PropertyChangeListener,IScriptObjectProxy {
	/* True when the needs rebuilt when the map/game is reloaded */
	public boolean isMapSpecific();
	// return 0L if there is no C++ pointer needed to delete
	public long getPointer();
}
