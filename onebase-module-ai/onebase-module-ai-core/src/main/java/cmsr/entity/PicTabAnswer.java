package cmsr.entity;

import lombok.Data;

@Data
public class PicTabAnswer {

    private String chartKey;

    private String datasetId;

    private String datasetName;

    private String dimension;

    private String metrics;

    private String reason;
}
