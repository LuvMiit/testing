package org.niias.asrb.kn.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.niias.asrb.model.Railway;

@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

    @Getter
    @Setter
    public String fio;
    @Setter
    @Getter
    public String subdiv;
    @Getter
    @Setter
    public String rail;

    @Override
    public String toString() {
        return "UserDTO{" +
                ", fio='" + fio + '\'' +
                ", subdiv='" + subdiv + '\'' +
                ", rail=" + rail +
                '}';
    }
}
