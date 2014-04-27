package com.ubiquity.commerce.service;

import java.util.List;

import org.apache.commons.configuration.Configuration;

import com.niobium.repository.CollectionVariant;
import com.niobium.repository.cache.DataCacheKeys;
import com.niobium.repository.cache.UserDataModificationCache;
import com.niobium.repository.cache.UserDataModificationCacheRedisImpl;
import com.niobium.repository.jpa.EntityManagerSupport;
import com.ubiquity.commerce.domain.Item;
import com.ubiquity.commerce.domain.Money;
import com.ubiquity.commerce.domain.Order;
import com.ubiquity.commerce.domain.PaymentMethod;
import com.ubiquity.commerce.payments.PaymentProcessor;
import com.ubiquity.commerce.payments.PaymentProcessorStripeImpl;
import com.ubiquity.commerce.repository.OrderRepository;
import com.ubiquity.commerce.repository.OrderRepositoryJpaImpl;
import com.ubiquity.commerce.repository.PaymentMethodRepository;
import com.ubiquity.commerce.repository.PaymentMethodRepositoryJpaImpl;
import com.ubiquity.commerce.repository.cache.CommerceCacheKeys;
import com.ubiquity.commerce.repository.cache.CommerceCacheKeys.UserProperties;
import com.ubiquity.identity.domain.Identity;
import com.ubiquity.identity.domain.User;
import com.ubiquity.identity.repository.UserRepository;
import com.ubiquity.identity.repository.UserRepositoryJpaImpl;

/***
 * 
 * @author peter.tadros
 * 
 */
public class CommerceService {

	private UserDataModificationCache dataModificationCache;
	private OrderRepository orderRepository;
	private UserRepository userRepository;
	private PaymentProcessor paymentProcessor;
	private PaymentMethodRepository paymentMethodRepository;
	
	/***
	 * Parameterized constructor builds a manager with required configuration
	 * property
	 * 
	 * @param configuration
	 */
	public CommerceService(Configuration configuration) {
		dataModificationCache = new UserDataModificationCacheRedisImpl(configuration.getInt(DataCacheKeys.Databases.ENDPOINT_MODIFICATION_DATABASE_USER));
		orderRepository = new OrderRepositoryJpaImpl();
		paymentProcessor = new PaymentProcessorStripeImpl(configuration);
		userRepository = new UserRepositoryJpaImpl();
		paymentMethodRepository = new PaymentMethodRepositoryJpaImpl();
	}

	/***
	 * Returns the full list of sent orders for the given user
	 * 
	 * @param userId
	 * @param ifModifiedBy
	 * @return
	 */
	public CollectionVariant<Order> findSentOrders(Long ownerId,
			Long ifModifiedBy) {

		Long lastModified = dataModificationCache.getLastModified(ownerId,
				CommerceCacheKeys.UserProperties.SENT_ITEMS, ifModifiedBy);

		// If there is no cache entry, there are no items
		if (lastModified == null)
			return null;

		try {
			List<Order> orders = orderRepository
					.findSentOrdersByOwnerId(ownerId);
			return new CollectionVariant<Order>(orders, lastModified);
		} finally {
			EntityManagerSupport.closeEntityManager();
		}

	}

	/***
	 * Returns the full list of received orders for the given user
	 * 
	 * @param userId
	 * @param ifModifiedBy
	 * @return
	 */
	public CollectionVariant<Order> findReceivedOrders(Long userId,
			Long ifModifiedBy) {

		Long lastModified = dataModificationCache.getLastModified(userId,
				CommerceCacheKeys.UserProperties.RECEIVED_ITEMS, ifModifiedBy);

		// If there is no cache entry, there are no received order
		if (lastModified == null)
			return null;
		try {
			List<Order> orders = orderRepository
					.findReceivedOrdersByUserId(userId);

			return new CollectionVariant<Order>(orders, lastModified);
		} finally {
			EntityManagerSupport.closeEntityManager();
		}
	}

	/**
	 * Saves a payment method and sets it as a default
	 * 
	 * @param method
	 */
	public void savePaymentMethod(PaymentMethod method) {
		// Set all previous payment methods as secondary
		User user = method.getUser();
		for(Identity identity : user.getIdentities()) {
			if(identity instanceof PaymentMethod) {
				PaymentMethod savedMethod = (PaymentMethod)identity;
				savedMethod.setIsDefault(false);
			}
		}

		// Go to external processor
		paymentProcessor.savePaymentMethod(method);
		
		// Once we're sure we have a response, commit changes in one go
		EntityManagerSupport.beginTransaction();
		userRepository.update(user);
		paymentMethodRepository.create(method);
		EntityManagerSupport.commit();
	}

	public void update(Order order) {
		try {
			EntityManagerSupport.beginTransaction();
			orderRepository.update(order);
			EntityManagerSupport.commit();
		} finally {
			EntityManagerSupport.closeEntityManager();
		}
	}

	public Order findByOrderId(Long orderId) {
		try {
			return orderRepository.read(orderId);
		} finally {
			EntityManagerSupport.closeEntityManager();
		}
	}

	/***
	 * Gets the default payment method from a user if the payment method is not
	 * set.
	 * 
	 * @param item
	 *            , contact
	 * @param selectedOptions
	 * @throws IllegalArgumentException
	 *             if the the paymentMethod is null and the user has no
	 *             configured payment method
	 */
	public Order purchaseOrder(Item item, Identity purchasedFor, Double price,
			User buyer, PaymentMethod paymentMethod, String message) {

		try {
			if (paymentMethod != null) {

				try {	
					// Set all previous payment methods as secondary
					for(Identity identity : buyer.getIdentities()) {
						if(identity instanceof PaymentMethod) {
							PaymentMethod method = (PaymentMethod)identity;
							method.setIsDefault(false);
						}
					}

					// Go to external processor
					paymentProcessor.savePaymentMethod(paymentMethod);
					// Once we're sure we have a response, commit changes in one go
					EntityManagerSupport.beginTransaction();
					userRepository.update(buyer);
					paymentMethodRepository.create(paymentMethod);
					EntityManagerSupport.commit();

				} finally {
					EntityManagerSupport.closeEntityManager();
				}


			} else {
				paymentMethod = getDefaultpaymentMethod(buyer);
			}

			if (paymentMethod == null)
				throw new IllegalArgumentException(
						"User has no payment method set");

			// charge it
			paymentProcessor.makePayement(paymentMethod, new Money(price));

			Order order = new Order.Builder().item(item).purchasedFor(purchasedFor)
					.message(message).isRedeemed(Boolean.FALSE)
					.createdAt(System.currentTimeMillis()).denomination(price)
					.user(buyer).paymentMethod(paymentMethod).build();

			try {
				EntityManagerSupport.beginTransaction();
				orderRepository.create(order);
				EntityManagerSupport.commit();
			} finally {
				EntityManagerSupport.closeEntityManager();
			}

			// Update sent orders modified cache
			dataModificationCache.put(order.getUser().getUserId(),
					CommerceCacheKeys.UserProperties.SENT_ITEMS,
					System.currentTimeMillis());

			
			User purchasedForUser = userRepository.getByIdentityId(purchasedFor.getIdentityId());
			if (purchasedForUser != null) { // recipient is already a user in the system
				dataModificationCache.put(purchasedForUser.getUserId(),
						UserProperties.RECEIVED_ITEMS,
						System.currentTimeMillis());
			}
			return order;

		} finally {
			EntityManagerSupport.closeEntityManager();
		}
	}

	private PaymentMethod getDefaultpaymentMethod(User user) {
		PaymentMethod paymentMethod = null;
		for(Identity identity : user.getIdentities()) {
			PaymentMethod method = (PaymentMethod)identity;
			if (method.getIsDefault()) {
				paymentMethod = method;
				break;
			}
		}
		return paymentMethod;
	}

	/***
	 * This method touches the local cache with received order of given user
	 * This method should be called in the first login of a user.
	 * 
	 * @param userId
	 */
	public void activateReceivedOrders(Long userId) {
		List<Order> orders = orderRepository.findReceivedOrdersByUserId(userId);
		if(orders != null)
			dataModificationCache.put(userId, UserProperties.RECEIVED_ITEMS, System.currentTimeMillis());
	}
}
