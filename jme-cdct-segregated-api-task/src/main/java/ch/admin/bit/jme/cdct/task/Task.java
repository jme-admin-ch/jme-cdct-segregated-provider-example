package ch.admin.bit.jme.cdct.task;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Data
@NoArgsConstructor(force = true)
public class Task {

    private String id;

    private String title;

    private String content;

    private ZonedDateTime createdAt;

    private String tag;

}
