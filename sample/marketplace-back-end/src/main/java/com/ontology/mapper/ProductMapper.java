package com.ontology.mapper;

import com.ontology.entity.Product;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;


@Component
public interface ProductMapper extends Mapper<Product> {
    List<Product> queryProduct(@Param("startPage") Integer startPage, @Param("pageSize") Integer pageSize, @Param("tag") String tag);

    int queryProductCount(@Param("tag") String tag);

    Product selectByDataId(@Param("dataId") String dataId);

    List<Product> selectSelfProduct(@Param("ontid") String ontid);

    List<Product> selectUpload();

    List<Product> selectAuth();

}
