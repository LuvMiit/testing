package org.niias.asrb.kn.model;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QUserActionRef is a Querydsl query type for UserActionRef
 */
@Generated("com.querydsl.codegen.EmbeddableSerializer")
public class QUserActionRef extends BeanPath<UserActionRef> {

    private static final long serialVersionUID = 895257238L;

    public static final QUserActionRef userActionRef = new QUserActionRef("userActionRef");

    public final DatePath<java.time.LocalDate> date = createDate("date", java.time.LocalDate.class);

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final StringPath name = createString("name");

    public QUserActionRef(String variable) {
        super(UserActionRef.class, forVariable(variable));
    }

    public QUserActionRef(Path<? extends UserActionRef> path) {
        super(path.getType(), path.getMetadata());
    }

    public QUserActionRef(PathMetadata metadata) {
        super(UserActionRef.class, metadata);
    }

}

