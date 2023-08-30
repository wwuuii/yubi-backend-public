package com.yuxian.yubi.model.entity;


import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

/**
 * 图表分析溢出任务表
 * @TableName chart_analyse_overflow
 */
@Data
public class ChartAnalyseOverflow implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 图表Id
     */
    private Long chartId;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 是否删除
     */
    private Integer isDelete;

    /**
     * 分析问题
     */
    private String question;

    private static final long serialVersionUID = 1L;
}