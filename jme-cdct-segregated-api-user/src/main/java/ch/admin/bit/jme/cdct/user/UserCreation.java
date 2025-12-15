package ch.admin.bit.jme.cdct.user;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@NoArgsConstructor(force = true)
public class UserCreation {

    @NonNull
    private String name;


}
