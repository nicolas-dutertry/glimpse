package org.glimpse.server.finance;

import org.glimpse.client.finance.Quotation;

public interface QuotationFinder {
	Quotation getQuotation(String s);
}
