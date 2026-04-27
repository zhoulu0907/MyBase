package com.cmsr.onebase.framework.uid;

import com.cmsr.onebase.framework.uid.impl.CachedUidGenerator;
import com.cmsr.onebase.framework.uid.impl.DefaultUidGenerator;
import com.cmsr.onebase.framework.uid.worker.DisposableWorkerIdAssigner;
import com.cmsr.onebase.framework.uid.worker.dao.WorkerNodeDAO;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;

/**
 * @Author：huangjie
 * @Date：2025/8/15 11:27
 */
@AutoConfiguration
public class UidAutoConfiguration {

    @Bean
    public DisposableWorkerIdAssigner disposableWorkerIdAssigner(DataSource dataSource) {
        DisposableWorkerIdAssigner workerIdAssigner = new DisposableWorkerIdAssigner();
        WorkerNodeDAO workerNodeDAO = new WorkerNodeDAO(dataSource);
        workerIdAssigner.setWorkerNodeDAO(workerNodeDAO);
        return workerIdAssigner;
    }

    @Bean("uidGenerator")
    public UidGenerator uidGenerator(DisposableWorkerIdAssigner workerIdAssigner) throws Exception {
        DefaultUidGenerator uidGenerator = new DefaultUidGenerator();
        uidGenerator.setEpochStr("2025-08-15");
        // 1 + timeBits + workerBits + seqBits = 64
        // timeBits：时间位数，可支持的时间范围 17 年
        uidGenerator.setTimeBits(29);
        // workerBits：工作节点位数，节点数 * 重启次数 = 52万
        uidGenerator.setWorkerBits(19);
        // seqBits：序列号位数，单节点并发量 32,768
        uidGenerator.setSeqBits(15);
        //RingBuffer size扩容参数, 可提高UID生成的吞吐量
        //uidGenerator.setBoostPower(3);
        //RingBuffer填充时机, 在Schedule线程中, 周期性检查填充
        //uidGenerator.setScheduleInterval(60);
        uidGenerator.setWorkerIdAssigner(workerIdAssigner);
        return uidGenerator;
    }
}
