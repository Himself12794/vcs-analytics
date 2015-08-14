package com.cisco.dft.sdk.vcs.util;

import static com.cisco.dft.sdk.vcs.util.FileExtensionMapping.FILE_EXTENSION_ASSOCIATIONS;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cisco.dft.sdk.vcs.core.ClocData;
import com.cisco.dft.sdk.vcs.core.ClocData.Header;
import com.cisco.dft.sdk.vcs.core.ClocData.LangStats;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * Used to detect language. This isn't meant to be very sophisticated, just to
 * give a general idea of what's in the repo.
 *
 * @author phwhitin
 *
 */
public final class CodeSniffer {

	private static final Logger LOGGER = LoggerFactory.getLogger("CodeSniffer");

	private CodeSniffer() {
	}

	public static Language detectLanguage(final File file) {
		return detectLanguage(file.getName());
	}

	/**
	 * Tries to guess the language based on file extension.
	 *
	 * @param file
	 * @return the language name if it mapped, or null if no mapping exists
	 */
	public static Language detectLanguage(final String file) {

		// Checking this first for files that may include the name as well as
		// the extension, such as pom.xml
		final String[] temp = file.split("\\\\");

		if (temp.length > 0) {

			final Language lang = FILE_EXTENSION_ASSOCIATIONS.get(temp[temp.length - 1]);

			if (lang != null) { return lang; }

		}

		final String[] filed = file.split("\\.");

		if (filed.length > 0) {
			
			final Language lang = FILE_EXTENSION_ASSOCIATIONS.get(filed[filed.length - 1]);
			
			return lang != null ? lang : Language.UNDEFINED;

		}

		return Language.UNDEFINED;

	}

	public static ClocData analyzeDirectory(final File directory) {

		Map<Language, LangStats> langCount = Maps.newHashMap();

		Header header = new Header();

		Iterator<File> files = FileUtils.iterateFiles(directory, FILE_EXTENSION_ASSOCIATIONS
				.keySet().toArray(new String[FILE_EXTENSION_ASSOCIATIONS.size()]), true);

		for (File file : Lists.newArrayList(files)) {

			if (file.isHidden()) {
				continue;
			}

			final Language lang = detectLanguage(file);

			header.incrementnFiles(1);

			LangStats stats = Util.putIfAbsent(langCount, lang, new LangStats(lang));
			stats.incrementnFiles(1);

		}

		return new ClocData(header, langCount);

	}

	public static int getLinesCount(File file) {
		int count = 0;

		try {
			LineNumberReader lnr = new LineNumberReader(new FileReader(file));
			long skipped = lnr.skip(Long.MAX_VALUE);

			count = skipped > 0 ? lnr.getLineNumber() + 1 : 0;
			lnr.close();
		} catch (IOException e1) {
			LOGGER.trace("Could not get line count", e1);
		}
		return count;
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

	/**
	 * Representation of many common programming languages. Made with reference
	 * to <a href="http://cloc.sourceforge.net">CLOC Sourceforge</a>
	 *
	 * There are two different types of languages as classified by this: Primary
	 * and Secondary. Refer to {@link LangType}
	 *
	 * @author phwhitin
	 *
	 */
	public static enum Language {

		C, CPP, CSS, ABAP, ACTIONSCRIPT, ADA, ADSO_IDSM, AMPLE, ANT, APEX_TRIGGER, ARDUINO_SKETCH,
		ASP, ASPdotNET, ASSEMBLY, AUTOHOTKEY, AWK,
		/** BASH */
		BOURNE_AGAIN_SHELL, BOURNE_SHELL, /** SH */
		BOURNE_AGAIN, C_SHELL, CSHARP, C_CPP_HEADER, CCS, CLOJURE, CLOJURESCRIPT, CMAKE, COBOL,
		COFFEESCRIPT, COLDFUSION, COLDFUSION_CFSSCRIPT, CUDA, CYTHON, D_DTRACE, DAL, DART, DIFF,
		DITA, DOS_BATCH, DTD, ECPP, ELIXER, ERB, ERLANG, EXPECT, FSHARP, FOCUS, FORTRAN_77,
		FORTRAN_90, FORTRAN_95, GO, GRAILS, GROOVY, HAML, HANDLEBARS, HARBOUR, HASKELL, HLSL, HTML,
		IDL, IDL_QT_PROJECT_PROLOG, INSTALLSHIELD, JAVA, JAVASCRIPT, JAVASERVER_FACES, JCL, JSON,
		JSP, KERMIT, KORN_SHELL, KOTLIN, LESS, LEX, LISP, LISP_JULIA, LISP_OPENCL,
		LIVELINK_OSCRIPT, LUA, M4, MAKE, MATLAB, MAVEN, MODULA3, MSBUILD_SCRIPT, MUMPS, MUSTACHE,
		MXML, NANT_SCRIPT, NASTRAN_DMAP, OBJECTIVE_C, OBJECTIVE_CPP, OCAML, ORACLE_FORMS,
		ORACLE_REPORTS, PASCAL, PASCAL_PUPPET, PATRAN_COMMAND_LANGUAGE, PERL, PERL_PROLOG, PHP,
		PHP_PASCAL, PIG_LATIN, PL_I, POWERSHELL, PROLOG, PROTOCOL_BUFFERS, PURESCRIPT, PYTHON, QML,
		R, RACKET, RAZOR, REXX, ROBOTFRAMEWORK, RUBY, RUBY_HTML, RUST, SAS, SASS, SCALA, SED,
		SKILL, SKILLPP, SMARTY, SOFTBRIDGE_BASIC, SQL, SQL_DATA, SQL_STORED_PROCEDURE, STANDARD_ML,
		SWIFT, TCL_TK, TEAMCENTER_MET, MTH, TITANIUM_STYLE_SHEET, TYPESCRIPT, UNITY_PREFAB, VALA,
		VALA_HEADER, VELOCITY_TEMPLATE_LANGUAGE, VERILOG_SYSTEMVERILOG, VHDL, VIM_SCRIPT,
		VISUAL_BASIC, VISUAL_FOX_PRO, VISUALFORCE_COMPONENT, VISUALFORCE_PAGE,
		WINDOWS_MESSAGE_FILE, WINDOWS_MODULE_DEFINITION, WINDOWS_RESOURCE_FILE, WIX_INCLUDE,
		WIX_SOURCE, WIX_STRING_LOCALIZATION, XAML, XBASE, XBASE_HEADER, XML, XQUERY, XSD, XSLT,
		YACC, YAML, UNDEFINED, TEAMCENTER_MTH;

		@Override
		public String toString() {

			String value = name().replace('_', ' ').replace("SHARP", "#").replace("dot", ".");

			if (this != PASCAL_PUPPET) {
				value = value.replace("PP", "++");
			}

			return value;
		}

		@JsonCreator
		public static Language getType(final String name) {
			String value = name;
			value = value.toUpperCase().replaceAll("[ /-]", "_").toUpperCase().replace('+', 'P')
					.replace("#", "SHARP").replace(".", "dot");

			try {
				return Language.valueOf(value);
			} catch (final Exception e) {
				LOGGER.debug("Error occured in mapping", e);
				return Language.UNDEFINED;
			}

		}

		public boolean isUndefined() {
			return this == UNDEFINED;
		}

	}

}
