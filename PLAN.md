# NetherBedrockRandomizer - Proje Planlama Belgesi

## Proje Özeti
Folia tabanlı 1.21.4-1.21.8 Minecraft sunucuları için Nether bedrock katmanlarını randomize eden anti-cheat plugin'i.

## Temel Amaç
Oyuncuların bedrock patternlerinden server seed'ini bulmasını engellemek ve böylece X-ray/seed-based cheat'leri önlemek.

## Teknik Gereksinimler

### Platform
- **Sunucu Yazılımı:** Folia (Paper fork - region-based multithreading)
- **Minecraft Versiyonları:** 1.21.4 ve 1.21.8
- **API:** Paper API / Spigot API
- **Java Versiyonu:** Java 21 (1.21.x gereksinimi)

### Folia Uyumluluk Kritik Noktaları
1. **Scheduler:** Bukkit Scheduler yerine Folia's regionalized scheduler kullanılmalı
2. **Thread Safety:** Her region kendi thread'inde çalıştığı için thread-safe olmalı
3. **Entity/Chunk Operations:** Regional task scheduler ile yapılmalı
4. **Global Task:** Sadece gerekirse GlobalRegionScheduler kullanılmalı

## Fonksiyonel Gereksinimler

### Ana Fonksiyonalite
1. **Chunk Load Detection**
   - Nether dünyasında chunk yüklendiğinde tespit edilmeli
   - Sadece yeni oluşan chunk'lar işlenmeli (var olanlar değil)
   
2. **Bedrock Layer Randomization**
   - **Katman 0 (Bottom Layer):** DEĞİŞTİRİLMEYECEK - tam bedrock kalacak
   - **Katman 1-4:** Randomize edilecek (normal Y koordinatları: Y=1 to Y=4)
   - **Katman 127 (Top Layer - Nether ceiling):** DEĞİŞTİRİLMEYECEK - tam bedrock kalacak
   - **Katman 123-126:** Randomize edilecek
   
3. **Randomization Algoritması**
   - Her chunk için benzersiz randomization (seed'e bağlı olmayan)
   - Chunk koordinatlarına + Random seed kullanarak deterministik ama tahmin edilemez
   - Orijinal bedrock dağılımına benzer yoğunlukta olmalı (%70-90 arası)

4. **Performans Optimizasyonu**
   - Async işlemler (Folia-safe şekilde)
   - Batch processing ile birden fazla block aynı anda değiştirilebilir
   - Minimal memory footprint
   - CPU kullanımını minimize etmek için efficient algorithms

## Teknik Mimari

### Plugin Yapısı
```
NetherBedrockRandomizer/
├── src/main/java/
│   └── dev/osturuk/netherbedrockrandomizer/
│       ├── NetherBedrockRandomizer.java (Main class)
│       ├── listener/
│       │   └── ChunkListener.java (Chunk load events)
│       ├── randomizer/
│       │   ├── BedrockRandomizer.java (Core randomization logic)
│       │   └── BedrockPattern.java (Pattern calculator)
│       ├── util/
│       │   ├── FoliaUtil.java (Folia compatibility helpers)
│       │   └── ChunkUtil.java (Chunk processing utilities)
│       └── config/
│           └── ConfigManager.java (Configuration handler)
├── src/main/resources/
│   ├── plugin.yml
│   └── config.yml
├── pom.xml
└── README.md
```

### Core Components

#### 1. ChunkListener
- `ChunkLoadEvent` ve `ChunkPopulateEvent` dinle
- Sadece Nether dünyası için filtreleme
- Folia region scheduler ile chunk processing'i schedule et

#### 2. BedrockRandomizer
- Chunk içindeki bedrock blockları tespit et
- Randomization pattern'i hesapla
- Block değişikliklerini batch olarak uygula

#### 3. BedrockPattern
- Chunk coordinate + plugin seed ile RandomGenerator oluştur
- Her block position için bedrock olup olmayacağını belirle
- Original vanilla distribution'a benzer pattern oluştur

#### 4. FoliaUtil
- Folia API varlığını kontrol et
- Region-aware task scheduling
- Thread-safe operations

## Performans Stratejileri

### Memory Optimization
1. **No Caching:** Chunk data cache'lenmeyecek (memory leak riski)
2. **Immediate Processing:** Chunk load olunca hemen işle ve unut
3. **Primitive Types:** Wrapper class yerine primitive types kullan
4. **Object Pooling:** Gereksiz object creation'dan kaçın

### CPU Optimization
1. **Batch Updates:** Block.setType() yerine multiple blocks aynı anda
2. **Early Exit:** Top/bottom layer check ile gereksiz loop'lardan kaç
3. **Efficient RNG:** SecureRandom yerine daha hızlı RNG (ThreadLocalRandom)
4. **Lazy Evaluation:** Sadece gerekli block'ları kontrol et

### Threading (Folia-specific)
1. **Region Scheduler:** Her chunk kendi region'ında işlensin
2. **No Global Locks:** Mutex/synchronized minimal kullan
3. **Non-blocking:** Async operations, main thread'i block etme

## Configuration Seçenekleri

```yaml
# config.yml
settings:
  # Ana plugin açık/kapalı
  enabled: true
  
  # Sadece belirli worldler
  worlds:
    - world_nether
  
  # Randomization yoğunluğu (0.0-1.0)
  # 0.9 = %90 bedrock, 0.7 = %70 bedrock
  bedrock-density: 0.85
  
  # Bottom layer bedrock count (katman 0 hariç kaç katman korunacak)
  protected-bottom-layers: 1  # Y=0 korunacak
  
  # Top layer bedrock count (katman 127 hariç kaç katman korunacak)
  protected-top-layers: 1  # Y=127 korunacak
  
  # Randomizable layer sayısı (bottom'dan yukarı)
  randomize-bottom-layers: 4  # Y=1,2,3,4
  
  # Randomizable layer sayısı (top'dan aşağı)
  randomize-top-layers: 4  # Y=123,124,125,126
  
  # Plugin-specific seed (değiştirilirse pattern değişir)
  custom-seed: "NetherBedrockRandomizer-2026"
  
  # Debug mode
  debug: false
  
performance:
  # Batch size for block updates
  batch-size: 256
  
  # Process only new chunks (not existing)
  only-new-chunks: true
```

## Implementation Phases

### Phase 1: Basic Setup ✓
- [ ] Maven project structure
- [ ] plugin.yml ve pom.xml
- [ ] Main class ve basic lifecycle
- [ ] Config loading

### Phase 2: Core Functionality
- [ ] ChunkListener implementation
- [ ] Folia compatibility layer
- [ ] Basic bedrock detection
- [ ] Randomization algorithm

### Phase 3: Optimization
- [ ] Batch block updates
- [ ] Performance profiling
- [ ] Memory usage optimization
- [ ] Threading optimization

### Phase 4: Testing & Polish
- [ ] Test on Folia 1.21.4
- [ ] Test on Folia 1.21.8
- [ ] Performance benchmarks
- [ ] Bug fixes

## Potential Issues & Solutions

### Issue 1: Folia Compatibility
**Problem:** Folia'nın region-based threading'i standard Bukkit API'sini kırar
**Solution:** FoliaUtil wrapper class ile hem Folia hem de Paper/Spigot support

### Issue 2: Performance Impact
**Problem:** Her chunk load'da block update'ler lag yaratabilir
**Solution:** 
- Batch updates kullan
- Region scheduler ile proper scheduling
- Sadece gerekli layer'ları işle

### Issue 3: Already Generated Chunks
**Problem:** Var olan chunk'lar seed'i reveal edebilir
**Solution:** 
- Plugin sadece yeni chunk'ları işlesin
- Opsiyonel: Admin command ile existing chunks'ı da randomize et

### Issue 4: Chunk Regeneration
**Problem:** Chunky gibi plugins chunk'ları regenerate ederse pattern bozulabilir
**Solution:**
- Her chunk load consistent bir seed kullan (chunk coordinates based)
- Aynı chunk birden fazla load olsa bile aynı pattern

## Testing Checklist

### Functionality Tests
- [ ] Nether chunk load edildiğinde bedrock randomize oluyor mu?
- [ ] Top layer (Y=127) korunuyor mu?
- [ ] Bottom layer (Y=0) korunuyor mu?
- [ ] Overworld/End etkilenmiyor mu?
- [ ] Config ayarları düzgün çalışıyor mu?

### Performance Tests
- [ ] TPS drop var mı? (20 TPS korunmalı)
- [ ] Memory leak var mı? (heap dump analysis)
- [ ] CPU spike var mı? (profiling)
- [ ] Large chunk pregeneration (Chunky) test

### Compatibility Tests
- [ ] Folia 1.21.4 çalışıyor mu?
- [ ] Folia 1.21.8 çalışıyor mu?
- [ ] Diğer plugins ile conflict var mı?

## API Dependencies

```xml
<dependencies>
    <!-- Folia API -->
    <dependency>
        <groupId>dev.folia</groupId>
        <artifactId>folia-api</artifactId>
        <version>1.21.4-R0.1-SNAPSHOT</version>
        <scope>provided</scope>
    </dependency>
</dependencies>

<repositories>
    <repository>
        <id>papermc</id>
        <url>https://repo.papermc.io/repository/maven-public/</url>
    </repository>
</repositories>
```

## Success Metrics

1. **Zero Performance Impact:** TPS remains at 20, no lag spikes
2. **Zero Memory Leaks:** Memory usage stable over time
3. **100% Randomization:** Seed cannot be determined from bedrock patterns
4. **100% Folia Compatible:** Works on both 1.21.4 and 1.21.8

## Notlar

- Folia Paper'ın experimental fork'udur ve normal Bukkit scheduler çalışmaz
- Her chunk işleme regional olmalı, global işlemlerden kaçınılmalı
- ThreadLocalRandom kullan, SecureRandom gereksiz overhead
- Block.setType() yerine chunk.setBlock() daha performanslı olabilir
- ChunkSnapshot kullanarak read operations optimize edilebilir (gerekirse)

---

**Hazırlayan:** Senior Java Developer & Minecraft CEO mindset  
**Tarih:** 13 Ocak 2026  
**Proje:** NetherBedrockRandomizer v1.0.0
