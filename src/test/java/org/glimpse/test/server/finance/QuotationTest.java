package org.glimpse.test.server.finance;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;


import junit.framework.TestCase;

import org.apache.commons.io.IOUtils;
import org.glimpse.client.finance.Quotation;
import org.glimpse.server.finance.QuotationFinder;
import org.glimpse.server.finance.RegexpQuotationFinder;

public class QuotationTest extends TestCase {
	public void testCac() throws Exception {
		
		QuotationFinder finder = new RegexpQuotationFinder();
		
		InputStream is = getClass().getResourceAsStream("/cac.txt");
		InputStreamReader reader = new InputStreamReader(is);
		StringWriter sw = new StringWriter(); 
		IOUtils.copy(reader, sw);
		reader.close();
		
		Quotation quotation = finder.getQuotation(sw.toString());
		assertNotNull(quotation);
        assertEquals(5184.43, quotation.getValue());
        assertEquals("Pts", quotation.getUnit());
        assertEquals(-1.06, quotation.getVariation());
	}
	
    public void testEuro() throws Exception {
		QuotationFinder finder = new RegexpQuotationFinder();
		
		InputStream is = getClass().getResourceAsStream("/euro.txt");
		InputStreamReader reader = new InputStreamReader(is);
		StringWriter sw = new StringWriter(); 
		IOUtils.copy(reader, sw);
		reader.close();
		
		Quotation quotation = finder.getQuotation(sw.toString());
		assertNotNull(quotation);
        assertEquals(3364.92, quotation.getValue());
        assertEquals("Pts", quotation.getUnit());
        assertEquals(-1.06, quotation.getVariation());
	}
}
