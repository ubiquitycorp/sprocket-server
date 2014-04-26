package com.ubiquity.giftsender.api.endpoints;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.niobium.common.serialize.JsonConverter;
import com.niobium.repository.CollectionVariant;
import com.ubiquity.commerce.domain.Item;
import com.ubiquity.commerce.domain.Order;
import com.ubiquity.commerce.domain.PaymentMethod;
import com.ubiquity.commerce.domain.PaymentMethodType;
import com.ubiquity.commerce.service.CommerceService;
import com.ubiquity.giftsender.api.dto.DtoAssembler;
import com.ubiquity.giftsender.api.dto.containers.ContactsDto;
import com.ubiquity.giftsender.api.dto.containers.EventsDto;
import com.ubiquity.giftsender.api.dto.containers.OrdersDto;
import com.ubiquity.giftsender.api.dto.model.ContactDto;
import com.ubiquity.giftsender.api.dto.model.EventDto;
import com.ubiquity.giftsender.api.dto.model.ItemDto;
import com.ubiquity.giftsender.api.dto.model.OrderDto;
import com.ubiquity.giftsender.api.dto.model.PaymentDto;
import com.ubiquity.giftsender.api.dto.model.RedeemResult;
import com.ubiquity.giftsender.api.exception.HttpException;
import com.ubiquity.giftsender.api.interceptors.Secure;
import com.ubiquity.giftsender.giftango.api.GiftangoAPI;
import com.ubiquity.giftsender.service.ServiceFactory;
import com.ubiquity.identity.domain.Identity;
import com.ubiquity.identity.domain.User;
import com.ubiquity.identity.service.UserService;
import com.ubiquity.social.api.Social;
import com.ubiquity.social.api.SocialFactory;
import com.ubiquity.social.domain.Contact;
import com.ubiquity.social.domain.Event;
import com.ubiquity.social.domain.SocialIdentity;
import com.ubiquity.social.domain.SocialProviderType;
import com.ubiquity.social.service.ContactService;
import com.ubiquity.social.service.EventService;

@Path("/1.0/mobile/users")
public class UserEndpoint {

	private Logger log = LoggerFactory.getLogger(getClass());

	private JsonConverter jsonConverter = JsonConverter.getInstance();

	@POST
	@Path("/{userId}/paymentMethods")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Secure
	public Response savePaymentMethod(@PathParam("userId") Long userId,
			InputStream body) throws Exception {

		// convert payload
		PaymentDto request = jsonConverter.convertFromPayload(body,
				PaymentDto.class);

		log.debug("savePaymentMethod request payload {}",
				jsonConverter.convertToPayload(request));

		// get the service from the context
		User user = ServiceFactory.getUserService().getUserById(userId);

		// convert to a domain object
		PaymentMethod paymentMethod = new PaymentMethod.Builder()
				.accountNumber(request.getAccountNumber())
				.expirationMonth(request.getExpirationMonth())
				.expirationYear(request.getExpirationYear())
				.name(request.getName())
				.securityCode(request.getSecurityCode())
				.user(user)
				.build();

		// Save payment via gateway and then to db;
		ServiceFactory.getCommerceService().savePaymentMethod(paymentMethod);

		PaymentDto result = new PaymentDto.Builder()
				.paymentMethodId(paymentMethod.getIdentityId())
				.accountNumberMask(paymentMethod.getAccountNumber())
				.build();
		
		return Response.ok().entity(jsonConverter.convertToPayload(result))
				.build();
	}

	@GET
	@Path("/ping")
	@Produces(MediaType.APPLICATION_JSON)
	public Response ping() {
		return Response.ok().entity("{\"message\":\"peter\"}").build();
	}

	/***
	 * Endpoint for purchasing an order. If credit card info is passed in, this
	 * information becomes the default payment method. If no payment information
	 * is passed-in, the default payment method is used. If no payment method is
	 * set, an error will be thrown.
	 * 
	 * @param userId
	 * @param apiKey
	 * @return
	 */
	@POST
	@Path("/{userId}/orders")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Secure
	public Response createOrder(@PathParam("userId") Long userId,
			InputStream body) {

		// convert payload
		OrderDto request = jsonConverter.convertFromPayload(body,
				OrderDto.class);
		log.debug("purchased items request payload {}",
				jsonConverter.convertToPayload(request));

		// get the service from the context
		User user = ServiceFactory.getUserService().getUserById(userId);
		if (user == null)
			throw new IllegalArgumentException("User does not exist");

		// Now get the item
		Item item = ServiceFactory.getItemService().getByItemId(
				request.getItemId());
		if (item == null)
			throw new IllegalArgumentException("Item does not exist");

		// Now get the item
		Contact contact = ServiceFactory.getContactService().getByContactId(
				request.getContactId());
		if (contact == null)
			throw new IllegalArgumentException("Contact does not exist");

		PaymentMethod method = null;
		PaymentDto paymentDto = request.getPayment();
		if (paymentDto != null) {
			// Create mask from input number
			String accountNumberMask = new StringBuilder()
					.append("****-****-****-")
					.append(paymentDto.getAccountNumber().substring(12))
					.toString();
			// convert to a domain object
			method = new PaymentMethod.Builder()
					.accountNumber(paymentDto.getAccountNumber())
					.accountNumberMask(accountNumberMask)
					.expirationMonth(paymentDto.getExpirationMonth())
					.expirationYear(paymentDto.getExpirationYear())
					.name(paymentDto.getName())
					.securityCode(paymentDto.getSecurityCode()).user(user)
					.createdDate(System.currentTimeMillis())
					.paymentMethodType(PaymentMethodType.CreditCard) // we be
																		// changed
																		// later
																		// when
																		// we
																		// add
																		// new
																		// payment
																		// methods.
					.isDefault(true).build();
		}

		// Make the purchase
		CommerceService commerceService = ServiceFactory.getCommerceService();
		Order order = commerceService.purchaseOrder(item, contact.getSocialIdentity(),
				request.getDenomination(), user, method, request.getMessage());

		PaymentDto payment = new PaymentDto.Builder().accountNumberMask(
				method.getAccountNumberMask()).build();

		OrderDto orderDto = new OrderDto.Builder().orderId(order.getOrderId())
				.created(System.currentTimeMillis()).payment(payment).build();

		// sends message to friend via LinkedIn
		SocialIdentity contactIdentity = contact.getSocialIdentity();
		// check if contact is LinkedIn contact because that's all we support (move to queue)
		if (contactIdentity.getSocialProviderType().equals(SocialProviderType.LinkedIn)) { 

			// Get a linked in identity from the buyer if we have one
			for(Identity sender : user.getIdentities()) {
				if(sender instanceof SocialIdentity) {
					SocialIdentity senderIdentity = (SocialIdentity)sender;
					if(senderIdentity.getSocialProviderType() == SocialProviderType.LinkedIn) {
						SocialFactory.createProvider(SocialProviderType.LinkedIn, user.getClientPlatform())
						.postToWall(senderIdentity, contactIdentity, order.getMessage());
					}
				}
			}
		}

		return Response.ok().entity(jsonConverter.convertToPayload(orderDto))
				.build();
	}

	/***
	 * 
	 * @param userId
	 * @return
	 */
	@GET
	@Path("/{userId}/orders")
	@Produces(MediaType.APPLICATION_JSON)
	@Secure
	public Response orders(@PathParam("userId") Long userId,
			@HeaderParam("If-Modified-Since") Long ifModifiedSince) {

		OrdersDto result = new OrdersDto();
		log.debug("Listing sent orders modified since: {}", ifModifiedSince);

		// Manager will return contacts if they have been modified, else it will
		// be empty
		CollectionVariant<Order> variant = ServiceFactory.getCommerceService()
				.findSentOrders(userId, ifModifiedSince);

		// Throw a 304 if there is no variant (no change)
		if (variant == null) {
			return Response.notModified().build();
		}
		// Assemble the item from the db...we should have code for this
		// already...
		HashMap<String, Object> denominations = new HashMap<String, Object>();
		ItemDto itemDto;
		ContactDto contactDto;
		Identity recipient;
		for (Order order : variant.collection) {
			itemDto = new ItemDto.Builder().itemId(order.getItem().getItemId())
					.etag(UUID.randomUUID().toString()).build();

			recipient = order.getPurchasedFor();
			// Now get the contact info by the identity
			Contact recipientContact = ServiceFactory.getContactService().getBySocialIdentityId(recipient.getIdentityId());
	
			String image = (recipientContact.getImage() != null) ? recipientContact.getImage().getUrl() : "";
			contactDto = new ContactDto.Builder()
					.displayName(recipientContact.getDisplayName())
					.imageUrl(image)
					.etag(UUID.randomUUID().toString())
					.build();

			denominations.clear();
			denominations.put("denominations", order.getDenomination());

			OrderDto orderDto = new OrderDto.Builder()
					.total(order.getDenomination()).item(itemDto)
					.selectedOptions(denominations).recipient(contactDto)
					.orderId(order.getOrderId()).created(order.getCreatedAt())
					.build();
			result.getOrders().add(orderDto);
		}

		return Response.ok(jsonConverter.convertToPayload(result))
				.header("Last-Modified", variant.getLastModified()).build();

	}

	/***
	 * 
	 * @param userId
	 * @return
	 */
	@GET
	@Path("/{userId}/orders/gifted")
	@Produces(MediaType.APPLICATION_JSON)
	@Secure
	public Response giftedOrders(@PathParam("userId") Long userId,
			@HeaderParam("If-Modified-Since") Long ifModifiedSince) {

		OrdersDto result = new OrdersDto();
		log.debug("Listing sent orders modified since: {}", ifModifiedSince);

		// Manager will return contacts if they have been modified, else it will
		// be empty
		CollectionVariant<Order> variant = ServiceFactory.getCommerceService()
				.findReceivedOrders(userId, ifModifiedSince);

		// Throw a 304 if there is no variant (no change)
		if (variant == null) {
			return Response.notModified().build();
		}
		// Assemble the item from the db...we should have code for this
		// already...
		HashMap<String, Object> denominations = new HashMap<String, Object>();
		ItemDto itemDto;
		ContactDto contactDto;
		User owner;
		for (Order order : variant.collection) {
			itemDto = new ItemDto.Builder().itemId(order.getItem().getItemId())
					.etag(UUID.randomUUID().toString()).build();
			owner = order.getUser();

			String image = (owner.getImage() != null) ? owner.getImage()
					.getUrl() : "";

			contactDto = new ContactDto.Builder()
					.displayName(owner.getDisplayName()).imageUrl(image)
					.etag(UUID.randomUUID().toString()).build();

			denominations.clear();
			denominations.put("denominations", order.getDenomination());

			OrderDto orderDto = new OrderDto.Builder()
					.total(order.getDenomination())
					.item(itemDto)
					.selectedOptions(denominations)
					.recipient(contactDto)
					// Need to send sender info not received info
					.orderId(order.getOrderId()).created(order.getCreatedAt())
					.isRedeemed(order.getIsRedeemed())
					.barcodeImageUrl(order.getCertBarcodeUrl())
					.pinId(order.getGiftangoPinId())
					.partnerCertId(order.getPartnerCertId())
					.message(order.getMessage()).build();
			result.getOrders().add(orderDto);
		}

		return Response.ok(jsonConverter.convertToPayload(result))
				.header("Last-Modified", variant.getLastModified()).build();

	}

	/***
	 * 
	 * @param userId
	 *            , orderId
	 * @return
	 */
	@POST
	@Path("/{userId}/orders/{orderId}/redeemed")
	@Produces(MediaType.APPLICATION_JSON)
	@Secure
	public Response redeem(@PathParam("userId") Long userId,
			@PathParam("orderId") Long orderId) {
		CommerceService orderService = ServiceFactory.getCommerceService();
		Order order = orderService.findByOrderId(orderId);
		if (order == null) // need to check if this order is associated with
							// this user or not
			throw new IllegalArgumentException("Order does not exist");

		if (order.getIsRedeemed())
			throw new HttpException("This order is already redeemed", 400);

		Contact recipient = ServiceFactory.getContactService().getBySocialIdentityId(order.getPurchasedFor().getIdentityId());
		GiftangoAPI service = new GiftangoAPI();
		Order newOrder = service.submitOrder(order, recipient);
		if (newOrder == null)
			throw new HttpException(
					"Sorry, there is an issue with Giftango service, try again later...",
					503);

		// update this order as redeemed
		newOrder.setIsRedeemed(true);
		orderService.update(newOrder);

		RedeemResult redeem = new RedeemResult();
		redeem.setOrderId(newOrder.getOrderId());
		redeem.setBarcodeImageUrl(newOrder.getCertBarcodeUrl());
		redeem.setPartnerCertId(newOrder.getPartnerCertId());
		redeem.setPinId(newOrder.getGiftangoPinId());

		// dummy data
		/*
		 * redeem.setBarcodeImageUrl(
		 * "https://www.vcdelivery.com/Cert/barcodeimage.ashx?Key=d%2bJedWR0ZjvBamO5fvEz%2bt9RQL9hy3UVQOcSgVe3ZfjvxPdaDpzlqmoTgOHA4uTY2aXJ9qxExlYepmKhQXZCNCc%2bf%2bkyWcY%2bg5Xj80i5pUG6013kX4n7bDGAEibcL3qR"
		 * ); redeem.setPartnerCertId("25-TEST-0009204");
		 * redeem.setPinId("09204");
		 */
		return Response
				.ok(JsonConverter.getInstance().convertToPayload(redeem))
				.build();
	}

	@GET
	@Path("/{userId}/contacts/{contactId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getContactDetails(@HeaderParam("ETag") String etag,
			@PathParam("userId") Long userId,
			@PathParam("contactId") Long contactId) {

		Contact contact = ServiceFactory.getContactService().getByContactId(
				contactId);
		if (contact == null)
			throw new HttpException("Contact does not exist", 400);
		else if (!contact.getOwner().getUserId().equals(userId))
			throw new HttpException("You don't own this contact", 400);

		ContactDto contactDto = DtoAssembler.assembleContactDto(contact);
		return Response.ok(jsonConverter.convertToPayload(contactDto)).build();
	}

	@GET
	@Path("/{userId}/events")
	@Produces(MediaType.APPLICATION_JSON)
	@Secure
	public Response events(@PathParam("userId") Long userId,
			@HeaderParam("If-Modified-Since") Long ifModifiedSince) {

		EventService eventService = ServiceFactory.getEventService();

		UserService userManager = ServiceFactory.getUserService(); // TODO: move
																	// this into
																	// an
																	// interceptor
		User user = userManager.getUserById(userId);
		if (user == null)
			throw new IllegalArgumentException("User does not exist");

		// Temporary cluge to load events if they don't exist already
		if (!eventService.hasEvents(userId)) {
			log.debug("User does not have events. Refreshing....");
			eventService.refreshEventsForUser(user);
		}

		// Manager will return events if they have been modified, else it will
		// be empty; it will return null if there is no change
		CollectionVariant<Event> variant = eventService.findEvents(userId,
				ifModifiedSince);

		// Throw a 304 if there is no variant (no change)
		if (variant == null) {
			return Response.notModified().build();
		}

		EventsDto result = new EventsDto();
		for (Event event : variant.getCollection()) {
			Contact owner = event.getContact();
			ContactDto ownerElement = null;
			if (owner != null) {
				ownerElement = new ContactDto.Builder()
						.contactId(owner.getContactId())
						.etag(UUID.randomUUID().toString()).build();
				EventDto eventElement = new EventDto.Builder()
						.eventId(event.getEventId())
						.name(event.getName())
						.startDate(event.getStartDate())
						.endDate(event.getEndDate())
						.imageUrl(
								"https://api.giftango.com/imageservice/Images/300x190/CIR_000107_06.png")
						.owner(ownerElement).build();
				result.getEvents().add(eventElement);
			}
		}

		return Response.ok().header("Last-Modified", variant.getLastModified())
				.entity(jsonConverter.convertToPayload(result)).build();
	}

	@GET
	@Path("/{userId}/contacts")
	@Produces(MediaType.APPLICATION_JSON)
	@Secure
	public Response contacts(@PathParam("userId") Long userId,
			@HeaderParam("ApiKey") String apiKey,
			@HeaderParam("If-Modified-Since") Long ifModifiedSince) {
		log.debug("Listing contacts modified since: {}", ifModifiedSince);

		ContactService contactService = ServiceFactory.getContactService();
		UserService userManager = ServiceFactory.getUserService();
		User user = userManager.getUserById(userId);
		if (user == null)
			throw new IllegalArgumentException("User does not exist");

		for (Identity identity : user.getIdentities()) {
			boolean isInstance = identity instanceof SocialIdentity;
			if(!isInstance)
				continue;
			
			SocialIdentity socialIdentity = (SocialIdentity)identity;
			// check remotely via social provider API and save to contact db via
			// contact manager
			if (!contactService.hasContactsForProvider(userId,
					socialIdentity.getSocialProviderType())) {
				// insert contacts to database. I will create a separate handler
				// for this
				// get user contacts from provider
				Social provider = SocialFactory.createProvider(
						socialIdentity.getSocialProviderType(),
						user.getClientPlatform());
				
				List<Contact> list = provider.findContactsByOwnerIdentity(socialIdentity);
				// save contacts to database
				for (Contact contact : list) {
					contactService.create(contact);
				}
			}
		}

		// Manager will return contacts if they have been modified, else it will
		// be empty
		CollectionVariant<Contact> variant = ServiceFactory.getContactService()
				.findAllContactsByOwnerId(userId, ifModifiedSince);

		// Throw a 304 if there is no variant (no change)
		if (variant == null) {
			return Response.notModified().build();
		}
		// Convert entire list to DTO
		ContactsDto result = new ContactsDto();
		for (Contact contact : variant.getCollection()) {
			ContactDto contactDto = DtoAssembler.assembleContactDto(contact);
			result.getContacts().add(contactDto);
		}

		return Response.ok().header("Last-Modified", variant.getLastModified())
				.entity(jsonConverter.convertToPayload(result)).build();
	}

}
