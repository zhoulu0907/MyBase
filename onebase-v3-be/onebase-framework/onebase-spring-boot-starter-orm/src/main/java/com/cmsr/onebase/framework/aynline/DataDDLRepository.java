package com.cmsr.onebase.framework.aynline;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.anyline.data.Run;
import org.anyline.metadata.Constraint;
import org.anyline.metadata.Table;
import org.anyline.service.AnylineService;

import java.util.List;

/**
 * DataRepository - JPA风格的CRUD操作工具类
 * <p>
 * 提供标准的CRUD操作接口，遵循Spring Data JPA的设计模式
 * 支持实体类的增删改查操作，包含分页、排序、条件查询等功能
 *
 * @author mickey
 */
@Slf4j
public class DataDDLRepository {

    @Resource
    private AnylineService<?> anylineService;

    public DataDDLRepository() {
    }


    /**
     * 创建表
     *
     * @param clazz   实体类
     * @param reset   是否删除已存在的表
     * @param execute 是否执行DDL
     * @throws Exception 异常
     */
    public void createTable(Class<?> clazz, boolean reset, boolean execute) throws Exception {
        if (anylineService == null) {
            throw new Exception("[DataRepository.createTable] AnylineService is null.");
        }

        log.info("CreateTable: {}", clazz);
        Table<?> table = Table.from(clazz);

        if (anylineService.metadata().exists(table) && reset) {
            log.info("DropTable: {}", clazz);
            anylineService.ddl().drop(table);
        }

        table.execute(execute);
        anylineService.ddl().create(table);

        // 处理唯一约束
        if (clazz.isAnnotationPresent(jakarta.persistence.Table.class)) {
            jakarta.persistence.Table tableAnnotation =
                    (jakarta.persistence.Table) clazz.getAnnotation(jakarta.persistence.Table.class);
            jakarta.persistence.UniqueConstraint[] uniqueConstraints = tableAnnotation.uniqueConstraints();

            for (jakarta.persistence.UniqueConstraint constraint : uniqueConstraints) {
                log.info("表名: {} 约束名称: {} 约束列名 {}",
                        table.getName(), constraint.name(), constraint.columnNames());

                Constraint<?> uk = new Constraint<>(table, constraint.name())
                        .setType(Constraint.TYPE.UNIQUE);

                for (String column : constraint.columnNames()) {
                    log.info(column);
                    uk.addColumn(column);
                }

                anylineService.ddl().add(uk);
            }
        }

        List<Run> ddls = (List<Run>) table.runs();
        for (Run ddl : ddls) {
            log.info(ddl.getFinalUpdate());
        }
    }


}
