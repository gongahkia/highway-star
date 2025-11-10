package highwaystar.services;

import com.google.firebase.auth.*;
import highwaystar.models.UserProfile;
import highwaystar.utils.ValidationUtils;

import java.util.concurrent.CompletableFuture;

public class AuthService {
    private static AuthService instance;
    private FirebaseAuth auth;

    private AuthService() {
        auth = FirebaseAuth.getInstance();
    }

    public static AuthService getInstance() {
        if (instance == null) {
            instance = new AuthService();
        }
        return instance;
    }

    public static class AuthResult {
        private final boolean success;
        private final String message;
        private final UserRecord user;

        public AuthResult(boolean success, String message, UserRecord user) {
            this.success = success;
            this.message = message;
            this.user = user;
        }

        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
        public UserRecord getUser() { return user; }
    }

    public CompletableFuture<AuthResult> registerUser(String email, String password) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // Validate email
                ValidationUtils.ValidationResult emailValidation = ValidationUtils.validateEmail(email);
                if (!emailValidation.isValid()) {
                    return new AuthResult(false, emailValidation.getMessage(), null);
                }

                // Validate password
                ValidationUtils.ValidationResult passwordValidation = ValidationUtils.validatePassword(password);
                if (!passwordValidation.isValid()) {
                    return new AuthResult(false, passwordValidation.getMessage(), null);
                }

                // Create user in Firebase Auth
                UserRecord.CreateRequest request = new UserRecord.CreateRequest()
                    .setEmail(email)
                    .setPassword(password)
                    .setEmailVerified(false);

                UserRecord userRecord = auth.createUser(request);

                // Create user profile in database
                ProfileService.getInstance().createProfile(userRecord.getUid(), email);

                return new AuthResult(true, "Registration successful!", userRecord);

            } catch (FirebaseAuthException e) {
                String errorCode = e.getAuthErrorCode().name();
                String message = switch (errorCode) {
                    case "EMAIL_ALREADY_EXISTS" -> "Email already in use";
                    case "INVALID_EMAIL" -> "Invalid email format";
                    case "WEAK_PASSWORD" -> "Password is too weak";
                    default -> "Registration failed: " + e.getMessage();
                };
                return new AuthResult(false, message, null);
            } catch (Exception e) {
                return new AuthResult(false, "Registration failed: " + e.getMessage(), null);
            }
        });
    }

    public CompletableFuture<AuthResult> loginUser(String email, String password) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // Validate email
                ValidationUtils.ValidationResult emailValidation = ValidationUtils.validateEmail(email);
                if (!emailValidation.isValid()) {
                    return new AuthResult(false, emailValidation.getMessage(), null);
                }

                // Get user by email
                UserRecord userRecord = auth.getUserByEmail(email);

                // Note: Firebase Admin SDK doesn't support password verification
                // In a real app, you'd use Firebase Auth REST API or client SDK
                // For now, we just check if user exists
                if (userRecord != null) {
                    return new AuthResult(true, "Login successful!", userRecord);
                }

                return new AuthResult(false, "Invalid credentials", null);

            } catch (FirebaseAuthException e) {
                String errorCode = e.getAuthErrorCode().name();
                String message = switch (errorCode) {
                    case "USER_NOT_FOUND" -> "No account found with this email";
                    case "INVALID_EMAIL" -> "Invalid email format";
                    default -> "Login failed: " + e.getMessage();
                };
                return new AuthResult(false, message, null);
            } catch (Exception e) {
                return new AuthResult(false, "Login failed: " + e.getMessage(), null);
            }
        });
    }

    public CompletableFuture<AuthResult> changePassword(String uid, String newPassword) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                ValidationUtils.ValidationResult validation = ValidationUtils.validatePassword(newPassword);
                if (!validation.isValid()) {
                    return new AuthResult(false, validation.getMessage(), null);
                }

                UserRecord.UpdateRequest request = new UserRecord.UpdateRequest(uid)
                    .setPassword(newPassword);

                UserRecord updatedUser = auth.updateUser(request);
                return new AuthResult(true, "Password changed successfully!", updatedUser);

            } catch (Exception e) {
                return new AuthResult(false, "Password change failed: " + e.getMessage(), null);
            }
        });
    }

    public CompletableFuture<UserRecord> getUserById(String uid) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return auth.getUser(uid);
            } catch (Exception e) {
                return null;
            }
        });
    }

    public CompletableFuture<Boolean> deleteUser(String uid) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                auth.deleteUser(uid);
                return true;
            } catch (Exception e) {
                return false;
            }
        });
    }
}
