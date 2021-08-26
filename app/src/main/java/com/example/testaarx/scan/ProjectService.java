package com.example.testaarx.scan;

import com.facilityone.wireless.a.arch.ec.module.Page;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;

/**
 * Author：gary
 * Email: xuhaozv@163.com
 * description:
 * Date: 2018/6/20 下午2:43
 */
public class ProjectService {


    //项目列表
    public static class ProjectResponseBean {
        public Long groupId;
        public String groupName;
        public String groupDesc;
        public List<ProjectBean> projects;
    }

    //项目角标
    public static class ProjectIndexReq {
        public Long userId;
        public List<Long> project;

        public ProjectIndexReq(Long userId) {
            this.userId = userId;
            this.project = new ArrayList<>();
        }
    }
    
    public static class ProjectIndexBean{
        public Long projectId;
        public Integer amount;
    }

     /**
      * @Auther: karelie
      * @Date: 2021/8/5
      * @Infor: 签到请求
      */
     public static class SignOnReq{
         public Long personId;
     }

      /**
       * @Auther: karelie
       * @Date: 2021/8/9
       * @Infor: 我的签到记录请求
       */
      public static class MineSignListReq{
          public Long timeStart;
          public Long timeEnd;
          public Page page;
      }

    public class MySignRecordEntity {
        public Page page;
        public List<MySignRecord> contents;
    }

    public static class MySignRecord {
        public Long contactId;
        public String contactName;
        public String locationName;
        public Location location;
        public Long createTime;
    }

    public static class Location {
        public Long cityId;
        public Long siteId;
        public Long buildingId;
        public Long floorId;
        public Long roomId;
    }

    /**
     * 库存二维码实例bean
     */
    public static class InventoryQRCodeBean {
        public String function;// 功能--比如能源管理（energy）
        public String subfunction;// 子功能--比如抄表项（meter）
        public String companyName;//公司名称--facilityone--简写--F-ONE
        public String wareHouseId;
        public String code;
    }


    public static class ProjectBean implements Comparable<ProjectBean> {
        public Long projectId;
        public String name;
        public String province;
        public String city;
        public String code;
        public String imgId;
        public String type;
        public transient String provincePy;//省拼音
        public transient String cityPy;//城市拼音
        public transient String namePy;
        public Integer msgCount;
        public transient String singlePy;//简拼
        public Boolean expired;//项目是否到期
        public transient int start;//搜索匹配的开始位置(name 中的位置顺序)
        public transient int end;//搜索匹配的结束位置(name 中的位置顺序)

        @Override
        public int compareTo(@NonNull ProjectBean another) {
            return this.provincePy.compareTo(another.provincePy);
        }

        @Override
        public String toString() {
            return "ProjectBean{" +
                    "projectId=" + projectId +
                    ", name='" + name + '\'' +
                    ", province='" + province + '\'' +
                    ", city='" + city + '\'' +
                    ", code='" + code + '\'' +
                    ", imgId=" + imgId +
                    ", type='" + type + '\'' +
                    ", provincePy='" + provincePy + '\'' +
                    ", msgCount=" + msgCount +
                    '}';
        }
    }
}
