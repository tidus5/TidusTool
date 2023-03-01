package com.tidus.me.db;

import com.tidus.me.db.bean.Student;
import com.tidus.me.db.dbcp.DBCPUtils;
import org.junit.Test;

public class DbcpTest {


    @Test
    public void insert() throws InterruptedException {
        Student student = new Student("aaa", 13);
        Thread.sleep(1000);
        DBCPUtils.insertStudent(student);
        Thread.sleep(1000);
        DBCPUtils.insertStudent(student);
    }
}
