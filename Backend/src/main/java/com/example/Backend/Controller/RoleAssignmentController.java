package com.example.Backend.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.Backend.DTO.DTOS.UserRoleDto;
import com.example.Backend.Service.RoleAssignmentService;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class RoleAssignmentController {

    @Autowired
    private RoleAssignmentService roleAssignmentService;

    // 指派角色給使用者
    @PostMapping("/assign-role")
    public ResponseEntity<String> assignRole(@RequestBody UserRoleDto dto) {
        roleAssignmentService.assignRolesToUser(dto);
        return ResponseEntity.ok("角色設定成功");
    }

    // 取得使用角色
    @GetMapping("/user/{id}/roles")
    public ResponseEntity<?> getUserRoles(@PathVariable Long id) {
        // 呼叫 Service 取得角色
        return ResponseEntity.ok(roleAssignmentService.getUserRoles(id));
    }

}
