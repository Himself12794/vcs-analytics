package com.cisco.dft.sdk.vcs.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.common.collect.Lists;
import com.google.common.collect.Range;

/**
 * Represents a data structure whose data entries can be restricted to a certain
 * time frame. This class is recursive, so if {@code T} is also an instance of
 * DateLimitedDataContainer, it will check the date limit on that as well, and
 * so on. If desired, this can also work similarly to a list as long as all of
 * its entries implement {@link DateLimitedData}
 *
 * @author phwhitin
 *
 * @param <T>
 */
public class DateLimitedDataContainer<T extends DateLimitedData> {

	protected final List<T> data;

	protected List<T> limitedData = Lists.newArrayList();

	protected Range<Date> dateRange = Range.all();

	public DateLimitedDataContainer() {
		this(new ArrayList<T>());
	}

	public DateLimitedDataContainer(final List<T> data) {
		this.data = data;
		includeAll();
	}

	/**
	 * Delegate for {@link List#add(Object)}.
	 *
	 * @param t
	 * @return
	 */
	public boolean add(final T t) {
		return t.isInDateRange(this.dateRange) ? this.data.add(t) && this.limitedData.add(t)
				: this.data.add(t);
	}

	public DateLimitedDataContainer<T> copy() {

		final DateLimitedDataContainer<T> theCopy = new DateLimitedDataContainer<T>(this.data);
		theCopy.dateRange = this.dateRange;
		theCopy.limitedData = this.limitedData;

		return theCopy;
	}

	/**
	 * Delegate for {@link List#get(int)}. Works appropriately if the data is
	 * limited.
	 *
	 * @param index
	 * @return
	 */
	public T get(final int index) {
		return isLimited() ? this.limitedData.get(index) : this.data.get(index);
	}

	/**
	 * Gets the list associated with the data.
	 *
	 * @return if limited, the limited data, else the full data
	 */
	public List<T> getData() {
		return isLimited() ? this.limitedData : this.data;
	}

	/**
	 * The current date range used for this repo.
	 *
	 * @return
	 */
	public Range<Date> getDateRange() {
		return this.dateRange;
	}

	/**
	 * Removes any date limits imposed.
	 *
	 * @return the limited object
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public DateLimitedDataContainer<T> includeAll() {
		for (final T t : this.data) {
			if (t instanceof DateLimitedDataContainer) {
				((DateLimitedDataContainer) t).limitToDateRange(Range.all());
			}
		}
		this.limitedData = Lists.newArrayList();
		this.dateRange = Range.all();
		return this;
	}

	/**
	 * Determines if the data is limited.
	 *
	 * @return whether or not the data is limited
	 */
	public boolean isLimited() {
		return !this.dateRange.equals(Range.all());
	}

	/**
	 * Limits returned data to a specific range. If you need to override this,
	 * don't forget to call super.{@link #limitToDateRange(DateRange)} or it
	 * won't work correctly.
	 *
	 * @param dateRange
	 *            the date range to use
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void limitToDateRange(final Range<Date> dateRange) {

		includeAll();
		this.dateRange = dateRange;

		for (final T t : this.data) {

			if (t.isInDateRange(dateRange)) {

				if (t instanceof DateLimitedDataContainer) {
					((DateLimitedDataContainer) t).limitToDateRange(dateRange);
				}

				this.limitedData.add(t);
			}
		}

	}

	/**
	 * Convenience sub class of {@link DateLimitedDataContainer} that also
	 * provides an implementation of {@link DateLimitedData} for ease of
	 * recursion.
	 *
	 * @author phwhitin
	 *
	 * @param <T>
	 */
	public static class RecursiveDateLimitedDataContainer<T extends DateLimitedData> extends
			DateLimitedDataContainer<T> implements DateLimitedData {

		public RecursiveDateLimitedDataContainer(final List<T> data) {
			super(data);
		}

		/**
		 * Checks to see if this particular piece of data falls within a time
		 * range.
		 */
		@Override
		public boolean isInDateRange(final Range<Date> dateRange) {
			boolean flag = false;

			for (final T t : data) {
				flag |= t.isInDateRange(dateRange);
			}

			return flag;
		}

	}

}
