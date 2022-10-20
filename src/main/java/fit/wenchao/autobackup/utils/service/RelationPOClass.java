package fit.wenchao.autobackup.utils.service;

import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Component
@interface RelationPOClass {
}