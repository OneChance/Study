package com.logic.mes.fragment;


import com.logic.mes.entity.base.ProduceDef;

import java.util.ArrayList;
import java.util.List;

public class FragmentFactory {

    private static PbjFragment pbjFragment;
    //private static ZbFragment zbFragment;
    private static PbFragment pbFragment;
    //private static TjFragment tjFragment;
    private static FxFragment fxFragment;
    private static YbFragment ybFragment;
    private static QxFragment qxFragment;

    public static List<BaseTagFragment> getFragmentsByProcesses(List<ProduceDef> produceDefs){
        List<BaseTagFragment> tags = new ArrayList<>();

        for(ProduceDef p: produceDefs){
            tags.add(createFragment(p.getId()));
        }

        return tags;
    }

    public static BaseTagFragment createFragment(int pid){

        switch (pid){
            case 2:return (pbjFragment==null?pbjFragment = new PbjFragment():pbjFragment);
            case 3:return (pbFragment==null?pbFragment = new PbFragment():pbFragment);
            case 6:return (ybFragment==null?ybFragment = new YbFragment():ybFragment);
            case 9:return (qxFragment==null?qxFragment = new QxFragment():qxFragment);
            case 11:return (fxFragment==null?fxFragment = new FxFragment():fxFragment);
            default:return new BaseTagFragment();
        }

    }
}
