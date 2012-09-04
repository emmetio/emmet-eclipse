package io.emmet.eclipse.preferences.output;

import io.emmet.Emmet;
import io.emmet.eclipse.EclipseEmmetPlugin;
import io.emmet.eclipse.preferences.PreferenceConstants;
import io.emmet.eclipse.preferences.PreferenceInitializer;
import io.emmet.eclipse.preferences.SpacerFieldEditor;
import io.emmet.eclipse.preferences.SpinnerFieldEditor;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;


public class DefaultOutputPreferencePage extends FieldEditorPreferencePage
		implements IWorkbenchPreferencePage {
	
	private String prefSuffix = "";
	
	public DefaultOutputPreferencePage() {
		super(FLAT);
		setPreferenceStore(EclipseEmmetPlugin.getDefault().getPreferenceStore());
		setDescription("Default output preferences for unknown syntaxes (like JavaScript, Python, etc.)");
		setPrefSuffix("default");
	}
	
	public String getPrefName(String prefix) {
		return PreferenceInitializer.getPrefName(prefix, getPrefSuffix());
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	@Override
	public void init(IWorkbench workbench) {
	}

	@Override
	protected void createFieldEditors() {
		
		addField(new RadioGroupFieldEditor(
				getPrefName(PreferenceConstants.P_PROFILE_TAG_CASE),
			"Tag case:",
			3,
			new String[][] { 
					{ "&Lowercase", OutputProfile.LOWERCASE }, 
					{ "&Uppercase", OutputProfile.UPPERCASE },
					{ "&As is", OutputProfile.LEAVE }
		}, getFieldEditorParent(), true));
		
		addField(new RadioGroupFieldEditor(
				getPrefName(PreferenceConstants.P_PROFILE_ATTR_CASE),
				"Attribute case:",
				3,
				new String[][] { 
					{ "L&owercase", OutputProfile.LOWERCASE }, 
					{ "U&ppercase", OutputProfile.UPPERCASE },
					{ "A&s is", OutputProfile.LEAVE }
				}, getFieldEditorParent(), true));
		
		addField(new RadioGroupFieldEditor(
				getPrefName(PreferenceConstants.P_PROFILE_ATTR_QUOTES),
				"Attribute quotes:",
				2,
				new String[][] { 
					{ "S&ingle", OutputProfile.SINGE_QUOTES }, 
					{ "&Double", OutputProfile.DOUBLE_QUOTES }
				}, getFieldEditorParent(), true));
		
		addField(new RadioGroupFieldEditor(
				getPrefName(PreferenceConstants.P_PROFILE_TAG_NEWLINE),
				"Each tag on new line:",
				3,
				new String[][] { 
					{ "Yes", OutputProfile.TRUE }, 
					{ "No", OutputProfile.FALSE },
					{ "Decide", OutputProfile.DECIDE }
				}, getFieldEditorParent(), true));
		
		addField(new BooleanFieldEditor(
				getPrefName(PreferenceConstants.P_PROFILE_PLACE_CURSOR),
				"Place caret placeholders in expanded abbreviations",
				getFieldEditorParent()));
		
		addField(new BooleanFieldEditor(
				getPrefName(PreferenceConstants.P_PROFILE_INDENT),
				"Indent tags",
				getFieldEditorParent()));
		
		SpinnerFieldEditor inlineBreak = new SpinnerFieldEditor(
			getPrefName(PreferenceConstants.P_PROFILE_INLINE_BREAK),
			"How many inline elements should be to force line break",
			6,
			getFieldEditorParent());
		
		inlineBreak.setValidRange(0, 99);
		addField(inlineBreak);
		
		addField(new RadioGroupFieldEditor(
				getPrefName(PreferenceConstants.P_PROFILE_SELF_CLOSING_TAG),
				"Self-closing style for writing empty elements:",
				1,
				new String[][] { 
					{ "Disabled (<br>)", OutputProfile.FALSE }, 
					{ "Enabled (<br/>)", OutputProfile.TRUE },
					{ "XHTML-style (<br />)", OutputProfile.XHTML_STYLE }
				}, getFieldEditorParent(), true));
		
		
		addField(new SpacerFieldEditor(getFieldEditorParent()));
		
		addField(
			new StringFieldEditor(getPrefName(PreferenceConstants.P_FILTERS), "Applied &filters:", getFieldEditorParent()));

	}

	public void setPrefSuffix(String prefSuffix) {
		this.prefSuffix = prefSuffix;
	}

	public String getPrefSuffix() {
		return prefSuffix;
	}
	
	@Override
	public boolean performOk() {
		Emmet.reset();
		return super.performOk();
	}
	
	@Override
	protected void performDefaults() {
		Emmet.reset();
		super.performDefaults();
	}
}
