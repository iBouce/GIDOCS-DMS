package com.ibouce.Elasticsearch.permission;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PermissionService {

    private final PermissionRepository permissionRepository;

    public PermissionService(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    public PermissionModel createPermission(PermissionModel permission) {
        return permissionRepository.save(permission);
    }

    public PermissionModel getPermissionById(Long id) {
        return permissionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Permission not found"));
    }

    public List<PermissionModel> getAllPermissions() {
        return permissionRepository.findAll();
    }
}

