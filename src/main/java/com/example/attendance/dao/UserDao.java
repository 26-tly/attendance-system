package com.example.attendance.dao;

import com.example.attendance.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.PreparedStatement;
import java.util.List;

@Repository
public class UserDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    //新增教师用户
    public  int insert(User user){
        String sql="INSERT INTO [user](username,password,user_role) VALUES(?,?,?)";
        // 用来接收自动生成的主键
        KeyHolder keyHolder = new GeneratedKeyHolder();

        // 执行更新并返回自增ID
        int rows = jdbcTemplate.update(conn -> {
            PreparedStatement ps = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getUserRole());
            return ps;
        }, keyHolder);

        // 将自增ID设置回 user 对象
        if (keyHolder.getKey() != null) {
            user.setUserId(keyHolder.getKey().intValue());
        }

        return rows;
    }
    //根据id查询用户
    public User findById(Integer userId){
        String sql="SELECT user_id,username,password,user_role FROM [user] WHERE user_id=?";
        List<User> list=jdbcTemplate.query(sql,new BeanPropertyRowMapper<>(User.class),userId);
        return list.isEmpty() ? null : list.get(0);
    }
    //根据用户名查询（用于登录验证）
    public User findByUsername(String username){
        String sql="SELECT user_id,username,password,user_role FROM [user] WHERE username = ?";
        List<User> list = jdbcTemplate.query(sql,new BeanPropertyRowMapper<>(User.class),username);
        return list.isEmpty() ? null : list.get(0);
    }
    //查询所有教师（user_role='teacher'）
    public List<User> findAllTeachers(){
        String sql = "SELECT user_id,username,password,user_role FROM[user] WHERE user_role = ?";
        return jdbcTemplate.query(sql,new BeanPropertyRowMapper<>(User.class),"teacher");
    }
    //更新用户信息
    public int update(User user){
        String sql = "UPDATE [user] SET username = ?,password = ?,user_role = ? WHERE user_id = ?";
        return jdbcTemplate.update(
                sql,
                user.getUsername(),
                user.getPassword(),
                user.getUserRole(),
                user.getUserId());
    }
    //删除用户
    public int deleteById(Integer userId){
        String sql = "DELETE FROM [user] WHERE user_id = ?";
        return jdbcTemplate.update(sql,userId);
    }
}
