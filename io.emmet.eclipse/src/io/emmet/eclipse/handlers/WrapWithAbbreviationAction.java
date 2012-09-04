package io.emmet.eclipse.handlers;

import io.emmet.Emmet;
import io.emmet.eclipse.EclipseEmmetEditor;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;


public class WrapWithAbbreviationAction extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		ActionRunner runner = ActionRunner.getSingleton();
		EclipseEmmetEditor editor = runner.getEditor();
		Emmet js = Emmet.getSingleton();
		String profileName = "eclipse";
		
		if (editor != null) {
			try {
				String abbr = editor.promptWrap("Enter abbreviation:");
				
				if (abbr != null && !abbr.equals("")) {
					// expand abbreviation with current profile
					return js.runAction(editor, "wrap_with_abbreviation", 
							abbr, editor.getSyntax(), profileName);
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return null;
	}

}
