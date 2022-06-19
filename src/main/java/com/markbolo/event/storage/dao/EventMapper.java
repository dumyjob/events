package com.markbolo.event.storage.dao;


import com.markbolo.event.storage.EventStatus;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.time.LocalDateTime;
import java.util.List;

public interface EventMapper extends Mapper<EventBean> {

    void release(@Param("oldStatus") EventStatus originStatus, @Param("newStatus") EventStatus newStatus);

    List<EventBean> getUnPublishEvents(@Param("time") LocalDateTime time, @Param("status") EventStatus status);
}
