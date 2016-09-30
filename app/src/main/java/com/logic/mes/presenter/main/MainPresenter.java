package com.logic.mes.presenter.main;

import com.logic.mes.entity.base.UserInfo;
import com.logic.mes.fragment.BaseTagFragment;
import com.logic.mes.fragment.FragmentFactory;
import java.util.List;

public class MainPresenter {
    private IMain iMain;
    public MainPresenter(IMain iMain){
       this.iMain = iMain;
    }
    public void getAuthTags(UserInfo userInfo){
        List<BaseTagFragment> tags = FragmentFactory.getFragmentsByProcesses(userInfo.getProduceDef());
        iMain.setTags(tags);
    }
}
