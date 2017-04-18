package kafka_study;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;



//java -cp kafka_study-0.0.1-SNAPSHOT-jar-with-dependencies.jar kafka_study.SimpleHLConsumer
//it consumes all messages from all partitions because of no particular partition specified

public class SimpleHLConsumer {
	private final ConsumerConnector consumer;
	private final String topic;

	public SimpleHLConsumer(String zookeeper, String groupId, String topic) {
		consumer = kafka.consumer.Consumer.createJavaConsumerConnector(createConsumerConfig(zookeeper, groupId));
		this.topic = topic;
	}

	private static ConsumerConfig createConsumerConfig(String zookeeper, String groupId) {
		Properties props = new Properties();
		props.put("zookeeper.connect", zookeeper);
		props.put("group.id", groupId);
		props.put("zookeeper.session.timeout.ms", "500");
		props.put("zookeeper.sync.time.ms", "250");
		props.put("auto.commit.interval.ms", "1000");
		return new ConsumerConfig(props);
	}

	public void testConsumer() {
		Map<String, Integer> topicMap = new HashMap<String, Integer>();
		// Define single thread for topic
		topicMap.put(topic, new Integer(1));
		Map<String, List<KafkaStream<byte[], byte[]>>> consumerStreamsMap = consumer.createMessageStreams(topicMap);
		List<KafkaStream<byte[], byte[]>> streamList = consumerStreamsMap.get(topic);
		for (final KafkaStream<byte[], byte[]> stream : streamList) {
			ConsumerIterator<byte[], byte[]> consumerIte = stream.iterator();
			while (consumerIte.hasNext())
				System.out.println("Message from Single Topic :: " + new String(consumerIte.next().message()));
		}
		if (consumer != null)
			consumer.shutdown();
	}

	public static void main(String[] args) {
		String zooKeeper = "pcdtckaf01d.emea1.cis.trcloud:2181,pcdtckaf02d.emea1.cis.trcloud:2181,pcdtckaf03d.emea1.cis.trcloud:2181";
		String groupId = "streaming_test_group";
		String topic = "streaming_test";
		SimpleHLConsumer simpleHLConsumer = new SimpleHLConsumer(zooKeeper, groupId, topic);
		simpleHLConsumer.testConsumer();
	}
}