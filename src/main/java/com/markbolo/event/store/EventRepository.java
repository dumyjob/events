package com.markbolo.event.store;


import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends Mapper<EventBean> {

    void release(@Param("oldStatus") EventStatus originStatus, @Param("newStatus") EventStatus newStatus);

    List<EventBean> selectWaiting(@Param("time") LocalDateTime time, @Param("status") EventStatus status);
}
