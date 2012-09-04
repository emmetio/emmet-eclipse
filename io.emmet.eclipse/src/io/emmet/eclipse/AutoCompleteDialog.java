package io.emmet.eclipse;

import java.util.ArrayList;

import org.eclipse.jface.bindings.keys.KeyStroke;
import org.eclipse.jface.bindings.keys.ParseException;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.jface.fieldassist.SimpleContentProposalProvider;
import org.eclipse.jface.fieldassist.TextContentAdapter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * Input dialog with autocomplete feature
 * @author sergey
 */
public class AutoCompleteDialog extends Dialog {
    /**
     * The title of the dialog.
     */
    private String title;

    /**
     * The message to display, or <code>null</code> if none.
     */
    private String message;

    /**
     * The input value; the empty string by default.
     */
    private String value = "";//$NON-NLS-1$

    /**
     * Ok button widget.
     */
    private Button okButton;

    /**
     * Input text widget.
     */
    private Text text;
    
    /**
     * Maximum proposals to be displayed in content assist
     */
    private int maxProposals = 10;
    
    private ArrayList<String> proposals;
    
    /**
     * Creates an input dialog with OK and Cancel buttons. Note that the dialog
     * will have no visual representation (no widgets) until it is told to open.
     * <p>
     * Note that the <code>open</code> method blocks for input dialogs.
     * </p>
     * 
     * @param parentShell
     *            the parent shell, or <code>null</code> to create a top-level
     *            shell
     * @param dialogTitle
     *            the dialog title, or <code>null</code> if none
     * @param dialogMessage
     *            the dialog message, or <code>null</code> if none
     * @param initialValue
     *            the initial input value, or <code>null</code> if none
     *            (equivalent to the empty string)
     */
    public AutoCompleteDialog(Shell parentShell, String dialogTitle,
            String dialogMessage, String initialValue) {
        super(parentShell);
        title = dialogTitle;
        message = dialogMessage;
        if (initialValue == null) {
			value = "";//$NON-NLS-1$
		} else {
			value = initialValue;
		}
        
        setProposals(new ArrayList<String>());
    }

    /*
     * (non-Javadoc) Method declared on Dialog.
     */
    protected void buttonPressed(int buttonId) {
        if (buttonId == IDialogConstants.OK_ID) {
            value = text.getText();
        } else {
            value = null;
        }
        super.buttonPressed(buttonId);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
     */
    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        if (title != null) {
			shell.setText(title);
		}
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
     */
    protected void createButtonsForButtonBar(Composite parent) {
        // create OK and Cancel buttons by default
        okButton = createButton(parent, IDialogConstants.OK_ID,
                IDialogConstants.OK_LABEL, true);
        createButton(parent, IDialogConstants.CANCEL_ID,
                IDialogConstants.CANCEL_LABEL, false);
        //do this here because setting the text will set enablement on the ok
        // button
        text.setFocus();
        if (value != null) {
            text.setText(value);
            text.selectAll();
        }
    }

    /*
     * (non-Javadoc) Method declared on Dialog.
     */
    protected Control createDialogArea(Composite parent) {
        // create composite
        Composite composite = (Composite) super.createDialogArea(parent);
        // create message
        if (message != null) {
            Label label = new Label(composite, SWT.WRAP);
            label.setText(message);
            GridData data = new GridData(GridData.GRAB_HORIZONTAL
                    | GridData.GRAB_VERTICAL | GridData.HORIZONTAL_ALIGN_FILL
                    | GridData.VERTICAL_ALIGN_CENTER);
            data.widthHint = convertHorizontalDLUsToPixels(IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH);
            label.setLayoutData(data);
            label.setFont(parent.getFont());
        }
        text = new Text(composite, getInputTextStyle());
        text.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL
                | GridData.HORIZONTAL_ALIGN_FILL));
        
		// Help the user with the possible inputs
        if (proposals != null) {
        	try {
        		String[] props = new String[Math.min(maxProposals, getProposals().size())];
        		props = getProposals().subList(0, props.length).toArray(props);
        		
        		SimpleContentProposalProvider provider = new SimpleContentProposalProvider(props);
        		provider.setFiltering(true);
        		
        		ContentProposalAdapter adapter = new ContentProposalAdapter(text,
        				new TextContentAdapter(),
        				provider,
        				KeyStroke.getInstance("ARROW_DOWN"), null);
        		
        		adapter.setProposalAcceptanceStyle(ContentProposalAdapter.PROPOSAL_REPLACE);
        		
        		composite.pack();
        		adapter.setPopupSize(new Point(text.getBounds().width, 100));
        	} catch (ParseException e) {
        		e.printStackTrace();
        	}
        }
        
        applyDialogFont(composite);
        return composite;
    }

    /**
     * Returns the ok button.
     * 
     * @return the ok button
     */
    protected Button getOkButton() {
        return okButton;
    }

    /**
     * Returns the text area.
     * 
     * @return the text area
     */
    protected Text getText() {
        return text;
    }

    /**
     * Returns the string typed into this input dialog.
     * 
     * @return the input string
     */
    public String getValue() {
        return value;
    }

	/**
	 * Returns the style bits that should be used for the input text field.
	 * Defaults to a single line entry. Subclasses may override.
	 * 
	 * @return the integer style bits that should be used when creating the
	 *         input text
	 */
	protected int getInputTextStyle() {
		return SWT.SINGLE | SWT.BORDER;
	}
	
	protected int getShellStyle() {
		return SWT.SHEET | SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL | getDefaultOrientation();
	}

	public void setMaxProposals(int maxProposals) {
		this.maxProposals = maxProposals;
	}

	public int getMaxProposals() {
		return maxProposals;
	}

	public void setProposals(ArrayList<String> proposals) {
		this.proposals = proposals;
	}

	public ArrayList<String> getProposals() {
		return proposals;
	}
}
