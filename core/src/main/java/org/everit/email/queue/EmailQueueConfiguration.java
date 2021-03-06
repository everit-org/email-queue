/*
 * Copyright (C) 2011 Everit Kft. (http://www.everit.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.everit.email.queue;

import org.everit.email.sender.EmailSender;
import org.everit.email.store.EmailStore;
import org.everit.persistence.querydsl.support.QuerydslSupport;
import org.everit.transaction.propagator.TransactionPropagator;

/**
 * Parameters to Email Queue.
 */
public class EmailQueueConfiguration {

  public EmailStore emailStore;

  public QuerydslSupport querydslSupport;

  public EmailSender emailSender;

  public TransactionPropagator transactionPropagator;

  public EmailQueueConfiguration emailStore(final EmailStore emailStore) {
    this.emailStore = emailStore;
    return this;
  }

  public EmailQueueConfiguration querydslSupport(final QuerydslSupport querydslSupport) {
    this.querydslSupport = querydslSupport;
    return this;
  }

  public EmailQueueConfiguration emailSender(final EmailSender emailSender) {
    this.emailSender = emailSender;
    return this;
  }

  public EmailQueueConfiguration transactionPropagator(
      final TransactionPropagator transactionPropagator) {
    this.transactionPropagator = transactionPropagator;
    return this;
  }

}
