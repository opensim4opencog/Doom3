/*
Copyleft (C) 2005 Hélio Perroni Filho
xperroni@yahoo.com
ICQ: 2490863

This file is part of ChatterBean.

ChatterBean is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 2 of the License, or (at your option) any later version.

ChatterBean is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with ChatterBean (look at the Documents/ directory); if not, either write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307 USA, or visit (http://www.gnu.org/licenses/gpl.txt).
*/

package bitoflife.chatterbean.util;

import java.io.FileNotFoundException;
import java.io.IOException;

import bsh.EvalError;
import bsh.Interpreter;
import bsh.TargetError;

/**
The main entry point for the Command distribution. The <code>Command</code> class translates command requests to BeanShell script names under the <code>Scripts/</code> directory, and then delegates the interpretation to an <code>Interpreter</code> instance. File IO exceptions are caught and translated as messages to the standard error stream.
*/
public class ScriptRunner {
    /*
    Attributes
    */

    private Interpreter interpreter = new Interpreter();

    /*
    Methods
    */

    /**
    Takes out the first argument of an array and returns the result. It is used to strip the name of the BeanShell script that is to be called off the original argument array passed to the <code>main()</code> method.
    */
    private static String[] params(String[] args) {
        if (args.length > 1) {
            String[] params = new String[args.length - 1];
            System.arraycopy(args, 1, params, 0, params.length);
            return params;
        } else
            return new String[0];
    }

    /**
    Main entry point for the <code>ScriptRunner</code> class.  
    */
    public static void main(String[] args) {
        ScriptRunner runner = new ScriptRunner();
        runner.run(args);
    }

    public void run(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage:");
            System.out.println("java -jar command.jar <command> [arguments]");
            return;
        }

        String filename = "Scripts/" + args[0] + ".bsh";

        try {
            interpreter.set("bsh.args", params(args));
            interpreter.source(filename);
        } catch (FileNotFoundException e) {
            System.err.println("File not found: " + filename);
        } catch (TargetError e) {
            System.err.println("Script threw exception: " + e);
        } catch (EvalError e) {
            System.err.println("Evaluation Error: " + e);
        } catch (IOException e) {
            System.err.println("I/O Error: "+ e);
        }
    }
}
