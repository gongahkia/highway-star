package highwaystar.utils;

import java.util.regex.Pattern;

public class ValidationUtils {
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    private static final int MIN_PASSWORD_LENGTH = 6;

    public static class ValidationResult {
        private final boolean valid;
        private final String message;

        public ValidationResult(boolean valid, String message) {
            this.valid = valid;
            this.message = message;
        }

        public boolean isValid() { return valid; }
        public String getMessage() { return message; }
    }

    public static ValidationResult validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return new ValidationResult(false, "Email cannot be empty");
        }
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            return new ValidationResult(false, "Invalid email format");
        }
        return new ValidationResult(true, "Valid email");
    }

    public static ValidationResult validatePassword(String password) {
        if (password == null || password.isEmpty()) {
            return new ValidationResult(false, "Password cannot be empty");
        }
        if (password.length() < MIN_PASSWORD_LENGTH) {
            return new ValidationResult(false, "Password must be at least " + MIN_PASSWORD_LENGTH + " characters");
        }

        boolean hasUpper = false;
        boolean hasLower = false;
        boolean hasDigit = false;

        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) hasUpper = true;
            if (Character.isLowerCase(c)) hasLower = true;
            if (Character.isDigit(c)) hasDigit = true;
        }

        int strength = 0;
        if (hasUpper) strength++;
        if (hasLower) strength++;
        if (hasDigit) strength++;
        if (password.length() >= 8) strength++;

        if (strength < 2) {
            return new ValidationResult(false, "Weak password. Use a mix of uppercase, lowercase, and numbers");
        }

        return new ValidationResult(true, "Valid password");
    }

    public static String getPasswordStrength(String password) {
        if (password == null || password.isEmpty()) return "Empty";

        int strength = 0;
        if (password.length() >= 8) strength++;
        if (password.length() >= 12) strength++;
        if (password.matches(".*[A-Z].*")) strength++;
        if (password.matches(".*[a-z].*")) strength++;
        if (password.matches(".*[0-9].*")) strength++;
        if (password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*")) strength++;

        return switch (strength) {
            case 0, 1 -> "Very Weak";
            case 2, 3 -> "Weak";
            case 4 -> "Medium";
            case 5 -> "Strong";
            default -> "Very Strong";
        };
    }

    public static boolean isValidNumber(String str) {
        if (str == null || str.trim().isEmpty()) return false;
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isPositiveNumber(String str) {
        if (!isValidNumber(str)) return false;
        return Double.parseDouble(str) > 0;
    }
}
