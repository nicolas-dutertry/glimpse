package org.glimpse.server.finance;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.glimpse.client.finance.Quotation;

public class RegexpQuotationFinder implements QuotationFinder {
	private Pattern valuePattern =
		Pattern.compile(
				"<big class=\"fv-last\">\\s*<span .*>([0-9\\s\\.]+)\\s*(\\(c\\))?\\s*(\\w*)\\s*</span>");
	private Pattern variationPattern =
		Pattern.compile(
				"<big class=\"fv-var\">\\s*<span .*>([-\\+]?[0-9\\s\\.]+)%</span>");
	@Override
	public Quotation getQuotation(String text) {
		Matcher valueMatcher = valuePattern.matcher(text);
		if(valueMatcher.find()) {
			String s = valueMatcher.group(1);
			s = StringUtils.remove(s, " ");
			double value = Double.parseDouble(s);
			String unit = valueMatcher.group(3);
			
			Matcher variationMatcher = variationPattern.matcher(text);
			if(variationMatcher.find()) {
				s = variationMatcher.group(1);
				s = StringUtils.remove(s, " ");
				double variation = Double.parseDouble(s);
				return new Quotation(value, unit, variation);
			}
		}
		
		return null;
	}

}
