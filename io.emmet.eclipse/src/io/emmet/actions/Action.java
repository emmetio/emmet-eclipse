package io.emmet.actions;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.ScriptableObject;

public class Action extends AbstractMenuItem {
	private String type = "action";
	private String name = null;
	private String id = null;
	
	public Action(NativeObject item) {
		this.name = Context.toString(ScriptableObject.getProperty(item, "label"));
		this.id = Context.toString(ScriptableObject.getProperty(item, "name"));
	}

	@Override
	public String getType() {
		return type;
	}

	@Override
	public String getName() {
		return name;
	}
	
	public String getId() {
		return id;
	}
}