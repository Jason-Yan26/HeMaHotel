package com.example.hemahotel.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.example.hemahotel.dao.GuestRepository;
import com.example.hemahotel.dao.UserRepository;
import com.example.hemahotel.entity.Guest;
import com.example.hemahotel.entity.User;
import com.example.hemahotel.jwt.JWTUtils;
import com.example.hemahotel.service.UserService;
import com.example.hemahotel.utils.ResponseUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@Slf4j
public class UserServiceImpl  implements UserService {

    private JSONObject jsonObject;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GuestRepository guestRepository;

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd/");

    //服务器地址
    @Value("${file.uploadUrl}")
    private String uploadPath;

    /**用户注册*/
    //TODO:手机验证码功能还未加入,默认verCode = 123456，verCodeId = 6;
    public ResponseUtils register(String telephone,String password,String verCode,Long verCodeId) {

        if(verCode.equals("123456") && verCodeId == 6L) {
            //如果该手机号已被注册，则不可以继续注册
            if(userRepository.findByPhone(telephone).isPresent())
                return ResponseUtils.response(404,"该手机号已被注册", jsonObject);

            Timestamp createTime = new Timestamp(System.currentTimeMillis());
            Timestamp updateTime = new Timestamp(System.currentTimeMillis());
            User user = new User(telephone, password, telephone, 0, createTime, updateTime);

            userRepository.save(user);

            Optional<User> u = userRepository.findByPhone(telephone);

            //用户注册成功,返回token,前端进入登录状态
            Map<String, String> map = new HashMap<>();
            map.put("id",u.get().getId().toString());
            map.put("phone",u.get().getPhone());
            String Token = JWTUtils.getToken(map);

            jsonObject = new JSONObject();
            jsonObject.put("token",Token);

            return ResponseUtils.response(200,"用户注册成功", jsonObject);
        }
        else{
            return ResponseUtils.response(403,"验证码错误,请重新输入验证码", jsonObject);
        }
    }

    /**用户登录(手机号/邮箱 + 密码）*/
    public ResponseUtils loginPassword(String teleEmail, String password){
        jsonObject = new JSONObject();

        Optional<User> user = userRepository.findByPhoneOrEmail(teleEmail,teleEmail);
        if(user.isPresent()){
            if(user.get().getPassword().equals(password)){
                Map<String, String> map = new HashMap<>();
                map.put("id",user.get().getId().toString());
                map.put("phone",user.get().getPhone());

                System.out.println(map);

                String Token = JWTUtils.getToken(map);

                jsonObject.put("token",Token);

                log.info("用户登录成功,用户id[{}]",user.get().getId());
                return ResponseUtils.response(200,"用户登录成功", jsonObject);
            }
            else{
                log.info("用户登录失败,用户id[{}]",user.get().getId());
                return ResponseUtils.response(403,"用户密码错误", jsonObject);
            }
        }
        else{
                return ResponseUtils.response(401,"用户并未注册账户，请注册后登录平台", jsonObject);
        }
    }

    /** 用户主页 */
    public ResponseUtils information(Long userId){
        jsonObject = new JSONObject();

        Optional<User> u = userRepository.findById(userId);
        //用户存在，返回用户的个人主页信息
        if(u.isPresent()) {
            User user = u.get();
            List<Guest> Guests = guestRepository.findAllByUserIdOrderByUpdateTimeDesc(user.getId());
            jsonObject.put("user",user);
            jsonObject.put("guests", Guests);

            return ResponseUtils.success("用户信息获取成功",jsonObject);
        }
        //用户不存在，返回错误提示信息
        else {
            jsonObject.put("id",userId);
            return ResponseUtils.response(401, "用户不存在", jsonObject);
        }
    }

    /** 修改用户密码 */
    public ResponseUtils passwordModify(Long id,String oldPassword,String newPassword){

        jsonObject = new JSONObject();
        Optional<User> u = userRepository.findById(id);

        if(!u.isPresent()){
            jsonObject.put("id",id);
            return ResponseUtils.response(402, "用户不存在", jsonObject);
        }
        //用户存在
        else {
            User user = u.get();
            String password = user.getPassword();
            //用户输入原始密码与数据库保持一致,还未加密密码
            if(oldPassword.equals(password)){
                user.setPassword(newPassword);
                userRepository.save(user);
                return ResponseUtils.response(200,"用户密码修改成功", jsonObject);
            }
            else{
                return ResponseUtils.response(401,"原密码输入错误,密码修改失败", jsonObject);
            }
        }
    }

    /** 修改用户个人信息*/
    public ResponseUtils informationModify(Long userId, String username, Integer gender, Date birthDate,
                                           Integer age, String signature, String preference_label,
                                           String address, String email, String phone){
        jsonObject = new JSONObject();
        Optional<User> u = userRepository.findById(userId);

        if(!u.isPresent()){
            jsonObject.put("id",userId);
            return ResponseUtils.response(404,"用户不存在", jsonObject);
        }
        //用户id存在
        else {
            User user = u.get();

            Optional<User> user1 = userRepository.findByUsername(username);
            if(user1.isPresent()) {
                if(!(user1.get().getId().equals(userId))){
                    return ResponseUtils.response(401,"用户名已存在，请重新修改用户名", jsonObject);
                }
            }
            user.setUsername(username);

            user.setGender(gender);
            user.setBirthDate(birthDate);
            //user.setAge(age);
            //user.setSignature(signature);
            //user.setPreferenceLabel(preference_label);
            //user.setAddress(address);

            user1 = userRepository.findByPhone(phone);
            if(user1.isPresent()) {
                if(!user1.get().getId().equals(userId)){
                    return ResponseUtils.response(402,"用户所输手机号已被绑定，请重新绑定用户手机号", jsonObject);
                }
            }
            user.setPhone(phone);

            user1 = userRepository.findByEmail(email);
            if(user1.isPresent()) {
                if(!user1.get().getId().equals(userId)){
                    return ResponseUtils.response(403,"用户所输邮箱已被绑定，请重新绑定用户邮箱", jsonObject);
                }
            }
            user.setEmail(email);

            userRepository.save(user);
            return ResponseUtils.response(200,"用户个人信息修改成功", jsonObject);
        }
    }

    /** 头像上传 */
    public ResponseUtils avatarUpload(Long userId, MultipartFile file, String urlPrefix){

        JSONObject jsonObject = new JSONObject();
        Optional<User> u = userRepository.findById(userId);

        if(!u.isPresent()){
            jsonObject.put("id",userId);
            return ResponseUtils.response(401,"用户不存在", jsonObject);
        }
        //用户id存在
        else {
            //1.文件保存地址的后半段目录：  2022/05/11/
            String directory = simpleDateFormat.format(new java.util.Date());

            //2.服务器端文件保存目录  D:/upload/2022/05/11/   如果目录不存在，则创建
            File dir = new File(uploadPath + directory);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            //3.文件保存名称（产生的唯一随机数+文件后缀）
            String suffix = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
            String newFileName= UUID.randomUUID().toString().replaceAll("-", "")+suffix;

            //4.创建这个新文件
            File newFile = new File(uploadPath + directory + newFileName);

            //5.文件上传
            try {
                file.transferTo(newFile);
                //可访问url格式：  文件目录(/upload/2022/05/11/xxx.jpg)
                String url = urlPrefix+ "/upload/" + directory + newFileName; // 部署到服务器端后需更改
                //String url = "file:///" + uploadPath + directory + newFileName; // 目前测试在本机
                jsonObject.put("url",url);

                //修改用户头像
                User user = u.get();
                user.setAvatar(url);
                userRepository.save(user);
                return ResponseUtils.response(200, "头像上传成功",jsonObject);

            } catch (IOException e) {
                e.printStackTrace();
                return ResponseUtils.response(400, "头像上传失败",jsonObject);
            }
        }



    }
}
