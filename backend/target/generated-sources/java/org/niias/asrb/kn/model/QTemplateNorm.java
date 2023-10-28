package org.niias.asrb.kn.model;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QTemplateNorm is a Querydsl query type for TemplateNorm
 */
@Generated("com.querydsl.codegen.EmbeddableSerializer")
public class QTemplateNorm extends BeanPath<TemplateNorm> {

    private static final long serialVersionUID = -1155568846L;

    public static final QTemplateNorm templateNorm = new QTemplateNorm("templateNorm");

    public final StringPath aktAddon = createString("aktAddon");

    public final StringPath customPeriod = createString("customPeriod");

    public final NumberPath<Long> dnchId = createNumber("dnchId", Long.class);

    public final StringPath docs = createString("docs");

    public final StringPath name = createString("name");

    public final StringPath otherAddon = createString("otherAddon");

    public final EnumPath<Period> period = createEnum("period", Period.class);

    public final StringPath recordingAddon = createString("recordingAddon");

    public QTemplateNorm(String variable) {
        super(TemplateNorm.class, forVariable(variable));
    }

    public QTemplateNorm(Path<? extends TemplateNorm> path) {
        super(path.getType(), path.getMetadata());
    }

    public QTemplateNorm(PathMetadata metadata) {
        super(TemplateNorm.class, metadata);
    }

}

