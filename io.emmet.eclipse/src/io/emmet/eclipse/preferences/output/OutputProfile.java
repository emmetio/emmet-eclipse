package io.emmet.eclipse.preferences.output;

import io.emmet.eclipse.EclipseEmmetPlugin;
import io.emmet.eclipse.preferences.PreferenceConstants;
import io.emmet.eclipse.preferences.PreferenceInitializer;

import java.util.HashMap;

import org.eclipse.jface.preference.IPreferenceStore;


public class OutputProfile {
	public static final String LOWERCASE = "lower";
	public static final String UPPERCASE = "upper";
	public static final String LEAVE = "leave";
	public static final String SINGE_QUOTES = "single";
	public static final String DOUBLE_QUOTES = "double";
	public static final String TRUE = "true";
	public static final String FALSE = "false";
	public static final String DECIDE = "decide";
	public static final String XHTML_STYLE = "xhtml";
	
	public static String[] syntaxes = {"html", "xml", "xsl", "css", "haml"};

	private String tagCase = LOWERCASE;
	private String attrCase = LOWERCASE;
	private String attrQuotes = DOUBLE_QUOTES;
	private String tagNewline = DECIDE;
	private boolean placeCaret = true;
	private boolean indentTags = true;
	private int inlineBreak = 3;
	private String selfClosing = XHTML_STYLE;
	private String filters = "";
	
	/**
	 * Creates output profile object from stored preferences
	 * @param suffix Syntax suffix (html, css, etc.)
	 * @return
	 */
	public static OutputProfile createFromPreferences(String suffix) {
		OutputProfile profile = new OutputProfile();
		IPreferenceStore store = EclipseEmmetPlugin.getDefault().getPreferenceStore();
		
		profile.setTagCase(store.getString(getPrefName(PreferenceConstants.P_PROFILE_TAG_CASE, suffix)));
		profile.setAttrCase(store.getString(getPrefName(PreferenceConstants.P_PROFILE_ATTR_CASE, suffix)));
		profile.setAttrQuotes(store.getString(getPrefName(PreferenceConstants.P_PROFILE_ATTR_QUOTES, suffix)));
		profile.setTagNewline(store.getString(getPrefName(PreferenceConstants.P_PROFILE_TAG_NEWLINE, suffix)));
		profile.setPlaceCaret(store.getBoolean(getPrefName(PreferenceConstants.P_PROFILE_PLACE_CURSOR, suffix)));
		profile.setIndentTags(store.getBoolean(getPrefName(PreferenceConstants.P_PROFILE_INDENT, suffix)));
		profile.setInlineBreak(store.getInt(getPrefName(PreferenceConstants.P_PROFILE_INLINE_BREAK, suffix)));
		profile.setSelfClosing(store.getString(getPrefName(PreferenceConstants.P_PROFILE_SELF_CLOSING_TAG, suffix)));
		profile.setFilters(store.getString(getPrefName(PreferenceConstants.P_FILTERS, suffix)));
		
		return profile;
	}
	
	public static HashMap<String,OutputProfile> allProfiles() {
		HashMap<String, OutputProfile> profiles = new HashMap<String, OutputProfile>();
		for (String syntax : syntaxes) {
			profiles.put(syntax, createFromPreferences(syntax));
		}
		
		return profiles;
	}

	private static String getPrefName(String prefix, String suffix) {
		return PreferenceInitializer.getPrefName(prefix, suffix);
	}

	public String getAttrCase() {
		return attrCase;
	}

	public void setAttrCase(String attrCase) {
		this.attrCase = attrCase;
	}

	public String getAttrQuotes() {
		return attrQuotes;
	}

	public void setAttrQuotes(String attrQuotes) {
		this.attrQuotes = attrQuotes;
	}

	public String getTagNewline() {
		return tagNewline;
	}

	public void setTagNewline(String tagNewline) {
		this.tagNewline = tagNewline;
	}

	public boolean isPlaceCaret() {
		return placeCaret;
	}

	public void setPlaceCaret(boolean placeCaret) {
		this.placeCaret = placeCaret;
	}

	public boolean isIndentTags() {
		return indentTags;
	}

	public void setIndentTags(boolean indentTags) {
		this.indentTags = indentTags;
	}

	public int getInlineBreak() {
		return inlineBreak;
	}

	public void setInlineBreak(int inlineBreak) {
		this.inlineBreak = inlineBreak;
	}

	public String getSelfClosing() {
		return selfClosing;
	}

	public void setSelfClosing(String selfClosing) {
		this.selfClosing = selfClosing;
	}

	public void setTagCase(String tagCase) {
		this.tagCase = tagCase;
	}

	public String getTagCase() {
		return tagCase;
	}

	public void setFilters(String filters) {
		this.filters = filters;
	}

	public String getFilters() {
		return filters;
	}
}
