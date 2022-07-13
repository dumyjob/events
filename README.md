## How to use

### Consumer

1. alternative annotation: @StreamListener

~~~
   @StreamListener(name="order_created")   
   public void consume(OrderCreated message){   
   // handle message    
   }
~~~

2. alternative implementation Class: ConsumerProcessor

~~~
   public class OrderCreatedProcessor extends ConsumerProcessorAdapter<OrderCreated> {

       @Override
       void handle(OrderCreated message){
         // handle message
       }
       @Override
       String name(){
          return "order_created"
       }
   }
~~~

3. Message Consumer Properties config

 ~~~
   spring.message.consumer.#{name}.topic=order   
   spring.message.consumer.#{name}.tag=order_created   
   spring.message.consumer.#{name}.consumerGroup=consumer_id   
   spring.message.consumer.#{name}.threadNum=4   
~~~

### Producer

~~~
  EventPublisher.instance()
                .publish(topic,tag,message);
  EventPublisher.instance()
                .publish(topic,tag,message,delay);
~~~

## messageConsumer

1. 支持consumer多线程消费
   1. Rabbit多线程消费,参考@RabbitListener
   2. rocketMq & ali-ons client本身支持多线程消费 ✔️
   3. kafka: kafka的consumer最大线程数量其实取决于topic的partition的数量
   4. 支持动态调整consumer线程数
2. consumer适配不同的broker, 能够支持同时消费不同类型的broker(kafka/rabbit/rocket/ali-ons) ✔️
   最好能够支持consumer能够同时消费多个broker的消息:broker迁移的需求
3. consumer支持注解@StreamListener ✔️
4. 能够控制consumer是否消费: barrier.consumers=true/false barrier.consumer.${name}=true/false
5. 支持consumer多tag消费
6. @StreamListener和ConsumerProcessor两种实现consumerHandler方式,都使用spring.message.consumer配置消费 ✔️
7. Consumer异常增强,钉钉告警或者其他方式 (能否通过无侵入的方式实现)
8. spring-boot-starter开发
   1. Kafka MessageConsumer和Spring-boot协调问题,
   2. Rabbit MessageConsumer和Spring-boot协调问题, ConnectionFactory在哪里声明?
9. 消息MessageId生成
10. 能都打开消费死信的开关

## producer

1. 延时消息
   1. ali-ons/rocketMq本身是支持延时消息的
   2. Rabbitmq不支持延时消息队列,只能通过队列ttl+死信队列实现固定延时
   3. Kafka无延时队列,虽然Kafka内部有时间轮，支持延时操作，例如：延迟生产、延迟拉取以及延迟删除，但这是Kafka自己内部使用的，用户无法将其作为延迟队列来使用
2. 消息发送异常补偿的时候,处理分布式并发问题
3. 调研ali-ons/rocketMq和RabbitMq以及Kafka对发送多tag消息的支持
   1. ali-ons/rocketMq是支持多tag消费发送的
   2. rabbitMq通过topic方式应该能够实现,需要研究一下rabbitmq源码,看看topic模式是怎么分发到consumer Queue上的
   3. kafka不是很了解,也需要调研一下