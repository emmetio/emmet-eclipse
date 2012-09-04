package io.emmet;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

/**
 * A coomon structure that contains list of tabstop groups and valid
 * text for these groups
 * @author sergey
 *
 */
public class TabStopStructure {
	/**
	 * Valid text for current tabstob structure
	 */
	private String text = "";
	private HashMap<String, TabStopGroup> groups;
	
	public TabStopStructure(String text) {
		createGroups();
		
		Emmet jse = Emmet.getSingleton();
		Scriptable tabstopData = (Scriptable) jse.execJSFunction("javaExtractTabstops", text);
		if (tabstopData != null) {
			text = Context.toString(ScriptableObject.getProperty(tabstopData, "text"));
			NativeArray tabstops = (NativeArray) ScriptableObject.getProperty(tabstopData, "tabstops");
			NativeObject tabstopItem;
			for (int i = 0; i < tabstops.getLength(); i++) {
				tabstopItem = (NativeObject) ScriptableObject.getProperty(tabstops, i);
				addTabStopToGroup(
						Context.toString(ScriptableObject.getProperty(tabstopItem, "group")), 
						(int) Context.toNumber(ScriptableObject.getProperty(tabstopItem, "start")), 
						(int) Context.toNumber(ScriptableObject.getProperty(tabstopItem, "end")));
			}
		}
		setText(text);
	}
	
	private void createGroups() {
		groups = new HashMap<String, TabStopGroup>();
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getText() {
		return text;
	}
	
	public void addTabStopToGroup(String groupName, int start, int end) {
		if (!groups.containsKey(groupName)) {
			groups.put(groupName, new TabStopGroup());
		}
		
		getTabStopGroup(groupName).addTabStop(start, end);
	}

	public HashMap<String, TabStopGroup> getGroups() {
		return groups;
	}
	
	/**
	 * Returns total amount of tabstops in current structure
	 * @return
	 */
	public int getTabStopsCount() {
		int result = 0;
		for (TabStopGroup item : groups.values()) {
			result += item.getLength();
		}
		
		return result;
	}
	
	public String[] getSortedGroupKeys() {
		Set<String> keySet = groups.keySet();
		String[] keys = keySet.toArray(new String[keySet.size()]);
		Arrays.sort(keys);
		return keys;
	}
	
	public TabStop getFirstTabStop() {
		String[] names = getSortedGroupKeys();
		return (names.length > 0) ? getTabStop(names[0], 0) : null;
	}
	
	public TabStopGroup getTabStopGroup(String groupName) {
		return (TabStopGroup) groups.get(groupName);
	}
	
	public TabStop getTabStop(String groupName, int index) {
		ArrayList<TabStop> tabStops = getTabStopGroup(groupName).getTabStopList();
		return (index < tabStops.size()) ? tabStops.get(index) : null;
	}
}
