/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package repsaj.airstreamer.server.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author jasper
 */
public class MapObjectUtil {

    private MapObjectUtil() {
    }

    public static <T extends MapObject> List<Map<String, Object>> listToDbMap(List<T> list) {
        if (!list.isEmpty()) {
            List<Map<String, Object>> tmpList = new ArrayList<Map<String, Object>>();
            for (T mapObject : list) {
                tmpList.add(mapObject.toMap());
            }
            return tmpList;
        } else {
            return null;
        }

    }

    public static <T extends MapObject> List<T> dbMapToList(Object listMap, Class<T> clazz) {

        List<Map<String, Object>> tmpList = (List<Map<String, Object>>) listMap;
        ArrayList<T> list = new ArrayList<T>();

        if (tmpList != null) {
            for (Map<String, Object> submap : tmpList) {
                try {
                    T tmpObj = (T) clazz.newInstance();
                    tmpObj.fromMap(submap);
                    list.add(tmpObj);
                } catch (Exception e) {
                    throw new IllegalArgumentException("Unable to instance object");
                }
            }
        }
        return list;
    }

    public static <T extends MapObject> Map<String, Object> mapToDbMap(Map<String, T> map) {
        if (!map.isEmpty()) {
            Map<String, Object> tmpMap = new HashMap<String, Object>();
            for (String key : map.keySet()) {
                T obj = map.get(key);
                tmpMap.put(key, obj.toMap());
            }
            return tmpMap;
        } else {
            return null;
        }

    }

    public static <T extends MapObject> Map<String, T> dbMapToMap(Object map, Class<T> clazz) {

        Map<String, T> retMap = new HashMap<String, T>();
        Map<String, Object> objMap = (Map<String, Object>) map;


        if (objMap != null) {
            for (String key : objMap.keySet()) {
                try {
                    Map<String, Object> submap = (Map<String, Object>) objMap.get(key);
                    T tmpObj = (T) clazz.newInstance();
                    tmpObj.fromMap(submap);
                    retMap.put(key, tmpObj);
                } catch (Exception e) {
                    throw new IllegalArgumentException("Unable to instance object");
                }
            }

        }
        return retMap;
    }
}
