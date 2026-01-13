# GitHub Yedekleme Rehberi

Bu rehber, NetherBedrockRandomizer plugin'ini GitHub'a yedeklemek için adım adım talimatlar içerir.

---

## 🔧 Ön Hazırlık

### Git Kurulumu Kontrol

```bash
# Git kurulu mu?
git --version

# Kurulu değilse (Ubuntu/Debian):
sudo apt install git

# Veya (Fedora/RHEL):
sudo dnf install git
```

### GitHub Hesabı

1. **GitHub hesabın var mı?** → https://github.com/signup
2. **SSH Key oluştur** (önerilir) veya HTTPS kullan

---

## 📦 Adım 1: Git Repository Başlat

```bash
# Proje klasörüne git
cd /home/osturuk/plugins/NetherBedrockRandomizer

# Git repository'yi başlat
git init

# Kullanıcı bilgilerini ayarla (ilk kez ise)
git config user.name "osturuk"
git config user.email "your-email@example.com"
```

---

## 📋 Adım 2: İlk Commit

```bash
# Mevcut dosyaları stage'e al (.gitignore sayesinde target/ hariç)
git add .

# İlk commit
git commit -m "Initial commit: NetherBedrockRandomizer v1.0.0

- Folia 1.21.4-1.21.8 support
- High-performance bedrock randomizer
- Thread-safe chunk processing
- Comprehensive documentation
- Build tested and working"
```

---

## 🌐 Adım 3: GitHub'da Repository Oluştur

### Seçenek A: Web Üzerinden

1. https://github.com/new adresine git
2. Repository bilgilerini doldur:
   - **Repository name**: `NetherBedrockRandomizer`
   - **Description**: `High-performance bedrock randomizer for Folia servers - Prevents seed-based cheating`
   - **Public** veya **Private** seç
   - **README** ekleme (zaten var)
   - **.gitignore** ekleme (zaten var)
   - **License**: MIT (zaten var)
3. **Create repository** tıkla

### Seçenek B: GitHub CLI (gh komut varsa)

```bash
# GitHub CLI ile (kurulu ise)
gh repo create NetherBedrockRandomizer --public --source=. --remote=origin --push

# Description ekle
gh repo edit --description "High-performance bedrock randomizer for Folia servers"
```

---

## 🔗 Adım 4: Remote Ekle ve Push

### GitHub'dan aldığın URL'i kullan

```bash
# HTTPS (kullanıcı adı + password/token gerekir)
git remote add origin https://github.com/KULLANICI_ADIN/NetherBedrockRandomizer.git

# VEYA SSH (SSH key varsa - önerilir)
git remote add origin git@github.com:KULLANICI_ADIN/NetherBedrockRandomizer.git

# Remote'u kontrol et
git remote -v

# Main branch'i oluştur ve push et
git branch -M main
git push -u origin main
```

---

## ✅ Adım 5: Doğrulama

```bash
# Browser'da kontrol et
# https://github.com/KULLANICI_ADIN/NetherBedrockRandomizer

# Aşağıdakileri görmelisin:
# ✅ README.md görünüyor
# ✅ 7 Java dosyası
# ✅ pom.xml
# ✅ config.yml ve plugin.yml
# ✅ Dokümantasyon dosyaları
# ✅ target/ klasörü YOK (.gitignore sayesinde)
```

---

## 🏷️ Adım 6: Release Oluştur (Opsiyonel)

### GitHub Web UI'den

1. Repository sayfanda **Releases** → **Create a new release**
2. Tag version: `v1.0.0`
3. Release title: `NetherBedrockRandomizer v1.0.0`
4. Description:
```markdown
## NetherBedrockRandomizer v1.0.0

First stable release! 🎉

### Features
- ✅ Folia 1.21.4-1.21.8 support
- ✅ High-performance bedrock randomization
- ✅ Thread-safe chunk processing
- ✅ Zero memory leaks
- ✅ Configurable settings
- ✅ Command system (/nbr)
- ✅ Real-time statistics

### Installation
Download `NetherBedrockRandomizer-1.0.0.jar` and place in `plugins/` folder.

### Requirements
- Folia 1.21.4+ (or Paper 1.21.4+)
- Java 21

See [README.md](README.md) for full documentation.
```
5. **Attach JAR**: Upload `target/NetherBedrockRandomizer-1.0.0.jar`
6. **Publish release**

---

## 🔄 Gelecekte Güncelleme

```bash
# Değişiklikleri yap (kod düzenle, test et, etc.)

# Değişiklikleri gör
git status

# Değişen dosyaları stage'e al
git add .

# Commit
git commit -m "Fix: [açıklama]"

# Push
git push origin main
```

---

## 📊 Git Komutları Özet

```bash
# Durum kontrol
git status

# Değişiklikleri gör
git diff

# Log göster
git log --oneline

# Branch oluştur
git checkout -b feature/new-feature

# Branch değiştir
git checkout main

# Merge
git merge feature/new-feature

# Pull (remote'dan çek)
git pull origin main

# Push (remote'a gönder)
git push origin main
```

---

## 🛡️ Güvenlik Notları

### Şunları ASLA commit etme:
- ❌ Production server config'leri
- ❌ Database şifreleri
- ❌ API keys
- ❌ Custom seed değerleri (production)
- ❌ Server IP'leri

### .gitignore zaten bunları hariç tutuyor:
- ✅ `target/` - Build artifacts
- ✅ `.idea/` - IDE files
- ✅ `*.log` - Log files
- ✅ `server.properties` - Server configs

---

## 🎯 GitHub Best Practices

### 1. README.md İyileştirmeleri

README'ye şunları ekleyebilirsin:
- Badge'ler (build status, version, license)
- Screenshots/GIFs (bedrock randomization örneği)
- Installation video
- Discord link

### 2. Issues

GitHub Issues kullan:
- Bug reports
- Feature requests
- Questions

### 3. Wiki

Detaylı dokümantasyon için GitHub Wiki kullan:
- API documentation
- Configuration guide
- Performance tuning
- Troubleshooting

### 4. Actions (CI/CD)

GitHub Actions ile otomatik build:
```yaml
# .github/workflows/maven.yml
name: Java CI
on: [push, pull_request]
jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v2
      - name: Set up JDK 21
        uses: actions/setup-java@v2
        with:
          java-version: '21'
      - name: Build with Maven
        run: mvn clean package
```

---

## ✅ Tamamlandı!

Artık plugin'in GitHub'da yedekli ve güvende! 🎉

### Sıradaki adımlar:
1. ✅ Test sunucusu kur
2. ✅ Plugin'i test et (TEST_GUIDE.md'ye bak)
3. ✅ Bug'ları düzelt (varsa)
4. ✅ Production'a deploy et
5. ✅ Community feedback topla

---

**Repository URL'ini paylaş!** 🌟

```
https://github.com/KULLANICI_ADIN/NetherBedrockRandomizer
```
