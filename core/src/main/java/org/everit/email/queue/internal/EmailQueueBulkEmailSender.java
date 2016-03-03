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
package org.everit.email.queue.internal;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

import org.everit.email.Email;
import org.everit.email.queue.CreatePassOnJobParam;
import org.everit.email.queue.schema.qdsl.QQueueMail;
import org.everit.email.sender.BulkEmailSender;
import org.everit.email.sender.EmailSender;
import org.everit.email.store.EmailStore;
import org.everit.persistence.querydsl.support.QuerydslSupport;
import org.everit.transaction.propagator.TransactionPropagator;

import com.mysema.query.sql.SQLQuery;
import com.mysema.query.sql.dml.SQLDeleteClause;
import com.mysema.query.sql.dml.SQLInsertClause;
import com.mysema.query.types.Projections;

/**
 * Email Queue Bulk email sender implementation.
 */
public class EmailQueueBulkEmailSender implements BulkEmailSender {

  /**
   * Queue email data holder.
   */
  public static class QueueEmailDTO {

    public long queueEmailId;

    public long storedEmailId;
  }

  private EmailSender emailSender;

  private EmailStore emailStore;

  private QuerydslSupport querydslSupport;

  private TransactionPropagator transactionPropagator;

  /**
   * Simple constructor.
   *
   * @param emailSender
   *          an {@link EmailSender} instance.
   * @param emailStore
   *          an {@link EmailStore} instance.
   * @param querydslSupport
   *          a {@link QuerydslSupport} instance.
   * @param transactionPropagator
   *          a {@link TransactionPropagator} instance.
   */
  public EmailQueueBulkEmailSender(final EmailSender emailSender, final EmailStore emailStore,
      final QuerydslSupport querydslSupport, final TransactionPropagator transactionPropagator) {
    this.emailSender = emailSender;
    this.emailStore = emailStore;
    this.querydslSupport = querydslSupport;
    this.transactionPropagator = transactionPropagator;
  }

  @Override
  public void close() {
    // do nothing
  }

  /**
   * Creates pass on job that send mails.
   */
  public Runnable createPassOnJob(final CreatePassOnJobParam param) {
    return () -> {
      transactionPropagator.required(() -> {
        List<QueueEmailDTO> queuedEmails = selectAndLockQueueEmail(param);
        try (BulkEmailSender bulkEmailSender = emailSender.openBulkEmailSender()) {
          for (QueueEmailDTO queueEmail : queuedEmails) {
            Email mail = emailStore.read(queueEmail.storedEmailId);
            bulkEmailSender.sendEmail(mail);
            deleteQueueEmail(queueEmail.queueEmailId);
            emailStore.remove(queueEmail.storedEmailId);
          }
        }
        return;
      });
    };
  }

  private void deleteQueueEmail(final long queueEmailId) {
    querydslSupport.execute((connection, configuration) -> {
      QQueueMail qQueueMail = QQueueMail.queueMail;
      return new SQLDeleteClause(connection, configuration, qQueueMail)
          .where(qQueueMail.queueEmailId.eq(queueEmailId))
          .execute();
    });
  }

  private void saveQueuedMail(final long storedEmailId) {
    querydslSupport.execute((connection, configuration) -> {
      QQueueMail qQueueMail = QQueueMail.queueMail;
      return new SQLInsertClause(connection, configuration, qQueueMail)
          .set(qQueueMail.storedEmailId, storedEmailId)
          .set(qQueueMail.storedTimestamp, Timestamp.from(Instant.now()))
          .execute();
    });
  }

  private List<QueueEmailDTO> selectAndLockQueueEmail(final CreatePassOnJobParam param) {
    return querydslSupport.execute((connection, configuration) -> {
      QQueueMail qQueueMail = QQueueMail.queueMail;
      return new SQLQuery(connection, configuration)
          .from(qQueueMail)
          .orderBy(qQueueMail.storedTimestamp.asc())
          .limit(param.max)
          .forUpdate()
          .list(Projections.fields(QueueEmailDTO.class,
              qQueueMail.queueEmailId,
              qQueueMail.storedEmailId));
    });
  }

  @Override
  public void sendEmail(final Email mail) {
    transactionPropagator.required(() -> {
      long storedEmailId = emailStore.store(mail);
      saveQueuedMail(storedEmailId);
      return null;
    });
  }

}
