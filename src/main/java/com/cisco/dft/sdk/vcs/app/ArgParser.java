package com.cisco.dft.sdk.vcs.app;

import java.util.Map;

import com.cisco.dft.sdk.vcs.util.Util;
import com.google.common.collect.Maps;

public final class ArgParser {
	
	private ArgParser() {}
	
	public static Map<String, String> getArgMap(String[] args) {
		Map<String, String> values = Maps.newHashMap();
		
		if (args.length > 0) {
			
			if (!args[0].startsWith("--") && !args[0].startsWith("-")) {
				values.put("action", args[0].toUpperCase());
			} else {
				values.put("action", null);
			}
			
			for (int i = 0; i < args.length; i++) {
				
				if ("--".equals(args[i].substring(0, 2))) {
					String[] value = args[i].replaceFirst("--", "").split("=");
					if (value.length > 1) {
						Util.putIfAbsent(values, value[0].toLowerCase(), value[1]);
					} else if (value.length > 0) {
						Util.putIfAbsent(values, value[0].toLowerCase(), "true");
					}
				} 
				
			}
			
		}
		return values;
	}

}
