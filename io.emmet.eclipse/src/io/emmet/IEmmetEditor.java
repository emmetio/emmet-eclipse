package io.emmet;

/**
 * Emmet editor interface that should be implemented in order to
 * run Emmet actions
 * @author Sergey Chikuyonok
 */
public interface IEmmetEditor {
	/**
	 * Returns character indexes of selected text: object with <code>start</code>
	 * and <code>end</code> indexes. If there's no selection, should return 
	 * list with both indexes referring to current caret position
	 * @example
	 * <pre>
	 * SelectionData selection = editor.getSelectionRange();
	 * System.out.println(selection.getStart() + ", " + selection.getEnd());
	 * </pre> 
	 */
	public SelectionData getSelectionRange();
	
	/**
	 * Creates selection from <code>start</code> to <code>end</code> character
	 * indexes.
	 * @example
	 * <pre>editor.createSelection(10, 40);</pre>
	 */
	public void createSelection(int start, int end);
	
	/**
	 * Returns current line's start and end indexes as object with <code>start</code>
	 * and <code>end</code> properties
	 * @return {Object}
	 * @example
	 * <pre>
	 * SelectionData range = editor.getCurrentLineRange();
	 * System.out.println(range.getStart() + ", " + range.getEnd();
	 * </pre>
	 */
	public SelectionData getCurrentLineRange();
	
	/**
	 * Returns current caret position
	 */
	public int getCaretPos();
	
	/**
	 * Set new caret position
	 */
	public void setCaretPos(int pos);
	
	/**
	 * Returns content of current line
	 */
	public String getCurrentLine();
	
	/**
	 * Replace current editor's content. If <code>value</code> contains 
	 * <code>caret_placeholder</code>, the editor will put caret into 
	 * this position. 
	 * @param value Content you want to paste
	 */
	public void replaceContent(String value);
	
	/**
	 * Place the <code>value</code> content at <code>start</code> string 
	 * index of current content. If <code>value</code> contains 
	 * <code>caret_placeholder</code>, the editor will put caret into 
	 * this position.
	 * @param value Content you want to paste
	 * @param start Start index of editor's content
	 */
	public void replaceContent(String value, int start);
	
	/**
	 * Replace editor's content part (from <code>start</code> to 
	 * <code>end</code> index) with <code>value</code>. If <code>value</code> 
	 * contains <code>caret_placeholder</code>, the editor will put caret into 
	 * this position.  
	 *  
	 * @param value Content you want to paste
	 * @param start Start index of editor's content
	 * @param end End index of editor's content
	 */
	public void replaceContent(String value, int start, int end);
	
	/**
	 * Replace editor's content part (from <code>start</code> to 
	 * <code>end</code> index) with <code>value</code>. If <code>value</code> 
	 * contains <code>caret_placeholder</code>, the editor will put caret into 
	 * this position.  
	 *  
	 * @param value Content you want to paste
	 * @param start Start index of editor's content
	 * @param end End index of editor's content
	 * @param no_indent Do not indent pasted value
	 */
	public void replaceContent(String value, int start, int end, boolean no_indent);
	
	/**
	 * Returns editor's content
	 */
	public String getContent();
	
	/**
	 * Returns current editor's syntax mode
	 */
	public String getSyntax();
	
	/**
	 * Returns current output profile name
	 */
	public String getProfileName();
	
	/**
	 * Ask user to enter something
	 * @param title Dialog title
	 * @return Entered data
	 * @since 0.65
	 */
	public String prompt(String title);
	
	/**
	 * Returns current selection
	 * @since 0.65
	 */
	public String getSelection();
	
	/**
	 * Returns current editor's file path
	 * @since 0.65 
	 */
	public String getFilePath();
}
