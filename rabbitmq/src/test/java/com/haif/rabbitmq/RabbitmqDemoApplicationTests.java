package com.haif.rabbitmq;

import com.rabbitmq.client.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class RabbitmqDemoApplicationTests {

	private Connection connection;

	private Channel channel;

	@Test
	public void contextLoads() {

	}

	@Before
	public void init() throws Exception {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("127.0.0.1");
		factory.setPort(5672);
		factory.setUsername("guest");
		factory.setPassword("guest");

		// 创建连接
		connection = factory.newConnection();
		// 创建信道
		channel = connection.createChannel();
	}

	@After
	public void destroy() throws Exception {
		channel.close();
		connection.close();
	}

	@Test
	public void producer() throws Exception {

		// 创建交换器 type="direct"、持久化、非自动删除
		channel.exchangeDeclare("exchange_demo","direct", true, false, null);
		// 创建队列 持久化、非排他、非自动删除
		channel.queueDeclare("queue_demo",true, false, false, null);
		// 交换器与队列通过路由键绑定
		channel.queueBind("queue_demo", "exchange_demo", "routingKey_demo");
		// 可绑定多个队列
		// channel.queueBind("queue_demo2", "exchange_demo", "routingKey_demo");
		// 发送持久化消息
		channel.basicPublish("exchange_demo", "routingKey_demo", MessageProperties.PERSISTENT_TEXT_PLAIN, ("message:"+LocalDateTime.now()).getBytes());

		log.info("send message to mq down!");
	}

	@Test
	public void consumer() throws Exception {

		// 设置客户端最多接收未被ack的消息个数
		channel.basicQos(64);
		Consumer consumer = new DefaultConsumer(channel) {
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
				log.info("receive message:{}", new String(body));

				// 手动应答
				channel.basicAck(envelope.getDeliveryTag(), false);

				// 手动拒绝，(消息标记，multi多条，requeue重新入队)
				// channel.basicNack(envelope.getDeliveryTag(), false, false);
				// 拒绝单条
				// channel.basicReject(envelope.getDeliveryTag(), false);
			}
		};

		// 回调
		channel.basicConsume("queue_demo", consumer);
		TimeUnit.SECONDS.sleep(30);
	}

	/**
	 * 生产者确认:事务机制
	 */
	@Test
	public void transaction() throws IOException {
		try {
			channel.txSelect();
			channel.basicPublish("exchange_demo", "routingKey_demo", MessageProperties.PERSISTENT_TEXT_PLAIN, ("transaction message:"+LocalDateTime.now()).getBytes());
			int result = 1 / 0;
			channel.txCommit();
		} catch (Exception e) {
			log.error("transaction send message failed.");
			channel.txRollback();
		}
	}

	/**
	 * 生产者确认:发送方确认机制-同步确认
	 */
	@Test
	public void  publisherConfirmSync() throws Exception {
		//将信道置为publisher confirm 模式
		channel.confirmSelect();

		//之后正常发送消息
		channel.basicPublish( "exchange_demo" , "routingKey_demo" , MessageProperties.PERSISTENT_TEXT_PLAIN, ("publisher confirm sync message:"+LocalDateTime.now()).getBytes());
		if(!channel.waitForConfirms()) {
			log.error("publisher confirm sync send message failed.");
			// do something else..
		}

		// 批量确认
		/*int msgCount = 0;
		List messageList = new ArrayList(16);
		for (int i = 0; i < 20; i++) {
			String message = "publisher batch confirm sync message:"+LocalDateTime.now();
			channel.basicPublish( "exchange_demo" , "routingKey_demo" , MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes());
			messageList.add(message);
			if (++msgCount >= 10) {
				msgCount = 0;
				if (channel.waitForConfirms()) {
					messageList.clear();
				} else {
					//将缓存中的消息重新发送
				}
			}
		}*/
	}

	/**
	 * 生产者确认:发送方确认机制-异步确认
	 */
	@SuppressWarnings({"unchecked", "MismatchedQueryAndUpdateOfCollection"})
	@Test
	public void  publisherConfirmAsync() {

		SortedSet confirmSet = new TreeSet();
		try{
			channel.confirmSelect() ;
			channel.addConfirmListener(new ConfirmListener() {

				//Basic.Ack
				public void handleAck(long deliveryTag , boolean multiple) throws IOException {
					//deliveryTag:消息唯一有序序号
					log.info("Nack, SeqNo: " + deliveryTag + ", multiple: " + multiple);
					// multiple=false一条, true多条
					if (multiple) {
						// unconfirm有序集合 SortedSet
						confirmSet.headSet(deliveryTag - 1).clear();
					} else {
						confirmSet.remove(deliveryTag);
					}
				}

				//Basic.Nack
				public void handleNack(long deliveryTag, boolean multiple) throws IOException {
					if (multiple) {
						confirmSet.headSet(deliveryTag - 1).clear();
					} else {
						confirmSet.remove(deliveryTag) ;
					}

					// 消息重发
					log.error("message need resend, seqNo:{}.", deliveryTag);
				}
			});

			long nextSeqNo = channel.getNextPublishSeqNo();
			String message = "publisher confirm sync message:"+LocalDateTime.now();
			channel.basicPublish( "exchange_demo", "routingKey_demo", MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes());
			confirmSet.add(nextSeqNo);
		} catch (Exception e){
			log.error("publisher confirm async send message failed.");
		}

	}
}
