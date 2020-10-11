package com.github.raphaelmorsch.ifood.cadastro.infra;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Documented
@Retention(RUNTIME)
@Target({ TYPE, ANNOTATION_TYPE, METHOD, FIELD })
@Constraint(validatedBy = ValidDTOValidator.class)
public @interface ValidDTO {

	String message() default "{com.github.raphaelmorsch.ifood.cadastro.infra.ValidDTO.message}";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
