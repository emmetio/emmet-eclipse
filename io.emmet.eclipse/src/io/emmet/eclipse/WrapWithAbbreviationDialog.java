package io.emmet.eclipse;

import io.emmet.Emmet;
import io.emmet.eclipse.handlers.ActionRunner;

import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;


public class WrapWithAbbreviationDialog extends AutoCompleteDialog {
	private Text wrapPreview;
	private static int timerDelay = 200;
	private String previewPlaceholder = "Enter abbreviation to get live preview";
	private String errorPlaceholder = "Invalid abbreviation";
	
	public WrapWithAbbreviationDialog(Shell parentShell, String dialogTitle,
			String dialogMessage, String initialValue) {
		super(parentShell, dialogTitle, dialogMessage, initialValue);
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite composite = (Composite) super.createDialogArea(parent);
		
		wrapPreview = new Text(composite, SWT.MULTI | SWT.BORDER | SWT.READ_ONLY | SWT.H_SCROLL | SWT.V_SCROLL);
        GridData wrapData = new GridData(GridData.FILL_BOTH);
        wrapData.heightHint = convertHeightInCharsToPixels(12);
        wrapPreview.setLayoutData(wrapData);
        
        setupUpdateListener(composite);
        
        applyDialogFont(composite);
		return composite;
	}
	
	private void setupUpdateListener(final Composite composite) {
		final Color defaultColor = wrapPreview.getForeground();
        final Color disabledColor = composite.getDisplay().getSystemColor(SWT.COLOR_GRAY);
        final Color errorColor = composite.getDisplay().getSystemColor(SWT.COLOR_RED);
        final Text text = getText();
        
        final Runnable task = new Runnable() {
			@Override
			public void run() {
				String curText = text.getText();
				if (curText.equals("")) {
					wrapPreview.setForeground(disabledColor);
					wrapPreview.setText(previewPlaceholder);
				} else {
					EclipseEmmetEditor editor = ActionRunner.getSingleton().getEditor();
					String result = Emmet.getSingleton().getWrapPreview(editor, curText);
					
					if (result == null || result.equals("") || result.equals("null")) {
						wrapPreview.setForeground(errorColor);
						wrapPreview.setText(errorPlaceholder);
					} else {
						wrapPreview.setForeground(defaultColor);
						wrapPreview.setText(editor.cleanText(result));
					}
				}
			}
		};
        
   		Listener listener = new Listener () {
        	private String lastText = "";
        	private Timer timer;
    		public void handleEvent (Event e) {
    			String curText = text.getText();
    			if (!curText.equals(lastText)) {
    				if (timer != null) {
    					timer.cancel();
    				}
    				timer = new Timer();
    				timer.schedule(new TimerTask() {
    					@Override
    					public void run() { 
    						composite.getDisplay().syncExec(task);
    					}
    				}, timerDelay);
    				lastText = curText;
    			}
    		}
    	};
    	
    	text.addListener(SWT.KeyUp, listener);
    	task.run();
	}
}
