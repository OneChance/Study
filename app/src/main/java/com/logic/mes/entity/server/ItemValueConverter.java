package com.logic.mes.entity.server;

import com.logic.mes.entity.process.ProcessBase;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class ItemValueConverter {

    static char spliter = 30;

    /***
     *
     * @param data 将提交数据转换为服务器规定的格式
     * @return
     */
    public static List<ProcessItem> convert(ProcessBase data) {
        List<ProcessItem> items = new ArrayList<>();


        try {

            Class c = data.getClass();

            String headkV = "";

            List<Object> detail = null;

            String itemKey = "";

            for (Field field : c.getDeclaredFields()) {

                Method getMethod = null;

                try {
                    getMethod = c.getMethod("get" + field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1));
                } catch (Exception e) {
                    continue;
                }

                if (field.getType() == List.class) {
                    //取出集合数据
                    detail = (List<Object>) getMethod.invoke(data);
                } else {
                    if (field.getAnnotation(ItemCol.class) != null) {
                        headkV = headkV + "," + spliter + field.getAnnotation(ItemCol.class).col() + "=" + getMethod.invoke(data);
                    }

                    if (field.getAnnotation(ItemKey.class) != null) {
                        itemKey = getMethod.invoke(data).toString();
                    }
                }
            }

            if (headkV.startsWith(",")) {
                headkV = headkV.substring(2);
            }

            if (detail == null) {
                ProcessItem item = new ProcessItem();
                item.setItemKey(itemKey);
                item.setItemValue(headkV);
                items.add(item);
            } else {
                for (Object o : detail) {
                    ProcessItem item = new ProcessItem();

                    String itemKV = "";
                    Class ic = o.getClass();
                    for (Field field : ic.getDeclaredFields()) {
                        Method getMethod = null;

                        try {
                            getMethod = ic.getMethod("get" + field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1));
                        } catch (Exception e) {
                            continue;
                        }

                        if (field.getAnnotation(ItemCol.class) != null) {
                            itemKV = itemKV + "," + spliter + field.getAnnotation(ItemCol.class).col() + "=" + getMethod.invoke(o);
                        }

                        if (field.getAnnotation(ItemKey.class) != null) {
                            itemKey = getMethod.invoke(o).toString();
                        }
                    }

                    if (!headkV.equals("")) {
                        itemKV = itemKV + "," + spliter + headkV;
                    }


                    if (itemKV.startsWith(",")) {
                        itemKV = itemKV.substring(2);
                    }

                    item.setItemKey(itemKey);
                    item.setItemValue(itemKV);
                    items.add(item);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return items;
    }


    public static void main(String[] args) throws Exception {

    }
}
