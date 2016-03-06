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
import org.everit.email.queue.PassOnJobConfiguration;
import org.everit.email.queue.schema.qdsl.QEmailQueue;
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
   * Queued email data holder.
   */
  public static class QueuedEmailDTO {

    public long queuedEmailId;

    public long storedEmailId;
  }

  private EmailStore emailStore;

  private QuerydslSupport querydslSupport;

  private EmailSender sink;

  private TransactionPropagator transactionPropagator;

  /**
   * Simple constructor.
   *
   * @param sink
   *          an {@link EmailSender} instance.
   * @param emailStore
   *          an {@link EmailStore} instance.
   * @param querydslSupport
   *          a {@link QuerydslSupport} instance.
   * @param transactionPropagator
   *          a {@link TransactionPropagator} instance.
   */
  public EmailQueueBulkEmailSender(final EmailSender sink, final EmailStore emailStore,
      final QuerydslSupport querydslSupport, final TransactionPropagator transactionPropagator) {
    this.sink = sink;
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
  public Runnable createPassOnJob(final PassOnJobConfiguration config) {
    return () -> {
      transactionPropagator.required(() -> {
        List<QueuedEmailDTO> queuedEmails = selectQueuedEmailForUpdate(config.batchSize);
        try (BulkEmailSender bulkEmailSender = sink.openBulkEmailSender()) {
          for (QueuedEmailDTO queuedEmail : queuedEmails) {
            Email mail = emailStore.read(queuedEmail.storedEmailId);
            bulkEmailSender.sendEmail(mail);
            deleteQueuedEmail(queuedEmail.queuedEmailId);
            emailStore.remove(queuedEmail.storedEmailId);
          }
        }
        return;
      });
    };
  }

  private void deleteQueuedEmail(final long queuedEmailId) {
    querydslSupport.execute((connection, configuration) -> {
      QEmailQueue qEmailQueue = QEmailQueue.emailQueue;
      return new SQLDeleteClause(connection, configuration, qEmailQueue)
          .where(qEmailQueue.queuedEmailId.eq(queuedEmailId))
          .execute();
    });
  }

  private void enqueueStoredEmailId(final long storedEmailId) {
    querydslSupport.execute((connection, configuration) -> {
      QEmailQueue qEmailQueue = QEmailQueue.emailQueue;
      return new SQLInsertClause(connection, configuration, qEmailQueue)
          .set(qEmailQueue.storedEmailId, storedEmailId)
          .set(qEmailQueue.timestamp_, Timestamp.from(Instant.now()))
          .execute();
    });
  }

  private List<QueuedEmailDTO> selectQueuedEmailForUpdate(final int limit) {
    return querydslSupport.execute((connection, configuration) -> {
      QEmailQueue qEmailQueue = QEmailQueue.emailQueue;
      return new SQLQuery(connection, configuration)
          .from(qEmailQueue)
          .orderBy(qEmailQueue.timestamp_.asc())
          .limit(limit)
          .forUpdate()
          .list(Projections.fields(QueuedEmailDTO.class,
              qEmailQueue.queuedEmailId,
              qEmailQueue.storedEmailId));
    });
  }

  @Override
  public void sendEmail(final Email mail) {
    transactionPropagator.required(() -> {
      long storedEmailId = emailStore.store(mail);
      enqueueStoredEmailId(storedEmailId);
    });
  }

}
