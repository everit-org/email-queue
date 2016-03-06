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

/**
 * Email Queue Sender implementation of {@link EmailQueueBulkEmailSender}.
 */
public class EmailQueue implements EmailSender {

  private EmailQueueBulkEmailSender emailQueueBulkEmailSender;

  /**
   * Simple constructor.
   *
   * @param emailQueueParam
   *          an {@link EmailQueueConfiguration} object. Cannot be <code>null</code>.
   */
  public EmailQueue(final EmailQueueConfiguration emailQueueParam) {
    Objects.requireNonNull(emailQueueParam, "emailQueueParam cannot be null.");
    Objects.requireNonNull(emailQueueParam.emailSender, "sink cannot be null.");
    Objects.requireNonNull(emailQueueParam.emailStore, "emailStore cannot be null.");
    Objects.requireNonNull(emailQueueParam.querydslSupport, "querydslSupport cannot be null.");
    Objects.requireNonNull(emailQueueParam.transactionPropagator,
        "transactionPropagator cannot be null.");
    emailQueueBulkEmailSender =
        new EmailQueueBulkEmailSender(emailQueueParam.emailSender, emailQueueParam.emailStore,
            emailQueueParam.querydslSupport, emailQueueParam.transactionPropagator);
  }

  /**
   * Creates pass on job that send mails.
   *
   * @param param
   *          a {@link PassOnJobConfiguration} object. Cannot be <code>null</code>!
   */
  public Runnable createPassOnJob(final PassOnJobConfiguration param) {
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
