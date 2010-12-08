package org.glimpse.server.finance;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.glimpse.client.finance.Quotation;

public class RegexpQuotationFinder implements QuotationFinder {
	private Pattern pattern =
		Pattern.compile(
				"<div class=\"InfB\">\\s*<span class=\"gras\">\\s*([0-9\\s\\.]+)\\s*(\\(c\\))?\\s*(\\w*)\\s*</span>(&nbsp;)*\\s*<span\\s+class=\"gras\">\\s*<span class=\"VAR[a-z]*\">([-\\+]?[0-9\\s\\.]+)%</span>");
	@Override
	public Quotation getQuotation(String text) {
		Matcher matcher = pattern.matcher(text);
		if(matcher.find()) {
			String s = matcher.group(1);
			s = StringUtils.remove(s, " ");
			double value = Double.parseDouble(s);
			String unit = matcher.group(3);
			s = matcher.group(5);
			s = StringUtils.remove(s, " ");
			double variation = Double.parseDouble(s);
			
			return new Quotation(value, unit, variation);
		} else {
			return null;
		}
		
		
	}

}
