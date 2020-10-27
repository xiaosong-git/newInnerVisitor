package com.xiaosong.common.web.post;

import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.ehcache.CacheKit;
import com.xiaosong.constant.TableList;
import com.xiaosong.model.VParams;
import com.xiaosong.model.VPost;
import com.xiaosong.param.ParamService;

/**
 * @program: xiaosong
 * @description:
 * @author: cwf
 * @create: 2019-12-29 20:20
 **/
public class PostService {
    public static final PostService me = new PostService();

    public Page<VPost> findList(int currentPage, int pageSize){
        return VPost.dao.paginate(currentPage, pageSize, "select *", "from v_post");
    }


}
