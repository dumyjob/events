## messageConsumer
1. 支持consumer多线程消费
2. consumer适配不同的broker
3. consumer支持注解@StreamListener
4. 能够控制consumer是否消费:
5. 支持consumer多tag消费
6. 最好能够支持consumer能够消费多个broker的消息:broker迁移的需求
7. 如何兼容@StreamListener和ConsuemrHandler接口2种实现cosumer的模式
8. 


## producer 
1. 延时消息
   1. ali-ons/rocketMq本身是支持延时消息的
   2. Rabbitmq不支持延时消息队列,只能通过队列ttl+死信队列实现固定延时
   3. Kafka无延时队列,虽然Kafka内部有时间轮，支持延时操作，例如：延迟生产、延迟拉取以及延迟删除，但这是Kafka自己内部使用的，用户无法将其作为延迟队列来使用