package com.example.prize;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmailUtils {

    // תבנית אימייל - מתחיל באות, כולל תווים, ומכיל @ עם סיומת דומיין
    private static final String EMAIL_REGEX = "^[a-zA-Z][a-zA-Z0-9._-]*\\@\\w+(\\.)*\\w+\\.\\w+$";
    private static final Pattern emailPattern = Pattern.compile(EMAIL_REGEX);  // קומפילציה מראש

    // פונקציה לבדוק אם כתובת אימייל חוקית
    public static boolean isValidEmail(String email) {
        return emailPattern.matcher(email).matches();  // החזרת תוצאה ישירה
    }
}

