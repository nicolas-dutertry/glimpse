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
package org.glimpse.test.server.finance;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;

import org.apache.commons.io.IOUtils;
import org.glimpse.client.finance.Quotation;
import org.glimpse.server.finance.QuotationFinder;
import org.glimpse.server.finance.RegexpQuotationFinder;
import org.junit.Assert;
import org.junit.Test;

public class QuotationTest {
    @Test
	public void testCac() throws Exception {
		
		QuotationFinder finder = new RegexpQuotationFinder();
		
		InputStream is = getClass().getResourceAsStream("/cac.txt");
		InputStreamReader reader = new InputStreamReader(is);
		StringWriter sw = new StringWriter(); 
		IOUtils.copy(reader, sw);
		reader.close();
		
		Quotation quotation = finder.getQuotation(sw.toString());
		Assert.assertNotNull(quotation);
        Assert.assertEquals(5184.43, quotation.getValue(), 0);
        Assert.assertEquals("Pts", quotation.getUnit());
        Assert.assertEquals(-1.06, quotation.getVariation(), 0);
	}
	
    @Test
    public void testEuro() throws Exception {
		QuotationFinder finder = new RegexpQuotationFinder();
		
		InputStream is = getClass().getResourceAsStream("/euro.txt");
		InputStreamReader reader = new InputStreamReader(is);
		StringWriter sw = new StringWriter(); 
		IOUtils.copy(reader, sw);
		reader.close();
		
		Quotation quotation = finder.getQuotation(sw.toString());
		Assert.assertNotNull(quotation);
        Assert.assertEquals(3364.92, quotation.getValue(), 0);
        Assert.assertEquals("Pts", quotation.getUnit());
        Assert.assertEquals(-1.06, quotation.getVariation(), 0);
	}
}
