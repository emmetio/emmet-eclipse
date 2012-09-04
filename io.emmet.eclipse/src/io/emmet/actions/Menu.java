package io.emmet.actions;

import io.emmet.Emmet;

import java.util.ArrayList;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.ScriptableObject;


public class Menu extends AbstractMenuItem {
	private String type = "menu";
	private String name = null;
	private ArrayList<AbstractMenuItem> items;
	
	public static Menu create() {
		// get all actions from Emmet core
		Emmet jse = Emmet.getSingleton();
		Object actions = jse.execJSFunction("require('actions').getMenu");
		
		return new Menu("Emmet", itemsFromJSArray((NativeArray) actions));
	}
	
	private static ArrayList<AbstractMenuItem> itemsFromJSArray(NativeArray ar) {
		ArrayList<AbstractMenuItem> list = new ArrayList<AbstractMenuItem>();
		
		NativeObject menuItem;
		for (int i = 0; i < ar.getLength(); i++) {
			menuItem = (NativeObject) ScriptableObject.getProperty(ar, i);
			if (Context.toString(ScriptableObject.getProperty(menuItem, "type")).equals("action")) {
				list.add(new Action(menuItem));
			} else {
				list.add(new Menu(menuItem));
			}
		}
		
		return list;
	}
	
	public Menu(String name, ArrayList<AbstractMenuItem> items) {
		this.name = name;
		this.items = items;
	}
	
	public Menu(NativeObject item) {
		this.name = Context.toString(ScriptableObject.getProperty(item, "name"));
		this.items = itemsFromJSArray((NativeArray) ScriptableObject.getProperty(item, "items"));
	}

	@Override
	public String getType() {
		return type;
	}

	@Override
	public String getName() {
		return name;
	}
	
	public ArrayList<AbstractMenuItem> getItems() {
		return items;
	}
}
