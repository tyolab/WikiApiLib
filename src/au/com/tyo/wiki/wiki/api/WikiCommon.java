package au.com.tyo.wiki.wiki.api;

public class WikiCommon {

	private PageImages pageImages;
	
	private MobileView mobileView;
	
	private ListRandom listRandom;
	
	private FeaturedFeed featuredFeed;
	
	private LangLink langLink;
	
	private Images images;
	
	private ImageUrl imageUrl;
	
	private ApiQueryListSearch wikiSearch;
	
	public WikiCommon() {
		mobileView = new MobileView();
		pageImages = new PageImages();
		featuredFeed = new FeaturedFeed();
		imageUrl = new ImageUrl();
		images = new Images();
		wikiSearch = new ApiQueryListSearch();
	}

	public PageImages getPageImages() {
		return pageImages;
	}

	public MobileView getMobileView() {
		return mobileView;
	}

	public ListRandom getListRandom() {
		if (this.listRandom == null)
			listRandom = new ListRandom();
		return listRandom;
	}

	public FeaturedFeed getFeaturedFeed() {
		return featuredFeed;
	}

	public LangLink getLangLink() {
		if (langLink == null)
			langLink = new LangLink();
		return langLink;
	}

	public ImageUrl getImageUrl() {
		return imageUrl;
	}
	
	public Images getImages() {
		return images;
	}
	
	public ApiQueryListSearch getWikiSearchApi() {
		return wikiSearch;
	}
}
