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
package org.glimpse.test.server.news;

import java.io.InputStream;

import org.glimpse.server.news.ServerNewsChannel;
import org.glimpse.server.news.ServerNewsChannelBuilder;
import org.glimpse.server.news.dom.DomServerNewsChannelBuilder;
import org.glimpse.server.news.sax.SaxServerNewsChannelBuilder;

import org.junit.Assert;
import org.junit.Test;

public class NewsTest {
	@Test
	public void testRssDom() throws Exception {
		testReadFile("rss.xml", new DomServerNewsChannelBuilder());
	}
	
    @Test
	public void testRssSax() throws Exception {
		testReadFile("rss.xml", new SaxServerNewsChannelBuilder());
	}
	
    @Test
	public void testAtomDom() throws Exception {
		testReadFile("atom.xml", new DomServerNewsChannelBuilder());
	}

    @Test
	public void testAtomSax() throws Exception {
		testReadFile("atom.xml", new SaxServerNewsChannelBuilder());
	}
	
	private void testReadFile(
			String resourcePath, ServerNewsChannelBuilder builder) throws Exception {
		InputStream is = getClass().getClassLoader().getResourceAsStream(
				resourcePath);
		ServerNewsChannel channel = builder.buildChannel(is);
		Assert.assertNotNull("channel is null", channel);
	}
}
