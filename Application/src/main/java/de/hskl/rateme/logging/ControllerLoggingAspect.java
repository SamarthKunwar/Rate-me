package de.hskl.rateme.logging;

import java.lang.reflect.RecordComponent;
import java.util.Arrays;
import java.util.StringJoiner;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Aspect
@Component
public class ControllerLoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(ControllerLoggingAspect.class);
    private static final int MAX_STRING_LENGTH = 120;

    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
    public void controllerMethods() {
    }

    @Before("controllerMethods()")
    public void logControllerCall(JoinPoint joinPoint) {
        String className = joinPoint.getSignature().getDeclaringType().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        String arguments = formatArguments(joinPoint.getArgs());

        logger.info("Controller call: {}.{}({})", className, methodName, arguments);
    }

    private String formatArguments(Object[] arguments) {
        return Arrays.stream(arguments)
                .map(this::formatArgument)
                .reduce((first, second) -> first + ", " + second)
                .orElse("");
    }

    private String formatArgument(Object argument) {
        if (argument == null) {
            return "null";
        }

        if (argument instanceof MultipartFile file) {
            return formatMultipartFile(file);
        }

        if (argument instanceof String text) {
            return formatString(text);
        }

        if (argument instanceof Number || argument instanceof Boolean) {
            return argument.toString();
        }

        if (argument instanceof byte[]) {
            return "<byte[]>";
        }

        if (argument.getClass().isRecord()) {
            return formatRecord(argument);
        }

        return argument.getClass().getSimpleName();
    }

    private String formatMultipartFile(MultipartFile file) {
        return "MultipartFile{name=%s, size=%d, contentType=%s}"
                .formatted(file.getOriginalFilename(), file.getSize(), file.getContentType());
    }

    private String formatString(String text) {
        if (looksLikeToken(text)) {
            return "<redacted token>";
        }

        return "\"" + shorten(text) + "\"";
    }

    private boolean looksLikeToken(String text) {
        return text.matches("[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}");
    }

    private String formatRecord(Object record) {
        StringJoiner joiner = new StringJoiner(", ",
                record.getClass().getSimpleName() + "{",
                "}");

        for (RecordComponent component : record.getClass().getRecordComponents()) {
            String name = component.getName();
            Object value = readRecordComponent(record, component);

            joiner.add(name + "=" + formatRecordValue(name, value));
        }

        return joiner.toString();
    }

    private Object readRecordComponent(Object record, RecordComponent component) {
        try {
            return component.getAccessor().invoke(record);
        } catch (ReflectiveOperationException e) {
            return "<unreadable>";
        }
    }

    private String formatRecordValue(String name, Object value) {
        if (isSensitiveName(name)) {
            return "<redacted>";
        }

        return formatArgument(value);
    }

    private boolean isSensitiveName(String name) {
        String normalizedName = name.toLowerCase();
        return normalizedName.contains("password")
                || normalizedName.contains("token")
                || normalizedName.contains("hash")
                || normalizedName.contains("salt");
    }

    private String shorten(String text) {
        if (text.length() <= MAX_STRING_LENGTH) {
            return text;
        }

        return text.substring(0, MAX_STRING_LENGTH) + "...";
    }
}
