package pers.sharedFileSystem.entity;

import java.io.Serializable;

/**
 * 目录结点的扩展区间属性实体类
 */
public class IntervalProperty implements Serializable {
    /**
     * 区间左边沿
     */
    public String Min;
    /**
     * 区间右边沿
     */
    public String Max;
    /**
     * 满足该区间时，该目录结点存储的父存储目录编号
     */
    public String DirectoryNodeId;
    /**
     * 打印节点信息
     *
     * @param tabs
     *            缩进tab
     */
    public void print(String tabs) {
        System.out.println(tabs + "Min: " + Min);
        System.out.println(tabs + "Max: " + Max);
        System.out.println(tabs + "DirectoryNodeId: " + DirectoryNodeId);
    }
}