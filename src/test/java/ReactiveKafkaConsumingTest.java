import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
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
class ReactiveKafkaConsumingTest {

	private ReceiverOptions<String, String> reeiverOptions;

	public ReceiverOptions<String, String> receiverCmAprvOptions() {
		StringDeserializer deserializer = new StringDeserializer();

		return ReceiverOptions.<String, String>create(getConsumerProps())
			.subscription(Collections.singleton("test"))
			.withKeyDeserializer(new StringDeserializer())
			.withValueDeserializer(deserializer)
			.pollTimeout(Duration.ofMillis(3000L));
	}

	public Properties getConsumerProps() {
		String bootStrapServers = "localhost:9092";
		Properties props = new Properties();
		props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootStrapServers);
		props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
		props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
		props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
		props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
		props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 1);
		props.put(ConsumerConfig.GROUP_ID_CONFIG, "consuming-test");
		props.put("sasl.jaas.config", String.format("org.apache.kafka.common.security.scram.ScramLoginModule required username='%s' password='%s';", "cpss_tst", "lotte1234!"));
		return props;
	}


	@BeforeEach
	void setUp() {
		reeiverOptions = receiverCmAprvOptions();
	}

	@Test
	public void ackSequencialTest() throws InterruptedException {
		// ack_sequencial();
		ack_pararell();
	}

	public void ack_sequencial() throws InterruptedException {
		AtomicLong latestOffset = new AtomicLong(0l);
		Flux.defer(() -> KafkaReceiver.create(reeiverOptions).receive())
			.parallel(5)
			.runOn(Schedulers.newParallel("kbs", 10))
			.flatMap(record -> processMessage(record))
			.sequential()
			.subscribe(record -> {
				if(latestOffset.get() < record.offset()){
					latestOffset.set(record.offset());
					record.receiverOffset().acknowledge();
				}
			});
	}

	public Mono<ReceiverRecord<String, String>> processMessage(ReceiverRecord<String, String> record) {
		return Mono.fromSupplier(() -> {
			try {
				if (record.offset() % 5 == 0) {
					throw new RuntimeException(); // testing for sending dlq
				}
				sleepUtil();
			}catch(Exception e) {
				sendToDlq(record, e);
			}
			return record;
		});
	}

	private void sleepUtil() {
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	public void sendToDlq(ReceiverRecord<String, String> record, Throwable e) {
		log.info("dlq로 여행을 떠나자");
	}


	public void ack_pararell() throws InterruptedException {
		Flux.defer(() -> KafkaReceiver.create(reeiverOptions).receive())
			.parallel()
			.flatMap(record -> processMessage(record))
			.subscribe(record -> {
				record.receiverOffset().acknowledge();
			});
	}

}