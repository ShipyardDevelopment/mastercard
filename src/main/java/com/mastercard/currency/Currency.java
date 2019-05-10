package com.mastercard.currency;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Currency {
	String code;
	String name;
}
