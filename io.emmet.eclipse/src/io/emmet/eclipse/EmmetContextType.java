package io.emmet.eclipse;

import org.eclipse.jface.text.templates.GlobalTemplateVariables;
import org.eclipse.jface.text.templates.TemplateContextType;

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
}
