package io.emmet.eclipse.preferences;

import io.emmet.eclipse.EclipseEmmetPlugin;
import io.emmet.eclipse.EmmetContextType;

import java.io.IOException;
import java.util.HashMap;

import org.eclipse.jface.text.templates.ContextTypeRegistry;
import org.eclipse.jface.text.templates.persistence.TemplateStore;
import org.eclipse.ui.editors.text.templates.ContributionContextTypeRegistry;
import org.eclipse.ui.editors.text.templates.ContributionTemplateStore;


public class TemplateHelper {
    /** The template store list. */
    private static HashMap<String, TemplateStore> fStoreList = new HashMap<String, TemplateStore>();
    
    private static TemplateStore fVariableStore;

    /** The context type registry. */
    private static ContributionContextTypeRegistry fRegistry;
    
    /** The context type registry. */
    private static ContributionContextTypeRegistry fVarsRegistry;

    /** Key to store custom templates. */
    public static final String CUSTOM_TEMPLATES_KEY = "io.emmet.eclipse.preferences.EmmetTemplatesPreferencesPage";

    /**
     * Returns this plug-in's template store.
     * 
     * @return the template store of this plug-in instance
     */
    public static TemplateStore getTemplateStore(String type) {
    	if (!fStoreList.containsKey(type)) {
    		ContributionTemplateStore store = new ContributionTemplateStore(TemplateHelper.getContextTypeRegistry(), 
    				EclipseEmmetPlugin.getDefault().getPreferenceStore(), CUSTOM_TEMPLATES_KEY + "." + type);
    		try {
    			store.load();
    			fStoreList.put(type, store);
    		} catch (IOException e) {
    			e.printStackTrace();
    			throw new RuntimeException(e);
    		}
    		
    	}
    	
        return fStoreList.get(type);
    }
    
    /**
     * Returns this plug-in's variable store.
     * 
     * @return the template store of this plug-in instance
     */
    public static TemplateStore getVariableStore() {
    	if (fVariableStore == null) {
    		fVariableStore = new ContributionTemplateStore(TemplateHelper.getVariableContextTypeRegistry(), 
    				EclipseEmmetPlugin.getDefault().getPreferenceStore(), CUSTOM_TEMPLATES_KEY + ".variable");
    		try {
    			fVariableStore .load();
    		} catch (IOException e) {
    			e.printStackTrace();
    			throw new RuntimeException(e);
    		}
    		
    	}
    	
    	return fVariableStore;
    }

    /**
     * Returns this plug-in's context type registry.
     * 
     * @return the context type registry for this plug-in instance
     */
    public static ContextTypeRegistry getContextTypeRegistry() {
        if (fRegistry == null) {
            // create and configure the contexts available in the template editor
            fRegistry = new ContributionContextTypeRegistry();
            fRegistry.addContextType(EmmetContextType.CTX_HTML);
            fRegistry.addContextType(EmmetContextType.CTX_CSS);
            fRegistry.addContextType(EmmetContextType.CTX_XML);
            fRegistry.addContextType(EmmetContextType.CTX_XSL);
            fRegistry.addContextType(EmmetContextType.CTX_HAML);
        }
        
        return fRegistry;
    }
    
    /**
     * Returns this plug-in's context type registry.
     * 
     * @return the context type registry for this plug-in instance
     */
    public static ContextTypeRegistry getVariableContextTypeRegistry() {
    	if (fVarsRegistry == null) {
    		// create and configure the contexts available in the template editor
    		fVarsRegistry = new ContributionContextTypeRegistry();
    		fVarsRegistry.addContextType(EmmetContextType.CTX_VARIABLE);
    	}
    	
    	return fVarsRegistry;
    }
}
