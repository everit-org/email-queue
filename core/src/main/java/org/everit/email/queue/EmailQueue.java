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

import java.util.Objects;

import org.everit.email.Email;
import org.everit.email.queue.internal.EmailQueueBulkEmailSender;
import org.everit.email.sender.BulkEmailSender;
import org.everit.email.sender.EmailSender;
import org.everit.email.store.EmailStore;
import org.everit.persistence.querydsl.support.QuerydslSupport;
import org.everit.transaction.propagator.TransactionPropagator;

/**
 * Email Queue Sender implementation of {@link EmailQueueBulkEmailSender}.
 */
public class EmailQueue implements EmailSender {

  private EmailQueueBulkEmailSender emailQueueBulkEmailSender;

  /**
   * Simple constructor.
   *
   * @param emailSender
   *          an {@link EmailSender} instance. Cannot be <code>null</code>!
   * @param emailStore
   *          an {@link EmailStore} instance. Cannot be <code>null</code>!
   * @param querydslSupport
   *          a {@link QuerydslSupport} instance. Cannot be <code>null</code>!
   * @param transactionPropagator
   *          a {@link TransactionPropagator} instance. Cannot be <code>null</code>!
   */
  public EmailQueue(final EmailSender emailSender, final EmailStore emailStore,
      final QuerydslSupport querydslSupport, final TransactionPropagator transactionPropagator) {
    Objects.requireNonNull(emailSender, "emailSender cannot be null.");
    Objects.requireNonNull(emailStore, "emailStore cannot be null.");
    Objects.requireNonNull(querydslSupport, "querydslSupport cannot be null.");
    Objects.requireNonNull(transactionPropagator, "transactionPropagator cannot be null.");
    emailQueueBulkEmailSender =
        new EmailQueueBulkEmailSender(emailSender, emailStore, querydslSupport,
            transactionPropagator);
  }

  /**
   * Creates pass on job that send mails.
   *
   * @param param
   *          a {@link CreatePassOnJobParam} object. Cannot be <code>null</code>!
   */
  public Runnable createPassOnJob(final CreatePassOnJobParam param) {
    Objects.requireNonNull(param, "param cannot be null.");
    return emailQueueBulkEmailSender.createPassOnJob(param);
  }

  @Override
  public BulkEmailSender openBulkEmailSender() {
    return emailQueueBulkEmailSender;
  }

  @Override
  public void sendEmail(final Email mail) {
    try (BulkEmailSender bulkEmailSender = openBulkEmailSender()) {
      bulkEmailSender.sendEmail(mail);
    }
  }
}
