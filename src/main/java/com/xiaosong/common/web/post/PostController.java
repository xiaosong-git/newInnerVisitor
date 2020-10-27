package com.xiaosong.common.web.post;

import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Page;
import com.xiaosong.model.VPost;
import com.xiaosong.param.ParamService;
import com.xiaosong.util.RetUtil;

/**
 * Created by CNL on 2020/10/22.
 */
public class PostController extends Controller {

    public PostService srv = PostService.me;

    public void findList() {
        int currentPage = getInt("currentPage");
        int pageSize = getInt("pageSize");
        Page<VPost> pagelist = srv.findList(currentPage,pageSize);
        renderJson(pagelist);
    }


    public void addPost() {
        String postName = get("postName");
        String postAuth = get("postAuth");
        VPost vPost = new VPost();
        vPost.setPostName(postName);
        vPost.setPostAuth(postAuth);

        boolean bool = vPost.save();
        if(bool) {
            renderJson(RetUtil.ok());
        }else {
            renderJson(RetUtil.fail());
        }
    }


    public void editPost() {
        Long id = getLong("id");
        String postName = get("postName");
        String postAuth = get("postAuth");
        VPost vPost = new VPost();
        vPost.setId(id);
        vPost.setPostName(postName);
        vPost.setPostAuth(postAuth);
        boolean bool = vPost.update();
        if(bool) {
            renderJson(RetUtil.ok());
        }else {
            renderJson(RetUtil.fail());
        }
    }


}
