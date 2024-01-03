package uk.ac.man.cs.eventlite.dao;
import java.util.List;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

public class TwitterService{
	private Twitter twitter;
	
    public TwitterService() {
    	ConfigurationBuilder cb = new ConfigurationBuilder();
    	cb.setDebugEnabled(true)
    	.setOAuthConsumerKey("YwNpdAOVZrc21EYUYWRMZrQHS")
    	.setOAuthConsumerSecret("JNLqTsvWTNdSWLYaYtxTzEy6AiDXCB5HQ92Wt75UHS2BO53r2a")
    	.setOAuthAccessToken("1520795918487937024-KrjACvgaAhZ1ivAsVnkF6tuP9XzOyL")
    	.setOAuthAccessTokenSecret("uJa0r2AWm92cAM9Wxzegmy8am6oYDxbIxe0fjrkjqEdn3");
    	TwitterFactory tf = new TwitterFactory(cb.build());
    	twitter = tf.getInstance();

    }
    public Status createTweet(String tweet) throws TwitterException{
    		return twitter.updateStatus(tweet);
    	}

	public List<Status> getTimeLine() throws TwitterException {
		List<Status> statuses = twitter.getHomeTimeline();
		return statuses;
		}

}