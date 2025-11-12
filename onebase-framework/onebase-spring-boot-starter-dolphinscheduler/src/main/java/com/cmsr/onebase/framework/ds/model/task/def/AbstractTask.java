package com.cmsr.onebase.framework.ds.model.task.def;

import com.cmsr.onebase.framework.common.util.json.JsonUtils;
import com.cmsr.onebase.framework.ds.model.common.Parameter;
import com.cmsr.onebase.framework.ds.model.common.TaskResource;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Accessors(chain = true)
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "taskType",
        visible = true
)
@JsonSubTypes({
        @JsonSubTypes.Type(name = "HTTP", value = HttpTask.class),
        @JsonSubTypes.Type(name = "FLINK", value = FlinkTask.class)
})
public abstract class AbstractTask {
    protected List<TaskResource> resourceList = new ArrayList<>();
    private List<Parameter> localParams = new ArrayList<>();

    @Override
    public String toString() {
        return JsonUtils.toJsonString(this);
    }

    public void withResource(String resourceName) {
        TaskResource taskResource = new TaskResource();
        taskResource.setResourceName(resourceName);
        this.resourceList.add(taskResource);
    }

    public abstract String grantTaskType();
}
