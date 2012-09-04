package io.emmet.eclipse.preferences;

import io.emmet.Emmet;
import io.emmet.eclipse.EclipseEmmetPlugin;

import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.texteditor.templates.TemplatePreferencePage;


public class EmmetSnippetsPreferencesPage extends TemplatePreferencePage
		implements IWorkbenchPreferencePage {
	
	public EmmetSnippetsPreferencesPage() {
		setPreferenceStore(EclipseEmmetPlugin.getDefault().getPreferenceStore());
        setTemplateStore(TemplateHelper.getTemplateStore("snippets"));
        setContextTypeRegistry(TemplateHelper.getContextTypeRegistry());
        setDescription("Snippets for Emmet are used for describing arbitrary code blocks.");
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
