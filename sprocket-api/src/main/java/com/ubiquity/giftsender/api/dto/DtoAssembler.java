package com.ubiquity.giftsender.api.dto;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import com.ubiquity.commerce.domain.Item;
import com.ubiquity.commerce.domain.ItemDoubleOption;
import com.ubiquity.commerce.domain.ItemOption;
import com.ubiquity.giftsender.api.dto.model.AccountDto;
import com.ubiquity.giftsender.api.dto.model.ContactDto;
import com.ubiquity.giftsender.api.dto.model.ItemDto;
import com.ubiquity.social.domain.Contact;
import com.ubiquity.social.domain.SocialIdentity;
import com.ubiquity.social.domain.SocialProviderType;

/***
 * Central class where transformations from model objects to dtos occur
 * 
 * @author chris
 *
 */
public class DtoAssembler {

	
	public static SocialIdentity assembleSocialIdentity(AccountDto accountDto, Boolean isActive) {
		SocialIdentity identity = new SocialIdentity.Builder()
			.accessToken(accountDto.getAccessToken())
			.secretToken(accountDto.getSecretToken())
			.isActive(isActive)
			.refreshToken("")
			.socialProviderType(SocialProviderType.getEnum(accountDto.getProviderId()))
			.build();
		return identity;
	}
	
	public static ItemDto assembleItemDto(Item item){
		ItemDto.Builder itemDtoBuilder = new ItemDto.Builder()
		.itemId(item.getItemId()).itemName(item.getItemName())
		.description(item.getDescription())
		.options(new HashMap<String, List<Object>>()).etag(UUID.randomUUID().toString());

		// Image is optional
		if (item.getImage() != null)
			itemDtoBuilder.imageUrl(item.getImage().getUrl());

		ItemDto itemDto = itemDtoBuilder.build();
		List<Object> list = new LinkedList<Object>();

		if (item.getOptions().size() > 0) {
			// copy item option values to list in ItemDto
			for (ItemOption itemOption : item.getOptions().get("denominations").getOptions()) {
				list.add(((ItemDoubleOption)itemOption).getValue());
			}
			itemDto.getOptions().put("denominations", list);
		}
		return itemDto;
	}

	public static ContactDto assembleContactDto(Contact contact) {
		ContactDto.Builder contactDtoBuilder = new ContactDto.Builder()
			.contactId(contact.getContactId())
			.displayName(contact.getDisplayName())
			.firstName(contact.getFirstName())
			.lastName(contact.getLastName()).email(contact.getEmail())
			.profileUrl(contact.getProfileUrl())
			.ownerId(contact.getOwner().getUserId())
			.socialProviderType(contact.getSocialIdentity().getSocialProviderType().getValue())
			.etag(UUID.randomUUID().toString())
			.socialProviderIdenitifer(contact.getSocialIdentity().getIdentifier());
		// Image is optional
		if (contact.getImage() != null)
			contactDtoBuilder.imageUrl(contact.getImage().getUrl());

		return contactDtoBuilder.build();
	}

}
