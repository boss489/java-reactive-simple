import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverOptions;
import reactor.kafka.receiver.ReceiverRecord;

@Slf4j
public class ReactiveKafkaProducingTest {
	//users/lotte/Downloads/kafka_2.12-2.5.0
	//./bin/zookeeper-server-start.sh config/zookeeper.properties
	//./bin/kafka-server-start.sh config/server.properties

	public static void main(String[] args) {
		publishMessages("test", 9999);
	}

	public static void publishMessages(String topic, int messageCount) {
		Properties props = new Properties();
		props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
		props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

		KafkaProducer<String, String> producer = new KafkaProducer<>(props);

		for (int i = 0; i < messageCount; i++) {
			String key = "key-" + i;
			String value = "message-" + i;
			producer.send(new ProducerRecord<>(topic, key, value), (metadata, exception) -> {
				if (exception != null) {
					System.err.printf("Error publishing message: %s%n", exception.getMessage());
				} else {
					System.out.printf("Message published to topic %s, partition %d, offset %d%n",
						metadata.topic(), metadata.partition(), metadata.offset());
				}
			});
		}

		producer.close();
	}

}