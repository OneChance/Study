package com.logic.mes.fragment;


import com.logic.mes.entity.base.ProduceDef;

import java.util.ArrayList;
import java.util.List;

public class FragmentFactory {

    private static PbjFragment pbjFragment;
    //private static ZbFragment zbFragment;
    private static PbFragment pbFragment;
    //private static TjFragment tjFragment;
    //private static FxFragment fxFragment;
    private static YbFragment ybFragment;
    private static QxFragment qxFragment;
    private static QgsfhFragment qgsfhFragment;
    private static ZxFragment zxFragment;

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
            case 10:return (qgsfhFragment==null?qgsfhFragment = new QgsfhFragment():qgsfhFragment);
            case 14:return (zxFragment==null?zxFragment = new ZxFragment():zxFragment);
            default:return new BaseTagFragment();
        }

    }
}
