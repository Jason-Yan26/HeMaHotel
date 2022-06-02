package com.example.hemahotel.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.example.hemahotel.dao.CommentRepository;
import com.example.hemahotel.dao.GuestRepository;
import com.example.hemahotel.dao.HotelRepository;
import com.example.hemahotel.dao.UserRepository;
import com.example.hemahotel.entity.*;
import com.example.hemahotel.jwt.JWTUtils;
import com.example.hemahotel.service.UserService;
import com.example.hemahotel.utils.ResponseUtils;
import com.example.hemahotel.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private HotelRepository hotelRepository;


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

            String password_security = SecurityUtils.encodePassword(password);

            User user = new User(telephone, password_security, telephone, 0, createTime, updateTime);
            user.setIdentity(0);//identity = 0 :普通用户

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

            String password_security =  user.get().getPassword();//数据库中存储的加密后的密码

            if(SecurityUtils.matchesPassword(password,password_security)){
                Map<String, String> map = new HashMap<>();
                map.put("id",user.get().getId().toString());
                map.put("phone",user.get().getPhone());

                System.out.println(map);

                String Token = JWTUtils.getToken(map);

                jsonObject.put("token",Token);
                jsonObject.put("identity",user.get().getIdentity());

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

    @Override
    public ResponseUtils getUserInformation(Long adminId, Long userId) {
        User user1 = userRepository.getById(adminId);
        JSONObject jsonObject = new JSONObject();

        if(!user1.getIdentity().equals(2)){ // 前台人员：2
            jsonObject.put("adminId", adminId);
            return ResponseUtils.response(400,"不存在查看权限", jsonObject);
        }
        else {
            Optional<User> u = userRepository.findById(userId);
            //用户存在，返回用户的个人主页信息
            if(u.isPresent()) {
                User user = u.get();
                //List<Guest> Guests = guestRepository.findAllByUserIdOrderByUpdateTimeDesc(user.getId());
                jsonObject.put("user",user);
                //jsonObject.put("guests", Guests);

                return ResponseUtils.success("用户信息获取成功",jsonObject);
            }
            //用户不存在，返回错误提示信息
            else {
                jsonObject.put("id",userId);
                return ResponseUtils.response(401, "用户不存在", jsonObject);
            }
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
            String password_security = user.getPassword();
            //用户输入原始密码与数据库保持一致,还未加密密码
            if(SecurityUtils.matchesPassword(oldPassword,password_security)){

                String newPassword_security = SecurityUtils.encodePassword(newPassword);

                user.setPassword(newPassword_security);
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
                                           String address){
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
            //user.setAge(age)
            user.setSignature(signature);
            user.setPreferenceLabel(preference_label);
            user.setAddress(address);

            //默认手机号不可修改，邮箱需要获取验证码进行绑定
//            user1 = userRepository.findByPhone(phone);
//            if(user1.isPresent()) {
//                if(!user1.get().getId().equals(userId)){
//                    return ResponseUtils.response(402,"用户所输手机号已被绑定，请重新绑定用户手机号", jsonObject);
//                }
//            }
//            user.setPhone(phone);

//            user1 = userRepository.findByEmail(email);
//            if(user1.isPresent()) {
//                if(!user1.get().getId().equals(userId)){
//                    return ResponseUtils.response(403,"用户所输邮箱已被绑定，请重新绑定用户邮箱", jsonObject);
//                }
//            }
//            user.setEmail(email);

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

    @Override
    public ResponseUtils getComments(Long userId, Integer pageIndex, Integer pageSize, String sortProperty) {

        Sort sort = Sort.by(Sort.Order.desc(sortProperty)); // sortProperty:排序属性
        Pageable pageable = PageRequest.of(pageIndex, pageSize, sort);
        Page<Comment> comments = commentRepository.findAllByUserId(userId, pageable);

        List<JSONObject> res = new ArrayList<>();
        for(Comment c:comments.getContent()) {
            JSONObject jsonObject = new JSONObject();
            User user = userRepository.getById(userId);
            Hotel hotel = hotelRepository.getById(c.getHotelId());
            jsonObject.put("commentId",c.getId());
            jsonObject.put("comment",c.getContent());
            jsonObject.put("star",c.getStar());
            jsonObject.put("username",user.getUsername());
            jsonObject.put("userAvatar",user.getAvatar());
            jsonObject.put("hotelId",hotel.getId());
            jsonObject.put("hotelName",hotel.getName());
            jsonObject.put("hotelPictureUrl",hotel.getPicture());
            jsonObject.put("createTime",c.getCreateTime());
            res.add(jsonObject);
        }

        return ResponseUtils.response(200, "用户评论获取成功", res);
    }

    @Override
    public ResponseUtils getCommentNum(Long userId) {

        Long number = commentRepository.countByUserId(userId);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("commentNum",number);
        return ResponseUtils.response(200, "用户评论总数获取成功", jsonObject);

    }

    /** 获取用户头像*/
    public ResponseUtils getAvatar(Long userId){
        JSONObject jsonObject = new JSONObject();
        Optional<User>  u = userRepository.findById(userId);
        if(u.isPresent()){
            String avatar = u.get().getAvatar();
            jsonObject.put("avatar",avatar);
            return ResponseUtils.response(200, "用户头像获取成功", jsonObject);
        }
        else{
            return ResponseUtils.response(401, "用户不存在", jsonObject);
        }
    }

    /** 获取注册用户数量*/
    public ResponseUtils getAmount(Long userId,Integer type){
        JSONObject jsonObject = new JSONObject();
        User user = userRepository.findById(userId).get();
        //确保该用户身份为系统管理员，才有权限可以操作
        if(user.getIdentity() == 1){

            //查询类型：0累计，1今日，2昨日
            long amount = 0;
            if(type == 0){
                amount = userRepository.count();
            }
            else if(amount == 1){
                //今日0点时间
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                Timestamp startTime = new Timestamp(calendar.getTimeInMillis());

                //今日23.59时间
                calendar.set(Calendar.HOUR_OF_DAY, 23);
                calendar.set(Calendar.MINUTE, 59);
                calendar.set(Calendar.SECOND, 59);
                calendar.set(Calendar.MILLISECOND, 999);
                Timestamp endTime = new Timestamp(calendar.getTimeInMillis());

                amount = userRepository.countAllByCreateTimeBetween(startTime,endTime);
            }
            else if(amount == 2){
                //昨日00：00时间
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.DAY_OF_MONTH,-1);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                Timestamp startTime = new Timestamp(calendar.getTimeInMillis());

                //昨日23:59时间
                calendar.set(Calendar.HOUR_OF_DAY, 23);
                calendar.set(Calendar.MINUTE, 59);
                calendar.set(Calendar.SECOND, 59);
                calendar.set(Calendar.MILLISECOND, 999);
                Timestamp endTime = new Timestamp(calendar.getTimeInMillis());

                amount = userRepository.countAllByCreateTimeBetween(startTime,endTime);
            }

            jsonObject.put("amount",amount);
            return ResponseUtils.response(200, "注册用户数量获取成功", jsonObject);
        }
        else{
            return ResponseUtils.response(401, "权限不足，无法获取订单信息", jsonObject);
        }
    }

}
