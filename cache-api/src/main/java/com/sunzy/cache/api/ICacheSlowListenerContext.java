package com.sunzy.cache.api;

/**
 * 慢操作监听器上下文
 *
 * （1）耗时统计
 * （2）监听器
 *
 * @author binbin.hou
 * @since 0.0.6
 */
public interface ICacheSlowListenerContext {
    /**
     * 执行方法
     * @return
     */
    String methodName();

    /**
     * 参数信息
     * @return
     */
    Object[] params();

    /**
     * 执行结果
     * @return
     */
    Object result();

    /**
     * 开始时间
     * @return 时间
     * @since 0.0.9
     */
    long startTimeMills();

    /**
     * 结束时间
     * @return 结束时间
     * @since 0.0.9
     */
    long endTimeMills();

    /**
     * 消耗时间
     * @return 耗时
     * @since 0.0.9
     */
    long costTimeMills();

}
