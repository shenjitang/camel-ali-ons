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

import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.SendResult;
import org.apache.camel.AsyncCallback;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.impl.DefaultAsyncProducer;

public class ONSProducer extends DefaultAsyncProducer implements Processor {

    private final ONSEndpoint onsEndpoint;
    private final String topic;

    public ONSProducer(ONSEndpoint onsEndpoint, String topic) {
        super(onsEndpoint);
        this.onsEndpoint = onsEndpoint;
        this.topic = topic;
    }
    
    @Override
    protected void doStart() throws Exception {
        // check the mqttEndpoint connection when it is started
        onsEndpoint.startProcedure();
        super.doStart();
    }


    @Override
    public boolean process(final Exchange exchange, final AsyncCallback callback) {
        String tag = (String)exchange.getIn().getHeader("ONS.tag");
        String key = (String)exchange.getIn().getHeader("ONS.key");
        String rtopic = (String)exchange.getIn().getHeader("ONS.topic");
        if (rtopic == null || rtopic.trim().length() == 0) {
            rtopic = topic;
        }
        byte[] body = exchange.getIn().getBody(byte[].class);
        if (body != null) {
            Message msg = new Message(rtopic, tag, body);
            if (key != null && key.trim().length() > 0) {
                msg.setKey(key);
            }
            try {
                //发送消息，只要不抛异常就是成功
                SendResult sendResult = onsEndpoint.getOnsProducer().send(msg);
                log.trace("onSuccess from {}", rtopic);
                log.trace(rtopic, sendResult.toString());
                return true;
            } catch (Exception e) {
                log.trace(rtopic, e);
                e.printStackTrace();
                return true;
            }
            
        } else {
            // no data to send so we are done
            log.trace("No data to publish");
            callback.done(true);
            return true;
        }
    }
}
