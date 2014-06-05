package com.ubiquity.commerce.payments;

import com.ubiquity.commerce.domain.Money;
import com.ubiquity.commerce.domain.PaymentMethod;

public interface PaymentProcessor {
	
	void savePaymentMethod(PaymentMethod method);
	void makePayement(PaymentMethod method, Money amount);

}
