package com.springboot.backend.apirest.controllers;


import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.NestedRuntimeException;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import com.springboot.backend.apirest.models.entity.Cliente;
import com.springboot.backend.apirest.models.services.IClienteService;


@CrossOrigin(origins= {"http://localhost:4200"})
@RestController
@RequestMapping("/api")
public class ClienteRestController {

	@Autowired
	private IClienteService clienteService;
	
	@GetMapping("/clientes")
	public List<Cliente> index(){
		return clienteService.findAll();
	}
	

	@GetMapping("/clientes/page/{page}")
	public Page<Cliente> index(@PathVariable Integer page){
		return clienteService.findAll(PageRequest.of(page,  4));
	}
	
	@GetMapping("/clientes/{id}")
    public ResponseEntity<?> show(@PathVariable Long id) {
 
        Cliente cliente = null;
        Map<String,Object> response = new HashMap<>();
 
        try {   
            cliente = clienteService.findById(id);
 
        } catch (NoSuchElementException e) {
        	 response.put("mensaje", "El cliente con el ID ".concat(id.toString().concat(" no existe en la base de datos")));
            return new ResponseEntity<Map<String,Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (DataAccessException e) {
            response.put("mensaje", "Error al realizar la consulta a la base de datos");
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String,Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            response.put("mensaje", "Error al realizar la consulta a la base de datos");
            response.put("error", e.getMessage());
            return new ResponseEntity<Map<String,Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
        }

            if(cliente==null){
                response.put("mensaje", "El cliente con el ID".concat(id.toString().concat("No existe en la base de datos")));
                return new ResponseEntity<Map<String,Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
            }
 
            return new ResponseEntity<Cliente>(cliente,HttpStatus.OK);
    }
	
	
	@PostMapping("/clientes")
	public ResponseEntity<?> create(@Valid @RequestBody Cliente cliente, BindingResult result) {

		Cliente clienteNuevo =null;
        Map<String,Object> response = new HashMap<>();
        
		if (result.hasErrors()) {
			
			List<String> errors= result.getFieldErrors()
					.stream()
					.map(err -> "El campo "+err.getField()+"  "+err.getDefaultMessage())
					.collect(Collectors.toList());
			
			response.put("errors", errors);
			 return new ResponseEntity<Map<String,Object>>(response,HttpStatus.BAD_REQUEST);
		}

		try {
			clienteNuevo =clienteService.save(cliente);
		} catch (DataAccessException e) {
            response.put("mensaje", "Error al realizar la inserción a la base de datos");
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String,Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
        }catch (NoSuchElementException e) {
       	 response.put("mensaje", "El cliente no existe en la base de datos");
         return new ResponseEntity<Map<String,Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
        }
		response.put("mensaje", "El cliente ha sido creado con éxito");
		response.put("cliente", clienteNuevo);
		return new ResponseEntity<Map<String,Object>>(response, HttpStatus.CREATED);
	}

	
	@PutMapping("/clientes/{id}")
	public ResponseEntity update(@Valid @RequestBody Cliente cliente, BindingResult result, @PathVariable Long id) {

		Cliente clienteUpdated =null;
        Map<String,Object> response = new HashMap<>();

		if (result.hasErrors()) {
			
			List<String> errors= result.getFieldErrors()
					.stream()
					.map(err -> "El campo "+err.getField()+"  "+err.getDefaultMessage())
					.collect(Collectors.toList());
			
			response.put("errors", errors);
			 return new ResponseEntity<Map<String,Object>>(response,HttpStatus.BAD_REQUEST);
		}

		
		try {
			Cliente clienteActual=clienteService.findById(id);

			clienteActual.setApellido(cliente.getApellido());
			clienteActual.setNombre(cliente.getNombre());
			clienteActual.setEmail(cliente.getEmail());
			clienteActual.setCreateAt(cliente.getCreateAt());
 
			clienteUpdated = clienteService.save(clienteActual);
 
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al actualizar el cliente en la base de datos");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		catch (NoSuchElementException e) {
       	 response.put("mensaje", "El cliente no existe en la base de datos");
           return new ResponseEntity<Map<String,Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
       } 
		
		response.put("", "El cliente ha sido actualizado con éxito");
		response.put("cliente", clienteUpdated);
		return new ResponseEntity<Map<String,Object>>(response, HttpStatus.CREATED);
	}

	
	@DeleteMapping("/clientes/{id}")
	public ResponseEntity<?> delete(@PathVariable Long id)
	{
		Map<String,Object> response=new HashMap<>();
		try {
		clienteService.delete(id);
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al eliminar el cliente en la base de datos");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		} 
		response.put("", "El cliente ha sido eliminado con éxito");
		return new ResponseEntity<Map<String,Object>>(response, HttpStatus.OK);
	}
}

