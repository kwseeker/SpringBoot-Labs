package cn.iocoder.springboot.lab27.springwebflux.controller;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

public class StringParamValidator  implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        System.out.println(clazz.getName());
        return clazz.isAssignableFrom(String.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        System.out.println("exec validate String param");
        String strParam = (String) target;
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "param", "param.invalid", "should not be blank");
    }
}
