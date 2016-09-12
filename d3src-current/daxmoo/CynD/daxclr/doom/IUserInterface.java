package daxclr.doom;

import daxclr.bsf.IScriptObject;

public interface IUserInterface extends ISys {
	float guiFind(String qpath, float autoload, float needUnique, float forceUnique); 
	float guiCheck(String fname);
	String guiName(float gui);
	String guiComment(float gui);
	float guiIsInteractive(float gui);
	float guiIsUniqued(float gui);
	void guiSetUniqued(float gui, float bit);
	String guiInitFromFile(float gui, String fname, float a, float b);
	void guiHandleNamedEvent(float gui, String ename);
	void guiRedraw(float gui);
	void guiDrawCursor(float gui);
	IScriptObject guiState(float gui);
	void guiDeleteStateVar(float gui, String key);
	void guiSetStateString(float gui, String key, String value);
	void guiSetStateInt(float gui, String key, float value);
	void guiSetStateFloat(float gui, String key, float value);
	void guiSetStateBool(float gui, String key, float value);
	String guiGetStateString(float gui, String key);
	float guiGetStateInt(float gui, String key);
	float guiGetStateFloat(float gui, String key);
	float guiGetStateBool(float gui, String key);
	void guiStateChanged(float gui, float time, float redraw);
	String guiActivate(float gui, float activated, float time);
	void guiTrigger(float gui, float time);
	void guiSetCursor(float gui, float x, float y);
	float guiCursorX(float gui);
	float guiCursorY(float gui);
}