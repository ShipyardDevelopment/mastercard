package com.mastercard.currency;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Builder
@ToString
@Data
public class CurrencyResult {

	LocalDate fxDate;
	BigDecimal targetCurrencyAmount;
	BigDecimal transferAmount;
	BigDecimal bankFee;
	BigDecimal conversionRate;
	String sourceCurrency;
	String targetCurrency;
	List<Currency> currencyList;
}
