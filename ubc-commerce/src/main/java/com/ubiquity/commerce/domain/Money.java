package com.ubiquity.commerce.domain;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;

/***
 * 
 * Class representing money
 * 
 * @author chris
 *
 */
public class Money {
	
	private Locale locale;
	private BigDecimal amount;
	private String currencyCode;
	private String formattedString;
	
	/***
	 * Default constructor required by JPA
	 */
	protected Money() {}
	
	/***
	 * 
	 * A money object with the default locale of US
	 * 
	 * @param amount
	 */
	public Money(BigDecimal amount) {
		this(Locale.US, amount);
	}
	
	/***
	 * 
	 * A money object with the amount and locale
	 * 
	 * @param locale
	 * @param amount
	 */
	public Money(Locale locale, BigDecimal amount) {
		this.locale = locale;
		this.amount = amount;
		this.currencyCode = Currency.getInstance(this.locale).getCurrencyCode();
		this.formattedString = NumberFormat.getCurrencyInstance(locale).format(amount);
	}

	/***
	 * 
	 * A money object with the amount as a BigDecimal the default locale of US
	 * 
	 * @param amount
	 */
	public Money(Double amount) {
		this(new BigDecimal(amount));
	}

	public Locale getLocale() {
		return locale;
	}

	public BigDecimal getAmount() {
		return amount;
	}
	
	/***
	 * Returns the country code based on the passed in locale
	 * 
	 * @return
	 */
	public String getCurrencyCodeAsString() {
		return currencyCode;
	}
	/***
	 * Returns a formatted string based on the locale
	 * 
	 * @return
	 */
	public String getAmountAsFormattedString() {
		return formattedString;
	}
	

	/**
	 * 
	 * Returns a string value without the symbol and a maximum 2 digit fraction
	 * 
	 * @return
	 */
	public String getAmountAsString() {
		DecimalFormat formatter = (DecimalFormat)NumberFormat.getInstance(locale);
		formatter.setMaximumFractionDigits(2);
		return formatter.format(amount);
	}

}
