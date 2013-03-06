package io.emmet.eclipse.handlers;

import io.emmet.Emmet;
import io.emmet.eclipse.EclipseEmmetEditor;
import io.emmet.eclipse.TabKeyHandler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;


/**
 * Our sample handler extends AbstractHandler, an IHandler base class.
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
public class ExpandAbbreviationAction extends AbstractHandler {
	/**
	 * The constructor.
	 */
	public ExpandAbbreviationAction() {
	}

	/**
	 * the command has been executed, so extract extract the needed information
	 * from the application context.
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException {
		expand();
		return null;
	}
	
	public static boolean expand() {
		ActionRunner runner = ActionRunner.getSingleton();
		EclipseEmmetEditor editor = runner.getEditor();
		Emmet js = Emmet.getSingleton();
//		String profileName = "eclipse";
		
		if (editor != null) {
			try {
				// force tab key handler installation
				TabKeyHandler.install(editor.getEditor());
				
				// expand abbreviation with current profile
				return js.runAction(editor, "expand_abbreviation", editor.getSyntax());
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		
		return false;
	}
}
