package com.example.Backend.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.Backend.DTO.Request.ForgotPasswordRequest;
import com.example.Backend.DTO.Request.LoginRequest;
import com.example.Backend.DTO.Request.RegisterRequest;
import com.example.Backend.DTO.Request.ResetPasswordRequest;
import com.example.Backend.DTO.Response.LoginResponse;
import com.example.Backend.Entity.Roles;
import com.example.Backend.Entity.UserRoles;
import com.example.Backend.Entity.Users;
import com.example.Backend.Repository.RolesRepository;
import com.example.Backend.Repository.UserRoleRepository;
import com.example.Backend.Repository.UsersRepository;
import com.example.Backend.Utils.JwtUtils;
import com.example.Backend.Utils.AccountStatus;

@Service
public class AuthService {

    @Autowired
    // 注入 Users 資料庫操作
    private UsersRepository usersRepository;

    // 注入 Roles 資料庫操作
    @Autowired
    private RolesRepository rolesRepository;

    // 注入 UserRole 資料庫操作
    @Autowired
    private UserRoleRepository userRoleRepository;

    @Autowired
    // 注入 JWT 工具類別
    private JwtUtils jwtUtils;

    @Autowired
    // 注入密碼加密工具
    private PasswordEncoder passwordEncoder;

    // 注入Email工具
    @Autowired
    private JavaMailSender mailSender;

    // 註冊流程(基本資料/寄信/驗證碼)
    public Users register(RegisterRequest request) {

        // 帳號 & Email 處理
        String accountTrimmed = request.getAccount().trim();
        String emailTrimmed = request.getEmail().trim().toLowerCase();

        // 檢查帳號是否已存在（忽略大小寫）
        if (usersRepository.findByAccountIgnoreCase(accountTrimmed) != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "帳號已存在");
        }

        // 檢查 Email 是否已存在
        if (usersRepository.findByEmailIgnoreCase(emailTrimmed) != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email 已存在");
        }

        // 建立 Users 物件
        Users user = new Users();
        user.setAccount(accountTrimmed);
        user.setPwdhash(passwordEncoder.encode(request.getPassword()));
        user.setUsername(request.getUsername().trim());
        user.setRealname(request.getRealname().trim());
        user.setEmail(emailTrimmed);
        user.setPhone(request.getPhone().trim());
        user.setStatus(AccountStatus.PENDING);

        // 產生隨機 6 碼驗證碼
        String verificationCode = String.format("%06d", new Random().nextInt(999999));

        // 設定驗證碼與有效期限（15 分鐘）
        user.setResettoken(verificationCode);
        user.setResetTokenExpiry(LocalDateTime.now().plusMinutes(15));

        // 角色處理
        Roles studentRole = rolesRepository.findByRolename("STUDENT");
        if (studentRole == null) {
            studentRole = new Roles();
            studentRole.setRolename("STUDENT");
            studentRole.setDescriptions("學生身份");
            rolesRepository.save(studentRole);
        }

        // 儲存 User
        Users savedUser = usersRepository.save(user);

        // 建立 UserRole 關聯
        UserRoles userRole = new UserRoles();
        userRole.setUsers(savedUser);
        userRole.setRoles(studentRole);
        userRoleRepository.save(userRole);

        // 寄送 Email 驗證碼
        sendVerificationEmail(savedUser.getEmail(), verificationCode, savedUser.getRealname());

        return savedUser;
    }

    private void sendVerificationEmail(String email, String code, String realname) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("帳號驗證碼通知");
        message.setText(
                "親愛的 " + realname + " 您好，\n\n"
                        + "感謝您註冊本平台，以下是您的帳號驗證碼：\n\n"
                        + "👉 驗證碼：" + code + "\n\n"
                        + "請於 15 分鐘內完成驗證，逾期將需重新註冊。\n\n"
                        + "此信件由系統自動寄出，請勿回覆。");

        mailSender.send(message);
    }

    public String verifyAccount(String account, String code) throws Exception {
        Users user = usersRepository.findByAccountIgnoreCase(account);

        if (user == null) {
            throw new Exception("帳號不存在");
        }

        if (user.getAccountStatus() == AccountStatus.ACTIVE) {
            throw new Exception("帳號已驗證");
        }

        if (user.getResettoken() == null || user.getResetTokenExpiry() == null) {
            throw new Exception("尚未申請驗證碼");
        }

        if (LocalDateTime.now().isAfter(user.getResetTokenExpiry())) {
            throw new Exception("驗證碼已過期，請重新註冊");
        }

        if (!user.getResettoken().equals(code)) {
            throw new Exception("驗證碼錯誤");
        }

        // 驗證成功 → 啟用帳號
        user.setStatus(AccountStatus.ACTIVE);
        user.setResettoken(null);
        user.setResetTokenExpiry(null);
        usersRepository.save(user);

        return "驗證成功，歡迎 " + user.getRealname();
    }

    // 登入
    public LoginResponse login(LoginRequest request) throws Exception {

        Users user = usersRepository.findByAccount(request.getAccount());

        // 檢查帳號是否已存在
        if (user == null) {
            throw new Exception("帳號不存在");
        }
        // 檢查密碼是否錯誤
        if (!passwordEncoder.matches(request.getPassword(), user.getPwdhash())) {
            throw new Exception("密碼錯誤");
        }

        // 生成 Token
        String token = jwtUtils.generateJwtToken(user);

        // 查角色
        List<String> roles = userRoleRepository.findUserRolesByUserId(user.getUserID());

        // 若沒有上傳頭貼則使用預設
        String avatar = (user.getAvatar() != null && !user.getAvatar().isEmpty())
                ? user.getAvatar()
                : "/assets/logo.png";

        return new LoginResponse(
                token,
                user.getUserID(),
                user.getAccount(),
                user.getUsername(),
                user.getEmail(),
                avatar,
                roles);
    }

    // 忘記密碼寄送信件
    public void sendResetCode(ForgotPasswordRequest request) {
        Users user = usersRepository.findByEmailIgnoreCase(request.getEmail());
        if (user == null) {
            // 【重要】確保這裡的錯誤訊息會被前端正確捕獲
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "查無此 Email");
        }

        // 產生六位數驗證碼
        String code = String.format("%06d", new Random().nextInt(999999));
        user.setResettoken(code);
        user.setResetTokenExpiry(LocalDateTime.now().plusMinutes(15)); // 15分鐘有效
        usersRepository.save(user);

        // 寄出信件
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail());
        message.setSubject("密碼重設驗證碼");
        message.setText("以下是您的密碼重設驗證碼，有效時間15分鐘：\n\n" + code + "\n\n請勿回覆此信件。");
        mailSender.send(message);
    }

    // 更新密碼
    public void verifyAndResetPassword(ResetPasswordRequest request) {
        Users user = usersRepository.findByEmailIgnoreCase(request.getEmail());
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "查無此 Email");
        }

        // 驗證驗證碼
        if (user.getResettoken() == null || !user.getResettoken().equals(request.getCode())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "驗證碼錯誤");
        }

        // 驗證有效期限
        if (user.getResetTokenExpiry() == null || user.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "驗證碼已過期");
        }

        // 更新密碼
        user.setPwdhash(passwordEncoder.encode(request.getNewPassword()));

        // 清除驗證碼
        user.setResettoken(null);
        user.setResetTokenExpiry(null);
        usersRepository.save(user);
    }

}