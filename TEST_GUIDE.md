# Test Rehberi - NetherBedrockRandomizer

## 🧪 Test Planı

Bu rehber, plugin'i test etmek için adım adım kılavuzdur.

---

## Ön Hazırlık

### Gerekli Sunucu Kurulumu

1. **Test Sunucusu Hazırla**
   ```bash
   # Folia 1.21.4 indir
   wget https://api.papermc.io/v2/projects/folia/versions/1.21.4/builds/latest/downloads/folia-1.21.4.jar
   
   # Veya Paper 1.21.4 (alternatif)
   wget https://api.papermc.io/v2/projects/paper/versions/1.21.4/builds/latest/downloads/paper-1.21.4.jar
   ```

2. **Server Klasörü Oluştur**
   ```bash
   mkdir test-server
   cd test-server
   mv ../folia-1.21.4.jar ./server.jar
   
   # İlk start (EULA kabul et)
   java -Xmx2G -Xms2G -jar server.jar nogui
   
   # eula.txt düzenle
   echo "eula=true" > eula.txt
   ```

3. **Plugin'i Kopyala**
   ```bash
   mkdir plugins
   cp ../NetherBedrockRandomizer/target/NetherBedrockRandomizer-1.0.0.jar plugins/
   ```

---

## Test Aşamaları

### Phase 1: Kurulum ve Başlatma Testi ✅

**Amaç**: Plugin'in düzgün yüklendiğini doğrula

**Adımlar**:
1. Sunucuyu başlat
   ```bash
   java -Xmx2G -Xms2G -jar server.jar nogui
   ```

2. Console'da şunları kontrol et:
   ```
   [NetherBedrockRandomizer] ================================================
   [NetherBedrockRandomizer]   _   _ ____  ____  
   [NetherBedrockRandomizer]  | \ | | __ )|  _ \ 
   ...
   [NetherBedrockRandomizer] NetherBedrockRandomizer v1.0.0 enabled!
   [NetherBedrockRandomizer] Ready to prevent seed-based cheating!
   ```

3. Hata mesajı olmamalı!

**✅ Başarı Kriterleri**:
- [ ] Plugin yüklendi
- [ ] Config dosyası oluştu
- [ ] Hata yok

---

### Phase 2: Konfigürasyon Testi ✅

**Amaç**: Config dosyasının doğru çalıştığını doğrula

**Adımlar**:
1. Sunucuyu durdur
2. Config'i düzenle:
   ```bash
   nano plugins/NetherBedrockRandomizer/config.yml
   ```

3. Custom seed'i değiştir:
   ```yaml
   settings:
     custom-seed: "TestServer-2026-MySecret-12345"
     debug: true  # Debug mode'u aç
   ```

4. Sunucuyu tekrar başlat

5. Console'da config bilgilerini kontrol et:
   ```
   [NetherBedrockRandomizer] Configuration Summary:
   [NetherBedrockRandomizer]   Bedrock Density: 85.0%
   ...
   ```

**✅ Başarı Kriterleri**:
- [ ] Config değişiklikleri algılandı
- [ ] Debug mode aktif
- [ ] Custom seed uygulandı

---

### Phase 3: Komut Testi ✅

**Amaç**: Plugin komutlarının çalıştığını doğrula

**Adımlar**:
1. Oyuna gir (op yetkisiyle)
2. Komutları test et:
   ```
   /nbr
   /nbr help
   /nbr info
   /nbr stats
   /nbr reload
   ```

3. Her komutun yanıt verdiğini kontrol et

**✅ Başarı Kriterleri**:
- [ ] Tüm komutlar çalışıyor
- [ ] Info doğru bilgileri gösteriyor
- [ ] Stats başlangıçta 0 chunks
- [ ] Reload çalışıyor

---

### Phase 4: Fonksiyonel Test - Bedrock Randomization 🎯

**Amaç**: Bedrock'ların randomize edildiğini doğrula

**Adımlar**:
1. Oyunda Nether'a git:
   ```
   /execute in minecraft:the_nether run tp @s 0 64 0
   ```

2. Yeni bir chunk'a ışınlan (hiç gidilmemiş yer):
   ```
   /tp @s 1000 64 1000
   ```

3. Bedrock katmanlarına in (Y=1-4):
   ```
   /tp @s ~ 4 ~
   ```

4. **VİZUEL KONTROL**:
   - F3 aç, Y koordinatını kontrol et
   - Y=0: TAM bedrock olmalı (korunmuş) ✅
   - Y=1,2,3,4: Karışık bedrock/netherrack olmalı ✅
   - Y=5+: Normal netherrack/lava olmalı ✅

5. Farklı chunk'larda test et:
   ```
   /tp @s 2000 4 2000
   /tp @s 3000 4 3000
   ```

6. Her chunk'ın farklı pattern olmalı!

7. Console'da debug log'ları kontrol et:
   ```
   [NetherBedrockRandomizer] Processing chunk: [world_nether] (62, 62)
   [NetherBedrockRandomizer] Chunk processed: 150 blocks changed in 8ms
   ```

**✅ Başarı Kriterleri**:
- [ ] Y=0 korunuyor (tam bedrock)
- [ ] Y=1-4 randomize (karışık)
- [ ] Y=127 korunuyor (nether ceiling)
- [ ] Her chunk farklı pattern
- [ ] Console'da processing logs

---

### Phase 5: Tavan (Ceiling) Testi 🎯

**Amaç**: Nether tavanındaki bedrock'ların randomize edildiğini doğrula

**Adımlar**:
1. Nether ceiling'e çık (Y=123-127):
   ```
   /tp @s ~ 124 ~
   ```

2. **VİZUEL KONTROL**:
   - Y=127: TAM bedrock olmalı (korunmuş) ✅
   - Y=123,124,125,126: Karışık bedrock/netherrack ✅

**✅ Başarı Kriterleri**:
- [ ] Y=127 korunuyor
- [ ] Y=123-126 randomize

---

### Phase 6: Overworld/End Testi ✅

**Amaç**: Diğer dimensionların etkilenmediğini doğrula

**Adımlar**:
1. Overworld'e dön:
   ```
   /tp @s 0 64 0
   ```

2. Yeni chunk'lara git ve kontrol et
3. Bedrock NORMAL vanilla pattern olmalı (randomize DEĞİL)

4. End'e git:
   ```
   /execute in minecraft:the_end run tp @s 0 64 0
   ```

5. Bedrock kontrol et - NORMAL olmalı

**✅ Başarı Kriterleri**:
- [ ] Overworld bedrock normal
- [ ] End bedrock normal
- [ ] Sadece Nether etkileniyor

---

### Phase 7: İstatistik Testi 📊

**Amaç**: Plugin istatistiklerinin doğru tutulduğunu doğrula

**Adımlar**:
1. Nether'da 10-20 chunk explore et
2. Komut çalıştır:
   ```
   /nbr stats
   ```

3. Sonuçları kontrol et:
   ```
   Chunks Processed: 15
   Blocks Randomized: 2400
   Average Processing Time: 7ms per chunk
   ```

**✅ Başarı Kriterleri**:
- [ ] Chunks Processed > 0
- [ ] Blocks Randomized > 0
- [ ] Processing Time < 50ms
- [ ] Mantıklı sayılar

---

### Phase 8: Performance Test - Chunky 🚀

**Amaç**: Büyük chunk generation'da performance test

**Gerekli**: Chunky plugin
```bash
# Chunky indir
wget https://ci.codemc.io/job/pop4959/job/Chunky/lastSuccessfulBuild/artifact/bukkit/target/Chunky-Bukkit.jar -O plugins/Chunky.jar
```

**Adımlar**:
1. Sunucuyu yeniden başlat
2. TPS monitoring başlat:
   ```
   /spark tps
   # veya
   /tps
   ```

3. Chunky ile chunk pre-generation:
   ```
   /chunky world world_nether
   /chunky radius 1000
   /chunky start
   ```

4. **MONİTOR ET**:
   - TPS: 19.5+ olmalı (idealse 20)
   - Console logs
   - `/nbr stats` periyodik kontrol
   - RAM kullanımı

5. 10 dakika bekle ve sonuçları topla

**✅ Başarı Kriterleri**:
- [ ] TPS > 19.0 (idealse 19.5+)
- [ ] Memory leak yok (RAM stabil)
- [ ] 100+ chunks işlendi
- [ ] Hata mesajı yok

---

### Phase 9: Reload Testi 🔄

**Amaç**: Hot-reload'ın çalıştığını doğrula

**Adımlar**:
1. Oyunda chunk'lar randomize olurken
2. Config'i değiştir (bedrock-density: 0.5)
3. Reload:
   ```
   /nbr reload
   ```

4. Yeni chunk'lara git
5. Yeni density'nin uygulandığını kontrol et (daha az bedrock)

**✅ Başarı Kriterleri**:
- [ ] Reload hatasız
- [ ] Yeni config uygulandı
- [ ] Sunucu çalışmaya devam etti

---

### Phase 10: Stress Test 💪

**Amaç**: Yüksek yük altında stability test

**Adımlar**:
1. 2-3 oyuncu aynı anda Nether explore
2. Elytra ile hızlı uçuş (chunk loading)
3. `/nbr stats` sürekli kontrol
4. Console'da hata kontrol

**Test Süresi**: 30 dakika

**✅ Başarı Kriterleri**:
- [ ] TPS stabil
- [ ] Hata yok
- [ ] Memory kullanımı stabil
- [ ] Tüm oyuncular için düzgün çalışıyor

---

## 🐛 Hata Senaryoları

### Test 1: Plugin Disable Edilirse
```
/plugins
# NetherBedrockRandomizer yeşil olmalı

# Disable et
/plugins disable NetherBedrockRandomizer

# Yeni chunk'lar normal vanilla olmalı
# Enable et
/plugins enable NetherBedrockRandomizer
```

### Test 2: Config Hatalı Değerler
```yaml
settings:
  bedrock-density: 5.0  # Hatalı (>1.0)
  randomize-bottom-layers: -5  # Hatalı (negatif)
```
- Plugin yüklenmeli
- Değerler clamp edilmeli (1.0 ve 0)
- Warning mesajları olmalı

### Test 3: World Yok
```yaml
settings:
  worlds:
    - nonexistent_world
```
- Hata olmamalı
- Sadece o world işlenmez

---

## 📊 Test Sonuçları Formu

### Test Sunucusu Bilgileri
```
Folia/Paper Versiyonu: _______
Java Versiyonu: _______
RAM: _______
Plugin Versiyonu: 1.0.0
```

### Test Sonuçları

| Test | Durum | Notlar |
|------|-------|--------|
| Phase 1: Kurulum | ⏳ | |
| Phase 2: Config | ⏳ | |
| Phase 3: Komutlar | ⏳ | |
| Phase 4: Bottom Bedrock | ⏳ | |
| Phase 5: Top Bedrock | ⏳ | |
| Phase 6: Overworld/End | ⏳ | |
| Phase 7: Stats | ⏳ | |
| Phase 8: Chunky | ⏳ | |
| Phase 9: Reload | ⏳ | |
| Phase 10: Stress | ⏳ | |

### Performance Metrics

```
TPS Ortalaması: _____
Chunk İşleme Süresi: _____ms
Memory Kullanımı: _____MB
Chunks Processed: _____
Blocks Randomized: _____
```

### Bulunan Hatalar

```
1. [Hata açıklaması]
2. [Hata açıklaması]
```

---

## ✅ Final Checklist

Tüm testler tamamlandığında:

- [ ] Tüm phase'ler başarılı
- [ ] Performance kabul edilebilir (TPS > 19)
- [ ] Hiç kritik hata yok
- [ ] Memory leak yok
- [ ] Farklı oyuncular test etti
- [ ] Production'a deploy hazır

---

## 🚀 Production Deployment

Testler başarılıysa:

1. **Backup Al**
   ```bash
   # World backup
   tar -czf backup-$(date +%Y%m%d).tar.gz world world_nether
   ```

2. **Production Config Ayarla**
   ```yaml
   settings:
     custom-seed: "PRODUCTION-UNIQUE-SECRET"
     debug: false  # Production'da false
   ```

3. **Deploy**
   ```bash
   cp NetherBedrockRandomizer-1.0.0.jar /production/server/plugins/
   ```

4. **Monitor Et**
   - İlk 1 saat yakından takip
   - Console logs kontrol
   - `/nbr stats` periyodik
   - Oyuncu feedback topla

---

**Başarılar! 🎉**
