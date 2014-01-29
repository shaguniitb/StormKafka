import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

public class TwitterSpout {

	/**
	 * @param args
	 * @throws TwitterException 
	 */
	
	public static void ProcessTweet(Twitter twitter) throws TwitterException {
//		Twitter twitter = TwitterFactory.getSingleton();
		Query query = new Query("source:twitter4j meryl");
		QueryResult result = twitter.search(query);
		for (Status status: result.getTweets()){
			System.out.println("@" + status.getUser().getScreenName() + ":" + status.getText());
		}		
	}
	
	public static void main(String[] args) throws TwitterException {
		// TODO Auto-generated method stub
		   //Twitter Conf.
	    ConfigurationBuilder cb = new ConfigurationBuilder();
	    cb.setDebugEnabled(true)
	            .setOAuthConsumerKey("6SHXy7aiX2ZnUZiF4ZD1zg")
	            .setOAuthConsumerSecret("W2Rryb92Z94uq8GDyZdQqGW2HTCBuC7WFuDtyPGdHw")
	            .setOAuthAccessToken("101576455-bzS8xHra4lxvAIfTpuPkEFmYLvDUTmiL7tnxY1KI")
	            .setOAuthAccessTokenSecret("J5nXSvuD2Qbc3prOYEQQfPlVFymBuwU49yj1KzLxjok04");

	    TwitterStreamFactory tf = new TwitterStreamFactory(cb.build());	    
        TwitterStream twitterStream = tf.getInstance();            
        StatusListener listener = new StatusListener() {
            @Override
            public void onStatus(Status status) {
                System.out.println("@" + status.getUser().getScreenName() + " - " + status.getText());
            }

            @Override
            public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
                System.out.println("Got a status deletion notice id:" + statusDeletionNotice.getStatusId());
            }

            @Override
            public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
                System.out.println("Got track limitation notice:" + numberOfLimitedStatuses);
            }

            @Override
            public void onScrubGeo(long userId, long upToStatusId) {
                System.out.println("Got scrub_geo event userId:" + userId + " upToStatusId:" + upToStatusId);
            }

            @Override
            public void onStallWarning(StallWarning warning) {
                System.out.println("Got stall warning:" + warning);
            }

            @Override
            public void onException(Exception ex) {
                ex.printStackTrace();
            }
        };
        twitterStream.addListener(listener);
        twitterStream.sample(); 
	

	}

}
