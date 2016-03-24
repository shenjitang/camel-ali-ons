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

import com.aliyun.openservices.ons.api.Consumer;
import org.apache.camel.AsyncCallback;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.impl.DefaultConsumer;

public class ONSConsumer extends DefaultConsumer {
    private Consumer consumer = null;

    public ONSConsumer(ONSEndpoint endpoint, Processor processor) {
        super(endpoint, processor);
    }

    @Override
    public ONSEndpoint getEndpoint() {
        return (ONSEndpoint) super.getEndpoint();
    }

    protected void doStart() throws Exception {
        getEndpoint().addConsumer(this);
        getEndpoint().startConsumer();
        super.doStart();
    }

    protected void doStop() throws Exception {
        consumer.shutdown();
        getEndpoint().removeConsumer(this);
        super.doStop();
    }

    void asynProcessExchange(final Exchange exchange) {
        boolean sync = true;
        try {
            sync = getAsyncProcessor().process(exchange, new AsyncCallback() {
                @Override
                public void done(boolean doneSync) {
                    if (exchange.getException() != null) {
                        getExceptionHandler().handleException("Error processing exchange.", exchange, exchange.getException());
                    }
                }
            });
        } catch (Throwable e) {
            exchange.setException(e);
        }

        if (sync) {
            if (exchange.getException() != null) {
                getExceptionHandler().handleException("Error processing exchange.", exchange, exchange.getException());
            }
        }       
    }
    
    void processExchange(final Exchange exchange) {
        try {
            getProcessor().process(exchange);
        } catch (Exception e) {
            exchange.setException(e);
        }
    }
}
