package com.example.Backend.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.Backend.DTO.DTOS.ECPayRequestDTO;
import com.example.Backend.DTO.Response.ECPayResponseDTO;
import com.example.Backend.Entity.CartDetail;
import com.example.Backend.Entity.Enrollment;
import com.example.Backend.Entity.OrderDetail;
import com.example.Backend.Entity.StudentProfile;
import com.example.Backend.Entity.UserOrder;
import com.example.Backend.Repository.CartDetailRepository;
import com.example.Backend.Repository.CartRepository;
import com.example.Backend.Repository.EnrollmentRepository;
import com.example.Backend.Repository.OrderDetailRepository;
import com.example.Backend.Repository.StudentProfileRepository;
import com.example.Backend.Repository.UserOrderRepository;
import com.example.Backend.Utils.ECPayUtil;
import com.example.Backend.Utils.EnrollmentStatus;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final UserOrderRepository userOrderRepository;
    private final ECPayService ecPayService;
    private final ECPayUtil ecPayUtil;
    private final CartRepository cartRepository;
    private final CartDetailRepository cartDetailRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final EnrollmentRepository enrollmentRepository;

    @Value("${ecpay.return.url}")
    private String ecpayReturnUrl;

    @Value("${ecpay.client.back.url:http://localhost:5173/checkout}")
    private String clientBackUrl;

    @Transactional
    public ECPayResponseDTO createOrderAndPayment(Long userId, BigDecimal amount, String itemName) {
        String merchantTradeNo = ecPayUtil.generateMerchantTradeNo();

        UserOrder order = new UserOrder();
        order.setUserId(userId);
        order.setOrderStatus("未付款");
        order.setPaymentMethod("ECPay");
        order.setTotalAmount(amount);
        order.setNetAmount(amount);
        order.setMerchantTradeNo(merchantTradeNo);
        order.setTradeAmt(amount);
        userOrderRepository.save(order);

        // 建立付款表單
        String callbackUrl = ecpayReturnUrl + "/api/payment/callback";
        String returnUrl = ecpayReturnUrl + "/api/payment/return";

        ECPayRequestDTO request = ECPayRequestDTO.builder()
                .merchantTradeNo(merchantTradeNo)
                .orderId(order.getOrderId())
                .itemName(itemName)
                .totalAmount(amount.intValue())
                .tradeDesc("線上課程購買")
                .returnUrl(callbackUrl)
                .orderResultUrl(returnUrl)
                .clientBackUrl(clientBackUrl)
                .build();

        log.info("✅ 訂單建立成功：OrderID={}, MerchantTradeNo={}", order.getOrderId(), merchantTradeNo);
        return ecPayService.createPaymentForm(request);
    }

    /**
     * ✅ 綠界付款成功回呼：更新訂單、建立明細與修課紀錄
     */
    @Transactional
    public String handlePaymentCallback(Map<String, String> params) {
        log.info("📩 收到綠界付款回呼：{}", params);

        String merchantTradeNo = params.get("MerchantTradeNo");
        String rtnCode = params.get("RtnCode");
        String rtnMsg = params.get("RtnMsg");

        // 1️⃣ 查找訂單
        UserOrder userOrder = userOrderRepository.findByMerchantTradeNo(merchantTradeNo)
                .orElseThrow(() -> new RuntimeException("找不到對應的訂單 MerchantTradeNo=" + merchantTradeNo));

        if (!"1".equals(rtnCode)) {
            userOrder.setOrderStatus("FAILED");
            userOrderRepository.save(userOrder);
            log.warn("⚠️ 綠界回傳非成功交易：{}", rtnMsg);
            return "0|OK";
        }

        // 2️⃣ 更新訂單
        userOrder.setOrderStatus("完成");
        userOrder.setPaymentDate(LocalDateTime.now());
        userOrder.setRtnCode(1);
        userOrder.setRtnMsg(rtnMsg);
        userOrderRepository.save(userOrder);

        log.info("💰 訂單付款完成：OrderID={}, MerchantTradeNo={}", userOrder.getOrderId(), merchantTradeNo);

        // 3️⃣ 建立訂單明細
        List<CartDetail> cartItems = cartDetailRepository.findByCart_UserIdAndCart_CartStatus(
                userOrder.getUserId(), (byte) 2);

        for (CartDetail cd : cartItems) {
            OrderDetail od = new OrderDetail();
            od.setUserOrder(userOrder);
            od.setCourse(cd.getCourse());
            od.setUnitPrice(cd.getCourse().getPrice());
            od.setQuantity(1);
            od.setCreatedAt(LocalDateTime.now());
            orderDetailRepository.save(od);

            log.info("🧾 已建立訂單明細：OrderID={}, CourseID={}", userOrder.getOrderId(), cd.getCourse().getCourseId());
        }

        // 4️⃣ 建立修課紀錄
        Long userId = userOrder.getUserId();
        Optional<StudentProfile> optStudent = studentProfileRepository.findByUsers_UserId(userId);

        if (optStudent.isPresent()) {
            StudentProfile student = optStudent.get();

            for (CartDetail cd : cartItems) {
                boolean exists = enrollmentRepository.existsByStudent_StudentIdAndCourse_CourseId(
                        student.getStudentId(), cd.getCourse().getCourseId());

                if (exists) {
                    log.info("⚠️ 學生已修過該課程，略過 -> StudentID={}, CourseID={}",
                            student.getStudentId(), cd.getCourse().getCourseId());
                    continue;
                }

                Enrollment enroll = new Enrollment();
                enroll.setStudent(student);
                enroll.setCourse(cd.getCourse());
                enroll.setEnrollmentStatus(EnrollmentStatus.ONGOING);
                enroll.setCreatedAt(LocalDateTime.now());
                enrollmentRepository.save(enroll);

                log.info("🎓 已建立修課紀錄 StudentID={}, CourseID={}, Status=ONGOING",
                        student.getStudentId(), cd.getCourse().getCourseId());
            }
        } else {
            log.warn("⚠️ 找不到 studentProfile，UserID={}", userId);
        }

        return "1|OK";
    }

    public UserOrder getOrderStatus(Long orderId) {
        return userOrderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("找不到訂單: " + orderId));
    }
}
