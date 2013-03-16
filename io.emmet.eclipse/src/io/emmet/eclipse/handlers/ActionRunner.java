package io.emmet.eclipse.handlers;

import io.emmet.Emmet;
import io.emmet.eclipse.EclipseEmmetHelper;
import io.emmet.eclipse.EclipseEmmetEditor;

import org.eclipse.ui.IEditorPart;


public class ActionRunner {
	private volatile static ActionRunner singleton;
	private EclipseEmmetEditor emmetEditor;
	
	private ActionRunner() {
		emmetEditor = new EclipseEmmetEditor();
	}

	public static ActionRunner getSingleton() {
		if (singleton == null) {
			synchronized (ActionRunner.class) {
				if (singleton == null)
					singleton = new ActionRunner();
			}
		}
		return singleton;
	}
	
	
	
	/**
	 * Runs Emmet action, automatically setting up context editor
	 * @param actionName Action name to perform
	 * @return
	 */
	public boolean run(String actionName) {
		EclipseEmmetEditor editor = getEditor();
		if (editor != null) {
			try {
				return Emmet.getSingleton().runAction(editor, actionName);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return false;
	}
	
	public EclipseEmmetEditor getEditor() {
		IEditorPart editor = EclipseEmmetHelper.getActiveEditor();
		if (editor != null) {
			emmetEditor.setContext(editor);
			return emmetEditor;
		}
		
		return null;
	}
}