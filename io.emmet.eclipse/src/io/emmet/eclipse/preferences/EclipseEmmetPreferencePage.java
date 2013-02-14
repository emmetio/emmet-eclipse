package io.emmet.eclipse.preferences;

import io.emmet.Emmet;
import io.emmet.eclipse.EclipseEmmetPlugin;
import io.emmet.eclipse.TabKeyHandler;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;


/**
 * This class represents a preference page that
 * is contributed to the Preferences dialog. By 
 * subclassing <samp>FieldEditorPreferencePage</samp>, we
 * can use the field support built into JFace that allows
 * us to create a page that is small and knows how to 
 * save, restore and apply itself.
 * <p>
 * This page is used to modify preferences only. They
 * are stored in the preference store that belongs to
 * the main plug-in class. That way, preferences can
 * be accessed directly via the preference store.
 */

public class EclipseEmmetPreferencePage
	extends FieldEditorPreferencePage
	implements IWorkbenchPreferencePage {

	public EclipseEmmetPreferencePage() {
		super(GRID);
		setPreferenceStore(EclipseEmmetPlugin.getDefault().getPreferenceStore());
		setDescription("Common Emmet preferences");
		
		
	}
	
	/**
	 * Creates the field editors. Field editors are abstractions of
	 * the common GUI blocks needed to manipulate various types
	 * of preferences. Each field editor knows how to save and
	 * restore itself.
	 */
	public void createFieldEditors() {
		addField(
			new BooleanFieldEditor(
				PreferenceConstants.P_TAB_EXPAND,
				"&Expand abbreviations by Tab key",
				getFieldEditorParent()));
		
		addField(
			new BooleanFieldEditor(
					PreferenceConstants.P_UPGRADE_EDITORS,
					"&Upgrade web editors",
					getFieldEditorParent()));
		
		addField(new LabelFieldEditor(
			"This option provides better newline insertion behaviour for \n" +
			"Web editors (Eclipse WTP's HTML, XML, XSL and CSS; Aptana's CSS editor).\n" +
			"For CSS editor, you can specify 'close_css_brace' variable \n" +
			"(see Variable section) with the value that will be automatically \n" +
			"inserted instead of closing brace of CSS rule defition.", getFieldEditorParent()));
		
		addField(new DirectoryFieldEditor(
			PreferenceConstants.P_EXTENSIONS_PATH,
			"E&xtensions path",
			getFieldEditorParent()));
		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {
	}
	
	private void updatePreferences() {
		TabKeyHandler.updateActivityState();
	}

	@Override
	public boolean performOk() {
		boolean result = super.performOk();
		if (result) {
			updatePreferences();
		}
		return result;
	}

	@Override
	protected void performDefaults() {
		super.performDefaults();
		updatePreferences();
	}
	
	protected void contributeButtons(Composite parent) {
		Button resetButton = new Button(parent, SWT.PUSH);
		resetButton.setText("Reload engine");
		Dialog.applyDialogFont(resetButton);
		
		resetButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				Emmet.reset();
			}
		});
		
		((GridLayout) parent.getLayout()).numColumns++;
    }
	
}