package com.example.hemahotel.service;

import com.example.hemahotel.utils.ResponseUtils;

public interface ForumService {

    //发帖
    public ResponseUtils add(Long userId, String title, String content);

    public ResponseUtils getAll(Integer pageIndex, Integer pageSize, String sortProperty);

    public ResponseUtils findById(Long Id);

    public ResponseUtils getForumNum();
}
