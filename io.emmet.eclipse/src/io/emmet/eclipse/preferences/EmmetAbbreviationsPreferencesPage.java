package io.emmet.eclipse.preferences;

import io.emmet.Emmet;
import io.emmet.eclipse.EclipseEmmetPlugin;

import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.texteditor.templates.TemplatePreferencePage;


public class EmmetAbbreviationsPreferencesPage extends TemplatePreferencePage implements
		IWorkbenchPreferencePage {
	
	public EmmetAbbreviationsPreferencesPage() {
		setPreferenceStore(EclipseEmmetPlugin.getDefault().getPreferenceStore());
        setTemplateStore(TemplateHelper.getTemplateStore("abbreviations"));
        setContextTypeRegistry(TemplateHelper.getContextTypeRegistry());
        setDescription("Abbreviations for Emmet are building blocks for (X)HTML tags. " +
        		"Abbreviation should look like opening XHTML tag, e.g.:\n" +
        		"<div class=\"text\">\n\n" +
        		"The forward slash at the of tag definition means that a self-closing " +
        		"form of this element is preffered, e.g.:\n" +
        		"<img src=\"myimage.png\" />");
	}

	@Override
	protected boolean isShowFormatterSetting() {
		return false;
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