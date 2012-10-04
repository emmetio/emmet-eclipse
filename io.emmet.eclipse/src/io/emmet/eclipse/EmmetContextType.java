package io.emmet.eclipse;

import org.eclipse.jface.text.templates.GlobalTemplateVariables;
import org.eclipse.jface.text.templates.TemplateContextType;
import org.eclipse.jface.text.templates.TemplateException;

public class EmmetContextType extends TemplateContextType {

	public static final String CTX_HTML = "io.emmet.eclipse.templates.html";
	public static final String CTX_CSS = "io.emmet.eclipse.templates.css";
	public static final String CTX_XML = "io.emmet.eclipse.templates.xml";
	public static final String CTX_XSL = "io.emmet.eclipse.templates.xsl";
	public static final String CTX_HAML = "io.emmet.eclipse.templates.haml";
	
	public static final String CTX_VARIABLE = "io.emmet.eclipse.variable";
	
	

	public EmmetContextType() {
		addResolver(new GlobalTemplateVariables.Cursor());
	}
	
	@Override
	public void validate(String pattern) throws TemplateException {
		// disable validation (actually, variable validation)
		// since it incompatible with Emmet snippets
	}
	
}
