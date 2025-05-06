document.addEventListener('DOMContentLoaded', () => {
    const userData = localStorage.getItem('userData');
    if (userData) {
        window.location.href = 'index.html';
    }
});

const authForm = document.getElementById('auth-form');
const authTitle = document.getElementById('auth-title');
const authButton = document.getElementById('auth-button');
const toggleAuthLink = document.getElementById('toggle-auth-link');
const errorMessage = document.getElementById('error-message');

let isLoginMode = true;

toggleAuthLink.addEventListener('click', (e) => {
    e.preventDefault();
    isLoginMode = !isLoginMode;
    authTitle.textContent = isLoginMode ? 'Login' : 'Sign Up';
    authButton.textContent = isLoginMode ? 'Login' : 'Sign Up';
    toggleAuthLink.textContent = isLoginMode ? 
        "Don't have an account? Sign up" : 
        "Already have an account? Login";
});

authForm.addEventListener('submit', async (e) => {
    e.preventDefault();
    const email = document.getElementById('email').value;
    const password = document.getElementById('password').value;

    try {
        const endpoint = isLoginMode ? '/api/auth/login' : '/api/auth/signup';
        const jwtToken = await generateJWT(email, password, isLoginMode ? 'user' : 'admin');
        const response = await fetch(`http://localhost:9090${endpoint}`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json',
                'Authorization': `Bearer ${jwtToken}`
            }
        });

        if (!response.ok) {
            let errorMessage;
            try {
                const errorData = await response.json();
                errorMessage = errorData.message || 'Authentication failed';
            } catch (e) {
                errorMessage = response.statusText || 'Authentication failed';
            }
            throw {
                message: errorMessage,
                status: response.status
            };
        }

        const data = await response.json();
        
        //changed
        
        localStorage.setItem('userData', JSON.stringify(data));
        
        window.location.href = 'index.html';
    } catch (error) {
        showError(error.message, error.status);
    }
});

async function generateJWT(email, password, role) {
    const header = {
        alg: 'RS256',
        typ: 'JWT'
    };

    const payload = {
        email: email,
        password: password,
        role: role,
        iat: Math.floor(Date.now() / 1000),
        exp: Math.floor(Date.now() / 1000) + (60 * 60) 
    };

    const privateKey = `------BEGIN PRIVATE KEY-----
MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQC9yWNwNbBPIeHC
NHNVGWhJ7i0v4Nw3dOPP9zCNFL1jffnc8EGpC3BZ9B4N3SeK5ephWhDhmJIwP/Cj
nINXBEGV9b5c0jl49d5iUdIfyvu4QtpmgYwk3FeLBW/JjqW9jGQ+j2Cjv0ChddBd
5sLM9X9LoJt4XYMTn3SHj5dJCoeV4cA/blSGnC4l3BoMVxazmj4FBNPqwMKUlVbH
8RuChH6dhVdUFKFHXFnNkD2YNbO2X2JFrRUQ0pI/mXS3Hqh73wxGnxuYf5JVA8cG
FfiSNyeqfUOMGlWc0toejqWAMCweubT5OF8MVgJDxQi8YoWhE+jz58fP3pcTNurn
ABn69VwxAgMBAAECggEAQSQjL+wVqIuiIBT9sytl2t5X/zOIkrZZzVN0hDAms0QV
o6qJdvrYpN+AA1Ux0eCJmsGdwrkqEEqaD6ZUhGUPF+hB3z0/AdAudQPNgiEbVksI
CKAVmL9SgwKqNH30UIOkEr8Gth2akvDLxLdEjLIE79HgGyfJqk4UYsU78ZOeJyi+
b94CfsT5isHVEiQE/+30Xm28w1vxwojOWKzmu0Dh/K5AJC3nB0MpBlfw+473HvNr
Tce5bzZmj6kwTvYUj7lRsZtp9Ryu0NBIcVp2ER8lo8KweJ6ch67kgYEnPxRI5pIx
0isx3D5XUTJR9lk2Y2AdOhp63YhwZCX7qaw0BAqFywKBgQDl3xTz0kUjqufB7n87
iW7hD34o0+bwWM4YwhRnGk97E15im/G5Y6dL67N3FQI8/er5SheJlgrscvYa/Qlo
mgxArFB4Cul9LRIKuq6kZ0XQKY5wVTL9u6rySOUE234weeT2u+kaNizzx5s7z+Ar
ex6/2Sks6I3PH3m2dzeevzQZ1wKBgQDTW+Zia3Qb6aSqSNaBg9Nx5Ru+6zWx8+o9
JZUmh5CecJJYTMtesHPnJ3jT4QRrkBZhiZ/mEdArJ/PuT0zsSGLp+nLs0KxN01iE
IUMzvObUMh/d7+e2IUTmr7TlqHkmEQlLSTge2VVQnH/kghZsAlHEyb22Sm5atBPh
nlkkqhbJNwKBgDywvGBveK+lFNFf/ZmjfHHRXFa9M7DrUWa5/xcFnEx7XaUdVSHE
TMw/q79Z/aBKzfoJdNNvmOy6oX2Ypx5F5cxBcnFvOBxDhQruJOPlHEqMXj4MUI0r
OkQDuOuiY9u6oxf9ntq7yPyIC9Ur2hzAweqXFEb2E1LHFaR2L2orQz87AoGBAJOr
yNqI0vXRl1/2RhD1Zt1Twr/wnHJ5xfX09TGXU/6vDkCtXwYIaOJfHLeEps70O/7n
5hUb6wLMBk1BkLAhnyJ0/qjg3xH9EQI4LzjNycUGRGeO/6irs/UtXEl9HS/whTrZ
rGE5XbdVsLGaBkpukGzauzbK6DFtUdq4BPZt9D+fAoGAdowi9mwIwcHmqiWfDUQ/
7avUI+XUuZ4TC+49ykgMZIiD89CDcMtad68zXUTg8zfyNUSu7bwIiWX8pi+1llSg
H60GE7nsg5suPnwhIhSuHSj8w/jqaY4ZaAEF/KBKbGAZz6fV/dRLoJWcbYrXnlFp
Khr9PN1wqdQs8ESoGeitRPU=
-----END PRIVATE KEY-----`;

    try {
        const sHeader = JSON.stringify(header);
        const sPayload = JSON.stringify(payload);
        const sJWT = KJUR.jws.JWS.sign("RS256", sHeader, sPayload, privateKey);
        return sJWT;
    } catch (error) {
        console.error('Error generating JWT:', error);
        throw error;
    }
}

function decodeJWT(token) {
    try {
        const decoded = KJUR.jws.JWS.parse(token);
        return decoded.payloadObj;
    } catch (error) {
        console.error('Error decoding JWT:', error);
        return null;
    }
}

function showError(error, statusCode) {
    let errorMessageText;
    
    switch(statusCode) {
        case 400:
            errorMessageText = 'Invalid request. Please check your input.';
            break;
        case 401:
            errorMessageText = 'Invalid credentials. Please try again.';
            break;
        case 403:
            errorMessageText = 'Access denied. You do not have permission.';
            break;
        case 404:
            errorMessageText = 'Resource not found. Please try again later.';
            break;
        case 409:
            errorMessageText = 'Email already exists. Please use a different email.';
            break;
        case 500:
            errorMessageText = 'Server error. Please try again later.';
            break;
        default:
            errorMessageText = typeof error === 'string' ? error : 'An unexpected error occurred. Please try again.';
    }

    const errorMessageElement = document.getElementById('error-message');
    if (errorMessageElement) {
        errorMessageElement.textContent = errorMessageText;
        console.log(`Error (${statusCode}):`, error);
        errorMessageElement.style.display = 'block';
        setTimeout(() => {
            errorMessageElement.style.display = 'none';
        }, 3000);
    } else {
        console.error('Error message element not found');
    }
}