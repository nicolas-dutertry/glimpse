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
package org.glimpse.server.news.sax;

import java.io.InputStream;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.glimpse.server.news.ServerNewsChannel;
import org.glimpse.server.news.ServerNewsChannelBuilder;

public class SaxServerNewsChannelBuilder implements ServerNewsChannelBuilder {

	public ServerNewsChannel buildChannel(InputStream is) throws Exception {
		SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
		ChannelHandler handler = new ChannelHandler();
		parser.parse(is, handler);
		
		return handler.getChannel();
	}

}
