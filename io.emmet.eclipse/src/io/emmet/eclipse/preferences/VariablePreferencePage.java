package io.emmet.eclipse.preferences;

import io.emmet.eclipse.EclipseEmmetPlugin;
import io.emmet.eclipse.EmmetContextType;

import java.io.IOException;
import java.text.Collator;
import java.util.Iterator;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.StatusDialog;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.templates.Template;
import org.eclipse.jface.text.templates.persistence.TemplatePersistenceData;
import org.eclipse.jface.text.templates.persistence.TemplateStore;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;


public abstract class VariablePreferencePage extends PreferencePage implements
		IWorkbenchPreferencePage {
	
	private static final String CONTEXT_TYPE_ID = EmmetContextType.CTX_VARIABLE;

	private static final String columnValue = "Value";

	private static final String columnName = "Name";
	
	/**
	 * Dialog to edit a template. Clients will usually instantiate, but
	 * may also extend.
	 *
	 * @since 3.3
	 */
	protected static class EditVariableDialog extends StatusDialog {

		private final Template fOriginalTemplate;

		private Text fNameText;
		private Text fDescriptionText;
		private boolean fIsNameModifiable;

		private StatusInfo fValidationStatus;
		private boolean fSuppressError= true; // #4354

		private Template fNewTemplate;


		/**
		 * Creates a new dialog.
		 *
		 * @param parent the shell parent of the dialog
		 * @param template the template to edit
		 * @param edit whether this is a new template or an existing being edited
		 * @param isNameModifiable whether the name of the template may be modified
		 */
		public EditVariableDialog(Shell parent, Template template, boolean edit, boolean isNameModifiable) {
			super(parent);

			String title= edit
				? "Edit variable"
				: "New variable";
			setTitle(title);

			fOriginalTemplate= template;
			fIsNameModifiable= isNameModifiable;

			fValidationStatus= new StatusInfo();
		}

		/*
		 * @see org.eclipse.jface.dialogs.Dialog#isResizable()
		 * @since 3.4
		 */
		protected boolean isResizable() {
			return true;
		}

		/*
		 * @see org.eclipse.ui.texteditor.templates.StatusDialog#create()
		 */
		public void create() {
			super.create();
			// update initial OK button to be disabled for new templates
			boolean valid= fNameText == null || fNameText.getText().trim().length() != 0;
			if (!valid) {
				StatusInfo status = new StatusInfo();
				status.setError("You must provide variable name");
				updateButtonsEnableState(status);
	 		}
		}

		/*
		 * @see Dialog#createDialogArea(Composite)
		 */
		protected Control createDialogArea(Composite ancestor) {
			Composite parent= new Composite(ancestor, SWT.NONE);
			GridLayout layout= new GridLayout();
			layout.numColumns= 2;
			layout.marginHeight= convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
			layout.marginWidth= convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
			layout.verticalSpacing= convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
			layout.horizontalSpacing= convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
			parent.setLayout(layout);
			parent.setLayoutData(new GridData(GridData.FILL_BOTH));

			ModifyListener listener= new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					doTextWidgetChanged(e.widget);
				}
			};

			if (fIsNameModifiable) {
				createLabel(parent, "Name");

				Composite composite= new Composite(parent, SWT.NONE);
				composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
				layout= new GridLayout();
				layout.numColumns= 2;
				layout.marginWidth= 0;
				layout.marginHeight= 0;
				composite.setLayout(layout);

				fNameText= createText(composite);
				fNameText.addModifyListener(listener);
				fNameText.addFocusListener(new FocusListener() {

					public void focusGained(FocusEvent e) {
					}

					public void focusLost(FocusEvent e) {
						if (fSuppressError) {
							fSuppressError= false;
							updateButtons();
						}
					}
				});
			}

			createLabel(parent, "Value");

			int descFlags= fIsNameModifiable ? SWT.BORDER : SWT.BORDER | SWT.READ_ONLY;
			fDescriptionText= new Text(parent, descFlags );
			fDescriptionText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			fDescriptionText.addModifyListener(listener);

//			Composite composite= new Composite(parent, SWT.NONE);
//			layout= new GridLayout();
//			layout.marginWidth= 0;
//			layout.marginHeight= 0;
//			composite.setLayout(layout);
//			composite.setLayoutData(new GridData());
			
			fDescriptionText.setText(fOriginalTemplate.getPattern());

			if (fIsNameModifiable) {
				fNameText.setText(fOriginalTemplate.getName());
				fNameText.addModifyListener(listener);
			} else {
				fDescriptionText.setFocus();
			}

			applyDialogFont(parent);
			return parent;
		}

		private void doTextWidgetChanged(Widget w) {
			if (w == fNameText) {
				fSuppressError= false;
				updateButtons();
			} else if (w == fDescriptionText) {
				// oh, nothing
			}
		}

		private String getContextId() {
			return fOriginalTemplate.getContextTypeId();
		}

		private static Label createLabel(Composite parent, String name) {
			Label label= new Label(parent, SWT.NULL);
			label.setText(name);
			label.setLayoutData(new GridData());

			return label;
		}

		private static Text createText(Composite parent) {
			Text text= new Text(parent, SWT.BORDER);
			text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

			return text;
		}
		
		private void updateButtons() {
			StatusInfo status;

			boolean valid= fNameText == null || fNameText.getText().trim().length() != 0;
			if (!valid) {
				status = new StatusInfo();
				if (!fSuppressError)
					status.setError("You have to provide variable name");
			} else {
	 			status= fValidationStatus;
	 		}
			updateStatus(status);
		}

		/*
		 * @since 3.1
		 */
		protected void okPressed() {
			String name= fNameText == null ? fOriginalTemplate.getName() : fNameText.getText();
			fNewTemplate= new Template(name, name, getContextId(), fDescriptionText.getText(), false);
			super.okPressed();
		}

		/**
		 * Returns the created template.
		 *
		 * @return the created template
		 * @since 3.1
		 */
		public Template getTemplate() {
			return fNewTemplate;
		}

		/*
		 * @see org.eclipse.jface.dialogs.Dialog#getDialogBoundsSettings()
		 * @since 3.2
		 */
		protected IDialogSettings getDialogBoundsSettings() {
			String sectionName= getClass().getName() + "_dialogBounds"; //$NON-NLS-1$
			IDialogSettings settings= EclipseEmmetPlugin.getDefault().getDialogSettings();
			IDialogSettings section= settings.getSection(sectionName);
			if (section == null)
				section= settings.addNewSection(sectionName);
			return section;
		}

	}

	/**
	 * Label provider for templates.
	 */
	private class TemplateLabelProvider extends LabelProvider implements ITableLabelProvider {

		/*
		 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
		 */
		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}

		/*
		 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
		 */
		public String getColumnText(Object element, int columnIndex) {
			TemplatePersistenceData data = (TemplatePersistenceData) element;
			Template template= data.getTemplate();

			switch (columnIndex) {
				case 0:
					return template.getName();
				case 1:
					return template.getPattern();
				default:
					return ""; //$NON-NLS-1$
			}
		}
	}
	
	/** The table presenting the templates. */
	private TableViewer fTableViewer;
	
	/* buttons */
	private Button fAddButton;
	private Button fEditButton;
	private Button fRemoveButton;
	
	/** The store for our templates. */
	private TemplateStore fTemplateStore;

	/*
	 * @see PreferencePage#createContents(Composite)
	 */
	protected Control createContents(Composite ancestor) {
		Composite parent= new Composite(ancestor, SWT.NONE);
		GridLayout layout= new GridLayout();
		layout.numColumns= 2;
		layout.marginHeight= 0;
		layout.marginWidth= 0;
		parent.setLayout(layout);

        Composite innerParent= new Composite(parent, SWT.NONE);
        GridLayout innerLayout= new GridLayout();
        innerLayout.numColumns= 2;
        innerLayout.marginHeight= 0;
        innerLayout.marginWidth= 0;
        innerParent.setLayout(innerLayout);
        GridData gd= new GridData(GridData.FILL_BOTH);
        gd.horizontalSpan= 2;
        innerParent.setLayoutData(gd);

        Composite tableComposite= new Composite(innerParent, SWT.NONE);
        GridData data= new GridData(GridData.FILL_BOTH);
        data.widthHint= 360;
        data.heightHint= convertHeightInCharsToPixels(10);
        tableComposite.setLayoutData(data);
        
        TableColumnLayout columnLayout = new TableColumnLayout();
        tableComposite.setLayout(columnLayout);
		Table table= new Table(tableComposite, SWT.BORDER | SWT.MULTI | SWT.FULL_SELECTION | SWT.H_SCROLL | SWT.V_SCROLL);

		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		GC gc= new GC(getShell());
		gc.setFont(JFaceResources.getDialogFont());

		TableColumn column1= new TableColumn(table, SWT.NONE);
		column1.setText(columnName);
		int minWidth= computeMinimumColumnWidth(gc, columnName);
		columnLayout.setColumnData(column1, new ColumnWeightData(1, minWidth, true));

		TableColumn column2= new TableColumn(table, SWT.NONE);
		column2.setText(columnValue);
		minWidth= computeMinimumColumnWidth(gc, columnValue);
		columnLayout.setColumnData(column2, new ColumnWeightData(3, minWidth, true));
		
		gc.dispose();

		fTableViewer= new TableViewer(table);
		fTableViewer.setLabelProvider(new TemplateLabelProvider());
		fTableViewer.setContentProvider(new TemplateContentProvider());

		fTableViewer.setComparator(new ViewerComparator() {
			public int compare(Viewer viewer, Object object1, Object object2) {
				if ((object1 instanceof TemplatePersistenceData) && (object2 instanceof TemplatePersistenceData)) {
					Template left= ((TemplatePersistenceData) object1).getTemplate();
					Template right= ((TemplatePersistenceData) object2).getTemplate();
					int result= Collator.getInstance().compare(left.getName(), right.getName());
					if (result != 0)
						return result;
					return Collator.getInstance().compare(left.getDescription(), right.getDescription());
				}
				return super.compare(viewer, object1, object2);
			}

			public boolean isSorterProperty(Object element, String property) {
				return true;
			}
		});

		fTableViewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent e) {
				edit();
			}
		});

		fTableViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent e) {
				selectionChanged1();
			}
		});

		Composite buttons= new Composite(innerParent, SWT.NONE);
		buttons.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));
		layout= new GridLayout();
		layout.marginHeight= 0;
		layout.marginWidth= 0;
		buttons.setLayout(layout);

		fAddButton= new Button(buttons, SWT.PUSH);
		fAddButton.setText("New...");
		fAddButton.setLayoutData(getButtonGridData(fAddButton));
		fAddButton.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				add();
			}
		});

		fEditButton= new Button(buttons, SWT.PUSH);
		fEditButton.setText("Edit...");
		fEditButton.setLayoutData(getButtonGridData(fEditButton));
		fEditButton.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				edit();
			}
		});

		fRemoveButton= new Button(buttons, SWT.PUSH);
		fRemoveButton.setText("Remove");
		fRemoveButton.setLayoutData(getButtonGridData(fRemoveButton));
		fRemoveButton.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				remove();
			}
		});


		fTableViewer.setInput(fTemplateStore);

		updateButtons();
		Dialog.applyDialogFont(parent);
		innerParent.layout();

		return parent;
	}
	
	/**
	 * Return the grid data for the button.
	 *
	 * @param button the button
	 * @return the grid data
	 */
	private static GridData getButtonGridData(Button button) {
		GridData data= new GridData(GridData.FILL_HORIZONTAL);
		return data;
	}

	@Override
	public void init(IWorkbench workbench) {
		
	}
	
	private void edit() {
		IStructuredSelection selection= (IStructuredSelection) fTableViewer.getSelection();

		Object[] objects= selection.toArray();
		if ((objects == null) || (objects.length != 1))
			return;

		TemplatePersistenceData data= (TemplatePersistenceData) selection.getFirstElement();
		edit(data);
	}
	
	private void edit(TemplatePersistenceData data) {
		Template oldTemplate= data.getTemplate();
		Template newTemplate= editTemplate(new Template(oldTemplate), true, true);
		if (newTemplate != null) {

			if (!newTemplate.getName().equals(oldTemplate.getName()) &&
				MessageDialog.openQuestion(getShell(),
				"New variable",
				"New variable message"))
			{
				data= new TemplatePersistenceData(newTemplate, true);
				fTemplateStore.add(data);
				fTableViewer.refresh();
			} else {
				data.setTemplate(newTemplate);
				fTableViewer.refresh(data);
			}
			selectionChanged1();
			fTableViewer.setSelection(new StructuredSelection(data));
		}
	}
	
	/**
	 * Creates the edit dialog. Subclasses may override this method to provide a
	 * custom dialog.
	 *
	 * @param template the template being edited
	 * @param edit whether the dialog should be editable
	 * @param isNameModifiable whether the template name may be modified
	 * @return the created or modified template, or <code>null</code> if the edition failed
	 * @since 3.1
	 */
	protected Template editTemplate(Template template, boolean edit, boolean isNameModifiable) {
		EditVariableDialog dialog= new EditVariableDialog(getShell(), template, edit, isNameModifiable);
		if (dialog.open() == Window.OK) {
			return dialog.getTemplate();
		}
		return null;
	}
	
	private void selectionChanged1() {
		updateButtons();
	}

	/**
	 * Updates the buttons.
	 */
	protected void updateButtons() {
		IStructuredSelection selection= (IStructuredSelection) fTableViewer.getSelection();
		int selectionCount= selection.size();
		int itemCount= fTableViewer.getTable().getItemCount();

		fEditButton.setEnabled(selectionCount == 1);
		fRemoveButton.setEnabled(selectionCount > 0 && selectionCount <= itemCount);
	}

	private void add() {
		Template template= new Template("", "", CONTEXT_TYPE_ID, "", true);   //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		
		Template newTemplate= editTemplate(template, false, true);
		if (newTemplate != null) {
			TemplatePersistenceData data= new TemplatePersistenceData(newTemplate, true);
			fTemplateStore.add(data);
			fTableViewer.refresh();
			fTableViewer.setSelection(new StructuredSelection(data));
		}
	}
	
	private void remove() {
		IStructuredSelection selection= (IStructuredSelection) fTableViewer.getSelection();

		@SuppressWarnings("rawtypes")
		Iterator elements= selection.iterator();
		while (elements.hasNext()) {
			TemplatePersistenceData data= (TemplatePersistenceData) elements.next();
			fTemplateStore.delete(data);
		}

		fTableViewer.refresh();
	}
	
	/**
	 * Sets the template store.
	 *
	 * @param store the new template store
	 */
	public void setTemplateStore(TemplateStore store) {
		fTemplateStore= store;
	}
	
	/**
	 * Returns the template store.
	 *
	 * @return the template store
	 */
	public TemplateStore getTemplateStore() {
		return fTemplateStore;
	}
	
	private int computeMinimumColumnWidth(GC gc, String string) {
		return gc.stringExtent(string).x + 10; // pad 10 to accommodate table header trimmings
	}
	
	/*
	 * @see PreferencePage#performDefaults()
	 */
	protected void performDefaults() {
		fTemplateStore.restoreDefaults(false);
		fTableViewer.refresh();
	}

	/*
	 * @see PreferencePage#performOk()
	 */
	public boolean performOk() {
		try {
			fTemplateStore.save();
		} catch (IOException e) {
			openWriteErrorDialog(e);
		}

		return super.performOk();
	}
	
	private void openWriteErrorDialog(IOException ex) {
		IStatus status= new Status(IStatus.ERROR, EclipseEmmetPlugin.PLUGIN_ID, IStatus.OK, "Failed to write templates.", ex); //$NON-NLS-1$
		EclipseEmmetPlugin.getDefault().getLog().log(status);
		String title= "Error while saving variables";
		String message= "Error occured while saving variable preverences";
		MessageDialog.openError(getShell(), title, message);
	}
}
