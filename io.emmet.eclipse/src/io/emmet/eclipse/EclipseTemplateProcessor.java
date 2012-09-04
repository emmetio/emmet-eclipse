package io.emmet.eclipse;

/**
 * Processes Eclipse template and converts it to Emmet abbreviation/snippet
 * @author sergey
 *
 */
public class EclipseTemplateProcessor {
	/**
	 * Convert Eclipse template to Emmet entry
	 * @param template
	 * @return
	 */
	public static String process(String template) {
		StringBuffer result = new StringBuffer();
		
		char ch;
		char nextCh;
		int i = 0;
		int len = template.length();
		int varEnd;
		String varName;
		
		while (i < len) {
			ch = template.charAt(i);
			nextCh = (i < len - 1) ? template.charAt(i + 1) : '\0';
			
			if (ch == '$') {
				if (nextCh == '$') { // escaping dollar sign
					result.append("\\$");
					i++;
				} else if (nextCh == '{') { // variable start
					varEnd = template.indexOf('}', i);
					if (varEnd != -1) {
						varName = template.substring(i + 2, varEnd);
						if (varName.equals("cursor")) {
							result.append('|');
						} else {
							// Leave variables as is because filters can provide
							// value substitutions
							result.append("${" + varName + "}");
						}
						i = varEnd;
					} else {
						result.append(ch);
					}
					
				} else { // just a dollar sign, escape it
					result.append("\\$");
				}
			} else {
				result.append(ch);
			}
			
			i++;
		}
		
		return result.toString();
	}
}
