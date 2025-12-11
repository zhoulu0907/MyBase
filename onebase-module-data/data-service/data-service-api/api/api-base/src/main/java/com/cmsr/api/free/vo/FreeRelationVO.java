package com.cmsr.api.free.vo;

import com.cmsr.api.free.dto.FreeRelationCategory;
import com.cmsr.api.free.dto.FreeRelationLink;
import com.cmsr.api.free.dto.FreeRelationNode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class FreeRelationVO implements Serializable {
    @Serial
    private static final long serialVersionUID = 7087187548660162237L;

    private List<FreeRelationCategory> categories;

    private List<FreeRelationLink> links;

    private List<FreeRelationNode> nodes;

    private int maxNodeSize;
}
