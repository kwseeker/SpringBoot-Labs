package cn.iocoder.springboot.lab27.springwebflux.controller;

import cn.iocoder.springboot.lab27.springwebflux.dto.UserAddDTO;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class UserValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return UserAddDTO.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        System.out.println("validate ...");
        UserAddDTO dto = (UserAddDTO) target;
        System.out.println(dto);
        if (dto.getPassword().length() <= 6) {
            errors.rejectValue("password", "password.invalid", "password is too weak");
        }
        if (dto.getUsername().length() > 32) {
            errors.rejectValue("username", "username.invalid", "username must be in 32 letters");
        }
    }
}