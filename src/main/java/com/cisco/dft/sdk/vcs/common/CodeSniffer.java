package com.cisco.dft.sdk.vcs.common;

import java.io.File;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.google.common.collect.Maps;

/**
 * Used to detect language. This isn't meant to be very sophisticated, just to
 * give a general idea of what's in the repo.
 * 
 * @author phwhitin
 *
 */
public final class CodeSniffer {

	private static final Map<String, Language> FILE_ASSOCIATIONS = Maps
			.newHashMap();

	private CodeSniffer() {
	}

	/**
	 * Representation of many common programming languages. 
	 * Made with reference to <a href="http://cloc.sourceforge.net">CLOC Sourceforge</a>
	 * 
	 * There are two different types of languages as classified by this: Primary and
	 * Secondary. Refer to {@link LangType}
	 * 
	 * @author phwhitin
	 *
	 */
	public static enum Language {
		
		C, CPP, CSS, ABAP, ACTIONSCRIPT, ADA, ADSO_IDSM, AMPLE, ANT,
		APEX_TRIGGER, ARDUINO_SKETCH, ASP, ASPdotNET, ASSEMBLY, AUTOHOTKEY, AWK, 
		/**BASH*/BOURNE_AGAIN_SHELL, /**SH*/BOURNE_AGAIN, C_SHELL, CSHARP, C_CPP_HEADER, CCS, CLOJURE, CLOJURESCRIPT,
		CMAKE, COBOL, COFFEESCRIPT, COLDFUSION, COLDFUSION_CFSSCRIPT, CUDA, CYTHON, D_DTRACE, DAL, DART, DIFF, DITA,
		DOS_BATCH, DTD, ECPP, ELIXER, ERB, ERLANG, EXPECT, FSHARP, FOCUS, FORTRAN_77, FORTRAN_90, FORTRAN_95, GO, GRAILS,
		GROOVY, HAML, HANDLEBARS, HARBOUR, HASKELL, HLSL, HTML, IDL, IDL_QT_PROJECT_PROLOG, INSTALLSHIELD, JAVA, JAVASCRIPT,
		JAVASERVER_FACES, JCL, JSON, JSP, KERMIT, KORN_SHELL, KOTLIN, LESS, LEX, LISP, LISP_JULIA, LISP_OPENCL, LIVELINK_OSCRIPT,
		LUA, M4, MAKE, MATLAB, MAVEN, MODULA3, MSBUILD_SCRIPT, MUMPS, MUSTACHE, MXML, NANT_SCRIPT, NASTRAN_DMAP, OBJECTIVE_C,
		OBJECTIVE_CPP, OCAML, ORACLE_FORMS, ORACLE_REPORTS, PASCAL, PASCAL_PUPPET, PATRAN_COMMAND_LANGUAGE, PERL, PERL_PROLOG,
		PHP, PHP_PASCAL, PIG_LATIN, PL_I, POWERSHELL, PROLOG, PROTOCOL_BUFFERS, PURESCRIPT, PYTHON, QML, R, RACKET, RAZOR, 
		REXX, ROBOTFRAMEWORK, RUBY, RUBY_HTML, RUST, SAS, SASS, SCALA, SED, SKILL, SKILLPP, SMARTY, SOFTBRIDGE_BASIC, 
		SQL, SQL_DATA, SQL_STORED_PROCEDURE, STANDARD_ML, SWIFT, TCL_TK, TEAMCENTER_MET, MTH, TITANIUM_STYLE_SHEET, 
		TYPESCRIPT, UNITY_PREFAB, VALA, VALA_HEADER, VELOCITY_TEMPLATE_LANGUAGE, VERILOG_SYSTEMVERILOG, VHDL, VIM_SCRIPT,
		VISUAL_BASIC, VISUAL_FOX_PRO, VISUALFORCE_COMPONENT, VISUALFORCE_PAGE, WINDOWS_MESSAGE_FILE, WINDOWS_MODULE_DEFINITION, 
		WINDOWS_RESOURCE_FILE, WIX_INCLUDE, WIX_SOURCE, WIX_STRING_LOCALIZATION, XAML, XBASE, XBASE_HEADER, XML, 
		XQUERY, XSD, XSLT, YACC, YAML, UNDEFINED;
		
		@Override
		public String toString() {
		
			String value = name().replace('_', ' ').replace("SHARP", "#").replace("dot", ".");
			
			if (this != PASCAL_PUPPET) {
				value = value.replace("PP", "++");
			}
			
			return value;
		}
		
		@JsonCreator
		public static Language getType(String name) {
			String value = name;
			value = value.toUpperCase().replaceAll("[ /-]", "_")
					.toUpperCase().replace('+', 'P')
					.replace("#", "SHARP")
					.replace(".", "dot");
			
			try {
				return Language.valueOf(value);
			} catch (Exception e) {
				Util.redirectLogError("Error occured in mapping", e);
				return Language.UNDEFINED;
			}
			
		}

	}

	/**
	 * This is to try to determine which languages provide the bulk of an
	 * application, or are just there for config and structure.
	 * <p>
	 * This was implemented to try to weed out files that are necessary for an
	 * application, but aren't generally part of the core source code.
	 * 
	 * There are two types:<br>
	 * <ol>
	 * <li>Primary - For types containing bulk logic or structure such as html,
	 * css, java, c, etc.</li>
	 * <li>Secondary - For more supportive types such as yaml or xml.</li>
	 * </ol>
	 * 
	 * @author phwhitin
	 *
	 */
	public static enum LangType {

		PRIMARY, SECONDARY

	}

	static {

		Map<String, Language> a = FILE_ASSOCIATIONS;

		a.put("java", Language.JAVA);
		a.put("cs", Language.CSHARP);
		a.put("c", Language.C);
		a.put("cc", Language.CPP);
		a.put("cpp", Language.CPP);
		a.put("cxx", Language.CPP);
		a.put("h", Language.C);
		a.put("hpp", Language.CPP);
		a.put("hxx", Language.CPP);
		a.put("js", Language.JAVASCRIPT);
		a.put("jsp", Language.JSP);
		a.put("py", Language.PYTHON);
		a.put("pyw", Language.PYTHON);
		a.put("lua", Language.LUA);
		a.put("html", Language.HTML);
		a.put("xhtml", Language.HTML);
		a.put("shtml", Language.HTML);
		a.put("php", Language.PHP);
		a.put("php3", Language.PHP);
		a.put("phpt", Language.PHP);
		a.put("phtml", Language.PHP);
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

			return lang != null ? lang : Language.UNDEFINED;

		}

		return Language.UNDEFINED;

	}

	public static Language detectLanguage(File file) {
		return detectLanguage(file.getName());
	}
	
}
