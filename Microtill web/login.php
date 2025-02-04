<?php
session_start();
require_once 'config.php';

if ($_SERVER["REQUEST_METHOD"] == "POST") {
    $email = $_POST['email'];
    $password = $_POST['password'];
    
    $sql = "SELECT id, email, password FROM users WHERE email = ?";
    
    if ($stmt = $mysqli->prepare($sql)) {
        $stmt->bind_param("s", $email);
        
        if ($stmt->execute()) {
            $stmt->store_result();
            
            if ($stmt->num_rows == 1) {
                $stmt->bind_result($id, $email, $hashed_password);
                if ($stmt->fetch()) {
                    if (password_verify($password, $hashed_password)) {
                        $_SESSION["loggedin"] = true;
                        $_SESSION["id"] = $id;
                        $_SESSION["email"] = $email;
                        header("location: dashboard.php");
                    } else {
                        $login_err = "Invalid email or password.";
                    }
                }
            } else {
                $login_err = "Invalid email or password.";
            }
        }
        $stmt->close();
    }
}
?>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Login - Elite Fitness</title>
    <link rel="stylesheet" href="styles.css">
</head>
<body class="light-mode">
    <div class="auth-container">
        <form class="auth-form" action="<?php echo htmlspecialchars($_SERVER["PHP_SELF"]); ?>" method="post">
            <h2>Login to Elite Fitness</h2>
            <?php 
            if(!empty($login_err)){
                echo '<div class="error-message">' . $login_err . '</div>';
            }        
            ?>
            <div class="form-group">
                <input type="email" name="email" placeholder="Email" required>
            </div>
            <div class="form-group">
                <input type="password" name="password" placeholder="Password" required>
            </div>
            <button type="submit" class="auth-button">Login</button>
            <p>Don't have an account? <a href="signup.php">Sign up here</a></p>
        </form>
    </div>
</body>
</html> 