package com.stt.dash.test;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Locale.Category;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

/**
 * Test superclass that runs tests in Turkish locale.
 */
public abstract class FormattingTest {

	private static Locale locale;

	@BeforeClass
	public static void setUpClass() {
		locale = Locale.getDefault(Category.FORMAT);
		Locale.setDefault(Category.FORMAT, new Locale("tr", "TR"));
	}

	@AfterClass
	public static void tearDownClass() {
		Locale.setDefault(Category.FORMAT, locale);
		locale = null;
	}

}