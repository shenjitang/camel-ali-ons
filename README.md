camel-ali-ons
==========
camel-ali-ons 是 apache camel的阿里云消息服务ons的component实现。
------
component name :  `ons`  
用于producer的uri:   
`ons://topicname?ons://hz_test001?accessKey=xxxxxxx&amp;secretKey=xxxxxxxx&amp;producerId=PID_xxxxxxx`   
用于consumer的uri:    
`ons://topicname?accessKey=xxxxxxx&amp;secretKey=xxxxxxx&amp;consumerId=CID_xxxxxxx`   
区别在于发送时指定url参数：`producerId`  接收时时指定url参数：`consumerId`  
发送时指定head参数  
------
ONS.tag，ONS.key  

接收到消息后head中的信息
------
ONS.tag，ONS.key，ONS.topic，ONS.msgId，ONS.startDeliverTime  
样例：
```
      <route>
          <from uri="timer://mocksz?period=1000&amp;repeatCount=10"></from>
          <setHeader headerName="ONS.tag">
              <constant>abc</constant>
          </setHeader>
          <setBody>
              <constant>this is a test message</constant>
          </setBody>
          <to uri="ons://topic1?accessKey=xxx&amp;secretKey=xxxx&amp;producerId=PID_topic1" />
      </route>
      <route>
          <from uri="ons://hz_test001?accessKey=xxx&amp;secretKey=xxxx&amp;consumerId=CID_topic1" />
          <to uri="log://ons.read?showHeaders=true" />
      </route>

```
