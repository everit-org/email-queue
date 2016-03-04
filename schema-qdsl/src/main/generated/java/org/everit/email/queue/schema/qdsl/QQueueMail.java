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
 * QQueueMail is a Querydsl query type for QQueueMail
 */
@Generated("com.mysema.query.sql.codegen.MetaDataSerializer")
public class QQueueMail extends com.mysema.query.sql.RelationalPathBase<QQueueMail> {

    private static final long serialVersionUID = -1655102570;

    public static final QQueueMail queueMail = new QQueueMail("EMQUEUE_QUEUE_MAIL");

    public class PrimaryKeys {

        public final com.mysema.query.sql.PrimaryKey<QQueueMail> emqueueQueuedMailPk = createPrimaryKey(queuedEmailId);

    }

    public class ForeignKeys {

        public final com.mysema.query.sql.ForeignKey<org.everit.email.store.ri.schema.qdsl.QEmail> queueMailEmstrEmailFk = createForeignKey(storedEmailId, "STORED_EMAIL_ID");

    }

    public final NumberPath<Long> queuedEmailId = createNumber("queuedEmailId", Long.class);

    public final NumberPath<Long> storedEmailId = createNumber("storedEmailId", Long.class);

    public final DateTimePath<java.sql.Timestamp> storedTimestamp = createDateTime("storedTimestamp", java.sql.Timestamp.class);

    public final PrimaryKeys pk = new PrimaryKeys();

    public final ForeignKeys fk = new ForeignKeys();

    public QQueueMail(String variable) {
        super(QQueueMail.class, forVariable(variable), "org.everit.email.queue", "EMQUEUE_QUEUE_MAIL");
        addMetadata();
    }

    public QQueueMail(String variable, String schema, String table) {
        super(QQueueMail.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QQueueMail(Path<? extends QQueueMail> path) {
        super(path.getType(), path.getMetadata(), "org.everit.email.queue", "EMQUEUE_QUEUE_MAIL");
        addMetadata();
    }

    public QQueueMail(PathMetadata<?> metadata) {
        super(QQueueMail.class, metadata, "org.everit.email.queue", "EMQUEUE_QUEUE_MAIL");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(queuedEmailId, ColumnMetadata.named("QUEUED_EMAIL_ID").ofType(-5).withSize(19).notNull());
        addMetadata(storedEmailId, ColumnMetadata.named("STORED_EMAIL_ID").ofType(-5).withSize(19).notNull());
        addMetadata(storedTimestamp, ColumnMetadata.named("STORED_TIMESTAMP").ofType(93).withSize(23).withDigits(10).notNull());
    }

}

