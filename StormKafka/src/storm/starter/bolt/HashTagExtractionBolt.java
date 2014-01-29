package storm.starter.bolt;

import backtype.storm.task.OutputCollector;

import backtype.storm.task.TopologyContext;

import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Tuple;

import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;
import org.apache.commons.lang3.StringUtils;
import storm.starter.spout.NewTwitterSpout;

import java.util.Map;
import java.util.StringTokenizer;


public class HashTagExtractionBolt extends BaseRichBolt {

	private OutputCollector _collector;
	
   @Override
   public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
        _collector = outputCollector;

   }
	
  @Override
  public void execute(Tuple tuple) {
	  String text = tuple.getStringByField(NewTwitterSpout.MESSAGE);
	  StringTokenizer st = new StringTokenizer(text);

      System.out.println("---- Split by space ------");
      while (st.hasMoreElements()) {
          String term = (String) st.nextElement();
          if (StringUtils.startsWith(term, "#")){
        	  System.out.println("Found Hashtag: " + term);
        	  _collector.emit(new Values(term, 1.0));
          }
              
      }

      // Confirm that this tuple has been treated.
      _collector.ack(tuple);
  }

  @Override
  public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
	  outputFieldsDeclarer.declare(new Fields("entity"));
  }
  
  @Override
  public void cleanup() {
      super.cleanup();

  }

}
