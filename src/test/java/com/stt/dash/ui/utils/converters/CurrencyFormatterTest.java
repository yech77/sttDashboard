/**
 *
 */
package com.stt.dash.ui.utils.converters;

import static org.junit.Assert.assertEquals;

import com.stt.dash.ui.utils.FormattingUtils;
import org.junit.Test;

import com.stt.dash.test.FormattingTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class CurrencyFormatterTest extends FormattingTest {

	@Test
	@DisplayName("formattingShoudBeLocaleIndependent")
	public void formattingShoudBeLocaleIndependent() {
		CurrencyFormatter formatter = new CurrencyFormatter();
		String result = formatter.encode(123456);
		assertEquals("$1,234.56", result);
	}

	@Test
	@DisplayName("Formatera numeros enteros con .")
	public void formatNumber() {
		NumberFormat nf = NumberFormat.getNumberInstance(new Locale("es", "VE"));
		DecimalFormat formatter = (DecimalFormat) nf;
		formatter.applyPattern("###,###,###");
		Assertions.assertEquals("12.000.000", formatter.format(12000000));
		nf = NumberFormat.getNumberInstance(Locale.getDefault());
		formatter = (DecimalFormat) nf;
		formatter.applyPattern("###,###");
		Assertions.assertEquals("12,000,000,000", formatter.format(12000000000l));
	}
}
