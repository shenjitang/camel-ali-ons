camel-ali-ons
==========
camel-ali-ons 是 apache camel的阿里云消息服务ons的component实现。

component name :  ons
------
uri: 
```
ons://topicname?ons://hz_test001?accessKey=xxxxxxx&amp;secretKey=xxxxxxxx&amp;producerId=PID_xxxxxxx
ons://topicname?accessKey=xxxxxxx&amp;secretKey=xxxxxxx&amp;consumerId=CID_xxxxxxx
```
发送时指定head参数：
------
ONS.tag，ONS.key
------
接收到消息后head中的信息
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
          <to uri="ons://topic1?accessKey=xxxxxxx&amp;secretKey=xxxxxxxx&amp;producerId=PID_xxxxxxx" />
      </route>
      <route>
          <from uri="ons://hz_test001?accessKey=xxxxxxx&amp;secretKey=xxxxxxx&amp;consumerId=CID_xxxxxxx" />
          <to uri="log://ons.read?showHeaders=true" />
      </route>

```
