package com.cisco.dft.sdk.vcs.util;

import static com.cisco.dft.sdk.vcs.util.Util.redirectLogError;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CLOCData {
	
	private final Header header;
	private final Map<String, LanguageStats> languageStats;
	
	public CLOCData(Header header, Map<String, LanguageStats> languageStats) {
		this.header = header;
		this.languageStats = languageStats;
	}

	public Header getHeader() {
		return header;
	}
	
	public Map<String, LanguageStats> getLanguageStats() {
		return languageStats;
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

	public static class LanguageStats {
	
		private String language;
		@JsonProperty("nFiles")
		private int nFiles;
		@JsonProperty("blank")
		private int blankLines;
		@JsonProperty("comment")
		private int commentLines;
		@JsonProperty("code")
		private int codeLines;
	
		public String getLanguage() {
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
		
		public void setLanguage(String language) {
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