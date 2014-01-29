package storm.starter.spout;

import twitter4j.FilterQuery;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.conf.ConfigurationBuilder;


import backtype.storm.Config;
import twitter4j.conf.ConfigurationBuilder;
import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;
import backtype.storm.utils.Utils;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;


public class NewTwitterSpout extends BaseRichSpout {
	
	public static final String MESSAGE = "message";
	
    SpoutOutputCollector _collector;
    LinkedBlockingQueue<Status> queue = null;
    TwitterStream _twitterStream;
//    String _username;
//    String _pwd;
    
    
//    public TwitterSampleSpout(String username, String pwd) {
//        _username = username;
//        _pwd = pwd;
//    }
    
    @Override
    public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
    queue = new LinkedBlockingQueue<Status>(1000);
    _collector = collector;
    ConfigurationBuilder cb = new ConfigurationBuilder();
	    cb.setDebugEnabled(true)
	            .setOAuthConsumerKey("6SHXy7aiX2ZnUZiF4ZD1zg")
	            .setOAuthConsumerSecret("W2Rryb92Z94uq8GDyZdQqGW2HTCBuC7WFuDtyPGdHw")
	            .setOAuthAccessToken("101576455-bzS8xHra4lxvAIfTpuPkEFmYLvDUTmiL7tnxY1KI")
	            .setOAuthAccessTokenSecret("J5nXSvuD2Qbc3prOYEQQfPlVFymBuwU49yj1KzLxjok04");

	    TwitterStreamFactory tf = new TwitterStreamFactory(cb.build());	    
        _twitterStream = tf.getInstance();            
        
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
        _twitterStream.addListener(listener);
        
        FilterQuery tweetFilterQuery = new FilterQuery(); // See 
		tweetFilterQuery.track(new String[]{"Bieber", "Teletubbies"}); // OR on keywords
		tweetFilterQuery.locations(new double[][]{new double[]{-126.562500,30.448674},
            new double[]{-61.171875,44.087585
            }}); // See https://dev.twitter.com/docs/streaming-apis/parameters#locations for proper location doc. 
		//Note that not all tweets have location metadata set.
		tweetFilterQuery.language(new String[]{"en"}); // Note that language does not work properly on Norwegian tweets 
        
        _twitterStream.filter(tweetFilterQuery);
        _twitterStream.sample(); 
    }

    @Override
    public void nextTuple() {
        Status ret = queue.poll();
        if(ret==null) {
            Utils.sleep(50);
        } else {
            _collector.emit(new Values(ret));
        }
    }

    @Override
    public void close() {
        _twitterStream.shutdown();
    }

    @Override
    public Map<String, Object> getComponentConfiguration() {
        Config ret = new Config();
        ret.setMaxTaskParallelism(1);
        return ret;
    }    

    @Override
    public void ack(Object id) {
    }

    @Override
    public void fail(Object id) {
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("tweet"));
    }
    
}
