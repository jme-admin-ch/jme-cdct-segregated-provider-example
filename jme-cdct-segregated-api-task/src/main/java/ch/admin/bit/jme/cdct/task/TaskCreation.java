package ch.admin.bit.jme.cdct.task;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@NoArgsConstructor(force = true)
public class TaskCreation {

    @NonNull
    private String title;

    @NonNull
    private String content;

    private String tag;

}
