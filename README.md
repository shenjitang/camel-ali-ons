camel-ali-ons
==========
camel-ali-ons 是 apache camel的阿里云消息服务ons的api调用方式的component实现。
------
component name :  `ons`  
用于producer的uri format:   
`<to uri="ons://topicname?accessKey=xxx&amp;secretKey=xxxx&amp;producerId=PID_xxx" />`   
用于consumer的uri format:    
`<from uri="ons://topicname?accessKey=xxx&amp;secretKey=xxxx&amp;consumerId=CID_xxx" />`  
区别在于发送时指定url参数：`producerId`  接收时时指定url参数：`consumerId`  

## Options
### Common
<table>
<thead><tr><th>Property</th><th>Default</th><th>Description</th></tr></thead>
<tbody>
<tr><td> <tt>accessKey</tt> </td><td></td><td> For aliyun ons authentication</td></tr>
<tr>
    <td> <tt>secretKey</tt> </td>
    <td></td>
    <td>for  aliyun ons authentication secret </td>
</tr>
</tbody></table>

### Procedure
<table>
<thead><tr><th>Property</th><th>Default</th><th>Description</th></tr></thead>
<tbody>
<tr><td> <tt>producerId</tt> </td><td></td><td> For aliyun ons topic ProcedureId</td></tr>
</tbody></table>

### Consumer
<table>
<thead><tr><th>Property</th><th>Default</th><th>Description</th></tr></thead>
<tbody>
<tr><td> <tt>consumerId</tt> </td><td></td><td> For aliyun ons topic ConsumerId</td></tr>
<tr><td> <tt>tag</tt> </td><td>*</td><td> the tag of message for consumer</td></tr>
</tbody></table>


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
