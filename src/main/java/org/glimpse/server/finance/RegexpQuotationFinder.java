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
package org.glimpse.server.finance;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.glimpse.client.finance.Quotation;
import org.springframework.stereotype.Component;

@Component
public class RegexpQuotationFinder implements QuotationFinder {

    // <div class="c-faceplate__price c-faceplate__price--inline"><span class="c-instrument c-instrument--last" data-ist-last>3 305.73</span>
    private Pattern valuePattern
            = Pattern.compile(
                    "<div\\s*class=\"c-faceplate__price.*\">\\s*<span\\s*class=\"c-instrument c-instrument--last\".*>([0-9\\s\\.]+)</span>");
    //<span class="c-faceplate__price-currency"> Pts</span></div>
    private Pattern unitPattern
            = Pattern.compile(
                    "class=\"c-faceplate__price-currency\">\\s*(\\w*)</span>");
    
    //<div class="c-faceplate__fluctuation c-faceplate__fluctuation--inline"><span class="c-tradingboard__fluctuation-space u-color-stream-down"><span class="c-instrument c-instrument--variation" data-ist-variation>-1.83%</span>
    private Pattern variationPattern
            = Pattern.compile(
                    "<div\\s*class=\"c-faceplate__fluctuation.*\">.*<span\\s*class=\"c-instrument c-instrument--variation\".*>([-\\+]?[0-9\\s\\.]+)%</span>");

    @Override
    public Quotation getQuotation(String text) {
        Matcher valueMatcher = valuePattern.matcher(text);
        if (valueMatcher.find()) {
            String s = valueMatcher.group(1);
            s = StringUtils.remove(s, " ");
            double value = Double.parseDouble(s);

            Matcher unitMatcher = unitPattern.matcher(text);
            if (unitMatcher.find()) {
                String unit = unitMatcher.group(1);

                Matcher variationMatcher = variationPattern.matcher(text);
                if (variationMatcher.find()) {
                    s = variationMatcher.group(1);
                    s = StringUtils.remove(s, " ");
                    double variation = Double.parseDouble(s);
                    return new Quotation(value, unit, variation);
                }
            }
        }

        return null;
    }

}
