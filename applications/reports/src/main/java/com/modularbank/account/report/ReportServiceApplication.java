package com.modularbank.account.report;

import com.modularbank.account.report.message.AccountMessageReceiver;
import com.modularbank.account.report.message.TransactionMessageReceiver;
import com.modularbank.account.report.repository.AccountRepository;
import com.modularbank.account.report.repository.TransactionRepository;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

@SpringBootApplication
@EnableRedisRepositories(basePackages = "com.modularbank.account.report.repository")
public class ReportServiceApplication {

	public static final String EXCHANGE = "account-exchange";

	public static final String ACCOUNT_QUEUE = "account";
	public static final String TRANSACTION_QUEUE = "transaction";

	@Bean
	@Qualifier("accountQueue")
	Queue accountQueue() {
		return new Queue(ACCOUNT_QUEUE, true);
	}

	@Bean
	@Qualifier("transactionQueue")
	Queue transactionQueue() {
		return new Queue(TRANSACTION_QUEUE, true);
	}

	@Bean
	TopicExchange exchange() {
		return new TopicExchange(EXCHANGE);
	}

	@Bean
	Binding accountBinding(@Qualifier("accountQueue") Queue queue, TopicExchange exchange) {
		return BindingBuilder.bind(queue).to(exchange).with("account.*");
	}

	@Bean
	Binding transactionBinding(@Qualifier("transactionQueue") Queue queue, TopicExchange exchange) {
		return BindingBuilder.bind(queue).to(exchange).with("transaction.*");
	}

	@Bean
	@Qualifier("accountListener")
	MessageListenerAdapter accountListenerAdapter(AccountMessageReceiver receiver) {
		return new MessageListenerAdapter(receiver, "receiveMessage");
	}

	@Bean
	@Qualifier("transactionListener")
	MessageListenerAdapter transactionListenerAdapter(TransactionMessageReceiver receiver) {
		return new MessageListenerAdapter(receiver, "receiveMessage");
	}

	@Bean
	SimpleMessageListenerContainer accountContainer(ConnectionFactory connectionFactory,
													@Qualifier("accountListener") MessageListenerAdapter accountListenerAdapter) {
		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
		container.setConnectionFactory(connectionFactory);
		container.setQueueNames(ACCOUNT_QUEUE);
		container.setMessageListener(accountListenerAdapter);
		return container;
	}

	@Bean
	SimpleMessageListenerContainer transactionContainer(ConnectionFactory connectionFactory,
														@Qualifier("transactionListener") MessageListenerAdapter transactionListenerAdapter) {
		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
		container.setConnectionFactory(connectionFactory);
		container.setQueueNames(TRANSACTION_QUEUE);
		container.setMessageListener(transactionListenerAdapter);
		return container;
	}

	@Bean
	JedisConnectionFactory jedisConnectionFactory() {
		RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration("localhost", 6379);
		return new JedisConnectionFactory(redisStandaloneConfiguration);
	}

	@Bean
	public RedisTemplate<String, Object> redisTemplate() {
		RedisTemplate<String, Object> template = new RedisTemplate<>();
		template.setConnectionFactory(jedisConnectionFactory());
		return template;
	}

	public static void main(String[] args) {
		SpringApplication.run(ReportServiceApplication.class, args);
	}

}
