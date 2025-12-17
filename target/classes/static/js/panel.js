const token = localStorage.getItem('jwtToken');
const username = localStorage.getItem('username');
const role = localStorage.getItem('role');
const userId = localStorage.getItem('userId');

document.addEventListener("DOMContentLoaded", function() {
    if (!token) window.location.href = '/';

    const headerUsername = document.getElementById('headerUsername');
    if (headerUsername) headerUsername.innerText = username;

    if (role === 'ADMIN') {
        const adminPanel = document.getElementById('adminPanel');
        if (adminPanel) adminPanel.style.display = 'block';
    }

    const wrapper = document.getElementById("wrapper");
    const toggleButton = document.getElementById("menu-toggle");

    if (toggleButton && wrapper) {
        toggleButton.onclick = function () {
            wrapper.classList.toggle("toggled");
        };
    }
    loadBooks();

    if (role === 'ADMIN') {
        const a = document.getElementById('adminBooksActions');
        const b = document.getElementById('adminAuthorsActions');
        const c = document.getElementById('adminCategoriesActions');

        if (a) a.style.display = 'block';
        if (b) b.style.display = 'block';
        if (c) c.style.display = 'block';
    }
});


function showSection(sectionId, element) {
    document.querySelectorAll('.content-section').forEach(el => el.classList.remove('active'));
    const target = document.getElementById(sectionId);
    if (target) target.classList.add('active');

    if (element) {
        document.querySelectorAll('#sidebar-wrapper .list-group-item').forEach(el => el.classList.remove('active'));
        element.classList.add('active');
    }

    if (sectionId === 'rentedSection') {
        loadRentedBooks();
    } else if (sectionId === 'penaltySection') {
        loadPenalties();
    } else if (sectionId === 'authorsSection') {
        loadAuthors();
    } else if (sectionId === 'categoriesSection') {
        loadCategories();
    } else if (sectionId === 'booksSection') {
        loadBooks();
    }
}


async function globalSearch(event) {
    event.preventDefault();
    const input = document.getElementById('globalSearchInput');
    if (!input) return;
    const query = input.value.trim();
    if (!query) return;

    try {
        const res = await fetch(`/api/kitaplar/arama?keyword=${encodeURIComponent(query)}`, {
            headers: { 'Authorization': 'Bearer ' + token }
        });

        if (res.ok) {
            const results = await res.json();
            window.allBooks = results;
            const booksMenuItem = document.querySelector('a[data-section="booksSection"]');
            showSection('booksSection', booksMenuItem);
            renderBooks(results);

            if (!results || results.length === 0) {
                alert("Bu isimde bir kitap bulunamadı.");
            }
        } else {
            alert("Arama sırasında hata oluştu.");
        }
    } catch (e) {
        console.error(e);
        alert("Sunucu hatası.");
    }
}


async function loadBooks() {
    const tableBody = document.getElementById('bookList');
    if (!tableBody) return;

    try {
        const res = await fetch('/api/kitaplar', {
            headers: { 'Authorization': 'Bearer ' + token }
        });
        window.allBooks = await res.json();
        renderBooks(window.allBooks);
    } catch (e) {
        console.error(e);
    }
}

function renderBooks(books) {
    const tableBody = document.getElementById('bookList');
    if (!tableBody) return;

    tableBody.innerHTML = '';
    books.forEach((book, index) => {
        const stokDurum = book.kopyaSayisi > 0
            ? `<span class="badge bg-success">${book.kopyaSayisi} Adet</span>`
            : `<span class="badge bg-danger">Tükendi</span>`;

        tableBody.innerHTML += `
            <tr>
                <th scope="row">${index + 1}</th>
                <td class="fw-bold">${book.ad}</td>
                <td>${book.yazar ? book.yazar.ad + ' ' + book.yazar.soyad : '-'}</td>
                <td><span class="badge bg-secondary">${book.kategori ? book.kategori.ad : '-'}</span></td>
                <td>${book.yayinYili}</td>
                <td>${stokDurum}</td>
                <td>
                    <button class="btn btn-sm btn-outline-primary" onclick="oduncAl('${book.id}')">Ödünç Al</button>
                </td>
            </tr>
        `;
    });
}

function filterBooks(query) {
    if (!window.allBooks) return;
    const lowerQuery = query.toLowerCase();
    const filtered = window.allBooks.filter(book =>
        book.ad.toLowerCase().includes(lowerQuery) ||
        (book.yazar && book.yazar.ad.toLowerCase().includes(lowerQuery)) ||
        (book.yazar && book.yazar.soyad.toLowerCase().includes(lowerQuery)) ||
        (book.kategori && book.kategori.ad.toLowerCase().includes(lowerQuery))
    );
    renderBooks(filtered);
}

async function oduncAl(kitapId) {
    document.getElementById('modalKitapId').value = kitapId;

    const dateInput = document.getElementById('selectedDate');
    const today = new Date().toISOString().split('T')[0];
    dateInput.min = today;
    dateInput.value = today;

    const dateModal = new bootstrap.Modal(document.getElementById('dateModal'));
    dateModal.show();
}


async function submitOduncAl() {
    const kitapId = document.getElementById('modalKitapId').value;
    const iadeTarihi = document.getElementById('selectedDate').value;

    if (!iadeTarihi) {
        alert("Lütfen geçerli bir tarih seçin.");
        return;
    }

    const data = {
        kullaniciId: parseInt(userId),
        kitapId: parseInt(kitapId),
        iadeTarihi: iadeTarihi
    };

    try {
        const res = await fetch('/api/odunc/al', {
            method: 'POST',
            headers: {
                'Authorization': 'Bearer ' + token,
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(data)
        });

        if (res.ok) {
            alert("Kitap başarıyla kiralandı!");

            const modalEl = document.getElementById('dateModal');
            const modal = bootstrap.Modal.getInstance(modalEl);
            if (modal) modal.hide();

            loadBooks();
            if (typeof loadRentedBooks === "function") {
                loadRentedBooks();
            }
        }
        else {
            try {
                const errorData = await res.json();
                alert("Hata: " + (errorData.hata || "İşlem başarısız."));
            }
            catch (jsonErr) {
                alert("İşlem sırasında bir hata oluştu (Kod: " + res.status + ")");
            }
        }
    }
    catch (e) {
        console.error("İşlem hatası detayı:", e);
        loadBooks();
        loadRentedBooks();
        alert("İşlem tamamlandı fakat liste güncellenirken bir sorun oluştu. Lütfen sayfayı yenileyin.");
    }
}


async function loadAuthors() {
    const tableBody = document.getElementById('authorList');
    if (!tableBody) return;

    tableBody.innerHTML = '<tr><td colspan="3" class="text-center">Yükleniyor...</td></tr>';

    try {
        const res = await fetch('/api/admin/yazarlar', {
            headers: { 'Authorization': 'Bearer ' + token }
        });

        if (res.ok) {
            window.allAuthors = await res.json();
            renderAuthors(window.allAuthors);
        } else {
            tableBody.innerHTML = '<tr><td colspan="3" class="text-center text-danger">Veriler alınamadı.</td></tr>';
        }
    } catch (e) {
        console.error(e);
        tableBody.innerHTML = '<tr><td colspan="3" class="text-center text-danger">Bağlantı hatası.</td></tr>';
    }
}

function renderAuthors(authors) {
    const tableBody = document.getElementById('authorList');
    if (!tableBody) return;

    tableBody.innerHTML = '';
    authors.forEach((yazar, index) => {
        tableBody.innerHTML += `
            <tr>
                <th>${index + 1}</th>
                <td class="fw-bold">${yazar.ad} ${yazar.soyad}</td>
                <td>${yazar.biyografi || '-'}</td>
            </tr>
        `;
    });
}

function filterAuthors(query) {
    if (!window.allAuthors) return;
    const lowerQuery = query.toLowerCase();
    const filtered = window.allAuthors.filter(y =>
        (y.ad && y.ad.toLowerCase().includes(lowerQuery)) ||
        (y.soyad && y.soyad.toLowerCase().includes(lowerQuery))
    );
    renderAuthors(filtered);
}


async function loadCategories() {
    const tableBody = document.getElementById('categoryList');
    if (!tableBody) return;

    tableBody.innerHTML = '<tr><td colspan="2" class="text-center">Yükleniyor...</td></tr>';

    try {
        const res = await fetch('/api/admin/kategoriler', {
            headers: { 'Authorization': 'Bearer ' + token }
        });

        if (res.ok) {
            window.allCategories = await res.json();
            renderCategories(window.allCategories);
        } else {
            tableBody.innerHTML = '<tr><td colspan="2" class="text-center text-danger">Veriler alınamadı.</td></tr>';
        }
    } catch (e) {
        console.error(e);
        tableBody.innerHTML = '<tr><td colspan="2" class="text-center text-danger">Bağlantı hatası.</td></tr>';
    }
}

function renderCategories(categories) {
    const tableBody = document.getElementById('categoryList');
    if (!tableBody) return;

    tableBody.innerHTML = '';
    categories.forEach((kategori, index) => {
        tableBody.innerHTML += `
            <tr>
                <th>${index + 1}</th>
                <td class="fw-bold">${kategori.ad}</td>
            </tr>
        `;
    });
}

function filterCategories(query) {
    if (!window.allCategories) return;
    const lowerQuery = query.toLowerCase();
    const filtered = window.allCategories.filter(k =>
        k.ad && k.ad.toLowerCase().includes(lowerQuery)
    );
    renderCategories(filtered);
}




async function loadRentedBooks() {
    const tableBody = document.getElementById('rentedBookList');
    const noDataMsg = document.getElementById('noRentedData');
    if (!tableBody) return;

    tableBody.innerHTML = '<tr><td colspan="6" class="text-center">Yükleniyor...</td></tr>';
    if (noDataMsg) noDataMsg.style.display = 'none';

    try {
        const res = await fetch(`/api/odunc/user/${userId}`, {
            headers: { 'Authorization': 'Bearer ' + token }
        });

        if (!res.ok) {
            tableBody.innerHTML = '<tr><td colspan="6" class="text-center text-danger">Veriler alınamadı.</td></tr>';
            return;
        }

        const loans = await res.json();
        window.myRentedBooks = loans;
        renderRentedBooks(loans);
    } catch (e) {
        console.error(e);
        tableBody.innerHTML = '<tr><td colspan="6" class="text-center text-danger">Bağlantı hatası.</td></tr>';
    }
}


function renderRentedBooks(loans) {
    const tableBody = document.getElementById('rentedBookList');
    const noDataMsg = document.getElementById('noRentedData');


    if (!tableBody) return;

    tableBody.innerHTML = '';


    if (!loans || loans.length === 0) {
        if (noDataMsg) noDataMsg.style.display = 'block';
        return;
    }
    if (noDataMsg) noDataMsg.style.display = 'none';

    loans.forEach((loan, index) => {
        const iadeTarihi = new Date(loan.beklenenIadeTarihi);
        const oduncTarihi = new Date(loan.oduncTarihi);
        const bugun = new Date();


        const diffTime = iadeTarihi - bugun;
        const kalanGun = Math.ceil(diffTime / (1000 * 60 * 60 * 24));

        let kalanSureBadge = '';

        if (loan.gercekIadeTarihi) {
            kalanSureBadge = `<span class="badge bg-secondary">İade Edildi</span>`;
        } else {
            if (kalanGun < 0) {
                kalanSureBadge = `<span class="badge bg-danger">${Math.abs(kalanGun)} Gün Gecikti!</span>`;
            } else if (kalanGun <= 3) {
                kalanSureBadge = `<span class="badge bg-warning text-dark">${kalanGun} Gün Kaldı</span>`;
            } else {
                kalanSureBadge = `<span class="badge bg-success">${kalanGun} Gün Kaldı</span>`;
            }
        }

        const islemButonlari = loan.gercekIadeTarihi ?
            `<span class="text-muted small"><i class="fas fa-check"></i> Tamamlandı</span>` :
            `
            <button class="btn btn-sm btn-outline-danger" onclick="iadeEt('${loan.id}')">
                <i class="fas fa-undo me-1"></i> İade Et
            </button>
            `;

        tableBody.innerHTML += `
            <tr>
                <th scope="row">${index + 1}</th>
                <td class="fw-bold">${loan.kitap.ad}</td>
                <td>${oduncTarihi.toLocaleDateString('tr-TR')}</td>
                <td>${iadeTarihi.toLocaleDateString('tr-TR')}</td>
                <td>${kalanSureBadge}</td>
                <td class="text-end">
                    ${islemButonlari}
                </td>
            </tr>
        `;
    });
}

function filterRentedBooks(query) {
    if (!window.myRentedBooks) return;
    const lowerQuery = query.toLowerCase();

    const filtered = window.myRentedBooks.filter(loan =>
        loan.kitap.ad.toLowerCase().includes(lowerQuery) ||
        (loan.kitap.yazar && loan.kitap.yazar.ad.toLowerCase().includes(lowerQuery)) ||
        (loan.kitap.yazar && loan.kitap.yazar.soyad.toLowerCase().includes(lowerQuery))
    );
    renderRentedBooks(filtered);
}

async function iadeEt(oduncId) {
    if (!confirm("Kitabı iade etmek istediğinize emin misiniz?")) return;

    try {
        const res = await fetch(`/api/odunc/iade/${oduncId}`, {
            method: 'PUT',
            headers: { 'Authorization': 'Bearer ' + token }
        });

        if (res.ok) {
            alert("İade işlemi başarılı.");
            loadRentedBooks();
            loadBooks();
        } else {
            alert("İade işlemi sırasında hata oluştu.");
        }
    } catch (e) {
        console.error(e);
        alert("Sunucu hatası.");
    }
}


async function loadPenalties() {
    const tableBody = document.getElementById('penaltyList');
    if (!tableBody) return;

    tableBody.innerHTML = '<tr><td colspan="6" class="text-center">Yükleniyor...</td></tr>';

    try {
        const res = await fetch(`/api/odunc/cezalar/kullanici/${userId}`, {
            headers: { 'Authorization': 'Bearer ' + token }
        });

        if (res.ok) {
            const cezalar = await res.json();
            renderPenalties(cezalar);
        } else {
            tableBody.innerHTML = '<tr><td colspan="6" class="text-center text-danger">Veriler alınamadı.</td></tr>';
        }
    } catch (e) {
        console.error(e);
        tableBody.innerHTML = '<tr><td colspan="6" class="text-center text-danger">Bağlantı hatası.</td></tr>';
    }
}

function renderPenalties(cezalar) {
    const tableBody = document.getElementById('penaltyList');
    const noDataMsg = document.getElementById('noPenaltyData');
    if (!tableBody) return;

    tableBody.innerHTML = '';

    if (!cezalar || cezalar.length === 0) {
        if (noDataMsg) noDataMsg.style.display = 'block';
        return;
    }
    if (noDataMsg) noDataMsg.style.display = 'none';

    cezalar.forEach((ceza, index) => {
        const durumBadge = ceza.odendiMi
            ? `<span class="badge bg-success">Ödendi</span>`
            : `<span class="badge bg-danger">Ödenmedi</span>`;

        const islemButonu = ceza.odendiMi
            ? `<span class="text-muted small"><i class="fas fa-check-circle"></i> Tamam</span>`
            : `<button class="btn btn-sm btn-outline-success" onclick="payFine('${ceza.id}', ${ceza.cezaMiktari})">
                 <i class="fas fa-credit-card me-1"></i> Öde
               </button>`;

        tableBody.innerHTML += `
            <tr>
                <th>${index + 1}</th>
                <td class="fw-bold">${ceza.oduncIslemi.kitap.ad}</td>
                <td>${new Date(ceza.cezaTarihi).toLocaleDateString('tr-TR')}</td>
                <td class="fw-bold text-danger">${ceza.cezaMiktari} TL</td>
                <td>${durumBadge}</td>
                <td class="text-end">${islemButonu}</td>
            </tr>
        `;
    });
}

async function payFine(cezaId, miktar) {
    if (!confirm(`${miktar} TL tutarındaki cezayı ödemek istiyor musunuz?`)) return;

    try {
        const res = await fetch(`/api/odunc/ceza/odeme/${cezaId}`, {
            method: 'PUT',
            headers: { 'Authorization': 'Bearer ' + token }
        });

        if (res.ok) {
            alert("Ödeme işlemi başarılı! Teşekkür ederiz.");
            loadPenalties();
        } else {
            alert("Ödeme sırasında hata oluştu.");
        }
    } catch (e) {
        console.error(e);
        alert("Sunucu hatası.");
    }
}


async function prepareBookModal() {
    const resYazar = await fetch('/api/admin/yazarlar', {
        headers: { 'Authorization': 'Bearer ' + token }
    });
    const yazarlar = await resYazar.json();
    const yazarSelect = document.getElementById('kitapYazarSelect');
    yazarSelect.innerHTML = '';
    yazarlar.forEach(y => yazarSelect.innerHTML += `<option value="${y.id}">${y.ad} ${y.soyad}</option>`);

    const resKat = await fetch('/api/admin/kategoriler', {
        headers: { 'Authorization': 'Bearer ' + token }
    });
    const kategoriler = await resKat.json();
    const katSelect = document.getElementById('kitapKategoriSelect');
    katSelect.innerHTML = '';
    kategoriler.forEach(k => katSelect.innerHTML += `<option value="${k.id}">${k.ad}</option>`);
}

async function addKitap() {
    const data = {
        ad: document.getElementById('kitapAd').value,
        isbn: "",
        yayinYili: parseInt(document.getElementById('kitapYil').value),
        kopyaSayisi: parseInt(document.getElementById('kitapStok').value),
        yazar: { id: parseInt(document.getElementById('kitapYazarSelect').value) },
        kategori: { id: parseInt(document.getElementById('kitapKategoriSelect').value) }
    };
    await sendPostRequest('/api/admin/kitap', data, 'kitapModal');
    loadBooks();
}

async function addYazar() {
    const data = {
        ad: document.getElementById('yazarAd').value,
        soyad: document.getElementById('yazarSoyad').value,
        biyografi: document.getElementById('yazarBio').value
    };
    await sendPostRequest('/api/admin/yazar', data, 'yazarModal');
    loadAuthors();
}

async function addKategori() {
    const data = {
        ad: document.getElementById('kategoriAd').value,
        aciklama: document.getElementById('kategoriAciklama').value
    };
    await sendPostRequest('/api/admin/kategori', data, 'kategoriModal');
    loadCategories();
}

async function sendPostRequest(url, data, modalId) {
    try {
        const res = await fetch(url, {
            method: 'POST',
            headers: {
                'Authorization': 'Bearer ' + token,
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(data)
        });
        if (res.ok) {
            alert("Kayıt başarılı!");
            const modalEl = document.getElementById(modalId);
            const modal = bootstrap.Modal.getInstance(modalEl);
            modal.hide();
        } else {
            alert("Hata oluştu! Lütfen bilgileri kontrol edin.");
        }
    } catch (e) {
        console.error(e);
        alert("Sunucu hatası.");
    }
}

function logout() {
    if (confirm("Çıkış yapılıyor...")) {
        localStorage.clear();
        window.location.href = '/';
    }
}
