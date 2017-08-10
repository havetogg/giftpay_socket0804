package org.jumutang.giftpay.dao;

import org.jumutang.giftpay.entity.ContentModel;

import java.util.List;

/**
 * Created by RuanYJ on 2017/6/30.
 */
public interface IContentModelDao {
    List<ContentModel> queryContentList(ContentModel contentModel);
    int addContentModel(ContentModel contentModel);
}
