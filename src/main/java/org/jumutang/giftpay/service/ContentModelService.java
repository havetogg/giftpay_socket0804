package org.jumutang.giftpay.service;

import org.jumutang.giftpay.dao.IContentModelDao;
import org.jumutang.giftpay.entity.ContentModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by RuanYJ on 2017/6/30.
 */
@Service
public class ContentModelService {
    @Autowired
    private IContentModelDao contentModelDao;


    public List<ContentModel> queryContentList(ContentModel contentModel){
        return this.contentModelDao.queryContentList(contentModel);
    }
    public int addContentModel(ContentModel contentModel){
        return this.contentModelDao.addContentModel(contentModel);
    }
}
