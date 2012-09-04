package io.emmet.eclipse;

import io.emmet.IEmmetFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;


public class EclipseEmmetFile implements IEmmetFile {

	@Override
	public String read(String path) {
		File f = new File(path);
		StringBuffer strContent = new StringBuffer("");
		
		if (f.exists() && f.isFile() && f.canRead()) {
			int ch;
			FileInputStream fin;
			try {
				fin = new FileInputStream(f);
				while( (ch = fin.read()) != -1)
					strContent.append((char)ch);
				
				fin.close();
			} catch (Exception e) {
				return "";
			}
		}
		
		return strContent.toString();
	}

	@Override
	public String locateFile(String editorFile, String fileName) {
		File f = new File(editorFile);
		String result = null;
		File tmp;
			
		// traverse upwards to find image uri
		while (f.getParent() != null) {
			tmp = new File(this.createPath(f.getParent(), fileName));
			if (tmp.exists()) {
				try {
					result = tmp.getCanonicalPath();
				} catch (IOException e) {}
				
				break;
			}
			
			f = new File(f.getParent());
		}
		
		return result;
	}

	@Override
	public String createPath(String parent, String fileName) {
		File f = new File(parent);
		String result = null;
			
		if (f.exists()) {
			if (f.isFile()) {
				parent = f.getParent();
			}
			
			File reqFile = new File(parent, fileName);
			try {
				result = reqFile.getCanonicalPath();
			} catch (IOException e) {
			
			}
		}
		
		return result;
	}

	@Override
	public void save(String file, String content) {
		File f = new File(file);
			
		if (file.indexOf('/') != -1) {
			File f_parent = new File(f.getParent());
			f_parent.mkdirs();
		}
		
		FileOutputStream stream = null;
		try {
			if (!f.exists()) 
				f.createNewFile();
			
			stream = new FileOutputStream(file);
			
			for (int i = 0; i < content.length(); i++) {
				stream.write(content.codePointAt(i));
			}
			
			stream.write(content.getBytes());
			stream.flush();
			stream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String getExt(String file) {
		int ix = file.lastIndexOf('.');
		if (ix != -1) {
			return file.substring(ix + 1).toLowerCase();
		} else {
			return "";
		}
	}
}
