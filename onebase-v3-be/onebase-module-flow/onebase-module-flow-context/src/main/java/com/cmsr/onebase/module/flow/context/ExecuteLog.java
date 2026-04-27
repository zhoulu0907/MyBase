package com.cmsr.onebase.module.flow.context;

import com.google.common.base.Stopwatch;
import lombok.Data;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

/**
 * @Author：huangjie
 * @Date：2025/12/15 14:28
 */
@Data
public class ExecuteLog {

    private Stopwatch stopwatch;

    private List<String> logs;

    public ExecuteLog() {
        this.stopwatch = Stopwatch.createStarted();
        this.logs = new CopyOnWriteArrayList<>();
    }

    public void addLog(String msg) {
        String log = String.format("[%d] %s", stopwatch.elapsed(TimeUnit.MILLISECONDS), msg);
        logs.add(log);
    }
}
