package org.niias.asrb.kn.model;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QTemplate is a Querydsl query type for Template
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QTemplate extends EntityPathBase<Template> {

    private static final long serialVersionUID = -926981546L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QTemplate template = new QTemplate("template");

    public final QUserActionRef agreed;

    public final QUserActionRef approved;

    public final QUserActionRef changed;

    public final StringPath comment = createString("comment");

    public final QUserActionRef created;

    public final NumberPath<Long> dnchId = createNumber("dnchId", Long.class);

    public final DatePath<java.time.LocalDate> fromDate = createDate("fromDate", java.time.LocalDate.class);

    public final NumberPath<Integer> fromRef = createNumber("fromRef", Integer.class);

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final EnumPath<PredLevel> level = createEnum("level", PredLevel.class);

    public final NumberPath<Integer> mainId = createNumber("mainId", Integer.class);

    public final ListPath<TemplateNorm, QTemplateNorm> norms = this.<TemplateNorm, QTemplateNorm>createList("norms", TemplateNorm.class, QTemplateNorm.class, PathInits.DIRECT2);

    public final StringPath postFullName = createString("postFullName");

    public final StringPath postName = createString("postName");

    public final EnumPath<Status> status = createEnum("status", Status.class);

    public final DatePath<java.time.LocalDate> toDate = createDate("toDate", java.time.LocalDate.class);

    public QTemplate(String variable) {
        this(Template.class, forVariable(variable), INITS);
    }

    public QTemplate(Path<? extends Template> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QTemplate(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QTemplate(PathMetadata metadata, PathInits inits) {
        this(Template.class, metadata, inits);
    }

    public QTemplate(Class<? extends Template> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.agreed = inits.isInitialized("agreed") ? new QUserActionRef(forProperty("agreed")) : null;
        this.approved = inits.isInitialized("approved") ? new QUserActionRef(forProperty("approved")) : null;
        this.changed = inits.isInitialized("changed") ? new QUserActionRef(forProperty("changed")) : null;
        this.created = inits.isInitialized("created") ? new QUserActionRef(forProperty("created")) : null;
    }

}

