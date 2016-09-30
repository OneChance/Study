package com.logic.mes.fragment;


import com.logic.mes.entity.base.ProduceDef;

import java.util.ArrayList;
import java.util.List;

public class FragmentFactory {

    private static ZbFragment zbFragment;
    private static PbFragment pbFragment;
    private static TjFragment tjFragment;
    private static FxFragment fxFragment;

    public static List<BaseTagFragment> getFragmentsByProcesses(List<ProduceDef> produceDefs){
        List<BaseTagFragment> tags = new ArrayList<>();

        for(ProduceDef p: produceDefs){
            tags.add(createFragment(p.getId()));
        }

        return tags;
    }

    public static BaseTagFragment createFragment(int pid){

        switch (pid){
            case 4:return (zbFragment==null?zbFragment = new ZbFragment():zbFragment);
            case 3:return (pbFragment==null?pbFragment = new PbFragment():pbFragment);
            case 7:return (tjFragment==null?tjFragment = new TjFragment():tjFragment);
            case 11:return (fxFragment==null?fxFragment = new FxFragment():fxFragment);
            default:return new BaseTagFragment();
        }

    }
}
