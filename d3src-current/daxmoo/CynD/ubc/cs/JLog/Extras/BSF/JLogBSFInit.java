/*
 This file is part of JLog.

 Created by Glendon Holst for Alan Mackworth and the
 "Computational Intelligence: A Logical Approach" text.

 Copyright 1998, 2000, 2002 by University of British Columbia and Alan Mackworth.

 This notice must remain in all files which belong to, or are derived from JLog.

 Check <http://jlogic.sourceforge.net/> or
 <http://sourceforge.net/projects/jlogic> for further information
 about JLog, or to contact the authors.

 JLog is free software; you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation; either version 2 of the License, or
 (at your option) any later version.

 JLog is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with JLog; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 URLs: <http://www.fsf.org> or <http://www.gnu.org>
 */

//#########################################################################
//	JLogBSFInit
//#########################################################################

package ubc.cs.JLog.Extras.BSF;

import org.apache.bsf.BSFManager;

/**
 * This class creates a BSF Manager and a Prolog BSFEngine for use of the various BSF predicates.
 * It is loaded via a LoadClass directive in the INIT_BSFLIB.TOC file, so all initializations
 * need to be done statically, because the constructor is never called.
 *
 * @author       Ulf Dittmer (Copyright 2005)
 */

public class JLogBSFInit {

	private static BSFManager mgr;
	private static JLogBSFEngine engine;

	static {
		try {
			// check whether there is an engine already;
			// if so, use that one, otherwise, create a new one
			if (JLogBSFEngine.engine == null) {
				mgr = new BSFManager();
				String[] extensions = {"plog"};
				mgr.registerScriptingEngine("prolog", "ubc.cs.JLog.Extras.BSF.JLogBSFEngine", extensions);
				engine = (JLogBSFEngine) mgr.loadScriptingEngine("prolog");
			} else {
				engine = JLogBSFEngine.engine;
				mgr = engine.getManager();
			}
		} catch (Exception ex) {
			System.err.println("Could not initialize JLog/BSF: "+ex.getMessage());

			engine.terminate();
			mgr.terminate();
		}
	}

	// package access because only the BSF classes should access these
	// static because there is no way to get an instance

	static BSFManager getBSFManager() {
		return mgr;
	}

	static JLogBSFEngine getBSFEngine() {
		return engine;
	}
}

