package com.tidus.me.db.dbcp;

import com.tidus.me.db.bean.Student;
import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.util.List;

public class DBCPUtils {

    public static final Logger logger = LoggerFactory.getLogger(DBCPUtils.class);
    private static final BasicDataSource dataSource;

    static {
        dataSource = new BasicDataSource();
        //基本设置
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://localhost:3308/local?characterEncoding=utf8&useSSL=false&autoReconnect=true&failOverReadOnly=false");
        dataSource.setUsername("local");
        dataSource.setPassword("local");
        //高级设置
        dataSource.setInitialSize(10);//初始化连接
        dataSource.setMinIdle(5);//最小空闲连接
        dataSource.setMaxIdle(20);//最大空闲连接
        dataSource.setMaxActive(50);//最大连接数量
        dataSource.setMaxWait(1000);//超时等待时间以毫秒为单位
    }


    /**
     * 获取DataSource对象
     *
     * @return
     */
    public static DataSource getDataSource() {
        return dataSource;
    }


    public static boolean insertStudent(Student student) {
        try {
            logger.info("insertStudent");
            //1，得到dataSource对象，
            DataSource dataSource = DBCPUtils.getDataSource();
            //2，得到QueryRunner对象
            QueryRunner queryRunner = new QueryRunner(dataSource);
            //3，执行插入操作sql
            queryRunner.update("insert into student (name,age) values(?,?)", student.getName(), student.getAge());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static Student selectStudent(int id) {
        try {
            logger.error("selectStudent_byId");
            //1，得到dataSource对象，
            DataSource dataSource = DBCPUtils.getDataSource();
            //2，得到QueryRunner对象
            QueryRunner queryRunner = new QueryRunner(dataSource);
            //3，执行查询作sql
            Student student = queryRunner.query("select id,name,age from student where id=?", new BeanHandler<>(Student.class), id);
            return student;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public static List<Student> selectUserList(int age) {
        try {
            logger.error("selectUserList_byAge");
            //1，得到dataSource对象，
            DataSource dataSource = DBCPUtils.getDataSource();
            //2，得到QueryRunner对象
            QueryRunner queryRunner = new QueryRunner(dataSource);
            //3，执行查询作sql
            List<Student> studentList = queryRunner.query("select id,name,age from student where age=?", new BeanListHandler<>(Student.class), age);
            return studentList;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public static boolean updateStudent(Student student) {
        try {
            logger.error("updateStudent");
            //1，得到dataSource对象，
            DataSource dataSource = DBCPUtils.getDataSource();
            //2，得到QueryRunner对象
            QueryRunner queryRunner = new QueryRunner(dataSource);
            //3，执行更新操作sql
            queryRunner.update("update student set age = ? where id = ?", student.getAge(), student.getId());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean deleteStudent(int id) {
        try {
            logger.error("deleteStudent");
            //1，得到dataSource对象，
            DataSource dataSource = DBCPUtils.getDataSource();
            //2，得到QueryRunner对象
            QueryRunner queryRunner = new QueryRunner(dataSource);
            //3，执行删除操作sql
            queryRunner.update("delete from student where id = ?", id);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


}
