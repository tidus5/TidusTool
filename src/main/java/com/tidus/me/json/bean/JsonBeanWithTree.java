package com.tidus.me.json.bean;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Maps;

import java.util.Date;
import java.util.TreeMap;
import java.util.TreeSet;

import static com.tidus.me.json.util.FastJsonUtil.SerializeFeature;


public class JsonBeanWithTree extends BaseJsonBean {
    private static final Date date = new Date();


    public TreeSet<Integer> integerSet = new TreeSet<>();
    public TreeMap<String, Integer> stringIntegerMap = Maps.newTreeMap();
//    public StringBuffer sb = new StringBuffer();

    public transient int testTransient = 1;

    public JsonBeanWithTree() {


    }

    public static JsonBeanWithTree getTestBean() {
        JsonBeanWithTree bean = new JsonBeanWithTree();
        return bean;
    }

    public int getTestTransient() {
        return testTransient;
    }

    public void setTestTransient(int testTransient) {
        this.testTransient = testTransient;
    }

    @JsonIgnore
    public int getIsOpen() {
        return 0;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj instanceof JsonBeanWithTree) {
            return obj.toString().equals(this.toString());
        }
        return super.equals(obj);
    }

    @Override
    public String toString() {
        //https://www.cnblogs.com/larva-zhh/p/11544317.html
        //map的字段按字母顺序排序。 默认是关闭的（建议关闭，开启会影响性能）
        //输出字符串时，开启map排序。这里开启为了统一输出格式
        return JSON.toJSONString(this, SerializeFeature, SerializerFeature.MapSortField);
    }
}
