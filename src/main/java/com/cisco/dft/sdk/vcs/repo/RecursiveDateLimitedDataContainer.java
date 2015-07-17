package com.cisco.dft.sdk.vcs.repo;

import java.util.Date;
import java.util.List;

/**
 * Convenience sub class of {@link DateLimitedDataContainer} that also provides an implementation of 
 * {@link DateLimitedData} for ease of recursion.
 * 
 * @author phwhitin
 *
 * @param <T>
 */
public class RecursiveDateLimitedDataContainer<T extends DateLimitedData> extends
		DateLimitedDataContainer<T> implements DateLimitedData {


	public RecursiveDateLimitedDataContainer(List<T> data) {
		super(data);
	}
	
	/**
	 * Checks to see if this particular piece of data falls within a time range.
	 */
	@Override
	public boolean isInDateRange(Date start, Date end, boolean inclusive) {
		boolean flag = false;

		for (T t : data) {
			flag |= t.isInDateRange(start, end, inclusive);
		}

		return flag;
	}

}
