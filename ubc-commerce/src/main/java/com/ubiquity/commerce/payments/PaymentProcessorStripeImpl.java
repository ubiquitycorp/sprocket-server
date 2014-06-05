package com.ubiquity.commerce.payments;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.configuration.Configuration;

import com.stripe.Stripe;
import com.stripe.exception.APIConnectionException;
import com.stripe.exception.APIException;
import com.stripe.exception.AuthenticationException;
import com.stripe.exception.CardException;
import com.stripe.exception.InvalidRequestException;
import com.stripe.model.Charge;
import com.stripe.model.Customer;
import com.ubiquity.commerce.domain.Money;
import com.ubiquity.commerce.domain.PaymentMethod;



/****
 * 
 * Service encapsulating all payment operations
 * 
 * @author chris
 *
 */
public class PaymentProcessorStripeImpl implements PaymentProcessor {

	/****
	 * 
	 * Parameterized constructor obtains an authorization token with the payment
	 * processor 
	 * 
	 * @param configuration
	 */
	public PaymentProcessorStripeImpl(Configuration configuration) {
		Stripe.apiKey = configuration.getString("stripe.api.secret");
	}


	/***
	 * Saves a payment method on the payment processing servers. The token / mask properties
	 * will be set on the reference passed in
	 * 
	 * @param method
	 * 
	 * @throws RuntimeException if any error occurred
	 * @throws IllegalArgument exception if the payment service returns a 400
	 */
	@Override
	public void savePaymentMethod(PaymentMethod method) {

		Map<String, Object> cardParams = new HashMap<String, Object>();
		cardParams.put("number", method.getAccountNumber());
		cardParams.put("exp_month", method.getExpirationMonth());
		cardParams.put("exp_year", method.getExpirationYear());
		cardParams.put("cvc", method.getSecurityCode());
		cardParams.put("name", method.getName());

		Map<String, Object> customerParams = new HashMap<String, Object>();
		customerParams.put("card", cardParams);


		Customer customer;
		try {
			customer = Customer.create(customerParams);
		} catch (AuthenticationException e) {
			throw new IllegalArgumentException("We could not authenticate your request at this time", e);
		} catch (InvalidRequestException e) {
			throw new IllegalArgumentException("Invalid request to processor API", e);
		} catch (APIConnectionException e) {
			throw new RuntimeException("Networking communication with payment processsor failed", e);
		} catch (CardException e) {
			throw new IllegalArgumentException(e.getMessage());
		} catch (APIException e) {
			throw new RuntimeException("Communication with payment processsor failed", e);
		}
		method.setPaymentMethodToken(customer.getId()); 
		method.setAccountNumberMask(new StringBuilder("************").append(method.getAccountNumber().substring(12, 15)).toString());

	}

	/***
	 * Executes a credit card transaction
	 * 
	 * @param method
	 * @param amount
	 */
	@Override
	public void makePayement(PaymentMethod method, Money amount) {


		Map<String, Object> chargeParams = new HashMap<String, Object>();
		chargeParams.put("amount", amount.getAmount().multiply(new BigDecimal(100.00)).intValue());
		chargeParams.put("customer", method.getPaymentMethodToken());
		chargeParams.put("currency", amount.getCurrencyCodeAsString());

		try {
			Charge.create(chargeParams);
		}  catch (AuthenticationException e) {
			throw new IllegalArgumentException("We could not authenticate your request at this time", e);
		} catch (InvalidRequestException e) {
			throw new IllegalArgumentException("Invalid request to processor API", e);
		} catch (APIConnectionException e) {
			throw new RuntimeException("Networking communication with payment processsor failed", e);
		} catch (CardException e) {
			throw new IllegalArgumentException(e.getMessage());
		} catch (APIException e) {
			throw new RuntimeException("Communication with payment processsor failed", e);
		}


	}
}
