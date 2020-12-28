package com.config;

import com.domain.UserVo;
import com.service.RedisService;
import com.util.RedisKeyUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.geo.*;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.*;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RedisConfigTest {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private RedisTemplate redisTemplate;

    @Resource
    private ValueOperations<String,Object> valueOperations;

    @Autowired
    private HashOperations<String, String, Object> hashOperations;

    @Autowired
    private ListOperations<String, Object> listOperations;

    @Autowired
    private SetOperations<String, Object> setOperations;

    @Autowired
    private ZSetOperations<String, Object> zSetOperations;

    @Resource
    private RedisService redisService;

    @Autowired
    private HyperLogLogOperations<String, Object> hyperLogLogOperations;

    @Autowired
    private GeoOperations<String, Object> geoOperations;



    @Test
    public void testObj() throws Exception{
        UserVo userVo = new UserVo();
        userVo.setAddress("上海");
        userVo.setName("测试dfas");
        userVo.setAge(123);
        ValueOperations<String,Object> operations = redisTemplate.opsForValue();
        redisService.expireKey("name",20, TimeUnit.SECONDS);
        String key = RedisKeyUtil.getKey(UserVo.Table,"name",userVo.getName());
        UserVo vo = (UserVo) operations.get(key);
        System.out.println(vo);
    }

    @Test
    public void testValueOption( )throws  Exception{
        UserVo userVo = new UserVo();
        userVo.setAddress("上海");
        userVo.setName("jantent");
        userVo.setAge(23);
        valueOperations.set("test",userVo);

        System.out.println(valueOperations.get("test"));
    }

    @Test
    public void testSetOperation() throws Exception{
        UserVo userVo = new UserVo();
        userVo.setAddress("北京");
        userVo.setName("jantent");
        userVo.setAge(23);
        UserVo auserVo = new UserVo();
        auserVo.setAddress("n柜昂周");
        auserVo.setName("antent");
        auserVo.setAge(23);
        setOperations.add("user:test",userVo,auserVo);
        Set<Object> result = setOperations.members("user:test");
        System.out.println(result);
    }

    @Test
    public void HashOperations() throws Exception{
        UserVo userVo = new UserVo();
        userVo.setAddress("北京");
        userVo.setName("jantent");
        userVo.setAge(23);
        hashOperations.put("hash:user",userVo.hashCode()+"",userVo);
        System.out.println(hashOperations.get("hash:user",userVo.hashCode()+""));
    }

    @Test
    public void  ListOperations() throws Exception{
        UserVo userVo = new UserVo();
        userVo.setAddress("北京");
        userVo.setName("jantent");
        userVo.setAge(23);
        String key = "list:user";
        listOperations.leftPush(key,userVo);
//        System.out.println(listOperations.leftPop("list:user"));
        // pop之后 值会消失
//        System.out.println(listOperations.leftPop("list:user"));
        System.out.println(listOperations.range(key, 0, listOperations.size(key)));
    }


    @Test
    public void testHyperLogLogOperations() {
        String key = "product_detail";
        hyperLogLogOperations.delete(key);
        hyperLogLogOperations.add(key, "1001", "1002", "1003");
        System.out.println("product_detail count : " + hyperLogLogOperations.size(key));

        String homePageKey = "home_page";

        hyperLogLogOperations.delete(homePageKey);
        List<String> users = new ArrayList<>();
        for(int i = 1; i <= 1000; i++) {
            users.add(String.valueOf(i));
        }
        hyperLogLogOperations.add(homePageKey, users.toArray(new String[0]));
        System.out.println("home_page count : " + hyperLogLogOperations.size(homePageKey));

        System.out.println("count :" + hyperLogLogOperations.union(homePageKey, key));
    }

    @Test
    public void testGeoOperations() {
        String GEO_KEY = "ah-cities";

        Point point = new Point(116.411550, 39.897921);
        geoOperations.geoAdd(GEO_KEY, point, "mail01");


        Distance distance = new Distance(0.5d, Metrics.KILOMETERS);
        //目标点,后边坐标应该改成可传
        Point point1 = new Point(116.411573, 39.897958);
        // 封装覆盖的面积
        Circle circle = new Circle(point1, distance);
        GeoResults<RedisGeoCommands.GeoLocation<Object>> geoResults =  geoOperations.geoRadius(GEO_KEY, circle);
        List<GeoResult<RedisGeoCommands.GeoLocation<Object>>> resultsContent = geoResults.getContent();

        for (GeoResult<RedisGeoCommands.GeoLocation<Object>> geoLocationGeoResult : resultsContent) {
            RedisGeoCommands.GeoLocation<Object> geoLocation = geoLocationGeoResult.getContent();
            // 编号
            String locationId = geoLocation.getName().toString();
            System.out.println(locationId);
        }
    }


}