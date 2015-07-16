package com.cisco.dft.sdk.vcs.util;

import java.util.Date;
import java.util.List;

import com.google.common.collect.Lists;

/**
 * Represents a data structure whose data entries can be restricted to a certain time frame
 * 
 * @author phwhitin
 *
 * @param <T>
 */
public class DateLimitedDataContainer<T extends DateLimitedData> {
	
	protected final List<T> data;
	
	protected List<T> limitedData = Lists.newArrayList();
	
	protected Date start, end;
	
	protected DateLimitedDataContainer(final List<T> data) {
		this.data = data;
	}

	/**
	 * Removes any date limits imposed.
	 * 
	 * @return the limited object
	 */
	public DateLimitedDataContainer<T> includeAll() {
		limitedData = Lists.newArrayList();
		setRange(null, null);
		return this;
	}
	
	protected void setRange(Date start, Date end) {
		this.start = start;
		this.end = end;
	}	
	
	/**
	 * Determines if the data is limited.
	 * 
	 * @return
	 */
	protected boolean isLimited() {
		return start != null && end != null;
	}
	
	public List<T> getData() {
		return isLimited() ? limitedData : data;
	}
	
	/**
	 * Limits returned data to a specific range.
	 * 
	 * @param start
	 *            the start date
	 * @param end
	 *            the end data
	 * @param inclusive
	 *            whether or not the range is inclusive
	 * @return the object instance for convenience
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public DateLimitedDataContainer<T> limitToRange(Date start, Date end, boolean inclusive) {
		
		includeAll();
		setRange(start, end);
		
		for (DateLimitedData dld : data) {
			if (dld.isInDateRange(start, end, inclusive)) {
				if (dld instanceof DateLimitedDataContainer) { ((DateLimitedDataContainer)dld).limitToRange(start, end, inclusive); }
				limitedData.add((T) dld);
			}
		}
		
		return this;
	}
	
	
	
}
