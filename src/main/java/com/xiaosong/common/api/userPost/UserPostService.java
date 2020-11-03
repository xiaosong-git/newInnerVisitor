package com.xiaosong.common.api.userPost;

import com.jfinal.plugin.activerecord.Db;
import com.xiaosong.model.VUserPost;

public class UserPostService
{



    public static final UserPostService me = new UserPostService();

    /**
     * 验证岗位权限
     * @param userId
     * @param postId
     * @return
     */
    public boolean checkPostAuth(Long userId,Long postId)
    {
        VUserPost vUserPost = VUserPost.dao.findFirst("select * from v_user_post where userId =? and postId =?",userId,postId);
        return vUserPost!=null;
    }


    public void addPostUser(Long userId,String[] postIds)
    {
             Db.delete("delete from  v_user_post where userId = ?",userId);
            if(postIds!=null)
            {
                for(String postId : postIds) {
                    VUserPost vUserPost = new VUserPost();
                    vUserPost.setPostId(Long.parseLong(postId));
                    vUserPost.setUserId(userId);
                    vUserPost.save();
                }
            }
    }

}
