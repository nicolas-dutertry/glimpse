/*
 * Copyright (C) 2009 Nicolas Dutertry
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
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
	
	@DefaultMessage("Powered by {0}")
	String poweredBy(String s);
}
