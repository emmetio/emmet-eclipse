package io.emmet.eclipse;

import java.lang.reflect.Method;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.ITextEditor;


/**
 * Tries to investigate editor's type and syntax profile
 * @author sergey
 *
 */
public class EditorTypeInvestigator {
	public static String TYPE_HTML = "html";
	public static String TYPE_XML = "xml";
	public static String TYPE_CSS = "css";
	public static String TYPE_HAML = "haml";
	public static String TYPE_XSL = "xsl";
	
	public static String PROFILE_XML = "xml";
	public static String PROFILE_XHTML = "xhtml";
	public static String PROFILE_HTML = "html";
	public static String PROFILE_DEFAULT = "default";
	
	private EditorTypeInvestigator() {
		
	}
	
	/**
	 * Returns current editor's syntax mode
	 */
	public static String getSyntax(EclipseEmmetEditor editor) {
		String result = null;
		
		IDocument doc = editor.getDocument();
		String className = PlatformUI.getWorkbench().getActiveWorkbenchWindow()
				.getActivePage().getActiveEditor().getSite().getId().toLowerCase();
		
		// try to get current partition (true Eclipse)
		try {
			ITypedRegion[] regions = doc.computePartitioning(editor.getCaretPos(), 0);
			if (regions.length > 0) {
				result = guessSyntaxFromString(regions[0].getType());
			}
		} catch (Exception e) {	}
		
		if (result == null) {
			// try Aptana 2 way
			IEditorPart ed = editor.getEditor();
			if (ed instanceof ITextEditor) {
				Class<? extends IEditorPart> editorClass = ed.getClass();
				try {
					Method getFileContext = editorClass.getMethod("getFileContext", new Class[]{});
					Object fileContext = getFileContext.invoke(ed);
					if (fileContext != null) {
						Class<? extends Object> fcClass = fileContext.getClass();
						Method getPartition = fcClass.getMethod("getPartitionAtOffset", new Class[]{Integer.TYPE});
						ITypedRegion region = (ITypedRegion) getPartition.invoke(fileContext, new Object[]{editor.getCaretPos()});
						result = guessSyntaxFromString(region.getType());
					}
					
				} catch (Exception e) {  }
			}
		}
		
		if (result == null) {
			// try to guess syntax from editor class
			result = guessSyntaxFromString(className);
		}
		
//		if (result == null)
//			result = TYPE_HTML; // fallback to HTML
		
		// in case of WTP's XML editor, we have to check editor class too
		if (result == TYPE_XML && guessSyntaxFromString(className) == TYPE_XSL)
			result = TYPE_XSL;
		
		return result;
	}

	private static String guessSyntaxFromString(String str) {
//		System.out.println("Guess syntax from " + str);
		if (str.indexOf("xsl") != -1)
			return TYPE_XSL;
		else if (str.indexOf("xml") != -1)
			return TYPE_XML;
		else if (str.indexOf("haml") != -1)
			return TYPE_HAML;
		else if (str.indexOf("sass") != -1)
			return TYPE_CSS;
		else if (str.indexOf("css") != -1)
			return TYPE_CSS;
		else if (str.indexOf(".less.") != -1)
			return TYPE_CSS;
		else if (str.indexOf("html") != -1)
			return TYPE_HTML;
		
		return null;
	}
	
	/**
	 * Returns current output profile name
	 */
	public static String getOutputProfile(EclipseEmmetEditor editor) {
		String syntax = getSyntax(editor);
		if (syntax != null) {
			if (syntax.equals(TYPE_XML) || syntax.equals(TYPE_XSL))
				return PROFILE_XML;
			return syntax;
		}
		
		// TODO more intelligent output profile guessing
		return PROFILE_DEFAULT;
	}
}
