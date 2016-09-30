package com.logic.mes.fragment;


import com.logic.mes.entity.process.Process;

import java.util.ArrayList;
import java.util.List;

public class FragmentFactory {

    private static ZbFragment zbFragment;
    private static PbFragment pbFragment;
    private static TjFragment tjFragment;
    private static FxFragment fxFragment;

    public static List<BaseTagFragment> getFragmentsByProcesses(List<Process> processes){
        List<BaseTagFragment> tags = new ArrayList<>();

        for(Process p:processes){
            tags.add(createFragment(p.getId()));
        }
        return tags;
    }

    public static BaseTagFragment createFragment(int pid){

        switch (pid){
            case 0:return (zbFragment==null?zbFragment = new ZbFragment():zbFragment);
            case 1:return (pbFragment==null?pbFragment = new PbFragment():pbFragment);
            case 3:return (tjFragment==null?tjFragment = new TjFragment():tjFragment);
            case 4:return (fxFragment==null?fxFragment = new FxFragment():fxFragment);
            default:return new BaseTagFragment();
        }

    }
}
