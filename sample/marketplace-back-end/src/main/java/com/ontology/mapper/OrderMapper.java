package com.ontology.mapper;

import com.ontology.entity.Order;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;


@Component
public interface OrderMapper extends Mapper<Order> {
    List<Order> selectByTxHash(@Param("txHash") String txHash);

    List<Order> querySelfOrder(@Param("ownerType") String ownerType, @Param("ontid") String ontid);

    List<Order> selectPurchase();

    List<Order> selectConfirm();

}
