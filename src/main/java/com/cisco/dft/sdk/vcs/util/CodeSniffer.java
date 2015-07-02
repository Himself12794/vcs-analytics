package com.cisco.dft.sdk.vcs.util;

import java.io.File;
import java.util.Map;

import com.google.common.collect.Maps;

public class CodeSniffer {
	
	private static final Map<String, Language> FILE_ASSOCIATIONS = Maps.newHashMap();
	
	/**
	 * Representation of many common programming language
	 * 
	 * @author phwhitin
	 *
	 */
	public static enum Language {
		
		JAVA,
		/**C#*/
		C_SHARP,
		/**C/C++*/
		C_CPP, JAVASCRIPT, PYTHON, LUA, HTML, PHP, XML, CSS, YAML,
		/**Undefined languages or non-code files*/
		OTHER
		
	}
	
	static {
		
		Map<String, Language> a = FILE_ASSOCIATIONS;
		
		a.put("java",Language.JAVA);
		a.put("cs",Language.C_SHARP);
		a.put("c", Language.C_CPP);
		a.put("cpp", Language.C_CPP);
		a.put("h", Language.C_CPP);
		a.put("js",Language.JAVASCRIPT);
		a.put("py",Language.PYTHON);
		a.put("lua",Language.LUA);
		a.put("html",Language.HTML);
		a.put("xhtml",Language.HTML);
		a.put("php", Language.PHP);
		a.put("xml", Language.XML);
		a.put("css", Language.CSS);
		a.put("yml", Language.YAML);
		
	}
	
	/**
	 * Tries to guess the language based on file extension.
	 * 
	 * @param file
	 * @return the language name if it mapped, or null if no mapping exists
	 */
	public static Language detectLanguage(String file) {
		
		String[] filed = file.split("\\.");
		
		if (filed.length > 0) {
			
			Language lang = FILE_ASSOCIATIONS.get(filed[filed.length - 1]);
			
			return lang != null ? lang : Language.OTHER;
			
		}
		
		return Language.OTHER;
		
	}
	
	public static Language detectLanguage(File file) {
		
		if (file.isFile()) {
			
			return detectLanguage(file.getName());
			
		}
		
		return Language.OTHER;
		
	}
	
	private CodeSniffer() {}
}
