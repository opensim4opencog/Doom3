/*
Copyleft (C) 2005 Hélio Perroni Filho
xperroni@yahoo.com
ICQ: 2490863

This file is part of ChatterBean.

ChatterBean is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 2 of the License, or (at your option) any later version.

ChatterBean is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with ChatterBean (look at the Documents/ directory); if not, either write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307 USA, or visit (http://www.gnu.org/licenses/gpl.txt).
*/

package bitoflife.chatterbean;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.LinkedList;
import java.util.List;

import org.apache.bsf.BSFException;

import bsh.EvalError;

import daxclr.bsf.ObjectRepository;
import daxclr.doom.modules.RemoteDoomModule;

public class Context {
	/*
	Attributes
	*/

	//dmiles- private final Map<String, String> bot = new HashMap<String, String>();
	//dmiles-  private final Map<String, String> predicates = new HashMap<String, String>();

	private final List<Response> responses = new LinkedList<Response>();

	private String that;

	/*
	Methods
	*/

	public void addResponse(Response response) {
		responses.add(0, response);
		that = response.getNormalized();
	}

	/*
	Properties
	*/

	public String getBotPredicate(String name) {
	   //dmiles- String value = bot.get(name);
	   //dmiles- return(value != null ? value : "");
		return getPredicate(name);
	}

	public void setBotPredicate(String name, String value) {
		//dmiles-		bot.put(name, value);
		setPredicate(name,value);
	}

	public String getPredicate(String name) {
		Object value = ObjectRepository.resolverMap.get(name);
		return(value != null ? ""+value : "");
	}

	public void setPredicate(String name, String value) {
		Object svalue = ObjectRepository.resolverMap.get(name);
		if (svalue==null) svalue = value;
		ObjectRepository.resolverMap.put(name,svalue);
	}

	public String getThat() {
		if (that == null || "".equals((""+that).trim()))
			return "*";
		else
			return that;
	}

	public String getTopic() {
		String value = getPredicate("topic");
		if (value == null || "".equals(value.trim()))
			return "*";
		else
			return value;
	}

	public String getSentences(int responseIndex, int sentenceIndex) {
		Response response = responses.get(responseIndex - 1);

		return response.getSentences(sentenceIndex - 1);
	}

	public void run() {
		// TODO Auto-generated method stub
		
	}

	public Serializable invokeMethod(String cmd, Object[] cmdArgs) throws NoSuchMethodException, RemoteException, BSFException, EvalError {
		// TODO Auto-generated method stub
		return null;
	}
}
