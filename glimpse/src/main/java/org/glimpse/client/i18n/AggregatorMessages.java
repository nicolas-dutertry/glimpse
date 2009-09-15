package org.glimpse.client.i18n;

import com.google.gwt.i18n.client.Messages;

public interface AggregatorMessages extends Messages {
	@DefaultMessage("{0} minutes ago")
	String someMinutesAgo(long l);
	
	@DefaultMessage("{0} hours ago")
	String someHoursAgo(long l);
	
	@DefaultMessage("{0} days ago")
	String someDaysAgo(long l);
	
	@DefaultMessage("{0} months ago")
	String someMonthsAgo(long l);
	
	@DefaultMessage("{0} years ago")
	String someYearsAgo(long l);
}
