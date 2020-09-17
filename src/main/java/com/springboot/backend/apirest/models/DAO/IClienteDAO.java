 package com.springboot.backend.apirest.models.DAO;

import org.springframework.data.jpa.repository.JpaRepository;

import com.springboot.backend.apirest.models.entity.Cliente;

public interface IClienteDAO extends JpaRepository<Cliente, Long> {

}
