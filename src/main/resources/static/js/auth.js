async function handleLogin(event) {
    event.preventDefault();
    const alertBox = document.getElementById('alertBox');

    const data = {
        kullaniciAdi: document.getElementById('username').value,
        sifre: document.getElementById('password').value
    };

    try {
        const response = await fetch('/api/auth/login', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(data)
        });

        if (response.ok) {
            const result = await response.json();
            localStorage.setItem('jwtToken', result.token);
            localStorage.setItem('username', result.kullaniciAdi);
            localStorage.setItem('role', result.rol);
            localStorage.setItem('userId', result.id);

            alertBox.className = 'alert alert-success';
            alertBox.textContent = 'Giriş Başarılı! Yönlendiriliyorsunuz...';
            setTimeout(() => window.location.href = "/panel", 1000);
        } else {
            alertBox.className = 'alert alert-danger';
            alertBox.textContent = 'Hatalı kullanıcı adı veya şifre!';
        }
    } catch (error) {
        console.error(error);
        alertBox.className = 'alert alert-danger';
        alertBox.textContent = 'Sunucuya bağlanılamadı!';
    }
}

async function handleRegister(event) {
    event.preventDefault();

    const data = {
        ad: document.getElementById('ad').value,
        soyad: document.getElementById('soyad').value,
        mail: document.getElementById('mail').value,
        kullaniciAdi: document.getElementById('kullaniciAdi').value,
        sifreHash: document.getElementById('sifre').value,
    };

    try {
        const response = await fetch('/api/auth/register', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(data)
        });

        if (response.ok) {
            alert("Kayıt Başarılı! Giriş sayfasına yönlendiriliyorsunuz.");
            window.location.href = "/";
        } else {
            alert("Kayıt başarısız! Bilgileri kontrol edin veya farklı bir kullanıcı adı deneyin.");
        }
    } catch (error) {
        console.error(error);
        alert("Sunucu hatası!");
    }
}