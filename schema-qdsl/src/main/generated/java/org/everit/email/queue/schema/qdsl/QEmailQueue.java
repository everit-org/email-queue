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
package org.everit.email.queue.schema.qdsl;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;

import com.mysema.query.sql.ColumnMetadata;




/**
 * QEmailQueue is a Querydsl query type for QEmailQueue
 */
@Generated("com.mysema.query.sql.codegen.MetaDataSerializer")
public class QEmailQueue extends com.mysema.query.sql.RelationalPathBase<QEmailQueue> {

    private static final long serialVersionUID = -367369369;

    public static final QEmailQueue emailQueue = new QEmailQueue("EMQUEUE_EMAIL_QUEUE");

    public class PrimaryKeys {

        public final com.mysema.query.sql.PrimaryKey<QEmailQueue> emqueueEmailQueuePk = createPrimaryKey(queuedEmailId);

    }

    public class ForeignKeys {

        public final com.mysema.query.sql.ForeignKey<org.everit.email.store.ri.schema.qdsl.QEmail> storedEmailFk = createForeignKey(storedEmailId, "STORED_EMAIL_ID");

    }

    public final NumberPath<Long> queuedEmailId = createNumber("queuedEmailId", Long.class);

    public final NumberPath<Long> storedEmailId = createNumber("storedEmailId", Long.class);

    public final DateTimePath<java.sql.Timestamp> timestamp_ = createDateTime("timestamp_", java.sql.Timestamp.class);

    public final PrimaryKeys pk = new PrimaryKeys();

    public final ForeignKeys fk = new ForeignKeys();

    public QEmailQueue(String variable) {
        super(QEmailQueue.class, forVariable(variable), "org.everit.email.queue", "EMQUEUE_EMAIL_QUEUE");
        addMetadata();
    }

    public QEmailQueue(String variable, String schema, String table) {
        super(QEmailQueue.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QEmailQueue(Path<? extends QEmailQueue> path) {
        super(path.getType(), path.getMetadata(), "org.everit.email.queue", "EMQUEUE_EMAIL_QUEUE");
        addMetadata();
    }

    public QEmailQueue(PathMetadata<?> metadata) {
        super(QEmailQueue.class, metadata, "org.everit.email.queue", "EMQUEUE_EMAIL_QUEUE");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(queuedEmailId, ColumnMetadata.named("QUEUED_EMAIL_ID").ofType(-5).withSize(19).notNull());
        addMetadata(storedEmailId, ColumnMetadata.named("STORED_EMAIL_ID").ofType(-5).withSize(19).notNull());
        addMetadata(timestamp_, ColumnMetadata.named("TIMESTAMP_").ofType(93).withSize(23).withDigits(10).notNull());
    }

}

