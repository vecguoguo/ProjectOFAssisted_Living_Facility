package com.neusoft.yyzx.mapping;

import java.util.HashMap;
import java.util.Map;

public class MappingFactory {

    private static MappingFactory mappingFactory = null;
    private Map<String, EntityMapping> maps = new HashMap();
    public static final String USER_MAPPING = "userMapping";
    public static final String BED_MAPPING = "bedMapping";
    public static final String BED_DETAILS_MAPPING = "bedDetailsMapping";
    public static final String CUSTOMER_MAPPING = "customerMapping";
    public static final String OUTWARD_MAPPING = "outwardMapping";
    public static final String BACKDOWN_MAPPING = "backdownMapping";
    public static final String NURSE_CONTENT_MAPPING = "nurseContentMapping";
    public static final String NURSE_LEVEL_MAPPING = "nurseLevelMapping";
    public static final String NURSE_LEVEL_ITEM_MAPPING = "nurseLevelItemMapping";
    public static final String CUSTOMER_NURSE_ITEM_MAPPING = "customerNurseItemMapping";
    public static final String NURSE_RECORD_MAPPING = "nurseRecordMapping";

    private MappingFactory() {
    }

    public static MappingFactory getInstance() {
        if (mappingFactory == null) {
            mappingFactory = new MappingFactory();
            //向Map集合中添加元素
            mappingFactory.maps.put(USER_MAPPING, new UserMapping());
            mappingFactory.maps.put(BED_MAPPING, new BedMapping());
            mappingFactory.maps.put(BED_DETAILS_MAPPING, new BedDetailsMapping());
            mappingFactory.maps.put(CUSTOMER_MAPPING, new CustomerMapping());
            mappingFactory.maps.put(OUTWARD_MAPPING, new OutwardMapping());
            mappingFactory.maps.put(BACKDOWN_MAPPING, new BackdownMapping());
            mappingFactory.maps.put(NURSE_CONTENT_MAPPING, new NurseContentMapping());
            mappingFactory.maps.put(NURSE_LEVEL_MAPPING, new NurseLevelMapping());
            mappingFactory.maps.put(NURSE_LEVEL_ITEM_MAPPING, new NurseLevelItemMapping());
            mappingFactory.maps.put(CUSTOMER_NURSE_ITEM_MAPPING, new CustomerNurseItemMapping());
            mappingFactory.maps.put(NURSE_RECORD_MAPPING, new NurseRecordMapping());
            //mappingFactory.maps.put("xxxMapping",new xxxMapping());
        }
        return mappingFactory;
    }

    /**
     * 从集合中根据key获取元素
     */
    public EntityMapping getMapping(String key) {
        return maps.get(key);
    }
}
