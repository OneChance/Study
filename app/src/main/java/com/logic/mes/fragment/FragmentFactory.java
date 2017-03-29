package com.logic.mes.fragment;


import com.logic.mes.entity.base.ProduceDef;

import java.util.ArrayList;
import java.util.List;

public class FragmentFactory {

    private PbjFragment pbjFragment;
    private PbFragment pbFragment;
    private YbFragment ybFragment;
    private QxFragment qxFragment;
    private QgsfhFragment qgsfhFragment;
    private ZxFragment zxFragment;
    private ZtFragment ztFragment;
    private RkFragment rkFragment;
    private CkFragment ckFragment;
    private JbFragment jbFragment;
    private DbFragment dbFragment;
    private WxFragment wxFragment;
    private ByFragment byFragment;

    private static FragmentFactory fragmentFactory;

    public static FragmentFactory getInstance() {
        if (fragmentFactory == null) {
            fragmentFactory = new FragmentFactory();
        }
        return fragmentFactory;
    }

    public List<BaseTagFragment> getFragmentsByProcesses(List<ProduceDef> produceDefs) {
        List<BaseTagFragment> tags = new ArrayList<>();

        for (ProduceDef p : produceDefs) {
            tags.add(createFragment(p.getProducecode()));
        }

        return tags;
    }

    private BaseTagFragment createFragment(String code) {

        switch (code) {
            case "pbj":
                return (pbjFragment == null ? pbjFragment = new PbjFragment() : pbjFragment);
            case "pb":
                return (pbFragment == null ? pbFragment = new PbFragment() : pbFragment);
            case "yb":
                return (ybFragment == null ? ybFragment = new YbFragment() : ybFragment);
            case "qx":
                return (qxFragment == null ? qxFragment = new QxFragment() : qxFragment);
            case "qgs":
                return (qgsfhFragment == null ? qgsfhFragment = new QgsfhFragment() : qgsfhFragment);
            case "zx":
                return (zxFragment == null ? zxFragment = new ZxFragment() : zxFragment);
            case "zt":
                return (ztFragment == null ? ztFragment = new ZtFragment() : ztFragment);
            case "rk":
                return (rkFragment == null ? rkFragment = new RkFragment() : rkFragment);
            case "ck":
                return (ckFragment == null ? ckFragment = new CkFragment() : ckFragment);
            case "jb":
                return (jbFragment == null ? jbFragment = new JbFragment() : jbFragment);
            case "db":
                return (dbFragment == null ? dbFragment = new DbFragment() : dbFragment);
            case "wx":
                return (wxFragment == null ? wxFragment = new WxFragment() : wxFragment);
            case "by":
                return (byFragment == null ? byFragment = new ByFragment() : byFragment);
            default:
                return new BaseTagFragment();
        }

    }
}
