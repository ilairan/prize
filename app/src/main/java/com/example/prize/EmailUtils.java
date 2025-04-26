package com.example.prize;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmailUtils {
    private static final String EMAIL_REGEX = "^[a-zA-Z][a-zA-Z0-9._-]*\\@\\w+(\\.)*\\w+\\.\\w+$";
    private static final Pattern emailPattern = Pattern.compile(EMAIL_REGEX);

    public static boolean isValidEmail(String email) {
        Matcher matcher = emailPattern.matcher(email);
        return matcher.matches();

    }
}
