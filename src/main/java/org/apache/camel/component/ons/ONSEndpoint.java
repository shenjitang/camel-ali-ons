/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.component.ons;

import com.aliyun.openservices.ons.api.Action;
import com.aliyun.openservices.ons.api.ConsumeContext;
import com.aliyun.openservices.ons.api.Consumer;
import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.MessageListener;
import com.aliyun.openservices.ons.api.ONSFactory;
import com.aliyun.openservices.ons.api.Producer;
import com.aliyun.openservices.ons.api.PropertyKeyConst;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.impl.DefaultEndpoint;
import org.apache.camel.spi.Metadata;
import org.apache.camel.spi.UriEndpoint;
import org.apache.camel.spi.UriParam;
import org.apache.camel.spi.UriPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ONS endpoint
 */
@UriEndpoint(scheme = "ons", title = "ONS", syntax = "ons:name", consumerClass = ONSConsumer.class, label = "messaging")
public class ONSEndpoint extends DefaultEndpoint {
    private static final Logger LOG = LoggerFactory.getLogger(ONSEndpoint.class);

    private static final int PUBLISH_MAX_RECONNECT_ATTEMPTS = 3;

    private final List<ONSConsumer> consumers = new CopyOnWriteArrayList<ONSConsumer>();
    
    private com.aliyun.openservices.ons.api.Producer onsProducer;
    private com.aliyun.openservices.ons.api.Consumer onsConsumer;

    @UriPath @Metadata(required = "true")
    private String name;
    @UriParam
    private String secretKey;
    @UriParam
    private String producerId;
    @UriParam()
    private String consumerId;
    @UriParam(defaultValue = "*")
    private String tag;
    @UriParam
    private String accessKey;
    
    public ONSEndpoint(final String uri, ONSComponent component) {
        super(uri, component);
    }


    public String getName() {
        return name;
    }

    /**
     * A logical name to use which is not the topic name.
     */
    public void setName(String name) {
        this.name = name;
    }

    @Override
    protected void doStart() throws Exception {
        super.doStart();
        createConnection();
    }

    protected void createConnection() {
        Properties properties = new Properties();
        properties.put(PropertyKeyConst.AccessKey, accessKey);
        properties.put(PropertyKeyConst.SecretKey, secretKey);
        if (producerId != null && producerId.trim().length() > 0) {
            properties.put(PropertyKeyConst.ProducerId, producerId);
            onsProducer = ONSFactory.createProducer(properties);
        }
        if (consumerId != null && consumerId.trim().length() > 0) {
            properties.put(PropertyKeyConst.ConsumerId, consumerId);
            onsConsumer = ONSFactory.createConsumer(properties);
            if (tag == null || tag.trim().length() > 0) {
                tag = "*";
            }
            onsConsumer.subscribe(name, tag, new MessageListener() {
                @Override
                public Action consume(Message message, ConsumeContext context) {
                    LOG.info("Receive: " + message);
                    try {
                        System.out.println("===receive msg body: " + new String(message.getBody(), "utf8"));
                    } catch (Exception e) {e.printStackTrace();}
                    Exchange exchange = createExchange();
                    exchange.getIn().setBody(message.getBody());
                    exchange.getIn().setHeader("ONS.topic", message.getTopic());
                    exchange.getIn().setHeader("ONS.msgId", message.getMsgID());
                    exchange.getIn().setHeader("ONS.tag", message.getTag());
                    exchange.getIn().setHeader("ONS.key", message.getKey());
                    exchange.getIn().setHeader("ONS.startDeliverTime", message.getStartDeliverTime());
                    for (ONSConsumer csm : consumers) {
                        csm.asynProcessExchange(exchange);
                    }
                    return Action.CommitMessage;
                }
            });
        }
    }

    public void startProcedure() {
        if (onsProducer == null) {
            createConnection();
        }
        onsProducer.start();
    }
    
    public void startConsumer() {
        if (onsConsumer == null) {
            createConnection();
        } 
        onsConsumer.start();
    }

    
    protected void doStop() throws Exception {
        super.doStop();
        if (onsProducer != null) {
            onsProducer.shutdown();
        }
        if (onsConsumer != null) {
            onsConsumer.shutdown();
        }
    }


    void addConsumer(ONSConsumer consumer) {
        consumers.add(consumer);
    }

    void removeConsumer(ONSConsumer consumer) {
        consumers.remove(consumer);
    }

    public boolean isSingleton() {
        return true;
    }

    public Producer getOnsProducer() {
        return onsProducer;
    }

    public Consumer getOnsConsumer() {
        return onsConsumer;
    }

    @Override
    public org.apache.camel.Producer createProducer() throws Exception {
        return new ONSProducer(this, name);
    }

    @Override
    public org.apache.camel.Consumer createConsumer(Processor processor) throws Exception {
        ONSConsumer answer = new ONSConsumer(this, processor);
        configureConsumer(answer);
        return answer;
    }

    public String getAccessKey() {
        return accessKey;
    }

    /**
     * 设置ali ons的AccessKey
     * @param accessKey 
     */
    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    /**
     * 设置ali ons的SecretKey
     * @param secretKey 
     */
    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getProducerId() {
        return producerId;
    }

    /**
     * 设置ali ons topic的ProducerId 
     * @param producerId 
     */
    public void setProducerId(String producerId) {
        this.producerId = producerId;
    }

    public String getConsumerId() {
        return consumerId;
    }

    /**
     * 设置ali ons topic的ConsumerId 
     * @param consumerId 
     */
    public void setConsumerId(String consumerId) {
        this.consumerId = consumerId;
    }

    public String getTag() {
        return tag;
    }

    /**
     * 设置ali ons topic消息的tag，用于接收
     * @param tag 
     */
    public void setTag(String tag) {
        this.tag = tag;
    }


    
    
}
