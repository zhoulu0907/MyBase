package com.cmsr.onebase.module.bpm.core.warmflow.anyline.dataobject;

import com.cmsr.onebase.framework.data.base.BaseDO;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.dromara.warm.flow.core.entity.Definition;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * WarmFlow 基础实体对象（V2）
 *
 * 用于承接 WarmFlow 实体与 OneBase 基础实体 {@link BaseDO} 之间的字段差异，
 * 在不改变上层业务使用方式的前提下，屏蔽两套模型在主键与时间字段上的不一致。
 *
 * 设计要点：
 * - 与 {@link BaseDO} 保持继承关系，复用通用审计等基础字段能力；
 * - 在本类中“显式声明”并覆盖 id、createTime、updateTime 字段，
 *   以适配 WarmFlow 对 get/set 签名与语义的要求；
 * - 将差异集中在该抽象层，避免在各具体 DO 中重复处理适配逻辑。
 *
 * 使用建议：
 * - 仅当实体需要直接实现/适配 WarmFlow 接口且与 {@link BaseDO} 的字段契约不一致时，
 *   才应继承本类；
 * - 具体业务实体在此基础上继续声明各自专有字段与映射注解。
 *
 * 注意：本类仅承载“差异收敛”的职责，不引入行为逻辑。
 *
 * @author liyang
 * @date 2025-09-30
 */
@Data
public class WarmFlowBaseDOV2 extends BaseDO {
    /**
     * 主键ID
     */
    private Long id;

    /**
     *
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
