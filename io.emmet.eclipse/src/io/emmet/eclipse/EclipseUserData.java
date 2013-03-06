package io.emmet.eclipse;

import io.emmet.IUserData;
import io.emmet.Emmet;
import io.emmet.eclipse.preferences.PreferenceConstants;
import io.emmet.eclipse.preferences.TemplateHelper;
import io.emmet.eclipse.preferences.output.OutputProfile;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.templates.Template;
import org.eclipse.jface.text.templates.persistence.TemplateStore;


import com.google.gson.Gson;

public class EclipseUserData implements IUserData {

	@Override
	public void load(Emmet ctx) {
		// since JSON with variable data types and fields in Java is pretty hard, 
		// we will collect user data into a simple hash array and then convert to
		// desired structure in JS
//		HashMap<String, Object> userData = new HashMap<String, Object>();
//		userData.put("variables", getTemplates("variables"));
//		userData.put("snippets", getTemplates("snippets"));
//		userData.put("abbreviations", getTemplates("abbreviations"));
//		userData.put("profiles", OutputProfile.allProfiles());
//		
//		Gson gson = new Gson();
//		String payload = gson.toJson(userData);
//		ctx.execJSFunction("javaLoadUserData", payload);
	}
	
	/**
	 * Loads Emmet extensions from folder
	 * @param cx
	 * @param scope
	 */
	@Override
	public void loadExtensions(Emmet ctx) {
		IPreferenceStore store = EclipseEmmetPlugin.getDefault().getPreferenceStore();
		String extensionsPath = store.getString(PreferenceConstants.P_EXTENSIONS_PATH);
		if (extensionsPath != null && extensionsPath.length() > 0) {
			File extDir = new File(extensionsPath);
			if (extDir.exists() && extDir.isDirectory()) {
				File[] files = extDir.listFiles();
				ArrayList<String> extFiles = new ArrayList<String>();
				
				try {
					for (File f : files) {
						extFiles.add(f.getCanonicalPath());
					}
				} catch (Exception e) {}
				
				Gson gson = new Gson();
				ctx.execJSFunction("javaLoadExtensions", gson.toJson(extFiles));
			}
		}
		
		// since JSON with variable data types and fields in Java is pretty hard, 
		// we will collect user data into a simple hash array and then convert to
		// desired structure in JS
		HashMap<String, Object> userData = new HashMap<String, Object>();
		userData.put("variables", getTemplates("variables"));
		userData.put("snippets", getTemplates("snippets"));
		userData.put("abbreviations", getTemplates("abbreviations"));
		userData.put("profiles", OutputProfile.allProfiles());
		
		Gson gson = new Gson();
		String payload = gson.toJson(userData);
		ctx.execJSFunction("javaLoadUserData", payload);
	}
	
	private ArrayList<ArrayList<String>> getTemplates(String type) {
		TemplateStore storage = storeFactory(type);
		Template[] templates = storage.getTemplates();
		ArrayList<ArrayList<String>> output = new ArrayList<ArrayList<String>>();
		
		for (Template template : templates) {
			ArrayList<String> templateItem = new ArrayList<String>();
			
			
			String ctxId = template.getContextTypeId();
			if (ctxId.lastIndexOf('.') != -1) {
				String syntax = ctxId.substring(ctxId.lastIndexOf('.') + 1);
				templateItem.add(syntax);
			}
			
			templateItem.add(template.getName());
			templateItem.add(EclipseTemplateProcessor.process(template.getPattern()));
			output.add(templateItem);
		}
		
		return output;
	}
	
	private TemplateStore storeFactory(String type) {
		if (type.equals("variables")) {
			return TemplateHelper.getVariableStore();
		}
		
		return TemplateHelper.getTemplateStore(type);
	}

	@Override
	public String[] additionalSourceJS() {
		return null;
	}
}
