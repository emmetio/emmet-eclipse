package io.emmet.eclipse.preferences;

import io.emmet.Emmet;
import io.emmet.eclipse.EclipseEmmetPlugin;

import org.eclipse.ui.IWorkbenchPreferencePage;


public class EmmetVariablesPreferencePage extends VariablePreferencePage
		implements IWorkbenchPreferencePage {
	
	public EmmetVariablesPreferencePage() {
		setPreferenceStore(EclipseEmmetPlugin.getDefault().getPreferenceStore());
        setTemplateStore(TemplateHelper.getVariableStore());
        setDescription("Variables for Emmet");
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
