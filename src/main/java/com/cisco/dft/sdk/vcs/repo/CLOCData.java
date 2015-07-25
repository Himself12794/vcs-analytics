package com.cisco.dft.sdk.vcs.repo;

import static com.cisco.dft.sdk.vcs.util.Util.redirectLogError;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import com.cisco.dft.sdk.vcs.util.CodeSniffer.Language;
import com.fasterxml.jackson.annotation.JsonProperty;

public class CLOCData {
	
	private final Header header;
	private final Map<Language, LangStats> languageStats;
	
	public CLOCData() {
		this(new Header(), new HashMap<Language, LangStats>());
	}
	
	public CLOCData(Header header, Map<Language, LangStats> languageStats) {
		this.header = header;
		this.languageStats = languageStats;
	}

	public Header getHeader() {
		return header;
	}
	
	Map<Language, LangStats> getLanguageStatsMutable() {
		return languageStats;
	}
	
	public LangStats[] getLanguageStats() {
		return languageStats.values().toArray(new LangStats[languageStats.values().size()]);
	}

	void reset() {
		header.reset();
		languageStats.clear();
	}
	
	/**
	 * Copies attributes from {@code data} to this instance.
	 * 
	 * @param data
	 * @return
	 */
	CLOCData imprint(CLOCData data) {
		this.header.imprint(data.header);
		this.languageStats.clear();
		this.languageStats.putAll(data.languageStats);
		
		return this;
	}

	public String toString() {
		Class<?> clazz = this.getClass();
		StringBuilder value = new StringBuilder(clazz.getSimpleName());
		value.append("[");
		
		for (Field field : clazz.getDeclaredFields()) {
			if (Modifier.isStatic(field.getModifiers())) { continue; }
			try {
				value.append(field.getName() + "=" + field.get(this) + ", ");
			} catch (Exception e) {
				redirectLogError("An unexpected error occured in value mapping",e);
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
		private String filesPerSecond;
		private String linesPerSecond;
	
		public void setClocUrl(String clocUrl) {
			this.clocUrl = clocUrl;
		}
	
		public void setClocVersion(String clocVersion) {
			this.clocVersion = clocVersion;
		}
	
		public void setElapsedSeconds(double elapsedSeconds) {
			this.elapsedSeconds = elapsedSeconds;
		}
	
		public void setnFiles(int nFiles) {
			this.nFiles = nFiles;
		}
	
		public void setnLines(int nLines) {
			this.nLines = nLines;
		}
	
		public void setFilesPerSecond(String filesPerSecond) {
			this.filesPerSecond = filesPerSecond;
		}
	
		public void setLinesPerSecond(String linesPerSecond) {
			this.linesPerSecond = linesPerSecond;
		}
	
		public String getClocUrl() {
			return clocUrl;
		}
	
		public String getClocVersion() {
			return clocVersion;
		}
	
		public double getElapsedSeconds() {
			return elapsedSeconds;
		}
	
		public int getnFiles() {
			return nFiles;
		}
	
		public int getnLines() {
			return nLines;
		}
	
		public double getFilesPerSecond() {
			return Double.valueOf(filesPerSecond);
		}
	
		public double getLinesPerSecond() {
			return Double.valueOf(linesPerSecond);
		}
		
		private void reset() {
			this.clocUrl = "";
			this.clocVersion = "";
			this.elapsedSeconds = 0.0D;
			this.filesPerSecond = "0.0";
			this.linesPerSecond = "0.0";
			this.nFiles = 0;
			this.nLines = 0;
			
		}
		
		/**
		 * Applys all attributes of header to this instance.
		 * 
		 * @param header
		 * @return
		 */
		Header imprint(Header header) {
			this.clocUrl = header.clocUrl;
			this.clocVersion = header.clocVersion;
			this.elapsedSeconds = header.elapsedSeconds;
			this.filesPerSecond = header.filesPerSecond;
			this.linesPerSecond = header.linesPerSecond;
			this.nFiles = header.nFiles;
			this.nLines = header.nLines;
			return this;
		}

		public String toString() {
			Class<?> clazz = this.getClass();
			StringBuilder value = new StringBuilder(clazz.getSimpleName());
			value.append("[");
			
			for (Field field : clazz.getDeclaredFields()) {
				if (Modifier.isStatic(field.getModifiers())) { continue; }
				try {
					value.append(field.getName() + "=" + field.get(this) + ", ");
				} catch (Exception e) {
					redirectLogError("An unexpected error occured in value mapping",e);
				}
			}
			
			value.append("]");
			
			return value.toString();		
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
			this(Language.OTHER);
		}
		
		public LangStats(Language lang) {
			language = lang;
		}
	
		public Language getLanguage() {
			return language;
		}
	
		public int getnFiles() {
			return nFiles;
		}
	
		public int getBlankLines() {
			return blankLines;
		}
	
		public int getCommentLines() {
			return commentLines;
		}
	
		public int getCodeLines() {
			return codeLines;
		}
		
		public void setLanguage(Language language) {
			this.language = language;
		}

		public void setnFiles(int nFiles) {
			this.nFiles = nFiles;
		}

		public void setBlankLines(int blankLines) {
			this.blankLines = blankLines;
		}

		public void setCommentLines(int commentLines) {
			this.commentLines = commentLines;
		}

		public void setCodeLines(int codeLines) {
			this.codeLines = codeLines;
		}
		
		public LangStats copy() {
			LangStats langStats = new LangStats();
			
			langStats.blankLines = this.blankLines;
			langStats.codeLines = this.codeLines;
			langStats.commentLines = this.commentLines;
			langStats.language = this.language;
			langStats.nFiles = this.nFiles;
			
			return langStats;
		}

		public String toString() {
			Class<?> clazz = this.getClass();
			StringBuilder value = new StringBuilder(clazz.getSimpleName());
			value.append("[");
			
			for (Field field : clazz.getDeclaredFields()) {
				if (Modifier.isStatic(field.getModifiers())) { continue; }
				try {
					value.append(field.getName() + "=" + field.get(this) + ", ");
				} catch (Exception e) {
					redirectLogError("An unexpected error occured in value mapping",e);
				}
			}
			
			value.append("]");
			
			return value.toString();		
		}
		
	}
}