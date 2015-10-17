package org.pinwheel.demo4agility.entity;

import android.util.Log;
import com.litesuits.orm.db.annotation.*;

import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

//@Table("test_orm_table_for_struct") // 可以不注解表名
public class DBStruct {

    @PrimaryKey(PrimaryKey.AssignType.AUTO_INCREMENT)
    public int id; // 必须包含一个主键,并且命名为 "id", 也可以注解其它属性为"id"
    public int test_int;
    private long test_long;
    private double test_double;
    protected float test_float;
    @Default("true")
    Boolean test_boolean;// @Default 默认值
    public String test_string;
    @Ignore
    public String test_ignore; // @Ignore 忽略, 表现为 每次查询出来都是新数据

    @Mapping(Mapping.Relation.OneToMany)
    public ArrayList<DBStruct2> test_list;

//    public DBStruct() {
//        Log.e("----------------->","DBStruct()");
//        Random random = new Random();
//        test_int = random.nextInt(10);
//        test_float = random.nextFloat();
//        test_long = random.nextLong();
//        test_double = random.nextDouble();
//        test_string = UUID.randomUUID().toString().replace("-", "").substring(10);
//        test_ignore = UUID.randomUUID().toString().replace("-", "").substring(20);
//    }

    public DBStruct(int arg0){
        Log.e("----------------->","DBStruct(int arg0)");
        test_int = arg0;
    }

    public void addList(DBStruct2 dbStruct2) {
        if (test_list == null) {
            test_list = new ArrayList<>();
        }
        test_list.add(dbStruct2);
    }

    @Override
    public String toString() {
        return "{id:" + id
                + ", int:" + test_int
                + ", long:" + test_long
                + ", double:" + test_double
                + ", float:" + test_float
                + ", boolean:" + test_boolean
                + ", string:" + test_string
                + ", ignore:" + test_ignore
                + ", list:" + (test_list == null ? "null" : test_list.toString())
                + "}";
    }

    @Table("test_orm_table_for_struct2")
    public static class DBStruct2 {

        @PrimaryKey(PrimaryKey.AssignType.AUTO_INCREMENT)
        public int id;
        public int test_int;

        public DBStruct2() {
            test_int = new Random().nextInt(10) + 10;
        }

        @Override
        public String toString() {
            return "{int:" + test_int + "}";
        }
    }


}
