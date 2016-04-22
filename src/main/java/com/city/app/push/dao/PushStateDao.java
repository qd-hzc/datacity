package com.city.app.push.dao;

import com.city.app.push.entity.PushState;
import com.city.common.dao.BaseDao;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by zhoutao on 2016/4/19.
 */
@Repository
public class PushStateDao extends BaseDao<PushState> {

    public void save(PushState pushState){
        super.insert(pushState, false);
    }

    public PushState findByAppUserId(Integer appUserId){
        PushState resut = null;

        String hql = "from PushState ps where ps.appUserId="+appUserId;
        List<PushState> list = super.queryByHQL(hql);
        if(list.size() > 0)
            resut = list.get(0);

        return  resut;
    }

    //将对应推送设置为已推送
    public void updateState(Integer appUserId){
        String hql = "update PushState ps set ps.pushState = false where ps.appUserId="+appUserId;
        super.updateByHQL(hql);
    }

    //重置
    public void reset(){
        String hql = "update PushState ps set ps.pushState = true";
        super.updateByHQL(hql);
    }

}
