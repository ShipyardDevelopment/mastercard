package com.mastercard.currency;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class CurrencyController {

	@Autowired
	CurrencyApiService currencyApiService;

	@GetMapping("*")
	public String greeting(@RequestParam(name="sourceCurrency", required=false, defaultValue="ALL") String sourceCurrency,
						   @RequestParam(name="targetCurrency", required=true, defaultValue="DZD") String targetCurrency,
						   @RequestParam(name="amount", required=true, defaultValue="1.0") Double amount,
						   Model model) {
		System.out.print(sourceCurrency);
		CurrencyResult result = currencyApiService.getAudience("2019-01-01", sourceCurrency, targetCurrency, new Double(0.0), amount);
		model.addAttribute("result", result);
		return "currency";
	}
}
