package io.emmet;

import java.io.InputStream;
import java.io.InputStreamReader;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

public class Emmet {
	private volatile static Emmet singleton;
	private static Context cx;
	private static Scriptable scope;
	private static String snippetsJSON = "snippets.json";
	private static IUserData userDataDelegate = null;
	
	
	private static String[] coreFiles = {
		"emmet-app.js",
//		"json2.js",
		"file-interface.js",
		"java-wrapper.js"
	}; 
	
	private Emmet() {
		cx = Context.enter();
		scope = cx.initStandardObjects();
		try {
			// load core
			String[] jsSource = coreFiles.clone();
			
			// does delegate provides additional source files?
			if (userDataDelegate != null) {
				String[] addons = userDataDelegate.additionalSourceJS();
				if (addons != null) {
					System.arraycopy(addons, 0, jsSource, jsSource.length, addons.length);
				}
			}
			
			for (int i = 0; i < jsSource.length; i++) {
				cx.evaluateReader(scope, getReaderForLocalFile(jsSource[i]), jsSource[i], 1, null);
			}
			
			// load default snippets
			execJSFunction("javaLoadSystemSnippets", readLocalFile(snippetsJSON));
			
			if (userDataDelegate != null) {
				userDataDelegate.load(this);
				userDataDelegate.loadExtensions(this);
			}
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}

	public static Emmet getSingleton() {
		if (singleton == null) {
			synchronized (Emmet.class) {
				if (singleton == null) {
					singleton = new Emmet();
				}
			}
		}
		return singleton;
	}
	
	public static void setUserDataDelegate(IUserData delegate) {
		userDataDelegate = delegate;
	}
	
	public static void reset() {
		if (singleton == null)
			return;
		
		Context.exit();
		cx = null; 
		scope = null;
		singleton = null;
	}
	
	private InputStreamReader getReaderForLocalFile(String fileName) {
		InputStream is = this.getClass().getResourceAsStream(fileName);
		return new InputStreamReader(is);
	}
	
	private String readLocalFile(String fileName) {
		// using Scanner trick:
		// http://stackoverflow.com/a/5445161/1312205
		InputStream is = this.getClass().getResourceAsStream(fileName);
		try {
	        return new java.util.Scanner(is).useDelimiter("\\A").next();
	    } catch (java.util.NoSuchElementException e) {
	        return "";
	    }
	}
	
	/**
	 * Executes arbitrary JS function with passed arguments. Each argument is
	 * automatically converted to JS type
	 * @param name JS function name. May have namespaces 
	 * (e.g. <code>emmet.require('actions').get</code>)
	 * @param vargs
	 * @return
	 */
	public Object execJSFunction(String name, Object... vargs) {
		// make sure that JS context is initiated
//		getSingleton();
		
		// temporary register all variables
		Object wrappedObj;
		StringBuilder jsArgs = new StringBuilder();
		for (int i = 0; i < vargs.length; i++) {
			wrappedObj = Context.javaToJS(vargs[i], scope);
			ScriptableObject.putProperty(scope, "__javaParam" + i, wrappedObj);
			if (i > 0) {
				jsArgs.append(',');
			}
			jsArgs.append("__javaParam" + i);
		}
		
		// evaluate code
		Object result = cx.evaluateString(scope, name + "(" + jsArgs.toString() + ");", "<eval>", 1, null);
		
		// remove temp variables
		for (int i = 0; i < vargs.length; i++) {
			ScriptableObject.deleteProperty(scope, "__javaParam" + i);
		}
		
		return result;
	}
	
	/**
	 * Runs Emmet script on passed editor object (should be the first argument)
	 * @return 'True' if action was successfully executed
	 */
	public boolean runAction(Object... args) {
		return Context.toBoolean(execJSFunction("runEmmetAction", args));
	}
	
	/**
	 * Returns preview for "Wrap with Abbreviation" action
	 */
	public String getWrapPreview(IEmmetEditor editor, String abbr) {
		return Context.toString(execJSFunction("previewWrapWithAbbreviation", editor, abbr));
	}
}