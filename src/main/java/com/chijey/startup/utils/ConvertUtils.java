package com.chijey.startup.utils;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.service.spi.ServiceException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.JpaSort;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.*;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ConvertUtils {

    private static final Pattern linePattern = Pattern.compile("_(\\w)");
    private static final Pattern humpPattern = Pattern.compile("[A-Z]");
    private static final Pattern PASS_THROUGH_PATTERN = Pattern.compile("^[A-Fa-f0-9]+$");
    private static final Integer PASS_THROUGH_MAX_LENGTH = 1000;

    /**
     * 分页参数转换成Pageable对象
     *  要这么写，好尴尬
     *  Sort sort = new Sort(Sort.Direction.DESC, "description").and(new Sort(Sort.Direction.ASC, "id"));
     *  备注：关联表排序可在此修改
     * @param page
     * @param size
     * @param sorts
     * @return
     */
    public static Pageable pagingConvert(Integer page, Integer size, String sorts) {
        List<Sort.Order> orders = Arrays.stream(sorts.split(",")).map(s -> {
            String[] split = s.split(": ");
            return split[1].toUpperCase().equals(Sort.Direction.ASC.name()) ? Sort.Order.asc(split[0]) : Sort.Order.desc(split[0]);
        }).collect(Collectors.toList());

        return PageRequest.of(page, size, Sort.by(orders));
    }


    /**
     * pageable分页查询排序orders
     * @param pageable
     * @param root
     * @param query
     * @param cb
     */
    public static void pageableOrder(Pageable pageable, Root root, CriteriaQuery query, CriteriaBuilder cb) {
        List<javax.persistence.criteria.Order> orders = new ArrayList<>();

        Iterator<Sort.Order> iterator = pageable.getSort().iterator();
        if(iterator.hasNext()){
            org.springframework.data.domain.Sort.Order next = iterator.next();
            String property = next.getProperty();
            String direction = next.getDirection().name();

            orders.add(direction.equals(Sort.Direction.ASC.name()) ? cb.asc(root.get(property)) :cb.asc(root.get(property)));
        }

        query.orderBy(orders);
    }

    /**
     * 获取Sort
     * @param sorts (例 createTime: desc)
     * @return
     */
    public static Sort domainSort(String sorts) {
        List<Sort.Order> orders = Arrays.stream(sorts.split(",")).map(s -> {
            String[] split = s.split(": ");
            return split[1].toUpperCase().equals(Sort.Direction.ASC.name()) ? Sort.Order.asc(split[0]) : Sort.Order.desc(split[0]);
        }).collect(Collectors.toList());

        return Sort.by(orders);
    }

    /**
     * JpaSort
     * @param sorts
     * @return
     */
    public static JpaSort jpaSort(String sorts) {
        JpaSort jpaSort = null;
        String[] orders = sorts.split(",");

        StringBuffer sb = new StringBuffer();
        for(int i=0; i<orders.length; i++){
            sb.setLength(0);
            String property = orders[i].split(": ")[0];
            property = property.contains(".") ? sb.append(property).insert(property.indexOf("."), "->'$").append("'").toString() : property;
            String direction = orders[i].split(": ")[1].toUpperCase();

            jpaSort = null == jpaSort ? JpaSort.unsafe(Sort.Direction.fromString(direction), property) : jpaSort.andUnsafe(Sort.Direction.fromString(direction), property);
        }

        return jpaSort;
    }

    /**
     * sql orderby
     * @param sorts
     * @return
     */
    public static String sqlOrderBy(String sorts) {
        if(StringUtils.isEmpty(sorts)){
            return "";
        }

        StringBuffer orderBy = new StringBuffer(" order by ");

        StringBuilder sb = new StringBuilder();
        String orders = Arrays.stream(sorts.split(",")).map(s -> {
            sb.setLength(0);
            String[] split = s.split(": ");

            String property = split[0];
            if (property.contains(".")) {
                property = String.join(".", humpToLine(property.split("\\.")[0]), property.split("\\.")[1]);
                property = sb.append(property).insert(property.indexOf("."), "->'$").append("'").toString();
            }else{
                property = humpToLine(property);
            }

            return String.join(" ", property, split[1]);
        }).collect(Collectors.joining(","));

        return orderBy.append(orders).toString();
    }

    /**
     * jpa orderby
     * @param sorts
     * @return
     */
    public static String jpaOrderBy(String sorts) {
        if(StringUtils.isEmpty(sorts)){
            return "";
        }
        StringBuffer orderBy = new StringBuffer(" order by ");


        StringBuilder sb = new StringBuilder();
        String orders = Arrays.stream(sorts.split(",")).map(s -> {
            sb.setLength(0);
            String[] split = s.split(": ");
            String property = split[0].contains(".") ? sb.append(split[0]).insert(split[0].indexOf("."), "->'$").append("'").toString() : split[0];

            return String.join(" ", property, split[1]);
        }).collect(Collectors.joining(","));

        return orderBy.append(orders).toString();
    }

    public static String limit(long offset, int pageSize) {
        return " limit " + offset + ", " + pageSize;
    }

    /**
     * 下划线转驼峰
     * @param str
     * @return
     */
    public static String lineToHump(String str) {
        str = str.toLowerCase();
        Matcher matcher = linePattern.matcher(str);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, matcher.group(1).toUpperCase());
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    /**
     * 驼峰转下划线(简单写法，效率低于{@link #humpToLine2(String)})
     * @param str
     * @return
     */
    public static String humpToLine(String str) {
        return str.replaceAll("[A-Z]", "_$0").toLowerCase();
    }

    /**
     * 驼峰转下划线,效率比humpToLine高
     * @param str
     * @return
     */
    public static String humpToLine2(String str) {
        Matcher matcher = humpPattern.matcher(str);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, "_" + matcher.group(0).toLowerCase());
        }

        matcher.appendTail(sb);
        return sb.toString();
    }


    /**
     * 断言异常
     * @param b
     * @param exceptionSupplier
     * @param <E>
     * @throws E
     */
    public static <E extends ServiceException> void assertThrow(boolean b, Supplier<? extends E> exceptionSupplier) throws E {
        if (b) {throw exceptionSupplier.get();}
    }

    /**
     * Object深拷贝 注意需要对泛型类进行序列化(实现Serializable)
     * @param src
     * @param <T>
     * @return
     * @throws Exception
     */
    public static <T> T deepCopy(T src) {
        try {
            ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(byteOut);
            out.writeObject(src);

            ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
            ObjectInputStream inStream = new ObjectInputStream(byteIn);
            T destList = (T) inStream.readObject();

            return destList;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 字符串左边补齐0
     * @param s
     * @param length
     * @return
     */
    public static String padLeft(String s, int length) {
        byte[] bs = new byte[length];
        byte[] ss = s.getBytes();
        Arrays.fill(bs, (byte) (48 & 0xff));
        System.arraycopy(ss, 0, bs, length - ss.length, ss.length);
        return new String(bs);
    }

    /**
     * geoString拼接
     * @param map
     * @return
     */
    public static Map geoString(Map map) {
        String lng = Objects.toString(map.get("lng"), StringUtils.EMPTY);
        String lat = Objects.toString(map.get("lat"), StringUtils.EMPTY);
        String geoString = String.format("%s,%s", lng, lat);
        map.put("location", geoString);

        return map;
    }

    /**
     * 转换command param 格式
     * @param fields
     * @return
     */
    public static  String parseCommandParam(Map<String,Object> fields){
        String result = Optional.ofNullable(fields).orElse(Collections.emptyMap()).keySet().stream().map(key -> {
            StringBuffer s = new StringBuffer(key);
            String value = String.valueOf(fields.get(key)).length() > PASS_THROUGH_MAX_LENGTH ? String.valueOf(fields.get(key)).substring(0, PASS_THROUGH_MAX_LENGTH) : String.valueOf(fields.get(key));
            s.append(":").append(key).append(":").append(value).append(":").append("input");
            return s.toString();
        }).collect(Collectors.joining(";"));
        return result;
    }


}
