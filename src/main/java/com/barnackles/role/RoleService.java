package com.barnackles.role;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;


    public Role findByRole(String role) {
        return roleRepository.findByRole(role);
    }

    public Role findById(Long id) {
        return roleRepository.findById(id);
    }

    public void saveRole(Role role) {
        roleRepository.save(role);
    }

    public void updateRole(Role role) {
        roleRepository.save(role);
    }

    public void deleteRole(Role role) {
        roleRepository.delete(role);
    }


}
