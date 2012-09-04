package io.emmet.eclipse.handlers;

import io.emmet.Emmet;
import io.emmet.SelectionData;
import io.emmet.eclipse.EclipseEmmetHelper;
import io.emmet.eclipse.EclipseEmmetPlugin;
import io.emmet.eclipse.EclipseEmmetEditor;
import io.emmet.eclipse.EditorTypeInvestigator;
import io.emmet.eclipse.preferences.PreferenceConstants;

public class InsertFormattedLineBreakAction {

	public static boolean execute() {
		if (!isEnabled()) return false;
		
		EclipseEmmetEditor editor = ActionRunner.getSingleton().getEditor();
		
		if (editor != null && shouldHandle(editor)) {
			try {
				Boolean result = Emmet.getSingleton().runAction(editor, "insert_formatted_line_break_only");
				if (!result) {
					String curPadding = editor.getCurrentLinePadding();
					String content = editor.getContent();
					int caretPos = editor.getCaretPos();
					int c_len = content.length();
					String nl = editor.getNewline();
					
					String nextNl = editor.getDocument().getLineDelimiter( editor.getDocument().getLineOfOffset(caretPos) );
					
					if (nextNl != null)
						nl = nextNl;
					
					// check out next line padding
					SelectionData lineRange = editor.getCurrentLineRange();
					StringBuilder nextPadding = new StringBuilder();
					
					for (int i = lineRange.getEnd() + nl.length(); i < c_len; i++) {
						char ch = content.charAt(i);
						if (ch == ' ' || ch == '\t')
							nextPadding.append(ch);
						else 
							break;
					}
						
					if (nextPadding.length() > curPadding.length()) {
						editor.replaceContent(nl + nextPadding.toString(), caretPos, caretPos, true);
						result = true;
					}
				}
				
				return result;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return false;
	}
	
	public static boolean isEnabled() {
		return EclipseEmmetPlugin.getDefault().getPreferenceStore()
			.getBoolean(PreferenceConstants.P_UPGRADE_EDITORS);
	}
	
	/**
	 * Check if newline insertion should be handled for passed editor
	 * @param editor
	 * @return
	 */
	public static boolean shouldHandle(EclipseEmmetEditor editor) {
		String ed = EclipseEmmetHelper.getEditorString(editor);
		return ed.indexOf("org.eclipse.wst.sse") != -1 
			|| ed.indexOf("org.eclipse.wst.xsl") != -1
			|| (EclipseEmmetHelper.isApatana(editor) 
					&& editor.getSyntax() == EditorTypeInvestigator.TYPE_CSS);
	}
}
