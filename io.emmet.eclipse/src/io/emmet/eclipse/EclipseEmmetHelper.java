package io.emmet.eclipse;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPathEditorInput;
import org.eclipse.ui.IStorageEditorInput;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.part.MultiPageEditorPart;
import org.eclipse.ui.texteditor.AbstractTextEditor;
import org.eclipse.ui.texteditor.DocumentProviderRegistry;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;

/**
 * Helper object that provides some commonly used functions for Emmet.
 * 
 * File location methods are taken from Aptana project:
 * {@link https://github.com/aptana/}
 * @author sergey
 *
 */
public class EclipseEmmetHelper {
	
	private static final String FILE_COLON = "file:"; //$NON-NLS-1$
	private static final String FILE_SLASH = FILE_COLON + "/"; //$NON-NLS-1$
	private static final String FILE_SLASH_SLASH = FILE_SLASH + "/"; //$NON-NLS-1$
	
	public static IEditorPart getActiveEditor() {
		IEditorPart editor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		return getTextEditor(editor);
	}

	/**
	 * Returns text editor object from given editor part. If given part is multipage editor,
	 * it tries to find text editor in it
	 * @param editor
	 * @return
	 */
	public static IEditorPart getTextEditor(IEditorPart editor) {
		if (editor instanceof MultiPageEditorPart) {
			IEditorPart currentPage = (IEditorPart) ((MultiPageEditorPart) editor).getSelectedPage();

			if (currentPage instanceof ITextEditor) {
				editor = (ITextEditor) currentPage;
			} else {
				ITextEditor adapter = (ITextEditor) ((MultiPageEditorPart) editor).getAdapter(ITextEditor.class);
				if (adapter != null) {
					editor = adapter;
				} else {
					editor = null;
				}
			}
		}
		
		return editor;
	}
	
	public static IDocument getActiveDocument() {
		return getDocument(getActiveEditor());
	}
	
	public static IDocument getDocument(IEditorPart editor) {
		if (editor != null) {
			IDocumentProvider dp = null;
			if (editor instanceof ITextEditor)
				dp = ((ITextEditor) editor).getDocumentProvider();
			
			if (dp == null)
				dp = DocumentProviderRegistry.getDefault().getDocumentProvider(editor.getEditorInput());
			
			if (dp != null)
				return (IDocument) dp.getDocument(editor.getEditorInput());
		}
		
		return null;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static ITextViewer getTextViewer(IEditorPart editor) {
		Field svField;
		ITextViewer viewer = null;
		
		if (editor instanceof AbstractTextEditor) {
			try {
				svField = AbstractTextEditor.class.getDeclaredField("fSourceViewer");
				svField.setAccessible(true);
				viewer = (ITextViewer) svField.get((AbstractTextEditor) editor);
			} catch (Exception e) {	}
		}
		
		if (viewer == null) {
			Class editorClass = editor.getClass();
			try {
				Method getViewer = editorClass.getMethod("getViewer", new Class[]{});
				viewer = (ITextViewer) getViewer.invoke(editor);
			} catch (Exception e) {	}
		}
		
		return viewer;
	}
	
	/**
	 * Calls IFile.getLocation if it exists and uses an Eclipse internal mechanism if the file is deleted. Look at the
	 * implementation of IFile.getLocation to see why this is necessary. Basically getLocation() returns null if the
	 * enclosing project doesn't exist so this allows the location of a deleted file to be found.
	 * 
	 * @param file
	 * @return - Absolute OS string of file location
	 */
	public static String getStringOfIFileLocation(IFile file) {
		String location = null;
		IPath path = getPathOfIFileLocation(file);
		if (path != null) {
			location = path.makeAbsolute().toOSString();
		}
		return location;
	}
	
	/**
	 * @see com.aptana.ide.core.ui.CoreUIUtils#getStringOfIFileLocation(IFile file)
	 * @param file
	 * @return - path of IFile
	 */
	public static IPath getPathOfIFileLocation(IFile file) {
		IPath location = null;
		if (file != null) {
			if (file.exists() && file.getProject() != null
					&& file.getProject().exists()) {
				location = file.getLocation();
			}
		}
		return location;
	}
	
	/**
	 * Returns the current path to the source file from an editor input.
	 * 
	 * @param input
	 *            the editor input
	 * @return the path, or null if not found
	 */
	public static String getPathFromEditorInput(IEditorInput input) {
		try {
			if (input instanceof FileEditorInput) {
				IFile file = ((FileEditorInput) input).getFile();
				return getStringOfIFileLocation(file);
			} else if (input instanceof IStorageEditorInput) {
				IStorageEditorInput sei = (IStorageEditorInput) input;
				try {
					return sei.getStorage().getFullPath().toOSString();
				} catch (Exception e) {
					if (input instanceof IPathEditorInput) {
						IPathEditorInput pin = (IPathEditorInput) input;
						return pin.getPath().toOSString();
					}
				}
			} else if (input instanceof IPathEditorInput) {
				IPathEditorInput pin = (IPathEditorInput) input;
				return pin.getPath().toOSString();
			}
		} catch (Exception e) {
			return null;
		}

		return null;
	}
	
	/**
	 * Returns the URI for the current editor (effectively the file path transformed into file://)
	 * 
	 * @param editor
	 * @return String
	 */
	public static String getURI(IEditorPart editor) {
		if (editor != null && editor.getEditorInput() != null) {
			return getURI(editor.getEditorInput());
		} else {
			return null;
		}
	}
	
	/**
	 * Returns a valid URI from the passed in editor input. This assumed that the editor input represents a file on disk
	 * 
	 * @param input
	 * @return String
	 */
	public static String getURI(IEditorInput input) {
		String s = getPathFromEditorInput(input);
		if (s == null) {
			try {
				Method method = input.getClass().getMethod("getURI"); //$NON-NLS-1$
				return ((URI) method.invoke(input)).toString();
			} catch (Exception e) {

			}
			return null;
		}
		return getURI(new File(s));
	}
	
	/**
	 * Returns a URI from a file
	 * 
	 * @param file
	 *            the file to pull from
	 * @return the string path to the file
	 */
	public static String getURI(File file) {
		return getURI(file, true);
	}

	/**
	 * Returns a URI from a file
	 * 
	 * @param file
	 *            the file to pull from
	 * @param urlEncode
	 *            do we url encode the file name
	 * @return the string path to the file
	 */
	public static String getURI(File file, boolean urlEncode) {
		String filePath = null;

		String path = file.getPath();
		if (path.startsWith("file:\\")) //$NON-NLS-1$
		{
			filePath = path.replaceAll("file:\\\\", FILE_SLASH_SLASH); //$NON-NLS-1$
		} else if (path.startsWith("http:\\")) //$NON-NLS-1$
		{
			filePath = path.replaceAll("http:\\\\", "http://"); //$NON-NLS-1$ //$NON-NLS-2$
		} else {
			try {
				filePath = file.getCanonicalPath();
			} catch (IOException e) {
				filePath = file.getAbsolutePath();
			}

			if (filePath.startsWith("\\\\")) //$NON-NLS-1$
			{
				filePath = filePath.substring(2);
			}
			filePath = appendProtocol(filePath);
		}

		filePath = filePath.replaceAll("\\\\", "/"); //$NON-NLS-1$ //$NON-NLS-2$

		if (urlEncode) {
			filePath = urlEncodeFilename(filePath.toCharArray());
		}

		URI uri;
		try {
			if (urlEncode) {
				uri = new URI(filePath).normalize();
				return uri.toString();
			} else {
				return filePath;
			}
		} catch (URISyntaxException e) {
			return filePath;
		}
	}
	
	/**
	 * Appends the file:// protocol, if none found
	 * 
	 * @param path
	 * @return String
	 */
	public static String appendProtocol(String path) {
		if (path.indexOf("://") < 0) //$NON-NLS-1$
		{
			return FILE_SLASH_SLASH + path;
		}
		return path;
	}
	
	/**
	 * This method encodes the URL, removes the spaces and brackets from the URL and replaces the same with
	 * <code>"%20"</code> and <code>"%5B" and "%5D"</code> and <code>"%7B" "%7D"</code>.
	 * 
	 * @param input
	 * @return String
	 * @since 3.0.2
	 */
	public static String urlEncodeFilename(char[] input) {

		if (input == null) {
			return null;
		}

		StringBuffer retu = new StringBuffer(input.length);
		for (int i = 0; i < input.length; i++) {
			if (input[i] == ' ') {
				retu.append("%20"); //$NON-NLS-1$
			} else if (input[i] == '[') {
				retu.append("%5B"); //$NON-NLS-1$
			} else if (input[i] == ']') {
				retu.append("%5D"); //$NON-NLS-1$
			} else if (input[i] == '{') {
				retu.append("%7B"); //$NON-NLS-1$
			} else if (input[i] == '}') {
				retu.append("%7D"); //$NON-NLS-1$
			} else if (input[i] == '`') {
				retu.append("%60"); //$NON-NLS-1$
			} else if (input[i] == '+') {
				retu.append("%2B"); //$NON-NLS-1$
			} else {
				retu.append(input[i]);
			}
		}
		return retu.toString();
	}
	
	/**
	 * Returns string representation of passed editor
	 * @param editor
	 * @return
	 */
	public static String getEditorString(EclipseEmmetEditor editor) {
		return editor.getEditor().toString().toLowerCase();
	}
	
	/**
	 * Test if current editor belongs to Aptana
	 * @param editor
	 * @return
	 */
	public static boolean isApatana(EclipseEmmetEditor editor) {
		return getEditorString(editor).indexOf(".aptana.") != -1;
	}

}
