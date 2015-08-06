package com.cisco.dft.sdk.vcs.core;

import static com.cisco.dft.sdk.vcs.util.Util.redirectLogError;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import com.cisco.dft.sdk.vcs.util.CodeSniffer.Language;
import com.cisco.dft.sdk.vcs.util.Util;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ClocData {

	private final Header header;

	private final Map<Language, LangStats> languageStats;

	public ClocData() {
		this(new Header(), new HashMap<Language, LangStats>());
	}

	public ClocData(final Header header, final Map<Language, LangStats> languageStats) {
		this.header = header;
		this.languageStats = languageStats;
	}

	public Header getHeader() {
		return header;
	}

	public LangStats[] getLanguageStats() {
		return languageStats.values().toArray(new LangStats[languageStats.values().size()]);
	}

	Map<Language, LangStats> getLanguageStatsMutable() {
		return languageStats;
	}

	/**
	 * Copies attributes from {@code data} to this instance.
	 *
	 * @param data
	 * @return
	 */
	ClocData imprint(final ClocData data) {
		header.imprint(data.header);
		languageStats.clear();
		languageStats.putAll(data.languageStats);

		return this;
	}

	void reset() {
		header.reset();
		languageStats.clear();
	}

	@Override
	public String toString() {
		return ClocData.toString(this);
	}

	private static <T> String toString(final T t) {

		final Class<?> clazz = t.getClass();

		final StringBuilder value = new StringBuilder(clazz.getSimpleName());
		value.append("[");

		for (final Field field : clazz.getDeclaredFields()) {
			if (Modifier.isStatic(field.getModifiers())) {
				continue;
			}
			try {
				field.setAccessible(true);
				value.append(field.getName() + "=" + field.get(t) + ", ");
				field.setAccessible(false);
			} catch (final Exception e) {
				redirectLogError("An unexpected error occured in value mapping", e);
			}
		}

		value.append("]");

		return value.toString();

	}

	public static class Header {

		private String clocUrl;
		private String clocVersion;
		private double elapsedSeconds;
		private int nFiles;
		private int nLines;
		private float filesPerSecond;
		private float linesPerSecond;

		public String getClocUrl() {
			return clocUrl;
		}

		public String getClocVersion() {
			return clocVersion;
		}

		public double getElapsedSeconds() {
			return elapsedSeconds;
		}

		public double getFilesPerSecond() {
			return Double.valueOf(filesPerSecond);
		}

		public double getLinesPerSecond() {
			return Double.valueOf(linesPerSecond);
		}

		public int getnFiles() {
			return nFiles;
		}

		public int getnLines() {
			return nLines;
		}

		/**
		 * Applys all attributes of header to this instance.
		 *
		 * @param header
		 * @return
		 */
		Header imprint(final Header header) {
			clocUrl = header.clocUrl;
			clocVersion = header.clocVersion;
			elapsedSeconds = header.elapsedSeconds;
			filesPerSecond = header.filesPerSecond;
			linesPerSecond = header.linesPerSecond;
			nFiles = header.nFiles;
			nLines = header.nLines;
			return this;
		}

		private void reset() {
			imprint(new Header());

		}

		public void setClocUrl(final String clocUrl) {
			this.clocUrl = clocUrl;
		}

		public void setClocVersion(final String clocVersion) {
			this.clocVersion = clocVersion;
		}

		public void setElapsedSeconds(final double elapsedSeconds) {
			this.elapsedSeconds = elapsedSeconds;
		}

		public void setFilesPerSecond(final float filesPerSecond) {
			this.filesPerSecond = filesPerSecond;
		}

		public void setLinesPerSecond(final float linesPerSecond) {
			this.linesPerSecond = linesPerSecond;
		}

		public void setnFiles(final int nFiles) {
			this.nFiles = nFiles;
		}

		public void setnLines(final int nLines) {
			this.nLines = nLines;
		}

		@Override
		public String toString() {
			return ClocData.toString(this);
		}

	}

	public static class LangStats {

		private Language language;

		@JsonProperty("nFiles")
		private int nFiles;
		@JsonProperty("blank")
		private int blankLines;
		@JsonProperty("comment")
		private int commentLines;
		@JsonProperty("code")
		private int codeLines;

		public LangStats() {
			this(Language.UNDEFINED);
		}

		public LangStats(final Language lang) {
			language = lang;
		}

		public LangStats copy() {
			final LangStats langStats = new LangStats();

			langStats.blankLines = blankLines;
			langStats.codeLines = codeLines;
			langStats.commentLines = commentLines;
			langStats.language = language;
			langStats.nFiles = nFiles;

			return langStats;
		}

		public int getBlankLines() {
			return blankLines;
		}

		public int getCodeLines() {
			return codeLines;
		}

		public int getCommentLines() {
			return commentLines;
		}

		public Language getLanguage() {
			return language;
		}

		public int getnFiles() {
			return nFiles;
		}

		public void setBlankLines(final int blankLines) {
			this.blankLines = blankLines;
		}

		public void setCodeLines(final int codeLines) {
			this.codeLines = codeLines;
		}

		public void setCommentLines(final int commentLines) {
			this.commentLines = commentLines;
		}

		public void setLanguage(final Language language) {
			this.language = language;
		}

		public void setnFiles(final int nFiles) {
			this.nFiles = nFiles;
		}

		@Override
		public String toString() {
			return toString(true);
		}

		public String toString(final boolean includeHeader) {

			final StringBuilder value = new StringBuilder();

			if (includeHeader) {
				value.append(getHeader());
			}

			value.append(Util.valueWithSpaces(language.toString()));
			value.append(Util.valueWithSpaces(nFiles));
			value.append(Util.valueWithSpaces(codeLines));
			value.append(Util.valueWithSpaces(commentLines));
			value.append(Util.valueWithSpaces(blankLines));

			value.append("\n");

			return value.toString();

		}

		/**
		 * The header for the data columns.
		 *
		 * @return
		 */
		public static String getHeader() {

			final StringBuilder value = new StringBuilder(Util.valueWithSpaces("Language"));

			value.append(Util.valueWithSpaces("Files"));
			value.append(Util.valueWithSpaces("Code Lines"));
			value.append(Util.valueWithSpaces("Comment Lines"));
			value.append(Util.valueWithSpaces("Blank Lines"));

			value.append("\n");

			return value.toString();

		}

	}
}