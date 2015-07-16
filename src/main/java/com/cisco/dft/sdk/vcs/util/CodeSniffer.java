package com.cisco.dft.sdk.vcs.util;

import java.io.File;
import java.util.Map;

import com.google.common.collect.Maps;

/**
 * Used to detect language. This isn't meant to be very sophisticated, just to give a
 * general idea of what's in the repo.
 * 
 * @author phwhitin
 *
 */
public final class CodeSniffer {
	
	private static final Map<String, Language> FILE_ASSOCIATIONS = Maps.newHashMap();
	
	private CodeSniffer() {}
	
	/**
	 * Representation of many common programming languages.
	 * 
	 * @author phwhitin
	 *
	 */
	public static enum Language {
		
		JAVA(LangType.PRIMARY),
		/**C#*/
		C_SHARP(LangType.PRIMARY),
		/**C/C++*/
		C_CPP(LangType.PRIMARY), 
		JAVASCRIPT(LangType.PRIMARY), 
		PYTHON(LangType.PRIMARY), 
		LUA(LangType.PRIMARY), 
		HTML(LangType.PRIMARY), 
		PHP(LangType.PRIMARY), 
		XML(LangType.SECONDARY), 
		CSS(LangType.PRIMARY), 
		YAML(LangType.SECONDARY),
		BATCH(LangType.PRIMARY),
		/**Undefined languages*/
		OTHER(LangType.SECONDARY);
		
		private LangType type;
		
		Language(LangType type) {
			this.type = type;
		}
		
		public LangType getType() { return type; }
		
		public boolean isPrimary() { return type == LangType.PRIMARY; }
		
		public boolean isSecondary() { return type == LangType.SECONDARY; }
		
	}
	
	/**
	 * This is to try to determine which languages provide the bulk of an application, or are
	 * just there for config and structure.<p>
	 * This was implemented to try to weed out files that are necessary for an application, but
	 * aren't generally part of the core source code. 
	 * 
	 * @author phwhitin
	 *
	 */
	public static enum LangType {
		
		/**For types containing bulk logic or structure such as html, css, java, c, etc.*/
		PRIMARY,
		/**For more supportive types such as yaml or xml*/
		SECONDARY
		
	}
	
	static {
		
		Map<String, Language> a = FILE_ASSOCIATIONS;
		
		a.put("java",Language.JAVA);
		a.put("cs",Language.C_SHARP);
		a.put("c", Language.C_CPP);
		a.put("cc", Language.C_CPP);
		a.put("cpp", Language.C_CPP);
		a.put("cxx", Language.C_CPP);
		a.put("h", Language.C_CPP);
		a.put("hpp", Language.C_CPP);
		a.put("hxx", Language.C_CPP);
		a.put("js",Language.JAVASCRIPT);
		a.put("py",Language.PYTHON);
		a.put("pyw",Language.PYTHON);
		a.put("lua",Language.LUA);
		a.put("html",Language.HTML);
		a.put("xhtml",Language.HTML);
		a.put("shtml",Language.HTML);
		a.put("php", Language.PHP);
		a.put("php3", Language.PHP);
		a.put("phpt", Language.PHP);
		a.put("phtml", Language.PHP);
		a.put("xml", Language.XML);
		a.put("css", Language.CSS);
		a.put("bat", Language.BATCH);
		a.put("cmd", Language.BATCH);
		a.put("nt", Language.BATCH);
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
			return detectLanguage(file.getName());
	}
}
