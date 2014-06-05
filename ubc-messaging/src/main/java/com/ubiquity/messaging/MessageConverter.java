package com.ubiquity.messaging;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.niobium.common.exception.ConstraintValidationException;
import com.ubiquity.messaging.format.Message;

public class MessageConverter {

	private Gson gson;

	private Map<String, Class<?>> aliases = new HashMap<String, Class<?>>();


	/***
	 * Converts a dto into an output string
	 * @param dto
	 * @return
	 */
	public <T> String serialize(T dto) {
		return gson.toJson(dto);
	}

	/***
	 * 
	 * Serializes the input stream into a destination class after performing validation
	 * 
	 * @param body payload
	 * @param clazz destination
	 * @return
	 * @throws IllegalArgumentException if payload is null or invalid JSON
	 * @throws ConstraintValidationException if any constraints are violated
	 */
	public <T> T deserialize(byte[] content, Class<T> clazz) {
		return deserialize(new String(content), clazz);
	}

	/***
	 * Serializes the string into a destination class
	 * 
	 * @param payload
	 * @param clazz
	 * @return
	 */
	public <T> T deserialize(String payload, Class<T> clazz) {
		T request;
		try {
			request = gson.fromJson(payload, clazz);
			if (request == null)
				throw new IllegalArgumentException("Payload cannot be null");
		} catch (Exception e) {
			throw new IllegalArgumentException("Problem parsing payload", e);
		}

		return request;
	}

	private void build() {
		JsonDeserializer<Message> messageTypeDeserializer = new JsonDeserializer<Message>() {
			@Override
			public Message deserialize(JsonElement element, Type type,
					JsonDeserializationContext context)
					throws JsonParseException {
				JsonObject jsonObject = element.getAsJsonObject();
				String messageType = context.deserialize(
						jsonObject.get("type"), String.class);

				Class<?> messageClass = aliases.get(messageType);
				if (messageClass == null)
					throw new JsonParseException("Unrecognized messge type: "
							+ messageType);
				Object content = context.deserialize(jsonObject.get("content"),
						messageClass);
				return new Message(content);

			}
		};
		
		this.gson = new GsonBuilder().registerTypeAdapter(Message.class, messageTypeDeserializer).create();

	}

	public static class Builder {
		private Map<String, Class<?>> aliases = new HashMap<String, Class<?>>();

		public Builder registerMessageType(Class<?> messageClass) {
			aliases.put(messageClass.getSimpleName(), messageClass);
			return this;
		}

		public MessageConverter build() {
			return new MessageConverter(this);
		}
	}

	private MessageConverter(Builder builder) {
		this.aliases.putAll(builder.aliases);
		build();
	}
}
