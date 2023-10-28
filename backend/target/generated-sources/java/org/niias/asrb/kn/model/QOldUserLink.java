package org.niias.asrb.kn.model;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QOldUserLink is a Querydsl query type for OldUserLink
 */
@Generated("com.querydsl.codegen.EmbeddableSerializer")
public class QOldUserLink extends BeanPath<OldUserLink> {

    private static final long serialVersionUID = 1257273936L;

    public static final QOldUserLink oldUserLink = new QOldUserLink("oldUserLink");

    public final NumberPath<Integer> oldUserDorKod = createNumber("oldUserDorKod", Integer.class);

    public final NumberPath<Integer> oldUserId = createNumber("oldUserId", Integer.class);

    public final StringPath oldUserName = createString("oldUserName");

    public QOldUserLink(String variable) {
        super(OldUserLink.class, forVariable(variable));
    }

    public QOldUserLink(Path<? extends OldUserLink> path) {
        super(path.getType(), path.getMetadata());
    }

    public QOldUserLink(PathMetadata metadata) {
        super(OldUserLink.class, metadata);
    }

}

