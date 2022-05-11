package com.example.hemahotel.service;


import com.example.hemahotel.utils.ResponseUtils;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.sql.Date;

public interface UserService {

    /** 用户注册 */
    public ResponseUtils register(String telephone,String password,String verCode,Long verCodeId);


    /** 用户登录(手机号/邮箱 + 密码） */
    public ResponseUtils loginPassword(String teleEmail, String password);

    /** 用户主页 */
    public ResponseUtils information(Long userId);

    /** 用户密码修改 */
    public ResponseUtils passwordModify(Long userId,String oldPassword,String newPassword);

    /** 用户个人信息修改(不包含头像) */
    public ResponseUtils informationModify(Long userId, String username, Integer gender, Date birthDate,
                                           Integer age, String signature, String preference_label,
                                           String address, String email, String phone);
    /** 用户头像上传 */
    public ResponseUtils avatarUpload(Long userId, MultipartFile file, String urlPrefix);
}
