package io.emmet.eclipse;

import io.emmet.IEmmetEditor;
import io.emmet.SelectionData;
import io.emmet.TabStop;
import io.emmet.TabStopGroup;
import io.emmet.TabStopStructure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.text.TextUtilities;
import org.eclipse.jface.text.link.LinkedModeModel;
import org.eclipse.jface.text.link.LinkedModeUI;
import org.eclipse.jface.text.link.LinkedPosition;
import org.eclipse.jface.text.link.LinkedPositionGroup;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;


public class EclipseEmmetEditor implements IEmmetEditor {

	private IEditorPart editor;
	private IDocument doc;
	private String caretPlaceholder = "${0}";
	
	private static Pattern whitespaceBegin = Pattern.compile("^(\\s+)");
	
	private static String DIALOG_PROMPT = "prompt";
	private static String DIALOG_WRAP_WITH_ABBREVIATION = "wrap";
	
	private HashMap<String, ArrayList<String>> proposals;
	
	public EclipseEmmetEditor() {
		
	}
	
	public EclipseEmmetEditor(IEditorPart editor) {
		setContext(editor);
	}
	
	public void setContext(IEditorPart editor) {
		this.editor = editor;
		doc = EclipseEmmetHelper.getDocument(editor);
		if (proposals == null) {
			proposals = new HashMap<String, ArrayList<String>>();
		}
	}
	
	public boolean isValid() {
		return editor != null && doc != null;
	}
	
	@Override
	public SelectionData getSelectionRange() {
		ISelectionProvider sp = editor.getEditorSite().getSelectionProvider();
		ISelection selection = sp.getSelection();
			
		SelectionData result = new SelectionData();
		
		if (selection instanceof ITextSelection) {
			ITextSelection txSel = (ITextSelection) selection;
			result.updateRangeWithLength(txSel.getOffset(), txSel.getLength());
		}
		
		return result;
	}

	@Override
	public void createSelection(int start, int end) {
		editor.getEditorSite().getSelectionProvider().setSelection(new TextSelection(start, end - start));
	}

	@Override
	public SelectionData getCurrentLineRange() {
		return getLineRangeFromPosition(getCaretPos());
	}
	
	public SelectionData getLineRangeFromPosition(int pos) {
		SelectionData result = new SelectionData();
		
		try {
			IRegion lineInfo = doc.getLineInformationOfOffset(pos);
			result.updateRangeWithLength(lineInfo.getOffset(), lineInfo.getLength());
		} catch (BadLocationException e) { }
		
		return result;
	}

	@Override
	public int getCaretPos() {
		return getSelectionRange().getStart();
	}

	@Override
	public void setCaretPos(int pos) {
		createSelection(pos, pos);
	}

	@Override
	public String getCurrentLine() {
		return getLineFromRange(getCurrentLineRange());
	}
	
	public String getLineFromRange(SelectionData range) {
		try {
			return doc.get(range.getStart(), range.getLength());
		} catch (BadLocationException e) {
			return "";
		}
	}

	@Override
	public void replaceContent(String value) {
		replaceContent(value, 0, doc.getLength(), false);
	}

	@Override
	public void replaceContent(String value, int start) {
		replaceContent(value, start, start, false);
	}
	
	@Override
	public void replaceContent(String value, int start, int end) {
		replaceContent(value, start, end, false);
	}

	@Override
	public void replaceContent(String value, int start, int end, boolean noIndent) {
		String newValue = value;
		
		if (!noIndent) {
			String line = getLineFromRange(getLineRangeFromPosition(start));
			String padding = getStringPadding(line);
			newValue = padString(value, padding);
		}
		
		TabStopStructure tabStops = new TabStopStructure(newValue);
		newValue = tabStops.getText();
		
		try {
			doc.replace(start, end - start, newValue);
			
			int totalLinks = tabStops.getTabStopsCount();
			
			if (totalLinks < 1) {
				tabStops.addTabStopToGroup("carets", newValue.length(), newValue.length());
			}
			
			String[] tabGroups = tabStops.getSortedGroupKeys();
			TabStop firstTabStop = tabStops.getFirstTabStop();
			
			if (totalLinks > 1 || firstTabStop != null && firstTabStop.getStart() != firstTabStop.getEnd()) {
				ITextViewer viewer = EclipseEmmetHelper.getTextViewer(editor);
				LinkedModeModel model = new LinkedModeModel();
				int exitPos = -1;
				
				for (int i = 0; i < tabGroups.length; i++) {
					TabStopGroup tabGroup = tabStops.getTabStopGroup(tabGroups[i]);
					LinkedPositionGroup group = null;
					
					if (tabGroups[i].equals("carets") || tabGroups[i].equals("0")) {
						int caretCount = tabGroup.getTabStopList().size();
						for (int j = 0; j < caretCount; j++) {
							TabStop ts = tabGroup.getTabStopList().get(j);
							group = new LinkedPositionGroup();
							group.addPosition(new LinkedPosition(doc, start + ts.getStart(), ts.getLength()));
							model.addGroup(group);
							if (j == caretCount - 1) {
								exitPos = start + ts.getStart();
							}							
						}
					} else {
						group = new LinkedPositionGroup();
						
						for (int j = 0; j < tabGroup.getTabStopList().size(); j++) {
							TabStop ts = tabGroup.getTabStopList().get(j);
							group.addPosition(new LinkedPosition(doc, start + ts.getStart(), ts.getLength()));
						}
						
						model.addGroup(group);
					}
				}
				
				model.forceInstall();
				LinkedModeUI linkUI = new LinkedModeUI(model, viewer);
				if (exitPos != -1) {
					linkUI.setExitPosition(viewer, exitPos, 0, Integer.MAX_VALUE);
				}
				
				// Aptana has a buggy linked mode implementation, use simple 
				// mode for it 
				linkUI.setSimpleMode(isApatana());
				linkUI.enter();
			} else {
				setCaretPos(start + firstTabStop.getStart());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getCurrentLinePadding() {
		return getStringPadding(getCurrentLine());
	}
	
	/**
	 * Returns whitespace padding from the beginning of the text
	 * @param text
	 * @return
	 */
	private String getStringPadding(String text) {
		Matcher matcher = whitespaceBegin.matcher(text);
		if (matcher.find()) {
			return matcher.group(0);
		} else {
			return "";
		}
	}
	
	/**
	 * Repeats string <code>howMany</code> times
	 */
	public String repeatString(String str, int howMany) {
		StringBuilder result = new StringBuilder();
		
		for (int i = 0; i < howMany; i++) {
			result.append(str);
		}
		
		return result.toString();
	}
	
	public String getNewline() {
		return TextUtilities.getDefaultLineDelimiter(doc);
	}
	
	/**
	 * Indents text with padding
	 * @param {String} text Text to indent
	 * @param {String|Number} pad Padding size (number) or padding itself (string)
	 * @return {String}
	 */
	public String padString(String text, String pad) {
		StringBuilder result = new StringBuilder();
		String newline = getNewline();
		String lines[] =  text.split("\\r\\n|\\n\\r|\\r|\\n", -1);
		
		if (lines.length > 0) {
			result.append(lines[0]);
			for (int i = 1; i < lines.length; i++) {
				result.append(newline + pad + lines[i]);
			}
		} else {
			result.append(text);
		}
			
		return result.toString();
	}

	@Override
	public String getContent() {
		return doc.get();
	}

	@Override
	public String getSyntax() {
		String syntax = EditorTypeInvestigator.getSyntax(this);
		if (syntax == null)
			syntax = EditorTypeInvestigator.TYPE_HTML;
		return syntax;
	}

	@Override
	public String getProfileName() {
		return EditorTypeInvestigator.getOutputProfile(this);
	}

	public String prompt(String type, String title) {

		final Display currentDisplay = Display.getCurrent();
		String defaultValueArg = "";

		/**
		 * Answer
		 */
		class Answer {
			public String result = "";
		}

		final String message = title;
		final String dialogType = type;
		final String defaultValue = defaultValueArg;
		final Answer a = new Answer();

		if (currentDisplay != null) {
			currentDisplay.syncExec(new Runnable() {

				public void run() {
					Shell shell = currentDisplay.getActiveShell();

					if (shell != null) {
						AutoCompleteDialog dialog = dialogFactory(dialogType, message, defaultValue);
						int dialogResult = dialog.open();
						if (dialogResult == Window.OK) {
							a.result = dialog.getValue();
							addProposal(message, a.result);
						} else {
							a.result = "";
						}
					}
				}
			});
		}

		return a.result;
	}
	
	@Override
	public String prompt(String title) {
		return prompt(DIALOG_PROMPT, title);
	}
	
	public String promptWrap(String title) {
		return prompt(DIALOG_WRAP_WITH_ABBREVIATION, title);
	}
	
	private AutoCompleteDialog dialogFactory(String type, String message, String defaultValue) {
		AutoCompleteDialog dialog;
		if (type == DIALOG_WRAP_WITH_ABBREVIATION) {
			dialog = new WrapWithAbbreviationDialog(null, "Emmet Prompt", message, defaultValue);
		} else {
			dialog = new AutoCompleteDialog(null, "Emmet Prompt", message, defaultValue);
		}
		
		dialog.setProposals(getProposals(message));
		return dialog;
	}
	
	private ArrayList<String> getProposals(String title) {
		if (proposals.containsKey(title))
			return proposals.get(title);
		
		return null;
	}
	
	private void addProposal(String title, String value) {
		if (!value.equals("")) {
			if (!proposals.containsKey(title))
				proposals.put(title, new ArrayList<String>());
			
			ArrayList<String> props = proposals.get(title);
			if (!props.contains(value))
				props.add(0, value);
		}
	}

	@Override
	public String getSelection() {
		SelectionData selection = getSelectionRange();
		try {
			return doc.get(selection.getStart(), selection.getLength());
		} catch (BadLocationException e) {
			return "";
		}
	}

	@Override
	public String getFilePath() {
		return EclipseEmmetHelper.getURI(editor).substring(5);
	}
	
	public IEditorPart getEditor() {
		return editor;
	}
	
	public IDocument getDocument() {
		return doc;
	}

	public String getCaretPlaceholder() {
		return caretPlaceholder;
	}
	
	public boolean isApatana() {
		return getEditor().toString().toLowerCase().indexOf(".aptana.") != -1;
	}
	
	public void print(String msg) {
		System.out.println("ZC: " + msg);
	}
	
	/**
	 * Removes caret placeholders and tabstops from text
	 * @param text
	 * @return
	 */
	public String cleanText(String text) {
		TabStopStructure tss = new TabStopStructure(text);
		return tss.getText();
	}
}
