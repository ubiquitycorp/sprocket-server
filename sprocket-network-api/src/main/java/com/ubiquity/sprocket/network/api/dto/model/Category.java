package com.ubiquity.sprocket.network.api.dto.model;

public enum Category {
	MostPopular("Most Popular"),
	Subscriptions("Latest Subscription Videos"),
	MyHistory("My History"),
	NewsFeed("News Feed"),
	LocalNewsFeed("Local News Feed"),
	Activities("My Activities"),
	MyTweets("My Tweets"),
	Tweets("Tweets"),
	MyFeeds("My Feeds"),
	HotPosts("Hot Posts");
	
	private String categoryName ;
	
	Category(String categoryName)
	{
		this.categoryName = categoryName;
	}
	
	public String getCategoryName() {
		return categoryName;
	}
	
	public static Category getCategoryByCategoryName(String categoryName) {
		for(Category category :Category.values())
		{
			if(category.getCategoryName() == categoryName)
				return category;
		}
		return null;
	}
	
	public static Category getCategoryById(int id) {
		validate(id);
		return Category.values()[id];
	}
	
	private static void validate(int id) {
		if(id > Category.values().length)
		throw new IllegalArgumentException("No such external network for id: " + id);
	}
}
