package com.mastercard.currency;

import com.mastercard.api.core.ApiConfig;
import com.mastercard.api.core.exception.ApiException;
import com.mastercard.api.core.model.Environment;
import com.mastercard.api.core.model.RequestMap;
import com.mastercard.api.core.security.oauth.OAuthAuthentication;
import com.mastercard.api.currencyconversion.ConversionRate;
import com.mastercard.api.currencyconversion.Currencies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class CurrencyApiService {
	private static final String P12_FILE = ---P12_FILE_NAME---;// You should put your P12 file under src/main/resource

	public CurrencyApiService(@Autowired ResourceLoader resourceLoader) throws IOException {
		String consumerKey = ---CONSUMER_KEY---;   // You should copy this from "My Keys" on your project page e.g. UTfbhDCSeNYvJpLL5l028sWL9it739PYh6LU5lZja15xcRpY!fd209e6c579dc9d7be52da93d35ae6b6c167c174690b72fa
		String keyAlias = ---KEY_ALIAS---;   // For production: change this to the key alias you chose when you created your production key
		String keyPassword = ---KEY_STORE_PASSWORD---;   // For production: change this to the key alias you chose when you created your production key
		ClassLoader classLoader = CurrencyApiService.class.getClassLoader();
		InputStream is = resourceLoader.getResource("classpath:" + P12_FILE).getInputStream();

		ApiConfig.setAuthentication(new OAuthAuthentication(consumerKey, is, keyAlias, keyPassword));   // You only need to set this once
		ApiConfig.setDebug(true);   // Enable http wire logging
		// This is needed to change the environment to run the sample code. For production: use ApiConfig.setSandbox(false);
		ApiConfig.setEnvironment(Environment.parse("sandbox_mtf"));
	}

	public CurrencyResult getAudience(final String fxDate, final String sourceCurrency, final String targetCurrency, final Double bankFee, final Double amount) {

		try {
			List<Currency> currencies = getCurrencyList();

			RequestMap map = new RequestMap();
			map.set("fxDate", fxDate);
			map.set("transCurr", sourceCurrency);
			map.set("crdhldBillCurr", targetCurrency);
			map.set("bankFee", String.valueOf(bankFee));
			map.set("transAmt", String.valueOf(amount));

			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			ConversionRate response = ConversionRate.query(map);
			CurrencyResult result = new CurrencyResult.CurrencyResultBuilder().
					bankFee(new BigDecimal((Double)response.get("data.bankFee")))
					.fxDate(LocalDate.parse((String) response.get("data.fxDate"), formatter))
					.sourceCurrency((String) response.get("data.transCurr"))
					.targetCurrency((String) response.get("data.crdhldBillCurr"))
					.transferAmount(new BigDecimal((Double)response.get("data.transAmt")).setScale(2, RoundingMode.CEILING))
					.bankFee(new BigDecimal((Double)response.get("data.transAmt")).setScale(2, RoundingMode.CEILING))
					.targetCurrencyAmount(new BigDecimal((Double)response.get("data.crdhldBillAmt")).setScale(2, RoundingMode.CEILING))
					.conversionRate(new BigDecimal((Double)response.get("data.conversionRate")).setScale(2, RoundingMode.CEILING))
					.currencyList(currencies)
					.build();
			System.out.println(result);
			result.getBankFee().setScale(2, RoundingMode.CEILING);
			result.getConversionRate().setScale(2, RoundingMode.CEILING);
			result.getTargetCurrencyAmount().setScale(2, RoundingMode.CEILING);
			result.getTransferAmount().setScale(2, RoundingMode.CEILING);

			return result;

		} catch (ApiException e) {
			return null;
		}
	}

	public List<Currency> getCurrencyList() throws ApiException {
		RequestMap map = new RequestMap();

		Currencies response = Currencies.query(map);
		List<Currency> currencies = new ArrayList<>();
		System.out.println("This sample shows looping through data.currencies");
		for(Map<String,Object> item : (List<Map<String, Object>>) response.get("data.currencies")) {
			currencies.add(new Currency((String)item.get("alphaCd"), (String)item.get("currNam")));
		}
		return currencies;
	}
}
