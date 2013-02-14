package io.emmet.eclipse.preferences;

import io.emmet.eclipse.EclipseEmmetPlugin;
import io.emmet.eclipse.preferences.output.OutputProfile;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;


/**
 * Class used to initialize default preference values.
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#initializeDefaultPreferences()
	 */
	public void initializeDefaultPreferences() {
		IPreferenceStore store = EclipseEmmetPlugin.getDefault().getPreferenceStore();
		store.setDefault(PreferenceConstants.P_TAB_EXPAND, true);
		store.setDefault(PreferenceConstants.P_TAB_EXT, "html, xml, xsl, xsd, css, php, tpl, less, styl, scss, sass, jade, haml, hbs");
		setupDefaultOutputProfiles();
	}
	
	private void setupDefaultOutputProfiles() {
		OutputProfile html = new OutputProfile();
		
		OutputProfile xml = new OutputProfile();
		xml.setTagCase(OutputProfile.LEAVE);
		xml.setAttrCase(OutputProfile.LEAVE);
		xml.setTagNewline(OutputProfile.TRUE);
		xml.setSelfClosing(OutputProfile.TRUE);
		
		setupOutputPrefrences("default", html);
		setupOutputPrefrences("html", html);
		setupOutputPrefrences("css", html);
		
		html.setFilters("haml");
		setupOutputPrefrences("haml", html);
		
		setupOutputPrefrences("xml", xml);
		
		xml.setFilters("html, xsl");
		setupOutputPrefrences("xsl", xml);
	}
	
	private void setupOutputPrefrences(String suffix, OutputProfile profile) {
		IPreferenceStore store = EclipseEmmetPlugin.getDefault().getPreferenceStore();
		store.setDefault(getPrefName(PreferenceConstants.P_PROFILE_TAG_CASE, suffix), profile.getTagCase());
		store.setDefault(getPrefName(PreferenceConstants.P_PROFILE_ATTR_CASE, suffix), profile.getAttrCase());
		store.setDefault(getPrefName(PreferenceConstants.P_PROFILE_ATTR_QUOTES, suffix), profile.getAttrQuotes());
		store.setDefault(getPrefName(PreferenceConstants.P_PROFILE_TAG_NEWLINE, suffix), profile.getTagNewline());
		store.setDefault(getPrefName(PreferenceConstants.P_PROFILE_PLACE_CURSOR, suffix), profile.isPlaceCaret());
		store.setDefault(getPrefName(PreferenceConstants.P_PROFILE_INDENT, suffix), profile.isIndentTags());
		store.setDefault(getPrefName(PreferenceConstants.P_PROFILE_INLINE_BREAK, suffix), profile.getInlineBreak());
		store.setDefault(getPrefName(PreferenceConstants.P_PROFILE_SELF_CLOSING_TAG, suffix), profile.getSelfClosing());
		store.setDefault(getPrefName(PreferenceConstants.P_FILTERS, suffix), profile.getFilters());
	}
	
	public static String getPrefName(String prefix, String suffix) {
		return prefix + "_" + suffix;
	}

}
